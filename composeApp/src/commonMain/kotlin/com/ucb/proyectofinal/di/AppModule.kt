package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.remoteconfig.MaintenanceViewModel
import com.ucb.proyectofinal.remoteconfig.RemoteConfigRepository
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase Remote Config
    single { RemoteConfigRepository() }
    viewModel { MaintenanceViewModel(get()) }
}
