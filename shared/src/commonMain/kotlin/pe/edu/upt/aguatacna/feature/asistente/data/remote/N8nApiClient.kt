package pe.edu.upt.aguatacna.feature.asistente.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import pe.edu.upt.aguatacna.feature.asistente.data.remote.dto.N8nChatRequest

/**
 * Cliente HTTP responsable de la comunicación directa con el endpoint de n8n.
 */
class N8nApiClient(
    private val httpClient: HttpClient = HttpClient()
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    /**
     * Envía la pregunta del usuario a n8n mediante HTTP POST y devuelve el texto de respuesta.
     */
    suspend fun enviarPregunta(
        pregunta: String,
        sessionId: String,
        timestamp: Long
    ): String {
        check(ConfiguracionN8n.WEBHOOK_URL.isNotBlank()) { "Falta configurar WEBHOOK_URL de n8n." }

        val requestPayload = N8nChatRequest(
            chatInput = pregunta,
            message = pregunta,
            sessionId = sessionId,
            timestamp = timestamp
        )
        val requestJson = json.encodeToString(requestPayload)

        val response = httpClient.post(ConfiguracionN8n.WEBHOOK_URL) {
            contentType(ContentType.Application.Json)
            setBody(requestJson)
            if (ConfiguracionN8n.AUTH_TOKEN.isNotBlank()) {
                val headerValue = "${ConfiguracionN8n.AUTH_PREFIX}${ConfiguracionN8n.AUTH_TOKEN}".trim()
                header(ConfiguracionN8n.AUTH_HEADER_KEY, headerValue)
            }
        }

        val responseBody = response.bodyAsText()

        if (!response.status.isSuccess()) {
            throw IllegalStateException(
                "Respuesta n8n (${response.status.value}): ${responseBody.take(120)}"
            )
        }

        return extraerTextoDeRespuesta(responseBody)
    }

    /**
     * Extrae de forma tolerante el mensaje de cualquier formato que n8n retorne:
     * - {"output": "..."}
     * - {"response": "..."}
     * - {"text": "..."}
     * - [{"output": "..."}]
     * - Texto plano
     */
    fun extraerTextoDeRespuesta(cuerpo: String): String {
        val limpio = cuerpo.trim()
        check(limpio.isNotBlank()) { "n8n respondió sin texto." }

        return try {
            val elemento = json.parseToJsonElement(limpio)
            extraerDesdeElemento(elemento) ?: limpio
        } catch (_: Exception) {
            limpio.removeSurrounding("\"")
        }
    }

    private fun extraerDesdeElemento(elemento: JsonElement): String? {
        return when (elemento) {
            is JsonPrimitive -> elemento.content
            is JsonArray -> elemento.firstOrNull()?.let { extraerDesdeElemento(it) }
            is JsonObject -> {
                val posiblesClaves = listOf(
                    "output", "response", "text", "message", "mensaje",
                    "reply", "content", "resultado", "data"
                )
                for (clave in posiblesClaves) {
                    val valor = elemento[clave]
                    if (valor != null) {
                        val extraido = extraerDesdeElemento(valor)
                        if (!extraido.isNullOrBlank()) return extraido
                    }
                }
                // Si n8n devolvió la estructura clásica {"json": { "output": "..." }}
                elemento["json"]?.let { extraerDesdeElemento(it) }
            }
        }
    }
}
