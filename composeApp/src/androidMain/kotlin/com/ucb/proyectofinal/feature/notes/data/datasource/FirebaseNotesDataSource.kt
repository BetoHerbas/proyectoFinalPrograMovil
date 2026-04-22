package com.ucb.proyectofinal.feature.notes.data.datasource

import com.google.firebase.database.FirebaseDatabase
import com.ucb.proyectofinal.feature.notes.domain.model.Note
import kotlinx.coroutines.tasks.await

/**
 * Fuente de datos Android que sube notas a Firebase Realtime Database.
 * El nodo raíz es /notes/{pushKey}.
 */
class FirebaseNotesDataSource {

    private val notesRef = FirebaseDatabase.getInstance().getReference("notes")

    suspend fun uploadNote(note: Note) {
        val map = mapOf(
            "id"        to note.id,
            "title"     to note.title,
            "body"      to note.body,
            "createdAt" to note.createdAt
        )
        notesRef.push().setValue(map).await()
    }
}
