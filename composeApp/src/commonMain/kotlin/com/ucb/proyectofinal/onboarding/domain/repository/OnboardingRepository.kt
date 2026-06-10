package com.ucb.proyectofinal.onboarding.domain.repository

import com.ucb.proyectofinal.onboarding.domain.model.OnboardingSlide

interface OnboardingRepository {
    suspend fun getOnboardingSlides(): List<OnboardingSlide>
    fun completeOnboarding()
}
