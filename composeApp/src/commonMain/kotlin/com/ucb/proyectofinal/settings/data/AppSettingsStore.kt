package com.ucb.proyectofinal.settings.data.datasource

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global singleton that holds observable app-wide settings (theme, language).
 * Both App.kt and SettingsViewModel read/write this object so that changes
 * made in the settings screen are immediately reflected in the root composable.
 * The dark mode preference is persisted via ThemePreferences so it survives app restarts.
 */
object AppSettingsStore {
    private val themePreferences = ThemePreferences()

    private val _isDarkMode = MutableStateFlow(themePreferences.isDarkMode())
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow("es")
    val language: StateFlow<String> = _language.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        themePreferences.setDarkMode(enabled)
        _isDarkMode.value = enabled
    }
    fun setLanguage(code: String) { _language.value = code }
}
