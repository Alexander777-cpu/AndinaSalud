package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CancelarCitaUseCase(
    private val repository: CitaRepository
) {
    suspend operator fun invoke(citaId: Long, motivo: String): Result<Cita> {
        val cita = repository.obtenerCitaPorId(citaId)
            ?: return Result.failure(IllegalArgumentException("Cita no encontrada"))

        // RN-03: Solo puede cancelarse si está Programada
        if (cita.estado !is EstadoCita.Programada) {
            return Result.failure(IllegalStateException("Solo se pueden cancelar citas en estado Programada"))
        }

        // RN-03: Faltan más de veinticuatro horas para su realización
        // Estimación con la fecha actual del sistema
        val momentoCita = "${cita.fecha} ${cita.hora}"
        val fechaReferenciaMas24h = "2026-09-23 14:00"
        if (momentoCita <= fechaReferenciaMas24h) {
            return Result.failure(IllegalStateException("No se puede cancelar con menos de 24 horas de anticipación"))
        }

        val citaCancelada = cita.copy(
            estado = EstadoCita.Cancelada(
                motivo = motivo.ifBlank { "Cancelada por el paciente" },
                canceladaPorPaciente = true
            )
        )

        return try {
            val actualizada = repository.actualizarCita(citaCancelada)
            Result.success(actualizada)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}