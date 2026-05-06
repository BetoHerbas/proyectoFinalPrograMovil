package com.ucb.proyectofinal.maintenance.presentation.state

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
