package com.ucb.proyectofinal.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.onboarding.domain.usecase.CompleteOnboardingUseCase
import com.ucb.proyectofinal.onboarding.domain.usecase.GetOnboardingSlidesUseCase
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

class OnboardingViewModel(
    private val getOnboardingSlidesUseCase: GetOnboardingSlidesUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _effects = Channel<OnboardingEffect>(Channel.BUFFERED)
    val effects: Flow<OnboardingEffect> = _effects.receiveAsFlow()

    init {
        loadOnboardingConfig()
    }

    private fun loadOnboardingConfig() {
        viewModelScope.launch {
            try {
                val slides = getOnboardingSlidesUseCase()
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
        completeOnboardingUseCase()
        viewModelScope.launch {
            _effects.send(OnboardingEffect.NavigateToHome)
        }
    }
}
