package com.ucb.proyectofinal.lists.presentation.state

import com.ucb.proyectofinal.lists.domain.model.ContentItem

data class ListDetailUiState(
    val isLoading: Boolean = true,
    val listId: String = "",
    val listName: String = "",
    val items: List<ContentItem> = emptyList(),
    val showAddDialog: Boolean = false,
    val error: String? = null
)
