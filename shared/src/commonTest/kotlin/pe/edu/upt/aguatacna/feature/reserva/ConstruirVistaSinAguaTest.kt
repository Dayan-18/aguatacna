package pe.edu.upt.aguatacna.feature.reserva

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PrevisualizacionSinAgua
import pe.edu.upt.aguatacna.feature.reserva.presentation.ConstruirVistaSinAgua
import pe.edu.upt.aguatacna.feature.reserva.presentation.describirHora
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstruirVistaSinAguaTest {
    private val construir = ConstruirVistaSinAgua()
    private val hoy = LocalDateTime(2026, 9, 19, 15, 20)

    private fun previsualizacion(
        proyectado: LocalDateTime = LocalDateTime(2026, 9, 19, 18, 40),
        momento: LocalDateTime = hoy,
        antes: Double = 82.0,
        despues: Double = 96.0
    ) = PrevisualizacionSinAgua(proyectado, momento, ConsumoHorario(antes), ConsumoHorario(despues))

    @Test
    fun elEjemploDelFigmaSeMuestraIgual() {
        val vista = construir(previsualizacion())
        assertEquals("6:40 p.m.", vista.textoProyectado)
        assertEquals("3:20 p.m.", vista.textoSeAcabo)
        assertEquals("3 h 20 min antes", vista.textoDiferencia)
        assertEquals("82 → 96 L/h", vista.textoConsumo)
        assertTrue(vista.seAcaboAntes)
    }

    @Test
    fun siSeAcabaraJustoCuandoSeProyectabaDiceJustoATiempo() {
        val vista = construir(previsualizacion(proyectado = hoy))
        assertEquals("justo a tiempo", vista.textoDiferencia)
        assertFalse(vista.seAcaboAntes)
    }

    @Test
    fun siDuroMasDeLoPrevistoLoDice() {
        val vista = construir(previsualizacion(proyectado = LocalDateTime(2026, 9, 19, 14, 50)))
        assertEquals("30 min después", vista.textoDiferencia)
        assertFalse(vista.seAcaboAntes)
    }

    @Test
    fun sinCambioDeConsumoNoMuestraFlecha() {
        assertEquals("82 L/h", construir(previsualizacion(despues = 82.0)).textoConsumo)
    }

    @Test
    fun siLoProyectadoCaeEnOtroDiaDiceCual() {
        val vista = construir(previsualizacion(proyectado = LocalDateTime(2026, 9, 20, 2, 0)))
        assertEquals("mañana 2:00 a.m.", vista.textoProyectado)
    }

    @Test
    fun describirHoraRedondeaAlMinuto() {
        assertEquals("6:40 p.m.", describirHora(LocalDateTime(2026, 9, 19, 18, 39, 53), hoy))
    }
}
