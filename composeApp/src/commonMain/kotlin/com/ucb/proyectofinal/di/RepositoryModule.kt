package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.auth.data.repository.AuthRepositoryImpl
import com.ucb.proyectofinal.auth.domain.repository.AuthRepository
import com.ucb.proyectofinal.auth.domain.repository.FirebaseAuthDataSource
import com.ucb.proyectofinal.lists.data.repository.ContentListRepositoryImpl
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import com.ucb.proyectofinal.lists.domain.repository.FirebaseRealtimeListsDataSource
import org.koin.dsl.module

val repositoryModule = module {
    single { FirebaseAuthDataSource() }
    single { FirebaseRealtimeListsDataSource() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<ContentListRepository> { ContentListRepositoryImpl(get(), get(), get()) }
}
