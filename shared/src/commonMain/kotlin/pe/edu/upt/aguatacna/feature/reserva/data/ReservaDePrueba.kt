package pe.edu.upt.aguatacna.feature.reserva.data

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.AbastecimientosDelSector
import pe.edu.upt.aguatacna.feature.reserva.domain.repository.ReservaRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// Temporal: una sola instancia para que la configuración y la reserva compartan estado
// hasta que el core conecte la inyección de dependencias (T028).
@OptIn(ExperimentalTime::class)
object ReservaDePrueba {
    val reloj: () -> LocalDateTime = { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
    val repositorio: ReservaRepository by lazy { FakeReservaRepository.conDatosDeEjemplo(reloj) }
    val sector: AbastecimientosDelSector = FakeAbastecimientosDelSector()
}
