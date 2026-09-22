package pe.upeu.andinasalud.presentation.citas

import pe.upeu.andinasalud.domain.model.Cita

enum class FiltroEstado(val etiqueta: String) {
    TODAS("Todas"),
    PROGRAMADAS("Programadas"),
    ATENDIDAS("Atendidas"),
    CANCELADAS("Canceladas")
}

data class CitasUiState(
    val isLoading: Boolean = true,
    val nombrePaciente: String = "",
    val citasOriginales: List<Cita> = emptyList(),
    val citasFiltradas: List<Cita> = emptyList(),
    val busqueda: String = "",
    val filtroEstado: FiltroEstado = FiltroEstado.TODAS,
    val error: String? = null
)