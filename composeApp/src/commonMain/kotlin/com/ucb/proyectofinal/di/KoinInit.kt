package com.ucb.proyectofinal.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(
    platformModules: List<Module> = emptyList(),
    config: (KoinApplication.() -> Unit)? = null
) {
    try {
        startKoin {
            config?.invoke(this)
            modules(
                platformModules +
                listOf(appModule, databaseModule, networkModule, repositoryModule, useCaseModule, viewModelModule)
            )
        }
    } catch (e: Exception) {
        // Ignored for Robolectric tests where startKoin might be called multiple times
    }
}
