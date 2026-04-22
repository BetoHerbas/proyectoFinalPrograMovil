package com.ucb.proyectofinal.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para notas creadas localmente.
 * [isPending] indica que aún no fue sincronizada con Firebase Realtime Database.
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val isPending: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
