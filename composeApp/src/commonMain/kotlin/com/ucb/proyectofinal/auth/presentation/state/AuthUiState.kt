package com.ucb.proyectofinal.auth.presentation.state

data class AuthUiState(
    val isLoading: Boolean = false,
    val emailInput: String = "",
    val passwordInput: String = "",
    val nameInput: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val generalError: String? = null
)
