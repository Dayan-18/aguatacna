package pe.edu.upt.aguatacna.feature.asistente

import kotlinx.coroutines.runBlocking
import pe.edu.upt.aguatacna.feature.asistente.data.AsistenteRepositoryImpl
import pe.edu.upt.aguatacna.feature.asistente.data.remote.N8nApiClient
import pe.edu.upt.aguatacna.feature.asistente.domain.usecase.EnviarMensajeUseCase
import pe.edu.upt.aguatacna.feature.asistente.domain.usecase.LimpiarConversacionUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AsistenteTest {

    private val apiClient = N8nApiClient()

    @Test
    fun extraerTextoDeRespuesta_soportaFormatoOutput() {
        assertEquals("Hola", apiClient.extraerTextoDeRespuesta("""{"output": "Hola"}"""))
    }

    @Test
    fun extraerTextoDeRespuesta_soportaFormatoResponse() {
        assertEquals("Revisé tu consumo", apiClient.extraerTextoDeRespuesta("""{"response": "Revisé tu consumo"}"""))
    }

    @Test
    fun extraerTextoDeRespuesta_soportaArrayEstandarN8n() {
        assertEquals("En un array", apiClient.extraerTextoDeRespuesta("""[{"output": "En un array"}]"""))
    }

    @Test
    fun extraerTextoDeRespuesta_soportaTextoPlano() {
        assertEquals("Texto plano", apiClient.extraerTextoDeRespuesta("Texto plano"))
    }

    @Test
    fun extraerTextoDeRespuesta_vacioEsError() {
        assertFailsWith<IllegalStateException> { apiClient.extraerTextoDeRespuesta("  ") }
    }

    @Test
    fun laConversacionEmpiezaVacia() = runBlocking {
        assertTrue(AsistenteRepositoryImpl(apiClient).obtenerHistorial().isEmpty())
    }

    @Test
    fun enviarMensajeUseCase_rechazaMensajesEnBlanco() = runBlocking {
        val repo = AsistenteRepositoryImpl(apiClient)
        assertTrue(EnviarMensajeUseCase(repo)("   ").isFailure)
        assertTrue(repo.obtenerHistorial().isEmpty())
    }

    @Test
    fun sinWebhookNoHayRespuestaInventada() = runBlocking {
        val repo = AsistenteRepositoryImpl(apiClient)

        val resultado = EnviarMensajeUseCase(repo)("Hola")

        assertTrue(resultado.isFailure)
        val historial = repo.obtenerHistorial()
        assertEquals(2, historial.size)
        assertEquals("Hola", historial[0].texto)
        assertTrue(historial[0].esUsuario)
        assertTrue(historial[1].esError)
    }

    @Test
    fun limpiarConversacion_dejaElChatVacio() = runBlocking {
        val repo = AsistenteRepositoryImpl(apiClient)
        EnviarMensajeUseCase(repo)("Hola")

        LimpiarConversacionUseCase(repo)()

        assertTrue(repo.obtenerHistorial().isEmpty())
    }
}
