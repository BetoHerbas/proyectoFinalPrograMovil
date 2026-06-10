package com.ucb.proyectofinal.profile.presentation.state

sealed class ProfileIntent {
    data object LoadProfile : ProfileIntent()
    data object Logout : ProfileIntent()
}
