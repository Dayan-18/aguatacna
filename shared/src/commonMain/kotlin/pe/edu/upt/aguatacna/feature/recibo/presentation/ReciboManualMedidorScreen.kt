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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.*

private val FondoPantalla = Color(0xFFF1F6F8)
private val BotonGuardar = Color(0xFF098093)

// Pantalla de corrección manual: permite editar consumo, lecturas, importe y período.
@Composable
fun ReciboManualMedidorScreen(
    onVolver: () -> Unit,
    campo: TipoCampoEdicion = TipoCampoEdicion.CONSUMO_M3,
    viewModel: CorreccionViewModel = viewModel { CorreccionViewModel.desdeInyeccion() }
) {
    val borrador by viewModel.borrador.collectAsStateWithLifecycle()
    val valorDetectado = remember(campo, borrador) { viewModel.valorInicial(campo) }
    var entrada by remember(valorDetectado) { mutableStateOf(valorDetectado) }
    var periodoSeleccionado by remember(borrador) { mutableStateOf(viewModel.periodoInicial()) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }
    val esPeriodo = campo == TipoCampoEdicion.PERIODO

    IconosClarosEnBarraDeEstado(claros = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPantalla)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ReciboBarraSuperior(
                onVolver = onVolver,
                titulo = if (esPeriodo) "Seleccionar período" else "Corregir ${campo.nombre}",
                subtitulo = if (esPeriodo) {
                    "Recibo para: ${periodoSeleccionado.displayCompleto}"
                } else {
                    "Valor actual: ${if (valorDetectado.isBlank()) "Sin datos" else "$valorDetectado ${campo.unidad}"}"
                }
            )

            if (esPeriodo) {
                SelectorPeriodoMeses(
                    periodoActual = viewModel.periodoActual,
                    periodoSeleccionado = periodoSeleccionado,
                    onSeleccionarPeriodo = { periodoSeleccionado = it }
                )
            } else {
                DisplayDigitosMedidor(
                    campoEtiqueta = "${campo.nombre} en ${campo.unidad}".uppercase(),
                    digitos = entrada,
                    unidad = campo.unidad,
                    valorDetectado = valorDetectado.ifBlank { "0" },
                    errorMensaje = errorMensaje,
                    maxDigitos = campo.maxDigitos
                )
            }
        }

        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            if (!esPeriodo) {
                TecladoNumericoMedidor(
                    permiteComa = campo.permiteComa,
                    onDigitoPulsado = { tecla ->
                        errorMensaje = null
                        entrada = aplicarTecla(entrada, tecla, campo.permiteComa, campo.maxDigitos)
                    }
                )
                Spacer(Modifier.height(20.dp))
            }

            BotonGuardarCorreccion(
                onGuardar = {
                    errorMensaje = viewModel.guardar(campo, entrada, periodoSeleccionado)
                    if (errorMensaje == null) onVolver()
                }
            )
        }
    }
}

@Composable
private fun BotonGuardarCorreccion(onGuardar: () -> Unit) {
    Button(
        onClick = onGuardar,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BotonGuardar)
    ) {
        Text(
            text = "Guardar corrección",
            fontFamily = FuenteTexto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
