package com.ucb.proyectofinal.feature.notes.presentation.state

import com.ucb.proyectofinal.feature.notes.domain.model.Note

data class NoteListUiState(
    val notes: List<Note> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
