package com.ucb.proyectofinal.explore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.explore.presentation.state.ExploreUiState
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.usecase.AddFavoriteUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetFavoritesUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetPublicListsUseCase
import com.ucb.proyectofinal.lists.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val getPublicListsUseCase: GetPublicListsUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ExploreUiState())
    val state: StateFlow<ExploreUiState> = _state.asStateFlow()

    init {
        loadPublicLists()
        observeFavorites()
    }

    fun refresh() = loadPublicLists()

    private fun loadPublicLists() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getPublicListsUseCase()
                .catch { _state.update { it.copy(isLoading = false, error = "Error al cargar listas públicas") } }
                .collect { lists -> _state.update { it.copy(isLoading = false, lists = lists) } }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            getFavoritesUseCase()
                .catch { /* silently ignore */ }
                .collect { favorites ->
                    _state.update { it.copy(favoriteIds = favorites.map { f -> f.id.value }.toSet()) }
                }
        }
    }

    fun toggleFavorite(list: ContentList) {
        viewModelScope.launch {
            if (_state.value.favoriteIds.contains(list.id.value)) {
                removeFavoriteUseCase(list.id)
            } else {
                addFavoriteUseCase(list)
            }
        }
    }
}
