package pe.edu.upt.aguatacna.feature.reserva.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfiguracionHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConsumoHorario
import pe.edu.upt.aguatacna.feature.reserva.domain.model.LitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PerfilHogar
import pe.edu.upt.aguatacna.feature.reserva.domain.model.PrevisualizacionSinAgua
import pe.edu.upt.aguatacna.feature.reserva.domain.model.Reserva
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado

interface ReservaRepository {

    /** El hogar configurado; emite `null` hasta que el usuario complete la configuración inicial. */
    fun observarPerfil(): Flow<PerfilHogar?>

    /** Guarda la configuración; al cambiarla se descarta el consumo aprendido, que ya no corresponde. */
    suspend fun guardarPerfil(configuracion: ConfiguracionHogar, consumoPorHabitos: ConsumoHorario?): Result<Unit>

    /** La reserva vigente; emite `null` mientras el hogar no tenga ningún llenado ni abastecimiento. */
    fun observarReserva(): Flow<Reserva?>

    /** Indicador para el posicionamiento por sector; `null` si aún no hay datos suficientes. */
    suspend fun litrosPorHabitanteDia(): LitrosPorHabitanteDia?

    suspend fun registrarLlenado(momento: LocalDateTime, tipo: TipoLlenado): Result<Unit>

    /** El agua no llegó al sector en la ventana vigente en `momento`. */
    suspend fun registrarSinLlegada(momento: LocalDateTime): Result<Unit>

    suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit>

    /** Lo que cambiaría al declarar que se quedó sin agua en `momento`, sin guardar nada. */
    suspend fun previsualizarSinAgua(momento: LocalDateTime): Result<PrevisualizacionSinAgua>
}
