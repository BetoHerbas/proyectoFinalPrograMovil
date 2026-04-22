package com.ucb.proyectofinal.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_entity")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    /** true = guardado localmente, pendiente de subir a Firebase */
    val isPending: Boolean = true,
    /** epoch-millis cuando se sincronizó; 0 si aún no se sincronizó */
    val syncedAt: Long = 0L
)
