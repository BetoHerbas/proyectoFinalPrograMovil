package com.ucb.proyectofinal.lists.presentation.state

import com.ucb.proyectofinal.lists.domain.model.ContentList

data class ContentListsUiState(
    val isLoading: Boolean = true,
    val lists: List<ContentList> = emptyList(),
    val error: String? = null
)
