package com.ucb.proyectofinal.lists.domain.repository

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.model.vo.Rating
import kotlinx.coroutines.flow.Flow

interface ContentListRepository {
    fun getLists(): Flow<List<ContentList>>
    suspend fun createList(
        name: ListName,
        type: ContentType,
        description: String,
        coverImageUrl: String?,
        isPublic: Boolean
    ): Result<ContentList>
    suspend fun updateList(
        listId: ListId,
        name: ListName,
        description: String,
        coverImageUrl: String?,
        isPublic: Boolean
    ): Result<ContentList>
    fun getListItems(listId: ListId): Flow<List<ContentItem>>
    suspend fun addItem(listId: ListId, title: ItemTitle, type: ContentType): Result<ContentItem>
    suspend fun searchCatalog(type: ContentType, query: String): Result<List<CatalogSearchItem>>
    suspend fun toggleSeen(item: ContentItem): Result<ContentItem>
    suspend fun rateItem(item: ContentItem, rating: Rating): Result<ContentItem>
    suspend fun deleteList(listId: ListId): Result<Unit>
    suspend fun deleteItem(item: ContentItem): Result<Unit>
}
