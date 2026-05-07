package com.ucb.proyectofinal.core.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentItemDao {
    @Query("SELECT * FROM content_items WHERE listId = :listId ORDER BY rowid DESC")
    fun getItemsByList(listId: String): Flow<List<ContentItemEntity>>

    @Query("SELECT COUNT(*) FROM content_items WHERE listId = :listId")
    suspend fun countByList(listId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ContentItemEntity)

    @Update
    suspend fun update(item: ContentItemEntity)

    @Delete
    suspend fun delete(item: ContentItemEntity)
}
