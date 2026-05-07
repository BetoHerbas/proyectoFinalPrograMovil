package com.ucb.proyectofinal.fakes

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.model.vo.Rating
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeContentListRepository : ContentListRepository {
    var shouldFail = false
    var failureMessage = "Operation failed"
    private var idCounter = 0

    private val _lists = MutableStateFlow<List<ContentList>>(emptyList())
    private val _items = MutableStateFlow<Map<String, List<ContentItem>>>(emptyMap())

    val listsSnapshot: List<ContentList> get() = _lists.value
    val itemsSnapshot: Map<String, List<ContentItem>> get() = _items.value

    fun setLists(lists: List<ContentList>) {
        _lists.value = lists
    }

    fun setItems(listId: String, items: List<ContentItem>) {
        _items.value = _items.value + (listId to items)
    }

    override fun getLists(): Flow<List<ContentList>> = _lists

    override suspend fun createList(
        name: ListName,
        type: ContentType,
        description: String,
        coverImageUrl: String?,
        isPublic: Boolean
    ): Result<ContentList> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        val list = ContentList(
            id = ListId("list-${idCounter++}"),
            name = name,
            type = type,
            itemCount = 0,
            description = description,
            coverImageUrl = coverImageUrl,
            isPublic = isPublic
        )
        _lists.value = _lists.value + list
        return Result.success(list)
    }

    override fun getListItems(listId: ListId): Flow<List<ContentItem>> =
        _items.map { it[listId.value] ?: emptyList() }

    override suspend fun addItem(
        listId: ListId,
        title: ItemTitle,
        type: ContentType
    ): Result<ContentItem> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        val item = ContentItem(ItemId("item-${idCounter++}"), listId, title, type, false, null)
        val current = _items.value[listId.value] ?: emptyList()
        _items.value = _items.value + (listId.value to current + item)
        return Result.success(item)
    }

    override suspend fun toggleSeen(item: ContentItem): Result<ContentItem> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        val updated = item.copy(seen = !item.seen)
        val current = _items.value[item.listId.value] ?: emptyList()
        _items.value = _items.value + (item.listId.value to current.map {
            if (it.id == item.id) updated else it
        })
        return Result.success(updated)
    }

    override suspend fun rateItem(item: ContentItem, rating: Rating): Result<ContentItem> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        val updated = item.copy(rating = rating)
        val current = _items.value[item.listId.value] ?: emptyList()
        _items.value = _items.value + (item.listId.value to current.map {
            if (it.id == item.id) updated else it
        })
        return Result.success(updated)
    }

    override suspend fun deleteList(listId: ListId): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        _lists.value = _lists.value.filter { it.id != listId }
        return Result.success(Unit)
    }

    override suspend fun deleteItem(item: ContentItem): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        val current = _items.value[item.listId.value] ?: emptyList()
        _items.value = _items.value + (item.listId.value to current.filter { it.id != item.id })
        return Result.success(Unit)
    }
}
