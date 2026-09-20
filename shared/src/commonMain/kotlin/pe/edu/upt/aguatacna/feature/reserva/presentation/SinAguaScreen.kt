package pe.edu.upt.aguatacna.feature.reserva.presentation

import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_atras
import aguatacna.shared.generated.resources.ic_gota
import aguatacna.shared.generated.resources.ic_info
import aguatacna.shared.generated.resources.ic_reloj
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.CoralOscuro
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave

private val MARGEN = Modifier.padding(horizontal = 24.dp)
private val DEGRADADO_ROJO = listOf(CoralOscuro, Coral)

/** Pantalla 19 del Figma: el usuario declara que se quedó sin agua. */
@Composable
fun SinAguaScreen(
    onVolver: () -> Unit,
    viewModel: SinAguaViewModel = viewModel { SinAguaViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.cargar() }
    LaunchedEffect(uiState.listo) { if (uiState.listo) onVolver() }
    SinAguaContenido(uiState, viewModel::alEvento, onVolver)
}

@Composable
fun SinAguaContenido(uiState: SinAguaUiState, onEvento: (SinAguaEvent) -> Unit, onVolver: () -> Unit) {
    IconosClarosEnBarraDeEstado(claros = true)
    var eligiendoHora by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EncabezadoSinAgua(onVolver)
        uiState.error?.let { AvisoDeError(it, { onEvento(SinAguaEvent.DescartarError) }, MARGEN) }
        TanqueVacio(MARGEN)
        when {
            uiState.cargando -> Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            uiState.vista != null -> ComparacionDeLoOcurrido(uiState.vista, MARGEN)
        }
        BotonPrincipal("Se acabó ahora", { onEvento(SinAguaEvent.SeAcaboAhora) }, MARGEN, degradado = DEGRADADO_ROJO, sombra = Color(0x4DE3572E))
        BotonSeAcaboAntes({ eligiendoHora = true }, MARGEN)
        NotaDeAprendizaje(MARGEN.padding(bottom = 24.dp))
    }
    if (eligiendoHora) {
        DialogoDeHora(
            titulo = "¿A qué hora se acabó el agua?",
            horaInicial = LocalTime(12, 0),
            onConfirmar = { hora ->
                eligiendoHora = false
                onEvento(SinAguaEvent.SeAcaboAntes(hora))
            },
            onCancelar = { eligiendoHora = false }
        )
    }
}

@Composable
private fun EncabezadoSinAgua(onVolver: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 34.dp, bottomEnd = 34.dp))
            .background(Brush.linearGradient(listOf(CoralOscuro, Coral)))
    ) {
        Box(
            Modifier.align(Alignment.TopStart).offset(x = 190.dp, y = (-120).dp).size(300.dp).clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(Color(0xFFFF9A6B).copy(alpha = 0.4f), Color(0xFFFF9A6B).copy(alpha = 0f))))
        )
        Column(Modifier.statusBarsPadding().padding(start = 20.dp, end = 24.dp, top = 6.dp, bottom = 30.dp)) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(Blanco.copy(alpha = 0.2f)).clickable(role = Role.Button, onClick = onVolver),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(Res.drawable.ic_atras), contentDescription = "Volver", modifier = Modifier.size(20.dp), tint = Blanco)
            }
            Spacer(Modifier.height(18.dp))
            Column(Modifier.padding(start = 4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Te quedaste sin agua", fontFamily = FuenteTexto, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.52).sp, color = Blanco)
                Text("Registra el momento para ajustar tu estimación", fontFamily = FuenteTexto, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = Blanco.copy(alpha = 0.82f))
                Spacer(Modifier.height(8.dp))
                PildoraAgotada()
            }
        }
    }
}

@Composable
private fun PildoraAgotada() {
    Row(
        Modifier.clip(RoundedCornerShape(99.dp)).background(Blanco).padding(start = 10.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(Coral))
        Text("Reserva agotada", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Coral)
    }
}

@Composable
private fun TanqueVacio(modifier: Modifier) {
    Column(
        modifier.fillMaxWidth().sombraSuave().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier.width(84.dp).height(100.dp).clip(RoundedCornerShape(18.dp)).background(Fondo).border(2.dp, Divisor, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(Res.drawable.ic_gota), contentDescription = null, modifier = Modifier.size(26.dp), tint = TintaTenue)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
            Text("0", fontFamily = FuenteNumeros, fontSize = 34.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1.02).sp, color = Coral)
            Text("L", Modifier.padding(bottom = 6.dp), fontFamily = FuenteTexto, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TintaTenue)
        }
        Text("se agotó antes de lo previsto", fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
    }
}

@Composable
private fun ComparacionDeLoOcurrido(vista: SinAguaVista, modifier: Modifier) {
    Column(modifier.fillMaxWidth().sombraSuave().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(18.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
        FilaDeComparacion("Proyectábamos que duraba hasta", vista.textoProyectado, Tinta)
        FilaDeComparacion("Se acabó", vista.textoSeAcabo, Coral)
        FilaDeComparacion("Diferencia", vista.textoDiferencia, if (vista.seAcaboAntes) Coral else Tinta)
        HorizontalDivider(color = Divisor)
        FilaDeComparacion("Consumo estimado", vista.textoConsumo, AguaMedia)
    }
}

@Composable
private fun FilaDeComparacion(etiqueta: String, valor: String, colorValor: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, fontFamily = FuenteTexto, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        Text(valor, fontFamily = FuenteNumeros, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorValor)
    }
}

@Composable
private fun BotonSeAcaboAntes(onClick: () -> Unit, modifier: Modifier) {
    val forma = RoundedCornerShape(16.dp)
    Row(
        modifier.fillMaxWidth().height(48.dp).clip(forma).background(Blanco).border(1.5.dp, Divisor, forma).clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(Res.drawable.ic_reloj), contentDescription = null, modifier = Modifier.size(18.dp), tint = Tinta)
        Text("Se acabó antes", fontFamily = FuenteTexto, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Tinta)
    }
}

@Composable
private fun NotaDeAprendizaje(modifier: Modifier) {
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Tenue).padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(painterResource(Res.drawable.ic_info), contentDescription = null, modifier = Modifier.size(17.dp), tint = AguaMedia)
        Text(
            "Con este registro la app aprende tu consumo real y ajusta la proyección del próximo día.",
            fontFamily = FuenteTexto, fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, color = AguaMedia
        )
    }
}
