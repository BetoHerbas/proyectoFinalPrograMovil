package com.ucb.proyectofinal.settings.domain.usecase

import com.ucb.proyectofinal.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class GetThemeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): StateFlow<Boolean> = repository.isDarkMode()
}

class SaveThemeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(isDark: Boolean) = repository.setDarkMode(isDark)
}

class GetLanguageUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): StateFlow<String> = repository.getLanguage()
}

class ChangeLanguageUseCase(private val repository: SettingsRepository) {
    operator fun invoke(code: String) {
        repository.setLanguage(code)
        repository.applyLanguage(code)
    }
}
