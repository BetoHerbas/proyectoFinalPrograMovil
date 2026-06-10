package com.ucb.proyectofinal.auth.data.datasource

import com.ucb.proyectofinal.auth.data.dto.AuthRemoteUser

expect class FirebaseAuthDataSource() {
    suspend fun login(email: String, password: String): AuthRemoteUser
    suspend fun register(email: String, password: String, name: String): AuthRemoteUser
    suspend fun logout()
    fun getCurrentUser(): AuthRemoteUser?
}
