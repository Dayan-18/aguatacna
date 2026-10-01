package pe.edu.upt.aguatacna.feature.retos.domain.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import pe.edu.upt.aguatacna.feature.retos.domain.model.Racha
import pe.edu.upt.aguatacna.feature.retos.domain.model.RetoUsuario

class CalcularRacha {
    fun calcular(cumplimientos: List<RetoUsuario>, hoy: LocalDate): Racha {
        val fechas = cumplimientos.filter { it.cumplido }.map { it.fecha }.toSet()
        var fecha = if (hoy in fechas) hoy else hoy.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
        var dias = 0
        while (fecha in fechas) {
            dias++
            fecha = fecha.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
        }
        return Racha(dias)
    }
}
