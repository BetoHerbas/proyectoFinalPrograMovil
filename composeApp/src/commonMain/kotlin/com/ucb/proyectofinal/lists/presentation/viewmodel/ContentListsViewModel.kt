package com.ucb.proyectofinal.lists.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.usecase.CreateContentListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.DeleteListUseCase
import com.ucb.proyectofinal.lists.domain.usecase.GetContentListsUseCase
import com.ucb.proyectofinal.lists.presentation.effect.ContentListsEffect
import com.ucb.proyectofinal.lists.presentation.intent.ContentListsIntent
import com.ucb.proyectofinal.lists.presentation.state.ContentListsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContentListsViewModel(
    private val getContentListsUseCase: GetContentListsUseCase,
    private val createContentListUseCase: CreateContentListUseCase,
    private val deleteListUseCase: DeleteListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ContentListsUiState())
    val state: StateFlow<ContentListsUiState> = _state.asStateFlow()

    private val _effects = Channel<ContentListsEffect>(Channel.BUFFERED)
    val effects: Flow<ContentListsEffect> = _effects.receiveAsFlow()

    init {
        loadLists()
    }

    fun onIntent(intent: ContentListsIntent) {
        when (intent) {
            is ContentListsIntent.LoadLists -> loadLists()
            is ContentListsIntent.CreateList -> createList(
                name = intent.name,
                type = intent.type,
                description = intent.description,
                coverImageUrl = intent.coverImageUrl,
                isPublic = intent.isPublic
            )
            is ContentListsIntent.DeleteList -> deleteList(intent.listId)
            is ContentListsIntent.NavigateToDetail -> viewModelScope.launch {
                _effects.send(
                    ContentListsEffect.NavigateToDetail(
                        listId = intent.listId,
                        listName = intent.listName,
                        listType = intent.listType,
                        description = intent.description,
                        coverImageUrl = intent.coverImageUrl,
                        isPublic = intent.isPublic
                    )
                )
            }
        }
    }

    private fun loadLists() {
        viewModelScope.launch {
            getContentListsUseCase()
                .catch { e -> _state.update { it.copy(isLoading = false, error = e.message) } }
                .collect { lists ->
                    _state.update { it.copy(isLoading = false, lists = lists, error = null) }
                }
        }
    }

    private fun createList(
        name: String,
        type: com.ucb.proyectofinal.lists.domain.model.ContentType,
        description: String,
        coverImageUrl: String?,
        isPublic: Boolean
    ) {
        val nameResult = ListName.of(name)
        if (nameResult.isFailure) {
            viewModelScope.launch {
                _effects.send(
                    ContentListsEffect.ShowError(
                        "Nombre inválido: ${nameResult.exceptionOrNull()?.message}"
                    )
                )
            }
            return
        }
        viewModelScope.launch {
            createContentListUseCase(
                name = nameResult.getOrThrow(),
                type = type,
                description = description,
                coverImageUrl = coverImageUrl,
                isPublic = isPublic
            )
                .onSuccess { _effects.send(ContentListsEffect.ShowSuccess("Lista creada exitosamente")) }
                .onFailure { _effects.send(ContentListsEffect.ShowError(it.message ?: "Error al crear lista")) }
        }
    }

    private fun deleteList(listId: String) {
        viewModelScope.launch {
            deleteListUseCase(ListId(listId))
                .onSuccess { _effects.send(ContentListsEffect.ShowSuccess("Lista eliminada")) }
                .onFailure { _effects.send(ContentListsEffect.ShowError(it.message ?: "Error al eliminar")) }
        }
    }
}
