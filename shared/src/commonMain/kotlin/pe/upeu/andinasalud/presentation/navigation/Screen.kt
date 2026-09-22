package pe.upeu.andinasalud.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val ruta: String,
    val titulo: String,
    val icono: ImageVector? = null
) {
    data object Citas : Screen(
        ruta = "citas",
        titulo = "Mis Citas",
        icono = Icons.Default.CalendarMonth
    )

    data object Solicitar : Screen(
        ruta = "solicitar",
        titulo = "Solicitar",
        icono = Icons.Default.AddCircle
    )

    data object Perfil : Screen(
        ruta = "perfil",
        titulo = "Perfil",
        icono = Icons.Default.Person
    )

    data class Detalle(val citaId: Long) : Screen(
        ruta = "detalle/$citaId",
        titulo = "Detalle de Cita"
    )
}