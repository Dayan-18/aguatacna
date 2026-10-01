package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reto

@Composable
internal fun EtiquetaSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(texto, modifier, color = TintaSuave, fontSize = 9.sp, fontWeight = FontWeight.Medium)
}

@Composable
internal fun RetosSemana(retos: List<Reto>, cumplidos: Set<String>, completar: (String) -> Unit) {
    Card(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco), elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            Text("Toca un reto para marcarlo o corregirlo hoy.", color = TintaSuave, fontSize = 9.sp, modifier = Modifier.padding(vertical = 5.dp))
            retos.forEachIndexed { indice, reto ->
                FilaReto(reto, reto.id in cumplidos, completar)
                if (indice < retos.lastIndex) Box(Modifier.fillMaxWidth().padding(start = 32.dp).height(1.dp).background(Divisor))
            }
        }
    }
}

@Composable
private fun FilaReto(reto: Reto, cumplido: Boolean, completar: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { completar(reto.id) }.padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(22.dp).clip(RoundedCornerShape(6.dp))
                .background(if (cumplido) Agua else Blanco),
            contentAlignment = Alignment.Center
        ) {
            if (cumplido) Icon(Icons.Default.Check, null, tint = Blanco, modifier = Modifier.size(14.dp))
            else Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                Card(Modifier.size(20.dp), border = BorderStroke(1.dp, Divisor), colors = CardDefaults.cardColors(containerColor = Blanco)) {}
            }
        }
        Text(reto.titulo, Modifier.padding(start = 10.dp).weight(1f), color = if (cumplido) Tinta else TintaSuave, fontSize = 12.sp)
        Text("${reto.litrosMeta} L", color = if (cumplido) AguaMedia else TintaSuave, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun ResumenAhorro(litrosCalculados: Int) {
    Column(
        Modifier.fillMaxWidth().padding(top = 1.dp).clip(RoundedCornerShape(bottomStart = 17.dp, bottomEnd = 17.dp))
            .background(Tenue).padding(horizontal = 14.dp, vertical = 11.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilaDato("Ahorro acumulado", "$litrosCalculados L")
        FilaDato("Equivale a", equivalenciaReserva(litrosCalculados))
    }
}

private fun equivalenciaReserva(litros: Int): String {
    val minutos = litros * 220 / 300
    return "${minutos / 60} h ${minutos % 60} min de reserva"
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = TintaSuave, fontSize = 10.sp)
        Text(valor, color = AguaMedia, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
