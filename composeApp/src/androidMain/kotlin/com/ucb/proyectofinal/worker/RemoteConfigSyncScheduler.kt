package com.ucb.proyectofinal.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * Programa la descarga inicial de Firebase Remote Config mediante un
 * OneTimeWorkRequest con constraint de conexión a internet.
 *
 * Se usa ExistingWorkPolicy.KEEP para que, si el trabajo ya fue encolado
 * (por ejemplo, en un reinicio de la app), no se duplique.
 */
class RemoteConfigSyncScheduler(private val context: Context) {

    companion object {
        private const val WORK_NAME = "remote_config_initial_sync"
    }

    fun scheduleInitialSync() {
        val request = OneTimeWorkRequestBuilder<RemoteConfigSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.KEEP,
                request
            )
    }
}
