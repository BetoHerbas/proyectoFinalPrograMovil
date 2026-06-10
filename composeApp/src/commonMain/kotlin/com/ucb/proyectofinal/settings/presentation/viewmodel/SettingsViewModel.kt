package com.ucb.proyectofinal.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.settings.domain.usecase.ChangeLanguageUseCase
import com.ucb.proyectofinal.settings.domain.usecase.GetLanguageUseCase
import com.ucb.proyectofinal.settings.domain.usecase.GetThemeUseCase
import com.ucb.proyectofinal.settings.domain.usecase.SaveThemeUseCase
import com.ucb.proyectofinal.settings.presentation.state.SettingsEffect
import com.ucb.proyectofinal.settings.presentation.state.SettingsIntent
import com.ucb.proyectofinal.settings.presentation.state.SettingsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getThemeUseCase: GetThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val changeLanguageUseCase: ChangeLanguageUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsUiState(
            isDarkMode = getThemeUseCase().value,
            language = getLanguageUseCase().value
        )
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private val _effects = Channel<SettingsEffect>(Channel.BUFFERED)
    val effects: Flow<SettingsEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleDarkMode -> {
                saveThemeUseCase(intent.isDark)
                _state.update { it.copy(isDarkMode = intent.isDark) }
            }
            is SettingsIntent.ChangeLanguage -> {
                changeLanguageUseCase(intent.languageCode)
                _state.update { it.copy(language = intent.languageCode) }
                viewModelScope.launch {
                    _effects.send(SettingsEffect.LanguageChanged(intent.languageCode))
                }
            }
        }
    }
}
