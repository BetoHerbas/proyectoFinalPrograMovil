package com.ucb.proyectofinal.feature.notes.domain.usecase

import com.ucb.proyectofinal.feature.notes.domain.model.Note
import com.ucb.proyectofinal.feature.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.getAllNotes()
}
