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
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Canal general (existente)
            NotificationChannel("default_channel", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Canal para notificaciones generales"
                manager.createNotificationChannel(this)
            }

            // Canal de sync — IMPORTANCE_HIGH para que aparezca como popup (heads-up)
            NotificationChannel("sync_channel", "Sincronización Offline", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notificaciones cuando los datos se sincronizan con Firebase"
                manager.createNotificationChannel(this)
            }
        }
    }
}
