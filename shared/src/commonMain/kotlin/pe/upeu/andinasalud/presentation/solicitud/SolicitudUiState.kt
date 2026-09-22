package pe.upeu.andinasalud.presentation.solicitud

import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.usecase.SolicitudErrores

data class SolicitudUiState(
    val isLoadingCatalogos: Boolean = true,
    val isGuardando: Boolean = false,
    val especialidadesDisponibles: List<String> = emptyList(),
    val sedesDisponibles: List<String> = emptyList(),
    val medicosTotales: List<Medico> = emptyList(),
    val medicosFiltrados: List<Medico> = emptyList(),
    // Campos del formulario
    val especialidadSeleccionada: String = "",
    val sedeSeleccionada: String = "",
    val medicoSeleccionado: String = "",
    val fecha: String = "2026-09-28",
    val hora: String = "10:00",
    val motivo: String = "",
    // Errores y estado de guardado
    val errores: SolicitudErrores = SolicitudErrores(),
    val guardadoExitoso: Boolean = false,
    val errorGeneral: String? = null
)