package pe.edu.upt.aguatacna.feature.recibo.di

import org.koin.dsl.module
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.data.ReciboRepositoryRoom
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ConfirmarReciboUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.CorregirCampoUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.EscanearReciboUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarHistorialUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarResumenUseCase

val moduloRecibo = module {
    // El DAO viene de la base de datos general (AguaTacnaDatabase), aportado por cada plataforma
    single<ReciboRepository> { ReciboRepositoryRoom(get()) }
    single { BorradorReciboStore() }

    // Casos de uso
    factory { ObservarResumenUseCase(get()) }
    factory { ObservarHistorialUseCase(get()) }
    factory { ConfirmarReciboUseCase(get()) }
    factory { CorregirCampoUseCase() }
    factory { EscanearReciboUseCase(get()) }

    // ViewModels se crean vía companion object con KoinPlatform (patrón del proyecto)
}
