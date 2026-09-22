package pe.upeu.andinasalud.presentation.perfil

import pe.upeu.andinasalud.domain.model.Paciente

data class PerfilUiState(
    val isLoading: Boolean = true,
    val paciente: Paciente? = null,
    val error: String? = null
)