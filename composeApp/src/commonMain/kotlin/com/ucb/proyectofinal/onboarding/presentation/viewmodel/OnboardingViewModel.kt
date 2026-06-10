package com.ucb.proyectofinal.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository
import com.ucb.proyectofinal.onboarding.data.datasource.getDeviceLanguage
import com.ucb.proyectofinal.onboarding.data.datasource.OnboardingPreferences
import com.ucb.proyectofinal.onboarding.domain.model.OnboardingConfigResponse
import com.ucb.proyectofinal.onboarding.domain.model.OnboardingSlide
import com.ucb.proyectofinal.onboarding.presentation.state.OnboardingEffect
import com.ucb.proyectofinal.onboarding.presentation.state.OnboardingUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class OnboardingViewModel(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _effects = Channel<OnboardingEffect>(Channel.BUFFERED)
    val effects: Flow<OnboardingEffect> = _effects.receiveAsFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        loadOnboardingConfig()
    }

    private fun loadOnboardingConfig() {
        viewModelScope.launch {
            try {
                val configJson = remoteConfigRepository.fetchOnboardingConfig()
                val deviceLang = getDeviceLanguage()
                val config = json.decodeFromString<OnboardingConfigResponse>(configJson)
                val slides = config.slides.map { slideConfig ->
                    OnboardingSlide(
                        id = slideConfig.id,
                        title = slideConfig.title[deviceLang]
                            ?: slideConfig.title["en"] ?: "",
                        description = slideConfig.description[deviceLang]
                            ?: slideConfig.description["en"] ?: "",
                        imageUrl = slideConfig.imageUrl[deviceLang]
                            ?: slideConfig.imageUrl["en"] ?: ""
                    )
                }
                _state.update { it.copy(slides = slides, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /** "Omitir" — navigates to Home without saving the flag. */
    fun skipOnboarding() {
        viewModelScope.launch {
            _effects.send(OnboardingEffect.NavigateToHome)
        }
    }

    /** "Iniciar" — saves the flag permanently and navigates to Home. */
    fun completeOnboarding() {
        onboardingPreferences.setOnboardingCompleted(true)
        viewModelScope.launch {
            _effects.send(OnboardingEffect.NavigateToHome)
        }
    }
}
