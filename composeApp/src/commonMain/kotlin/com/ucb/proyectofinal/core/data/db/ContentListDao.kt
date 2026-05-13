package com.ucb.proyectofinal.core.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentListDao {
    @Query("SELECT * FROM content_lists ORDER BY rowid DESC")
    fun getAllLists(): Flow<List<ContentListEntity>>

    @Query("SELECT * FROM content_lists WHERE id = :id")
    suspend fun getById(id: String): ContentListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: ContentListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lists: List<ContentListEntity>)

    @Update
    suspend fun update(list: ContentListEntity)

    @Delete
    suspend fun delete(list: ContentListEntity)

    @Query("DELETE FROM content_lists WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM content_lists")
    suspend fun clearAll()
}
