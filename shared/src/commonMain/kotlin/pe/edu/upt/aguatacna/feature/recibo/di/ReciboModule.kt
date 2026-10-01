package pe.edu.upt.aguatacna.feature.recibo.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.map
import org.koin.dsl.module
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.data.ReciboRepositoryRoom
import pe.edu.upt.aguatacna.feature.recibo.data.sync.NubeReciboSupabase
import pe.edu.upt.aguatacna.feature.recibo.data.sync.SincronizadorRecibo
import pe.edu.upt.aguatacna.feature.recibo.domain.port.ParserRecibo
import pe.edu.upt.aguatacna.feature.recibo.domain.port.ReconocedorTexto
import pe.edu.upt.aguatacna.feature.recibo.domain.repository.ReciboRepository
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ConfirmarReciboUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.CorregirCampoUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.EscanearReciboUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarHistorialUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ObservarResumenUseCase
import pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr.ParserReciboEpsTacna
import pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr.crearReconocedorTexto

// Los DAO vienen de la base de datos general (AguaTacnaDatabase), aportada por cada plataforma.
val moduloRecibo = module {
    single<ReciboRepository> { ReciboRepositoryRoom(get()) }
    // Una plataforma sin DAO del borrador lo guarda solo en memoria.
    single { BorradorReciboStore(getOrNull()) }
    single<ParserRecibo> { ParserReciboEpsTacna }
    single<ReconocedorTexto> { crearReconocedorTexto() }
    single {
        val supabase = get<SupabaseClient>()
        SincronizadorRecibo(
            dao = get(),
            nube = NubeReciboSupabase(supabase),
            haySesion = supabase.auth.sessionStatus.map { it is SessionStatus.Authenticated }
        )
    }

    factory { ObservarResumenUseCase(get()) }
    factory { ObservarHistorialUseCase(get()) }
    factory { ConfirmarReciboUseCase(get()) }
    factory { CorregirCampoUseCase() }
    factory { EscanearReciboUseCase(get(), get()) }
}
