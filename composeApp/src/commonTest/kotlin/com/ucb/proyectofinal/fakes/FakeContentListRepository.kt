package com.ucb.proyectofinal.fakes

import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
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
    private val _favorites = MutableStateFlow<List<ContentList>>(emptyList())

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

    override suspend fun updateList(
        listId: ListId,
        name: ListName,
        description: String,
        coverImageUrl: String?,
        isPublic: Boolean
    ): Result<ContentList> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        val currentList = _lists.value.find { it.id == listId }
            ?: return Result.failure(Exception("List not found"))

        val updatedList = currentList.copy(
            name = name,
            description = description,
            coverImageUrl = coverImageUrl,
            isPublic = isPublic
        )

        _lists.value = _lists.value.map {
            if (it.id == listId) updatedList else it
        }

        return Result.success(updatedList)
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

    override suspend fun searchCatalog(
        type: ContentType,
        query: String
    ): Result<List<CatalogSearchItem>> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        val label = when (type) {
            ContentType.MOVIE -> "Movie"
            ContentType.SERIES -> "Series"
            ContentType.BOOK -> "Book"
            ContentType.VIDEOGAME -> "Videogame"
        }
        return Result.success(
            listOf(
                CatalogSearchItem(
                    sourceId = "fake-${type.name.lowercase()}-1",
                    title = "$label $query",
                    subtitle = "Fake source"
                )
            )
        )
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

    override fun getPublicLists(): Flow<List<ContentList>> = _lists.map { lists ->
        lists.filter { it.isPublic }
    }

    override fun getFavorites(): Flow<List<ContentList>> = _favorites

    override suspend fun addFavorite(list: ContentList): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        _favorites.value = _favorites.value + list
        return Result.success(Unit)
    }

    override suspend fun removeFavorite(listId: ListId): Result<Unit> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        _favorites.value = _favorites.value.filter { it.id != listId }
        return Result.success(Unit)
    }

    override suspend fun getItemDetails(
        type: ContentType,
        title: String
    ): Result<com.ucb.proyectofinal.lists.domain.model.ItemDetail> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        return Result.success(
            com.ucb.proyectofinal.lists.domain.model.ItemDetail.Movie(
                id = ItemId("mock-id"),
                title = ItemTitle.of(title).getOrThrow(),
                description = "Mock description",
                imageUrl = null,
                rating = 8.0,
                totalReviews = 100,
                tags = emptyList(),
                parentsGuide = null,
                cast = emptyList(),
                reviews = emptyList(),
                director = "Mock Director",
                duration = "120m",
                genres = emptyList(),
                year = "2024"
            )
        )
    }
}
