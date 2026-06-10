package com.ucb.proyectofinal.onboarding.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.FirebaseApp

/**
 * Android implementation using SharedPreferences.
 * Gets Context from FirebaseApp (always initialized before Koin).
 */
actual class OnboardingPreferences actual constructor() {

    private val prefs: SharedPreferences by lazy {
        FirebaseApp.getInstance().applicationContext
            .getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
    }

    actual fun isOnboardingCompleted(): Boolean =
        prefs.getBoolean("onboarding_completed", false)

    actual fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
    }
}
