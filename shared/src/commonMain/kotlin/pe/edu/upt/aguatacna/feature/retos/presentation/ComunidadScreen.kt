package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.retos.domain.model.PosicionSector

@Composable
fun ComunidadScreen(
    posicion: PosicionSector?, onVolver: () -> Unit,
    onVerIncidencias: () -> Unit, onReportar: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(Fondo).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        AhorroTopBar("Tu sector", "Comparación anónima · Ciudad Nueva", onVolver)
        posicion?.let { ConsumoSector(it) }
        EtiquetaSeccion("POSICIÓN EN CIUDAD NUEVA", Modifier.padding(top = 28.dp, bottom = 9.dp))
        PosicionCiudad()
        AvisoPrivacidad()
        EtiquetaSeccion("PARTICIPA EN TU SECTOR", Modifier.padding(top = 20.dp, bottom = 9.dp))
        Button(
            onClick = onVerIncidencias, modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = Agua)
        ) {
            Icon(Icons.Outlined.LocationOn, null, modifier = Modifier.padding(end = 7.dp))
            Text("Ver incidencias reportadas", fontWeight = FontWeight.SemiBold)
        }
        OutlinedButton(
            onClick = onReportar, modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(48.dp),
            shape = RoundedCornerShape(13.dp), border = BorderStroke(1.dp, Divisor),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Blanco, contentColor = Tinta)
        ) { Text("Reportar una fuga en la vía", fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
private fun ConsumoSector(posicion: PosicionSector) {
    Card(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco), elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("TU CONSUMO POR HABITANTE Y DÍA", color = TintaSuave, fontSize = 9.sp)
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.Bottom) {
                Text("${posicion.consumoHogar.valor.toInt()}", color = AguaMedia, fontSize = 39.sp, fontWeight = FontWeight.Bold)
                Text(" L/hab-día", color = TintaSuave, fontSize = 10.sp, modifier = Modifier.padding(bottom = 8.dp))
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    Text("11 % menos", color = AguaMedia, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(Modifier.fillMaxWidth().padding(top = 8.dp).height(10.dp).clip(RoundedCornerShape(8.dp)).background(Tenue)) {
                Box(Modifier.fillMaxWidth(0.72f).height(10.dp).background(Agua))
                Box(Modifier.padding(start = 246.dp).height(16.dp).background(TintaSuave).fillMaxWidth(0.006f))
            }
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0", color = TintaSuave, fontSize = 8.sp)
                Text("promedio del sector  ${posicion.promedioSector.valor.toInt()}", color = TintaSuave, fontSize = 8.sp)
                Text("390", color = TintaSuave, fontSize = 8.sp)
            }
        }
    }
}

@Composable
private fun PosicionCiudad() {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = Blanco)) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DatoSector("Hogares comparados", "128")
            DatoSector("Tu tramo", "30 % que menos consume", destacado = true)
            DatoSector("Mejor tramo del sector", "198 L/hab-día")
        }
    }
}

@Composable
private fun DatoSector(etiqueta: String, valor: String, destacado: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = TintaSuave, fontSize = 10.sp)
        Text(valor, color = if (destacado) AguaMedia else Tinta, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AvisoPrivacidad() {
    Row(
        Modifier.fillMaxWidth().padding(top = 10.dp).clip(RoundedCornerShape(13.dp)).background(Tenue).padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Outlined.Info, null, tint = AguaMedia, modifier = Modifier.padding(end = 8.dp))
        Text("La comparación es anónima: nunca se muestra la identidad de otro hogar, solo promedios del sector.", color = AguaMedia, fontSize = 9.sp)
    }
}
