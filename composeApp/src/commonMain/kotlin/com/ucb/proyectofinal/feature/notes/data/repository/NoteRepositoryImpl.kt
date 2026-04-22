package com.ucb.proyectofinal.feature.notes.data.repository

import com.ucb.proyectofinal.core.data.db.NoteDao
import com.ucb.proyectofinal.core.data.db.NoteEntity
import com.ucb.proyectofinal.feature.notes.domain.model.Note
import com.ucb.proyectofinal.feature.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    override suspend fun createNote(note: Note) {
        noteDao.insert(note.toEntity())
    }

    override fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getPendingNotes(): List<Note> =
        noteDao.getPendingNotes().map { it.toDomain() }

    override suspend fun markAsSynced(id: Long) {
        noteDao.markSynced(id)
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private fun Note.toEntity() = NoteEntity(
        id = id,
        title = title,
        body = body,
        isPending = isPending,
        createdAt = createdAt
    )

    private fun NoteEntity.toDomain() = Note(
        id = id,
        title = title,
        body = body,
        isPending = isPending,
        createdAt = createdAt
    )
}
