package com.ucb.proyectofinal.lists.presentation.state

data class EditListUiState(
    val listId: String = "",
    val name: String = "",
    val description: String = "",
    val coverImageUrl: String? = null,
    val isPrivate: Boolean = false,
    val listType: String = "MOVIE",
    val isSaving: Boolean = false,
    val error: String? = null
)
