package com.ucb.proyectofinal.lists.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.lists.domain.model.CatalogSearchItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.usecase.AddItemToListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.SearchCatalogUseCase
import com.ucb.proyectofinal.lists.presentation.effect.AddItemEffect
import com.ucb.proyectofinal.lists.presentation.intent.AddItemIntent
import com.ucb.proyectofinal.lists.presentation.state.AddItemUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddItemViewModel(
    private val searchCatalogUseCase: SearchCatalogUseCase,
    private val addItemToListUseCase: AddItemToListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddItemUiState())
    val state: StateFlow<AddItemUiState> = _state.asStateFlow()

    private val _effects = Channel<AddItemEffect>(Channel.BUFFERED)
    val effects: Flow<AddItemEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: AddItemIntent) {
        when (intent) {
            is AddItemIntent.Init -> init(intent.listId, intent.listName, intent.listType)
            is AddItemIntent.QueryChanged -> _state.update { it.copy(query = intent.query) }
            is AddItemIntent.Search -> search()
            is AddItemIntent.AddToList -> addToList(intent.item)
        }
    }

    private fun init(listId: String, listName: String, listType: ContentType) {
        if (_state.value.listId == listId) return
        _state.update {
            it.copy(
                listId = listId,
                listName = listName,
                listType = listType,
                query = ""
            )
        }
        search()
    }

    private fun search() {
        val current = _state.value
        val query = current.query.trim()

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            searchCatalogUseCase(current.listType, query)
                .onSuccess { items ->
                    _state.update { it.copy(isLoading = false, items = items) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _effects.send(AddItemEffect.ShowError(error.message ?: "No se pudo buscar"))
                }
        }
    }

    private fun addToList(item: CatalogSearchItem) {
        val current = _state.value
        val title = ItemTitle.of(item.title)
        if (title.isFailure) {
            viewModelScope.launch {
                _effects.send(AddItemEffect.ShowError("Título inválido"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(addingItemId = item.sourceId) }
            addItemToListUseCase(
                listId = ListId(current.listId),
                title = title.getOrThrow(),
                type = current.listType
            )
                .onSuccess {
                    _state.update { it.copy(addingItemId = null) }
                    _effects.send(AddItemEffect.ShowSuccess("${item.title} agregado"))
                }
                .onFailure { error ->
                    _state.update { it.copy(addingItemId = null) }
                    _effects.send(AddItemEffect.ShowError(error.message ?: "No se pudo agregar"))
                }
        }
    }

}
