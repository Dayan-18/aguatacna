package pe.edu.upt.aguatacna.feature.reserva.presentation

import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral

private val CORAL_OSCURO = Color(0xFF7A2410)

@Composable
fun QueRecortarScreen(
    onVolver: () -> Unit,
    onVerCisternas: () -> Unit,
    viewModel: QueRecortarViewModel = viewModel { QueRecortarViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val vista = uiState.vista
    IconosClarosEnBarraDeEstado(claros = vista != null)
    when {
        uiState.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        vista == null -> SinNadaQueRecortar(onVolver)
        else -> QueRecortarContenido(vista, viewModel::alEvento, onVolver, onVerCisternas)
    }
}

@Composable
fun QueRecortarContenido(
    vista: QueRecortarVista,
    onEvento: (QueRecortarEvent) -> Unit,
    onVolver: () -> Unit,
    onVerCisternas: () -> Unit
) {
    val margen = Modifier.padding(horizontal = 20.dp)
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EncabezadoNoAlcanza(vista, onVolver)
        TarjetaDeHorarios(vista, margen)
        ListaDeRecortes(vista.opciones, onEvento, margen)
        ResumenDeAhorro(vista, margen)
        BotonPrincipal("Ver puntos de cisterna cercanos", onVerCisternas, margen.padding(bottom = 24.dp))
    }
}

@Composable
private fun EncabezadoNoAlcanza(vista: QueRecortarVista, onVolver: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.linearGradient(listOf(CORAL_OSCURO, Coral)))
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TextButton(onClick = onVolver) { Text("‹ Volver", color = Blanco) }
        Column(Modifier.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("No te alcanza", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Blanco)
            Text("Tu reserva se agota antes del próximo abastecimiento", style = MaterialTheme.typography.bodyMedium, color = Blanco.copy(alpha = 0.85f))
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Faltan", color = Blanco.copy(alpha = 0.85f))
                Text(vista.textoDeficit, style = MaterialTheme.typography.headlineMedium, fontFamily = FuenteNumeros, fontWeight = FontWeight.Bold, color = Blanco)
            }
        }
    }
}

@Composable
private fun SinNadaQueRecortar(onVolver: () -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onVolver) { Text("‹ Volver") }
        Text("Por ahora no necesitas recortar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Tu reserva alcanza hasta que vuelva el agua, o tu sector aún no tiene horario cargado.")
    }
}
