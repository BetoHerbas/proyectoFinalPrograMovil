package com.ucb.proyectofinal.profile.presentation.effect

sealed class ProfileEffect {
    data object NavigateToLogin : ProfileEffect()
    data class ShowError(val message: String) : ProfileEffect()
}
