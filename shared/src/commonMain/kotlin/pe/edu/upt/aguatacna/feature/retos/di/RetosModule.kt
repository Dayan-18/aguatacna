package pe.edu.upt.aguatacna.feature.retos.di

import io.github.jan.supabase.SupabaseClient
import org.koin.dsl.module
import pe.edu.upt.aguatacna.core.di.QUALIFICADOR_USUARIO
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.feature.retos.data.RetosRepositoryImpl
import pe.edu.upt.aguatacna.feature.retos.data.ReporteRepositoryImpl
import pe.edu.upt.aguatacna.feature.retos.data.sync.NubeReportesSupabase
import pe.edu.upt.aguatacna.feature.retos.data.sync.NubeRetosSupabase
import pe.edu.upt.aguatacna.feature.retos.domain.repository.ReporteRepository
import pe.edu.upt.aguatacna.feature.retos.domain.repository.RetosRepository

val moduloRetos = module {
    single<RetosRepository> {
        val reloj = get<Reloj>()
        RetosRepositoryImpl(
            dao = get(), usuarioId = get(QUALIFICADOR_USUARIO),
            nube = NubeRetosSupabase(get<SupabaseClient>()), hoy = { reloj.ahora().date }
        )
    }
    single<ReporteRepository> {
        ReporteRepositoryImpl(get(), get(QUALIFICADOR_USUARIO), NubeReportesSupabase(get<SupabaseClient>()))
    }
}
