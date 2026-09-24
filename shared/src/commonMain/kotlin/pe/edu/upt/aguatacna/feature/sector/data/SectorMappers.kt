package pe.edu.upt.aguatacna.feature.sector.data

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.feature.sector.data.local.ConfirmacionHorarioEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.CronogramaEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.PuntoCisternaEntity
import pe.edu.upt.aguatacna.feature.sector.data.local.SectorEntity
import pe.edu.upt.aguatacna.feature.sector.domain.model.ConfirmacionHorario
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.EstadoCisterna
import pe.edu.upt.aguatacna.feature.sector.domain.model.FuenteCronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.PuntoCisterna
import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoConfirmacion
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoCronograma

internal fun SectorEntity.aDominio() = Sector(id, nombre, distrito, Coordenada(latitud, longitud))

internal fun CronogramaEntity.aDominio() = Cronograma(
    id = id,
    sectorId = sectorId,
    fecha = LocalDate.parse(fecha),
    horaInicio = LocalTime.parse(horaInicio),
    horaFin = LocalTime.parse(horaFin),
    tipo = TipoCronograma.valueOf(tipo),
    fuente = FuenteCronograma.valueOf(fuente)
)

internal fun PuntoCisternaEntity.aDominio() = PuntoCisterna(
    id = id,
    sectorId = sectorId,
    nombre = nombre,
    ubicacion = Coordenada(latitud, longitud),
    horarioInicio = LocalTime.parse(horarioInicio),
    horarioFin = LocalTime.parse(horarioFin),
    estado = EstadoCisterna.valueOf(estado)
)

internal fun ConfirmacionHorarioEntity.aDominio() = ConfirmacionHorario(
    id = id,
    sectorId = sectorId,
    usuarioId = usuarioId,
    momento = LocalDateTime.parse(momento),
    tipo = TipoConfirmacion.valueOf(tipo)
)

internal fun ConfirmacionHorario.aEntidad() = ConfirmacionHorarioEntity(
    id = id,
    sectorId = sectorId,
    usuarioId = usuarioId,
    momento = momento.toString(),
    tipo = tipo.name
)
