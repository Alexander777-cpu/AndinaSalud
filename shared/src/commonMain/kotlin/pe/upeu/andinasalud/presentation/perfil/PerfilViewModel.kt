package pe.upeu.andinasalud.presentation.perfil

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.andinasalud.domain.repository.CitaRepository

class PerfilViewModel(
    private val repository: CitaRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        cargarPerfil()
    }

    fun cargarPerfil() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        scope.launch {
            try {
                val paciente = repository.obtenerPaciente()
                _uiState.value = PerfilUiState(
                    isLoading = false,
                    paciente = paciente,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = PerfilUiState(
                    isLoading = false,
                    paciente = null,
                    error = e.message ?: "Error al cargar la información del paciente"
                )
            }
        }
    }
}