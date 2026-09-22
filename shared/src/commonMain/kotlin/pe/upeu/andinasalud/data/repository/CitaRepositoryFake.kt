package pe.upeu.andinasalud.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.upeu.andinasalud.data.local.CitasSimuladas
import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Paciente
import pe.upeu.andinasalud.domain.repository.CitaRepository

class CitaRepositoryFake : CitaRepository {
    private val mutex = Mutex()
    private val citas = CitasSimuladas.obtenerCitasIniciales()

    override suspend fun obtenerCitas(): List<Cita> {
        delay(800) // RF-08: Retardo simulado de 800 ms
        return mutex.withLock { citas.toList() }
    }

    override suspend fun obtenerCitaPorId(id: Long): Cita? {
        delay(800)
        return mutex.withLock { citas.find { it.id == id } }
    }

    override suspend fun guardarCita(cita: Cita): Cita {
        delay(800)
        return mutex.withLock {
            val nuevoId = if (citas.isEmpty()) 1L else citas.maxOf { it.id } + 1L
            val citaConId = cita.copy(id = nuevoId)
            citas.add(citaConId)
            citaConId
        }
    }

    override suspend fun actualizarCita(cita: Cita): Cita {
        delay(800)
        return mutex.withLock {
            val index = citas.indexOfFirst { it.id == cita.id }
            if (index != -1) {
                citas[index] = cita
                cita
            } else {
                throw IllegalArgumentException("No se encontró la cita con id ${cita.id}")
            }
        }
    }

    override suspend fun obtenerPaciente(): Paciente {
        return CitasSimuladas.paciente
    }

    override suspend fun obtenerMedicos(): List<Medico> {
        return CitasSimuladas.medicos
    }

    override suspend fun obtenerSedes(): List<String> {
        return CitasSimuladas.sedes
    }

    override suspend fun obtenerEspecialidades(): List<String> {
        return CitasSimuladas.especialidades
    }
}