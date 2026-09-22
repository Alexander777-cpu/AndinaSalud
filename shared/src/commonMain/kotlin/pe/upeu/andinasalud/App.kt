package pe.upeu.andinasalud

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import pe.upeu.andinasalud.di.getCitasViewModel
import pe.upeu.andinasalud.di.getDetalleViewModel
import pe.upeu.andinasalud.di.getPerfilViewModel
import pe.upeu.andinasalud.di.getSolicitudViewModel
import pe.upeu.andinasalud.di.initKoin
import pe.upeu.andinasalud.presentation.citas.CitasScreen
import pe.upeu.andinasalud.presentation.detalle.DetalleScreen
import pe.upeu.andinasalud.presentation.navigation.BottomNavBar
import pe.upeu.andinasalud.presentation.navigation.Screen
import pe.upeu.andinasalud.presentation.perfil.PerfilScreen
import pe.upeu.andinasalud.presentation.solicitud.SolicitudScreen
import pe.upeu.andinasalud.presentation.theme.AndinaSaludTheme

@Composable
fun App() {
    initKoin()

    var esModoOscuro by remember { mutableStateOf(false) }
    var pantallaActual by remember { mutableStateOf<Screen>(Screen.Citas) }

    val citasViewModel = remember { getCitasViewModel() }
    val solicitudViewModel = remember { getSolicitudViewModel() }
    val perfilViewModel = remember { getPerfilViewModel() }
    val detalleViewModel = remember { getDetalleViewModel() }

    AndinaSaludTheme(darkTheme = esModoOscuro) {
        Scaffold(
            bottomBar = {
                // La barra inferior solo se muestra en las pantallas principales
                if (pantallaActual !is Screen.Detalle) {
                    BottomNavBar(
                        pantallaActual = pantallaActual,
                        onSeleccionarPantalla = { pantalla ->
                            pantallaActual = pantalla
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            val modifierConPadding = Modifier.padding(paddingValues)

            when (val pantalla = pantallaActual) {
                is Screen.Citas -> {
                    CitasScreen(
                        viewModel = citasViewModel,
                        onCitaClick = { citaId ->
                            pantallaActual = Screen.Detalle(citaId)
                        },
                        modifier = modifierConPadding
                    )
                }
                is Screen.Solicitar -> {
                    SolicitudScreen(
                        viewModel = solicitudViewModel,
                        onCitaCreada = {
                            citasViewModel.cargarDatos()
                            pantallaActual = Screen.Citas
                        },
                        modifier = modifierConPadding
                    )
                }
                is Screen.Perfil -> {
                    PerfilScreen(
                        viewModel = perfilViewModel,
                        esModoOscuro = esModoOscuro,
                        onCambiarModoOscuro = { esModoOscuro = it },
                        modifier = modifierConPadding
                    )
                }
                is Screen.Detalle -> {
                    DetalleScreen(
                        citaId = pantalla.citaId,
                        viewModel = detalleViewModel,
                        onVolver = {
                            citasViewModel.cargarDatos()
                            pantallaActual = Screen.Citas
                        },
                        modifier = modifierConPadding
                    )
                }
            }
        }
    }
}