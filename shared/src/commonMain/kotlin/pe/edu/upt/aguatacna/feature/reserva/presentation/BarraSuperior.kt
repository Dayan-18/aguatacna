package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_atras
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue

/** El botón cuadrado de "atrás" del Figma; claro sobre fondos blancos y translúcido sobre las cabeceras de color. */
@Composable
fun BotonAtras(onClick: () -> Unit, modifier: Modifier = Modifier, sobreColor: Boolean = false) {
    val fondo = if (sobreColor) Blanco.copy(alpha = 0.2f) else Fondo
    Box(
        modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(fondo).clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(Res.drawable.ic_atras), contentDescription = "Volver", modifier = Modifier.size(20.dp), tint = if (sobreColor) Blanco else Tinta)
    }
}

/** La barra blanca de las pantallas de formulario: botón atrás (si hay a dónde volver), título y subtítulo. */
@Composable
fun BarraSuperior(titulo: String, subtitulo: String, onVolver: (() -> Unit)?) {
    Row(
        Modifier.fillMaxWidth().background(Blanco).statusBarsPadding().padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        if (onVolver != null) BotonAtras(onVolver)
        Column(Modifier.padding(top = 2.dp, start = if (onVolver == null) 4.dp else 0.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(titulo, fontFamily = FuenteTexto, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.19).sp, color = Tinta)
            Text(subtitulo, fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TintaTenue)
        }
    }
}
