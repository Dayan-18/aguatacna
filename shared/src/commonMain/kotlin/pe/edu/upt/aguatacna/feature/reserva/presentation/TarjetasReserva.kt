package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EstadoProyeccion

@Composable
fun TarjetaProyeccion(vista: ReservaVista, modifier: Modifier = Modifier) {
    val urgente = if (vista.estado == EstadoProyeccion.NO_ALCANZA) Coral else Tinta
    Column(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Blanco).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Proyección de hoy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Tinta)
        FilaDato("Se agota", vista.textoAgotamiento, urgente)
        vista.textoVuelveElAgua?.let { FilaDato("Vuelve el agua", it, Tinta) }
        vista.textoDeficit?.let { FilaDato("Déficit", it, urgente) }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String, colorValor: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, style = MaterialTheme.typography.bodyLarge, color = TintaSuave)
        Text(valor, style = MaterialTheme.typography.bodyLarge, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = colorValor)
    }
}

@Composable
fun TarjetasDeConsumo(vista: ReservaVista, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TarjetaCifra("${vista.consumoLitrosPorHora}", "L/h", "consumo estimado")
        TarjetaCifra(vista.litrosPorHabitanteDia?.toString() ?: "—", "L/hab·día", "tu promedio")
    }
}

@Composable
private fun RowScope.TarjetaCifra(cifra: String, unidad: String, descripcion: String) {
    Column(
        modifier = Modifier.weight(1f).clip(RoundedCornerShape(18.dp)).background(Blanco).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(cifra, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = AguaMedia)
            Text(" $unidad", style = MaterialTheme.typography.bodySmall, color = TintaSuave, modifier = Modifier.padding(bottom = 3.dp))
        }
        Text(descripcion, style = MaterialTheme.typography.bodySmall, color = TintaSuave)
    }
}

@Composable
fun AvisoDeError(mensaje: String, onCerrar: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Coral.copy(alpha = 0.12f)).padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(mensaje, style = MaterialTheme.typography.bodyMedium, color = Tinta, modifier = Modifier.weight(1f))
        TextButton(onClick = onCerrar) { Text("Cerrar", color = AguaMedia) }
    }
}
