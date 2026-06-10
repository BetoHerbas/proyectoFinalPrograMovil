package com.ucb.proyectofinal.settings.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.FirebaseApp

actual class ThemePreferences actual constructor() {

    private val prefs: SharedPreferences by lazy {
        FirebaseApp.getInstance().applicationContext
            .getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    }

    actual fun isDarkMode(): Boolean = prefs.getBoolean("is_dark_mode", false)

    actual fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("is_dark_mode", enabled).apply()
    }
}
