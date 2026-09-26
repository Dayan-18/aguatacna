package pe.edu.upt.aguatacna.feature.asistente.di

import io.ktor.client.HttpClient
import org.koin.dsl.module
import pe.edu.upt.aguatacna.feature.asistente.data.AsistenteRepositoryImpl
import pe.edu.upt.aguatacna.feature.asistente.data.remote.N8nApiClient
import pe.edu.upt.aguatacna.feature.asistente.domain.repository.AsistenteRepository
import pe.edu.upt.aguatacna.feature.asistente.domain.usecase.EnviarMensajeUseCase
import pe.edu.upt.aguatacna.feature.asistente.domain.usecase.LimpiarConversacionUseCase
import pe.edu.upt.aguatacna.feature.asistente.domain.usecase.ObservarMensajesUseCase
import pe.edu.upt.aguatacna.feature.asistente.presentation.AsistenteViewModel

/**
 * Módulo de inyección de dependencias (Koin) para el Asistente Hídrico y n8n.
 */
val moduloAsistente = module {
    single { HttpClient() }
    single { N8nApiClient(get()) }
    single<AsistenteRepository> { AsistenteRepositoryImpl(get()) }

    // Casos de uso
    factory { ObservarMensajesUseCase(get()) }
    factory { EnviarMensajeUseCase(get()) }
    factory { LimpiarConversacionUseCase(get()) }

    // ViewModel
    factory { AsistenteViewModel(get(), get(), get()) }
}
