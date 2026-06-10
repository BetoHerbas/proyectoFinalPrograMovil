package com.ucb.proyectofinal.settings.presentation.state

sealed class SettingsIntent {
    data class ToggleDarkMode(val isDark: Boolean) : SettingsIntent()
    data class ChangeLanguage(val languageCode: String) : SettingsIntent()
}
