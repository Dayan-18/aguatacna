package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_alerta
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave
import pe.edu.upt.aguatacna.feature.reserva.domain.model.TipoLlenado

private val MARGEN = Modifier.padding(horizontal = 24.dp)
private val FONDO_NOTA = Color(0xFFFDF1E1)
private val TEXTO_NOTA = Color(0xFF7A4A12)

/** Pantalla 04 del Figma: el usuario dice qué pasó con el agua de su sector. */
@Composable
fun RegistrarLlenadoScreen(
    vista: ReservaVista?,
    onEvento: (ReservaEvent) -> Unit,
    onVolver: () -> Unit
) {
    IconosClarosEnBarraDeEstado(claros = false)
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BarraSuperior("¿Se llenó el tanque?", "Cuéntanos qué pasó con el agua de tu sector", onVolver)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaDelTanque(vista?.capacidadLitros, MARGEN)
            BotonPrincipal("Sí, está lleno", { onEvento(ReservaEvent.RegistrarLlenado(TipoLlenado.COMPLETO)) }, MARGEN)
            Row(MARGEN, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BotonSecundario("Llenó a la mitad", { onEvento(ReservaEvent.RegistrarLlenado(TipoLlenado.MITAD)) }, Modifier.weight(1f), alto = 48, color = Tinta, borde = Divisor)
                BotonSecundario("No llegó", { onEvento(ReservaEvent.AguaNoLlego) }, Modifier.weight(1f), alto = 48, color = Tinta, borde = Divisor)
            }
            if (vista != null) ConEsteRegistro(vista, MARGEN)
            NotaDeConfirmacion(MARGEN)
        }
    }
}

@Composable
private fun TarjetaDelTanque(capacidadLitros: Int?, modifier: Modifier) {
    Column(
        modifier.fillMaxWidth().sombraSuave().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TanqueLleno()
        capacidadLitros?.let {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                Text(formatearMiles(it), fontFamily = FuenteNumeros, fontSize = 38.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1.14).sp, color = AguaMedia)
                Text("L", Modifier.padding(bottom = 7.dp), fontFamily = FuenteTexto, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TintaTenue)
            }
        }
        Text("capacidad completa de tu tanque", fontFamily = FuenteTexto, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
    }
}

@Composable
private fun ConEsteRegistro(vista: ReservaVista, modifier: Modifier) {
    Column(modifier.fillMaxWidth().sombraSuave().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(18.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("CON ESTE REGISTRO", fontFamily = FuenteTexto, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.9.sp, color = TintaTenue)
        FilaDelRegistro("Reserva", "${formatearMiles(vista.capacidadLitros)} L", AguaMedia)
        FilaDelRegistro("Alcanza hasta", vista.textoAlcanzaHastaSiLlena, Tinta)
    }
}

@Composable
private fun FilaDelRegistro(etiqueta: String, valor: String, colorValor: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, fontFamily = FuenteTexto, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        Text(valor, fontFamily = FuenteNumeros, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorValor)
    }
}

@Composable
private fun NotaDeConfirmacion(modifier: Modifier) {
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(FONDO_NOTA).padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(painterResource(Res.drawable.ic_alerta), contentDescription = null, modifier = Modifier.size(17.dp), tint = Ocre)
        Text(
            "Si no confirmas, la app asume el llenado en el horario del sector y marca la estimación como no confirmada.",
            fontFamily = FuenteTexto, fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, color = TEXTO_NOTA
        )
    }
}
