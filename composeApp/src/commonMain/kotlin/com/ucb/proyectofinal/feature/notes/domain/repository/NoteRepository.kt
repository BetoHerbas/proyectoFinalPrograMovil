package com.ucb.proyectofinal.feature.notes.domain.repository

import com.ucb.proyectofinal.feature.notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    /** Guarda una nota localmente con isPending = true. */
    suspend fun createNote(note: Note)

    /** Flow que emite la lista completa de notas en tiempo real. */
    fun getAllNotes(): Flow<List<Note>>

    /** Devuelve solo las notas pendientes de sincronización. */
    suspend fun getPendingNotes(): List<Note>

    /** Marca una nota como sincronizada exitosamente. */
    suspend fun markAsSynced(id: Long)
}
