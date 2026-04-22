package com.ucb.proyectofinal.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones CRUD sobre la tabla remote_config.
 * Permite observar cambios reactivamente con Flow y consultar valores
 * individuales por clave.
 */
@Dao
interface RemoteConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(configs: List<RemoteConfigEntity>)

    @Query("SELECT * FROM remote_config ORDER BY `key` ASC")
    fun observeAll(): Flow<List<RemoteConfigEntity>>

    @Query("SELECT * FROM remote_config WHERE `key` = :key")
    suspend fun getByKey(key: String): RemoteConfigEntity?

    @Query("SELECT COUNT(*) FROM remote_config")
    suspend fun count(): Int
}
