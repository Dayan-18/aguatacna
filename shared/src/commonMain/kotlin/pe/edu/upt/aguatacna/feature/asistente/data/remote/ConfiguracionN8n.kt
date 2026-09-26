package pe.edu.upt.aguatacna.feature.asistente.data.remote

/**
 * Configuración para la conexión con el Webhook de n8n.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * GUÍA DE CONFIGURACIÓN N8N:
 * ─────────────────────────────────────────────────────────────────────────────
 * 1. En tu flujo de n8n, añade un nodo "Webhook" o "n8n Chat Trigger":
 *    - Método HTTP: POST
 *    - Ruta: por ejemplo `/webhook/asistente-agua`
 *    - Response Mode: "Using 'Respond to Webhook' Node" (o última respuesta)
 *
 * 2. Copia la URL del Webhook generada por n8n (Test URL o Production URL).
 *
 * 3. Pega tu URL en la variable [WEBHOOK_URL] abajo.
 *
 * DATOS ENVIADOS A N8N (JSON en el Body):
 * {
 *   "chatInput": "pregunta del usuario",
 *   "message": "pregunta del usuario",
 *   "sessionId": "id-unico-de-sesion",
 *   "timestamp": 1727280000000
 * }
 *
 * RESPUESTA ESPERADA DE N8N:
 * La aplicación interpreta cualquiera de los formatos comunes de n8n:
 *  - {"output": "texto de respuesta de la IA"}
 *  - {"response": "texto de respuesta"} o {"text": "..."} o {"message": "..."}
 *  - [{"output": "texto"}] (formato de array estándar de n8n)
 *  - Texto plano directo en el body
 * ─────────────────────────────────────────────────────────────────────────────
 */
object ConfiguracionN8n {

    /**
     * URL del Webhook de tu flujo de n8n.
     * MODIFICA ESTA VARIABLE con la URL de tu webhook de n8n.
     */
    var WEBHOOK_URL: String = "https://ejemplo-n8n.com/webhook/asistente-agua"

    /**
     * Token opcional si tu nodo Webhook en n8n usa Header Auth o Bearer Token.
     * Si no requiere autenticación, déjalo vacío ("").
     */
    var AUTH_TOKEN: String = ""

    /**
     * Nombre del encabezado HTTP para la autenticación si se requiere.
     * Por defecto "Authorization".
     */
    var AUTH_HEADER_KEY: String = "Authorization"

    /**
     * Prefijo del token (ej. "Bearer "). Dejar vacío si solo se envía el token directo.
     */
    var AUTH_PREFIX: String = "Bearer "

    /**
     * Determina si el webhook ya tiene configurada una URL real y no el placeholder inicial.
     */
    val estaConfigurado: Boolean
        get() = WEBHOOK_URL.isNotBlank() &&
                !WEBHOOK_URL.contains("ejemplo-n8n.com") &&
                !WEBHOOK_URL.contains("tu-instancia")
}
