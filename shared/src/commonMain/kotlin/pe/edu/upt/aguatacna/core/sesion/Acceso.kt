package pe.edu.upt.aguatacna.core.sesion

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/** Cómo entró la persona a la app: solo en el teléfono, o con una cuenta de Google para sincronizar. */
enum class ModoDeAcceso { SIN_CUENTA, GOOGLE }

sealed interface ResultadoInicio {
    data object Exitoso : ResultadoInicio
    data object Cancelado : ResultadoInicio
    data object NoDisponible : ResultadoInicio
    data object SinCuentas : ResultadoInicio
    data class Fallido(val motivo: String) : ResultadoInicio
}

/** Guarda en el teléfono el modo elegido; `null` significa que todavía no eligió. */
interface RegistroDeAcceso {
    fun observar(): Flow<ModoDeAcceso?>
    suspend fun guardar(modo: ModoDeAcceso)

    /** Si ya registró su domicilio: sin sector no hay horario de abastecimiento ni proyección. */
    fun tieneDomicilio(): Flow<Boolean>
}

/** Lo que hace cada plataforma para entrar con Google. Mientras no exista, responde `NoDisponible`. */
fun interface InicioConGoogle {
    suspend fun iniciar(): ResultadoInicio
}

object InicioConGoogleNoDisponible : InicioConGoogle {
    override suspend fun iniciar(): ResultadoInicio = ResultadoInicio.NoDisponible
}

/** Para vistas previas y para cuando la inyección aún no está iniciada. */
class RegistroDeAccesoEnMemoria(inicial: ModoDeAcceso? = null) : RegistroDeAcceso {
    private val modo = MutableStateFlow(inicial)
    override fun observar(): Flow<ModoDeAcceso?> = modo
    override suspend fun guardar(modo: ModoDeAcceso) { this.modo.value = modo }

    // Sin base de datos no hay dónde guardar el sector: se da por registrado para no bloquear la entrada.
    override fun tieneDomicilio(): Flow<Boolean> = flowOf(true)
}
