package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

@Composable
internal fun InsigniasAhorro(diasConRetos: Int, litrosHoy: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Insignia("Primer ahorro", "Completa tu primer reto", diasConRetos > 0, 1f, "Ganada")
        Insignia("Semana azul", "Cumple al menos un reto durante 7 días", diasConRetos >= 7, (diasConRetos / 7f).coerceAtMost(1f), "$diasConRetos/7 días")
        Insignia("Guardián del agua", "Ahorra 500 L completando retos", litrosHoy >= 500, (litrosHoy / 500f).coerceAtMost(1f), "$litrosHoy/500 L")
    }
}

@Composable
private fun Insignia(titulo: String, requisito: String, ganada: Boolean, progreso: Float, detalle: String) {
    Card(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco), elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).clip(CircleShape).background(if (ganada) Ocre.copy(alpha = 0.16f) else Tenue),
                contentAlignment = Alignment.Center
            ) {
                Icon(if (ganada) Icons.Outlined.EmojiEvents else Icons.Outlined.Lock, null, tint = if (ganada) Ocre else TintaSuave)
            }
            Column(Modifier.padding(start = 11.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(titulo, color = Tinta, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(if (ganada) "GANADA" else detalle, color = if (ganada) Ocre else AguaMedia, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
                Text(requisito, color = TintaSuave, fontSize = 9.sp)
                Box(Modifier.fillMaxWidth().height(5.dp).clip(CircleShape).background(Divisor)) {
                    Box(Modifier.fillMaxWidth(progreso).height(5.dp).background(if (ganada) Ocre else Agua))
                }
            }
        }
    }
}
