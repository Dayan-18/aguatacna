package pe.edu.upt.aguatacna.feature.recibo

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo

// Seis meses de recibos de ejemplo (mar–ago 2026). Agosto supera los 100 m³ para probar el Alto consumo.
object SemillaRecibos {

    private val consumos = listOf(3 to 16, 4 to 16, 5 to 15, 6 to 17, 7 to 16, 8 to 120)

    fun generarRecibos(): List<Recibo> = consumos.map { (mes, consumo) ->
        Recibo(
            id = "seed-$mes",
            periodoConsumo = PeriodoConsumo(2026, mes),
            consumoM3 = consumo,
            importeTotal = Dinero(consumo * 300L),
            fechaEmision = LocalDate(2026, mes, 28),
            fechaVencimiento = LocalDate(2026, mes + 1, 11),
            tipoConsumo = TipoConsumo.LECTURA,
            origen = OrigenDatos.MANUAL
        )
    }
}
