package com.ucb.proyectofinal.settings.data.repository

import com.ucb.proyectofinal.settings.data.datasource.AppSettingsStore
import com.ucb.proyectofinal.settings.data.datasource.applyLocale
import com.ucb.proyectofinal.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsRepositoryImpl : SettingsRepository {

    override fun isDarkMode(): StateFlow<Boolean> = AppSettingsStore.isDarkMode

    override fun getLanguage(): StateFlow<String> = AppSettingsStore.language

    override fun setDarkMode(enabled: Boolean) {
        AppSettingsStore.setDarkMode(enabled)
    }

    override fun setLanguage(code: String) {
        AppSettingsStore.setLanguage(code)
    }

    override fun applyLanguage(code: String) {
        applyLocale(code)
    }
}
