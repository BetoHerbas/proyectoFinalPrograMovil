package com.ucb.proyectofinal.auth.domain.usecase

import com.ucb.proyectofinal.auth.domain.model.User
import com.ucb.proyectofinal.auth.domain.repository.AuthRepository

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(): User? = authRepository.getCurrentUser()
}
