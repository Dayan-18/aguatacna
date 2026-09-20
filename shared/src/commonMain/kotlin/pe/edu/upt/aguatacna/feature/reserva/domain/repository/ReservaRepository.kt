package pe.edu.upt.aguatacna.feature.reserva.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.LitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado

interface ReservaRepository {

    /** La reserva vigente; emite `null` mientras el hogar no tenga ningún llenado ni abastecimiento. */
    fun observarReserva(): Flow<Reserva?>

    /** Indicador para el posicionamiento por sector; `null` si aún no hay datos suficientes. */
    suspend fun litrosPorHabitanteDia(): LitrosPorHabitanteDia?

    suspend fun registrarLlenado(momento: LocalDateTime, tipo: TipoLlenado): Result<Unit>

    /** El agua no llegó al sector en la ventana vigente en `momento`. */
    suspend fun registrarSinLlegada(momento: LocalDateTime): Result<Unit>

    suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit>
}
