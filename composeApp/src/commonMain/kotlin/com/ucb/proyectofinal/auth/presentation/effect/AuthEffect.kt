package com.ucb.proyectofinal.auth.presentation.effect

sealed class AuthEffect {
    data object NavigateToHome : AuthEffect()
    data object NavigateToLogin : AuthEffect()
    data class ShowError(val message: String) : AuthEffect()
}
