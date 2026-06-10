package com.ucb.proyectofinal.settings.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    fun isDarkMode(): StateFlow<Boolean>
    fun getLanguage(): StateFlow<String>
    fun setDarkMode(enabled: Boolean)
    fun setLanguage(code: String)
    fun applyLanguage(code: String)
}
