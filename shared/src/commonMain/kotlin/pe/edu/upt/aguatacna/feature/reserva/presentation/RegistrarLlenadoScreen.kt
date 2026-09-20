package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.AguaProfunda
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado

@Composable
fun RegistrarLlenadoScreen(
    capacidadLitros: Int?,
    onEvento: (ReservaEvent) -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BarraSuperior("¿Se llenó el tanque?", "Cuéntanos qué pasó con el agua", onVolver)
        TanqueCompleto(capacidadLitros, Modifier.padding(horizontal = 20.dp))
        val margen = Modifier.padding(horizontal = 20.dp)
        BotonPrincipal("Sí, está lleno", { onEvento(ReservaEvent.RegistrarLlenado(TipoLlenado.COMPLETO)) }, margen)
        Row(margen, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OpcionSecundaria("Llenó a la mitad", { onEvento(ReservaEvent.RegistrarLlenado(TipoLlenado.MITAD)) }, Modifier.weight(1f))
            OpcionSecundaria("No llegó", { onEvento(ReservaEvent.AguaNoLlego) }, Modifier.weight(1f))
        }
        NotaDeConfirmacion(margen)
    }
}

@Composable
private fun BarraSuperior(titulo: String, subtitulo: String, onVolver: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Blanco).padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onVolver) { Text("‹", style = MaterialTheme.typography.headlineMedium, color = Tinta) }
        Column {
            Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Tinta)
            Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = TintaSuave)
        }
    }
}

@Composable
private fun TanqueCompleto(capacidadLitros: Int?, modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Blanco).padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.clip(RoundedCornerShape(24.dp)).background(AguaProfunda).padding(16.dp)) {
            IndicadorNivelReservorio(fraccion = 1f)
        }
        capacidadLitros?.let {
            Text("${formatearMiles(it)} L", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = AguaMedia)
        }
        Text("capacidad completa de tu tanque", style = MaterialTheme.typography.bodySmall, color = TintaSuave)
    }
}

@Composable
private fun OpcionSecundaria(texto: String, onClick: () -> Unit, modifier: Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(25.dp),
        border = BorderStroke(1.dp, Tenue),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Blanco, contentColor = Tinta)
    ) {
        Text(texto, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun NotaDeConfirmacion(modifier: Modifier) {
    Text(
        "Si no confirmas, la app asume el llenado en el horario del sector y marca la estimación como no confirmada.",
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Ocre.copy(alpha = 0.16f)).padding(14.dp),
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Start,
        color = Color(0xFF6B3F08)
    )
}
