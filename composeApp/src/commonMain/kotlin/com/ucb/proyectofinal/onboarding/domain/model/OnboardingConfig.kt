package com.ucb.proyectofinal.onboarding.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Maps the JSON structure from Firebase Remote Config for onboarding slides.
 */
@Serializable
data class OnboardingConfigResponse(
    @SerialName("onboarding_config") val slides: List<OnboardingSlideConfig>
)

@Serializable
data class OnboardingSlideConfig(
    val id: Int,
    val title: Map<String, String>,
    val description: Map<String, String>,
    @SerialName("image_url") val imageUrl: Map<String, String>
)
