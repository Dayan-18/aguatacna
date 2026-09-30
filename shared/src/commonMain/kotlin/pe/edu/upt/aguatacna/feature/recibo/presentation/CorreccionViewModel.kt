package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.util.Reloj
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.CorregirCampoUseCase
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.CorregirCampoUseCase.CampoEditable
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.ResultadoCorreccion
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.TipoCampoEdicion

// Pantalla de corrección: traduce lo que el usuario escribió a un campo del borrador y lo corrige.
class CorreccionViewModel(
    private val borradorStore: BorradorReciboStore,
    private val corregirCampo: CorregirCampoUseCase,
    reloj: Reloj
) : ViewModel() {

    val periodoActual: PeriodoConsumo = PeriodoConsumo.de(reloj.ahora().date)
    val borrador: StateFlow<ReciboBorrador?> = borradorStore.borrador

    init {
        viewModelScope.launch { borradorStore.asegurar { ReciboBorrador.vacio(periodoActual) } }
    }

    fun valorInicial(campo: TipoCampoEdicion): String {
        val actual = borrador.value ?: return ""
        return when (campo) {
            TipoCampoEdicion.CONSUMO_M3 -> actual.consumoM3.valor.enTexto()
            TipoCampoEdicion.LECTURA_ANTERIOR -> actual.lecturaAnteriorM3.valor.enTexto()
            TipoCampoEdicion.LECTURA_ACTUAL -> actual.lecturaActualM3.valor.enTexto()
            TipoCampoEdicion.IMPORTE -> actual.importeTotal.valor?.takeIf { it != Dinero.CERO }
                ?.let { "${it.soles},${it.centavos.toString().padStart(2, '0')}" }.orEmpty()
            TipoCampoEdicion.PERIODO -> actual.periodoConsumo.valor?.displayCompleto.orEmpty()
        }
    }

    fun periodoInicial(): PeriodoConsumo = borrador.value?.periodoConsumo?.valor ?: periodoActual

    /** Devuelve el mensaje de error, o null si la corrección se aplicó. */
    fun guardar(campo: TipoCampoEdicion, entrada: String, periodo: PeriodoConsumo): String? {
        val actual = borrador.value ?: return "Aún estamos cargando tu recibo. Intenta de nuevo."
        val editable = when (campo) {
            TipoCampoEdicion.CONSUMO_M3 -> CampoEditable.ConsumoM3(entrada.toIntOrNull())
            TipoCampoEdicion.LECTURA_ANTERIOR -> CampoEditable.LecturaAnterior(entrada.toIntOrNull())
            TipoCampoEdicion.LECTURA_ACTUAL -> CampoEditable.LecturaActual(entrada.toIntOrNull())
            TipoCampoEdicion.IMPORTE -> CampoEditable.Importe(Dinero.parsear(entrada))
            TipoCampoEdicion.PERIODO -> CampoEditable.Periodo(periodo)
        }
        return when (val resultado = corregirCampo(actual, editable)) {
            is ResultadoCorreccion.Aplicada -> {
                borradorStore.guardar(resultado.borrador)
                null
            }
            is ResultadoCorreccion.Invalida -> resultado.mensaje
        }
    }

    private fun Int?.enTexto(): String = this?.takeIf { it != 0 }?.toString().orEmpty()

    companion object {
        fun desdeInyeccion(): CorreccionViewModel {
            val koin = KoinPlatform.getKoin()
            return CorreccionViewModel(koin.get(), koin.get(), koin.get())
        }
    }
}

/** Aplica una tecla del teclado en pantalla a lo que el usuario lleva escrito. */
fun aplicarTecla(entrada: String, tecla: String, permiteComa: Boolean, maxDigitos: Int): String = when {
    tecla == TECLA_BORRAR -> entrada.dropLast(1)
    tecla == "," -> if (permiteComa && ',' !in entrada) entrada.ifEmpty { "0" } + "," else entrada
    permiteComa && entrada.substringAfter(',', "").length >= 2 -> entrada
    entrada == "0" -> tecla
    entrada.length < maxDigitos -> entrada + tecla
    else -> entrada
}

const val TECLA_BORRAR = "←"
