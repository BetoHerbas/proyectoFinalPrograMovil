package com.ucb.proyectofinal.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.settings.data.AppSettingsStore
import com.ucb.proyectofinal.settings.platform.applyLocale
import com.ucb.proyectofinal.settings.presentation.effect.SettingsEffect
import com.ucb.proyectofinal.settings.presentation.intent.SettingsIntent
import com.ucb.proyectofinal.settings.presentation.state.SettingsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsUiState(
            isDarkMode = AppSettingsStore.isDarkMode.value,
            language = AppSettingsStore.language.value
        )
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _effects = Channel<SettingsEffect>(Channel.BUFFERED)
    val effects: Flow<SettingsEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleDarkMode -> {
                AppSettingsStore.setDarkMode(intent.isDark)
                _state.update { it.copy(isDarkMode = intent.isDark) }
            }
            is SettingsIntent.ChangeLanguage -> {
                AppSettingsStore.setLanguage(intent.languageCode)
                _state.update { it.copy(language = intent.languageCode) }
                applyLocale(intent.languageCode)
                viewModelScope.launch {
                    _effects.send(SettingsEffect.LanguageChanged(intent.languageCode))
                }
            }
        }
    }
}
