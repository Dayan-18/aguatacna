package pe.edu.upt.aguatacna.feature.recibo.presentation

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.presentation.revision.RevisionViewModel

// Colores específicos del diseño foto
private val TealBadgeFondo = Color(0xFFDFF4F3)
private val TealBadgeBorde = Color(0xFFCAECEA)
private val TealBadgeTexto = Color(0xFF0A7B83)
private val OcreBadgeFondo = Color(0xFFFFF7ED)
private val OcreBadgeBorde = Color(0xFFFDE0B5)
private val DocFondo = Color(0xFFF5FBFB)
private val DocBorde = Color(0xFFD6EDEC)
private val DocBarraTeal = Color(0xFF0A7B83)
private val TipFondo = Color(0x80D9ECEF) // 50% opacidad
private val TipBorde = Color(0xFFB2D9DE)
private val TipTexto = Color(0xFF215157)
private val DashedBorde = Color(0xFFCFE0E2)

/**
 * Pantalla de revisión de foto/recibo escaneado ("Revisa tu recibo" — Fase 8).
 * Muestra datos reales del [ReciboBorrador] y permite confirmar o corregir.
 */
@Composable
fun ReciboFotoScreen(
    onVolver: () -> Unit,
    onRetomarFoto: () -> Unit = {},
    onCorregirCampo: (TipoCampoEdicion) -> Unit = {},
    viewModel: RevisionViewModel = viewModel { RevisionViewModel.desdeInyeccion() }
) {
    val borrador by viewModel.borrador.collectAsStateWithLifecycle()
    val errorDuplicado by viewModel.errorDuplicado.collectAsStateWithLifecycle()

    LaunchedEffect(borrador) {
        if (borrador == null) {
            val nuevo = pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador(
                periodoConsumo = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(
                    pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo(2026, 8),
                    confianza = 1f
                ),
                consumoM3 = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(null, 1f),
                importeTotal = pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo(null, 1f),
                origen = pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos.MANUAL
            )
            viewModel.guardarBorrador(nuevo)
        }
    }

    // Limpiar error de duplicado cuando cambia el período del borrador
    LaunchedEffect(borrador?.periodoConsumo?.valor) {
        viewModel.descartarError()
    }

    IconosClarosEnBarraDeEstado(claros = false)
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F7F7))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp) // Espacio para los botones flotantes
        ) {
            // ── Header (T-8.3) ──
            EncabezadoFoto(
                onVolver = onVolver,
                borrador = borrador
            )

            // ── Contenido ──
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Alerta de período duplicado ──
                if (errorDuplicado != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF2E4))
                            .border(1.dp, Color(0xFFF9DFC5), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = Ocre,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            errorDuplicado!!,
                            fontFamily = FuenteTexto,
                            fontSize = 12.sp,
                            color = Color(0xFF7C4D29),
                            lineHeight = 17.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ── Tarjeta del recibo (T-8.2) ──
                TarjetaRecibo(
                    borrador = borrador
                )

                // ── Campos detectados (T-8.2, T-8.6) ──
                CamposDetectados(
                    borrador = borrador,
                    onFilaClick = onCorregirCampo
                )

                // ── Tip informativo ──
                TipInformativo()

                // ── Zona retomar foto (T-8.4) ──
                ZonaRetomarFoto(
                    tieneFoto = borrador != null,
                    onClick = onRetomarFoto
                )
            }
        }

        // ── Botón flotante Confirmar (T-8.5) ──
        BotonesFlotantes(
            modifier = Modifier.align(Alignment.BottomCenter),
            habilitadoConfirmar = borrador?.esConfirmable == true,
            onConfirmar = {
                viewModel.confirmar(onCompletado = onVolver)
            }
        )
    }
}

