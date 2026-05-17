package com.ucb.proyectofinal.lists.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.lists.domain.model.vo.ListId
import com.ucb.proyectofinal.lists.domain.model.vo.ListName
import com.ucb.proyectofinal.lists.domain.usecase.UpdateListUseCase
import com.ucb.proyectofinal.lists.presentation.effect.EditListEffect
import com.ucb.proyectofinal.lists.presentation.intent.EditListIntent
import com.ucb.proyectofinal.lists.presentation.state.EditListUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditListViewModel(
    private val updateListUseCase: UpdateListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditListUiState())
    val state: StateFlow<EditListUiState> = _state.asStateFlow()

    private val _effects = Channel<EditListEffect>(Channel.BUFFERED)
    val effects: Flow<EditListEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: EditListIntent) {
        when (intent) {
            is EditListIntent.LoadList -> {
                _state.update {
                    it.copy(
                        listId = intent.listId,
                        name = intent.name,
                        description = intent.description,
                        coverImageUrl = intent.coverImageUrl,
                        isPrivate = !intent.isPublic,
                        listType = intent.listType
                    )
                }
            }
            is EditListIntent.UpdateName -> _state.update { it.copy(name = intent.name) }
            is EditListIntent.UpdateDescription -> {
                if (intent.description.length <= 150) {
                    _state.update { it.copy(description = intent.description) }
                }
            }
            is EditListIntent.TogglePrivate -> _state.update { it.copy(isPrivate = intent.isPrivate) }
            is EditListIntent.SaveChanges -> saveChanges()
        }
    }

    private fun saveChanges() {
        val current = _state.value
        val nameResult = ListName.of(current.name)
        if (nameResult.isFailure) {
            viewModelScope.launch {
                _effects.send(
                    EditListEffect.ShowError(
                        "Nombre inválido: ${nameResult.exceptionOrNull()?.message}"
                    )
                )
            }
            return
        }

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            updateListUseCase(
                listId = ListId(current.listId),
                name = nameResult.getOrThrow(),
                description = current.description,
                coverImageUrl = current.coverImageUrl,
                isPublic = !current.isPrivate
            )
                .onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    _effects.send(EditListEffect.ShowSuccess("Lista actualizada"))
                    _effects.send(EditListEffect.NavigateBack)
                }
                .onFailure { e ->
                    _state.update { it.copy(isSaving = false) }
                    _effects.send(EditListEffect.ShowError(e.message ?: "Error al actualizar"))
                }
        }
    }
}
