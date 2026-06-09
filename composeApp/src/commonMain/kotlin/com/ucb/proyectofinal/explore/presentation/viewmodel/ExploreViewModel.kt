package com.ucb.proyectofinal.explore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.explore.presentation.state.ExploreUiState
import com.ucb.proyectofinal.lists.domain.model.ContentList
import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val repository: ContentListRepository
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
            repository.getPublicLists()
                .catch { _state.update { it.copy(isLoading = false, error = "Error al cargar listas públicas") } }
                .collect { lists -> _state.update { it.copy(isLoading = false, lists = lists) } }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavorites()
                .catch { /* silently ignore */ }
                .collect { favorites ->
                    _state.update { it.copy(favoriteIds = favorites.map { f -> f.id.value }.toSet()) }
                }
        }
    }

    fun toggleFavorite(list: ContentList) {
        viewModelScope.launch {
            if (_state.value.favoriteIds.contains(list.id.value)) {
                repository.removeFavorite(list.id)
            } else {
                repository.addFavorite(list)
            }
        }
    }
}
