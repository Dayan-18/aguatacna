package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo

// Cuadrícula de los últimos 12 meses (4 filas × 3) para elegir el período de consumo del recibo.
@Composable
fun SelectorPeriodoMeses(
    periodoActual: PeriodoConsumo,
    periodoSeleccionado: PeriodoConsumo,
    onSeleccionarPeriodo: (PeriodoConsumo) -> Unit
) {
    val meses = remember(periodoActual) {
        generateSequence(periodoActual) { it.anterior() }.take(12).toList().reversed()
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 24.dp)
            .tarjeta(radio = 24.dp, relleno = 20.dp, borde = null),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "SELECCIONA EL PERÍODO (ÚLTIMOS 12 MESES)",
            style = estilo(11.sp, FontWeight.SemiBold, TintaTenue),
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            meses.chunked(3).forEach { fila ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    fila.forEach { periodo ->
                        BotonMes(periodo, periodo == periodoSeleccionado, Modifier.weight(1f)) {
                            onSeleccionarPeriodo(periodo)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BotonMes(periodo: PeriodoConsumo, seleccionado: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier.height(52.dp).caja(12.dp, if (seleccionado) VerdeBoton else DigitoFondo).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val peso = if (seleccionado) FontWeight.Bold else FontWeight.SemiBold
            Text(periodo.mesCorto, style = estilo(13.sp, peso, if (seleccionado) Blanco else DigitoTexto))
            Text(
                periodo.anio.toString(),
                style = estilo(11.sp, color = if (seleccionado) Blanco.copy(alpha = 0.85f) else TintaTenue, numeros = true)
            )
        }
    }
}
