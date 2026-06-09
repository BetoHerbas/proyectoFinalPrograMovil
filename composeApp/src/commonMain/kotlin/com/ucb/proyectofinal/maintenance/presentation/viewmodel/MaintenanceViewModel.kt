package com.ucb.proyectofinal.maintenance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.maintenance.domain.repository.RemoteConfigRepository
import com.ucb.proyectofinal.maintenance.presentation.state.MaintenanceState
import com.ucb.proyectofinal.settings.data.AppSettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel que observa continuamente el flag "mantainence" de Firebase Remote Config.
 *
 * En el primer inicio emite Loading mientras se obtiene el valor real.
 * En inicios posteriores usa el último valor conocido (cacheado en [AppSettingsStore])
 * para mostrar la pantalla correcta inmediatamente, mientras escucha cambios en vivo.
 */
class MaintenanceViewModel(
    private val remoteConfigRepository: RemoteConfigRepository
) : ViewModel() {

    private val _state = MutableStateFlow<MaintenanceState>(MaintenanceState.Loading)
    val state: StateFlow<MaintenanceState> = _state.asStateFlow()

    private var isFirstEmission = true

    init {
        observeMaintenance()
    }

    private fun observeMaintenance() {
        viewModelScope.launch {
            val cached = AppSettingsStore.isUnderMaintenance.value
            _state.value = if (cached) MaintenanceState.UnderMaintenance
                           else MaintenanceState.Operational

            remoteConfigRepository
                .observeMaintenance()
                .catch { e ->
                    if (isFirstEmission) {
                        _state.value = MaintenanceState.Error(e.message ?: "Error desconocido")
                    }
                }
                .collect { isMaintenance ->
                    isFirstEmission = false
                    AppSettingsStore.setUnderMaintenance(isMaintenance)
                    _state.value = if (isMaintenance) MaintenanceState.UnderMaintenance
                                   else MaintenanceState.Operational
                }
        }
    }
}

