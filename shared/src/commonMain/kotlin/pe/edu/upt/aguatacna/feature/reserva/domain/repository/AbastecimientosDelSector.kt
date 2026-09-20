package pe.edu.upt.aguatacna.feature.reserva.domain.repository

import kotlinx.datetime.LocalDateTime

/** Lo único que la reserva necesita del sector: cuándo empezó cada abastecimiento. */
interface AbastecimientosDelSector {
    suspend fun iniciosHasta(ahora: LocalDateTime): List<LocalDateTime>
}
