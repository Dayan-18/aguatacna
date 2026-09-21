package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.AguaProfunda
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

@Composable
fun SectorSinHorarioScreen() {
    IconosClarosEnBarraDeEstado(claros = true)
    Column(
        modifier = Modifier.fillMaxSize().background(Fondo).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EncabezadoSinHorario(sectorNombre = "Ciudad Nueva 04", distrito = "Ciudad Nueva", codigoSector = "04")
        TarjetaHorarioVacio(Modifier.padding(horizontal = 20.dp))
    }
}

@Composable
private fun EncabezadoSinHorario(sectorNombre: String, distrito: String, codigoSector: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.linearGradient(listOf(AguaProfunda, AguaMedia)))
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(sectorNombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Blanco)
        Text("Distrito $distrito · sector $codigoSector", style = MaterialTheme.typography.bodyMedium, color = Blanco.copy(alpha = 0.75f))
        PastillaSinHorario()
        CajaProximo()
    }
}

@Composable
private fun PastillaSinHorario() {
    Row(
        Modifier.clip(RoundedCornerShape(50)).background(Blanco).padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(TintaSuave))
        Text("Sin horario cargado", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Tinta)
    }
}

@Composable
private fun CajaProximo() {
    Column(
        Modifier.fillMaxWidth().padding(top = 6.dp).clip(RoundedCornerShape(14.dp))
            .background(Blanco.copy(alpha = 0.14f)).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("PRÓXIMO ABASTECIMIENTO", style = MaterialTheme.typography.labelSmall, color = Blanco.copy(alpha = 0.7f))
        Text("Todavía no lo sabemos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Blanco)
    }
}
