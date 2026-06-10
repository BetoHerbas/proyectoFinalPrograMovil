package com.ucb.proyectofinal.di

import com.ucb.proyectofinal.maintenance.data.repository.RemoteConfigRepositoryImpl
import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository

actual fun provideRemoteConfigRepository(): RemoteConfigRepository = RemoteConfigRepositoryImpl()
