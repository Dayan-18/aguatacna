package pe.edu.upt.aguatacna.data

import pe.edu.upt.aguatacna.core.util.nuevoUuid
import pe.edu.upt.aguatacna.data.local.UsuarioDao
import pe.edu.upt.aguatacna.data.local.UsuarioEntity

/** El usuario se identifica con un UUID local que se crea una sola vez y no cambia (constitución, artículo III). */
class IdentidadLocal(
    private val dao: UsuarioDao,
    private val nuevoId: () -> String = ::nuevoUuid
) {
    suspend fun obtenerOCrear(): UsuarioEntity =
        dao.obtener() ?: UsuarioEntity(id = nuevoId()).also { dao.guardar(it) }
}
