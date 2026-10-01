package pe.edu.upt.aguatacna.feature.retos.data

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.retos.data.local.RetoEntity
import pe.edu.upt.aguatacna.feature.retos.data.local.RetoUsuarioEntity
import pe.edu.upt.aguatacna.feature.retos.data.local.RetosDao
import pe.edu.upt.aguatacna.feature.retos.data.sync.NubeRetosSupabase
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reto
import pe.edu.upt.aguatacna.feature.retos.domain.model.RetoUsuario
import pe.edu.upt.aguatacna.feature.retos.domain.repository.RetosRepository

class RetosRepositoryImpl(
    private val dao: RetosDao,
    private val usuarioId: String,
    private val nube: NubeRetosSupabase,
    private val hoy: () -> LocalDate
) : RetosRepository {
    override suspend fun obtenerRetosActivos(): List<Reto> {
        val remotos = runCatching { nube.obtenerRetos() }.getOrDefault(emptyList())
        if (remotos.isNotEmpty()) dao.guardarRetos(remotos.map { it.aEntidad() })
        val locales = dao.obtenerRetos().filter { it.titulo.isNotBlank() && it.litrosMeta > 0 }
        if (locales.isEmpty()) dao.guardarRetos(retosIniciales.map { it.aEntidad() })
        return dao.obtenerRetos().filter { it.titulo.isNotBlank() && it.litrosMeta > 0 }.map { it.aDominio() }
    }

    override suspend fun obtenerCumplimientos(): List<RetoUsuario> {
        runCatching { nube.obtenerCumplimientos() }.getOrDefault(emptyList()).forEach {
            dao.guardarCumplimiento(it.aEntidad(usuarioId))
        }
        return dao.obtenerCumplimientos(usuarioId).map { it.aDominio() }
    }

    override suspend fun guardarCumplimiento(cumplimiento: RetoUsuario) {
        dao.guardarCumplimiento(cumplimiento.aEntidad(usuarioId))
        runCatching { nube.guardar(cumplimiento) }
    }

    override suspend fun obtenerPromedioSector(sectorId: String): Double = 310.0
    override suspend fun fechaActual(): LocalDate = hoy()
}

private val retosIniciales = listOf(
    Reto("carga-completa", "Lavar ropa solo con carga completa", "Aprovecha cada ciclo de lavado.", 90),
    Reto("ducha-corta", "Duchas de 5 minutos toda la semana", "Reduce el tiempo bajo la ducha.", 210),
    Reto("reusar-agua", "Reutilizar el agua del enjuague", "Úsala para limpiar pisos.", 60)
)

private fun Reto.aEntidad() = RetoEntity(id, titulo, descripcion, litrosMeta, activo)
private fun RetoEntity.aDominio() = Reto(id, titulo, descripcion, litrosMeta, activo)
private fun RetoUsuario.aEntidad(usuario: String) = RetoUsuarioEntity(usuario, retoId, fecha.toString(), cumplido)
private fun RetoUsuarioEntity.aDominio() = RetoUsuario(retoId, LocalDate.parse(fecha), cumplido)
