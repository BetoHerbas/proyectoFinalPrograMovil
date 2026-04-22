package com.example.examen3moviles

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class SincronizadorEventos(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        val baseDeDatos = AppDatabase.obtenerInstancia(applicationContext)
        val eventoDao = baseDeDatos.eventoDao()
        val eventosNoSincronizados = eventoDao.obtenerEventosNoSincronizados()

        if (eventosNoSincronizados.isEmpty()) return androidx.work.ListenableWorker.Result.success()

        val firebaseRef = Firebase.database.getReference("eventos_registro")

        return try {
            eventosNoSincronizados.forEach { evento ->
                val clave = firebaseRef.push().key ?: return@forEach
                val datos = mapOf(
                    "tipo" to evento.tipo,
                    "timestamp" to evento.timestamp
                )
                firebaseRef.child(clave).setValue(datos).await()
                eventoDao.marcarComoSincronizado(evento.id)
            }
            androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            androidx.work.ListenableWorker.Result.retry()
        }
    }
}
