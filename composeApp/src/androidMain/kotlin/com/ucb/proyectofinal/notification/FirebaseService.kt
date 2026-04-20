package com.ucb.proyectofinal.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ucb.proyectofinal.MainActivity
import com.ucb.proyectofinal.R

class FirebaseService : FirebaseMessagingService() {
    companion object {
        val TAG = FirebaseService::class.java.simpleName
        private const val CHANNEL_ID = "default_channel"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Desde: ${remoteMessage.from}")

        // Si el mensaje contiene una notificación, la mostramos manualmente
        // (Esto es necesario para cuando la app está en primer plano)
        remoteMessage.notification?.let {
            showNotification(it.title ?: "Notificación", it.body ?: "")
        }

        // Si el mensaje contiene datos adicionales (data payload)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Carga de datos del mensaje: ${remoteMessage.data}")
            // Aquí podrías procesar datos personalizados si no vienen como notificación
            if (remoteMessage.notification == null) {
                val title = remoteMessage.data["title"] ?: "Aviso"
                val body = remoteMessage.data["body"] ?: ""
                showNotification(title, body)
            }
        }
    }

    private fun showNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono temporal del sistema
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Usamos el tiempo actual como ID para permitir múltiples notificaciones
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
