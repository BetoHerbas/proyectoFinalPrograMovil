package com.ucb.proyectofinal.onboarding.presentation.state

import com.ucb.proyectofinal.onboarding.domain.model.OnboardingSlide

data class OnboardingUiState(
    val slides: List<OnboardingSlide> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class OnboardingEffect {
    data object NavigateToHome : OnboardingEffect()
}
