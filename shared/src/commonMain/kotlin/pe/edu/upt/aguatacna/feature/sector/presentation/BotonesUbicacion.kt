package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import aguatacna.shared.generated.resources.ic_sector
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Tinta

private val FORMA = RoundedCornerShape(16.dp)
private val SOMBRA_BOTON = Color(0x5912A1AD)

/** La fila de acciones de Registrar domicilio: detectar el sector por GPS o elegirlo en el mapa. */
@Composable
fun BotonesUbicacion(
    onUsarUbicacion: () -> Unit,
    onMarcarEnMapa: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BotonUsarUbicacion(onUsarUbicacion, Modifier.weight(1f))
        BotonMarcarEnMapa(onMarcarEnMapa, Modifier.weight(1f))
    }
}

@Composable
private fun BotonUsarUbicacion(onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier.height(54.dp).shadow(8.dp, FORMA, ambientColor = SOMBRA_BOTON, spotColor = SOMBRA_BOTON)
            .clip(FORMA).background(Brush.horizontalGradient(listOf(AguaMedia, Agua)))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(Res.drawable.ic_sector), contentDescription = null, modifier = Modifier.size(18.dp), tint = Blanco)
            Text("Usar mi ubicación", fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Blanco)
        }
    }
}

@Composable
private fun BotonMarcarEnMapa(onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier.height(54.dp).clip(FORMA).background(Blanco).border(BorderStroke(1.5.dp, Divisor), FORMA)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("Marcar en el mapa", fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Tinta)
    }
}
