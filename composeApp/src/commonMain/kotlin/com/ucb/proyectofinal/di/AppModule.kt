package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository
import org.koin.dsl.module

val appModule = module {
    single { RemoteConfigRepository() }
}
