package pe.edu.upt.aguatacna.feature.reserva.data.mapper

import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.data.local.EventoLlenadoEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.NovedadReservaEntity
import pe.edu.upt.aguatacna.feature.reserva.data.local.PerfilHogarEntity
import pe.edu.upt.aguatacna.feature.reserva.domain.model.CapacidadLitros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ClaseIntervalo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.DatosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EventoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Habitantes
import pe.edu.upt.aguatacna.feature.reserva.domain.model.HabitosDelHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.IntervaloConsumo
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Litros
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PerfilHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoReservorio
import pe.edu.upt.aguatacna.feature.reserva.domain.usecase.HistorialReserva

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

fun PerfilHogar.aDatosDelHogar() = DatosDelHogar(capacidad, habitantes, consumoPorHabitos)

fun aHistorial(
    perfil: PerfilHogar,
    llenados: List<EventoLlenadoEntity>,
    novedades: List<NovedadReservaEntity>
): HistorialReserva {
    val sinAgua = novedades.filter { it.tipo == NovedadReservaEntity.SIN_AGUA }
    return HistorialReserva(
        llenados = llenados.map { it.aDominio() },
        sinLlegada = novedades.filter { it.tipo == NovedadReservaEntity.SIN_LLEGADA }.map { LocalDateTime.parse(it.momento) },
        observados = sinAgua.mapNotNull { it.aIntervaloObservado() },
        agotadaEn = sinAgua.maxOfOrNull { LocalDateTime.parse(it.momento) },
        consumoVigente = perfil.consumoVigente
    )
}

private fun NovedadReservaEntity.aIntervaloObservado(): IntervaloConsumo? {
    if (inicioObservado == null || litrosObservados == null) return null
    return IntervaloConsumo(
        LocalDateTime.parse(inicioObservado), LocalDateTime.parse(momento), Litros(litrosObservados), ClaseIntervalo.OBSERVADO
    )
}

fun IntervaloConsumo?.comoNovedad(id: String, usuarioId: String, momento: LocalDateTime) = NovedadReservaEntity(
    id = id,
    usuarioId = usuarioId,
    momento = momento.toString(),
    tipo = NovedadReservaEntity.SIN_AGUA,
    inicioObservado = this?.inicio?.toString(),
    litrosObservados = this?.litrosConsumidos?.valor
)
