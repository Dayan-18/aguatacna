package pe.edu.upt.aguatacna.feature.reserva.presentation

import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.LocalTime
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EstadoProyeccion

private val MARGEN = Modifier.padding(horizontal = 24.dp)

@Composable
fun ReservaScreen(
    onVerCisternas: () -> Unit = {},
    viewModel: ReservaViewModel = viewModel { ReservaViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var registrando by rememberSaveable { mutableStateOf(false) }
    var editandoHogar by rememberSaveable { mutableStateOf(false) }
    var recortando by rememberSaveable { mutableStateOf(false) }
    var sinAgua by rememberSaveable { mutableStateOf(false) }
    var viendoAvisos by rememberSaveable { mutableStateOf(false) }

    if (viendoAvisos) {
        AvisosScreen(
            onVolver = { viendoAvisos = false },
            onAbrir = { destino ->
                viendoAvisos = false
                if (destino == DestinoDelAviso.QUE_RECORTAR) recortando = true
            }
        )
    } else if (sinAgua) {
        SinAguaScreen(onVolver = { sinAgua = false })
    } else if (recortando) {
        QueRecortarScreen(onVolver = { recortando = false }, onVerCisternas = { recortando = false; onVerCisternas() })
    } else if (!uiState.cargando && (!uiState.hogarConfigurado || editandoHogar)) {
        ConfiguracionScreen(
            onListo = { editandoHogar = false },
            onVolver = if (uiState.hogarConfigurado) ({ editandoHogar = false }) else null
        )
    } else if (registrando) {
        RegistrarLlenadoScreen(
            vista = uiState.vista,
            onEvento = { evento ->
                viewModel.alEvento(evento)
                registrando = false
            },
            onVolver = { registrando = false }
        )
    } else {
        ReservaContenido(uiState, viewModel::alEvento, { registrando = true }, { editandoHogar = true }, { recortando = true }, { sinAgua = true }, { viendoAvisos = true })
    }
}

@Composable
fun ReservaContenido(
    uiState: ReservaUiState,
    onEvento: (ReservaEvent) -> Unit,
    onRegistrarLlenado: () -> Unit,
    onEditarHogar: () -> Unit,
    onQueRecortar: () -> Unit,
    onSinAgua: () -> Unit,
    onAvisos: () -> Unit
) {
    if (uiState.cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val vista = uiState.vista
    IconosClarosEnBarraDeEstado(claros = vista != null)
    var corrigiendoHora by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (vista == null) SinDatosDeReserva() else EncabezadoReserva(vista, uiState.avisosSinLeer, onAvisos, onEditarHogar)
        uiState.error?.let { AvisoDeError(it, { onEvento(ReservaEvent.DescartarError) }, MARGEN) }
        if (vista?.horaLlenadoAsumido != null) {
            TarjetaConfirmarLlenado(
                vista.horaLlenadoAsumido,
                onConfirmar = { onEvento(ReservaEvent.ConfirmarLlenadoAsumido) },
                onCorregirHora = { corrigiendoHora = true },
                modifier = MARGEN
            )
        }
        if (vista != null) {
            TarjetaProyeccion(vista, MARGEN)
            TarjetasDeConsumo(vista, MARGEN)
        }
        if (vista?.estado == EstadoProyeccion.NO_ALCANZA && vista.confirmacion == ConfirmacionEstimacion.CONFIRMADA) {
            BotonSecundario("¿Qué puedo recortar?", onQueRecortar, MARGEN)
        }
        BotonPrincipal("Registrar llenado", onRegistrarLlenado, MARGEN)
        if (vista != null) {
            TextButton(onSinAgua, Modifier.align(Alignment.CenterHorizontally)) {
                Text("Me quedé sin agua antes de lo previsto", fontFamily = FuenteTexto, fontSize = 13.sp, color = AguaMedia, fontWeight = FontWeight.SemiBold)
            }
        }
    }
    if (corrigiendoHora) {
        DialogoDeHora(
            titulo = "¿A qué hora llegó el agua?",
            horaInicial = LocalTime(5, 0),
            onConfirmar = { hora ->
                corrigiendoHora = false
                onEvento(ReservaEvent.CorregirHoraDelLlenado(hora))
            },
            onCancelar = { corrigiendoHora = false }
        )
    }
}

@Composable
private fun SinDatosDeReserva() {
    Column(Modifier.fillMaxWidth().statusBarsPadding().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Mi reserva", fontFamily = FuenteTexto, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Text("Aún no tenemos datos de tu reservorio. Registra tu primer llenado para ver cuánto te queda.", fontFamily = FuenteTexto, color = TintaSuave)
    }
}
