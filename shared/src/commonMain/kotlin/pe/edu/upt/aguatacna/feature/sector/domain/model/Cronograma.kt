package pe.edu.upt.aguatacna.feature.sector.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime

enum class TipoCronograma { PROGRAMADO, EMERGENCIA }

enum class FuenteCronograma { EPS, COLABORATIVA }

data class Cronograma(
    val id: String,
    val sectorId: String,
    val fecha: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val tipo: TipoCronograma,
    val fuente: FuenteCronograma
) {
    init {
        require(horaInicio < horaFin) { "El abastecimiento debe empezar antes de terminar" }
    }

    val inicio: LocalDateTime get() = fecha.atTime(horaInicio)
    val fin: LocalDateTime get() = fecha.atTime(horaFin)

    val duracionMinutos: Int
        get() = (horaFin.toSecondOfDay() - horaInicio.toSecondOfDay()) / 60

    fun contiene(momento: LocalDateTime): Boolean = momento >= inicio && momento < fin
}
