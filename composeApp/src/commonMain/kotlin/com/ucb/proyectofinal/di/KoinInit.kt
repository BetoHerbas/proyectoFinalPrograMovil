package com.ucb.proyectofinal.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: (KoinApplication.() -> Unit)? = null
) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModules +
            listOf(appModule, databaseModule, networkModule, repositoryModule, useCaseModule, viewModelModule)
        )
    }
}
