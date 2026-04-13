package com.ucb.proyectofinal.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual suspend fun getToken(): String =
   suspendCoroutine { continuation ->
       FirebaseMessaging.getInstance().token
           .addOnCompleteListener { task ->
               if (!task.isSuccessful) {
                   Log.w("FIREBASE", "getInstanceId failed", task.exception)
                   continuation.resumeWithException(task.exception ?: Exception("Unknown error"))
                   return@addOnCompleteListener
               }
               // Si la tarea fue exitosa, se obtiene el token
               val token = task.result
               Log.d("FIREBASE", "FCM Token: $token")

               // Reanudar la ejecución con el token
               continuation.resume(token ?: "")
           }
}
