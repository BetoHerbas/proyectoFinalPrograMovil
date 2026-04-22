package com.ucb.proyectofinal.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Worker que se ejecuta en segundo plano cuando hay conexión a internet.
 * Procesa la cola offline: sube los ítems pendientes de Room a Firebase RTDB
 * y envía una notificación local de resumen al terminar.
 */
class LogUploadWorker(
   appContext: Context,
   workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters), KoinComponent {

   private val syncUseCase: SyncPendingItemsUseCase by inject()

   override suspend fun doWork(): Result {
       return syncUseCase().fold(
           onFailure = { error ->
               println("SyncWorker: error al sincronizar → ${error.message}")
               Result.failure()
           },
           onSuccess = { count ->
               if (count > 0) {
                   println("SyncWorker: $count ítems sincronizados con Firebase")
                   LocalNotificationHelper.showSyncSummary(applicationContext, count)
               } else {
                   println("SyncWorker: sin ítems pendientes")
               }
               Result.success()
           }
       )
   }
}
