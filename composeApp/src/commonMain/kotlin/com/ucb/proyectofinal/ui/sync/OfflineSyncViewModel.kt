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
 *  - [todos]   → lista reactiva de todos los ítems en Room
 *  - [addTodo] → inserta un ítem con isPending=true y encola sync automático
 *
 * Al llamar [addTodo]:
 *  1. Guarda el ítem en Room con isPending=true (funciona sin internet).
 *  2. Llama a [triggerImmediateSync] que encola un OneTimeWorkRequest con
 *     restricción NetworkType.CONNECTED.
 *     → Si hay red: sincroniza de inmediato.
 *     → Si no hay red: WorkManager espera y sincroniza en cuanto se conecte.
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

    /**
     * Guarda la nota en Room con [isPending]=true y programa la sincronización
     * automática (se ejecuta cuando haya red disponible).
     */
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
            // Encola el sync: se ejecuta ahora si hay red, o cuando vuelva la red
            triggerImmediateSync()
        }
    }
}
