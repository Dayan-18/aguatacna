package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado

private val MARGEN = Modifier.padding(horizontal = 24.dp)

@Composable
fun ConfiguracionScreen(
    onListo: () -> Unit,
    onVolver: (() -> Unit)? = null,
    viewModel: ConfiguracionViewModel = viewModel { ConfiguracionViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.cargar() }
    LaunchedEffect(uiState.guardado) { if (uiState.guardado) onListo() }
    ConfiguracionContenido(uiState, viewModel::alEvento, onVolver)
}

/** Pantalla 02 del Figma. El botón Continuar queda fijo abajo; el formulario se desplaza. */
@Composable
fun ConfiguracionContenido(
    uiState: ConfiguracionUiState,
    onEvento: (ConfiguracionEvent) -> Unit,
    onVolver: (() -> Unit)?
) {
    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BarraSuperior("Configura tu reserva", "Solo una vez. Después es un toque al día.", onVolver)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 22.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            uiState.error?.let { AvisoDeError(it, { onEvento(ConfiguracionEvent.DescartarError) }, MARGEN) }
            SeccionConfiguracion("Tipo de reservorio", MARGEN) {
                SelectorDeTipo(uiState.tipo) { onEvento(ConfiguracionEvent.ElegirTipo(it)) }
            }
            SeccionConfiguracion("Capacidad", MARGEN) {
                ControlDeCapacidad(uiState.capacidadLitros) { onEvento(ConfiguracionEvent.CambiarCapacidad(it)) }
            }
            SeccionConfiguracion("Habitantes del hogar", MARGEN) {
                ControlDeHabitantes(uiState.habitantes) { onEvento(ConfiguracionEvent.CambiarHabitantes(it)) }
            }
            SeccionConfiguracion("Hábitos · estimación inicial", MARGEN) { TarjetaDeHabitos(uiState, onEvento) }
        }
        BotonPrincipal(
            if (uiState.guardando) "Guardando…" else "Continuar",
            { onEvento(ConfiguracionEvent.Continuar) },
            MARGEN.padding(top = 8.dp, bottom = 14.dp)
        )
    }
}

@Composable
private fun TarjetaDeHabitos(uiState: ConfiguracionUiState, onEvento: (ConfiguracionEvent) -> Unit) {
    TarjetaBlanca {
        FilaDeHabito("Duchas por día") { ControlCompacto(uiState.duchasPorDia) { onEvento(ConfiguracionEvent.CambiarDuchas(it)) } }
        FilaSiNo("Lavadora", uiState.usaLavadora) { onEvento(ConfiguracionEvent.AlternarLavadora) }
        FilaSiNo("Riego de jardín", uiState.riegaJardin) { onEvento(ConfiguracionEvent.AlternarRiego) }
        HorizontalDivider(Modifier.padding(vertical = 4.dp), color = Divisor)
        FilaDeHabito("Consumo estimado") {
            Text(
                uiState.consumoEstimadoLitrosPorHora?.let { "$it L/h" } ?: "—",
                fontFamily = FuenteNumeros, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AguaMedia
            )
        }
    }
}
