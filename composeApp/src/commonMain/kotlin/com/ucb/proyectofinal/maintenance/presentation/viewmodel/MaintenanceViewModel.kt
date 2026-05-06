package com.ucb.proyectofinal.maintenance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository
import com.ucb.proyectofinal.maintenance.presentation.state.MaintenanceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

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

