package pe.upeu.andinasalud.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomNavBar(
    pantallaActual: Screen,
    onSeleccionarPantalla: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        Screen.itemsBarraInferior.forEach { pantalla ->
            val seleccionada = when (pantallaActual) {
                is Screen.Detalle -> false
                else -> pantallaActual.ruta == pantalla.ruta
            }

            NavigationBarItem(
                selected = seleccionada,
                onClick = { onSeleccionarPantalla(pantalla) },
                icon = {
                    pantalla.icono?.let {
                        Icon(imageVector = it, contentDescription = pantalla.titulo)
                    }
                },
                label = { Text(pantalla.titulo) }
            )
        }
    }
}