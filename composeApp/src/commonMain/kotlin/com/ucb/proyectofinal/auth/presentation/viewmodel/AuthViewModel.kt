package com.ucb.proyectofinal.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.auth.domain.model.vo.Email
import com.ucb.proyectofinal.auth.domain.model.vo.Password
import com.ucb.proyectofinal.auth.domain.usecase.GetCurrentUserUseCase
import com.ucb.proyectofinal.auth.domain.usecase.LoginUseCase
import com.ucb.proyectofinal.auth.domain.usecase.LogoutUseCase
import com.ucb.proyectofinal.auth.domain.usecase.RegisterUseCase
import com.ucb.proyectofinal.auth.presentation.effect.AuthEffect
import com.ucb.proyectofinal.auth.presentation.intent.AuthIntent
import com.ucb.proyectofinal.auth.presentation.state.AuthUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _effects = Channel<AuthEffect>(Channel.BUFFERED)
    val effects: Flow<AuthEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdateEmail ->
                _state.update { it.copy(emailInput = intent.email, emailError = null, generalError = null) }
            is AuthIntent.UpdatePassword ->
                _state.update { it.copy(passwordInput = intent.password, passwordError = null, generalError = null) }
            is AuthIntent.UpdateName ->
                _state.update { it.copy(nameInput = intent.name, nameError = null, generalError = null) }
            is AuthIntent.Login -> login()
            is AuthIntent.Register -> register()
            is AuthIntent.Logout -> logout()
            is AuthIntent.ClearErrors -> _state.update {
                it.copy(emailError = null, passwordError = null, nameError = null, generalError = null)
            }
        }
    }

    private fun login() {
        val emailResult = Email.of(_state.value.emailInput)
        val passwordResult = Password.of(_state.value.passwordInput)
        var hasError = false
        if (emailResult.isFailure) {
            _state.update { it.copy(emailError = "Email inválido") }
            hasError = true
        }
        if (passwordResult.isFailure) {
            _state.update { it.copy(passwordError = "Mínimo 6 caracteres") }
            hasError = true
        }
        if (hasError) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }
            loginUseCase(emailResult.getOrThrow(), passwordResult.getOrThrow())
                .onSuccess {
                    _state.update { current -> current.copy(generalError = null) }
                    _effects.send(AuthEffect.NavigateToHome)
                }
                .onFailure {
                    _state.update { current ->
                        current.copy(generalError = it.message ?: "Error al iniciar sesión")
                    }
                    _effects.send(AuthEffect.ShowError(it.message ?: "Error al iniciar sesión"))
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun register() {
        val emailResult = Email.of(_state.value.emailInput)
        val passwordResult = Password.of(_state.value.passwordInput)
        val name = _state.value.nameInput.trim()
        var hasError = false
        if (emailResult.isFailure) {
            _state.update { it.copy(emailError = "Email inválido") }
            hasError = true
        }
        if (passwordResult.isFailure) {
            _state.update { it.copy(passwordError = "Mínimo 6 caracteres") }
            hasError = true
        }
        if (name.isBlank()) {
            _state.update { it.copy(nameError = "Nombre requerido") }
            hasError = true
        }
        if (hasError) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }
            registerUseCase(emailResult.getOrThrow(), passwordResult.getOrThrow(), name)
                .onSuccess {
                    _state.update { current -> current.copy(generalError = null) }
                    _effects.send(AuthEffect.NavigateToHome)
                }
                .onFailure {
                    _state.update { current ->
                        current.copy(generalError = it.message ?: "Error al registrarse")
                    }
                    _effects.send(AuthEffect.ShowError(it.message ?: "Error al registrarse"))
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _effects.send(AuthEffect.NavigateToLogin)
        }
    }
}
