package pe.edu.upt.aguatacna.feature.recibo.domain.port

import pe.edu.upt.aguatacna.feature.recibo.domain.model.TextoReconocido

/**
 * Puerto para el servicio de reconocimiento de texto (OCR) (T-1.5, T-4.1).
 *
 * Se implementa en `androidMain` con Google ML Kit Text Recognition (Fase 4).
 */
interface ReconocedorTexto {
    /**
     * Procesa los bytes de la imagen del recibo y devuelve el texto reconocido.
     */
    suspend fun reconocer(bytesImagen: ByteArray): Result<TextoReconocido>
}
