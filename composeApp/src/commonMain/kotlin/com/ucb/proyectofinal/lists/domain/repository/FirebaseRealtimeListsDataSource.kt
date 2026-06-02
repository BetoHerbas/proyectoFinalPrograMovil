package com.ucb.proyectofinal.lists.domain.repository

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentList
import kotlinx.coroutines.flow.Flow

expect class FirebaseRealtimeListsDataSource() {
    fun observeLists(userId: String): Flow<List<ContentList>>
    fun observeItems(userId: String, listId: String): Flow<List<ContentItem>>
    fun observePublicLists(): Flow<List<ContentList>>
    fun observeFavorites(userId: String): Flow<List<ContentList>>
    suspend fun createList(userId: String, list: ContentList)
    suspend fun updateList(userId: String, list: ContentList)
    suspend fun addItem(userId: String, item: ContentItem)
    suspend fun updateItem(userId: String, item: ContentItem)
    suspend fun deleteList(userId: String, listId: String)
    suspend fun deleteItem(userId: String, listId: String, itemId: String)
    suspend fun addFavorite(userId: String, list: ContentList)
    suspend fun removeFavorite(userId: String, listId: String)
}
