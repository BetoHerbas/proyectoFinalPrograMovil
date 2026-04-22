package com.ucb.proyectofinal.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ucb.proyectofinal.MainActivity

/**
 * Helper para lanzar notificaciones locales de resumen de sincronización.
 *
 * Usa el canal "sync_channel" (IMPORTANCE_HIGH) para que aparezca como
 * heads-up popup en pantalla, no solo en el panel de notificaciones.
 *
 * ⚠️ Si instalaste la app antes de este cambio, desinstala y reinstala
 *    para que Android tome el nuevo canal de alta importancia.
 */
object LocalNotificationHelper {
    private const val CHANNEL_ID      = "sync_channel"
    private const val NOTIFICATION_ID = 1001

    /**
     * Muestra una notificación local con el resumen de la sincronización.
     * @param syncedCount número de ítems que se subieron a Firebase.
     */
    fun showSyncSummary(context: Context, syncedCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val body = if (syncedCount == 1)
            "1 ítem subido a Firebase Realtime Database"
        else
            "$syncedCount ítems subidos a Firebase Realtime Database"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("✅ Sincronización completada")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            // PRIORITY_HIGH → heads-up popup visible en pantalla inmediatamente
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }
}
