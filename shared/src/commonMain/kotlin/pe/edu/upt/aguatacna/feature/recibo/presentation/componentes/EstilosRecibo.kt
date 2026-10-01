// Piezas visuales que comparten todas las pantallas de Recibo: colores, estilo de texto,
// tarjetas, cajas de ícono y etiquetas de sección. Cambiar algo aquí lo cambia en todo el módulo.
package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave

// ── Colores del módulo (los del tema están en core/ui/theme) ─────────────────

internal val BordeTarjeta = Divisor.copy(alpha = 0.5f)
internal val TealClaro = Color(0xFFE4F3F4)
internal val TealBorde = Color(0x33087E8B)
internal val TealTexto = Color(0xFF0A7B83)
internal val TealBarra = Color(0xFF10939C)
internal val VerdeBoton = Color(0xFF098093)
internal val OcreClaro = Color(0xFFFFF7ED)
internal val OcreBorde = Color(0xFFFDE0B5)
internal val OcreOscuro = Color(0xFF7C4D29)
internal val NaranjaBarra = Color(0xFFE18228)
internal val Rojo = Color(0xFFDC2626)
internal val RojoClaro = Color(0xFFFEF2F2)
internal val GrisGrafico = Color(0xFF8899A6)
internal val TintaGrafico = Color(0xFF0F172A)
internal val BordeGrafico = Color(0xFFE8F0F2)
internal val DigitoFondo = Color(0xFFF0F6F8)
internal val DigitoTexto = Color(0xFF14232C)

// ── Texto ─────────────────────────────────────────────────────────────────────

/** Estilo de texto del módulo en una línea: `Text("…", style = estilo(12.sp, FontWeight.Bold, Tinta))`. */
@Composable
internal fun estilo(
    tamano: TextUnit,
    peso: FontWeight = FontWeight.Normal,
    color: Color = Color.Unspecified,
    numeros: Boolean = false
): TextStyle = LocalTextStyle.current.merge(
    TextStyle(
        color = color,
        fontSize = tamano,
        fontWeight = peso,
        fontFamily = if (numeros) FuenteNumeros else FuenteTexto
    )
)

/** Título en mayúsculas sobre una tarjeta ("CAMPOS DETECTADOS", "DETALLE DEL PERÍODO"…). */
@Composable
internal fun EtiquetaSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(texto, style = estilo(11.sp, FontWeight.Bold, TintaTenue), letterSpacing = 1.5.sp, modifier = modifier)
}

// ── Formas ────────────────────────────────────────────────────────────────────

/** Recorta con esquinas redondeadas, pinta el fondo y, si se indica, un borde de 1 dp. */
internal fun Modifier.caja(radio: Dp, fondo: Color, borde: Color? = null): Modifier {
    val forma = RoundedCornerShape(radio)
    return clip(forma).background(fondo).then(if (borde != null) Modifier.border(1.dp, borde, forma) else Modifier)
}

/** Tarjeta blanca con sombra suave, la base de todas las tarjetas del módulo. */
internal fun Modifier.tarjeta(radio: Dp, relleno: Dp, borde: Color? = BordeTarjeta): Modifier =
    sombraSuave(radio).caja(radio, Blanco, borde).padding(relleno)

/** Ícono centrado dentro de una caja de color (la forma y el fondo van en [modifier]). */
@Composable
internal fun IconoEnCaja(
    icono: ImageVector,
    tinte: Color,
    lado: Dp,
    modifier: Modifier = Modifier,
    tamanoIcono: Dp = lado / 2
) {
    Box(modifier.size(lado), contentAlignment = Alignment.Center) {
        Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(tamanoIcono))
    }
}
