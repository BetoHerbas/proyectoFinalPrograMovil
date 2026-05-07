package com.ucb.proyectofinal.auth.domain.repository

data class AuthRemoteUser(
    val uid: String,
    val email: String,
    val name: String
)

expect class FirebaseAuthDataSource() {
    suspend fun login(email: String, password: String): AuthRemoteUser
    suspend fun register(email: String, password: String, name: String): AuthRemoteUser
    suspend fun logout()
    fun getCurrentUser(): AuthRemoteUser?
}
