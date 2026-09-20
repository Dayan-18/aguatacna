package pe.edu.upt.aguatacna.feature.reserva.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.data.local.AvisoReservaDao
import pe.edu.upt.aguatacna.feature.reserva.data.local.AvisoReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Aviso
import pe.edu.upt.aguatacna.feature.reserva.domain.model.AvisoGuardado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Deficit
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.RegistroDeAvisos

class RegistroDeAvisosEnRoom(
    private val dao: AvisoReservaDao,
    private val usuarioId: String,
    private val nuevoId: () -> String
) : RegistroDeAvisos {

    override suspend fun yaSeAviso(clave: String): Boolean = dao.contar(usuarioId, clave) > 0

    override suspend fun registrar(aviso: Aviso, momento: LocalDateTime) {
        dao.guardar(aviso.aEntidad(nuevoId(), usuarioId, momento))
    }

    override fun observar(): Flow<List<AvisoGuardado>> =
        dao.observar(usuarioId).map { lista -> lista.map { it.aDominio() } }

    override suspend fun marcarTodosComoLeidos() = dao.marcarTodosLeidos(usuarioId)
}

private fun Aviso.aEntidad(id: String, usuarioId: String, momento: LocalDateTime) = when (this) {
    is Aviso.AgotamientoAntesDelAbastecimiento ->
        AvisoReservaEntity(id, usuarioId, clave, AvisoReservaEntity.AGOTAMIENTO, momento.toString(), agotamiento.toString(), deficit.horas, false)
    is Aviso.ConfirmarLlenado ->
        AvisoReservaEntity(id, usuarioId, clave, AvisoReservaEntity.CONFIRMAR_LLENADO, momento.toString(), inicioDeLaVentana.toString(), null, false)
}

private fun AvisoReservaEntity.aDominio(): AvisoGuardado {
    val fecha = LocalDateTime.parse(fechaDato)
    val aviso = when (tipo) {
        AvisoReservaEntity.AGOTAMIENTO -> Aviso.AgotamientoAntesDelAbastecimiento(fecha, Deficit(horasDato ?: 0.0))
        else -> Aviso.ConfirmarLlenado(fecha)
    }
    return AvisoGuardado(id, aviso, LocalDateTime.parse(momento), leido)
}
