package pe.upeu.andinasalud.presentation.citas

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase

class CitasViewModel(
    private val repository: CitaRepository,
    private val obtenerCitasUseCase: ObtenerCitasUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow(CitasUiState())
    val uiState: StateFlow<CitasUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val paciente = repository.obtenerPaciente()
                val citas = obtenerCitasUseCase()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    nombrePaciente = paciente.nombre,
                    citasOriginales = citas,
                    error = null
                )
                aplicarFiltros()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar las citas médicas"
                )
            }
        }
    }

    fun onBusquedaCambiada(nuevaBusqueda: String) {
        _uiState.value = _uiState.value.copy(busqueda = nuevaBusqueda)
        aplicarFiltros()
    }

    fun onFiltroEstadoCambiado(nuevoFiltro: FiltroEstado) {
        _uiState.value = _uiState.value.copy(filtroEstado = nuevoFiltro)
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val state = _uiState.value
        val busquedaNormalizada = normalizarTexto(state.busqueda)

        val filtradas = state.citasOriginales.filter { cita ->
            // Filtro por Estado (RF-05)
            val coincideEstado = when (state.filtroEstado) {
                FiltroEstado.TODAS -> true
                FiltroEstado.PROGRAMADAS -> cita.estado is EstadoCita.Programada
                FiltroEstado.ATENDIDAS -> cita.estado is EstadoCita.Atendida
                FiltroEstado.CANCELADAS -> cita.estado is EstadoCita.Cancelada
            }

            // Búsqueda insensible a mayúsculas y tildes por médico o especialidad (RF-05)
            val medicoNorm = normalizarTexto(cita.medico)
            val especialidadNorm = normalizarTexto(cita.especialidad)
            val coincideTexto = busquedaNormalizada.isBlank() ||
                    medicoNorm.contains(busquedaNormalizada) ||
                    especialidadNorm.contains(busquedaNormalizada)

            coincideEstado && coincideTexto
        }

        _uiState.value = _uiState.value.copy(citasFiltradas = filtradas)
    }

    private fun normalizarTexto(texto: String): String {
        var normalizado = texto.lowercase().trim()
        val origen = "áéíóúüÁÉÍÓÚÜ"
        val destino = "aeiouuaeiouu"
        for (i in origen.indices) {
            normalizado = normalizado.replace(origen[i], destino[i])
        }
        return normalizado
    }
}