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
    single<CitaRepository> { CitaRepositoryFake() }

    factory { ObtenerCitasUseCase(get()) }
    factory { SolicitarCitaUseCase(get()) }
    factory { CancelarCitaUseCase(get()) }

    factory { CitasViewModel(get(), get()) }
    factory { SolicitudViewModel(get(), get()) }
    factory { PerfilViewModel(get()) }
    factory { DetalleViewModel(get(), get()) }
}

private var koinApp: Koin? = null

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    if (koinApp == null) {
        try {
            koinApp = startKoin {
                appDeclaration()
                modules(appModule)
            }.koin
        } catch (_: Exception) {
            // Evita crash si ya estaba iniciado previamente
        }
    }
}

fun getKoin(): Koin {
    if (koinApp == null) {
        initKoin()
    }
    return koinApp ?: error("No se pudo inicializar Koin")
}

fun getCitasViewModel(): CitasViewModel = getKoin().get()
fun getSolicitudViewModel(): SolicitudViewModel = getKoin().get()
fun getPerfilViewModel(): PerfilViewModel = getKoin().get()
fun getDetalleViewModel(): DetalleViewModel = getKoin().get()