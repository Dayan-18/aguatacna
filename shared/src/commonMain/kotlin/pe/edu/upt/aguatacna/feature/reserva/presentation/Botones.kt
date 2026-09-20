package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto

private val FORMA = RoundedCornerShape(16.dp)
private val SOMBRA_DEL_BOTON = Color(0x5912A1AD)

/** El botón del Figma: degradado horizontal de azul profundo a agua, 16 de radio y sombra turquesa. */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alto: Int = 54,
    degradado: List<Color> = listOf(AguaMedia, Agua),
    sombra: Color = SOMBRA_DEL_BOTON
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(alto.dp)
            .shadow(8.dp, FORMA, ambientColor = sombra, spotColor = sombra)
            .clip(FORMA)
            .background(Brush.horizontalGradient(degradado))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Blanco)
    }
}

@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alto: Int = 54,
    color: Color = Coral,
    borde: Color = color
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(alto.dp)
            .clip(FORMA)
            .background(Blanco)
            .border(BorderStroke(1.5.dp, borde), FORMA)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
