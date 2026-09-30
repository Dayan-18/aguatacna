package pe.edu.upt.aguatacna.feature.asistente.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.edu.upt.aguatacna.feature.asistente.domain.model.MensajeAsistente

/**
 * Contrato del repositorio para el Asistente Hídrico (DDD).
 */
interface AsistenteRepository {
    /** Observa el flujo de mensajes de la conversación en tiempo real. */
    fun observarMensajes(): Flow<List<MensajeAsistente>>

    /** Obtiene el historial actual de mensajes. */
    suspend fun obtenerHistorial(): List<MensajeAsistente>

    /** Envía un mensaje a n8n y devuelve la respuesta del asistente. */
    suspend fun enviarMensaje(texto: String): Result<MensajeAsistente>

    /** Vacía la conversación. */
    suspend fun limpiarConversacion()
}
