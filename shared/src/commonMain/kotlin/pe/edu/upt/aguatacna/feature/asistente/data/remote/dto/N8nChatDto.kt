package pe.edu.upt.aguatacna.feature.asistente.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Payload JSON enviado al nodo Webhook de n8n.
 * Incluye tanto `chatInput` (convención del nodo n8n Chat) como `message` (convención habitual de webhooks).
 */
@Serializable
data class N8nChatRequest(
    val chatInput: String,
    val message: String,
    val sessionId: String,
    val timestamp: Long
)

/**
 * DTO para deserializar respuestas con formato JSON de n8n.
 */
@Serializable
data class N8nChatResponse(
    val output: String? = null,
    val response: String? = null,
    val text: String? = null,
    val message: String? = null
)
