package pe.edu.upt.aguatacna.feature.reserva.data.mapper

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.data.local.EventoLlenadoEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.PerfilHogarEntity
import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PerfilHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio

fun PerfilHogar.aEntidad() = PerfilHogarEntity(
    usuarioId = usuarioId,
    tipoReservorio = tipoReservorio.name,
    capacidadLitros = capacidad.litros.valor,
    habitantes = habitantes.cantidad,
    duchasPorDia = habitos.duchasPorDia,
    usaLavadora = habitos.usaLavadora,
    riegaJardin = habitos.riegaJardin,
    consumoPorHabitosLitrosHora = consumoPorHabitos?.litrosPorHora,
    consumoVigenteLitrosHora = consumoVigente?.litrosPorHora
)

fun PerfilHogarEntity.aDominio() = PerfilHogar(
    usuarioId = usuarioId,
    tipoReservorio = TipoReservorio.valueOf(tipoReservorio),
    capacidad = CapacidadLitros.deLitros(capacidadLitros),
    habitantes = Habitantes(habitantes),
    habitos = HabitosDelHogar(duchasPorDia, usaLavadora, riegaJardin),
    consumoPorHabitos = consumoPorHabitosLitrosHora?.let(::ConsumoHorario),
    consumoVigente = consumoVigenteLitrosHora?.let(::ConsumoHorario)
)

fun EventoLlenado.aEntidad(id: String, usuarioId: String) =
    EventoLlenadoEntity(id, usuarioId, momento.toString(), tipo.name)

fun EventoLlenadoEntity.aDominio() = EventoLlenado(LocalDateTime.parse(momento), TipoLlenado.valueOf(tipo))
