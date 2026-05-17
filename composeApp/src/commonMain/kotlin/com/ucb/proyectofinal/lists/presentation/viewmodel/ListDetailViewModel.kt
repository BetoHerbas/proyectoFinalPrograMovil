package com.ucb.proyectofinal.lists.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.lists.domain.model.ContentItem
import com.ucb.proyectofinal.lists.domain.model.ContentType
import com.ucb.proyectofinal.lists.domain.model.vo.ItemTitle
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.Rating
import com.ucb.proyectofinal.lists.domain.usecase.AddItemToListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.DeleteItemUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetListItemsUseCase
import com.ucb.proyectofinal.lists.domain.usecase.RateItemUseCase
import com.ucb.proyectofinal.lists.domain.usecase.ToggleItemSeenUseCase
import com.ucb.proyectofinal.lists.presentation.effect.ListDetailEffect
import com.ucb.proyectofinal.lists.presentation.intent.ListDetailIntent
import com.ucb.proyectofinal.lists.presentation.state.ItemFilter
import com.ucb.proyectofinal.lists.presentation.state.ListDetailUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListDetailViewModel(
    private val getListItemsUseCase: GetListItemsUseCase,
    private val addItemToListUseCase: AddItemToListUseCase,
    private val toggleItemSeenUseCase: ToggleItemSeenUseCase,
    private val rateItemUseCase: RateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListDetailUiState())
    val state: StateFlow<ListDetailUiState> = _state.asStateFlow()

    private val _effects = Channel<ListDetailEffect>(Channel.BUFFERED)
    val effects: Flow<ListDetailEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: ListDetailIntent) {
        when (intent) {
            is ListDetailIntent.LoadDetail -> {
                _state.update {
                    it.copy(
                        listId = intent.listId,
                        listName = intent.listName,
                        description = intent.description,
                        coverImageUrl = intent.coverImageUrl,
                        isPublic = intent.isPublic,
                        listType = intent.listType
                    )
                }
                loadItems(intent.listId)
            }
            is ListDetailIntent.AddItem -> addItem(intent.title, intent.type)
            is ListDetailIntent.ToggleSeen -> toggleSeen(intent.item)
            is ListDetailIntent.RateItem -> rateItem(intent.item, intent.rating)
            is ListDetailIntent.DeleteItem -> deleteItem(intent.item)
            is ListDetailIntent.ShowAddDialog ->
                _state.update { it.copy(showAddDialog = true) }
            is ListDetailIntent.HideAddDialog ->
                _state.update { it.copy(showAddDialog = false) }
            is ListDetailIntent.ChangeFilter ->
                _state.update { it.copy(selectedFilter = intent.filter) }
        }
    }

    private fun loadItems(listId: String) {
        viewModelScope.launch {
            getListItemsUseCase(ListId(listId))
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { items -> _state.update { it.copy(isLoading = false, items = items) } }
        }
    }

    private fun addItem(title: String, type: ContentType) {
        val titleResult = ItemTitle.of(title)
        if (titleResult.isFailure) {
            viewModelScope.launch { _effects.send(ListDetailEffect.ShowError("Título inválido")) }
            return
        }
        val listId = _state.value.listId.ifEmpty { return }
        viewModelScope.launch {
            addItemToListUseCase(ListId(listId), titleResult.getOrThrow(), type)
                .onSuccess {
                    _state.update { it.copy(showAddDialog = false) }
                    _effects.send(ListDetailEffect.ShowSuccess("Ítem agregado"))
                }
                .onFailure { _effects.send(ListDetailEffect.ShowError(it.message ?: "Error al agregar")) }
        }
    }

    private fun toggleSeen(item: ContentItem) {
        viewModelScope.launch {
            toggleItemSeenUseCase(item)
                .onFailure { _effects.send(ListDetailEffect.ShowError(it.message ?: "Error")) }
        }
    }

    private fun rateItem(item: ContentItem, rating: Int) {
        val ratingResult = Rating.of(rating)
        if (ratingResult.isFailure) {
            viewModelScope.launch {
                _effects.send(ListDetailEffect.ShowError("Rating debe ser entre 1 y 5"))
            }
            return
        }
        viewModelScope.launch {
            rateItemUseCase(item, ratingResult.getOrThrow())
                .onFailure { _effects.send(ListDetailEffect.ShowError(it.message ?: "Error al calificar")) }
        }
    }

    private fun deleteItem(item: ContentItem) {
        viewModelScope.launch {
            deleteItemUseCase(item)
                .onSuccess { _effects.send(ListDetailEffect.ShowSuccess("Ítem eliminado")) }
                .onFailure { _effects.send(ListDetailEffect.ShowError(it.message ?: "Error al eliminar")) }
        }
    }
}
