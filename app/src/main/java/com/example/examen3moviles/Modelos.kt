package com.example.examen3moviles

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RegistroEvento")
data class RegistroEvento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = false
)

@Entity(tableName = "ConfiguracionLocal")
data class ConfiguracionLocal(
    @PrimaryKey val clave: String,
    val valor: String
)
