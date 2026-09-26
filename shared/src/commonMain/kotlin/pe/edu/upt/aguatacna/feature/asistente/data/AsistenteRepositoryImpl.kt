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
 * Implementación del repositorio del Asistente Hídrico conectado a n8n.
 * Mantiene el estado reactivo en memoria durante la sesión del usuario.
 */
class AsistenteRepositoryImpl(
    private val n8nApiClient: N8nApiClient = N8nApiClient()
) : AsistenteRepository {

    // Identificador único de sesión persistente durante la ejecución de la app
    private val sessionId: String = nuevoUuid()

    private val mensajeBienvenida = MensajeAsistente(
        id = "msg-bienvenida-0",
        texto = "Hola. Conozco tu hogar: tanque de 1100 L, 4 personas, sector Ciudad Nueva. ¿En qué te ayudo?",
        esUsuario = false,
        timestamp = Clock.System.now().toEpochMilliseconds()
    )

    private val _mensajes = MutableStateFlow<List<MensajeAsistente>>(listOf(mensajeBienvenida))

    override fun observarMensajes(): Flow<List<MensajeAsistente>> = _mensajes.asStateFlow()

    override suspend fun obtenerHistorial(): List<MensajeAsistente> = _mensajes.value

    override suspend fun enviarMensaje(texto: String): Result<MensajeAsistente> {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        val mensajeUsuario = MensajeAsistente(
            id = nuevoUuid(),
            texto = texto,
            esUsuario = true,
            timestamp = timestamp
        )

        // Se agrega inmediatamente el mensaje del usuario para máxima fluidez
        _mensajes.value = _mensajes.value + mensajeUsuario

        return try {
            val respuestaTexto = n8nApiClient.enviarPregunta(
                pregunta = texto,
                sessionId = sessionId,
                timestamp = timestamp
            )

            val mensajeAsistente = MensajeAsistente(
                id = nuevoUuid(),
                texto = respuestaTexto,
                esUsuario = false,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )

            _mensajes.value = _mensajes.value + mensajeAsistente
            Result.success(mensajeAsistente)
        } catch (e: Exception) {
            val mensajeError = MensajeAsistente(
                id = nuevoUuid(),
                texto = "No se pudo comunicar con el asistente (n8n): ${e.message ?: "Error de red"}. Por favor verifica tu conexión o el webhook configurado.",
                esUsuario = false,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                esError = true
            )
            _mensajes.value = _mensajes.value + mensajeError
            Result.failure(e)
        }
    }

    override suspend fun limpiarConversacion() {
        _mensajes.value = listOf(mensajeBienvenida)
    }
}
