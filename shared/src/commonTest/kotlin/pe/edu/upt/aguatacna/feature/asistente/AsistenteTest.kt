package pe.edu.upt.aguatacna.feature.asistente

import kotlinx.coroutines.runBlocking
import pe.edu.upt.aguatacna.feature.asistente.data.AsistenteRepositoryImpl
import pe.edu.upt.aguatacna.feature.asistente.data.remote.N8nApiClient
import pe.edu.upt.aguatacna.feature.asistente.domain.usecase.EnviarMensajeUseCase
import pe.edu.upt.aguatacna.feature.asistente.domain.usecase.LimpiarConversacionUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AsistenteTest {

    private val apiClient = N8nApiClient()

    @Test
    fun extraerTextoDeRespuesta_soportaFormatoOutput() {
        val jsonOutput = """{"output": "Hola, soy tu asistente de agua"}"""
        val resultado = apiClient.extraerTextoDeRespuesta(jsonOutput)
        assertEquals("Hola, soy tu asistente de agua", resultado)
    }

    @Test
    fun extraerTextoDeRespuesta_soportaFormatoResponse() {
        val jsonResponse = """{"response": "Revisé tu consumo de agua"}"""
        val resultado = apiClient.extraerTextoDeRespuesta(jsonResponse)
        assertEquals("Revisé tu consumo de agua", resultado)
    }

    @Test
    fun extraerTextoDeRespuesta_soportaArrayEstandarN8n() {
        val jsonArray = """[{"output": "Respuesta dentro de un array de n8n"}]"""
        val resultado = apiClient.extraerTextoDeRespuesta(jsonArray)
        assertEquals("Respuesta dentro de un array de n8n", resultado)
    }

    @Test
    fun extraerTextoDeRespuesta_soportaTextoPlano() {
        val textoPlano = "Texto plano directo desde n8n"
        val resultado = apiClient.extraerTextoDeRespuesta(textoPlano)
        assertEquals("Texto plano directo desde n8n", resultado)
    }

    @Test
    fun enviarMensajeUseCase_rechazaMensajesEnBlanco() {
        runBlocking {
            val repo = AsistenteRepositoryImpl(apiClient)
            val useCase = EnviarMensajeUseCase(repo)

            val resultado = useCase("   ")
            assertTrue(resultado.isFailure)
        }
    }

    @Test
    fun flujoMensajes_agregaMensajeUsuarioYRespuesta() {
        runBlocking {
            val repo = AsistenteRepositoryImpl(apiClient)
            val useCase = EnviarMensajeUseCase(repo)

            val iniciales = repo.obtenerHistorial()
            assertEquals(1, iniciales.size)
            assertFalse(iniciales[0].esUsuario)

            val resultado = useCase("¿Cómo detectar una fuga?")
            assertTrue(resultado.isSuccess)

            val despues = repo.obtenerHistorial()
            assertEquals(3, despues.size) // Bienvenida + Usuario + Asistente
            assertEquals("¿Cómo detectar una fuga?", despues[1].texto)
            assertTrue(despues[1].esUsuario)
            assertFalse(despues[2].esUsuario)
        }
    }

    @Test
    fun limpiarConversacion_restauraMensajeInicial() {
        runBlocking {
            val repo = AsistenteRepositoryImpl(apiClient)
            val enviarUseCase = EnviarMensajeUseCase(repo)
            val limpiarUseCase = LimpiarConversacionUseCase(repo)

            enviarUseCase("Hola")
            assertEquals(3, repo.obtenerHistorial().size)

            limpiarUseCase()
            val despuesDeLimpiar = repo.obtenerHistorial()
            assertEquals(1, despuesDeLimpiar.size)
            assertFalse(despuesDeLimpiar[0].esUsuario)
        }
    }
}
