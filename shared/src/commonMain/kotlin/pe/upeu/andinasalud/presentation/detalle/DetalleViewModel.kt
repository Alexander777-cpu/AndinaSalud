package pe.upeu.andinasalud.presentation.detalle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase

class DetalleViewModel(
    private val repository: CitaRepository,
    private val cancelarCitaUseCase: CancelarCitaUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    private var citaActualId: Long? = null

    fun cargarCita(id: Long) {
        citaActualId = id
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val cita = repository.obtenerCitaPorId(id)
                if (cita != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        cita = cita,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No se encontró la cita médica solicitada"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar el detalle de la cita"
                )
            }
        }
    }

    fun mostrarDialogoCancelar() {
        _uiState.value = _uiState.value.copy(
            mostrarDialogoCancelar = true,
            motivoCancelacion = "",
            errorMotivo = null
        )
    }

    fun ocultarDialogoCancelar() {
        _uiState.value = _uiState.value.copy(
            mostrarDialogoCancelar = false,
            motivoCancelacion = "",
            errorMotivo = null
        )
    }

    fun onMotivoCambiado(nuevoMotivo: String) {
        _uiState.value = _uiState.value.copy(
            motivoCancelacion = nuevoMotivo,
            errorMotivo = null
        )
    }

    fun confirmarCancelacion() {
        val cita = _uiState.value.cita ?: return
        val motivo = _uiState.value.motivoCancelacion.trim()

        if (motivo.length < 10 || motivo.length > 200) {
            _uiState.value = _uiState.value.copy(
                errorMotivo = "El motivo debe tener entre 10 y 200 caracteres (RN-04)"
            )
            return
        }

        _uiState.value = _uiState.value.copy(isCancelando = true, errorMotivo = null)

        scope.launch {
            val resultado = cancelarCitaUseCase(citaId = cita.id, motivo = motivo)
            resultado.onSuccess { citaActualizada ->
                _uiState.value = _uiState.value.copy(
                    isCancelando = false,
                    mostrarDialogoCancelar = false,
                    cita = citaActualizada,
                    cancelacionExitosa = true
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isCancelando = false,
                    errorMotivo = error.message ?: "No se pudo cancelar la cita"
                )
            }
        }
    }
}