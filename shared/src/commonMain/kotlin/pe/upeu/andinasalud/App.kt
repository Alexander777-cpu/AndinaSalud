package pe.upeu.andinasalud

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import pe.upeu.andinasalud.presentation.theme.AndinaSaludTheme

@Composable
fun App() {
    AndinaSaludTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("AndinaSalud - Inicializado con éxito")
            }
        }
    }
}