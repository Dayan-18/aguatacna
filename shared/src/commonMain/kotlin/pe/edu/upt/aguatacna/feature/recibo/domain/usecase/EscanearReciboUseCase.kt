package pe.edu.upt.aguatacna.feature.recibo.domain.usecase

import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.port.ReconocedorTexto
import pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr.ParserReciboEpsTacna
import pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr.ResultadoParseo

/**
 * Caso de uso para escanear y analizar un recibo (T-2.2, T-4.3, T-5.1).
 *
 * Flujo: `bytesImagen -> OCR (ReconocedorTexto) -> ParserReciboEpsTacna -> ReciboBorrador`.
 */
class EscanearReciboUseCase(
    private val reconocedor: ReconocedorTexto
) {
    suspend operator fun invoke(bytesImagen: ByteArray): Result<ReciboBorrador> {
        if (bytesImagen.isEmpty()) {
            return Result.failure(IllegalArgumentException("La imagen capturada está vacía."))
        }

        return try {
            val resultadoOcr = reconocedor.reconocer(bytesImagen)
            resultadoOcr.fold(
                onSuccess = { texto ->
                    if (texto.estaVacio) {
                        Result.failure(IllegalStateException("No se detectó ningún texto en la imagen. Intenta con mejor iluminación."))
                    } else {
                        when (val parseo = ParserReciboEpsTacna.parsear(texto)) {
                            is ResultadoParseo.Exito -> Result.success(parseo.borrador)
                            is ResultadoParseo.NoLegible -> Result.failure(IllegalStateException(parseo.motivo))
                        }
                    }
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
