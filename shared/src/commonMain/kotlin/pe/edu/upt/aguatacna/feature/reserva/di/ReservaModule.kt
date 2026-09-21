package pe.edu.upt.aguatacna.feature.reserva.di

import org.koin.dsl.module
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.map
import pe.edu.upt.aguatacna.core.di.QUALIFICADOR_USUARIO
import pe.edu.upt.aguatacna.feature.reserva.data.sync.NubeReservaSupabase
import pe.edu.upt.aguatacna.feature.reserva.data.sync.SincronizadorReserva
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.core.util.nuevoUuid
import pe.edu.upt.aguatacna.data.local.UsuarioDao
import pe.edu.upt.aguatacna.feature.reserva.data.AbastecimientosDeSector
import pe.edu.upt.aguatacna.feature.reserva.data.EstimadorPorHabitosProvisional
import pe.edu.upt.aguatacna.feature.reserva.data.RegistroDeAvisosEnRoom
import pe.edu.upt.aguatacna.feature.reserva.data.ReservaRepositoryImpl
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.EstimadorPorHabitos
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.RegistroDeAvisos
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository

// Hasta que el usuario registre su domicilio (feature/sector) se usa este sector de prueba.
private const val SECTOR_DE_PRUEBA = "CN-04"

val moduloReserva = module {
    single<EstimadorPorHabitos> { EstimadorPorHabitosProvisional() }
    single<AbastecimientosDelSector> {
        AbastecimientosDeSector(get()) { get<UsuarioDao>().obtener()?.sectorId ?: SECTOR_DE_PRUEBA }
    }
    single<RegistroDeAvisos> { RegistroDeAvisosEnRoom(get(), get(QUALIFICADOR_USUARIO), ::nuevoUuid) }
    single<ReservaRepository> {
        ReservaRepositoryImpl(get(), get(QUALIFICADOR_USUARIO), get(), get<Reloj>()::ahora, ::nuevoUuid)
    }
    single {
        val supabase = get<SupabaseClient>()
        SincronizadorReserva(
            dao = get(),
            usuarioId = get(QUALIFICADOR_USUARIO),
            nube = NubeReservaSupabase(supabase),
            haySesion = supabase.auth.sessionStatus.map { it is SessionStatus.Authenticated }
        )
    }
}
