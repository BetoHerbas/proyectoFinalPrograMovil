package com.ucb.proyectofinal.remoteconfig

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementación iOS stub de RemoteConfigRepository.
 * Siempre emite false (sin mantenimiento) hasta integrar el SDK nativo de Firebase iOS.
 */
actual class RemoteConfigRepository actual constructor() {
    actual fun observeMaintenance(): Flow<Boolean> = flowOf(false)
}

