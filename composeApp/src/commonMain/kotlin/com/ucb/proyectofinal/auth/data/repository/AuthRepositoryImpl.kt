package com.ucb.proyectofinal.auth.data.repository

import com.ucb.proyectofinal.auth.domain.model.User
import com.ucb.proyectofinal.auth.domain.model.vo.Email
import com.ucb.proyectofinal.auth.domain.model.vo.Password
import com.ucb.proyectofinal.auth.domain.model.vo.UserId
import com.ucb.proyectofinal.auth.domain.repository.FirebaseAuthDataSource
import com.ucb.proyectofinal.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {
    private var currentUser: User? = null

    override suspend fun login(email: Email, password: Password): Result<User> =
        runCatching {
            firebaseAuthDataSource.login(email.value, password.value).toDomainUser()
        }.onSuccess { user ->
            currentUser = user
        }

    override suspend fun register(email: Email, password: Password, name: String): Result<User> =
        runCatching {
            firebaseAuthDataSource.register(email.value, password.value, name).toDomainUser()
        }.onSuccess { user ->
            currentUser = user
        }

    override suspend fun logout() {
        firebaseAuthDataSource.logout()
        currentUser = null
    }

    override fun getCurrentUser(): User? =
        currentUser ?: firebaseAuthDataSource.getCurrentUser()?.toDomainUser()?.also {
            currentUser = it
        }

    private fun com.ucb.proyectofinal.auth.domain.repository.AuthRemoteUser.toDomainUser(): User = User(
        id = UserId(uid),
        email = Email.of(email).getOrElse {
            throw IllegalStateException("Email inválido devuelto por Firebase")
        },
        name = name
    )
}
