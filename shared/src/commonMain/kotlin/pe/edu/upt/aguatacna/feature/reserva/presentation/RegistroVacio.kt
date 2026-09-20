package pe.edu.upt.aguatacna.feature.reserva.presentation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Aviso
import pe.edu.upt.aguatacna.feature.reserva.domain.model.AvisoGuardado
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.RegistroDeAvisos

/** Historial sin avisos, para cuando aún no hay base de datos conectada. */
internal object RegistroVacio : RegistroDeAvisos {
    override suspend fun yaSeAviso(clave: String) = false
    override suspend fun registrar(aviso: Aviso, momento: LocalDateTime) = Unit
    override fun observar(): Flow<List<AvisoGuardado>> = flowOf(emptyList())
    override suspend fun marcarTodosComoLeidos() = Unit
}
