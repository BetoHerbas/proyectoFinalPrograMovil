package com.ucb.proyectofinal.lists.domain.repository

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentList
import kotlinx.coroutines.flow.Flow

expect class FirebaseRealtimeListsDataSource() {
    fun observeLists(userId: String): Flow<List<ContentList>>
    fun observeItems(userId: String, listId: String): Flow<List<ContentItem>>
    suspend fun createList(userId: String, list: ContentList)
    suspend fun addItem(userId: String, item: ContentItem)
    suspend fun updateItem(userId: String, item: ContentItem)
    suspend fun deleteList(userId: String, listId: String)
    suspend fun deleteItem(userId: String, listId: String, itemId: String)
}
