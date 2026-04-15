package com.ucb.proyectofinal

import android.app.Application
import com.ucb.proyectofinal.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
            modules(org.koin.dsl.module {
                single { com.ucb.proyectofinal.worker.FetchPopularMoviesUseCase() }
            })
        }
        
        // Inicializa y agenda la subida de logs
        com.ucb.proyectofinal.worker.LogScheduler(this).schedulePeriodicaUpload()
    }
}
