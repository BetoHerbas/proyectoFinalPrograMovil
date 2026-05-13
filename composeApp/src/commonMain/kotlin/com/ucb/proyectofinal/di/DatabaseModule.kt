package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.core.data.db.DatabaseFactory
import org.koin.dsl.module

val databaseModule = module {
    single { get<DatabaseFactory>().createDatabase() }
    single { get<com.ucb.proyectofinal.core.data.db.AppDatabase>().contentListDao() }
    single { get<com.ucb.proyectofinal.core.data.db.AppDatabase>().contentItemDao() }
    single { get<com.ucb.proyectofinal.core.data.db.AppDatabase>().userDao() }
}
