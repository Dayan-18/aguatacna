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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.AguaProfunda
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave

// Colores específicos del diseño que no están en la paleta general
private val OcreFondo = Color(0xFFFDF2E7)
private val OcreBorde = Color(0x33E18228) // 20% opacidad
private val RojoFondo = Color(0xFFFEF2F2)
private val Rojo = Color(0xFFDC2626)
private val TealClaro = Color(0xFFE4F3F4)
private val TealBorde = Color(0x33087E8B)
private val InfoFondo = Color(0xCCDCF0F2) // 80% opacidad
private val InfoBorde = Color(0x33087E8B)

/**
 * Pantalla principal del feature Recibo.
 * Corresponde al diseño de principal.html.
 */
@Composable
fun ReciboPrincipalScreen(
    onVerHistorial: () -> Unit,
    onEscanearRecibo: () -> Unit,
    onLecturaManual: () -> Unit
) {
    IconosClarosEnBarraDeEstado(claros = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──
        EncabezadoRecibo()

        // ── Contenido scrolleable ──
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Tarjeta Recibo Activo (Hero Card) ──
            TarjetaReciboActivo(onVerHistorial)

            // ── Escanear nuevo recibo ──
            TarjetaEscanear(onEscanearRecibo)

            // ── Lectura manual del medidor ──
            TarjetaLecturaManual(onLecturaManual)

            // ── Herramientas y Reportes ──
            SeccionHerramientas()

            // ── Banner informativo Sunass ──
            BannerSunass()

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Header
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun EncabezadoRecibo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Botón volver
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .sombraSuave(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Blanco.copy(alpha = 0.9f))
                    .border(1.dp, Divisor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
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
                    "Recibo y Facturación",
                    fontFamily = FuenteTexto,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Tinta
                )
                Text(
                    "Suministro N° 0412887 · EPS Tacna",
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TintaTenue
                )
            }
        }
        // Botón ayuda
        Box(
            modifier = Modifier
                .size(40.dp)
                .sombraSuave(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Blanco)
                .border(1.dp, Divisor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("?", fontFamily = FuenteTexto, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TintaSuave)
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Tarjeta Recibo Activo (Hero Card)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaReciboActivo(onVerHistorial: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave(26.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
            .padding(20.dp)
    ) {
        // Fila superior: Mes + badge atípico
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Ícono documento
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TealClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📄", fontSize = 20.sp)
                }
                Column {
                    Text(
                        "RECIBO ACTIVO",
                        fontFamily = FuenteTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TintaTenue,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Agosto 2026",
                        fontFamily = FuenteTexto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Tinta
                    )
                }
            }
            // Badge atípico
            BadgeAtipico()
        }

        Spacer(Modifier.height(12.dp))

        // Precio y vencimiento
        HorizontalDivider(color = Divisor)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text("Total a pagar", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TintaTenue)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("S/", fontFamily = FuenteNumeros, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TintaSuave)
                    Spacer(Modifier.width(4.dp))
                    Text("74,20", fontFamily = FuenteNumeros, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Tinta)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Vencimiento", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TintaTenue)
                Spacer(Modifier.height(4.dp))
                Text(
                    "28 Ago 2026",
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Rojo,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RojoFondo)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        HorizontalDivider(color = Divisor)

        Spacer(Modifier.height(10.dp))

        // Métricas: Consumo y Variación
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Consumo facturado
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Fondo)
                    .border(1.dp, Divisor, RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Text("Consumo facturado", fontFamily = FuenteTexto, fontSize = 11.sp, color = TintaSuave)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("33", fontFamily = FuenteNumeros, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Tinta)
                    Spacer(Modifier.width(4.dp))
                    Text("m³", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
                }
            }
            // Variación Sunass
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OcreFondo)
                    .border(1.dp, Color(0xFFFDE0B5), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Text("Variación Sunass", fontFamily = FuenteTexto, fontSize = 11.sp, color = Ocre)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("+106 %", fontFamily = FuenteNumeros, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Ocre)
                    Spacer(Modifier.width(4.dp))
                    Text("vs prom.", fontFamily = FuenteTexto, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Ocre.copy(alpha = 0.7f))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Botón principal naranja: Ver histórico y cómo reclamar
        Button(
            onClick = onVerHistorial,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Ocre)
        ) {
            Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = Blanco)
            Spacer(Modifier.width(8.dp))
            Text(
                "Ver histórico y cómo reclamar",
                fontFamily = FuenteTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        // Botón secundario: Revisar lectura y datos detectados → también va a historial
        OutlinedButton(
            onClick = onVerHistorial,
            modifier = Modifier.fillMaxWidth().height(40.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("👁", fontSize = 14.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                "Revisar lectura y datos detectados",
                fontFamily = FuenteTexto,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = TintaSuave
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Escanear nuevo recibo
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaEscanear(onEscanearRecibo: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícono cámara
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(TealClaro)
                .border(1.dp, TealBorde, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("📷", fontSize = 24.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Escanear nuevo recibo",
                fontFamily = FuenteTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Tinta
            )
            Text(
                "Sube o toma una foto para digitalizar el consumo automáticamente.",
                fontFamily = FuenteTexto,
                fontSize = 11.sp,
                color = TintaSuave,
                maxLines = 2
            )
        }

        Spacer(Modifier.width(8.dp))

        // Botón "+"
        IconButton(
            onClick = onEscanearRecibo,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AguaMedia)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Escanear", tint = Blanco, modifier = Modifier.size(20.dp))
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Lectura manual del medidor (NUEVO botón del mismo tamaño)
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaLecturaManual(onLecturaManual: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .clickable(onClick = onLecturaManual)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícono medidor
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE8F0F1))
                .border(1.dp, Divisor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("🔢", fontSize = 24.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Lectura manual del medidor",
                fontFamily = FuenteTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Tinta
            )
            Text(
                "Ingresa la lectura de tu medidor de agua manualmente.",
                fontFamily = FuenteTexto,
                fontSize = 11.sp,
                color = TintaSuave,
                maxLines = 2
            )
        }

        Spacer(Modifier.width(8.dp))

        // Flecha
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AguaMedia),
            contentAlignment = Alignment.Center
        ) {
            Text("›", fontFamily = FuenteTexto, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Blanco)
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Badge "Atípico"
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BadgeAtipico() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(OcreFondo)
            .border(1.dp, OcreBorde, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Ocre))
        Text("Atípico", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Ocre)
    }
}

