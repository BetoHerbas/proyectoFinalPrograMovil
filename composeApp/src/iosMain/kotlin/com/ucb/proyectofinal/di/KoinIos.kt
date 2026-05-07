package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.core.data.db.DatabaseFactory
import org.koin.dsl.module

fun initKoinIos() {
    initKoin(
        platformModules = listOf(
            module { single { DatabaseFactory() } }
        )
    )
}
