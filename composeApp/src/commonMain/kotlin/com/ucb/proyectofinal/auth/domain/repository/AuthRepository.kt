package com.ucb.proyectofinal.auth.domain.repository

import com.ucb.proyectofinal.auth.domain.model.User
import com.ucb.proyectofinal.auth.domain.model.vo.Email
import com.ucb.proyectofinal.auth.domain.model.vo.Password

interface AuthRepository {
    suspend fun login(email: Email, password: Password): Result<User>
    suspend fun register(email: Email, password: Password, name: String): Result<User>
    suspend fun logout()
    fun getCurrentUser(): User?
}
