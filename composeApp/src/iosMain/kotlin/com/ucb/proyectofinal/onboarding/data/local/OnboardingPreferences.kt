package com.ucb.proyectofinal.onboarding.data.local

/**
 * iOS stub — always reports onboarding as not completed.
 */
actual class OnboardingPreferences actual constructor() {
    actual fun isOnboardingCompleted(): Boolean = false
    actual fun setOnboardingCompleted(completed: Boolean) { /* no-op */ }
}
