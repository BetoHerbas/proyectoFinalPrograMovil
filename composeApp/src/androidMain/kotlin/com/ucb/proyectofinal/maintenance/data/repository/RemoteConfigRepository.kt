package com.ucb.proyectofinal.maintenance.domain.repository

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Implementación Android de RemoteConfigRepository.
 *
 * Usa DOS mecanismos en paralelo para garantizar actualizaciones en tiempo real:
 *  1. [addOnConfigUpdateListener] — WebSocket push de Firebase (reacción inmediata).
 *  2. Polling periódico cada [POLL_INTERVAL_MS] ms — respaldo si el push falla.
 */
actual class RemoteConfigRepository actual constructor() {

    companion object {
        /** Cada cuánto tiempo se consulta Remote Config activamente (ms). */
        private const val POLL_INTERVAL_MS = 10_000L
    }

    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            // 0 para debug (sin caché). En producción usa 3600.
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf("mantainence" to false))
    }

    actual fun observeMaintenance(): Flow<Boolean> = callbackFlow {

        /** Hace fetch + activate y emite el valor actual. */
        suspend fun fetchAndEmit() {
            try {
                remoteConfig.fetchAndActivate().await()
            } catch (_: Exception) { /* usa el valor cacheado */ }
            trySend(remoteConfig.getBoolean("mantainence"))
        }

        // ── 1. Valor inicial ──────────────────────────────────────────────
        fetchAndEmit()

        // ── 2. Push en tiempo real (WebSocket de Firebase) ───────────────
        val listener = object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnSuccessListener {
                    trySend(remoteConfig.getBoolean("mantainence"))
                }
            }
            override fun onError(error: FirebaseRemoteConfigException) {
                // Silenciamos: el polling de abajo actuará como respaldo
            }
        }
        val registration = remoteConfig.addOnConfigUpdateListener(listener)

        // ── 3. Polling periódico como respaldo ────────────────────────────
        val pollingJob = launch {
            while (isActive) {
                delay(POLL_INTERVAL_MS)
                fetchAndEmit()
            }
        }

        // ── 4. Limpieza al cancelar el Flow ───────────────────────────────
        awaitClose {
            registration.remove()
            pollingJob.cancel()
        }
    }
}


