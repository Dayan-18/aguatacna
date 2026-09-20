package pe.edu.upt.aguatacna.feature.asistente.domain.repository

import pe.edu.upt.aguatacna.feature.asistente.domain.model.MensajeAsistente

interface AsistenteRepository {
    suspend fun obtenerHistorial(): List<MensajeAsistente>
    suspend fun enviarMensaje(texto: String): MensajeAsistente
}
