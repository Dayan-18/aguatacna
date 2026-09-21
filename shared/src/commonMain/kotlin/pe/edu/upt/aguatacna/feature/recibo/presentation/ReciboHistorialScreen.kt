package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Dinero
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.BarraHistorialSlot
import pe.edu.upt.aguatacna.feature.recibo.presentation.comun.EstiloEstado

// Colores específicos del diseño historial
private val OcreFondo = Color(0xFFFEF2E4)
private val OcreBorde = Color(0xFFF9DFC5)
private val OcreTexto = Color(0xFFB45309)
private val OcreTextoOscuro = Color(0xFF7C4D29)

/**
 * Pantalla de historial de consumo (Fase 10).
 * Renderiza gráfico de 6 meses con línea de umbral atípico, selección de mes y datos reactivos.
 */
@Composable
fun ReciboHistorialScreen(
    onVolver: () -> Unit,
    onModificarRecibo: (PeriodoConsumo) -> Unit = {},
    viewModel: HistorialViewModel = viewModel { HistorialViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    IconosClarosEnBarraDeEstado(claros = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F7F7))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        when (val state = uiState) {
            is HistorialUiState.Cargando -> {
                EncabezadoHistorial(onVolver = onVolver, esAtipico = false, chipTexto = "Cargando...")
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AguaMedia)
                }
            }

            is HistorialUiState.SinHistorial -> {
                EncabezadoHistorial(onVolver = onVolver, esAtipico = false, chipTexto = "Sin recibos")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aún no tienes recibos registrados para ver tu histórico de consumo.",
                        fontFamily = FuenteTexto,
                        fontSize = 14.sp,
                        color = TintaSuave,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is HistorialUiState.ConDatos -> {
                // ── Header (T-10.4) ──
                EncabezadoHistorial(
                    onVolver = onVolver,
                    esAtipico = state.esAtipico,
                    chipTexto = state.estadoSeleccionado?.chipTexto ?: "Historial"
                )

                // ── Gráfico de barras de 6 meses con umbral (T-10.2, T-10.3, T-10.7) ──
                GraficoBarras(
                    barras = state.barras,
                    promedioM3 = state.promedioHistorico,
                    umbralAtipicoM3 = state.umbralAtipicoM3,
                    mesSeleccionado = state.mesSeleccionado,
                    onSeleccionarMes = { viewModel.seleccionarMes(it) }
                )

                // ── Alerta estado de consumo (T-10.4) ──
                AlertaEstado(
                    estado = state.estadoSeleccionado,
                    mes = state.mesSeleccionado,
                    promedio = state.promedioHistoricoSeleccionado,
                    exceso = state.excesoPorcentajeSeleccionado,
                    esAtipico = state.esAtipico,
                    onComoReclamar = { viewModel.mostrarDialogoReclamo(true) }
                )

                // ── Detalle del período seleccionado (T-10.5) con modificar factura ──
                DetallePeriodo(
                    mes = state.mesSeleccionado,
                    consumo = state.consumoSeleccionado,
                    promedio = state.promedioHistoricoSeleccionado,
                    exceso = state.excesoPorcentajeSeleccionado,
                    importe = state.importeSeleccionado,
                    esAtipico = state.esAtipico,
                    onModificarRecibo = onModificarRecibo
                )

                // ── Botones de acción (T-10.6) ──
                BotonesAccion(
                    esAtipico = state.esAtipico,
                    onComoReclamar = { viewModel.mostrarDialogoReclamo(true) }
                )

                // ── Modal informativo "Cómo reclamar" (T-10.6) ──
                if (state.mostrarDialogoReclamo) {
                    DialogoComoReclamar(onDismiss = { viewModel.mostrarDialogoReclamo(false) })
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

// ──────────────────────────────────────────────────────────────────────
// Header
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun EncabezadoHistorial(
    onVolver: () -> Unit,
    esAtipico: Boolean,
    chipTexto: String
) {
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

        // Badge atípico o estado del mes seleccionado
        val fondoBadge = if (esAtipico) Color(0xFFFEF2E6) else Color(0xFFE4F3F4)
        val colorTextoBadge = if (esAtipico) Color(0xFFE18228) else AguaMedia

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(fondoBadge)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(colorTextoBadge)
            )
            Text(
                text = chipTexto,
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorTextoBadge,
                softWrap = false,
                maxLines = 1
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Gráfico de barras de 6 meses con línea de umbral atípico (T-10.2, T-10.3)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun GraficoBarras(
    barras: List<BarraHistorialSlot>,
    promedioM3: Int,
    umbralAtipicoM3: Double,
    mesSeleccionado: PeriodoConsumo,
    onSeleccionarMes: (PeriodoConsumo) -> Unit
) {
    val chartHeight = 136.dp

    // Fórmulas de sección 3.4 (T-10.3)
    val maxConsumo = barras.mapNotNull { it.consumoM3?.toFloat() }.maxOrNull() ?: 1f
    val yMax = maxOf(umbralAtipicoM3.toFloat(), maxConsumo).coerceAtLeast(1f)

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
                    text = "límite 100 m³  ·  promedio ",
                    fontFamily = FuenteTexto,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF8899A6)
                )
                Text(
                    text = "$promedioM3",
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

        // Contenedor del gráfico (Línea discontinua a 100 m³ + 6 barras)
        val maxConsumo = barras.maxOfOrNull { it.consumoM3 ?: 0 } ?: 0
        val yMax = maxOf(125f, maxConsumo.toFloat() * 1.15f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
        ) {
            // Línea discontinua fija en 100 m³
            Canvas(modifier = Modifier.fillMaxSize()) {
                val posLinea = (100f / yMax).coerceIn(0f, 1f)
                val lineYDashed = size.height * (1f - posLinea)
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)

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

            // Las 6 barras estructuradas con bordes redondeados y proporcionales
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                barras.forEach { item ->
                    val esSeleccionado = item.periodo == mesSeleccionado
                    val m3 = item.consumoM3
                    val barHeight = if (m3 != null) chartHeight * (m3.toFloat() / yMax).coerceAtMost(1f) else 0.dp
                    val esAltoConsumo = (m3 ?: 0) > 100
                    val barColor = if (esAltoConsumo) Color(0xFFE18228) else Color(0xFF10939C)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(chartHeight)
                            .clickable { onSeleccionarMes(item.periodo) },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (m3 != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.72f)
                                    .height(barHeight.coerceAtLeast(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(barColor),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                if (esAltoConsumo || esSeleccionado) {
                                    Text(
                                        text = "$m3",
                                        fontFamily = FuenteNumeros,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Blanco,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
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
            barras.forEach { item ->
                val esSeleccionado = item.periodo == mesSeleccionado
                val textoMes = if (item.consumoM3 != null) item.mesCorto else "—"
                val colorTexto = when {
                    item.esAtipico -> Color(0xFFE18228)
                    esSeleccionado -> Color(0xFF0F172A)
                    else -> Color(0xFF8899A6)
                }

                Text(
                    text = textoMes,
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = if (item.esAtipico || esSeleccionado) FontWeight.Bold else FontWeight.Medium,
                    color = colorTexto,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSeleccionarMes(item.periodo) }
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Alerta estado de consumo (T-10.4)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun AlertaEstado(
    estado: EstadoConsumo?,
    mes: PeriodoConsumo,
    promedio: Int,
    exceso: Int?,
    esAtipico: Boolean,
    onComoReclamar: () -> Unit
) {
    val estadoSeguro = estado ?: EstadoConsumo.SinHistorial(0)
    val estilo = EstiloEstado.desde(estadoSeguro)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(estilo.colorFondo)
            .border(1.dp, estilo.colorBorde, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                if (esAtipico) Icons.Default.Warning else Icons.Outlined.Shield,
                contentDescription = null,
                tint = estilo.colorPrincipal,
                modifier = Modifier.size(16.dp)
            )
            Text(
                if (esAtipico) "Alto consumo" else estilo.chipTexto,
                fontFamily = FuenteTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = estilo.colorPrincipal
            )
        }
        Spacer(Modifier.height(6.dp))

        val descripcion = when (estado) {
            is EstadoConsumo.Atipico ->
                "${mes.mesLargo} supera los 100 m³ o excede tu promedio habitual de $promedio m³. Puedes revisar o reclamar tu recibo ante EPS Tacna."
            is EstadoConsumo.Normal ->
                "${mes.mesLargo} está dentro de tu promedio habitual de $promedio m³. Todo en orden."
            is EstadoConsumo.FacturadoPorPromedio ->
                "EPS Tacna facturó ${mes.mesLargo} por promedio; no corresponde a una lectura real del medidor."
            is EstadoConsumo.SinHistorial, null ->
                "Registro de consumo para evaluar tus recibos."
        }

        Text(
            text = descripcion,
            fontFamily = FuenteTexto,
            fontSize = 12.sp,
            color = if (esAtipico) OcreTextoOscuro else TintaSuave,
            lineHeight = 18.sp
        )
    }
}

// ──────────────────────────────────────────────────────────────────────
// Detalle del período (T-10.5)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun DetallePeriodo(
    mes: PeriodoConsumo,
    consumo: Int?,
    promedio: Int,
    exceso: Int?,
    importe: Dinero?,
    esAtipico: Boolean,
    onModificarRecibo: (PeriodoConsumo) -> Unit = {}
) {
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
            val consumoTexto = consumo?.toString() ?: "—"
            val consumoColor = if (esAtipico) Ocre else Tinta
            FilaDetalle("Consumo de ${mes.mesLargo.lowercase()}", consumoTexto, if (consumo != null) "m³" else null, consumoColor)

            FilaDetalle("Promedio histórico", "$promedio", "m³", Tinta)

            val excesoTexto = when {
                exceso == null -> "—"
                exceso > 0 -> "+$exceso %"
                else -> "$exceso %"
            }
            val excesoColor = if (esAtipico) Ocre else TintaSuave
            FilaDetalle("Exceso", excesoTexto, null, excesoColor)

            HorizontalDivider(color = Divisor.copy(alpha = 0.5f))

            val importeTexto = importe?.formatear() ?: "—"
            FilaDetalle("Importe facturado", importeTexto, null, Tinta)

            if (consumo != null) {
                HorizontalDivider(color = Divisor.copy(alpha = 0.5f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onModificarRecibo(mes) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = AguaMedia
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Modificar factura",
                        fontFamily = FuenteTexto,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AguaMedia
                    )
                }
            }
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
// Botones de acción (T-10.6)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BotonesAccion(
    esAtipico: Boolean,
    onComoReclamar: () -> Unit
) {
    if (esAtipico) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp)
        ) {
            Button(
                onClick = onComoReclamar,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Ocre)
            ) {
                Text(
                    "Cómo reclamar",
                    fontFamily = FuenteTexto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Modal informativo: Pasos oficiales de reclamo Sunass (T-10.6)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun DialogoComoReclamar(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE18228),
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Reclamo por Consumo Atípico",
                    fontFamily = FuenteTexto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Tinta
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Según el Reglamento de Calidad de la Sunass (art. 88), sigue estos pasos ante un consumo excesivo:",
                    fontFamily = FuenteTexto,
                    fontSize = 13.sp,
                    color = TintaSuave,
                    lineHeight = 18.sp
                )

                PasoReclamo(
                    numero = "1",
                    titulo = "Revisa fugas internas",
                    descripcion = "Verifica inodoros, grifos y cisternas. Si la fuga es interna, el usuario es responsable de repararla antes de solicitar una inspección."
                )

                PasoReclamo(
                    numero = "2",
                    titulo = "Presenta tu reclamo a tiempo",
                    descripcion = "Tienes hasta 60 días calendario contados desde el vencimiento del recibo (o hasta 12 meses si ya lo pagaste) para reclamar ante EPS Tacna."
                )

                PasoReclamo(
                    numero = "3",
                    titulo = "Protección durante el trámite",
                    descripcion = "Mientras el reclamo esté en trámite, EPS Tacna no puede cortar el servicio ni condicionar la atención al pago del importe reclamado."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE18228))
            ) {
                Text("Entendido", fontFamily = FuenteTexto, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Blanco,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun PasoReclamo(numero: String, titulo: String, descripcion: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEF2E6)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                numero,
                fontFamily = FuenteNumeros,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE18228)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                titulo,
                fontFamily = FuenteTexto,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Tinta
            )
            Spacer(Modifier.height(2.dp))
            Text(
                descripcion,
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                color = TintaSuave,
                lineHeight = 16.sp
            )
        }
    }
}
