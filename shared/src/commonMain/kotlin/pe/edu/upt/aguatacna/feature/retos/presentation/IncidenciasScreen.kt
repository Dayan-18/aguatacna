package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reporte
import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector

@Composable
fun IncidenciasScreen(reportes: List<Reporte>, sectorActual: Sector?, onVolver: () -> Unit, onReportar: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Fondo).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) { AhorroTopBar("Incidencias", "Reportes de la comunidad", onVolver) }
            Text("● 18 activas", Modifier.clip(RoundedCornerShape(12.dp)).background(Tenue).padding(horizontal = 9.dp, vertical = 5.dp), color = AguaMedia, fontSize = 8.sp)
        }
        MapaReportes()
        Column(Modifier.padding(top = 13.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            reportes.forEach { reporte ->
                IncidenciaCard(reporteAUi(reporte, sectorActual), reporte.fotoBytes?.let(::decodificarFoto))
            }
            incidencias.forEach { IncidenciaCard(it) }
        }
        Button(
            onClick = onReportar, modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 18.dp).height(50.dp),
            shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = Agua)
        ) { Text("＋  Reportar una incidencia", fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
private fun IncidenciaCard(item: IncidenciaUi, foto: ImageBitmap? = null) {
    Card(
        Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco), elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            foto?.let {
                Image(
                    it, "Fotografía del reporte",
                    Modifier.size(52.dp).padding(end = 9.dp).clip(RoundedCornerShape(9.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(item.titulo, color = Tinta, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(item.direccion, color = TintaSuave, fontSize = 9.sp)
                Text(item.detalle, color = TintaSuave, fontSize = 8.sp)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(item.estado, Modifier.clip(RoundedCornerShape(10.dp)).background(item.color.copy(alpha = 0.13f)).padding(horizontal = 8.dp, vertical = 4.dp), color = item.color, fontSize = 8.sp)
                Text(item.distancia, color = AguaMedia, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private data class IncidenciaUi(
    val titulo: String, val direccion: String, val detalle: String,
    val estado: String, val distancia: String, val color: androidx.compose.ui.graphics.Color
)

private val incidencias = listOf(
    IncidenciaUi("Fuga en la vía", "Av. Municipal", "hace 2 días · 3 vecinos", "Sin atender", "0,4 km", Coral),
    IncidenciaUi("Rotura de tubería", "Jr. Tarapacá 210", "ayer · 5 vecinos", "En revisión", "1,1 km", Ocre),
    IncidenciaUi("Desperdicio", "Parque Central", "hace 6 días · 2 vecinos", "Resuelto", "1,8 km", Agua)
)

private fun reporteAUi(reporte: Reporte, sector: Sector?) = IncidenciaUi(
    titulo = etiquetaTipo(reporte.tipo),
    direccion = sector?.let { "${it.nombre} · ${it.distrito}" }
        ?: if (reporte.latitud != null) "Ubicación registrada" else "Ubicación no disponible",
    detalle = if (reporte.fotoUri != null) "ahora · con fotografía" else "ahora · sin fotografía",
    estado = "Pendiente",
    distancia = "0,0 km",
    color = Ocre
)
