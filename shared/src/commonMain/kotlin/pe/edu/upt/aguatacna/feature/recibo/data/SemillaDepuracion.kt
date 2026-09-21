package pe.edu.upt.aguatacna.feature.recibo.data

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Recibo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.TipoConsumo

/**
 * Semilla de depuración (T-2.3).
 *
 * Carga 6 meses de datos de ejemplo para poder ver los estados sin escanear.
 * Activar/desactivar con [DEBUG_SEED_ENABLED].
 *
 * **Se elimina en T-12.2.**
 */
object SemillaDepuracion {

    /** Activar para cargar datos de ejemplo al iniciar (desactivado para producción T-12.2). */
    const val DEBUG_SEED_ENABLED = false

    /**
     * Genera 6 meses de recibos de ejemplo.
     *
     * Consumos: Mar=16, Abr=16, May=15, Jun=17, Jul=16, Ago=33
     * Promedio de los 5 primeros: (16+16+15+17+16)/5 = 16
     * Agosto 33 vs promedio 16 → exceso 106% → Atípico ✔
     */
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
