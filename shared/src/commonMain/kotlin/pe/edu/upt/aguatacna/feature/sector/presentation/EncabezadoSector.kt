package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.AguaProfunda
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector

@Composable
fun EncabezadoSector(
    sector: Sector,
    cronogramaDeHoy: Cronograma?,
    proximo: Cronograma?,
    ahora: LocalDateTime
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.linearGradient(listOf(AguaProfunda, AguaMedia)))
            .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(sector.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Blanco)
        Text("Distrito ${sector.distrito}", style = MaterialTheme.typography.bodyMedium, color = Blanco.copy(alpha = 0.75f))
        if (cronogramaDeHoy != null) {
            Etiqueta("${duracionATexto(cronogramaDeHoy.duracionMinutos)} de agua al día", fondo = Ocre, colorTexto = Blanco)
        }
        ProximoAbastecimiento(proximo, ahora)
    }
}

@Composable
private fun ProximoAbastecimiento(proximo: Cronograma?, ahora: LocalDateTime) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Blanco.copy(alpha = 0.14f))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text("PRÓXIMO ABASTECIMIENTO", style = MaterialTheme.typography.labelSmall, color = Blanco.copy(alpha = 0.7f))
            val texto = proximo?.let { "${it.fecha.relativoA(ahora.date)} ${it.horaInicio.aTexto()}" }
            Text(texto ?: "Sin horario cargado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Blanco)
        }
        if (proximo != null) {
            Text("en ${duracionATexto(minutosEntre(ahora, proximo.inicio))}", style = MaterialTheme.typography.bodySmall, color = Blanco.copy(alpha = 0.85f))
        }
    }
}
