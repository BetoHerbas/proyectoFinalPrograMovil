package com.ucb.proyectofinal.remoteconfig

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.ucb.proyectofinal.MainActivity
import com.ucb.proyectofinal.core.data.RemoteConfigCacheRepository
import com.ucb.proyectofinal.core.data.db.RemoteConfigEntity
import com.ucb.proyectofinal.worker.RemoteConfigSyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Monitor que escucha cambios en Firebase Remote Config en tiempo real.
 *
 * Cuando Firebase detecta un cambio (publicado desde la consola):
 * 1. Activa la nueva configuración.
 * 2. Compara cada valor nuevo con el valor guardado en Room (caché local).
 * 3. Si algún valor cambió:
 *    - Actualiza Room con el nuevo valor.
 *    - Dispara una notificación push LOCAL informando al usuario del cambio.
 *
 * Integra: Firebase Remote Config + Notificaciones push locales + Room.
 */
class RemoteConfigChangeMonitor(
    private val context: Context,
    private val cacheRepository: RemoteConfigCacheRepository
) {

    companion object {
        private const val TAG = "RCChangeMonitor"
        const val CHANNEL_ID = "config_changes_channel"
        private const val NOTIFICATION_BASE_ID = 5000
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    /**
     * Comienza a escuchar cambios de Remote Config.
     * Primero hace una verificación inmediata por si hubo cambios mientras
     * la app estaba cerrada, y luego se queda escuchando en tiempo real.
     */
    fun startListening() {
        Log.d(TAG, "Iniciando monitoreo de cambios en Remote Config...")

        // 1. Verificación inicial: Buscar cambios que ocurrieron mientras la app estaba cerrada
        scope.launch {
            try {
                remoteConfig.fetchAndActivate().addOnSuccessListener {
                    scope.launch { compareAndNotify() }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en fetch inicial: ${e.message}")
            }
        }

        // 2. Quedarse escuchando en tiempo real para cambios futuros (mientras la app está abierta)
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Cambio detectado en Remote Config en tiempo real. Claves: ${configUpdate.updatedKeys}")

                // Activar los nuevos valores
                remoteConfig.activate().addOnSuccessListener {
                    Log.d(TAG, "Nuevos valores activados correctamente")

                    // Comparar con Room y notificar
                    scope.launch {
                        compareAndNotify()
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.e(TAG, "Error en el listener de Remote Config: ${error.message}")
            }
        })
    }

    /**
     * Compara los valores actuales de Firebase Remote Config con los guardados
     * en Room. Si hay diferencias, actualiza Room y dispara una notificación local.
     */
    private suspend fun compareAndNotify() {
        val now = System.currentTimeMillis()
        val changes = mutableListOf<ConfigChange>()

        for (key in RemoteConfigSyncWorker.CONFIG_KEYS) {
            val newValue = remoteConfig.getString(key)
            val oldEntity = cacheRepository.getByKey(key)
            val oldValue = oldEntity?.value

            if (oldValue != null && oldValue != newValue) {
                Log.d(TAG, "Cambio detectado → $key: '$oldValue' → '$newValue'")
                changes.add(ConfigChange(key, oldValue, newValue))
            } else if (oldValue == null) {
                Log.d(TAG, "Nueva clave → $key: '$newValue'")
                changes.add(ConfigChange(key, "(sin valor previo)", newValue))
            }
        }

        // Actualizar Room con los nuevos valores
        val updatedConfigs = RemoteConfigSyncWorker.CONFIG_KEYS.map { key ->
            RemoteConfigEntity(
                key = key,
                value = remoteConfig.getString(key),
                updatedAt = now
            )
        }
        cacheRepository.saveCachedConfig(updatedConfigs)
        Log.d(TAG, "Caché local actualizada con ${updatedConfigs.size} claves")

        // Disparar notificación si hubo cambios
        if (changes.isNotEmpty()) {
            sendChangeNotification(changes)
        }
    }

    /**
     * Dispara una notificación push local informando al usuario de los cambios
     * detectados en Remote Config.
     */
    private fun sendChangeNotification(changes: List<ConfigChange>) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir el texto del cuerpo de la notificación
        val body = changes.joinToString("\n") { change ->
            val label = when (change.key) {
                "mantainence" -> "Modo Mantenimiento"
                "welcome_message" -> "Mensaje de Bienvenida"
                "app_min_version" -> "Versión Mínima"
                else -> change.key
            }
            "• $label: ${change.oldValue} → ${change.newValue}"
        }

        val title = if (changes.size == 1) {
            "⚙️ Configuración actualizada"
        } else {
            "⚙️ ${changes.size} configuraciones actualizadas"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle(title)
            .setContentText(
                if (changes.size == 1) body
                else "${changes.size} valores cambiaron"
            )
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            NOTIFICATION_BASE_ID + System.currentTimeMillis().toInt() % 1000,
            notification
        )

        Log.d(TAG, "Notificación local enviada: $title")
    }

    /**
     * Representa un cambio detectado en una clave de Remote Config.
     */
    private data class ConfigChange(
        val key: String,
        val oldValue: String,
        val newValue: String
    )
}