// ──────────────────────────────────────────────────────────────────────
// Sección Herramientas y Reportes
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun SeccionHerramientas() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "HERRAMIENTAS Y REPORTES",
                fontFamily = FuenteTexto,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TintaTenue,
                letterSpacing = 1.sp
            )
            Text(
                "EPS Tacna",
                fontFamily = FuenteTexto,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = AguaMedia
            )
        }

        // 1. Histórico de 6 meses
        TarjetaHerramienta(
            icono = "📊",
            iconoFondo = Color(0xFFF0F8F8),
            titulo = "Histórico de 6 meses",
            subtitulo = "Promedio regular: 16 m³",
            trailingContent = {
                // Mini barras
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    val alturas = listOf(14.dp, 12.dp, 14.dp, 16.dp, 16.dp)
                    alturas.forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(h)
                                .clip(RoundedCornerShape(50))
                                .background(AguaMedia.copy(alpha = 0.7f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Ocre)
                    )
                }
            }
        )

        // 2. Tus reclamos Sunass
        TarjetaHerramienta(
            icono = "🛡",
            iconoFondo = Color(0xFFFFF7ED),
            titulo = "Tus reclamos Sunass",
            subtitulo = "Paso 1: Inspección técnica domiciliaria",
            badgeTexto = "1 ACTIVO",
            trailingContent = {
                Text("›", fontFamily = FuenteTexto, fontSize = 18.sp, color = TintaTenue)
            }
        )

        // 3. Descargar recibo oficial
        TarjetaHerramienta(
            icono = "⬇",
            iconoFondo = Color(0xFFF1F5F9),
            titulo = "Descargar recibo oficial",
            subtitulo = "PDF con validez legal EPS Tacna",
            trailingContent = {
                Text(
                    "PDF 420 KB",
                    fontFamily = FuenteTexto,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AguaMedia,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TealClaro)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        )
    }
}

@Composable
private fun TarjetaHerramienta(
    icono: String,
    iconoFondo: Color,
    titulo: String,
    subtitulo: String,
    badgeTexto: String? = null,
    trailingContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave(22.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Blanco)
            .border(1.dp, Divisor.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconoFondo),
                contentAlignment = Alignment.Center
            ) {
                Text(icono, fontSize = 18.sp)
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(titulo, fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Tinta)
                    if (badgeTexto != null) {
                        Text(
                            badgeTexto,
                            fontFamily = FuenteTexto,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ocre,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Ocre.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(subtitulo, fontFamily = FuenteTexto, fontSize = 11.sp, color = TintaSuave)
            }
        }
        trailingContent()
    }
}

// ──────────────────────────────────────────────────────────────────────
// Banner informativo Sunass
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BannerSunass() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InfoFondo)
            .border(1.dp, InfoBorde, RoundedCornerShape(20.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Ícono info
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(AguaMedia.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text("ℹ", fontSize = 12.sp, color = AguaMedia)
        }
        Text(
            buildString {
                append("Norma Sunass: ")
                append("Si tu consumo supera el 100% del promedio histórico, la EPS Tacna debe inspeccionar tu predio antes de cualquier corte.")
            },
            fontFamily = FuenteTexto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TintaSuave,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
