package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.core.data.network.createHttpClient
import org.koin.dsl.module

val networkModule = module {
    single { createHttpClient() }
}
