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
        // Si aún no se ha configurado la URL real de n8n, provee una respuesta interactiva de muestra
        if (!ConfiguracionN8n.estaConfigurado) {
            return generarRespuestaDemostracion(pregunta)
        }

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
        if (limpio.isBlank()) return "El asistente no devolvió texto de respuesta."

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

    private fun generarRespuestaDemostracion(pregunta: String): String {
        val q = pregunta.lowercase()
        return when {
            q.contains("recibo") || q.contains("doble") || q.contains("caro") || q.contains("subió") ->
                "Tu consumo puede deberse a una fuga interna o un cobro atípico de EPS Tacna. Puedes comparar tu consumo con meses anteriores en la pestaña de Recibos y verificar si supera el 100% de tu promedio para iniciar un reclamo formal ante Sunass."

            q.contains("fuga") ->
                "Para detectar fugas invisibles: cierra todos los caños y la llave de paso de tus tanques o cisternas. Luego observa el medidor de agua; si la aguja roja o el reloj siguen girando, tienes una fuga interna que debes reparar de inmediato."

            q.contains("horario") || q.contains("sector") || q.contains("cuándo") || q.contains("cuando") ->
                "En Tacna los horarios de servicio por red varían por distrito y sector (ej. Ciudad Nueva, Alto de la Alianza, Gregorio Albarracín). Puedes revisar los turnos actualizados y cisternas en la pestaña 'Sectores' de la app."

            q.contains("tanque") || q.contains("litro") || q.contains("dura") ->
                "Con un tanque estándar de 1100 L para 4 personas con un consumo moderado de 100 L/habitante al día, la reserva estimada es de aproximadamente 2 a 3 días con uso responsable."

            q.contains("reclamo") || q.contains("dónde") || q.contains("donde") ->
                "Puedes presentar tu reclamo en la sede de EPS Tacna (Av. Dos de Mayo) o a través de su plataforma virtual, adjuntando la foto de tu medidor y recibo dentro de los primeros 60 días desde la emisión."

            else ->
                "He recibido tu pregunta: \"$pregunta\".\n\nEl módulo de asistente está completamente operativo y conectado a la arquitectura de la app. Para recibir respuestas directamente de tu IA en n8n, actualiza `WEBHOOK_URL` en `ConfiguracionN8n.kt` con la URL de tu flujo."
        }
    }
}
