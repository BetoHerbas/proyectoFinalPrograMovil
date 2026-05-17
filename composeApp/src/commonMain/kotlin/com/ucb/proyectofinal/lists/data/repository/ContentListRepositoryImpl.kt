package com.ucb.proyectofinal.lists.data.repository

import com.ucb.proyectofinal.auth.domain.repository.AuthRepository
import com.ucb.proyectofinal.core.config.AppSecrets
import com.ucb.proyectofinal.core.data.db.ContentItemDao
import com.ucb.proyectofinal.core.data.db.ContentItemEntity
import com.ucb.proyectofinal.core.data.db.ContentListDao
import com.ucb.proyectofinal.core.data.db.ContentListEntity
import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.model.vo.Rating
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import com.ucb.proyectofinal.lists.domain.repository.FirebaseRealtimeListsDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodeURLQueryComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ContentListRepositoryImpl(
    private val authRepository: AuthRepository,
    private val realtimeListsDataSource: FirebaseRealtimeListsDataSource,
    private val httpClient: HttpClient,
    private val contentListDao: ContentListDao,
    private val contentItemDao: ContentItemDao
) : ContentListRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private val tmdbToken: String
        get() = AppSecrets.tmdbReadToken.trim()

    private val googleBooksKey: String
        get() = AppSecrets.googleBooksApiKey.trim()

    override fun getLists(): Flow<List<ContentList>> =
        currentUserIdOrNull()?.let { userId ->
            channelFlow {
                launch {
                    realtimeListsDataSource.observeLists(userId).collect { lists ->
                        contentListDao.clearAll()
                        if (lists.isNotEmpty()) {
                            contentListDao.insertAll(lists.map { it.toEntity() })
                        }
                    }
                }
                contentListDao.getAllLists()
                    .map { entities -> entities.map { it.toDomain() } }
                    .collect { send(it) }
            }
        } ?: flowOf(emptyList())

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createList(
        name: ListName,
        type: ContentType,
        description: String,
        coverImageUrl: String?,
        isPublic: Boolean
    ): Result<ContentList> =
        runCatching {
            val userId = requireCurrentUserId()
            val list = ContentList(
                id = ListId(Uuid.random().toString()),
                name = name,
                type = type,
                itemCount = 0,
                description = description.trim(),
                coverImageUrl = coverImageUrl?.trim()?.ifBlank { null },
                isPublic = isPublic
            )
            realtimeListsDataSource.createList(userId, list)
            contentListDao.insert(list.toEntity())
            list
        }

    override suspend fun updateList(
        listId: ListId,
        name: ListName,
        description: String,
        coverImageUrl: String?,
        isPublic: Boolean
    ): Result<ContentList> =
        runCatching {
            val userId = requireCurrentUserId()
            val existing = contentListDao.getById(listId.value)
                ?: error("Lista no encontrada")
            val updated = existing.toDomain().copy(
                name = name,
                description = description.trim(),
                coverImageUrl = coverImageUrl?.trim()?.ifBlank { null },
                isPublic = isPublic
            )
            realtimeListsDataSource.updateList(userId, updated)
            contentListDao.insert(updated.toEntity())
            updated
        }

    override fun getListItems(listId: ListId): Flow<List<ContentItem>> =
        currentUserIdOrNull()?.let { userId ->
            channelFlow {
                launch {
                    realtimeListsDataSource.observeItems(userId, listId.value).collect { items ->
                        contentItemDao.deleteByListId(listId.value)
                        if (items.isNotEmpty()) {
                            contentItemDao.insertAll(items.map { it.toEntity() })
                        }
                    }
                }
                contentItemDao.getItemsByList(listId.value)
                    .map { entities -> entities.map { it.toDomain() } }
                    .collect { send(it) }
            }
        } ?: flowOf(emptyList())

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun addItem(
        listId: ListId,
        title: ItemTitle,
        type: ContentType
    ): Result<ContentItem> = runCatching {
        val userId = requireCurrentUserId()
        val item = ContentItem(
            id = ItemId(Uuid.random().toString()),
            listId = listId,
            title = title,
            type = type,
            seen = false,
            rating = null
        )
        realtimeListsDataSource.addItem(userId, item)
        contentItemDao.insert(item.toEntity())
        item
    }

    override suspend fun searchCatalog(
        type: ContentType,
        query: String
    ): Result<List<CatalogSearchItem>> = runCatching {
        val normalized = query.trim()
        if (normalized.isBlank()) {
            return@runCatching topTenFor(type)
        }
        when (type) {
            ContentType.MOVIE -> searchMovies(normalized)
            ContentType.SERIES -> searchSeries(normalized)
            ContentType.BOOK -> searchBooks(normalized)
            ContentType.VIDEOGAME -> emptyList()
        }
    }

    override suspend fun toggleSeen(item: ContentItem): Result<ContentItem> = runCatching {
        val userId = requireCurrentUserId()
        val updated = item.copy(seen = !item.seen)
        realtimeListsDataSource.updateItem(userId, updated)
        contentItemDao.insert(updated.toEntity())
        updated
    }

    override suspend fun rateItem(item: ContentItem, rating: Rating): Result<ContentItem> =
        runCatching {
            val userId = requireCurrentUserId()
            val updated = item.copy(rating = rating)
            realtimeListsDataSource.updateItem(userId, updated)
            contentItemDao.insert(updated.toEntity())
            updated
        }

    override suspend fun deleteList(listId: ListId): Result<Unit> = runCatching {
        val userId = requireCurrentUserId()
        realtimeListsDataSource.deleteList(userId, listId.value)
        contentListDao.deleteById(listId.value)
        contentItemDao.deleteByListId(listId.value)
    }

    override suspend fun deleteItem(item: ContentItem): Result<Unit> = runCatching {
        val userId = requireCurrentUserId()
        realtimeListsDataSource.deleteItem(userId, item.listId.value, item.id.value)
        contentItemDao.deleteById(item.id.value)
    }

    private fun currentUserIdOrNull(): String? = authRepository.getCurrentUser()?.id?.value

    private fun requireCurrentUserId(): String =
        currentUserIdOrNull() ?: error("No hay sesión activa")

    private fun ContentList.toEntity(): ContentListEntity = ContentListEntity(
        id = id.value,
        name = name.value,
        type = type.name,
        itemCount = itemCount,
        description = description,
        coverImageUrl = coverImageUrl,
        isPublic = isPublic
    )

    private fun ContentListEntity.toDomain(): ContentList = ContentList(
        id = ListId(id),
        name = ListName(name),
        type = type.toContentTypeOrDefault(),
        itemCount = itemCount,
        description = description,
        coverImageUrl = coverImageUrl,
        isPublic = isPublic
    )

    private fun ContentItem.toEntity(): ContentItemEntity = ContentItemEntity(
        id = id.value,
        listId = listId.value,
        title = title.value,
        type = type.name,
        seen = seen,
        rating = rating?.value
    )

    private fun ContentItemEntity.toDomain(): ContentItem = ContentItem(
        id = ItemId(id),
        listId = ListId(listId),
        title = ItemTitle(title),
        type = type.toContentTypeOrDefault(),
        seen = seen,
        rating = rating?.let { Rating(it) }
    )

    private fun String.toContentTypeOrDefault(): ContentType =
        runCatching { ContentType.valueOf(this) }.getOrDefault(ContentType.MOVIE)

    private suspend fun searchMovies(query: String): List<CatalogSearchItem> {
        return runCatching {
            searchTmdb(path = "movie", query = query)
        }.getOrElse {
            searchMoviesFallback(query)
        }
    }

    private suspend fun topTenMovies(): List<CatalogSearchItem> {
        return runCatching {
            topTmdb(path = "movie")
        }.getOrElse {
            topTenMoviesFallback()
        }
    }

    private suspend fun searchSeries(query: String): List<CatalogSearchItem> {
        return runCatching {
            searchTmdb(path = "tv", query = query)
        }.getOrElse {
            searchSeriesFallback(query)
        }
    }

    private suspend fun topTenSeries(): List<CatalogSearchItem> {
        return runCatching {
            topTmdb(path = "tv")
        }.getOrElse {
            topTenSeriesFallback()
        }
    }

    private suspend fun searchBooks(query: String): List<CatalogSearchItem> {
        val keyParam = if (googleBooksKey.isBlank()) "" else "&key=${googleBooksKey.encodeURLQueryComponent()}"
        val url = "https://www.googleapis.com/books/v1/volumes?q=${query.encodeURLQueryComponent()}&maxResults=24&printType=books$keyParam"
        val root = json.parseToJsonElement(httpClient.get(url).bodyAsText()).jsonObject
        val docs = root["items"]?.jsonArray ?: JsonArray(emptyList())
        return docs.mapNotNull { doc ->
            val obj = doc.jsonObject
            val volumeInfo = obj["volumeInfo"]?.jsonObject ?: return@mapNotNull null
            val title = volumeInfo.stringValue("title") ?: return@mapNotNull null
            val author = volumeInfo["authors"]
                ?.jsonArray
                ?.firstOrNull()
                ?.let { primitiveContentOrNull(it) }
                ?: ""
            val sourceId = obj.stringValue("id") ?: title
            val cover = googleBooksCoverUrl(volumeInfo)
            CatalogSearchItem(
                sourceId = sourceId,
                title = title,
                subtitle = author,
                imageUrl = cover
            )
        }
    }

    private suspend fun topTenBooks(): List<CatalogSearchItem> {
        val keyParam = if (googleBooksKey.isBlank()) "" else "&key=${googleBooksKey.encodeURLQueryComponent()}"
        val url = "https://www.googleapis.com/books/v1/volumes?q=bestseller&maxResults=10&printType=books$keyParam"
        val root = json.parseToJsonElement(httpClient.get(url).bodyAsText()).jsonObject
        val docs = root["items"]?.jsonArray ?: JsonArray(emptyList())
        return docs.mapNotNull { doc ->
            val obj = doc.jsonObject
            val volumeInfo = obj["volumeInfo"]?.jsonObject ?: return@mapNotNull null
            val title = volumeInfo.stringValue("title") ?: return@mapNotNull null
            val author = volumeInfo["authors"]
                ?.jsonArray
                ?.firstOrNull()
                ?.let { primitiveContentOrNull(it) }
                ?: ""
            val sourceId = obj.stringValue("id") ?: title
            val cover = googleBooksCoverUrl(volumeInfo)
            CatalogSearchItem(
                sourceId = sourceId,
                title = title,
                subtitle = author,
                imageUrl = cover
            )
        }
    }

    private suspend fun topTenFor(type: ContentType): List<CatalogSearchItem> = when (type) {
        ContentType.MOVIE -> topTenMovies()
        ContentType.SERIES -> topTenSeries()
        ContentType.BOOK -> topTenBooks()
        ContentType.VIDEOGAME -> emptyList()
    }

    private suspend fun topTmdb(path: String): List<CatalogSearchItem> {
        val token = tmdbToken
        if (token.isBlank()) error("TMDB token no configurado")

        val url = "https://api.themoviedb.org/3/$path/popular?language=es-ES&page=1"
        val root = json.parseToJsonElement(
            httpClient.get(url) {
                header("Authorization", "Bearer $token")
            }.bodyAsText()
        ).jsonObject

        return parseTmdbResults(root).take(10)
    }

    private suspend fun searchTmdb(path: String, query: String): List<CatalogSearchItem> {
        val token = tmdbToken
        if (token.isBlank()) error("TMDB token no configurado")

        val url = "https://api.themoviedb.org/3/search/$path?query=${query.encodeURLQueryComponent()}&language=es-ES&page=1&include_adult=false"
        val root = json.parseToJsonElement(
            httpClient.get(url) {
                header("Authorization", "Bearer $token")
            }.bodyAsText()
        ).jsonObject

        return parseTmdbResults(root).take(24)
    }

    private fun parseTmdbResults(root: JsonObject): List<CatalogSearchItem> {
        val results = root["results"]?.jsonArray ?: JsonArray(emptyList())
        return results.mapNotNull { result ->
            val obj = result.jsonObject
            val title = obj.stringValue("title")
                ?: obj.stringValue("name")
                ?: return@mapNotNull null
            val sourceId = obj.stringValue("id") ?: title
            val rating = obj.stringValue("vote_average") ?: ""
            val date = obj.stringValue("release_date")
                ?: obj.stringValue("first_air_date")
                ?: ""
            val subtitle = listOf(rating.takeIf { it.isNotBlank() }?.let { "TMDB $it" }, date.takeIf { it.isNotBlank() })
                .filterNotNull()
                .joinToString(" • ")
            val posterPath = obj.stringValue("poster_path")
            CatalogSearchItem(
                sourceId = sourceId,
                title = title,
                subtitle = subtitle,
                imageUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
            )
        }
    }

    private suspend fun searchMoviesFallback(query: String): List<CatalogSearchItem> {
        val url = "https://itunes.apple.com/search?media=movie&limit=24&term=${query.encodeURLQueryComponent()}"
        val root = json.parseToJsonElement(httpClient.get(url).bodyAsText()).jsonObject
        val results = root["results"]?.jsonArray ?: JsonArray(emptyList())
        return results.mapNotNull { result ->
            val obj = result.jsonObject
            val title = obj.stringValue("trackName") ?: return@mapNotNull null
            val subtitle = obj.stringValue("artistName") ?: ""
            val sourceId = obj.stringValue("trackId") ?: title
            CatalogSearchItem(
                sourceId = sourceId,
                title = title,
                subtitle = subtitle,
                imageUrl = obj.stringValue("artworkUrl100")
            )
        }
    }

    private suspend fun topTenMoviesFallback(): List<CatalogSearchItem> {
        val url = "https://itunes.apple.com/us/rss/topmovies/limit=10/json"
        val root = json.parseToJsonElement(httpClient.get(url).bodyAsText()).jsonObject
        val entries = root["feed"]
            ?.jsonObject
            ?.get("entry")
            ?.jsonArray
            ?: JsonArray(emptyList())

        return entries.mapNotNull { entry ->
            val obj = entry.jsonObject
            val title = obj["im:name"]?.jsonObject?.stringValue("label") ?: return@mapNotNull null
            val subtitle = obj["im:artist"]?.jsonObject?.stringValue("label") ?: ""
            val sourceId = obj["id"]
                ?.jsonObject
                ?.get("attributes")
                ?.jsonObject
                ?.stringValue("im:id")
                ?: title
            val images = obj["im:image"]?.jsonArray ?: JsonArray(emptyList())
            val imageUrl = images.lastOrNull()
                ?.jsonObject
                ?.stringValue("label")

            CatalogSearchItem(
                sourceId = sourceId,
                title = title,
                subtitle = subtitle,
                imageUrl = imageUrl
            )
        }
    }

    private suspend fun searchSeriesFallback(query: String): List<CatalogSearchItem> {
        val url = "https://api.tvmaze.com/search/shows?q=${query.encodeURLQueryComponent()}"
        val results = json.parseToJsonElement(httpClient.get(url).bodyAsText()).jsonArray
        return results.mapNotNull { result ->
            val show = result.jsonObject["show"]?.jsonObject ?: return@mapNotNull null
            val title = show.stringValue("name") ?: return@mapNotNull null
            val network = show["network"]?.jsonObject?.stringValue("name")
            val platform = show["webChannel"]?.jsonObject?.stringValue("name")
            val sourceId = show.stringValue("id") ?: title
            CatalogSearchItem(
                sourceId = sourceId,
                title = title,
                subtitle = network ?: platform ?: "",
                imageUrl = show["image"]?.jsonObject?.stringValue("medium")
            )
        }
    }

    private suspend fun topTenSeriesFallback(): List<CatalogSearchItem> {
        val url = "https://api.tvmaze.com/shows?page=1"
        val results = json.parseToJsonElement(httpClient.get(url).bodyAsText()).jsonArray
        return results.take(10).mapNotNull { result ->
            val show = result.jsonObject
            val title = show.stringValue("name") ?: return@mapNotNull null
            val network = show["network"]?.jsonObject?.stringValue("name")
            val platform = show["webChannel"]?.jsonObject?.stringValue("name")
            val sourceId = show.stringValue("id") ?: title
            CatalogSearchItem(
                sourceId = sourceId,
                title = title,
                subtitle = network ?: platform ?: "",
                imageUrl = show["image"]?.jsonObject?.stringValue("medium")
            )
        }
    }

    private fun JsonObject.stringValue(key: String): String? =
        this[key]?.let { primitiveContentOrNull(it) }

    private fun primitiveContentOrNull(element: JsonElement): String? {
        return if (element is JsonNull) null else element.jsonPrimitive.content
    }

    private fun googleBooksCoverUrl(volumeInfo: JsonObject): String? {
        val imageLinks = volumeInfo["imageLinks"]?.jsonObject ?: return null
        val raw = imageLinks.stringValue("thumbnail")
            ?: imageLinks.stringValue("smallThumbnail")
            ?: return null

        // Google Books often returns http URLs; force https so Android can load them without cleartext.
        return raw
            .replace("http://", "https://")
            .replace("&edge=curl", "")
            .replace("zoom=1", "zoom=2")
    }
}
