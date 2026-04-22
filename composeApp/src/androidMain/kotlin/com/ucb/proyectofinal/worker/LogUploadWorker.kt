package com.ucb.proyectofinal.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ucb.proyectofinal.feature.notes.domain.usecase.SyncPendingNotesUseCase
import com.ucb.proyectofinal.notification.LocalNotificationHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Worker que se ejecuta en segundo plano cuando hay conexión a internet.
 * Procesa la cola de notas pendientes y las sube a Firebase Realtime Database.
 * Al terminar, envía una notificación local de resumen.
 */
class LogUploadWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters), KoinComponent {

    private val syncPendingNotesUseCase: SyncPendingNotesUseCase by inject()

    override suspend fun doWork(): Result {
        return try {
            val syncedCount = syncPendingNotesUseCase()

            if (syncedCount > 0) {
                LocalNotificationHelper.showSyncSummary(applicationContext, syncedCount)
            }

            println("SyncWorker: $syncedCount nota(s) sincronizadas")
            Result.success()
        } catch (e: Exception) {
            println("SyncWorker: Error durante sincronización — ${e.message}")
            Result.retry()
        }
    }
}

