package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.*

// Pantalla de historial: gráfico de 6 meses con el límite de consumo; tocar un mes abre su edición.
@Composable
fun ReciboHistorialScreen(
    onVolver: () -> Unit,
    onEditarRecibo: () -> Unit,
    viewModel: HistorialViewModel = viewModel { HistorialViewModel.desdeInyeccion() }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val editar = { periodo: PeriodoConsumo ->
        viewModel.prepararEdicion(periodo, onEditarRecibo)
    }

    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(Fondo).statusBarsPadding().verticalScroll(rememberScrollState())) {
        ReciboBarraSuperior(
            titulo = "Tu histórico",
            subtitulo = "Consumo facturado, últimos 6 meses",
            onVolver = onVolver,
            trailingContent = (state as? HistorialUiState.ConDatos)?.masReciente?.estado?.let { estado ->
                { BadgeEstado(EstiloEstado.desde(estado)) }
            }
        )
        when (val s = state) {
            is HistorialUiState.Cargando -> Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AguaMedia)
            }
            is HistorialUiState.SinHistorial -> Text(
                "Aún no tienes recibos registrados para ver tu histórico de consumo.",
                style = estilo(14.sp, color = TintaSuave),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(32.dp)
            )
            is HistorialUiState.ConDatos -> {
                GraficoBarrasHistorial(s.barras, s.masReciente.promedioPrevio, s.masReciente.periodo, onSeleccionarMes = editar)
                DetallePeriodoHistorial(s.masReciente, onModificarRecibo = editar)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
