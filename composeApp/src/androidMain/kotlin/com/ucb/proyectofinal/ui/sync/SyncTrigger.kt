package com.ucb.proyectofinal.ui.sync

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ucb.proyectofinal.worker.LogUploadWorker
import org.koin.java.KoinJavaComponent.getKoin

/**
 * Implementación Android de [triggerImmediateSync].
 * Encola un OneTimeWorkRequest de [LogUploadWorker] sin restricciones de red,
 * para que se ejecute de inmediato en modo demo.
 */
actual fun triggerImmediateSync() {
    val app: Application = getKoin().get()
    val request = OneTimeWorkRequestBuilder<LogUploadWorker>().build()
    WorkManager.getInstance(app).enqueue(request)
}
