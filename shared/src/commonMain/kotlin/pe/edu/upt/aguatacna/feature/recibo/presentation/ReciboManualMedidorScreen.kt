package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.*

// Pantalla de corrección manual: permite editar consumo, lecturas, importe y período.
@Composable
fun ReciboManualMedidorScreen(
    campo: TipoCampoEdicion,
    onVolver: () -> Unit,
    viewModel: CorreccionViewModel = viewModel { CorreccionViewModel.desdeInyeccion() }
) {
    val borrador by viewModel.borrador.collectAsStateWithLifecycle()
    val valorDetectado = remember(campo, borrador) { viewModel.valorInicial(campo) }
    var entrada by remember(valorDetectado) { mutableStateOf(valorDetectado) }
    // La primera tecla reemplaza el valor detectado; si no, "72,00" no admite más dígitos y "23" se vuelve "234".
    var reemplazarDetectado by remember(valorDetectado) { mutableStateOf(true) }
    var periodoSeleccionado by remember(borrador) { mutableStateOf(viewModel.periodoInicial()) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    val esPeriodo = campo == TipoCampoEdicion.PERIODO

    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(Fondo).statusBarsPadding()) {
        Column(Modifier.weight(1f)) {
            ReciboBarraSuperior(
                titulo = if (esPeriodo) "Seleccionar período" else "Corregir ${campo.nombre}",
                subtitulo = if (esPeriodo) "Recibo para: ${periodoSeleccionado.displayCompleto}"
                else "Valor actual: ${valorDetectado.ifBlank { null }?.let { "$it ${campo.unidad}" } ?: "Sin datos"}",
                onVolver = onVolver
            )
            if (esPeriodo) {
                SelectorPeriodoMeses(viewModel.periodoActual, periodoSeleccionado) { periodoSeleccionado = it }
            } else {
                DisplayDigitosMedidor(campo, entrada, valorDetectado.ifBlank { "0" }, errorMensaje)
            }
        }
        Column(Modifier.padding(bottom = 32.dp)) {
            if (!esPeriodo) {
                TecladoNumericoMedidor(campo.permiteComa) { tecla ->
                    errorMensaje = null
                    val base = if (reemplazarDetectado && tecla != TECLA_BORRAR) "" else entrada
                    entrada = aplicarTecla(base, tecla, campo.permiteComa, campo.maxDigitos)
                    reemplazarDetectado = false
                }
                Spacer(Modifier.height(20.dp))
            }
            Button(
                onClick = {
                    errorMensaje = viewModel.guardar(campo, entrada, periodoSeleccionado)
                    if (errorMensaje == null) onVolver()
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeBoton)
            ) {
                Text("Guardar corrección", style = estilo(14.sp, FontWeight.SemiBold))
            }
        }
    }
}
