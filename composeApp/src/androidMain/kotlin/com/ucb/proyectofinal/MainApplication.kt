package com.ucb.proyectofinal

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import com.ucb.proyectofinal.core.data.db.AppDatabase
import com.ucb.proyectofinal.di.initKoin
import com.ucb.proyectofinal.feature.notes.data.datasource.FirebaseNotesDataSource
import com.ucb.proyectofinal.feature.notes.domain.usecase.SyncPendingNotesUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class MainApplication : Application() {

    private val androidModule = module {
        // Room Database (Android-specific builder)
        single {
            Room.databaseBuilder<AppDatabase>(
                context = androidContext().applicationContext,
                name = "app_database.db"
            ).fallbackToDestructiveMigration(true).build()
        }
        // NoteDao provisto por AppDatabase
        single { get<AppDatabase>().getNoteDao() }

        // Firebase Realtime Database datasource
        single { FirebaseNotesDataSource() }

        // Sync use case (Android-only: depende de FirebaseNotesDataSource)
        single { SyncPendingNotesUseCase(get(), get()) }

        // Legacy — FetchPopularMoviesUseCase (mantenido para compatibilidad con LogScheduler)
        single { com.ucb.proyectofinal.worker.FetchPopularMoviesUseCase() }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        initKoin {
            androidContext(this@MainApplication)
            modules(androidModule)
        }

        // Agenda sincronización periódica al recuperar conexión
        com.ucb.proyectofinal.worker.LogScheduler(this).schedulePeriodicaUpload()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Canal general
            NotificationChannel(
                "default_channel",
                "Notificaciones Generales",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal para notificaciones generales"
                notificationManager.createNotificationChannel(this)
            }

            // Canal de sincronización
            NotificationChannel(
                "sync_channel",
                "Sincronización",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de sincronización de notas con la nube"
                notificationManager.createNotificationChannel(this)
            }
        }
    }
}

