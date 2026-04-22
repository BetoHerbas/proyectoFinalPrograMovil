package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.core.data.RemoteConfigCacheRepository
import com.ucb.proyectofinal.core.data.db.AppDatabase
import com.ucb.proyectofinal.remoteconfig.MaintenanceViewModel
import com.ucb.proyectofinal.remoteconfig.RemoteConfigRepository
import com.ucb.proyectofinal.remoteconfig.RemoteConfigViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase Remote Config (mantainence flag)
    single { RemoteConfigRepository() }
    viewModel { MaintenanceViewModel(get()) }

    // Remote Config caché local (Room)
    single { get<AppDatabase>().remoteConfigDao() }
    single { RemoteConfigCacheRepository(get()) }
    viewModel { RemoteConfigViewModel(get()) }
}

