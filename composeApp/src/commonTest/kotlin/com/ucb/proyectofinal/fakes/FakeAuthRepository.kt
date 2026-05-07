package com.ucb.proyectofinal.fakes

import com.ucb.proyectofinal.auth.domain.model.User
import com.ucb.proyectofinal.auth.domain.model.vo.Email
import com.ucb.proyectofinal.auth.domain.model.vo.Password
import com.ucb.proyectofinal.auth.domain.model.vo.UserId
import com.ucb.proyectofinal.auth.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {
    var shouldFail = false
    var failureMessage = "Auth failed"
    private var currentUser: User? = null

    override suspend fun login(email: Email, password: Password): Result<User> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        val user = User(UserId("fake-id"), email, email.value.substringBefore("@"))
        currentUser = user
        return Result.success(user)
    }

    override suspend fun register(email: Email, password: Password, name: String): Result<User> {
        if (shouldFail) return Result.failure(Exception(failureMessage))
        val user = User(UserId("fake-id"), email, name)
        currentUser = user
        return Result.success(user)
    }

    override suspend fun logout() {
        currentUser = null
    }

    override fun getCurrentUser(): User? = currentUser
}
