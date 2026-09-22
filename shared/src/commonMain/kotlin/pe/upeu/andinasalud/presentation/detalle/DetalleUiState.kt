package pe.upeu.andinasalud.presentation.detalle

import pe.upeu.andinasalud.domain.model.Cita

data class DetalleUiState(
    val isLoading: Boolean = true,
    val cita: Cita? = null,
    val error: String? = null,
    val mostrarDialogoCancelar: Boolean = false,
    val motivoCancelacion: String = "",
    val errorMotivo: String? = null,
    val isCancelando: Boolean = false,
    val cancelacionExitosa: Boolean = false
)