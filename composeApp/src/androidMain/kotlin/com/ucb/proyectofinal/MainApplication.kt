package com.ucb.proyectofinal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import com.ucb.proyectofinal.core.data.RemoteConfigCacheRepository
import com.ucb.proyectofinal.core.data.db.AppDatabase
import com.ucb.proyectofinal.di.initKoin
import com.ucb.proyectofinal.remoteconfig.RemoteConfigChangeMonitor
import com.ucb.proyectofinal.worker.RemoteConfigSyncScheduler
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()

        // Construir la base de datos Room
        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()

        initKoin {
            androidContext(this@MainApplication)
            modules(org.koin.dsl.module {
                single { database }
                single { com.ucb.proyectofinal.worker.FetchPopularMoviesUseCase() }
            })
        }

        // Inicializa y agenda la subida de logs
        com.ucb.proyectofinal.worker.LogScheduler(this).schedulePeriodicaUpload()

        // Programar sincronización inicial de Remote Config
        RemoteConfigSyncScheduler(this).scheduleInitialSync()

        // Iniciar monitor de cambios en Remote Config (notificaciones locales)
        val cacheRepository = org.koin.java.KoinJavaComponent.getKoin()
            .get<RemoteConfigCacheRepository>()
        RemoteConfigChangeMonitor(this, cacheRepository).startListening()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Canal para notificaciones generales (push de Firebase)
            val defaultChannel = NotificationChannel(
                "default_channel",
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal para notificaciones generales"
            }
            notificationManager.createNotificationChannel(defaultChannel)

            // Canal para cambios de Remote Config
            val configChannel = NotificationChannel(
                RemoteConfigChangeMonitor.CHANNEL_ID,
                "Cambios de Configuración",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando un valor remoto cambia"
            }
            notificationManager.createNotificationChannel(configChannel)
        }
    }
}
