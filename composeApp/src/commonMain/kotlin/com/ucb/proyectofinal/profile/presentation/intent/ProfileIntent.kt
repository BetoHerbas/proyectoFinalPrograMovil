package com.ucb.proyectofinal.profile.presentation.intent

sealed class ProfileIntent {
    data object LoadProfile : ProfileIntent()
    data object Logout : ProfileIntent()
}
