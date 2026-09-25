package pe.edu.upt.aguatacna.feature.recibo.domain.port

import pe.edu.upt.aguatacna.feature.recibo.domain.model.TextoReconocido

// Puerto de OCR: implementado en androidMain con Google ML Kit Text Recognition.
interface ReconocedorTexto {
    suspend fun reconocer(bytesImagen: ByteArray): Result<TextoReconocido>
}
