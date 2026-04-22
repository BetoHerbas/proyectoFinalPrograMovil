package com.example.examen3moviles

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Dao
interface EventoDao {
    @Insert
    suspend fun insertarEvento(evento: RegistroEvento)

    @Query("SELECT * FROM RegistroEvento WHERE sincronizado = 0")
    suspend fun obtenerEventosNoSincronizados(): List<RegistroEvento>

    @Query("UPDATE RegistroEvento SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarComoSincronizado(id: Int)
}

@Dao
interface ConfiguracionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarConfiguracion(config: ConfiguracionLocal)

    @Query("SELECT * FROM ConfiguracionLocal WHERE clave = :clave LIMIT 1")
    suspend fun obtenerConfiguracion(clave: String): ConfiguracionLocal?
}

@Database(entities = [RegistroEvento::class, ConfiguracionLocal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventoDao(): EventoDao
    abstract fun configuracionDao(): ConfiguracionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "examen3_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
