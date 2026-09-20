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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.ui.text.style.TextAlign
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
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón volver cuadrado / redondeado como en la imagen
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F6F8))
                    .clickable(onClick = onVolver),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Volver",
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFF0F172A)
                )
            }
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = "Tu histórico",
                    fontFamily = FuenteTexto,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    lineHeight = 24.sp,
                    maxLines = 1
                )
                Text(
                    text = "Consumo facturado, últimos 6 meses",
                    fontFamily = FuenteTexto,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF8899A6),
                    maxLines = 1
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        // Badge atípico horizontal, compacto y sin apiñarse
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFFEF2E6))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE18228))
            )
            Text(
                text = "Atípico",
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE18228),
                softWrap = false,
                maxLines = 1
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Gráfico de barras de 6 meses
// ──────────────────────────────────────────────────────────────────────

private data class DatoMesHistorial(
    val mes: String,
    val m3: Float,
    val color: Color,
    val esAtipico: Boolean,
    val textoValor: String? = null
)

@Composable
private fun GraficoBarras() {
    val chartHeight = 136.dp
    val maxM3 = 33f
    val promedioM3 = 16f

    val datos = listOf(
        DatoMesHistorial("Mar", 14.2f, Color(0xFF10939C), false),
        DatoMesHistorial("Abr", 12.5f, Color(0xFF10939C), false),
        DatoMesHistorial("May", 15.5f, Color(0xFF10939C), false),
        DatoMesHistorial("Jun", 16.8f, Color(0xFF10939C), false),
        DatoMesHistorial("Jul", 17.5f, Color(0xFF10939C), false),
        DatoMesHistorial("Ago", 33.0f, Color(0xFFE18228), true, "33")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Blanco)
            .border(1.dp, Color(0xFFE8F0F2), RoundedCornerShape(28.dp))
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Encabezado gráfico de la tarjeta
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "metros cúbicos facturados",
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF8899A6)
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "promedio ",
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Text(
                    text = "16",
                    fontFamily = FuenteNumeros,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text = "m³",
                    fontFamily = FuenteTexto,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
            }
        }

        // Línea divisoria superior
        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp, bottom = 14.dp),
            color = Color(0xFFE8F0F2),
            thickness = 1.dp
        )

        // Contenedor del gráfico (Línea discontinua en el medio + 6 barras redondeadas)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
        ) {
            // Línea discontinua del promedio en el medio + línea base horizontal
            Canvas(modifier = Modifier.fillMaxSize()) {
                val lineYDashed = size.height * (1f - (promedioM3 / maxM3))
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)

                // Línea discontinua del medio (promedio 16 m³)
                drawLine(
                    color = Color(0xFF94A3B8),
                    start = Offset(0f, lineYDashed),
                    end = Offset(size.width, lineYDashed),
                    strokeWidth = 1.8.dp.toPx(),
                    pathEffect = dashEffect
                )

                // Línea base horizontal inferior
                drawLine(
                    color = Color(0xFFE8F0F2),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Las 6 barras estructuradas con bordes redondeados y anchos proporcionales
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                datos.forEach { item ->
                    val barHeight = chartHeight * (item.m3 / maxM3)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(chartHeight),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.72f)
                                .height(barHeight)
                                .clip(RoundedCornerShape(8.dp))
                                .background(item.color),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            if (item.textoValor != null) {
                                Text(
                                    text = item.textoValor,
                                    fontFamily = FuenteNumeros,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Blanco,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Fila de meses debajo de las barras
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            datos.forEach { item ->
                Text(
                    text = item.mes,
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = if (item.esAtipico) FontWeight.Bold else FontWeight.Medium,
                    color = if (item.esAtipico) Color(0xFFE18228) else Color(0xFF8899A6),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
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
private fun FilaDetalle(
    etiqueta: String,
    valor: String,
    unidad: String?,
    colorValor: Color,
    colorUnidad: Color = colorValor
) {
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
                Text(unidad, fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = colorUnidad)
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
