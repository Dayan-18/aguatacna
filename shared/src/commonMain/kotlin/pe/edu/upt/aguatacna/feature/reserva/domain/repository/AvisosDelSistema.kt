package pe.edu.upt.aguatacna.feature.reserva.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Aviso
import pe.edu.upt.aguatacna.feature.reserva.domain.model.AvisoGuardado

/** El historial de avisos: sirve para no repetir el mismo aviso cada hora y para la lista de avisos. */
interface RegistroDeAvisos {
    suspend fun yaSeAviso(clave: String): Boolean
    suspend fun registrar(aviso: Aviso, momento: LocalDateTime)
    fun observar(): Flow<List<AvisoGuardado>>
    suspend fun marcarTodosComoLeidos()
}

/** Muestra el aviso al usuario; cada plataforma lo hace a su manera. */
interface Notificador {
    fun mostrar(aviso: Aviso)
}
