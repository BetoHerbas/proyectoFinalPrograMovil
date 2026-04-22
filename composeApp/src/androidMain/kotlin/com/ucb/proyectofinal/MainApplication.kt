package com.ucb.proyectofinal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.ucb.proyectofinal.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initKoin {
            androidContext(this@MainApplication)
            modules(org.koin.dsl.module {
                single { com.ucb.proyectofinal.worker.FetchPopularMoviesUseCase() }
            })
        }
        
        // Inicializa y agenda la subida de logs
        com.ucb.proyectofinal.worker.LogScheduler(this).schedulePeriodicaUpload()
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
