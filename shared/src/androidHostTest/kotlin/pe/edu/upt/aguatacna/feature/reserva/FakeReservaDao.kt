package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import pe.edu.upt.aguatacna.feature.reserva.data.local.EventoLlenadoEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.NovedadReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.PerfilHogarEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.ReservaDao

/** DAO en memoria de un solo usuario, para probar el repositorio sin una base real. */
class FakeReservaDao : ReservaDao {
    val perfil = MutableStateFlow<PerfilHogarEntity?>(null)
    val llenados = MutableStateFlow<List<EventoLlenadoEntity>>(emptyList())
    val novedades = MutableStateFlow<List<NovedadReservaEntity>>(emptyList())

    override fun observarPerfil(usuarioId: String): Flow<PerfilHogarEntity?> = perfil

    override suspend fun guardarPerfil(perfil: PerfilHogarEntity) {
        this.perfil.value = perfil
    }

    override fun observarLlenados(usuarioId: String): Flow<List<EventoLlenadoEntity>> =
        llenados.map { lista -> lista.sortedBy { it.momento } }

    override suspend fun guardarLlenado(llenado: EventoLlenadoEntity) {
        llenados.value = llenados.value.filterNot { it.id == llenado.id } + llenado
    }

    override suspend fun borrarLlenados(usuarioId: String) {
        llenados.value = emptyList()
    }

    override fun observarNovedades(usuarioId: String): Flow<List<NovedadReservaEntity>> =
        novedades.map { lista -> lista.sortedBy { it.momento } }

    override suspend fun guardarNovedad(novedad: NovedadReservaEntity) {
        novedades.value = novedades.value.filterNot { it.id == novedad.id } + novedad
    }
}
