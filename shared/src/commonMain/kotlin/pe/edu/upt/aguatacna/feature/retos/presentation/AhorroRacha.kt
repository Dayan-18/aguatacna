package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

@Composable
internal fun RachaSemanal(dias: Int, hoy: LocalDate?, diasCumplidos: Set<LocalDate>) {
    Card(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco), elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("RACHA SEMANAL", color = TintaSuave, fontSize = 9.sp)
                Text("$dias días seguidos", color = Agua, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                val inicio = hoy?.minus(hoy.dayOfWeek.ordinal, DateTimeUnit.DAY)
                listOf("L", "M", "M", "J", "V", "S", "D").forEachIndexed { indice, letra ->
                    val fecha = inicio?.plus(indice, DateTimeUnit.DAY)
                    DiaRacha(letra, fecha?.let { it in diasCumplidos } == true, fecha == hoy)
                }
            }
            Text("Azul: cumpliste un reto  ·  Naranja: hoy", color = TintaSuave, fontSize = 8.sp, modifier = Modifier.padding(top = 9.dp))
        }
    }
}

@Composable
private fun DiaRacha(letra: String, logrado: Boolean, actual: Boolean) {
    val forma = RoundedCornerShape(9.dp)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(width = 36.dp, height = 39.dp).clip(forma)
                .background(if (logrado) Agua else if (actual) Ocre.copy(alpha = 0.16f) else Tenue)
                .then(if (actual) Modifier.border(2.dp, Ocre, forma) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            if (logrado) Icon(Icons.Default.Check, null, tint = Blanco, modifier = Modifier.size(16.dp))
            else Text(if (actual) "HOY" else letra, color = if (actual) Ocre else TintaSuave, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
        Text(letra, color = if (actual) Ocre else TintaSuave, fontSize = 8.sp, modifier = Modifier.padding(top = 3.dp))
    }
}
