package com.ucb.proyectofinal.onboarding.domain.usecase

import com.ucb.proyectofinal.onboarding.domain.model.OnboardingSlide
import com.ucb.proyectofinal.onboarding.domain.repository.OnboardingRepository

class GetOnboardingSlidesUseCase(
    private val repository: OnboardingRepository
) {
    suspend operator fun invoke(): List<OnboardingSlide> {
        return repository.getOnboardingSlides()
    }
}
