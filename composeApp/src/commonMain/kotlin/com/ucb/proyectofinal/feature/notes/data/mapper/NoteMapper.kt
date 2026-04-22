package com.ucb.proyectofinal.feature.notes.data.mapper

import com.ucb.proyectofinal.feature.notes.data.entity.NoteEntity
import com.ucb.proyectofinal.feature.notes.domain.model.Note

fun Note.toEntity() = NoteEntity(
    id = id,
    title = title,
    body = body,
    isPending = isPending,
    createdAt = createdAt
)

fun NoteEntity.toDomain() = Note(
    id = id,
    title = title,
    body = body,
    isPending = isPending,
    createdAt = createdAt
)
