package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository
import com.ucb.proyectofinal.onboarding.data.datasource.OnboardingPreferences
import org.koin.dsl.module

val appModule = module {
    single<RemoteConfigRepository> { provideRemoteConfigRepository() }
    single { OnboardingPreferences() }
}

expect fun provideRemoteConfigRepository(): RemoteConfigRepository
