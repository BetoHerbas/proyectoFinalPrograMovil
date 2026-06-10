package com.ucb.proyectofinal.maintenance.domain.usecase

import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository
import kotlinx.coroutines.flow.Flow

class ObserveMaintenanceUseCase(
    private val remoteConfigRepository: RemoteConfigRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return remoteConfigRepository.observeMaintenance()
    }
}
