package pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr

import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import pe.edu.upt.aguatacna.feature.recibo.domain.model.LineaTexto
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TextoReconocido
import pe.edu.upt.aguatacna.feature.recibo.domain.port.ReconocedorTexto
import kotlin.coroutines.resume

/**
 * Implementación de [ReconocedorTexto] para Android usando Google ML Kit Text Recognition (T-4.2).
 *
 * Utiliza el modelo de alfabeto latino: totalmente local, gratuito y sin necesidad de internet.
 */
class ReconocedorTextoAndroid : ReconocedorTexto {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    override suspend fun reconocer(bytesImagen: ByteArray): Result<TextoReconocido> {
        if (bytesImagen.isEmpty()) {
            return Result.failure(IllegalArgumentException("Bytes de imagen vacíos."))
        }

        val bitmap = BitmapFactory.decodeByteArray(bytesImagen, 0, bytesImagen.size)
            ?: return Result.failure(IllegalArgumentException("No se pudo decodificar la imagen."))

        val image = InputImage.fromBitmap(bitmap, 0)

        return suspendCancellableCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val lineas = visionText.textBlocks.flatMap { block ->
                        block.lines.map { line ->
                            val box = line.boundingBox
                            LineaTexto(
                                texto = line.text,
                                x = box?.left?.toFloat() ?: 0f,
                                y = box?.top?.toFloat() ?: 0f,
                                ancho = box?.width()?.toFloat() ?: 0f,
                                alto = box?.height()?.toFloat() ?: 0f
                            )
                        }
                    }
                    val resultado = TextoReconocido(
                        textoPlano = visionText.text,
                        lineas = lineas
                    )
                    continuation.resume(Result.success(resultado))
                }
                .addOnFailureListener { error ->
                    continuation.resume(Result.failure(error))
                }
        }
    }
}
