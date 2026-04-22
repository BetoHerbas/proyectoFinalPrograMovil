package com.ucb.proyectofinal.feature.notes.data.datasource

import com.ucb.proyectofinal.feature.notes.data.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteLocalDataSource {
    suspend fun insert(note: NoteEntity): Long
    fun getAllNotes(): Flow<List<NoteEntity>>
    suspend fun getPendingNotes(): List<NoteEntity>
    suspend fun markSynced(id: Long)
}
