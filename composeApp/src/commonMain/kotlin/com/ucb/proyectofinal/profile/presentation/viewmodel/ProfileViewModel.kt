package com.ucb.proyectofinal.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.auth.domain.usecase.GetCurrentUserUseCase
import com.ucb.proyectofinal.auth.domain.usecase.LogoutUseCase
import com.ucb.proyectofinal.profile.presentation.effect.ProfileEffect
import com.ucb.proyectofinal.profile.presentation.intent.ProfileIntent
import com.ucb.proyectofinal.profile.presentation.state.ProfileUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _effects = Channel<ProfileEffect>(Channel.BUFFERED)
    val effects: Flow<ProfileEffect> = _effects.receiveAsFlow()

    init {
        onIntent(ProfileIntent.LoadProfile)
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> {
                val user = getCurrentUserUseCase()
                _state.update { it.copy(isLoading = false, user = user) }
            }
            is ProfileIntent.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _effects.send(ProfileEffect.NavigateToLogin)
        }
    }
}
