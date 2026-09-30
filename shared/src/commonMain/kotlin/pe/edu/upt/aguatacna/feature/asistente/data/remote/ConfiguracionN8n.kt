package pe.edu.upt.aguatacna.feature.asistente.data.remote

/**
 * Conexión con el Webhook de n8n (método POST).
 *
 * Cuerpo que se envía:
 * {"chatInput": "...", "message": "...", "sessionId": "...", "timestamp": 1727280000000}
 *
 * Respuestas que se entienden: {"output": "..."}, {"response"|"text"|"message": "..."},
 * [{"output": "..."}] o texto plano.
 */
object ConfiguracionN8n {

    /** Pega aquí la URL del Webhook de tu flujo de n8n (Test URL o Production URL). */
    var WEBHOOK_URL: String = ""

    /** Token opcional si el Webhook usa Header Auth o Bearer; vacío si no requiere autenticación. */
    var AUTH_TOKEN: String = ""

    var AUTH_HEADER_KEY: String = "Authorization"

    /** Prefijo del token (ej. "Bearer "). Vacío si se envía el token directo. */
    var AUTH_PREFIX: String = "Bearer "
}
