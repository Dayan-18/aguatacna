package pe.edu.upt.aguatacna.feature.recibo.data

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo

// Genera 6 meses de recibos de ejemplo para pruebas y vistas previas (sin escanear nada real).
object SemillaDepuracion {

    // Consumos: Mar=16, Abr=16, May=15, Jun=17, Jul=16, Ago=33 (Agosto queda atípico frente al promedio de 16)
    fun generarRecibos(): List<Recibo> {
        val datosEjemplo = listOf(
            Triple(PeriodoConsumo(2026, 3), 16, 4820L),   // Marzo
            Triple(PeriodoConsumo(2026, 4), 16, 4820L),   // Abril
            Triple(PeriodoConsumo(2026, 5), 15, 4520L),   // Mayo
            Triple(PeriodoConsumo(2026, 6), 17, 5120L),   // Junio
            Triple(PeriodoConsumo(2026, 7), 16, 4820L),   // Julio
            Triple(PeriodoConsumo(2026, 8), 33, 7420L),   // Agosto (atípico)
        )

        return datosEjemplo.mapIndexed { index, (periodo, consumo, importeCentimos) ->
            Recibo(
                id = "seed-${index + 1}",
                periodoConsumo = periodo,
                consumoM3 = consumo,
                importeTotal = Dinero(importeCentimos),
                fechaEmision = LocalDate(periodo.anio, periodo.mes, 28),
                fechaVencimiento = if (periodo.mes < 12) {
                    LocalDate(periodo.anio, periodo.mes + 1, 11)
                } else {
                    LocalDate(periodo.anio + 1, 1, 11)
                },
                tipoConsumo = TipoConsumo.LECTURA,
                origen = OrigenDatos.MANUAL
            )
        }
    }
}
