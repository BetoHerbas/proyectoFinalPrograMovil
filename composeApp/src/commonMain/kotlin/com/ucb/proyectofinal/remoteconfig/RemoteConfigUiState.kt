package com.ucb.proyectofinal.remoteconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinal.core.data.RemoteConfigCacheRepository
import com.ucb.proyectofinal.core.data.db.RemoteConfigEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de configuración remota cacheada.
 */
sealed class RemoteConfigUiState {
    /** Cargando datos de Room. */
    data object Loading : RemoteConfigUiState()

    /** Se encontraron configuraciones cacheadas. */
    data class Loaded(val configs: List<RemoteConfigEntity>) : RemoteConfigUiState()

    /** No hay configuración cacheada (primera vez sin internet). */
    data object Empty : RemoteConfigUiState()

    /** Error al leer de Room. */
    data class Error(val message: String) : RemoteConfigUiState()
}

/**
 * ViewModel que observa la configuración remota cacheada en Room.
 *
 * Siempre lee de Room (offline-first):
 * - Si el Worker ya descargó datos → muestra los valores.
 * - Si no hay datos aún → muestra estado vacío con mensaje.
 * - Los cambios en Room se reflejan automáticamente en la UI
 *   gracias al Flow reactivo.
 */
class RemoteConfigViewModel(
    private val cacheRepository: RemoteConfigCacheRepository
) : ViewModel() {

    private val _state = MutableStateFlow<RemoteConfigUiState>(RemoteConfigUiState.Loading)
    val state: StateFlow<RemoteConfigUiState> = _state.asStateFlow()

    init {
        observeCachedConfig()
    }

    private fun observeCachedConfig() {
        viewModelScope.launch {
            cacheRepository
                .observeCachedConfig()
                .catch { e ->
                    _state.value = RemoteConfigUiState.Error(
                        e.message ?: "Error al leer la configuración local"
                    )
                }
                .collect { configs ->
                    _state.value = if (configs.isEmpty()) {
                        RemoteConfigUiState.Empty
                    } else {
                        RemoteConfigUiState.Loaded(configs)
                    }
                }
        }
    }
}
