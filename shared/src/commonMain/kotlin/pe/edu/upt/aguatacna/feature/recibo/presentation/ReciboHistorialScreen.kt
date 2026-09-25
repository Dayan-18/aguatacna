package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.*

// Pantalla de historial: gráfico de 6 meses con umbral atípico, selección de mes y guía de reclamos Sunass.
@Composable
fun ReciboHistorialScreen(
    onVolver: () -> Unit,
    onModificarRecibo: (PeriodoConsumo) -> Unit = {},
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
        when (val state = uiState) {
            is HistorialUiState.Cargando -> {
                ReciboBarraSuperior(
                    titulo = "Tu histórico",
                    subtitulo = "Consumo facturado, últimos 6 meses",
                    onVolver = onVolver
                )
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AguaMedia)
                }
            }

            is HistorialUiState.SinHistorial -> {
                ReciboBarraSuperior(
                    titulo = "Tu histórico",
                    subtitulo = "Consumo facturado, últimos 6 meses",
                    onVolver = onVolver
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
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
            }

            is HistorialUiState.ConDatos -> {
                val estiloSeleccionado = EstiloEstado.desde(state.estadoSeleccionado ?: pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo.SinHistorial(0))

                ReciboBarraSuperior(
                    titulo = "Tu histórico",
                    subtitulo = "Consumo facturado, últimos 6 meses",
                    onVolver = onVolver,
                    trailingContent = {
                        BadgeEstado(estilo = estiloSeleccionado)
                    }
                )

                GraficoBarrasHistorial(
                    barras = state.barras,
                    promedioM3 = state.promedioHistorico,
                    umbralAtipicoM3 = state.umbralAtipicoM3,
                    mesSeleccionado = state.mesSeleccionado,
                    onSeleccionarMes = { viewModel.seleccionarMes(it) }
                )

                AlertaEstadoHistorial(
                    estado = state.estadoSeleccionado,
                    mes = state.mesSeleccionado,
                    promedio = state.promedioHistoricoSeleccionado,
                    exceso = state.excesoPorcentajeSeleccionado,
                    esAtipico = state.esAtipico,
                    onComoReclamar = { viewModel.mostrarDialogoReclamo(true) }
                )

                DetallePeriodoHistorial(
                    mes = state.mesSeleccionado,
                    consumo = state.consumoSeleccionado,
                    promedio = state.promedioHistoricoSeleccionado,
                    exceso = state.excesoPorcentajeSeleccionado,
                    importe = state.importeSeleccionado,
                    esAtipico = state.esAtipico,
                    onModificarRecibo = onModificarRecibo
                )

                BotonesAccionHistorial(
                    esAtipico = state.esAtipico,
                    onComoReclamar = { viewModel.mostrarDialogoReclamo(true) }
                )

                if (state.mostrarDialogoReclamo) {
                    DialogoComoReclamar(onDismiss = { viewModel.mostrarDialogoReclamo(false) })
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BotonesAccionHistorial(
    esAtipico: Boolean,
    onComoReclamar: () -> Unit
) {
    if (esAtipico) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp)
        ) {
            Button(
                onClick = onComoReclamar,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Ocre)
            ) {
                Text(
                    "Cómo reclamar",
                    fontFamily = FuenteTexto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
