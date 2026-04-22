package com.ucb.proyectofinal.core.data

import com.ucb.proyectofinal.core.data.db.RemoteConfigDao
import com.ucb.proyectofinal.core.data.db.RemoteConfigEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio intermediario entre Firebase Remote Config y Room.
 * La UI siempre lee de aquí (offline-first):
 * - El Worker escribe los valores descargados usando [saveCachedConfig].
 * - El ViewModel observa cambios usando [observeCachedConfig].
 * - Si no hay internet, Room devuelve la última versión guardada.
 */
class RemoteConfigCacheRepository(
    private val remoteConfigDao: RemoteConfigDao
) {

    /** Observa todos los pares clave-valor cacheados de forma reactiva. */
    fun observeCachedConfig(): Flow<List<RemoteConfigEntity>> =
        remoteConfigDao.observeAll()

    /** Guarda (o actualiza) una lista de configuraciones en la caché local. */
    suspend fun saveCachedConfig(configs: List<RemoteConfigEntity>) =
        remoteConfigDao.insertAll(configs)

    /** Obtiene un valor de configuración por clave. */
    suspend fun getByKey(key: String): RemoteConfigEntity? =
        remoteConfigDao.getByKey(key)

    /** Verifica si ya hay configuración cacheada (para saber si es la primera vez). */
    suspend fun hasCachedConfig(): Boolean =
        remoteConfigDao.count() > 0
}
