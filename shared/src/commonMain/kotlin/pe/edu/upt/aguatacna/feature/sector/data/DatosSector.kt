package pe.edu.upt.aguatacna.feature.sector.data

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.EstadoCisterna
import pe.edu.upt.aguatacna.feature.sector.domain.model.FuenteCronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.PuntoCisterna
import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoCronograma

// Datos de prueba. Las coordenadas son aproximadas y los horarios ilustran
// la diferencia de continuidad entre sectores descrita en el anteproyecto §2.1;
// se reemplazan por la sectorización oficial cuando EPS Tacna la entregue.
val listaSectores = listOf(
    Sector("CN-04", "Ciudad Nueva 04", "Ciudad Nueva", Coordenada(-17.9830, -70.2380)),
    Sector("AA-02", "Alto de la Alianza 02", "Alto de la Alianza", Coordenada(-17.9930, -70.2520)),
    Sector("CE-01", "Cercado 01", "Tacna", Coordenada(-18.0130, -70.2500)),
    Sector("GA-07", "Viñani", "Gregorio Albarracín Lanchipa", Coordenada(-18.0480, -70.2530)),
    Sector("PO-01", "Pocollay 01", "Pocollay", Coordenada(-17.9980, -70.2190)),
    // Sector sin entrada en horarioPorSector: EPS aún no publicó su cronograma.
    // Sirve para el estado "Sector sin horario" (pantalla 20).
    Sector("LG-06", "Leguía 06", "Ciudad Nueva", Coordenada(-17.9700, -70.2600))
)

private val horarioPorSector = mapOf(
    "CN-04" to (LocalTime(5, 0) to LocalTime(9, 0)),
    "AA-02" to (LocalTime(6, 0) to LocalTime(10, 0)),
    "CE-01" to (LocalTime(5, 0) to LocalTime(17, 0)),
    "GA-07" to (LocalTime(7, 0) to LocalTime(13, 0)),
    "PO-01" to (LocalTime(5, 0) to LocalTime(13, 0))
)

// Incluye días pasados: feature/reserva necesita el último abastecimiento ya
// ocurrido para asumir el llenado cuando el usuario no lo registra.
fun cronogramasCercanos(hoy: LocalDate): List<Cronograma> =
    (-DIAS_PASADOS until DIAS_FUTUROS).flatMap { dia ->
        val fecha = hoy.plus(dia, DateTimeUnit.DAY)
        horarioPorSector.map { (sectorId, horario) ->
            Cronograma(
                id = "$sectorId-$fecha",
                sectorId = sectorId,
                fecha = fecha,
                horaInicio = horario.first,
                horaFin = horario.second,
                tipo = TipoCronograma.PROGRAMADO,
                fuente = FuenteCronograma.EPS
            )
        }
    }

private const val DIAS_PASADOS = 3
private const val DIAS_FUTUROS = 7

val listaPuntosCisterna = listOf(
    PuntoCisterna(
        id = "PC-01", sectorId = "GA-07", nombre = "Viñani · Asoc. La Esperanza",
        ubicacion = Coordenada(-18.0495, -70.2548),
        horarioInicio = LocalTime(7, 0), horarioFin = LocalTime(13, 0),
        estado = EstadoCisterna.ACTIVO
    ),
    PuntoCisterna(
        id = "PC-02", sectorId = "AA-02", nombre = "Alto de la Alianza · Plaza",
        ubicacion = Coordenada(-17.9915, -70.2535),
        horarioInicio = LocalTime(8, 0), horarioFin = LocalTime(12, 0),
        estado = EstadoCisterna.EN_RUTA
    ),
    PuntoCisterna(
        id = "PC-03", sectorId = "CN-04", nombre = "Ciudad Nueva · Mercado",
        ubicacion = Coordenada(-17.9845, -70.2365),
        horarioInicio = LocalTime(6, 0), horarioFin = LocalTime(10, 0),
        estado = EstadoCisterna.TERMINADO
    )
)
