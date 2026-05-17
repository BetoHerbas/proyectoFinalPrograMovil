package com.ucb.proyectofinal.lists.presentation.state

import com.ucb.proyectofinal.lists.domain.model.ContentItem

data class ListDetailUiState(
    val isLoading: Boolean = true,
    val listId: String = "",
    val listName: String = "",
    val description: String = "",
    val coverImageUrl: String? = null,
    val isPublic: Boolean = true,
    val listType: String = "MOVIE",
    val selectedFilter: ItemFilter = ItemFilter.ALL,
    val items: List<ContentItem> = emptyList(),
    val showAddDialog: Boolean = false,
    val error: String? = null
)

enum class ItemFilter { ALL, PENDING, COMPLETED }
