package pe.edu.upt.aguatacna.feature.sector.di

import org.koin.dsl.module
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.feature.sector.data.FakeSectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.repository.SectorRepository

// Provisional, lo aportó reserva para conectar el cronograma: sector la reemplaza
// por su repositorio real cuando lo tenga.
val moduloSector = module {
    single<SectorRepository> { FakeSectorRepository(get<Reloj>().ahora().date) }
}
