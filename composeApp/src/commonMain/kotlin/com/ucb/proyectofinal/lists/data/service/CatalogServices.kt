package com.ucb.proyectofinal.lists.data.service

import com.ucb.proyectofinal.core.config.AppSecrets
import com.ucb.proyectofinal.lists.data.datasource.CatalogRemoteDataSource
import com.ucb.proyectofinal.lists.domain.model.CastMember
import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.ItemDetail
import com.ucb.proyectofinal.lists.domain.model.Review
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodeURLQueryComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class CatalogServices(
    private val httpClient: HttpClient
) : CatalogRemoteDataSource {

    private val json = Json { ignoreUnknownKeys = true }

    private val tmdbToken: String
        get() = AppSecrets.tmdbReadToken.trim()

    private val googleBooksKey: String
        get() = AppSecrets.googleBooksApiKey.trim()

    private val rawgApiKey: String
        get() = AppSecrets.rawgApiKey.trim()


    override suspend fun searchCatalog(type: ContentType, query: String): List<CatalogSearchItem> {
        val normalized = query.trim()
        if (normalized.isBlank()) {
            return topTenFor(type)
        }
        return when (type) {
            ContentType.MOVIE -> searchMovies(normalized)
            ContentType.SERIES -> searchSeries(normalized)
            ContentType.BOOK -> searchBooks(normalized)
            ContentType.VIDEOGAME -> searchVideogames(normalized)
        }
    }

    override suspend fun getItemDetails(type: ContentType, title: String): ItemDetail {
        val normalized = title.trim()
        return when (type) {
            ContentType.MOVIE -> getMovieDetail(normalized)
            ContentType.SERIES -> getSeriesDetail(normalized)
            ContentType.BOOK -> getBookDetail(normalized)
            ContentType.VIDEOGAME -> getVideogameDetail(normalized)
        }
    }

    override suspend fun topTenFor(type: ContentType): List<CatalogSearchItem> = when (type) {
        ContentType.MOVIE -> topTenMovies()
        ContentType.SERIES -> topTenSeries()
        ContentType.BOOK -> topTenBooks()
        ContentType.VIDEOGAME -> topTenVideogames()
    }

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



    private suspend fun searchVideogames(query: String): List<CatalogSearchItem> {
        val key = rawgApiKey
        if (key.isBlank()) return emptyList()

        return runCatching {
            val url = "https://api.rawg.io/api/games?search=${query.encodeURLQueryComponent()}&key=$key&page_size=24"
            val root = json.parseToJsonElement(httpClient.get(url).bodyAsText()).jsonObject
            val results = root["results"]?.jsonArray ?: JsonArray(emptyList())
            
            results.mapNotNull { result ->
                val obj = result.jsonObject
                val title = obj.stringValue("name") ?: return@mapNotNull null
                val sourceId = obj.stringValue("id") ?: title
                val date = obj.stringValue("released") ?: ""
                val rating = obj.stringValue("rating") ?: ""
                
                val subtitle = listOf(
                    rating.takeIf { it.isNotBlank() && it != "0.0" }?.let { "★ $it" },
                    date.takeIf { it.isNotBlank() }?.take(4)
                ).filterNotNull().joinToString(" • ")

                CatalogSearchItem(
                    sourceId = sourceId,
                    title = title,
                    subtitle = subtitle,
                    imageUrl = obj.stringValue("background_image")
                )
            }
        }.getOrElse { emptyList() }
    }

    private suspend fun topTenVideogames(): List<CatalogSearchItem> {
        val key = rawgApiKey
        if (key.isBlank()) return emptyList()

        return runCatching {
            val url = "https://api.rawg.io/api/games?key=$key&ordering=-added&page_size=10"
            val root = json.parseToJsonElement(httpClient.get(url).bodyAsText()).jsonObject
            val results = root["results"]?.jsonArray ?: JsonArray(emptyList())
            
            results.mapNotNull { result ->
                val obj = result.jsonObject
                val title = obj.stringValue("name") ?: return@mapNotNull null
                val sourceId = obj.stringValue("id") ?: title
                val date = obj.stringValue("released") ?: ""
                val rating = obj.stringValue("rating") ?: ""
                
                val subtitle = listOf(
                    rating.takeIf { it.isNotBlank() && it != "0.0" }?.let { "★ $it" },
                    date.takeIf { it.isNotBlank() }?.take(4)
                ).filterNotNull().joinToString(" • ")

                CatalogSearchItem(
                    sourceId = sourceId,
                    title = title,
                    subtitle = subtitle,
                    imageUrl = obj.stringValue("background_image")
                )
            }
        }.getOrElse { emptyList() }
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

    private suspend fun getMovieDetail(title: String): ItemDetail {
        val searchResults = searchMovies(title)
        val firstResult = searchResults.firstOrNull() ?: error("Movie not found")
        val tmdbId = firstResult.sourceId
        
        val url = "https://api.themoviedb.org/3/movie/$tmdbId?language=es-ES&append_to_response=credits,reviews"
        val root = json.parseToJsonElement(
            httpClient.get(url) {
                header("Authorization", "Bearer $tmdbToken")
            }.bodyAsText()
        ).jsonObject
        
        val runtime = root["runtime"]?.jsonPrimitive?.content ?: ""
        val runtimeStr = if (runtime.isNotBlank() && runtime != "null") "${runtime}m" else "N/A"
        
        val overview = root["overview"]?.jsonPrimitive?.content ?: ""
        
        val credits = root["credits"]?.jsonObject
        val cast = credits?.get("cast")?.jsonArray?.take(10)?.mapNotNull {
            val castObj = it.jsonObject
            CastMember(
                name = castObj.stringValue("name") ?: "",
                role = castObj.stringValue("character") ?: "",
                imageUrl = castObj.stringValue("profile_path")?.let { path -> "https://image.tmdb.org/t/p/w200$path" }
            )
        } ?: emptyList()
        
        val crew = credits?.get("crew")?.jsonArray
        val director = crew?.firstOrNull { it.jsonObject.stringValue("job") == "Director" }
            ?.jsonObject?.stringValue("name") ?: "Unknown"
            
        val reviewsArray = root["reviews"]?.jsonObject?.get("results")?.jsonArray?.take(5)
        val reviews = reviewsArray?.mapNotNull {
            val revObj = it.jsonObject
            val author = revObj.stringValue("author") ?: ""
            val content = revObj.stringValue("content") ?: ""
            val authorDetails = revObj["author_details"]?.jsonObject
            val rating = authorDetails?.stringValue("rating")?.toDoubleOrNull()?.toInt() ?: 0
            val date = revObj.stringValue("created_at")?.take(10) ?: ""
            Review(author, content, rating, date)
        } ?: emptyList()
        
        val genres = root["genres"]?.jsonArray?.mapNotNull { it.jsonObject.stringValue("name") } ?: emptyList()
        val voteAverage = root.stringValue("vote_average")?.toDoubleOrNull() ?: 0.0
        val voteCount = root.stringValue("vote_count")?.toDoubleOrNull()?.toInt() ?: 0
        val releaseDate = root.stringValue("release_date") ?: ""
        val year = releaseDate.take(4)
        
        return ItemDetail.Movie(
            id = ItemId(title),
            title = ItemTitle.of(firstResult.title).getOrThrow(),
            description = overview.ifBlank { "No description available." },
            imageUrl = firstResult.imageUrl,
            rating = voteAverage,
            totalReviews = voteCount,
            tags = genres.take(3),
            parentsGuide = null,
            cast = cast,
            reviews = reviews,
            director = director,
            duration = runtimeStr,
            genres = genres,
            year = year
        )
    }

    private suspend fun getSeriesDetail(title: String): ItemDetail {
        val searchResults = searchSeries(title)
        val firstResult = searchResults.firstOrNull() ?: error("Series not found")
        val tmdbId = firstResult.sourceId
        
        val url = "https://api.themoviedb.org/3/tv/$tmdbId?language=es-ES&append_to_response=credits,reviews"
        val root = json.parseToJsonElement(
            httpClient.get(url) {
                header("Authorization", "Bearer $tmdbToken")
            }.bodyAsText()
        ).jsonObject
        
        val overview = root.stringValue("overview") ?: ""
        val numEpisodes = root.stringValue("number_of_episodes")?.toDoubleOrNull()?.toInt() ?: 0
        val numSeasons = root.stringValue("number_of_seasons")?.toDoubleOrNull()?.toInt() ?: 0
        
        val credits = root["credits"]?.jsonObject
        val cast = credits?.get("cast")?.jsonArray?.take(10)?.mapNotNull {
            val castObj = it.jsonObject
            CastMember(
                name = castObj.stringValue("name") ?: "",
                role = castObj.stringValue("character") ?: "",
                imageUrl = castObj.stringValue("profile_path")?.let { path -> "https://image.tmdb.org/t/p/w200$path" }
            )
        } ?: emptyList()
        
        val creators = root["created_by"]?.jsonArray
        val creator = creators?.firstOrNull()?.jsonObject?.stringValue("name") ?: "Unknown"
        
        val reviewsArray = root["reviews"]?.jsonObject?.get("results")?.jsonArray?.take(5)
        val reviews = reviewsArray?.mapNotNull {
            val revObj = it.jsonObject
            val author = revObj.stringValue("author") ?: ""
            val content = revObj.stringValue("content") ?: ""
            val authorDetails = revObj["author_details"]?.jsonObject
            val rating = authorDetails?.stringValue("rating")?.toDoubleOrNull()?.toInt() ?: 0
            val date = revObj.stringValue("created_at")?.take(10) ?: ""
            Review(author, content, rating, date)
        } ?: emptyList()
        
        val genres = root["genres"]?.jsonArray?.mapNotNull { it.jsonObject.stringValue("name") } ?: emptyList()
        val voteAverage = root.stringValue("vote_average")?.toDoubleOrNull() ?: 0.0
        val voteCount = root.stringValue("vote_count")?.toDoubleOrNull()?.toInt() ?: 0
        val releaseDate = root.stringValue("first_air_date") ?: ""
        val year = releaseDate.take(4)
        
        return ItemDetail.Series(
            id = ItemId(title),
            title = ItemTitle.of(firstResult.title).getOrThrow(),
            description = overview.ifBlank { "No description available." },
            imageUrl = firstResult.imageUrl,
            rating = voteAverage,
            totalReviews = voteCount,
            tags = genres.take(3),
            parentsGuide = null,
            cast = cast,
            reviews = reviews,
            creator = creator,
            episodes = numEpisodes,
            seasons = numSeasons,
            year = year
        )
    }

    private suspend fun getBookDetail(title: String): ItemDetail {
        val keyParam = if (googleBooksKey.isBlank()) "" else "&key=${googleBooksKey.encodeURLQueryComponent()}"
        val url = "https://www.googleapis.com/books/v1/volumes?q=${title.encodeURLQueryComponent()}&maxResults=1$keyParam"
        val root = json.parseToJsonElement(httpClient.get(url).bodyAsText()).jsonObject
        val doc = root["items"]?.jsonArray?.firstOrNull()?.jsonObject ?: error("Book not found")
        val volumeInfo = doc["volumeInfo"]?.jsonObject ?: error("No info")
        
        val apiTitle = volumeInfo.stringValue("title") ?: title
        val author = volumeInfo["authors"]?.jsonArray?.firstOrNull()?.let { primitiveContentOrNull(it) } ?: "Unknown Author"
        val description = volumeInfo.stringValue("description") ?: "No description available."
        val pages = volumeInfo.stringValue("pageCount")?.toDoubleOrNull()?.toInt() ?: 0
        val publisher = volumeInfo.stringValue("publisher") ?: "Unknown Publisher"
        val cover = googleBooksCoverUrl(volumeInfo)
        val categories = volumeInfo["categories"]?.jsonArray?.mapNotNull { primitiveContentOrNull(it) } ?: emptyList()
        val rating = volumeInfo.stringValue("averageRating")?.toDoubleOrNull() ?: 0.0
        val ratingCount = volumeInfo.stringValue("ratingsCount")?.toDoubleOrNull()?.toInt() ?: 0
        
        return ItemDetail.Book(
            id = ItemId(title),
            title = ItemTitle.of(apiTitle).getOrThrow(),
            description = description,
            imageUrl = cover,
            rating = rating,
            totalReviews = ratingCount,
            tags = categories.take(3),
            parentsGuide = null,
            cast = emptyList(),
            reviews = emptyList(),
            author = author,
            pages = pages,
            publisher = publisher
        )
    }

    private suspend fun getVideogameDetail(title: String): ItemDetail {
        val key = rawgApiKey
        if (key.isBlank()) error("RAWG API Key missing")
        
        val urlSearch = "https://api.rawg.io/api/games?search=${title.encodeURLQueryComponent()}&key=$key&page_size=1"
        val rootSearch = json.parseToJsonElement(httpClient.get(urlSearch).bodyAsText()).jsonObject
        val doc = rootSearch["results"]?.jsonArray?.firstOrNull()?.jsonObject ?: error("Game not found")
        
        val sourceId = doc.stringValue("id") ?: error("No ID")
        
        val urlDetail = "https://api.rawg.io/api/games/$sourceId?key=$key"
        val root = json.parseToJsonElement(httpClient.get(urlDetail).bodyAsText()).jsonObject
        
        val apiTitle = root.stringValue("name") ?: title
        val description = root.stringValue("description_raw") ?: "No description available."
        val background = root.stringValue("background_image")
        val rating = root.stringValue("rating")?.toDoubleOrNull() ?: 0.0
        val totalReviews = root.stringValue("reviews_count")?.toDoubleOrNull()?.toInt() ?: 0
        val playtime = root.stringValue("playtime")?.toDoubleOrNull()?.toInt() ?: 0
        val released = root.stringValue("released") ?: ""
        
        val developers = root["developers"]?.jsonArray?.mapNotNull { it.jsonObject.stringValue("name") } ?: emptyList()
        val developer = developers.firstOrNull() ?: "Unknown Developer"
        
        val platforms = root["platforms"]?.jsonArray?.mapNotNull { it.jsonObject["platform"]?.jsonObject?.stringValue("name") } ?: emptyList()
        val genres = root["genres"]?.jsonArray?.mapNotNull { it.jsonObject.stringValue("name") } ?: emptyList()
        
        return ItemDetail.Videogame(
            id = ItemId(title),
            title = ItemTitle.of(apiTitle).getOrThrow(),
            description = description,
            imageUrl = background,
            rating = rating,
            totalReviews = totalReviews,
            tags = genres.take(3),
            parentsGuide = null,
            cast = emptyList(),
            reviews = emptyList(),
            developer = developer,
            platforms = platforms,
            playtime = playtime,
            released = released
        )
    }

}
