package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaProfunda
import pe.edu.upt.aguatacna.core.ui.theme.Blanco

@Composable
internal fun CabeceraAhorro(dias: Int, onVerPosicion: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().height(178.dp)
            .background(Brush.horizontalGradient(listOf(AguaProfunda, Agua)))
    ) {
        Box(
            Modifier.size(190.dp).offset(x = 205.dp, y = (-95).dp)
                .clip(CircleShape).background(Color.White.copy(alpha = 0.12f))
        )
        Column(Modifier.statusBarsPadding().padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
            Text("Tu ahorro", color = Blanco, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Esta semana · actualiza tus retos de hoy", color = Blanco.copy(alpha = 0.72f), fontSize = 11.sp)
            Row(
                Modifier.fillMaxWidth().padding(top = 14.dp)
                    .clip(RoundedCornerShape(15.dp)).background(Color.White.copy(alpha = 0.13f))
                    .padding(start = 13.dp, top = 8.dp, bottom = 8.dp, end = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, null, tint = Blanco, modifier = Modifier.size(20.dp))
                    Column(Modifier.padding(start = 7.dp)) {
                        Text("$dias días", color = Blanco, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Text("seguidos en que tu reserva alcanzó", color = Blanco.copy(alpha = 0.68f), fontSize = 8.sp)
                    }
                }
                TextButton(onClick = onVerPosicion) { Text("Ver mi posición  ›", color = Blanco, fontSize = 10.sp) }
            }
        }
    }
}
