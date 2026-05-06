package com.ucb.proyectofinal.maintenance.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interfaz expect/actual para Firebase Remote Config.
 * La implementación Android escucha cambios en tiempo real vía
 * addOnConfigUpdateListener y los emite como un Flow.
 */
expect class RemoteConfigRepository() {
    /**
     * Emite el valor actual de "mantainence" y luego emite cada vez que
     * Firebase Remote Config detecta un cambio publicado desde la consola.
     */
    fun observeMaintenance(): Flow<Boolean>
}

