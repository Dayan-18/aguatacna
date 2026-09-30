package pe.edu.upt.aguatacna.feature.asistente.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock
import pe.edu.upt.aguatacna.core.util.nuevoUuid
import pe.edu.upt.aguatacna.feature.asistente.data.remote.N8nApiClient
import pe.edu.upt.aguatacna.feature.asistente.domain.model.MensajeAsistente
import pe.edu.upt.aguatacna.feature.asistente.domain.repository.AsistenteRepository

/**
 * Chat con n8n: la conversación empieza vacía y solo contiene lo que el usuario envía
 * y lo que n8n responde. Vive en memoria durante la sesión.
 */
class AsistenteRepositoryImpl(
    private val n8nApiClient: N8nApiClient = N8nApiClient()
) : AsistenteRepository {

    // n8n usa este id para mantener el contexto de la conversación.
    private val sessionId: String = nuevoUuid()

    private val _mensajes = MutableStateFlow<List<MensajeAsistente>>(emptyList())

    override fun observarMensajes(): Flow<List<MensajeAsistente>> = _mensajes.asStateFlow()

    override suspend fun obtenerHistorial(): List<MensajeAsistente> = _mensajes.value

    override suspend fun enviarMensaje(texto: String): Result<MensajeAsistente> {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        _mensajes.value = _mensajes.value + MensajeAsistente(nuevoUuid(), texto, esUsuario = true, timestamp = timestamp)

        return try {
            val respuesta = n8nApiClient.enviarPregunta(texto, sessionId, timestamp)
            val mensaje = MensajeAsistente(nuevoUuid(), respuesta, esUsuario = false, timestamp = ahora())
            _mensajes.value = _mensajes.value + mensaje
            Result.success(mensaje)
        } catch (e: Exception) {
            // Se muestra el error real (sin texto predeterminado) para saber por qué falló el envío.
            val error = MensajeAsistente(nuevoUuid(), e.message ?: e.toString(), esUsuario = false, timestamp = ahora(), esError = true)
            _mensajes.value = _mensajes.value + error
            Result.failure(e)
        }
    }

    override suspend fun limpiarConversacion() {
        _mensajes.value = emptyList()
    }

    private fun ahora(): Long = Clock.System.now().toEpochMilliseconds()
}
