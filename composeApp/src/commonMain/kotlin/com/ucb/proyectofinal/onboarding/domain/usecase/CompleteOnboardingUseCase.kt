package com.ucb.proyectofinal.onboarding.domain.usecase

import com.ucb.proyectofinal.onboarding.domain.repository.OnboardingRepository

class CompleteOnboardingUseCase(
    private val repository: OnboardingRepository
) {
    operator fun invoke() {
        repository.completeOnboarding()
    }
}
