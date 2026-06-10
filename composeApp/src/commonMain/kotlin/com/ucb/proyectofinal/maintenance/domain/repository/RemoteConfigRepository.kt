package com.ucb.proyectofinal.maintenance.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Contrato puro de Kotlin para Firebase Remote Config.
 * El dominio depende únicamente de esta interfaz, sin acoplarse a KMP.
 */
interface RemoteConfigRepository {
    /**
     * Emite el valor actual de "mantainence" y luego emite cada vez que
     * Firebase Remote Config detecta un cambio publicado desde la consola.
     */
    fun observeMaintenance(): Flow<Boolean>

    /**
     * Emite true si videogame_category_enabled = true Y el usuario actual
     * pertenece al grupo videogame_target_group. De lo contrario emite false.
     */
    fun observeVideogameCategoryEnabled(): Flow<Boolean>

    /**
     * Obtiene el JSON de configuración del onboarding desde Remote Config.
     * Realiza fetch + activate y retorna el string del parámetro "onboarding_config".
     */
    suspend fun fetchOnboardingConfig(): String
}
