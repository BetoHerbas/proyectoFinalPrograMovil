package com.ucb.proyectofinal.feature.notes.domain.model

/**
 * Modelo de dominio para una Nota. Desacoplado de Room y Firebase.
 */
data class Note(
    val id: Long = 0,
    val title: String,
    val body: String,
    val isPending: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
