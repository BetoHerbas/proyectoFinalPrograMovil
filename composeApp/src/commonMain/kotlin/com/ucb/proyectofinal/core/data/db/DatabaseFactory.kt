package com.ucb.proyectofinal.core.data.db

expect class DatabaseFactory {
    fun createDatabase(): AppDatabase
}
