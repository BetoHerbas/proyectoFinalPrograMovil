package com.ucb.proyectofinal.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ucb.proyectofinal.core.data.db.RemoteConfigEntity
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.ucb.proyectofinal.core.data.RemoteConfigCacheRepository

/**
 * Worker que se ejecuta UNA sola vez al inicio de la app (OneTimeWorkRequest)
 * para descargar la configuración remota de Firebase y guardarla en Room.
 *
 * Claves descargadas:
 * - mantainence (Boolean)
 * - welcome_message (String)
 * - app_min_version (String)
 *
 * Si no hay internet, retorna Result.retry() y WorkManager reintentará
 * con backoff exponencial automáticamente.
 */
class RemoteConfigSyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters), KoinComponent {

    companion object {
        private const val TAG = "RemoteConfigSyncWorker"

        /** Lista de claves que se cachean localmente. */
        val CONFIG_KEYS = listOf("mantainence", "welcome_message", "app_min_version")
    }

    private val cacheRepository: RemoteConfigCacheRepository by inject()

    override suspend fun doWork(): Result {
        Log.d(TAG, "Iniciando sincronización de Remote Config...")

        return try {
            val remoteConfig = FirebaseRemoteConfig.getInstance()

            // Configurar fetch interval a 0 para debug
            val settings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            remoteConfig.setConfigSettingsAsync(settings)

            // Valores por defecto
            remoteConfig.setDefaultsAsync(
                mapOf(
                    "mantainence" to false,
                    "welcome_message" to "¡Bienvenido a la app!",
                    "app_min_version" to "1.0.0"
                )
            )

            // Descargar y activar
            remoteConfig.fetchAndActivate().await()
            Log.d(TAG, "Remote Config descargado y activado correctamente")

            // Leer las claves y construir entidades
            val now = System.currentTimeMillis()
            val configs = CONFIG_KEYS.map { key ->
                val value = remoteConfig.getString(key)
                Log.d(TAG, "  $key = $value")
                RemoteConfigEntity(
                    key = key,
                    value = value,
                    updatedAt = now
                )
            }

            // Guardar en Room
            cacheRepository.saveCachedConfig(configs)
            Log.d(TAG, "Configuración guardada en caché local (${configs.size} claves)")

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error al sincronizar Remote Config: ${e.message}", e)
            // Reintentar con backoff exponencial
            Result.retry()
        }
    }
}
