package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository
import com.ucb.proyectofinal.maintenance.presentation.viewmodel.MaintenanceViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Firebase Remote Config
    single { RemoteConfigRepository() }
    viewModel { MaintenanceViewModel(get()) }
}
