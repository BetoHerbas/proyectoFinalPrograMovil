package com.ucb.proyectofinal.feature.notes.domain.usecase

import com.ucb.proyectofinal.feature.notes.domain.model.Note
import com.ucb.proyectofinal.feature.notes.domain.repository.NoteRepository

class CreateNoteUseCase(
    private val repository: NoteRepository,
    private val syncController: SyncController? = null
) {
    suspend operator fun invoke(title: String, body: String) {
        require(title.isNotBlank()) { "El título no puede estar vacío" }
        repository.createNote(Note(title = title.trim(), body = body.trim()))
        syncController?.triggerImmediateSync()
    }
}

