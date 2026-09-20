package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

@Composable
fun ReservaScreen(
    viewModel: ReservaViewModel = viewModel { ReservaViewModel.conDatosDePrueba() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var registrando by rememberSaveable { mutableStateOf(false) }
    var editandoHogar by rememberSaveable { mutableStateOf(false) }

    if (!uiState.cargando && (!uiState.hogarConfigurado || editandoHogar)) {
        ConfiguracionScreen(
            onListo = { editandoHogar = false },
            onVolver = if (uiState.hogarConfigurado) ({ editandoHogar = false }) else null
        )
    } else if (registrando) {
        RegistrarLlenadoScreen(
            capacidadLitros = uiState.vista?.capacidadLitros,
            onEvento = { evento ->
                viewModel.alEvento(evento)
                registrando = false
            },
            onVolver = { registrando = false }
        )
    } else {
        ReservaContenido(uiState, viewModel::alEvento, { registrando = true }, { editandoHogar = true })
    }
}

@Composable
fun ReservaContenido(
    uiState: ReservaUiState,
    onEvento: (ReservaEvent) -> Unit,
    onRegistrarLlenado: () -> Unit,
    onEditarHogar: () -> Unit
) {
    if (uiState.cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val vista = uiState.vista
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (vista == null) SinDatosDeReserva() else EncabezadoReserva(vista)
        val margen = Modifier.padding(horizontal = 20.dp)
        uiState.error?.let { AvisoDeError(it, { onEvento(ReservaEvent.DescartarError) }, margen) }
        if (vista != null) {
            TarjetaProyeccion(vista, margen)
            TarjetasDeConsumo(vista, margen)
        }
        BotonPrincipal("Registrar llenado", onRegistrarLlenado, margen)
        if (vista != null) {
            TextButton({ onEvento(ReservaEvent.MeQuedeSinAgua) }, Modifier.align(Alignment.CenterHorizontally)) {
                Text("Me quedé sin agua antes de lo previsto", color = AguaMedia, fontWeight = FontWeight.SemiBold)
            }
        }
        TextButton(onEditarHogar, Modifier.align(Alignment.CenterHorizontally)) {
            Text("Editar mi hogar", color = TintaSuave)
        }
    }
}

@Composable
fun BotonPrincipal(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(27.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AguaMedia, contentColor = Blanco)
    ) {
        Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SinDatosDeReserva() {
    Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Mi reserva", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
        Text("Aún no tenemos datos de tu reservorio. Registra tu primer llenado para ver cuánto te queda.", color = TintaSuave)
    }
}
