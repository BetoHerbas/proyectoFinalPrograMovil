package com.ucb.proyectofinal.feature.notes.data.datasource

interface NoteRemoteDataSource {
    /** Sube una nota a Firebase Realtime Database. Devuelve true si tuvo éxito. */
    suspend fun uploadNote(id: Long, title: String, body: String, createdAt: Long): Boolean
}
