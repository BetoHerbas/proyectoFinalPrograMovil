package com.ucb.proyectofinal.settings.presentation.effect

sealed class SettingsEffect {
    data class LanguageChanged(val languageCode: String) : SettingsEffect()
}
