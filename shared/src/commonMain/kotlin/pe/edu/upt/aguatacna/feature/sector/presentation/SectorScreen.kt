package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoConfirmacion

@Composable
fun SectorScreen(
    viewModel: SectorViewModel = viewModel { SectorViewModel.conDatosDePrueba() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mapaAbierto by rememberSaveable { mutableStateOf(false) }
    val casa = uiState.ubicacionCasa

    SectorContenido(uiState, onConfirmar = viewModel::confirmar, onAbrirMapa = { mapaAbierto = true })
    if (mapaAbierto && casa != null) {
        MapaCompleto(casa, uiState.cisternas, onVolver = { mapaAbierto = false })
    }
}

@Composable
fun SectorContenido(
    uiState: SectorUiState,
    onConfirmar: (TipoConfirmacion) -> Unit,
    onAbrirMapa: () -> Unit
) {
    val sector = uiState.sector
    val ahora = uiState.ahora
    if (uiState.cargando || sector == null || ahora == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (uiState.cargando) CircularProgressIndicator() else Text("No encontramos tu sector")
        }
        return
    }
    val margen = Modifier.padding(horizontal = 20.dp)

    // Column con scroll y no LazyColumn: una lista perezosa destruye el mapa al salir
    // de la pantalla y MapLibre pierde sus marcadores al recrearlo.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EncabezadoSector(sector, uiState.cronogramaDeHoy, uiState.proximoAbastecimiento, ahora)
        uiState.ubicacionCasa?.let { casa -> VistaPreviaMapa(casa, uiState.cisternas, onAbrirMapa, margen) }
        TarjetaHorarioDeHoy(uiState.cronogramaDeHoy, uiState.aguaLlegandoAhora, margen)
        TarjetaConfirmacion(uiState.confirmacionesDeHoy, uiState.mensaje, onConfirmar, margen)
        Text(
            "CISTERNAS CERCANAS",
            modifier = margen.padding(top = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        uiState.cisternas.forEach { cisterna -> TarjetaCisterna(cisterna, margen) }
        Spacer(Modifier.height(24.dp))
    }
}