// ──────────────────────────────────────────────────────────────────────
// Header con badge dinámico (T-8.3)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun EncabezadoFoto(
    onVolver: () -> Unit,
    borrador: ReciboBorrador?
) {
    val tieneDudosos = borrador?.let {
        it.consumoM3.esDudoso || it.importeTotal.esDudoso || it.periodoConsumo.esDudoso
    } ?: false

    val esManual = borrador?.origen == OrigenDatos.MANUAL

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    "Revisa tu recibo",
                    fontFamily = FuenteTexto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Tinta
                )
                Text(
                    "Confirma los datos antes de guardar",
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TintaTenue
                )
            }
        }

        // Chip de estado dinámico (T-8.3)
        when {
            tieneDudosos -> {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(OcreBadgeFondo)
                        .border(1.dp, OcreBadgeBorde, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Ocre))
                    Text("Revisa los datos", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Ocre)
                }
            }
            esManual -> {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Divisor.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TintaSuave))
                    Text("Manual", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TintaSuave)
                }
            }
            else -> {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(TealBadgeFondo)
                        .border(1.dp, TealBadgeBorde, RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TealBadgeTexto))
                    Text("Leído", fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TealBadgeTexto)
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Tarjeta del recibo con datos dinámicos (T-8.2)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaRecibo(
    borrador: ReciboBorrador?
) {
    val mesTexto = borrador?.periodoConsumo?.valor?.displayCompleto ?: "Seleccionar período"
    val suministroTexto = borrador?.numeroMedidor?.valor?.let { "Medidor $it" }
        ?: borrador?.numeroRecibo?.valor?.let { "Recibo N° $it" }
        ?: "Suministro 0412887"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ícono documento ilustrado
        Column(
            modifier = Modifier
                .size(width = 64.dp, height = 72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DocFondo)
                .border(1.dp, DocBorde, RoundedCornerShape(12.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.width(28.dp).height(4.dp).clip(RoundedCornerShape(50)).background(Color(0xFFB6DFDD)))
                Box(Modifier.width(36.dp).height(4.dp).clip(RoundedCornerShape(50)).background(Color(0xFFCFECEB)))
                Box(Modifier.width(20.dp).height(4.dp).clip(RoundedCornerShape(50)).background(Color(0xFFCFECEB)))
            }
            Box(Modifier.width(36.dp).height(6.dp).clip(RoundedCornerShape(50)).background(DocBarraTeal))
        }

        // Detalles del recibo
        Column {
            Text(
                "Recibo EPS Tacna",
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TintaTenue
            )
            Text(
                mesTexto,
                fontFamily = FuenteTexto,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Tinta
            )
            Text(
                suministroTexto,
                fontFamily = FuenteNumeros,
                fontSize = 12.sp,
                color = TintaTenue,
                letterSpacing = 1.sp
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Campos detectados (T-8.2: dinámicos y editables)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun CamposDetectados(
    borrador: ReciboBorrador?,
    onFilaClick: (TipoCampoEdicion) -> Unit
) {
    Column {
        Text(
            "CAMPOS DETECTADOS",
            fontFamily = FuenteTexto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TintaTenue,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .sombraSuave(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Blanco)
                .border(1.dp, Divisor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Período de consumo (editable)
            val periodoTexto = borrador?.periodoConsumo?.valor?.displayCompleto ?: "Seleccionar período"
            FilaCampo(
                etiqueta = "Período de consumo",
                valor = periodoTexto,
                unidad = null,
                colorValor = Tinta,
                esDudoso = borrador?.periodoConsumo?.esDudoso ?: false,
                onClick = { onFilaClick(TipoCampoEdicion.PERIODO) }
            )
            HorizontalDivider(color = Divisor)

            // Fila Lectura anterior (solo si no es null)
            val anterior = borrador?.lecturaAnteriorM3?.valor
            if (anterior != null) {
                FilaCampo(
                    etiqueta = "Lectura anterior",
                    valor = anterior.toString(),
                    unidad = "m³",
                    colorValor = TintaSuave,
                    esDudoso = borrador.lecturaAnteriorM3.esDudoso,
                    onClick = { onFilaClick(TipoCampoEdicion.LECTURA_ANTERIOR) }
                )
                HorizontalDivider(color = Divisor)
            }

            // Fila Lectura actual (solo si no es null)
            val actual = borrador?.lecturaActualM3?.valor
            if (actual != null) {
                FilaCampo(
                    etiqueta = "Lectura actual",
                    valor = actual.toString(),
                    unidad = "m³",
                    colorValor = TintaSuave,
                    esDudoso = borrador.lecturaActualM3.esDudoso,
                    onClick = { onFilaClick(TipoCampoEdicion.LECTURA_ACTUAL) }
                )
                HorizontalDivider(color = Divisor)
            }

            // Consumo del período (siempre visible)
            val consumo = borrador?.consumoM3?.valor?.let { "$it" } ?: "Sin datos"
            FilaCampo(
                etiqueta = "Consumo del período",
                valor = consumo,
                unidad = if (borrador?.consumoM3?.valor != null) "m³" else null,
                colorValor = AguaMedia,
                esDudoso = borrador?.consumoM3?.esDudoso ?: false,
                onClick = { onFilaClick(TipoCampoEdicion.CONSUMO_M3) }
            )
            HorizontalDivider(color = Divisor)

            // Importe total (siempre visible)
            val importe = borrador?.importeTotal?.valor?.formatear() ?: "Sin datos"
            FilaCampo(
                etiqueta = "Importe total",
                valor = importe,
                unidad = null,
                colorValor = Tinta,
                esDudoso = borrador?.importeTotal?.esDudoso ?: false,
                onClick = { onFilaClick(TipoCampoEdicion.IMPORTE) }
            )
        }
    }
}

@Composable
private fun FilaCampo(
    etiqueta: String,
    valor: String,
    unidad: String?,
    colorValor: Color,
    esDudoso: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(etiqueta, fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
            if (esDudoso) {
                Icon(
                    Icons.Outlined.Warning,
                    contentDescription = "Dato dudoso",
                    tint = Ocre,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(valor, fontFamily = FuenteNumeros, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (esDudoso) Ocre else colorValor)
            if (unidad != null) {
                Spacer(Modifier.width(4.dp))
                Text(unidad, fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TintaTenue)
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Tip informativo
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TipInformativo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TipFondo)
            .border(1.dp, TipBorde, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            Icons.Outlined.Keyboard,
            contentDescription = null,
            tint = AguaMedia,
            modifier = Modifier.size(18.dp)
        )
        Text(
            "Si algún dato salió mal, corrígelo aquí mismo. También puedes escribirlo a mano sin usar la cámara.",
            fontFamily = FuenteTexto,
            fontSize = 12.sp,
            color = TipTexto,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// ──────────────────────────────────────────────────────────────────────
// Zona retomar foto (T-8.4)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun ZonaRetomarFoto(
    tieneFoto: Boolean,
    onClick: () -> Unit
) {
    val textoBoton = if (tieneFoto) "Tomar otra foto" else "Tomar foto del recibo"
    val borderColor = DashedBorde

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Blanco.copy(alpha = 0.8f))
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                    )
                )
            }
            .clickable(onClick = onClick)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.PhotoCamera,
            contentDescription = textoBoton,
            tint = Color(0xFF8CA3A6),
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            textoBoton,
            fontFamily = FuenteTexto,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TintaTenue,
            textAlign = TextAlign.Center
        )
    }
}

// ──────────────────────────────────────────────────────────────────────
// Botones flotantes (T-8.5, T-8.6)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BotonesFlotantes(
    modifier: Modifier = Modifier,
    habilitadoConfirmar: Boolean,
    onConfirmar: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F7F7).copy(alpha = 0.95f))
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Button(
            onClick = onConfirmar,
            enabled = habilitadoConfirmar,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AguaMedia)
        ) {
            Text("Confirmar datos", fontFamily = FuenteTexto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
