package pe.edu.upt.aguatacna.feature.sector.presentation

import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoConfirmacion

@Composable
fun SectorScreen(
    viewModel: SectorViewModel = viewModel { SectorViewModel.conDatosDePrueba() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mapaAbierto by rememberSaveable { mutableStateOf(false) }
    var verPuntos by rememberSaveable { mutableStateOf(false) }
    val casa = uiState.ubicacionCasa

    if (verPuntos) {
        PuntosCisternaScreen(onVolver = { verPuntos = false })
        return
    }

    SectorContenido(
        uiState,
        onConfirmar = viewModel::confirmar,
        onAbrirMapa = { mapaAbierto = true },
        onVerPuntos = { verPuntos = true }
    )
    if (mapaAbierto && casa != null) {
        MapaCompleto(casa, uiState.cisternas, onVolver = { mapaAbierto = false })
    }
}

@Composable
fun SectorContenido(
    uiState: SectorUiState,
    onConfirmar: (TipoConfirmacion) -> Unit,
    onAbrirMapa: () -> Unit,
    onVerPuntos: () -> Unit
) {
    val sector = uiState.sector
    val ahora = uiState.ahora
    if (uiState.cargando || sector == null || ahora == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (uiState.cargando) CircularProgressIndicator() else Text("No encontramos tu sector")
        }
        return
    }
    IconosClarosEnBarraDeEstado(claros = true)
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
        BotonVerPuntos(uiState.cisternas.size, onVerPuntos, margen)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BotonVerPuntos(cantidad: Int, onClick: () -> Unit, modifier: Modifier) {
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Blanco)
            .border(BorderStroke(1.5.dp, Divisor), RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Ver puntos de cisterna", fontWeight = FontWeight.Bold, color = AguaMedia)
        Text("$cantidad cerca  ›", color = TintaSuave)
    }
}
