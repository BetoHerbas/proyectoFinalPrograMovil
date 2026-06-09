package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository
import com.ucb.proyectofinal.onboarding.data.local.OnboardingPreferences
import org.koin.dsl.module

val appModule = module {
    single { RemoteConfigRepository() }
    single { OnboardingPreferences() }
}
