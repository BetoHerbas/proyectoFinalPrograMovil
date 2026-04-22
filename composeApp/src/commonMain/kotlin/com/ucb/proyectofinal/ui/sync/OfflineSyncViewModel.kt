package com.ucb.proyectofinal.ui.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.core.data.db.TodoDao
import com.ucb.proyectofinal.core.data.db.TodoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de demo de sincronización offline.
 *
 * Expone:
 *  - [todos]       → lista reactiva de todos los ítems en Room
 *  - [addTodo]     → inserta un ítem nuevo con isPending=true
 *  - [forceSyncNow] → callback que la UI invoca para disparar sync inmediato
 */
class OfflineSyncViewModel(
    private val dao: TodoDao
) : ViewModel() {

    /** Lista reactiva de todos los ítems (pendientes y sincronizados) */
    val todos: StateFlow<List<TodoEntity>> = dao
        .getAllTodos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Agrega un ítem nuevo en Room con isPending=true */
    fun addTodo(title: String, description: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            dao.insert(
                TodoEntity(
                    title       = title.trim(),
                    description = description.trim(),
                    isPending   = true
                )
            )
        }
    }
}
