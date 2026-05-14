package com.ucb.proyectofinal.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await

/**
 * Worker periodico de A/B testing con Remote Config.
 *
 * Intervalo por defecto: 30 min (todos los usuarios).
 * Cuando videogame_category_enabled = true y el usuario pertenece a videogame_target_group:
 * el intervalo se reduce a 15 min.
 */
class ABTestingWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val database = FirebaseDatabase.getInstance().reference
        try {
            remoteConfig.fetchAndActivate().await()
        } catch (_: Exception) { /* usa el valor en cache */ }

        val videogameEnabled = remoteConfig.getBoolean("videogame_category_enabled")
        val videogameTargetGroup = remoteConfig.getString("videogame_target_group")
            .ifBlank { "B" }
            .uppercase()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: return Result.success()

        val userGroup = runCatching {
            database.child("users").child(userId).child("abGroup").get().await()
                .getValue(String::class.java)
                ?.trim()
                ?.uppercase()
                ?.takeIf { it == "A" || it == "B" }
                ?: "A"
        }.getOrDefault("A")

        val videogameEnabledForUser = videogameEnabled &&
            (videogameTargetGroup == "ALL" || userGroup == videogameTargetGroup)

        logEvent(
            userId = userId,
            userAssignedGroup = userGroup,
            event = "worker_ran",
            videogameEnabled = videogameEnabled,
            videogameTargetGroup = videogameTargetGroup,
            videogameEnabledForUser = videogameEnabledForUser
        )

        if (videogameEnabledForUser) {
            val prefs = applicationContext.getSharedPreferences(
                "ab_testing_prefs", Context.MODE_PRIVATE
            )
            val alreadyNotified = prefs.getBoolean("videogame_notification_sent", false)
            if (!alreadyNotified) {
                sendVideogameNotification()
                prefs.edit().putBoolean("videogame_notification_sent", true).apply()
                logEvent(
                    userId = userId,
                    userAssignedGroup = userGroup,
                    event = "notification_sent",
                    videogameEnabled = videogameEnabled,
                    videogameTargetGroup = videogameTargetGroup,
                    videogameEnabledForUser = videogameEnabledForUser
                )
            }
        }

        return Result.success()
    }

    private fun sendVideogameNotification() {
        val notification = NotificationCompat.Builder(applicationContext, "default_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Novedad disponible!")
            .setContentText("Acceso anticipado: ya puedes crear listas de videojuegos")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1002, notification)
    }

    private fun logEvent(
        userId: String,
        userAssignedGroup: String,
        event: String,
        videogameEnabled: Boolean,
        videogameTargetGroup: String,
        videogameEnabledForUser: Boolean
    ) {
        val timestamp = System.currentTimeMillis()
        val syncIntervalMinutes = ABTestingScheduler.intervalForEnabled(videogameEnabledForUser)
        val logData = mapOf(
            "userAssignedGroup" to userAssignedGroup,
            "syncIntervalMinutes" to syncIntervalMinutes,
            "event" to event,
            "videogameEnabled" to videogameEnabled,
            "videogameTargetGroup" to videogameTargetGroup,
            "videogameEnabledForUser" to videogameEnabledForUser,
            "timestamp" to timestamp
        )
        FirebaseDatabase.getInstance().reference
            .child("ab_logs")
            .child(userId)
            .child(timestamp.toString())
            .setValue(logData)
    }
}
