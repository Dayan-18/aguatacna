package pe.edu.upt.aguatacna.feature.sector

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.sector.domain.model.ConfirmacionHorario
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.FuenteCronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoConfirmacion
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoCronograma

val AYER = LocalDate(2026, 9, 17)
val HOY = LocalDate(2026, 9, 18)
val MANANA = LocalDate(2026, 9, 19)
val PASADO = LocalDate(2026, 9, 20)

fun cronograma(fecha: LocalDate, desde: Int, hasta: Int, sectorId: String = "CN-04") = Cronograma(
    id = "$sectorId-$fecha",
    sectorId = sectorId,
    fecha = fecha,
    horaInicio = LocalTime(desde, 0),
    horaFin = LocalTime(hasta, 0),
    tipo = TipoCronograma.PROGRAMADO,
    fuente = FuenteCronograma.EPS
)

fun momento(fecha: LocalDate, hora: Int, minuto: Int = 0) =
    LocalDateTime(fecha, LocalTime(hora, minuto))

fun confirmacion(
    tipo: TipoConfirmacion,
    hora: Int,
    minuto: Int,
    fecha: LocalDate = HOY,
    sectorId: String = "CN-04"
) = ConfirmacionHorario(
    id = "c-$tipo-$hora-$minuto-$sectorId-$fecha",
    sectorId = sectorId,
    usuarioId = "u-$hora$minuto",
    momento = momento(fecha, hora, minuto),
    tipo = tipo
)
