package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave

// Colores específicos del diseño historial
private val OcreFondo = Color(0xFFFEF2E4)
private val OcreBorde = Color(0xFFF9DFC5)
private val OcreTexto = Color(0xFFB45309)
private val OcreTextoOscuro = Color(0xFF7C4D29)

/**
 * Pantalla de historial de consumo.
 * Corresponde al diseño de historial.html.
 */
@Composable
fun ReciboHistorialScreen(onVolver: () -> Unit) {
    IconosClarosEnBarraDeEstado(claros = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F7F7))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──
        EncabezadoHistorial(onVolver)

        // ── Gráfico de barras ──
        GraficoBarras()

        // ── Alerta consumo atípico ──
        AlertaAtipico()

        // ── Detalle del período ──
        DetallePeriodo()

        // ── Botones de acción ──
        BotonesAccion()

        Spacer(Modifier.height(24.dp))
    }
}

// ──────────────────────────────────────────────────────────────────────
// Header
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun EncabezadoHistorial(onVolver: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Botón volver
            IconButton(
                onClick = onVolver,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Blanco)
                    .border(1.dp, Divisor.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(20.dp),
                    tint = TintaSuave
                )
            }
            Column {
                Text(
                    "Tu histórico",
                    fontFamily = FuenteTexto,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Tinta
                )
                Text(
                    "Consumo facturado, últimos 6 meses",
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    color = TintaTenue
                )
            }
        }
        // Badge atípico
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(OcreFondo)
                .border(1.dp, OcreBorde, RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Ocre))
            Text("Atípico", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Ocre)
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Gráfico de barras de 6 meses
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun GraficoBarras() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        // Encabezado gráfico
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("metros cúbicos facturados", fontFamily = FuenteTexto, fontSize = 11.sp, color = TintaTenue)
            Row {
                Text("promedio ", fontFamily = FuenteTexto, fontSize = 11.sp, color = TintaSuave)
                Text("16 m³", fontFamily = FuenteNumeros, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Tinta)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Divisor)

        Spacer(Modifier.height(8.dp))

        // Barras con Canvas simplificado (representación visual)
        val datos = listOf(
            "Mar" to 0.48f,
            "Abr" to 0.40f,
            "May" to 0.52f,
            "Jun" to 0.54f,
            "Jul" to 0.57f,
            "Ago" to 1.00f
        )

        Row(
            modifier = Modifier.fillMaxWidth().height(130.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            datos.forEachIndexed { index, (mes, porcentaje) ->
                val esAtipico = index == datos.lastIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    // Valor sobre la barra (solo para agosto)
                    if (esAtipico) {
                        Text("33", fontFamily = FuenteNumeros, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Blanco)
                    }
                    // Barra
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height((110 * porcentaje).dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(if (esAtipico) Ocre else AguaMedia),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (esAtipico) {
                            Text(
                                "33",
                                fontFamily = FuenteNumeros,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Blanco,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // Etiqueta del mes
                    Text(
                        mes,
                        fontFamily = FuenteTexto,
                        fontSize = 10.sp,
                        fontWeight = if (esAtipico) FontWeight.Bold else FontWeight.Medium,
                        color = if (esAtipico) Ocre else TintaTenue
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Alerta consumo atípico
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun AlertaAtipico() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(OcreFondo)
            .border(1.dp, OcreBorde, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = OcreTexto,
                modifier = Modifier.size(16.dp)
            )
            Text(
                "Consumo atípico",
                fontFamily = FuenteTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = OcreTexto
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "Agosto supera en 106 % tu promedio histórico de 16 m³. La Sunass considera atípico todo consumo que exceda el 100 %: puedes presentar un reclamo formal.",
            fontFamily = FuenteTexto,
            fontSize = 12.sp,
            color = OcreTextoOscuro,
            lineHeight = 18.sp
        )
    }
}

// ──────────────────────────────────────────────────────────────────────
// Detalle del período
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun DetallePeriodo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
    ) {
        Text(
            "DETALLE DEL PERÍODO",
            fontFamily = FuenteTexto,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TintaTenue,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .sombraSuave(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Blanco)
                .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FilaDetalle("Consumo de agosto", "33", "m³", Ocre)
            FilaDetalle("Promedio histórico", "16", "m³", Tinta)
            FilaDetalle("Exceso", "+106 %", null, Ocre)
            HorizontalDivider(color = Divisor.copy(alpha = 0.5f))
            FilaDetalle("Importe facturado", "S/ 74,20", null, Tinta)
        }
    }
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: String, unidad: String?, colorValor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(etiqueta, fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(valor, fontFamily = FuenteNumeros, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorValor)
            if (unidad != null) {
                Spacer(Modifier.width(3.dp))
                Text(unidad, fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TintaSuave)
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Botones de acción
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BotonesAccion() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Botón "Cómo reclamar"
        Button(
            onClick = { /* Solo visual */ },
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Ocre)
        ) {
            Text(
                "Cómo reclamar",
                fontFamily = FuenteTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        // Botón "Revisar fugas"
        OutlinedButton(
            onClick = { /* Solo visual */ },
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                "Revisar fugas",
                fontFamily = FuenteTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Tinta
            )
        }
    }
}
