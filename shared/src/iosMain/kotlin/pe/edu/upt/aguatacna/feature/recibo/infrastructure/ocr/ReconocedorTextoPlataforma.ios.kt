package pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr

import pe.edu.upt.aguatacna.feature.recibo.domain.model.TextoReconocido
import pe.edu.upt.aguatacna.feature.recibo.domain.port.ReconocedorTexto

actual fun crearReconocedorTexto(): ReconocedorTexto = ReconocedorTextoIos()

// ML Kit no tiene versión para Kotlin/Native, y el target iOS sigue siendo de paridad parcial
// (constitución, sección X). Fallar con un mensaje claro lleva al usuario a la pantalla de error,
// que ya ofrece "Ingresar datos a mano". La opción futura es Apple Vision (VNRecognizeTextRequest),
// que también reconoce texto en el dispositivo y sin internet.
private class ReconocedorTextoIos : ReconocedorTexto {
    override suspend fun reconocer(bytesImagen: ByteArray): Result<TextoReconocido> =
        Result.failure(
            UnsupportedOperationException(
                "En iOS aún no se puede leer el recibo con la cámara. Ingresa los datos a mano."
            )
        )
}
