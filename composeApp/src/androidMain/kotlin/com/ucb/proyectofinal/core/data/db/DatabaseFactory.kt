package com.ucb.proyectofinal.core.data.db

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual class DatabaseFactory(private val context: Context) {
    actual fun createDatabase(): AppDatabase =
        Room.databaseBuilder<AppDatabase>(
            context = context.applicationContext,
            name = context.getDatabasePath("app_database.db").absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(true)
            .build()
}
