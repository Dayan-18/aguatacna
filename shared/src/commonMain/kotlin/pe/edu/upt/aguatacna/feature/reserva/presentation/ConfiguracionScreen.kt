package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

@Composable
fun ConfiguracionScreen(
    onListo: () -> Unit,
    onVolver: (() -> Unit)? = null,
    viewModel: ConfiguracionViewModel = viewModel { ConfiguracionViewModel.conDatosDePrueba() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.cargar() }
    LaunchedEffect(uiState.guardado) { if (uiState.guardado) onListo() }
    ConfiguracionContenido(uiState, viewModel::alEvento, onVolver)
}

@Composable
fun ConfiguracionContenido(
    uiState: ConfiguracionUiState,
    onEvento: (ConfiguracionEvent) -> Unit,
    onVolver: (() -> Unit)?
) {
    val margen = Modifier.padding(horizontal = 20.dp)
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EncabezadoConfiguracion(onVolver)
        uiState.error?.let { AvisoDeError(it, { onEvento(ConfiguracionEvent.DescartarError) }, margen) }
        SeccionConfiguracion("Tipo de reservorio", margen) {
            SelectorDeTipo(uiState.tipo) { onEvento(ConfiguracionEvent.ElegirTipo(it)) }
        }
        SeccionConfiguracion("Capacidad", margen) {
            ControlDeCapacidad(uiState.capacidadLitros) { onEvento(ConfiguracionEvent.CambiarCapacidad(it)) }
        }
        SeccionConfiguracion("Habitantes del hogar", margen) {
            TarjetaBlanca {
                ControlDeCantidad(uiState.habitantes, { onEvento(ConfiguracionEvent.CambiarHabitantes(it)) })
                Text("define los L/hab·día", style = MaterialTheme.typography.bodySmall, color = TintaSuave)
            }
        }
        SeccionConfiguracion("Hábitos · estimación inicial", margen) { TarjetaDeHabitos(uiState, onEvento) }
        BotonPrincipal(if (uiState.guardando) "Guardando…" else "Continuar", { onEvento(ConfiguracionEvent.Continuar) }, margen.padding(bottom = 24.dp))
    }
}

@Composable
private fun EncabezadoConfiguracion(onVolver: (() -> Unit)?) {
    Row(Modifier.fillMaxWidth().background(Blanco).padding(horizontal = 12.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        if (onVolver != null) TextButton(onClick = onVolver) { Text("‹", style = MaterialTheme.typography.headlineMedium, color = Tinta) }
        Column(Modifier.padding(start = if (onVolver == null) 8.dp else 0.dp)) {
            Text("Configura tu reserva", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Tinta)
            Text("Solo una vez. Después es un toque al día.", style = MaterialTheme.typography.bodySmall, color = TintaSuave)
        }
    }
}

@Composable
private fun TarjetaDeHabitos(uiState: ConfiguracionUiState, onEvento: (ConfiguracionEvent) -> Unit) {
    TarjetaBlanca {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Duchas por día", style = MaterialTheme.typography.bodyLarge, color = Tinta)
            ControlDeCantidad(uiState.duchasPorDia, { onEvento(ConfiguracionEvent.CambiarDuchas(it)) })
        }
        FilaConInterruptor("Lavadora", uiState.usaLavadora) { onEvento(ConfiguracionEvent.AlternarLavadora) }
        FilaConInterruptor("Riego de jardín", uiState.riegaJardin) { onEvento(ConfiguracionEvent.AlternarRiego) }
        HorizontalDivider(color = Tenue)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Consumo estimado", style = MaterialTheme.typography.bodyLarge, color = TintaSuave)
            Text(
                uiState.consumoEstimadoLitrosPorHora?.let { "$it L/h" } ?: "—",
                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = AguaMedia
            )
        }
    }
}

@Composable
private fun TarjetaBlanca(contenido: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) { contenido() }
}
