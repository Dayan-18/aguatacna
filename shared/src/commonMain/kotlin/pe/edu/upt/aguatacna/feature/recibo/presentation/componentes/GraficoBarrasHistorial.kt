package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.service.EvaluadorConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.BarraHistorialSlot

private val ALTO_GRAFICO = 136.dp

// Gráfico de 6 meses con el límite de consumo punteado; naranja si hubo alto consumo, teal si no.
@Composable
fun GraficoBarrasHistorial(
    barras: List<BarraHistorialSlot>,
    promedioM3: Int?,
    mesSeleccionado: PeriodoConsumo,
    onSeleccionarMes: (PeriodoConsumo) -> Unit
) {
    val yMax = maxOf(125f, (barras.maxOfOrNull { it.consumoM3 ?: 0 } ?: 0) * 1.15f)
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 16.dp)
            .tarjeta(radio = 28.dp, relleno = 20.dp, borde = BordeGrafico)
    ) {
        EncabezadoGrafico(promedioM3)
        HorizontalDivider(Modifier.padding(top = 12.dp, bottom = 14.dp), 1.dp, BordeGrafico)
        Box(Modifier.fillMaxWidth().height(ALTO_GRAFICO)) {
            LineaLimite(yMax)
            Row(Modifier.fillMaxSize(), Arrangement.SpaceBetween, Alignment.Bottom) {
                barras.forEach { barra ->
                    Barra(barra, barra.periodo == mesSeleccionado, yMax, Modifier.weight(1f)) {
                        onSeleccionarMes(barra.periodo)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            barras.forEach { barra ->
                EtiquetaMes(barra, barra.periodo == mesSeleccionado, Modifier.weight(1f)) { onSeleccionarMes(barra.periodo) }
            }
        }
    }
}

@Composable
private fun EncabezadoGrafico(promedioM3: Int?) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text("límite ${EvaluadorConsumo.LIMITE_M3} m³", style = estilo(11.5.sp, color = GrisGrafico), maxLines = 1, softWrap = false)
        Row(verticalAlignment = Alignment.Bottom) {
            Text("promedio ", style = estilo(11.5.sp, color = GrisGrafico))
            Text(promedioM3?.toString() ?: "—", style = estilo(13.sp, FontWeight.Bold, TintaGrafico, numeros = true))
            Spacer(Modifier.width(2.dp))
            Text("m³", style = estilo(10.sp, FontWeight.SemiBold, Color(0xFF475569)))
        }
    }
}

@Composable
private fun LineaLimite(yMax: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val y = size.height * (1f - (EvaluadorConsumo.LIMITE_M3 / yMax).coerceIn(0f, 1f))
        drawLine(
            Color(0xFF94A3B8), Offset(0f, y), Offset(size.width, y),
            strokeWidth = 1.8.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
        )
        drawLine(BordeGrafico, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 1.dp.toPx())
    }
}

@Composable
private fun Barra(barra: BarraHistorialSlot, seleccionada: Boolean, yMax: Float, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.height(ALTO_GRAFICO).clickable(onClick = onClick), contentAlignment = Alignment.BottomCenter) {
        val m3 = barra.consumoM3 ?: return@Box
        val alto = (ALTO_GRAFICO * (m3 / yMax).coerceAtMost(1f)).coerceAtLeast(8.dp)
        Box(
            Modifier.fillMaxWidth(0.72f).height(alto).caja(8.dp, if (barra.esAltoConsumo) NaranjaBarra else TealBarra),
            contentAlignment = Alignment.TopCenter
        ) {
            if (barra.esAltoConsumo || seleccionada) {
                Text("$m3", style = estilo(12.sp, FontWeight.Bold, Blanco, numeros = true), modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}

@Composable
private fun EtiquetaMes(barra: BarraHistorialSlot, seleccionada: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val color = when {
        barra.esAltoConsumo -> NaranjaBarra
        seleccionada -> TintaGrafico
        else -> GrisGrafico
    }
    val peso = if (barra.esAltoConsumo || seleccionada) FontWeight.Bold else FontWeight.Medium
    Text(
        if (barra.consumoM3 != null) barra.periodo.mesCorto else "—",
        style = estilo(12.sp, peso, color),
        textAlign = TextAlign.Center,
        modifier = modifier.clickable(onClick = onClick)
    )
}
