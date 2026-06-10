package com.ucb.proyectofinal.onboarding.data.datasource

/**
 * Abstracción expect/actual para persistir el flag de onboarding completado.
 * Android usa SharedPreferences, iOS usa NSUserDefaults (stub).
 */
expect class OnboardingPreferences() {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)
}
