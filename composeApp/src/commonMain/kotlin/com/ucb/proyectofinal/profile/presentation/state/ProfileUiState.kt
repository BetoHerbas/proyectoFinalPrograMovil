package com.ucb.proyectofinal.profile.presentation.state

import com.ucb.proyectofinal.auth.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null
)
