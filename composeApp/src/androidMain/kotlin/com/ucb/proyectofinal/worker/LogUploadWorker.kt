package com.ucb.proyectofinal.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LogUploadWorker(
   appContext: Context,
   workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters), KoinComponent {

   // Inyectando el caso de uso
   private val fetchPopularMoviesUseCase: FetchPopularMoviesUseCase by inject()

   override suspend fun doWork(): Result {
       println("ejecutar instrucción para subir datos")
       val response = fetchPopularMoviesUseCase.invoke()
       
       return response.fold(
           onFailure = {
               Result.failure()
           },
           onSuccess = { list ->
               println("datos subidos ${list.size}")
               Result.success()
           }
       )
   }
}
