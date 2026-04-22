package com.ucb.proyectofinal.core.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que almacena pares clave-valor de Firebase Remote Config
 * para funcionar como caché local (offline-first).
 */
@Entity(tableName = "remote_config")
data class RemoteConfigEntity(
    @PrimaryKey val key: String,
    val value: String,
    val updatedAt: Long = 0L
)
