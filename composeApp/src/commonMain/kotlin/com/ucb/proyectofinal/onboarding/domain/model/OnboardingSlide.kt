package com.ucb.proyectofinal.onboarding.domain.model

/**
 * Represents a single onboarding slide already resolved to the device language.
 */
data class OnboardingSlide(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)
