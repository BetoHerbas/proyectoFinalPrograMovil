package com.ucb.proyectofinal.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ucb.proyectofinal.MainActivity

/**
 * Helper para lanzar notificaciones locales de resumen de sincronización.
 */
object LocalNotificationHelper {
    private const val CHANNEL_ID   = "default_channel"
    private const val NOTIFICATION_ID = 1001

    /**
     * Muestra una notificación local con el resumen de la sincronización.
     * @param context contexto de la aplicación
     * @param syncedCount número de ítems que se subieron a Firebase
     */
    fun showSyncSummary(context: Context, syncedCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "✅ Sincronización completada"
        val body  = if (syncedCount == 1)
            "1 ítem subido a Firebase Realtime Database"
        else
            "$syncedCount ítems subidos a Firebase Realtime Database"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }
}
