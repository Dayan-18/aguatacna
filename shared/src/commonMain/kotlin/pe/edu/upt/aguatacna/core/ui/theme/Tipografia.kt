package pe.edu.upt.aguatacna.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// El Figma usa Plus Jakarta Sans para el texto e IBM Plex Mono para las cifras. Mientras sus archivos
// no estén en composeResources/font se usan las fuentes del sistema; al agregarlos solo cambian estas dos líneas.
val FuenteTexto: FontFamily = FontFamily.SansSerif
val FuenteNumeros: FontFamily = FontFamily.Monospace

private val SOMBRA_SUAVE = Color(0x120A2124)

/** La sombra de las tarjetas del Figma: 0 6 18 con el 7 % de una tinta azul oscura. */
fun Modifier.sombraSuave(radio: Dp = 18.dp): Modifier =
    shadow(6.dp, RoundedCornerShape(radio), ambientColor = SOMBRA_SUAVE, spotColor = SOMBRA_SUAVE)
