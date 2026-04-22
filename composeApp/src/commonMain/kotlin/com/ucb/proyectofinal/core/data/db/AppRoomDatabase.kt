package com.ucb.proyectofinal.core.data.db

import androidx.room.*
import com.ucb.proyectofinal.feature.notes.data.entity.NoteEntity

@Database(
    entities = [TodoEntity::class, NoteEntity::class],
    version = 2
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): TodoDao
    abstract fun getNoteDao(): NoteDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}