package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_flecha_abajo
import aguatacna.shared.generated.resources.ic_reloj
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave

private val FONDO_ALERTA = Color(0xFFFCE9E3)

/** Pantalla 13 del Figma: los avisos que la reserva le ha dado al usuario. */
@Composable
fun AvisosScreen(
    onVolver: () -> Unit,
    onAbrir: (DestinoDelAviso) -> Unit,
    viewModel: AvisosViewModel = viewModel { AvisosViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffect(Unit) { onDispose { viewModel.marcarComoLeidos() } }
    AvisosContenido(uiState, onVolver, onAbrir)
}

@Composable
fun AvisosContenido(uiState: AvisosUiState, onVolver: () -> Unit, onAbrir: (DestinoDelAviso) -> Unit) {
    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BarraSuperior("Avisos", "Alertas de tu reserva y de tu sector", onVolver)
        if (!uiState.cargando && uiState.avisos.isEmpty()) {
            SinAvisos()
        } else {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.avisos.forEach { aviso -> TarjetaDeAviso(aviso) { onAbrir(aviso.destino) } }
            }
        }
    }
}

@Composable
private fun TarjetaDeAviso(aviso: AvisoVista, onClick: () -> Unit) {
    val forma = RoundedCornerShape(18.dp)
    val resaltar = aviso.sinLeer && aviso.tipo == TipoDeAviso.AGOTAMIENTO
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .sombraSuave()
            .clip(forma)
            .background(Blanco)
            .then(if (resaltar) Modifier.border(1.5.dp, Coral.copy(alpha = 0.35f), forma) else Modifier)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconoDelAviso(aviso.tipo)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(aviso.titulo, fontFamily = FuenteTexto, fontSize = 12.5.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold, color = Tinta)
            Text(aviso.texto, fontFamily = FuenteTexto, fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
            Text(aviso.cuando, fontFamily = FuenteNumeros, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, color = TintaTenue)
        }
    }
}

@Composable
private fun IconoDelAviso(tipo: TipoDeAviso) {
    val (fondo, icono, color) = when (tipo) {
        TipoDeAviso.AGOTAMIENTO -> Triple(FONDO_ALERTA, Res.drawable.ic_flecha_abajo, Coral)
        TipoDeAviso.CONFIRMAR_LLENADO -> Triple(Tenue, Res.drawable.ic_reloj, AguaMedia)
    }
    Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(fondo), contentAlignment = Alignment.Center) {
        Icon(painterResource(icono), contentDescription = null, modifier = Modifier.size(19.dp), tint = color)
    }
}

@Composable
private fun SinAvisos() {
    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Todavía no tienes avisos", fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Tinta)
        Text(
            "Cuando tu reserva no alcance para llegar al próximo abastecimiento, o haya que confirmar un llenado, te lo diremos aquí.",
            fontFamily = FuenteTexto, fontSize = 12.5.sp, lineHeight = 18.sp, color = TintaSuave
        )
    }
}
