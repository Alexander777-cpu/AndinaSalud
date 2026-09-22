package pe.upeu.andinasalud.domain.model

data class Medico(
    val id: Long = 0,
    val nombre: String,
    val especialidad: String,
    val sedes: List<String>
)