package com.ucb.proyectofinal.auth.domain.usecase

import com.ucb.proyectofinal.auth.domain.repository.AuthRepository

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke() = authRepository.logout()
}
