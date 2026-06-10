package com.ucb.proyectofinal.lists.data.repository

import com.ucb.proyectofinal.auth.domain.repository.AuthRepository
import com.ucb.proyectofinal.lists.data.dao.ContentItemDao
import com.ucb.proyectofinal.lists.data.entity.ContentItemEntity
import com.ucb.proyectofinal.lists.data.dao.ContentListDao
import com.ucb.proyectofinal.lists.data.entity.ContentListEntity
import com.ucb.proyectofinal.lists.domain.model.CastMember
import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.ItemDetail
import com.ucb.proyectofinal.lists.domain.model.ParentsGuide
import com.ucb.proyectofinal.lists.domain.model.Review
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.model.vo.Rating
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import com.ucb.proyectofinal.lists.data.datasource.FirebaseRealtimeListsDataSource
import com.ucb.proyectofinal.lists.data.datasource.CatalogRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ContentListRepositoryImpl(
    private val authRepository: AuthRepository,
    private val realtimeListsDataSource: FirebaseRealtimeListsDataSource,
    private val catalogRemoteDataSource: CatalogRemoteDataSource,
    private val contentListDao: ContentListDao,
    private val contentItemDao: ContentItemDao
) : ContentListRepository {

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
        catalogRemoteDataSource.searchCatalog(type, query)
    }

    override suspend fun getItemDetails(type: ContentType, title: String): Result<ItemDetail> = runCatching {
        catalogRemoteDataSource.getItemDetails(type, title)
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

    override fun getPublicLists(): Flow<List<ContentList>> =
        realtimeListsDataSource.observePublicLists()

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

    override fun getFavorites(): Flow<List<ContentList>> {
        val userId = currentUserIdOrNull() ?: return flowOf(emptyList())
        return realtimeListsDataSource.observeFavorites(userId)
    }

    override suspend fun addFavorite(list: ContentList): Result<Unit> = runCatching {
        val userId = requireCurrentUserId()
        realtimeListsDataSource.addFavorite(userId, list)
    }

    override suspend fun removeFavorite(listId: ListId): Result<Unit> = runCatching {
        val userId = requireCurrentUserId()
        realtimeListsDataSource.removeFavorite(userId, listId.value)
    }
}
