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
import androidx.compose.ui.draw.drawBehind
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
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave

// Colores específicos del diseño foto
private val TealBadgeFondo = Color(0xFFDFF4F3)
private val TealBadgeBorde = Color(0xFFCAECEA)
private val TealBadgeTexto = Color(0xFF0A7B83)
private val DocFondo = Color(0xFFF5FBFB)
private val DocBorde = Color(0xFFD6EDEC)
private val DocBarraTeal = Color(0xFF0A7B83)
private val TipFondo = Color(0x80D9ECEF) // 50% opacidad
private val TipBorde = Color(0xFFB2D9DE)
private val TipTexto = Color(0xFF215157)
private val DashedBorde = Color(0xFFCFE0E2)

/**
 * Pantalla de revisión de foto/recibo escaneado.
 * Corresponde al diseño de foto.html.
 */
@Composable
fun ReciboFotoScreen(onVolver: () -> Unit) {
    IconosClarosEnBarraDeEstado(claros = false)
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F7F7))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp) // Espacio para los botones flotantes
        ) {
            // ── Header ──
            EncabezadoFoto(onVolver)

            // ── Contenido ──
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Tarjeta del recibo ──
                TarjetaRecibo()

                // ── Campos detectados ──
                CamposDetectados()

                // ── Tip informativo ──
                TipInformativo()

                // ── Zona retomar foto ──
                ZonaRetomarFoto()
            }
        }

        // ── Botones flotantes ──
        BotonesFlotantes(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ──────────────────────────────────────────────────────────────────────
// Header
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun EncabezadoFoto(onVolver: () -> Unit) {
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
        // Badge "Leído"
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

// ──────────────────────────────────────────────────────────────────────
// Tarjeta del recibo
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaRecibo() {
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
                "Agosto 2026",
                fontFamily = FuenteTexto,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Tinta
            )
            Text(
                "Suministro 0412887",
                fontFamily = FuenteNumeros,
                fontSize = 12.sp,
                color = TintaTenue,
                letterSpacing = 1.sp
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Campos detectados
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun CamposDetectados() {
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
            FilaCampo("Lectura anterior", "1 284", "m³", TintaSuave)
            HorizontalDivider(color = Divisor)
            FilaCampo("Lectura actual", "1 317", "m³", TintaSuave)
            HorizontalDivider(color = Divisor)
            FilaCampo("Consumo del período", "33", "m³", AguaMedia)
            HorizontalDivider(color = Divisor)
            FilaCampo("Importe", "S/ 74,20", null, Tinta)
        }
    }
}

@Composable
private fun FilaCampo(etiqueta: String, valor: String, unidad: String?, colorValor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(etiqueta, fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(valor, fontFamily = FuenteNumeros, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorValor)
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
// Zona retomar foto
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun ZonaRetomarFoto() {
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
            .clickable { /* Solo visual */ }
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.PhotoCamera,
            contentDescription = null,
            tint = Color(0xFF8CA3A6),
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Volver a tomar la foto",
            fontFamily = FuenteTexto,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TintaTenue,
            textAlign = TextAlign.Center
        )
    }
}

// ──────────────────────────────────────────────────────────────────────
// Botones flotantes
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BotonesFlotantes(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F7F7).copy(alpha = 0.95f))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Confirmar
        Button(
            onClick = { /* Solo visual */ },
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AguaMedia)
        ) {
            Text("Confirmar", fontFamily = FuenteTexto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        // Corregir
        OutlinedButton(
            onClick = { /* Solo visual */ },
            modifier = Modifier.weight(1f).height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Corregir", fontFamily = FuenteTexto, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Tinta)
        }
    }
}
