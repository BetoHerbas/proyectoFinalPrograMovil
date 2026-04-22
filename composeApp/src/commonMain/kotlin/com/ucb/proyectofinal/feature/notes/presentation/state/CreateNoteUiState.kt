package com.ucb.proyectofinal.feature.notes.presentation.state

data class CreateNoteUiState(
    val title: String = "",
    val body: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)
