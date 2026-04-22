package com.ucb.proyectofinal.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ucb.proyectofinal.MainActivity

/**
 * Helper para disparar notificaciones locales de resumen de sincronización.
 * Usa el canal "sync_channel" creado en [MainApplication].
 */
object LocalNotificationHelper {

    private const val CHANNEL_ID = "sync_channel"
    private const val NOTIFICATION_ID = 1001

    fun showSyncSummary(context: Context, syncedCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Sincronización completada")
            .setContentText(
                if (syncedCount == 1) "1 nota sincronizada con éxito"
                else "$syncedCount notas sincronizadas con éxito"
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Se subieron $syncedCount nota(s) a la nube. Todos tus datos están actualizados.")
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }
}
