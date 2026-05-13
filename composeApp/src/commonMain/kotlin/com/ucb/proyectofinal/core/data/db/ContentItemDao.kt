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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ContentItemEntity>)

    @Update
    suspend fun update(item: ContentItemEntity)

    @Delete
    suspend fun delete(item: ContentItemEntity)

    @Query("DELETE FROM content_items WHERE listId = :listId")
    suspend fun deleteByListId(listId: String)

    @Query("DELETE FROM content_items WHERE id = :id")
    suspend fun deleteById(id: String)
}
