package com.ucb.proyectofinal.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.proyectofinal.feature.notes.data.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    /** Observa todas las notas en tiempo real (para la lista de la UI). */
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    /** Devuelve notas que aún no fueron subidas a Firebase. */
    @Query("SELECT * FROM notes WHERE isPending = 1")
    suspend fun getPendingNotes(): List<NoteEntity>

    /** Marca una nota como sincronizada (isPending = false). */
    @Query("UPDATE notes SET isPending = 0 WHERE id = :id")
    suspend fun markSynced(id: Long)
}
