package com.ucb.proyectofinal.ui.sync

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ucb.proyectofinal.worker.LogUploadWorker
import org.koin.java.KoinJavaComponent.getKoin

private const val SYNC_WORK_NAME = "offline_sync_work"

/**
 * Encola un [LogUploadWorker] con restricción de red CONNECTED.
 * - Si hay internet ahora → se ejecuta de inmediato.
 * - Si no hay internet   → WorkManager lo mantiene en cola y lo ejecuta
 *   automáticamente en cuanto se recupere la conexión.
 *
 * Usa [ExistingWorkPolicy.KEEP] para no duplicar la tarea si ya está en cola.
 */
actual fun triggerImmediateSync() {
    val app: Application = getKoin().get()

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val request = OneTimeWorkRequestBuilder<LogUploadWorker>()
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(app)
        .enqueueUniqueWork(SYNC_WORK_NAME, ExistingWorkPolicy.KEEP, request)
}
