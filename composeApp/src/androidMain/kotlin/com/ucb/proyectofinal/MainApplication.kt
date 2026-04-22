package com.ucb.proyectofinal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.ucb.proyectofinal.core.data.db.getDatabaseBuilder
import com.ucb.proyectofinal.di.initKoin
import com.ucb.proyectofinal.worker.SyncPendingItemsUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initKoin {
            androidContext(this@MainApplication)
            modules(module {
                // Base de datos Room (con destructive migration para la demo)
                single { getDatabaseBuilder(get<android.app.Application>()).build() }
                // DAO — expuesto para que OfflineSyncViewModel (en appModule) lo resuelva
                single { get<com.ucb.proyectofinal.core.data.db.AppDatabase>().getDao() }
                // Use case de sincronización offline
                single { SyncPendingItemsUseCase(get()) }
            })
        }

        // Agenda la sincronización periódica en segundo plano (cada 15 min, requiere red)
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
