package pe.edu.upt.aguatacna.feature.recibo

import pe.edu.upt.aguatacna.feature.recibo.presentation.TECLA_BORRAR
import pe.edu.upt.aguatacna.feature.recibo.presentation.aplicarTecla
import kotlin.test.Test
import kotlin.test.assertEquals

class AplicarTeclaTest {

    private fun importe(entrada: String, tecla: String) = aplicarTecla(entrada, tecla, permiteComa = true, maxDigitos = 7)
    private fun consumo(entrada: String, tecla: String) = aplicarTecla(entrada, tecla, permiteComa = false, maxDigitos = 3)

    @Test
    fun agregaYBorraDigitos() {
        assertEquals("12", consumo("1", "2"))
        assertEquals("1", consumo("12", TECLA_BORRAR))
        assertEquals("", consumo("", TECLA_BORRAR))
    }

    @Test
    fun respetaElMaximoDeDigitosYReemplazaElCeroInicial() {
        assertEquals("999", consumo("999", "1"))
        assertEquals("5", consumo("0", "5"))
    }

    @Test
    fun laComaSoloSeAdmiteUnaVezYConDosDecimales() {
        assertEquals("0,", importe("", ","))
        assertEquals("78,", importe("78", ","))
        assertEquals("78,2", importe("78,2", ","))
        assertEquals("78,29", importe("78,2", "9"))
        assertEquals("78,29", importe("78,29", "1"))
        assertEquals("78", consumo("78", ","))
    }
}
