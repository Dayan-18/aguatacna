package pe.edu.upt.aguatacna.feature.sector.di

import io.github.jan.supabase.SupabaseClient
import org.koin.dsl.module
import pe.edu.upt.aguatacna.feature.sector.data.SectorRepositoryImpl
import pe.edu.upt.aguatacna.feature.sector.data.sync.NubeSectorSupabase
import pe.edu.upt.aguatacna.feature.sector.domain.repository.SectorRepository

val moduloSector = module {
    single<SectorRepository> {
        SectorRepositoryImpl(dao = get(), nube = NubeSectorSupabase(get<SupabaseClient>()))
    }
}
