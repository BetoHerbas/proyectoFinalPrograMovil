package com.ucb.proyectofinal.feature.notes.domain.usecase

import com.ucb.proyectofinal.feature.notes.data.datasource.FirebaseNotesDataSource
import com.ucb.proyectofinal.feature.notes.domain.repository.NoteRepository

/**
 * Caso de uso Android-only que sincroniza las notas pendientes con Firebase RTDB.
 * Retorna el número de notas subidas exitosamente.
 */
class SyncPendingNotesUseCase(
    private val repository: NoteRepository,
    private val firebaseDataSource: FirebaseNotesDataSource
) {
    suspend operator fun invoke(): Int {
        val pending = repository.getPendingNotes()
        var syncedCount = 0

        for (note in pending) {
            try {
                firebaseDataSource.uploadNote(note)
                repository.markAsSynced(note.id)
                syncedCount++
            } catch (e: Exception) {
                // Si una nota falla, continúa con las demás y lo reintentará en el siguiente ciclo
                println("SyncPendingNotes: Error al subir nota ${note.id} — ${e.message}")
            }
        }

        return syncedCount
    }
}
