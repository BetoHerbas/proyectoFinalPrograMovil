package com.ucb.proyectofinal.core.data.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

actual class DatabaseFactory {
    actual fun createDatabase(): AppDatabase =
        Room.databaseBuilder<AppDatabase>(
            name = NSHomeDirectory() + "/app_database.db"
        )
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(true)
            .build()
}
