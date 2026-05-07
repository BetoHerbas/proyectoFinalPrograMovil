package com.ucb.proyectofinal.auth.presentation.intent

sealed class AuthIntent {
    data class UpdateEmail(val email: String) : AuthIntent()
    data class UpdatePassword(val password: String) : AuthIntent()
    data class UpdateName(val name: String) : AuthIntent()
    data object Login : AuthIntent()
    data object Register : AuthIntent()
    data object Logout : AuthIntent()
    data object ClearErrors : AuthIntent()
}
