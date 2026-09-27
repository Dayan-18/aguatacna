package pe.edu.upt.aguatacna.core.di

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import pe.edu.upt.aguatacna.core.db.AguaTacnaDatabase
import pe.edu.upt.aguatacna.core.db.crearBaseDeDatos
import pe.edu.upt.aguatacna.core.sesion.InicioConGoogle
import pe.edu.upt.aguatacna.core.sesion.InicioConGoogleAndroid
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.data.IdentidadLocal
import pe.edu.upt.aguatacna.feature.reserva.data.sync.SincronizadorReserva

import pe.edu.upt.aguatacna.feature.recibo.domain.port.ReconocedorTexto
import pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr.ReconocedorTextoAndroid

/** Lo que solo Android sabe aportar: la base de datos y el usuario local. */
fun moduloPlataforma(base: AguaTacnaDatabase, usuarioId: String) = module {
    single { base }
    single { base.usuarioDao() }
    single { base.reservaDao() }
    single { base.reciboDao() }
    single { base.avisoReservaDao() }
    single { base.sectorDao() }
    single(QUALIFICADOR_USUARIO) { usuarioId }
    single<ReconocedorTexto> { ReconocedorTextoAndroid() }
    single<InicioConGoogle> { InicioConGoogleAndroid(get()) }
}

/** Se llama una sola vez desde la clase `Application`, antes de mostrar ninguna pantalla. */
fun iniciarAplicacion(context: Context) {
    if (koinIniciado()) return
    val base = crearBaseDeDatos(context)
    // El usuario debe existir antes de la primera pantalla; es una lectura local y rápida.
    val usuario = runBlocking(Dispatchers.IO) { IdentidadLocal(base.usuarioDao()).obtenerOCrear() }
    iniciarKoin { modules(moduloPlataforma(base, usuario.id)) }
}

/** Se queda copiando la reserva a la nube (si hay sesión de Google) hasta que se cancele la corrutina que lo llama. */
suspend fun mantenerReservaSincronizada() {
    if (koinIniciado()) KoinPlatform.getKoin().get<SincronizadorReserva>().mantenerSincronizado()
}
