package com.ucb.proyectofinal.settings.presentation.state

sealed class SettingsEffect {
    data class LanguageChanged(val languageCode: String) : SettingsEffect()
}
