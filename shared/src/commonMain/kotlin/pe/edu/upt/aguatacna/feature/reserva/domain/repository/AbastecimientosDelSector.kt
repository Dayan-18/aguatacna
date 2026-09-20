package pe.edu.upt.aguatacna.feature.reserva.domain.repository

import kotlinx.datetime.LocalDateTime

/** Lo único que la reserva necesita del sector: cuándo empezó y cuándo empieza cada abastecimiento. */
interface AbastecimientosDelSector {
    suspend fun iniciosHasta(ahora: LocalDateTime): List<LocalDateTime>

    /** `null` si el sector no tiene cronograma futuro cargado. */
    suspend fun proximoDesde(ahora: LocalDateTime): LocalDateTime?

    /** Cómo se llama el lugar del usuario, para el saludo de la pantalla; `null` si aún no tiene sector. */
    suspend fun nombreDelSector(): String?
}
