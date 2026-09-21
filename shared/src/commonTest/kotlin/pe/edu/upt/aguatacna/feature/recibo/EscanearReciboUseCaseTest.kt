package pe.edu.upt.aguatacna.feature.recibo

import pe.edu.upt.aguatacna.feature.recibo.domain.model.LineaTexto
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TextoReconocido
import pe.edu.upt.aguatacna.feature.recibo.domain.port.ReconocedorTexto
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.EscanearReciboUseCase
import pe.edu.upt.aguatacna.feature.reserva.ejecutar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EscanearReciboUseCaseTest {

    private class FakeReconocedor(
        var respuesta: Result<TextoReconocido>
    ) : ReconocedorTexto {
        override suspend fun reconocer(bytesImagen: ByteArray): Result<TextoReconocido> = respuesta
    }

    @Test
    fun fotoVaciaDevuelveError() {
        val fake = FakeReconocedor(Result.success(TextoReconocido("Texto")))
        val useCase = EscanearReciboUseCase(fake)

        val resultado = ejecutar { useCase(byteArrayOf()) }
        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull()?.message?.contains("vacía") == true)
    }

    @Test
    fun imagenSinTextoDevuelveErrorEntendible() {
        val fake = FakeReconocedor(Result.success(TextoReconocido("   ", emptyList())))
        val useCase = EscanearReciboUseCase(fake)

        val resultado = ejecutar { useCase(byteArrayOf(1, 2, 3)) }
        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull()?.message?.contains("No se detectó ningún texto") == true)
    }

    @Test
    fun excepcionDelOcrDevuelveFailure() {
        val fake = FakeReconocedor(Result.failure(RuntimeException("Error en sensor de cámara")))
        val useCase = EscanearReciboUseCase(fake)

        val resultado = ejecutar { useCase(byteArrayOf(1, 2, 3)) }
        assertTrue(resultado.isFailure)
        assertEquals("Error en sensor de cámara", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun ocrExitosoDevuelveBorradorValido() {
        val textoEsperado = TextoReconocido(
            textoPlano = """
                EPS TACNA S.A.
                Consumo: AGOSTO-2026
                Volumen Fac m³ 23
                TOTAL A PAGAR: S/ 78.00
            """.trimIndent(),
            lineas = listOf(
                LineaTexto("EPS TACNA S.A.", 10f, 20f, 100f, 30f),
                LineaTexto("Consumo: AGOSTO-2026", 10f, 60f, 150f, 30f)
            )
        )
        val fake = FakeReconocedor(Result.success(textoEsperado))
        val useCase = EscanearReciboUseCase(fake)

        val resultado = ejecutar { useCase(byteArrayOf(1, 2, 3)) }
        assertTrue(resultado.isSuccess)
        val borrador = resultado.getOrNull()
        assertEquals(23, borrador?.consumoM3?.valor)
        assertEquals(7800L, borrador?.importeTotal?.valor?.centimos)
        assertTrue(borrador?.esConfirmable == true)
    }
}
