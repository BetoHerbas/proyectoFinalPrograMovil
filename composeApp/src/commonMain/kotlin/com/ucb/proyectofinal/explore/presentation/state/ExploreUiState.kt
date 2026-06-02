package com.ucb.proyectofinal.explore.presentation.state

import com.ucb.proyectofinal.lists.domain.model.ContentList

data class ExploreUiState(
    val isLoading: Boolean = true,
    val lists: List<ContentList> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val error: String? = null
)
