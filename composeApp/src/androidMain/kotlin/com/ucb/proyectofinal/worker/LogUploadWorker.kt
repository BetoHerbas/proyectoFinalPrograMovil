package com.ucb.proyectofinal.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class LogUploadWorker(
   appContext: Context,
   workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

   override suspend fun doWork(): Result {
       println("ejecutar instrucción para subir datos")
       return Result.success()
   }
}
