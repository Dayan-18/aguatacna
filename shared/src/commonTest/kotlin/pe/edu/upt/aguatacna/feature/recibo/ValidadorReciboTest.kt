package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.service.ValidadorRecibo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidadorReciboTest {

    private fun crearReciboValido(): Recibo = Recibo(
        id = "test-1",
        periodoConsumo = PeriodoConsumo(2026, 8),
        consumoM3 = 33,
        importeTotal = Dinero(7420L),
        fechaEmision = LocalDate(2026, 8, 1),
        fechaVencimiento = LocalDate(2026, 8, 28),
        tipoConsumo = TipoConsumo.LECTURA,
        lecturaAnteriorM3 = 100,
        lecturaActualM3 = 133
    )

    @Test
    fun reciboCoherenteNoProduceAdvertencias() {
        val recibo = crearReciboValido()
        val advertencias = ValidadorRecibo.validar(recibo)
        assertTrue(advertencias.isEmpty(), "Un recibo coherente no debe tener advertencias")
    }

    @Test
    fun advierteConsumoFueraDeRango() {
        val negativo = crearReciboValido().copy(consumoM3 = -5)
        val excesivo = crearReciboValido().copy(consumoM3 = 1500)

        val advNegativo = ValidadorRecibo.validar(negativo)
        assertTrue(advNegativo.any { it.campo == "consumoM3" })

        val advExcesivo = ValidadorRecibo.validar(excesivo)
        assertTrue(advExcesivo.any { it.campo == "consumoM3" })
    }

    @Test
    fun advierteVencimientoAnteriorAEmision() {
        val recibo = crearReciboValido().copy(
            fechaEmision = LocalDate(2026, 8, 28),
            fechaVencimiento = LocalDate(2026, 8, 1)
        )
        val adv = ValidadorRecibo.validar(recibo)
        assertTrue(adv.any { it.campo == "fechaVencimiento" })
    }

    @Test
    fun advierteLecturasIncoherentesConConsumo() {
        // actual - anterior = 133 - 100 = 33, pero el consumo facturado dice 50
        val recibo = crearReciboValido().copy(consumoM3 = 50)
        val adv = ValidadorRecibo.validar(recibo)
        assertTrue(adv.any { it.campo == "consumoM3" })
    }

    @Test
    fun advierteLecturaActualMenorQueAnterior() {
        val recibo = crearReciboValido().copy(
            lecturaAnteriorM3 = 150,
            lecturaActualM3 = 100,
            consumoM3 = -50
        )
        val adv = ValidadorRecibo.validar(recibo)
        assertTrue(adv.any { it.campo == "lecturaActualM3" })
    }
}
