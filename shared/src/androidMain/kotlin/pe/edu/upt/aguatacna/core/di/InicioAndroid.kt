package pe.edu.upt.aguatacna.core.di

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module
import pe.edu.upt.aguatacna.core.db.AguaTacnaDatabase
import pe.edu.upt.aguatacna.core.db.crearBaseDeDatos
import pe.edu.upt.aguatacna.data.IdentidadLocal

/** Lo que solo Android sabe aportar: la base de datos y el usuario local. */
fun moduloPlataforma(base: AguaTacnaDatabase, usuarioId: String) = module {
    single { base }
    single { base.usuarioDao() }
    single { base.reservaDao() }
    single(QUALIFICADOR_USUARIO) { usuarioId }
}

/** Se llama una sola vez desde la clase `Application`, antes de mostrar ninguna pantalla. */
fun iniciarAplicacion(context: Context) {
    if (koinIniciado()) return
    val base = crearBaseDeDatos(context)
    // El usuario debe existir antes de la primera pantalla; es una lectura local y rápida.
    val usuario = runBlocking(Dispatchers.IO) { IdentidadLocal(base.usuarioDao()).obtenerOCrear() }
    iniciarKoin { modules(moduloPlataforma(base, usuario.id)) }
}
