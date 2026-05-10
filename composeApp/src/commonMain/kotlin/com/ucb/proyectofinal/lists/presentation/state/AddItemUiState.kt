package com.ucb.proyectofinal.lists.presentation.state

import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
import com.ucb.proyectofinal.lists.domain.model.ContentType

data class AddItemUiState(
    val listId: String = "",
    val listName: String = "",
    val listType: ContentType = ContentType.MOVIE,
    val query: String = "",
    val isLoading: Boolean = false,
    val addingItemId: String? = null,
    val items: List<CatalogSearchItem> = emptyList()
)
