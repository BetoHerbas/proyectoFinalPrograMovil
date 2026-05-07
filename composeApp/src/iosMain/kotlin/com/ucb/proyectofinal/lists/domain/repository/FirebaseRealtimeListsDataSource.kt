package com.ucb.proyectofinal.lists.domain.repository

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

actual class FirebaseRealtimeListsDataSource actual constructor() {

    private companion object {
        val listsByUser = MutableStateFlow<Map<String, List<ContentList>>>(emptyMap())
        val itemsByUserList = MutableStateFlow<Map<String, List<ContentItem>>>(emptyMap())
    }

    actual fun observeLists(userId: String): Flow<List<ContentList>> =
        listsByUser.map { it[userId].orEmpty() }

    actual fun observeItems(userId: String, listId: String): Flow<List<ContentItem>> =
        itemsByUserList.map { it["$userId:$listId"].orEmpty() }

    actual suspend fun createList(userId: String, list: ContentList) {
        val current = listsByUser.value[userId].orEmpty()
        listsByUser.value = listsByUser.value + (userId to (listOf(list) + current))
    }

    actual suspend fun addItem(userId: String, item: ContentItem) {
        val key = "$userId:${item.listId.value}"
        val current = itemsByUserList.value[key].orEmpty()
        itemsByUserList.value = itemsByUserList.value + (key to (listOf(item) + current))
        updateListCount(userId, item.listId.value)
    }

    actual suspend fun updateItem(userId: String, item: ContentItem) {
        val key = "$userId:${item.listId.value}"
        val current = itemsByUserList.value[key].orEmpty()
        itemsByUserList.value = itemsByUserList.value + (
            key to current.map { if (it.id == item.id) item else it }
        )
    }

    actual suspend fun deleteList(userId: String, listId: String) {
        val list = listsByUser.value[userId].orEmpty().filterNot { it.id.value == listId }
        listsByUser.value = listsByUser.value + (userId to list)
        itemsByUserList.value = itemsByUserList.value - "$userId:$listId"
    }

    actual suspend fun deleteItem(userId: String, listId: String, itemId: String) {
        val key = "$userId:$listId"
        val filtered = itemsByUserList.value[key].orEmpty().filterNot { it.id.value == itemId }
        itemsByUserList.value = itemsByUserList.value + (key to filtered)
        updateListCount(userId, listId)
    }

    private fun updateListCount(userId: String, listId: String) {
        val key = "$userId:$listId"
        val items = itemsByUserList.value[key].orEmpty()
        val updatedLists = listsByUser.value[userId].orEmpty().map {
            if (it.id.value == listId) it.copy(itemCount = items.size) else it
        }
        listsByUser.value = listsByUser.value + (userId to updatedLists)
    }
}
