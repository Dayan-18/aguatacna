package pe.edu.upt.aguatacna.core.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import io.github.jan.supabase.SupabaseClient
import pe.edu.upt.aguatacna.core.nube.crearClienteSupabase
import pe.edu.upt.aguatacna.core.sesion.RegistroDeAcceso
import pe.edu.upt.aguatacna.core.sesion.RegistroDeAccesoEnRoom
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.core.util.RelojDelSistema
import pe.edu.upt.aguatacna.feature.asistente.di.moduloAsistente
import pe.edu.upt.aguatacna.feature.recibo.di.moduloRecibo
import pe.edu.upt.aguatacna.feature.reserva.di.moduloReserva
import pe.edu.upt.aguatacna.feature.retos.di.moduloRetos
import pe.edu.upt.aguatacna.feature.sector.di.moduloSector

/** Cada plataforma aporta con este nombre el UUID local del usuario. */
val QUALIFICADOR_USUARIO = named("usuarioId")

val moduloCore = module {
    single<Reloj> { RelojDelSistema() }
    single<RegistroDeAcceso> { RegistroDeAccesoEnRoom(get()) }
    // Se crea al primer uso; cada plataforma aporta su `InicioConGoogle`.
    single<SupabaseClient> { crearClienteSupabase() }
}

val modulosApp: List<Module> = listOf(moduloCore, moduloReserva, moduloSector, moduloRecibo, moduloRetos, moduloAsistente)

fun koinIniciado(): Boolean = KoinPlatform.getKoinOrNull() != null

fun iniciarKoin(extra: KoinAppDeclaration? = null): KoinApplication =
    startKoin {
        extra?.invoke(this)
        modules(modulosApp)
    }
