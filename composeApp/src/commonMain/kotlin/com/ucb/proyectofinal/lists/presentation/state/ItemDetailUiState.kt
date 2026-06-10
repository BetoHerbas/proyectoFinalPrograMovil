package com.ucb.proyectofinal.lists.presentation.state

import com.ucb.proyectofinal.lists.domain.model.ItemDetail

data class ItemDetailUiState(
    val isLoading: Boolean = true,
    val item: ItemDetail? = null,
    val error: String? = null
)
