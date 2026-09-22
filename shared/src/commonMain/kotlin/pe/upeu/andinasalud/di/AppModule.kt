package pe.upeu.andinasalud.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import pe.upeu.andinasalud.data.repository.CitaRepositoryFake
import pe.upeu.andinasalud.domain.repository.CitaRepository
import pe.upeu.andinasalud.domain.usecase.CancelarCitaUseCase
import pe.upeu.andinasalud.domain.usecase.ObtenerCitasUseCase
import pe.upeu.andinasalud.domain.usecase.SolicitarCitaUseCase
import pe.upeu.andinasalud.presentation.citas.CitasViewModel
import pe.upeu.andinasalud.presentation.perfil.PerfilViewModel
import pe.upeu.andinasalud.presentation.solicitud.SolicitudViewModel

val appModule = module {
    // REGLA CRÍTICA: Debe ser 'single' y tipado a la interfaz 'CitaRepository'
    single<CitaRepository> { CitaRepositoryFake() }

    // Casos de uso
    factory { ObtenerCitasUseCase(get()) }
    factory { SolicitarCitaUseCase(get()) }
    factory { CancelarCitaUseCase(get()) }

    // ViewModels
    factory { CitasViewModel(get(), get()) }
    factory { PerfilViewModel(get()) }
    factory { SolicitudViewModel(get(), get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    runCatching {
        startKoin {
            appDeclaration()
            modules(appModule)
        }
    }
}