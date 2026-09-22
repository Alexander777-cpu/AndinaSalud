package pe.upeu.andinasalud.data.local

import pe.upeu.andinasalud.domain.model.Cita
import pe.upeu.andinasalud.domain.model.EstadoCita
import pe.upeu.andinasalud.domain.model.Medico
import pe.upeu.andinasalud.domain.model.Paciente

object CitasSimuladas {
    val paciente = Paciente(
        id = "P-0417",
        nombre = "Lucia Quispe Mamani",
        documento = "70154823",
        correo = "lucia.quispe@correo.pe",
        telefono = "987654321"
    )

    val sedes = listOf("Ñaña", "Chosica", "Chaclacayo", "Santa Anita")

    val especialidades = listOf(
        "Medicina General",
        "Odontología",
        "Pediatría",
        "Nutrición",
        "Psicología"
    )

    val medicos = listOf(
        Medico(1, "Dr. Iván Rojas", "Medicina General", listOf("Ñaña", "Chosica")),
        Medico(2, "Dra. Patricia Silva", "Medicina General", listOf("Chaclacayo", "Santa Anita")),
        Medico(3, "Dra. Rosa Flores", "Odontología", listOf("Chosica", "Ñaña")),
        Medico(4, "Dr. Roberto Díaz", "Odontología", listOf("Santa Anita")),
        Medico(5, "Dra. Carla Núñez", "Pediatría", listOf("Chaclacayo", "Santa Anita")),
        Medico(6, "Dr. Marco Mendoza", "Pediatría", listOf("Ñaña")),
        Medico(7, "Lic. Ana Bermúdez", "Nutrición", listOf("Santa Anita", "Chaclacayo")),
        Medico(8, "Lic. Carlos Ramos", "Nutrición", listOf("Chosica")),
        Medico(9, "Ps. Luis Tapia", "Psicología", listOf("Ñaña", "Santa Anita")),
        Medico(10, "Ps. Elena Castro", "Psicología", listOf("Chosica", "Chaclacayo"))
    )

    fun obtenerCitasIniciales(): MutableList<Cita> = mutableListOf(
        Cita(
            id = 1L,
            especialidad = "Medicina General",
            medico = "Dr. Iván Rojas",
            sede = "Ñaña",
            fecha = "2026-09-28",
            hora = "09:00",
            estado = EstadoCita.Programada(recordatorioActivo = true)
        ),
        Cita(
            id = 2L,
            especialidad = "Odontología",
            medico = "Dra. Rosa Flores",
            sede = "Chosica",
            fecha = "2026-09-30",
            hora = "16:30",
            estado = EstadoCita.Programada(recordatorioActivo = false)
        ),
        Cita(
            id = 3L,
            especialidad = "Nutrición",
            medico = "Lic. Ana Bermúdez",
            sede = "Santa Anita",
            fecha = "2026-10-05",
            hora = "11:15",
            estado = EstadoCita.Programada(recordatorioActivo = true)
        ),
        Cita(
            id = 4L,
            especialidad = "Pediatría",
            medico = "Dra. Carla Núñez",
            sede = "Chaclacayo",
            fecha = "2026-08-30",
            hora = "08:45",
            estado = EstadoCita.Atendida("Control en tres meses")
        ),
        Cita(
            id = 5L,
            especialidad = "Psicología",
            medico = "Ps. Luis Tapia",
            sede = "Ñaña",
            fecha = "2026-09-02",
            hora = "15:00",
            estado = EstadoCita.Atendida("Continuar sesiones quincenales")
        ),
        Cita(
            id = 6L,
            especialidad = "Medicina General",
            medico = "Dr. Iván Rojas",
            sede = "Chosica",
            fecha = "2026-09-05",
            hora = "10:30",
            estado = EstadoCita.Cancelada("Viaje del paciente", true)
        )
    )
}