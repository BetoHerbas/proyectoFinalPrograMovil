package com.ucb.proyectofinal.settings.data.datasource

/**
 * Abstracción expect/actual para persistir la preferencia de modo oscuro.
 * Android usa SharedPreferences, iOS usa NSUserDefaults (stub).
 */
expect class ThemePreferences() {
    fun isDarkMode(): Boolean
    fun setDarkMode(enabled: Boolean)
}
