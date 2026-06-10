package com.ucb.proyectofinal.favorites.presentation.state

import com.ucb.proyectofinal.lists.domain.model.ContentList

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favorites: List<ContentList> = emptyList(),
    val error: String? = null
)
