// Componentes visuales usados únicamente en la pantalla de captura/revisión de foto (ReciboFotoScreen).
package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador

// ── Colores privados ──────────────────────────────────────────────────────────

private val TipFondo = Color(0x80D9ECEF)
private val TipBorde = Color(0xFFB2D9DE)
private val TipTexto = Color(0xFF215157)
private val DashedBorde = Color(0xFFCFE0E2)
private val DocFondo = Color(0xFFF5FBFB)
private val DocBorde = Color(0xFFD6EDEC)
private val DocBarraTeal = Color(0xFF0A7B83)

// ── TipInformativo / ZonaRetomarFoto ─────────────────────────────────────────

// Banner que aconseja al usuario revisar y corregir cualquier dato.
@Composable
fun TipInformativo(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
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
            text = "Si algún dato salió mal, corrígelo aquí mismo. También puedes escribirlo a mano sin usar la cámara.",
            fontFamily = FuenteTexto,
            fontSize = 12.sp,
            color = TipTexto,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// Zona táctil (borde discontinuo) para retomar la fotografía si no fue satisfactoria.
@Composable
fun ZonaRetomarFoto(
    tieneFoto: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textoBoton = if (tieneFoto) "Tomar otra foto" else "Tomar foto del recibo"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Blanco.copy(alpha = 0.8f))
            .drawBehind {
                drawRoundRect(
                    color = DashedBorde,
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
            text = textoBoton,
            fontFamily = FuenteTexto,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TintaTenue,
            textAlign = TextAlign.Center
        )
    }
}

// ── TarjetaDocumentoRecibo ────────────────────────────────────────────────────

// Tarjeta de presentación del recibo en revisión: ícono de documento, período y número de medidor.
@Composable
fun TarjetaDocumentoRecibo(
    borrador: ReciboBorrador?,
    modifier: Modifier = Modifier
) {
    val mesTexto = borrador?.periodoConsumo?.valor?.displayCompleto ?: "Seleccionar período"
    val suministroTexto = borrador?.numeroMedidor?.valor?.let { "Medidor $it" }
        ?: borrador?.numeroRecibo?.valor?.let { "Recibo N° $it" }
        ?: "Suministro 0412887"

    Row(
        modifier = modifier
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

// ── ListaCamposRevision / FilaCampoRevision ──────────────────────────────────

// Lista de campos detectados por OCR (o ingresados a mano); resalta con advertencia los de baja confianza (< 70%).
@Composable
fun ListaCamposRevision(
    borrador: ReciboBorrador?,
    onFilaClick: (TipoCampoEdicion) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
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
            // Período de consumo
            val periodoTexto = borrador?.periodoConsumo?.valor?.displayCompleto ?: "Seleccionar período"
            FilaCampoRevision(
                etiqueta = "Período de consumo",
                valor = periodoTexto,
                unidad = null,
                colorValor = Tinta,
                esDudoso = borrador?.periodoConsumo?.esDudoso ?: false,
                onClick = { onFilaClick(TipoCampoEdicion.PERIODO) }
            )
            HorizontalDivider(color = Divisor)

            // Lectura anterior (si no es null)
            val anterior = borrador?.lecturaAnteriorM3?.valor
            if (anterior != null) {
                FilaCampoRevision(
                    etiqueta = "Lectura anterior",
                    valor = anterior.toString(),
                    unidad = "m³",
                    colorValor = TintaSuave,
                    esDudoso = borrador.lecturaAnteriorM3.esDudoso,
                    onClick = { onFilaClick(TipoCampoEdicion.LECTURA_ANTERIOR) }
                )
                HorizontalDivider(color = Divisor)
            }

            // Lectura actual (si no es null)
            val actual = borrador?.lecturaActualM3?.valor
            if (actual != null) {
                FilaCampoRevision(
                    etiqueta = "Lectura actual",
                    valor = actual.toString(),
                    unidad = "m³",
                    colorValor = TintaSuave,
                    esDudoso = borrador.lecturaActualM3.esDudoso,
                    onClick = { onFilaClick(TipoCampoEdicion.LECTURA_ACTUAL) }
                )
                HorizontalDivider(color = Divisor)
            }

            // Consumo del período
            val consumo = borrador?.consumoM3?.valor?.let { "$it" } ?: "Sin datos"
            FilaCampoRevision(
                etiqueta = "Consumo del período",
                valor = consumo,
                unidad = if (borrador?.consumoM3?.valor != null) "m³" else null,
                colorValor = AguaMedia,
                esDudoso = borrador?.consumoM3?.esDudoso ?: false,
                onClick = { onFilaClick(TipoCampoEdicion.CONSUMO_M3) }
            )
            HorizontalDivider(color = Divisor)

            // Importe total
            val importe = borrador?.importeTotal?.valor?.formatear() ?: "Sin datos"
            FilaCampoRevision(
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
fun FilaCampoRevision(
    etiqueta: String,
    valor: String,
    unidad: String?,
    colorValor: Color,
    modifier: Modifier = Modifier,
    esDudoso: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                etiqueta,
                fontFamily = FuenteTexto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TintaSuave
            )
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
            Text(
                valor,
                fontFamily = FuenteNumeros,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (esDudoso) Ocre else colorValor
            )
            if (unidad != null) {
                Spacer(Modifier.width(4.dp))
                Text(
                    unidad,
                    fontFamily = FuenteTexto,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TintaTenue
                )
            }
        }
    }
}
