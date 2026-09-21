package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_info
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Tenue

private val FORMA_BOTON = RoundedCornerShape(16.dp)
private val SOMBRA_BOTON = Color(0x5912A1AD)

/** El recordatorio de privacidad del Figma: solo se guarda el sector, no la coordenada exacta. */
@Composable
fun NotaPrivacidad(texto: String, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Tenue).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(painterResource(Res.drawable.ic_info), contentDescription = null, modifier = Modifier.size(18.dp), tint = AguaMedia)
        Text(texto, fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 17.sp, color = AguaMedia)
    }
}

@Composable
fun BotonConfirmarSector(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().height(56.dp)
            .shadow(8.dp, FORMA_BOTON, ambientColor = SOMBRA_BOTON, spotColor = SOMBRA_BOTON)
            .clip(FORMA_BOTON).background(Brush.horizontalGradient(listOf(AguaMedia, Agua)))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("Confirmar sector", fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Blanco)
    }
}
