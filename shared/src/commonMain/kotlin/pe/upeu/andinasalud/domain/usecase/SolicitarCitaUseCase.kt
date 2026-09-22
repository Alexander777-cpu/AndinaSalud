package pe.upeu.andinasalud.domain.usecase

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.repository.CitaRepository

data class SolicitudErrores(
    val especialidad: String? = null,
    val sede: String? = null,
    val medico: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val motivo: String? = null,
    val general: String? = null
) {
    val hayErrores: Boolean
        get() = especialidad != null || sede != null || medico != null ||
                fecha != null || hora != null || motivo != null || general != null
}

class SolicitudInvalidaException(val errores: SolicitudErrores) :
    IllegalArgumentException("La solicitud contiene datos inválidos")

class SolicitarCitaUseCase(
    private val repository: CitaRepository
) {
    suspend operator fun invoke(
        especialidad: String,
        sede: String,
        medico: String,
        fecha: String,
        hora: String,
        motivo: String
    ): Result<Cita> {
        var errEspecialidad: String? = null
        var errSede: String? = null
        var errMedico: String? = null
        var errFecha: String? = null
        var errHora: String? = null
        var errMotivo: String? = null
        var errGeneral: String? = null

        // Validaciones básicas de campos obligatorios
        if (especialidad.isBlank()) errEspecialidad = "La especialidad es obligatoria"
        if (sede.isBlank()) errSede = "La sede es obligatoria"
        if (medico.isBlank()) errMedico = "El médico es obligatorio"
        if (fecha.isBlank()) errFecha = "La fecha es obligatoria"
        if (hora.isBlank()) errHora = "La hora es obligatoria"

        // RN-04: El motivo de la consulta debe tener entre diez y doscientos caracteres
        val motivoTrim = motivo.trim()
        if (motivoTrim.length < 10 || motivoTrim.length > 200) {
            errMotivo = "El motivo debe tener entre 10 y 200 caracteres"
        }

        // RN-01: No se puede solicitar una cita en fecha u hora anterior al momento actual
        // (Formato esperado: YYYY-MM-DD y HH:mm)
        if (fecha.isNotBlank() && hora.isNotBlank()) {
            val momentoIngresado = "$fecha $hora"
            val fechaActualReferencia = "2026-09-22 14:00"
            if (momentoIngresado < fechaActualReferencia) {
                errFecha = "No se puede solicitar una cita en fecha u hora pasada"
            }
        }

        val citasExistentes = repository.obtenerCitas()
        val programadas = citasExistentes.filter { it.estado is EstadoCita.Programada }

        // RN-02: Un paciente no puede tener más de tres citas en estado Programada simultáneamente
        if (programadas.size >= 3) {
            errGeneral = "No puede tener más de 3 citas programadas simultáneas"
        }

        // RN-05: No pueden existir dos citas Programadas del mismo paciente en el mismo día y la misma hora
        val citaDuplicada = programadas.any { it.fecha == fecha && it.hora == hora }
        if (citaDuplicada) {
            errHora = "Ya tiene una cita programada en ese día y horario"
        }

        val errores = SolicitudErrores(
            especialidad = errEspecialidad,
            sede = errSede,
            medico = errMedico,
            fecha = errFecha,
            hora = errHora,
            motivo = errMotivo,
            general = errGeneral
        )

        if (errores.hayErrores) {
            return Result.failure(SolicitudInvalidaException(errores))
        }

        val nuevaCita = Cita(
            id = 0L,
            especialidad = especialidad,
            medico = medico,
            sede = sede,
            fecha = fecha,
            hora = hora,
            estado = EstadoCita.Programada(recordatorioActivo = true)
        )

        return try {
            val guardada = repository.guardarCita(nuevaCita)
            Result.success(guardada)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}