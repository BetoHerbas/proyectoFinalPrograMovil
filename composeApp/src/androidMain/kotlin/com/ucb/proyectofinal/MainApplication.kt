package com.ucb.proyectofinal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.FirebaseApp
import com.ucb.proyectofinal.core.data.db.DatabaseFactory
import com.ucb.proyectofinal.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ensureFirebaseInitialized()
        createNotificationChannel()
        initKoin(
            platformModules = listOf(
                org.koin.dsl.module {
                    single { DatabaseFactory(get()) }
                    single { com.ucb.proyectofinal.worker.FetchPopularMoviesUseCase() }
                }
            )
        ) {
            androidContext(this@MainApplication)
        }
        
        // Inicializa y agenda la subida de logs
        com.ucb.proyectofinal.worker.LogScheduler(this).schedulePeriodicaUpload()
        // Agenda el worker de A/B testing (usa el ultimo intervalo guardado)
        com.ucb.proyectofinal.worker.ABTestingScheduler(this).schedule()
    }

    private fun ensureFirebaseInitialized() {
        val app = FirebaseApp.initializeApp(this)
        if (app == null) {
            Log.e(
                "MainApplication",
                "Firebase no pudo inicializarse. Verifica composeApp/google-services.json y applicationId."
            )
            return
        }
        Log.i(
            "MainApplication",
            "Firebase inicializado: appId=${app.options.applicationId}, projectId=${app.options.projectId}"
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Channel"
            val descriptionText = "Canal para notificaciones generales"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("default_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
