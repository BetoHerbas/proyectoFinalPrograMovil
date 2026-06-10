package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.auth.data.datasource.FirebaseAuthDataSource
import com.ucb.proyectofinal.auth.data.repository.AuthRepositoryImpl
import com.ucb.proyectofinal.auth.domain.repository.AuthRepository
import com.ucb.proyectofinal.lists.data.datasource.FirebaseRealtimeListsDataSource
import com.ucb.proyectofinal.lists.data.repository.ContentListRepositoryImpl
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import com.ucb.proyectofinal.settings.data.repository.SettingsRepositoryImpl
import com.ucb.proyectofinal.settings.domain.repository.SettingsRepository
import com.ucb.proyectofinal.onboarding.domain.repository.OnboardingRepository
import com.ucb.proyectofinal.onboarding.data.repository.OnboardingRepositoryImpl
import com.ucb.proyectofinal.lists.data.datasource.CatalogRemoteDataSource
import com.ucb.proyectofinal.lists.data.service.CatalogServices
import org.koin.dsl.module

val repositoryModule = module {
    single { FirebaseAuthDataSource() }
    single { FirebaseRealtimeListsDataSource() }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<CatalogRemoteDataSource> { CatalogServices(get()) }
    single<ContentListRepository> { ContentListRepositoryImpl(get(), get(), get(), get(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl() }
    single<OnboardingRepository> { OnboardingRepositoryImpl(get(), get()) }
}
