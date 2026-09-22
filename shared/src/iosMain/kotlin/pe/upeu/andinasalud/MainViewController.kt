package pe.upeu.andinasalud

import androidx.compose.ui.window.ComposeUIViewController
import pe.upeu.andinasalud.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}