package com.ucb.proyectofinal.auth.data.repository

import com.ucb.proyectofinal.auth.domain.model.User
import com.ucb.proyectofinal.auth.domain.model.vo.Email
import com.ucb.proyectofinal.auth.domain.model.vo.Password
import com.ucb.proyectofinal.auth.domain.model.vo.UserId
import com.ucb.proyectofinal.auth.domain.repository.FirebaseAuthDataSource
import com.ucb.proyectofinal.auth.domain.repository.AuthRepository
import com.ucb.proyectofinal.core.data.db.UserDao
import com.ucb.proyectofinal.core.data.db.UserEntity
import kotlinx.coroutines.runBlocking

class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val userDao: UserDao
) : AuthRepository {
    private var currentUser: User? = null

    override suspend fun login(email: Email, password: Password): Result<User> =
        runCatching {
            firebaseAuthDataSource.login(email.value, password.value).toDomainUser()
        }.onSuccess { user ->
            currentUser = user
            userDao.insertUser(user.toEntity())
        }

    override suspend fun register(email: Email, password: Password, name: String): Result<User> =
        runCatching {
            firebaseAuthDataSource.register(email.value, password.value, name).toDomainUser()
        }.onSuccess { user ->
            currentUser = user
            userDao.insertUser(user.toEntity())
        }

    override suspend fun logout() {
        firebaseAuthDataSource.logout()
        currentUser = null
        userDao.clearUser()
    }

    override fun getCurrentUser(): User? {
        currentUser?.let { return it }

        firebaseAuthDataSource.getCurrentUser()?.toDomainUser()?.also {
            currentUser = it
            return it
        }

        return runCatching { runBlocking { userDao.getUser()?.toDomainUser() } }
            .getOrNull()
            ?.also { currentUser = it }
    }

    private fun com.ucb.proyectofinal.auth.domain.repository.AuthRemoteUser.toDomainUser(): User = User(
        id = UserId(uid),
        email = Email.of(email).getOrElse {
            throw IllegalStateException("Email inválido devuelto por Firebase")
        },
        name = name
    )

    private fun User.toEntity(): UserEntity = UserEntity(
        id = id.value,
        email = email.value,
        name = name
    )

    private fun UserEntity.toDomainUser(): User = User(
        id = UserId(id),
        email = Email.of(email).getOrElse {
            throw IllegalStateException("Email inválido guardado localmente")
        },
        name = name
    )
}
