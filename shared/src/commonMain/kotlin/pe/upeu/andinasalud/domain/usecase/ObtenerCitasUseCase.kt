package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class ObtenerCitasUseCase(
    private val repository: CitaRepository
) {
    suspend operator fun invoke(
        filtroEstado: String? = null,
        busqueda: String = ""
    ): List<Cita> {
        val citas = repository.obtenerCitas()
        val busquedaNormalizada = normalizarTexto(busqueda)

        return citas
            .filter { cita ->
                // Filtro por estado (Programada, Atendida, Cancelada)
                val coincideEstado = when (filtroEstado) {
                    "Programada" -> cita.estado is EstadoCita.Programada
                    "Atendida" -> cita.estado is EstadoCita.Atendida
                    "Cancelada" -> cita.estado is EstadoCita.Cancelada
                    else -> true
                }

                // RF-05: Búsqueda por especialidad o médico sin mayúsculas ni tildes
                val coincideBusqueda = if (busquedaNormalizada.isBlank()) {
                    true
                } else {
                    normalizarTexto(cita.especialidad).contains(busquedaNormalizada) ||
                            normalizarTexto(cita.medico).contains(busquedaNormalizada)
                }

                coincideEstado && coincideBusqueda
            }
            .sortedWith(compareBy({ it.fecha }, { it.hora })) // RF-02: Orden de la más próxima a la más lejana
    }

    private fun normalizarTexto(texto: String): String {
        return texto.lowercase().trim()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ñ", "n")
    }
}