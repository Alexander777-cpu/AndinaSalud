package pe.upeu.andinasalud.di

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.detalle.DetalleViewModel
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel

val appModule = module {
    // REGLA CRÍTICA: single y tipado a la interfaz CitaRepository
    single<CitaRepository> { CitaRepositoryFake() }

    // Casos de uso
    factory { ObtenerCitasUseCase(get()) }
    factory { SolicitarCitaUseCase(get()) }
    factory { CancelarCitaUseCase(get()) }

    // ViewModels
    factory { CitasViewModel(get(), get()) }
    factory { SolicitudViewModel(get(), get()) }
    factory { PerfilViewModel(get()) }
    factory { DetalleViewModel(get(), get()) }
}

var koinInstance: Koin? = null

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    if (koinInstance == null) {
        runCatching {
            koinInstance = startKoin {
                appDeclaration()
                modules(appModule)
            }.koin
        }
    }
}

// Funciones auxiliares de inyección segura para la interfaz de Compose
fun getCitasViewModel(): CitasViewModel = koinInstance?.get() ?: run {
    initKoin()
    koinInstance!!.get()
}

fun getSolicitudViewModel(): SolicitudViewModel = koinInstance?.get() ?: run {
    initKoin()
    koinInstance!!.get()
}

fun getPerfilViewModel(): PerfilViewModel = koinInstance?.get() ?: run {
    initKoin()
    koinInstance!!.get()
}

fun getDetalleViewModel(): DetalleViewModel = koinInstance?.get() ?: run {
    initKoin()
    koinInstance!!.get()
}