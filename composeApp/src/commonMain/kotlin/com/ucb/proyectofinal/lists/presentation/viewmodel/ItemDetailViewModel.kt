package com.ucb.proyectofinal.lists.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.lists.domain.model.*
import com.ucb.proyectofinal.lists.domain.model.vo.ItemId
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.presentation.intent.ItemDetailIntent
import com.ucb.proyectofinal.lists.presentation.state.ItemDetailUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.ucb.proyectofinal.lists.domain.repository.ContentListRepository

class ItemDetailViewModel(
    private val repository: ContentListRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ItemDetailUiState())
    val state: StateFlow<ItemDetailUiState> = _state.asStateFlow()

    fun onIntent(intent: ItemDetailIntent) {
        when (intent) {
            is ItemDetailIntent.LoadDetail -> loadItemDetail(intent.itemId, intent.itemType)
            else -> {}
        }
    }

    private fun loadItemDetail(itemId: String, itemType: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val type = runCatching { ContentType.valueOf(itemType) }.getOrDefault(ContentType.MOVIE)
            val result = repository.getItemDetails(type, itemId)
            
            result.onSuccess { detail ->
                _state.update { it.copy(isLoading = false, item = detail) }
            }.onFailure {
                // handle error state if needed, for now just stop loading
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
