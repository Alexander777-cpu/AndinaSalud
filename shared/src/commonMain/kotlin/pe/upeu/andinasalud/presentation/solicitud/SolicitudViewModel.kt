package pe.upeu.andinasalud.presentation.solicitud

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitudErrores
import pe.upeu.andinasalud.domain.usecase.SolicitudInvalidaException

class SolicitudViewModel(
    private val repository: CitaRepository,
    private val solicitarCitaUseCase: SolicitarCitaUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()

    init {
        cargarCatalogos()
    }

    fun cargarCatalogos() {
        _uiState.value = _uiState.value.copy(isLoadingCatalogos = true)
        scope.launch {
            try {
                val esp = repository.obtenerEspecialidades()
                val sedes = repository.obtenerSedes()
                val medicos = repository.obtenerMedicos()

                _uiState.value = _uiState.value.copy(
                    isLoadingCatalogos = false,
                    especialidadesDisponibles = esp,
                    sedesDisponibles = sedes,
                    medicosTotales = medicos,
                    especialidadSeleccionada = esp.firstOrNull().orEmpty(),
                    sedeSeleccionada = sedes.firstOrNull().orEmpty()
                )
                actualizarMedicosFiltrados()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingCatalogos = false,
                    errorGeneral = e.message ?: "Error al cargar catálogos"
                )
            }
        }
    }

    fun onEspecialidadCambiada(nueva: String) {
        _uiState.value = _uiState.value.copy(especialidadSeleccionada = nueva)
        actualizarMedicosFiltrados()
    }

    fun onSedeCambiada(nueva: String) {
        _uiState.value = _uiState.value.copy(sedeSeleccionada = nueva)
        actualizarMedicosFiltrados()
    }

    fun onMedicoCambiado(nuevo: String) {
        _uiState.value = _uiState.value.copy(medicoSeleccionado = nuevo)
    }

    fun onFechaCambiada(nueva: String) {
        _uiState.value = _uiState.value.copy(fecha = nueva)
    }

    fun onHoraCambiada(nueva: String) {
        _uiState.value = _uiState.value.copy(hora = nueva)
    }

    fun onMotivoCambiado(nuevo: String) {
        _uiState.value = _uiState.value.copy(motivo = nuevo)
    }

    private fun actualizarMedicosFiltrados() {
        val esp = _uiState.value.especialidadSeleccionada
        val sede = _uiState.value.sedeSeleccionada
        val filtrados = _uiState.value.medicosTotales.filter { medico ->
            medico.especialidad == esp && medico.sedes.contains(sede)
        }
        _uiState.value = _uiState.value.copy(
            medicosFiltrados = filtrados,
            medicoSeleccionado = filtrados.firstOrNull()?.nombre.orEmpty()
        )
    }

    fun guardarCita() {
        val state = _uiState.value
        _uiState.value = state.copy(isGuardando = true, errorGeneral = null)

        scope.launch {
            val resultado = solicitarCitaUseCase(
                especialidad = state.especialidadSeleccionada,
                sede = state.sedeSeleccionada,
                medico = state.medicoSeleccionado,
                fecha = state.fecha,
                hora = state.hora,
                motivo = state.motivo
            )

            resultado.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isGuardando = false,
                    guardadoExitoso = true,
                    errores = SolicitudErrores()
                )
            }.onFailure { error ->
                if (error is SolicitudInvalidaException) {
                    _uiState.value = _uiState.value.copy(
                        isGuardando = false,
                        errores = error.errores
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isGuardando = false,
                        errorGeneral = error.message ?: "Error al solicitar cita"
                    )
                }
            }
        }
    }

    fun reiniciarEstadoGuardado() {
        _uiState.value = _uiState.value.copy(
            guardadoExitoso = false,
            motivo = "",
            errores = SolicitudErrores()
        )
    }
}