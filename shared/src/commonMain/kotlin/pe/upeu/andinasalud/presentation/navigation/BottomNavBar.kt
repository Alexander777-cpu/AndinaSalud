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
    // Declarar la lista dentro de la función garantiza que los objetos ya existen en memoria
    val items = listOf(
        Screen.Citas,
        Screen.Solicitar,
        Screen.Perfil
    )

    NavigationBar(modifier = modifier) {
        items.forEach { screen ->
            val seleccionado = pantallaActual::class == screen::class

            NavigationBarItem(
                selected = seleccionado,
                onClick = { onSeleccionarPantalla(screen) },
                icon = {
                    screen.icono?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = screen.titulo
                        )
                    }
                },
                label = {
                    Text(text = screen.titulo)
                }
            )
        }
    }
}