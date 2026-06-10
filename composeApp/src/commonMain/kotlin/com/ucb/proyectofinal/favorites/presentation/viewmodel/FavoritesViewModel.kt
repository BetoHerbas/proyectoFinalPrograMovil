package com.ucb.proyectofinal.favorites.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.favorites.presentation.state.FavoritesUiState
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.usecase.GetFavoritesUseCase
import com.ucb.proyectofinal.lists.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getFavoritesUseCase()
                .catch { _state.update { it.copy(isLoading = false, error = "Error al cargar favoritos") } }
                .collect { lists -> _state.update { it.copy(isLoading = false, favorites = lists) } }
        }
    }

    fun removeFavorite(listId: ListId) {
        viewModelScope.launch {
            removeFavoriteUseCase(listId)
        }
    }
}
