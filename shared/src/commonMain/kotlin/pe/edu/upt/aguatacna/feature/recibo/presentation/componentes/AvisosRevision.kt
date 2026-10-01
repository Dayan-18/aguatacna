// Avisos de la pantalla "Revisa tu recibo": duplicado/advertencias, consejo y zona para retomar la foto.
package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Warning
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue

private val TipFondo = Color(0x80D9ECEF)
private val TipBorde = Color(0xFFB2D9DE)
private val TipTexto = Color(0xFF215157)
private val BordePunteado = Color(0xFFCFE0E2)

// Aviso naranja: recibo duplicado o advertencias de coherencia (no bloquean la confirmación).
@Composable
fun AvisoRevision(texto: String) =
    Aviso(texto, Icons.Outlined.Warning, Ocre, OcreOscuro, Modifier.caja(12.dp, Color(0xFFFEF2E4), Color(0xFFF9DFC5)))

// Consejo para corregir cualquier dato o escribirlo a mano.
@Composable
fun TipInformativo() = Aviso(
    "Si algún dato salió mal, corrígelo aquí mismo. También puedes escribirlo a mano sin usar la cámara.",
    Icons.Outlined.Keyboard, AguaMedia, TipTexto, Modifier.caja(12.dp, TipFondo, TipBorde)
)

@Composable
private fun Aviso(texto: String, icono: ImageVector, colorIcono: Color, colorTexto: Color, fondo: Modifier) {
    Row(
        modifier = Modifier.fillMaxWidth().then(fondo).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icono, null, Modifier.size(18.dp), tint = colorIcono)
        Text(texto, style = estilo(12.sp, color = colorTexto), lineHeight = 17.sp, modifier = Modifier.weight(1f))
    }
}

// Zona con borde punteado para volver a tomar la foto.
@Composable
fun ZonaRetomarFoto(tieneFoto: Boolean, onClick: () -> Unit) {
    val texto = if (tieneFoto) "Tomar otra foto" else "Tomar foto del recibo"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Blanco.copy(alpha = 0.8f))
            .drawBehind {
                drawRoundRect(
                    color = BordePunteado,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f))
                )
            }
            .clickable(onClick = onClick)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.PhotoCamera, texto, Modifier.size(32.dp), tint = Color(0xFF8CA3A6))
        Spacer(Modifier.height(6.dp))
        Text(texto, style = estilo(12.sp, FontWeight.SemiBold, TintaTenue), textAlign = TextAlign.Center)
    }
}
