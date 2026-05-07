package com.ucb.proyectofinal.auth.domain.usecase

import com.ucb.proyectofinal.auth.domain.model.vo.Email
import com.ucb.proyectofinal.auth.domain.model.vo.Password
import com.ucb.proyectofinal.fakes.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterUseCaseTest {

    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var useCase: RegisterUseCase

    @BeforeTest
    fun setUp() {
        fakeRepo = FakeAuthRepository()
        useCase = RegisterUseCase(fakeRepo)
    }

    @Test
    fun `register with valid data returns success`() = runTest {
        val result = useCase(
            Email.of("newuser@test.com").getOrThrow(),
            Password.of("password1").getOrThrow(),
            "Juan"
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `register preserves name correctly`() = runTest {
        val result = useCase(
            Email.of("newuser@test.com").getOrThrow(),
            Password.of("password1").getOrThrow(),
            "Ana Lopez"
        )

        assertEquals("Ana Lopez", result.getOrThrow().name)
    }

    @Test
    fun `register when repository fails returns failure`() = runTest {
        fakeRepo.shouldFail = true
        val result = useCase(
            Email.of("user@test.com").getOrThrow(),
            Password.of("password1").getOrThrow(),
            "Test"
        )

        assertTrue(result.isFailure)
    }
}
