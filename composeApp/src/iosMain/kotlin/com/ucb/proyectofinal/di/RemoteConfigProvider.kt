package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository

// iOS stub — Remote Config not fully implemented on iOS
actual fun provideRemoteConfigRepository(): RemoteConfigRepository {
    error("RemoteConfigRepository not available on iOS")
}
