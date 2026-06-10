package com.ucb.proyectofinal.auth.presentation.viewmodel

import app.cash.turbine.test
import com.ucb.proyectofinal.auth.domain.repository.AuthRepository
import com.ucb.proyectofinal.auth.presentation.intent.AuthIntent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val validateEmailUseCase: com.ucb.proyectofinal.auth.domain.usecase.ValidateEmailUseCase = com.ucb.proyectofinal.auth.domain.usecase.ValidateEmailUseCase()
    private val validatePasswordUseCase: com.ucb.proyectofinal.auth.domain.usecase.ValidatePasswordUseCase = com.ucb.proyectofinal.auth.domain.usecase.ValidatePasswordUseCase()
    private val validateUsernameUseCase: com.ucb.proyectofinal.auth.domain.usecase.ValidateUsernameUseCase = com.ucb.proyectofinal.auth.domain.usecase.ValidateUsernameUseCase()

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = AuthViewModel(
            authRepository,
            validateEmailUseCase,
            validatePasswordUseCase,
            validateUsernameUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `SignIn intent with valid credentials successfully authenticates`() = runTest {
        val email = "test@example.com"
        val password = "Password123!"
        
        coEvery { authRepository.signInWithEmail(email, password) } returns Result.success(Unit)

        viewModel.state.test {
            awaitItem() // initial state
            
            viewModel.onIntent(AuthIntent.EmailChanged(email))
            awaitItem()
            
            viewModel.onIntent(AuthIntent.PasswordChanged(password))
            awaitItem()
            
            viewModel.onIntent(AuthIntent.SignIn)
            
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertTrue(finalState.isAuthenticated)
            assertEquals(null, finalState.error)
        }
    }
    
    @Test
    fun `SignIn intent with invalid email updates error state`() = runTest {
        val email = "invalid"
        val password = "Password123!"

        viewModel.state.test {
            awaitItem() // initial state
            
            viewModel.onIntent(AuthIntent.EmailChanged(email))
            awaitItem()
            
            viewModel.onIntent(AuthIntent.PasswordChanged(password))
            awaitItem()
            
            viewModel.onIntent(AuthIntent.SignIn)
            
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertFalse(finalState.isAuthenticated)
            assertTrue(finalState.emailError != null) // email is invalid
        }
    }
}
