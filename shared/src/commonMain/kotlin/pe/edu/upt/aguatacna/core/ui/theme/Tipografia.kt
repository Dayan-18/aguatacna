package pe.edu.upt.aguatacna.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ibm_plex_mono_bold
import aguatacna.shared.generated.resources.ibm_plex_mono_medium
import aguatacna.shared.generated.resources.ibm_plex_mono_semibold
import aguatacna.shared.generated.resources.plus_jakarta_sans
import org.jetbrains.compose.resources.Font

private val PESOS_DEL_TEXTO = listOf(FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold, FontWeight.ExtraBold)

/** Plus Jakarta Sans, la fuente de texto del Figma. Es una fuente variable: un solo archivo para todos los pesos. */
val FuenteTexto: FontFamily
    @OptIn(ExperimentalTextApi::class)
    @Composable
    get() {
        val archivo = Res.font.plus_jakarta_sans
        val tipos = PESOS_DEL_TEXTO.map { peso ->
            Font(archivo, weight = peso, variationSettings = FontVariation.Settings(FontVariation.weight(peso.weight)))
        }
        return remember(tipos) { FontFamily(tipos) }
    }

/** IBM Plex Mono, la fuente de las cifras del Figma. */
val FuenteNumeros: FontFamily
    @Composable
    get() {
        val tipos = listOf(
            Font(Res.font.ibm_plex_mono_medium, FontWeight.Medium),
            Font(Res.font.ibm_plex_mono_semibold, FontWeight.SemiBold),
            Font(Res.font.ibm_plex_mono_bold, FontWeight.Bold)
        )
        return remember(tipos) { FontFamily(tipos) }
    }

private val SOMBRA_SUAVE = Color(0x120A2124)

/** La sombra de las tarjetas del Figma: 0 6 18 con el 7 % de una tinta azul oscura. */
fun Modifier.sombraSuave(radio: Dp = 18.dp): Modifier =
    shadow(6.dp, RoundedCornerShape(radio), ambientColor = SOMBRA_SUAVE, spotColor = SOMBRA_SUAVE)
