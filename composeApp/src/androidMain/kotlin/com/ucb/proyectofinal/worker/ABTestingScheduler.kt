package com.ucb.proyectofinal.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class ABTestingScheduler(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "ab_testing_prefs"
        private const val KEY_VIDEOGAME_ENABLED = "last_known_videogame_enabled"

        /**
         * Intervalo en minutos segun si la feature de videojuegos esta activa para el usuario:
         *  - true  (usuario en grupo objetivo con flag activa) -> 15 min
         *  - false (flag inactiva o usuario fuera del grupo objetivo) -> 30 min
         */
        fun intervalForEnabled(enabled: Boolean): Long = if (enabled) 15L else 30L
    }

    /**
     * Programa (o reprograma) el worker con el intervalo correcto.
     * Persiste el valor en SharedPreferences para sobrevivir reinicios de la app.
     */
    fun scheduleForEnabled(enabled: Boolean) {
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_VIDEOGAME_ENABLED, enabled)
            .apply()

        val intervalMinutes = intervalForEnabled(enabled)
        val request = PeriodicWorkRequest.Builder(
            ABTestingWorker::class.java,
            intervalMinutes,
            TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()

        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
            "abTestingWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    /**
     * Llamado al inicio de la app. Usa el ultimo estado persistido en prefs.
     * Si no hay valor guardado (primera instalacion), usa false -> 30 min por defecto.
     */
    fun schedule() {
        val savedEnabled = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_VIDEOGAME_ENABLED, false)
        scheduleForEnabled(savedEnabled)
    }
}
