package com.ucb.proyectofinal.core.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity)

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    @Query("SELECT * FROM todo_entity")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todo_entity WHERE id = :id")
    suspend fun getTodoById(id: Long): TodoEntity?

    /** Retorna todos los ítems aún pendientes de sincronización */
    @Query("SELECT * FROM todo_entity WHERE isPending = 1")
    suspend fun getPendingTodos(): List<TodoEntity>

    /** Marca un ítem como sincronizado y registra el timestamp */
    @Query("UPDATE todo_entity SET isPending = 0, syncedAt = :syncedAt WHERE id = :id")
    suspend fun markAsSynced(id: Long, syncedAt: Long)
}
