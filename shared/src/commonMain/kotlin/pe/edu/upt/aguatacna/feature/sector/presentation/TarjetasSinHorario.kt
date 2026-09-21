package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import aguatacna.shared.generated.resources.ic_alerta
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue

private val FORMA_BOTON = RoundedCornerShape(16.dp)
private val SOMBRA_BOTON = Color(0x5912A1AD)

/** El horario de hoy cuando el sector aún no tiene cronograma cargado. */
@Composable
fun TarjetaHorarioVacio(modifier: Modifier = Modifier) {
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("HORARIO DE HOY", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TintaSuave)
            Box(
                Modifier.fillMaxWidth().height(34.dp).clip(RoundedCornerShape(8.dp)).background(Tenue),
                contentAlignment = Alignment.Center
            ) {
                Text("sin datos", style = MaterialTheme.typography.bodyMedium, color = TintaTenue)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("0:00", "6:00", "12:00", "18:00", "24:00").forEach {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = TintaSuave)
                }
            }
            Text(
                "EPS Tacna todavía no publicó el horario de tu sector.",
                style = MaterialTheme.typography.bodyMedium,
                color = TintaSuave
            )
        }
    }
}

/** La tarjeta colaborativa: los vecinos confirman la llegada para estimar el horario. */
@Composable
fun TarjetaAyudanos(confirmaciones: Int, meta: Int, onLlegoAgua: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("AYÚDANOS A CONSTRUIRLO", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TintaSuave)
            Text(
                "Cuando llegue el agua a tu casa, tócalo. Con $meta confirmaciones de vecinos estimamos el horario del sector.",
                style = MaterialTheme.typography.bodyMedium,
                color = Tinta
            )
            BotonLlegoAgua(onLlegoAgua)
            ProgresoConfirmaciones(confirmaciones, meta)
        }
    }
}

@Composable
private fun BotonLlegoAgua(onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(54.dp)
            .shadow(8.dp, FORMA_BOTON, ambientColor = SOMBRA_BOTON, spotColor = SOMBRA_BOTON)
            .clip(FORMA_BOTON).background(Brush.horizontalGradient(listOf(AguaMedia, Agua)))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("Llegó el agua", fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Blanco)
    }
}

@Composable
private fun ProgresoConfirmaciones(confirmaciones: Int, meta: Int) {
    val fraccion = (confirmaciones.toFloat() / meta).coerceIn(0f, 1f)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(50)).background(Tenue)) {
            Box(Modifier.fillMaxWidth(fraccion).fillMaxHeight().clip(RoundedCornerShape(50)).background(Agua))
        }
        Text("$confirmaciones de $meta confirmaciones", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = AguaMedia)
    }
}

/** Recuerda que sin horario la reserva no puede proyectar déficit. */
@Composable
fun AvisoReservaSinProyeccion(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Ocre.copy(alpha = 0.14f)).padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(painterResource(Res.drawable.ic_alerta), contentDescription = null, modifier = Modifier.size(18.dp), tint = Ocre)
        Text(
            "Mientras no haya horario, tu reserva se muestra sin proyección de déficit: no hay con qué compararla.",
            style = MaterialTheme.typography.bodySmall,
            color = Tinta
        )
    }
}
