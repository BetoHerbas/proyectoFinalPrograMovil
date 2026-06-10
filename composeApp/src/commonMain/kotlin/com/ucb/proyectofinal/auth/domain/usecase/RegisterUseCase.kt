package com.ucb.proyectofinal.auth.domain.usecase

import com.ucb.proyectofinal.auth.domain.model.User
import com.ucb.proyectofinal.auth.domain.vo.Email
import com.ucb.proyectofinal.auth.domain.vo.Password
import com.ucb.proyectofinal.auth.domain.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: Email, password: Password, name: String): Result<User> =
        authRepository.register(email, password, name)
}
