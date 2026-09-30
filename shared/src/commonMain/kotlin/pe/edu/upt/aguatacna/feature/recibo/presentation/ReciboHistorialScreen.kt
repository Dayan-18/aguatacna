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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.*

// Pantalla de historial: gráfico de 6 meses con el límite de consumo, selección de mes y edición del recibo.
@Composable
fun ReciboHistorialScreen(
    onVolver: () -> Unit,
    onEditarRecibo: () -> Unit = {},
    viewModel: HistorialViewModel = viewModel { HistorialViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    IconosClarosEnBarraDeEstado(claros = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F7F7))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        val state = uiState
        ReciboBarraSuperior(
            titulo = "Tu histórico",
            subtitulo = "Consumo facturado, últimos 6 meses",
            onVolver = onVolver,
            trailingContent = (state as? HistorialUiState.ConDatos)?.seleccionado?.estado?.let { estado ->
                { BadgeEstado(estilo = EstiloEstado.desde(estado)) }
            }
        )

        when (state) {
            is HistorialUiState.Cargando -> Box(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AguaMedia)
            }

            is HistorialUiState.SinHistorial -> Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Aún no tienes recibos registrados para ver tu histórico de consumo.",
                    fontFamily = FuenteTexto,
                    fontSize = 14.sp,
                    color = TintaSuave,
                    textAlign = TextAlign.Center
                )
            }

            is HistorialUiState.ConDatos -> {
                GraficoBarrasHistorial(
                    barras = state.barras,
                    promedioM3 = state.promedioHistorico,
                    mesSeleccionado = state.seleccionado.periodo,
                    onSeleccionarMes = viewModel::seleccionarMes
                )
                AlertaEstadoHistorial(estado = state.seleccionado.estado)
                DetallePeriodoHistorial(
                    slot = state.seleccionado,
                    onModificarRecibo = { periodo -> viewModel.prepararEdicion(periodo, onEditarRecibo) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
