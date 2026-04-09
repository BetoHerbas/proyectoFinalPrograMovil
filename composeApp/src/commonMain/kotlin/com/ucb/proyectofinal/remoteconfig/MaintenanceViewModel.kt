package com.ucb.proyectofinal.remoteconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Estado de la verificación de mantenimiento.
 */
sealed class MaintenanceState {
    /** Todavía cargando / verificando Remote Config */
    data object Loading : MaintenanceState()
    /** App en mantenimiento */
    data object UnderMaintenance : MaintenanceState()
    /** App operativa con normalidad */
    data object Operational : MaintenanceState()
    /** Error al consultar Remote Config (se permite continuar) */
    data class Error(val message: String) : MaintenanceState()
}

/**
 * ViewModel que observa continuamente el flag "mantainence" de Firebase Remote Config.
 *
 * Mientras la app está abierta, [observeMaintenance()] escucha cambios en tiempo real:
 * si el valor cambia en Firebase Console, la pantalla reacciona sin reiniciar la app.
 */
class MaintenanceViewModel(
    private val remoteConfigRepository: RemoteConfigRepository
) : ViewModel() {

    private val _state = MutableStateFlow<MaintenanceState>(MaintenanceState.Loading)
    val state: StateFlow<MaintenanceState> = _state.asStateFlow()

    init {
        observeMaintenance()
    }

    private fun observeMaintenance() {
        viewModelScope.launch {
            remoteConfigRepository
                .observeMaintenance()
                .catch { e ->
                    // Si el Flow falla, dejamos pasar (fail-open)
                    _state.value = MaintenanceState.Error(e.message ?: "Error desconocido")
                }
                .collect { isMaintenance ->
                    _state.value = if (isMaintenance) {
                        MaintenanceState.UnderMaintenance
                    } else {
                        MaintenanceState.Operational
                    }
                }
        }
    }
}

