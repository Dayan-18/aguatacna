package pe.edu.upt.aguatacna.core.sesion

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.edu.upt.aguatacna.data.local.UsuarioDao

class RegistroDeAccesoEnRoom(private val dao: UsuarioDao) : RegistroDeAcceso {
    override fun observar(): Flow<ModoDeAcceso?> =
        dao.observarModoDeAcceso().map { nombre -> ModoDeAcceso.entries.firstOrNull { it.name == nombre } }

    override suspend fun guardar(modo: ModoDeAcceso) = dao.guardarModoDeAcceso(modo.name)
}
