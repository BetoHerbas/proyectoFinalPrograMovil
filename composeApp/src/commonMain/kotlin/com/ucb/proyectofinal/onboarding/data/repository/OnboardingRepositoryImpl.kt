package com.ucb.proyectofinal.onboarding.data.repository

import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository
import com.ucb.proyectofinal.onboarding.data.datasource.getDeviceLanguage
import com.ucb.proyectofinal.onboarding.data.datasource.OnboardingPreferences
import com.ucb.proyectofinal.onboarding.data.dto.OnboardingConfigResponse
import com.ucb.proyectofinal.onboarding.domain.model.OnboardingSlide
import com.ucb.proyectofinal.onboarding.domain.repository.OnboardingRepository
import kotlinx.serialization.json.Json

class OnboardingRepositoryImpl(
    private val remoteConfigRepository: RemoteConfigRepository,
    private val onboardingPreferences: OnboardingPreferences
) : OnboardingRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getOnboardingSlides(): List<OnboardingSlide> {
        val configJson = remoteConfigRepository.fetchOnboardingConfig()
        val deviceLang = getDeviceLanguage()
        val config = json.decodeFromString<OnboardingConfigResponse>(configJson)
        return config.slides.map { slideConfig ->
            OnboardingSlide(
                id = slideConfig.id,
                title = slideConfig.title[deviceLang] ?: slideConfig.title["en"] ?: "",
                description = slideConfig.description[deviceLang] ?: slideConfig.description["en"] ?: "",
                imageUrl = slideConfig.imageUrl[deviceLang] ?: slideConfig.imageUrl["en"] ?: ""
            )
        }
    }

    override fun completeOnboarding() {
        onboardingPreferences.setOnboardingCompleted(true)
    }
}
