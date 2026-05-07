package com.ucb.proyectofinal.auth.presentation

import app.cash.turbine.test
import com.ucb.proyectofinal.auth.domain.usecase.GetCurrentUserUseCase
import com.ucb.proyectofinal.auth.domain.usecase.LoginUseCase
import com.ucb.proyectofinal.auth.domain.usecase.LogoutUseCase
import com.ucb.proyectofinal.auth.domain.usecase.RegisterUseCase
import com.ucb.proyectofinal.auth.presentation.effect.AuthEffect
import com.ucb.proyectofinal.auth.presentation.intent.AuthIntent
import com.ucb.proyectofinal.auth.presentation.viewmodel.AuthViewModel
import com.ucb.proyectofinal.fakes.FakeAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var fakeRepo: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeRepo = FakeAuthRepository()
        viewModel = AuthViewModel(
            loginUseCase = LoginUseCase(fakeRepo),
            registerUseCase = RegisterUseCase(fakeRepo),
            logoutUseCase = LogoutUseCase(fakeRepo),
            getCurrentUserUseCase = GetCurrentUserUseCase(fakeRepo)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has default values`() {
        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("", state.emailInput)
        assertEquals("", state.passwordInput)
        assertNull(state.emailError)
        assertNull(state.passwordError)
        assertNull(state.generalError)
    }

    @Test
    fun `UpdateEmail intent updates emailInput`() {
        viewModel.onIntent(AuthIntent.UpdateEmail("test@test.com"))
        assertEquals("test@test.com", viewModel.state.value.emailInput)
    }

    @Test
    fun `UpdatePassword intent updates passwordInput`() {
        viewModel.onIntent(AuthIntent.UpdatePassword("mySecret"))
        assertEquals("mySecret", viewModel.state.value.passwordInput)
    }

    @Test
    fun `Login with valid credentials emits NavigateToHome effect`() = runTest {
        viewModel.onIntent(AuthIntent.UpdateEmail("user@test.com"))
        viewModel.onIntent(AuthIntent.UpdatePassword("password1"))

        viewModel.effects.test {
            viewModel.onIntent(AuthIntent.Login)
            val effect = awaitItem()
            assertTrue(effect is AuthEffect.NavigateToHome)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Login with invalid email sets emailError in state`() = runTest {
        viewModel.onIntent(AuthIntent.UpdateEmail("bademail"))
        viewModel.onIntent(AuthIntent.UpdatePassword("password1"))
        viewModel.onIntent(AuthIntent.Login)

        assertNotNull(viewModel.state.value.emailError)
    }

    @Test
    fun `Login with short password sets passwordError in state`() = runTest {
        viewModel.onIntent(AuthIntent.UpdateEmail("user@test.com"))
        viewModel.onIntent(AuthIntent.UpdatePassword("abc"))
        viewModel.onIntent(AuthIntent.Login)

        assertNotNull(viewModel.state.value.passwordError)
    }

    @Test
    fun `Login with repository failure emits ShowError effect`() = runTest {
        fakeRepo.shouldFail = true
        viewModel.onIntent(AuthIntent.UpdateEmail("user@test.com"))
        viewModel.onIntent(AuthIntent.UpdatePassword("password1"))

        viewModel.effects.test {
            viewModel.onIntent(AuthIntent.Login)
            val effect = awaitItem()
            assertTrue(effect is AuthEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
