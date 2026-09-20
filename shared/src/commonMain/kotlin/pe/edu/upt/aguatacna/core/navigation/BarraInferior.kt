package pe.edu.upt.aguatacna.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue

private const val DURACION = 280

@Composable
fun BarraInferior(actual: Destino, onSeleccionar: (Destino) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Blanco)) {
        HorizontalDivider(color = Divisor)
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Destino.entries.forEach { destino ->
                val activa = destino == actual
                // La pestaña activa se lleva más espacio para que quepa su nombre.
                val peso by animateFloatAsState(
                    targetValue = if (activa) 2.1f else 1f,
                    animationSpec = tween(DURACION, easing = FastOutSlowInEasing),
                    label = "peso"
                )
                Pestana(destino, activa, Modifier.weight(peso)) { onSeleccionar(destino) }
            }
        }
    }
}

@Composable
private fun Pestana(
    destino: Destino,
    activa: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val fondo by animateColorAsState(
        targetValue = if (activa) Tenue else Color.Transparent,
        animationSpec = tween(DURACION),
        label = "fondo"
    )
    val color by animateColorAsState(
        targetValue = if (activa) AguaMedia else TintaTenue,
        animationSpec = tween(DURACION),
        label = "color"
    )
    val escala by animateFloatAsState(
        targetValue = if (activa) 1.1f else 1f,
        animationSpec = tween(DURACION, easing = FastOutSlowInEasing),
        label = "escala"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(fondo)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(destino.icono),
            contentDescription = destino.etiqueta,
            modifier = Modifier.size(22.dp).scale(escala),
            tint = color
        )
        AnimatedVisibility(
            visible = activa,
            enter = expandHorizontally(tween(DURACION)) + fadeIn(tween(DURACION)),
            exit = shrinkHorizontally(tween(DURACION)) + fadeOut(tween(DURACION / 2))
        ) {
            Text(
                text = destino.etiqueta,
                modifier = Modifier.padding(start = 7.dp),
                fontFamily = FuenteTexto,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}
