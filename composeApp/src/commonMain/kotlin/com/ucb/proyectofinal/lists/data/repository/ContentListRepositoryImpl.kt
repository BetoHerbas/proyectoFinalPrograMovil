package com.ucb.proyectofinal.lists.data.repository

import com.ucb.proyectofinal.auth.domain.repository.AuthRepository
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ContentListRepositoryImpl(
    private val authRepository: AuthRepository,
    private val realtimeListsDataSource: FirebaseRealtimeListsDataSource
) : ContentListRepository {

    override fun getLists(): Flow<List<ContentList>> =
        currentUserIdOrNull()?.let { userId ->
            realtimeListsDataSource.observeLists(userId)
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
            list
        }

    override fun getListItems(listId: ListId): Flow<List<ContentItem>> =
        currentUserIdOrNull()?.let { userId ->
            realtimeListsDataSource.observeItems(userId, listId.value)
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
        item
    }

    override suspend fun toggleSeen(item: ContentItem): Result<ContentItem> = runCatching {
        val userId = requireCurrentUserId()
        val updated = item.copy(seen = !item.seen)
        realtimeListsDataSource.updateItem(userId, updated)
        updated
    }

    override suspend fun rateItem(item: ContentItem, rating: Rating): Result<ContentItem> =
        runCatching {
            val userId = requireCurrentUserId()
            val updated = item.copy(rating = rating)
            realtimeListsDataSource.updateItem(userId, updated)
            updated
        }

    override suspend fun deleteList(listId: ListId): Result<Unit> = runCatching {
        val userId = requireCurrentUserId()
        realtimeListsDataSource.deleteList(userId, listId.value)
    }

    override suspend fun deleteItem(item: ContentItem): Result<Unit> = runCatching {
        val userId = requireCurrentUserId()
        realtimeListsDataSource.deleteItem(userId, item.listId.value, item.id.value)
    }

    private fun currentUserIdOrNull(): String? = authRepository.getCurrentUser()?.id?.value

    private fun requireCurrentUserId(): String =
        currentUserIdOrNull() ?: error("No hay sesión activa")
}
