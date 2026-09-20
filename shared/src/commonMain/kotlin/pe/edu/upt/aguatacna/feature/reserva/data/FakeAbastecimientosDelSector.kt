package pe.edu.upt.aguatacna.feature.reserva.data

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector

// Temporal: un abastecimiento diario a la misma hora, hasta conectar el cronograma real de sector (T028).
class FakeAbastecimientosDelSector(private val horaInicio: LocalTime = LocalTime(5, 0)) : AbastecimientosDelSector {

    override suspend fun iniciosHasta(ahora: LocalDateTime): List<LocalDateTime> =
        listOf(ahora.date.minus(1, DateTimeUnit.DAY), ahora.date)
            .map { it.atTime(horaInicio) }
            .filter { it <= ahora }

    override suspend fun proximoDesde(ahora: LocalDateTime): LocalDateTime? =
        listOf(ahora.date, ahora.date.plus(1, DateTimeUnit.DAY))
            .map { it.atTime(horaInicio) }
            .firstOrNull { it > ahora }

    override suspend fun nombreDelSector(): String = "Ciudad Nueva"
}
