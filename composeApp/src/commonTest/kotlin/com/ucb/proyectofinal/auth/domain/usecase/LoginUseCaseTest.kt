package com.ucb.proyectofinal.auth.domain.usecase

import com.ucb.proyectofinal.auth.domain.model.vo.Email
import com.ucb.proyectofinal.auth.domain.model.vo.Password
import com.ucb.proyectofinal.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginUseCaseTest {

    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var useCase: LoginUseCase

    @BeforeTest
    fun setUp() {
        fakeRepo = FakeAuthRepository()
        useCase = LoginUseCase(fakeRepo)
    }

    @Test
    fun `login with valid credentials returns success`() = runTest {
        val email = Email.of("user@test.com").getOrThrow()
        val password = Password.of("password123").getOrThrow()

        val result = useCase(email, password)

        assertTrue(result.isSuccess)
        assertEquals("user@test.com", result.getOrThrow().email.value)
    }

    @Test
    fun `login when repository fails returns failure`() = runTest {
        fakeRepo.shouldFail = true
        val email = Email.of("user@test.com").getOrThrow()
        val password = Password.of("password123").getOrThrow()

        val result = useCase(email, password)

        assertTrue(result.isFailure)
    }

    @Test
    fun `login failure message matches repository message`() = runTest {
        fakeRepo.shouldFail = true
        fakeRepo.failureMessage = "Invalid credentials"

        val result = useCase(
            Email.of("user@test.com").getOrThrow(),
            Password.of("password123").getOrThrow()
        )

        assertEquals("Invalid credentials", result.exceptionOrNull()?.message)
    }

    @Test
    fun `login sets current user on success`() = runTest {
        useCase(Email.of("a@b.com").getOrThrow(), Password.of("pass12").getOrThrow())
        assertEquals("a@b.com", fakeRepo.getCurrentUser()?.email?.value)
    }
}
