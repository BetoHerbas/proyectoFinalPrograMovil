package com.ucb.proyectofinal.auth.domain.repository

actual class FirebaseAuthDataSource actual constructor() {

    private var currentUser: AuthRemoteUser? = null

    actual suspend fun login(email: String, password: String): AuthRemoteUser {
        val user = currentUser ?: AuthRemoteUser(
            uid = "ios_${email.hashCode()}",
            email = email,
            name = email.substringBefore("@")
        )
        currentUser = user
        return user
    }

    actual suspend fun register(email: String, password: String, name: String): AuthRemoteUser {
        val user = AuthRemoteUser(
            uid = "ios_${email.hashCode()}",
            email = email,
            name = name
        )
        currentUser = user
        return user
    }

    actual suspend fun logout() {
        currentUser = null
    }

    actual fun getCurrentUser(): AuthRemoteUser? = currentUser
}
