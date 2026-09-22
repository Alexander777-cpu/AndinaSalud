package pe.upeu.andinasalud.domain.repository

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Paciente

interface CitaRepository {
    suspend fun obtenerCitas(): List<Cita>
    suspend fun obtenerCitaPorId(id: Long): Cita?
    suspend fun guardarCita(cita: Cita): Cita
    suspend fun actualizarCita(cita: Cita): Cita
    suspend fun obtenerPaciente(): Paciente
    suspend fun obtenerMedicos(): List<Medico>
    suspend fun obtenerSedes(): List<String>
    suspend fun obtenerEspecialidades(): List<String>
}