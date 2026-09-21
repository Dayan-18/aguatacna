package pe.edu.upt.aguatacna.feature.bienvenida.presentation

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_check
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.AguaProfunda
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado

private val RADIO = RoundedCornerShape(16.dp)
private val SOMBRA_DEL_BOTON = Color(0x38001A1F)

/** Pantalla 01 del Figma: presenta la app y deja elegir entre empezar sin cuenta o entrar con Google. */
@Composable
fun BienvenidaScreen(
    uiState: AccesoUiState,
    onAlternarConsentimiento: () -> Unit,
    onSinCuenta: () -> Unit,
    onGoogle: () -> Unit,
    onDescartarMensaje: () -> Unit
) {
    IconosClarosEnBarraDeEstado(claros = true)
    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(AguaProfunda, AguaMedia, Agua)))) {
        Circulo(420, Modifier.offset(x = 120.dp, y = (-170).dp))
        Circulo(380, Modifier.offset(x = (-150).dp, y = 520.dp))
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
            Presentacion(Modifier.weight(1f))
            Acciones(uiState, onAlternarConsentimiento, onSinCuenta, onGoogle, onDescartarMensaje)
        }
    }
}

@Composable
private fun Presentacion(modifier: Modifier) {
    Column(modifier.verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(40.dp))
        Logo()
        Spacer(Modifier.height(30.dp))
        Text(
            "Sabe cuánta agua te queda y hasta cuándo te alcanza.",
            fontFamily = FuenteTexto, fontSize = 33.sp, lineHeight = 39.sp, letterSpacing = (-0.8).sp,
            fontWeight = FontWeight.ExtraBold, color = Blanco
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "Aunque el servicio venga por horas. Sin leer el medidor.",
            Modifier.fillMaxWidth(0.9f), fontFamily = FuenteTexto, fontSize = 14.5.sp, lineHeight = 22.sp,
            fontWeight = FontWeight.Medium, color = Blanco.copy(alpha = 0.78f)
        )
        Spacer(Modifier.height(30.dp))
        Column(verticalArrangement = Arrangement.spacedBy(11.dp)) {
            Beneficio(Icons.Filled.TouchApp, "Marcas el llenado", "Un toque cuando llega el agua")
            Beneficio(Icons.Filled.Bolt, "Calcula la proyección", "Sabe a qué hora se te acaba")
            Beneficio(Icons.Filled.Wifi, "Funciona sin internet", "Todo se guarda en tu teléfono")
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun Circulo(tamano: Int, modifier: Modifier) {
    Box(modifier.size(tamano.dp).clip(CircleShape).background(Blanco.copy(alpha = 0.06f)))
}

@Composable
private fun Logo() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(Blanco.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Filled.WaterDrop, contentDescription = null, modifier = Modifier.size(24.dp), tint = Blanco) }
        Text("AguaTacna", fontFamily = FuenteTexto, fontSize = 21.sp, letterSpacing = (-0.4).sp, fontWeight = FontWeight.ExtraBold, color = Blanco)
    }
}

@Composable
private fun Beneficio(icono: ImageVector, titulo: String, detalle: String) {
    Row(
        Modifier.fillMaxWidth().clip(RADIO).background(Blanco.copy(alpha = 0.13f)).padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(Blanco.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) { Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp), tint = Blanco) }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(titulo, fontFamily = FuenteTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Blanco)
            Text(detalle, fontFamily = FuenteTexto, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = Blanco.copy(alpha = 0.72f))
        }
    }
}

@Composable
private fun Acciones(
    uiState: AccesoUiState,
    onAlternarConsentimiento: () -> Unit,
    onSinCuenta: () -> Unit,
    onGoogle: () -> Unit,
    onDescartarMensaje: () -> Unit
) {
    val opacidad = if (uiState.puedeEntrar) 1f else 0.5f
    Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp)) {
        Consentimiento(uiState.consentimiento, onAlternarConsentimiento)
        Spacer(Modifier.height(20.dp))
        uiState.mensaje?.let { Mensaje(it, onDescartarMensaje) }
        Box(
            Modifier.fillMaxWidth().height(54.dp).alpha(opacidad)
                .shadow(8.dp, RADIO, ambientColor = SOMBRA_DEL_BOTON, spotColor = SOMBRA_DEL_BOTON)
                .clip(RADIO).background(Blanco)
                .clickable(enabled = uiState.puedeEntrar, role = Role.Button, onClick = onSinCuenta),
            contentAlignment = Alignment.Center
        ) { Text("Empezar sin cuenta", fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AguaMedia) }
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth().height(50.dp).alpha(opacidad).clip(RADIO)
                .border(1.5.dp, Blanco.copy(alpha = 0.45f), RADIO)
                .clickable(enabled = uiState.puedeEntrar, role = Role.Button, onClick = onGoogle),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("G", fontFamily = FuenteTexto, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = Blanco)
            Text(
                if (uiState.enCurso) "Conectando…" else "Entrar con Google",
                fontFamily = FuenteTexto, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, color = Blanco
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "La cuenta solo hace falta para el ranking, los reportes y sincronizar entre dispositivos.",
            Modifier.fillMaxWidth(), fontFamily = FuenteTexto, fontSize = 10.sp, lineHeight = 14.sp,
            fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, color = Blanco.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun Consentimiento(marcado: Boolean, onAlternar: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(role = Role.Checkbox, onClick = onAlternar)
            .semantics { stateDescription = if (marcado) "Aceptado" else "Sin aceptar" },
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Box(
            Modifier.size(23.dp).clip(RoundedCornerShape(7.dp))
                .background(if (marcado) Blanco else Blanco.copy(alpha = 0.18f))
                .border(1.5.dp, Blanco.copy(alpha = 0.6f), RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (marcado) Icon(painterResource(Res.drawable.ic_check), contentDescription = null, modifier = Modifier.size(15.dp), tint = AguaMedia)
        }
        Text(
            "Acepto que la app guarde en mi teléfono los datos de mi hogar y mi consumo. Puedo borrarlos cuando quiera.",
            fontFamily = FuenteTexto, fontSize = 11.5.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium, color = Blanco.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun Mensaje(texto: String, onDescartar: () -> Unit) {
    Text(
        texto,
        Modifier.fillMaxWidth().padding(bottom = 10.dp).clip(RoundedCornerShape(12.dp)).background(Coral.copy(alpha = 0.9f))
            .clickable(role = Role.Button, onClick = onDescartar).padding(horizontal = 14.dp, vertical = 10.dp),
        fontFamily = FuenteTexto, fontSize = 12.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium, color = Blanco
    )
}
