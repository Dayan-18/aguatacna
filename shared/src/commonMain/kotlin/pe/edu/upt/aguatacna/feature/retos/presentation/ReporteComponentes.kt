package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
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
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.retos.domain.model.TipoReporte

@Composable
internal fun ZonaFotoReporte(imagen: ImageBitmap?, elegirFoto: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().height(155.dp).clickable(onClick = elegirFoto),
        shape = RoundedCornerShape(15.dp), border = BorderStroke(1.dp, Divisor),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        if (imagen != null) {
            androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth().height(155.dp)) {
                Image(imagen, "Fotografía adjunta", Modifier.fillMaxWidth().height(155.dp), contentScale = ContentScale.Crop)
                Text(
                    "✓ Fotografía adjunta · toca para cambiar",
                    Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(AguaMedia.copy(alpha = 0.86f)).padding(9.dp),
                    color = Blanco, fontSize = 10.sp
                )
            }
        } else {
            Column(
                Modifier.fillMaxWidth().padding(top = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Icon(Icons.Outlined.PhotoCamera, null, tint = AguaMedia, modifier = Modifier.size(28.dp))
                Text("Tomar foto o elegir de la galería", color = TintaSuave, fontSize = 10.sp)
            }
        }
    }
}

@Composable
internal fun TiposReporte(actual: TipoReporte, onCambiar: (TipoReporte) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        listOf(TipoReporte.FUGA, TipoReporte.BAJA_PRESION, TipoReporte.CORTE_NO_PROGRAMADO).forEach { tipo ->
            val seleccionado = tipo == actual
            Card(
                Modifier.weight(1f).height(70.dp).clickable { onCambiar(tipo) },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (seleccionado) Agua else Divisor),
                colors = CardDefaults.cardColors(containerColor = if (seleccionado) Tenue else Blanco)
            ) {
                Column(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (tipo == TipoReporte.FUGA) "⌁" else if (tipo == TipoReporte.BAJA_PRESION) "⌇" else "♨", color = AguaMedia, fontSize = 18.sp)
                    Text(etiquetaTipo(tipo), color = if (seleccionado) AguaMedia else TintaSuave, fontSize = 8.sp)
                }
            }
        }
    }
}

@Composable
internal fun UbicacionReporte(estado: ReporteUbicacionUi) {
    Card(
        Modifier.fillMaxWidth().padding(top = 24.dp), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco), elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DatoReporte(
                "Ubicación",
                estado.sector?.nombre ?: if (estado.buscando) "Cargando…" else "Sin domicilio registrado"
            )
            DatoReporte(
                "Sector",
                estado.sector?.let { "${it.id} · ${it.distrito}" } ?: "Pendiente"
            )
            DatoReporte("Fecha", "Ahora")
            Text(
                estado.mensaje,
                color = if (estado.esError) androidx.compose.material3.MaterialTheme.colorScheme.error else TintaSuave,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun DatoReporte(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = TintaSuave, fontSize = 10.sp)
        Text(valor, color = if (etiqueta.startsWith("Ubicación")) AguaMedia else Tinta, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun AvisoReporte() {
    Row(
        Modifier.fillMaxWidth().padding(top = 10.dp).clip(RoundedCornerShape(13.dp)).background(Tenue).padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Outlined.Info, null, tint = AguaMedia, modifier = Modifier.size(18.dp))
        Text("Tu reporte es público. Se muestra la ubicación de la incidencia, pero nunca tu nombre.", Modifier.padding(start = 8.dp), color = AguaMedia, fontSize = 9.sp)
    }
}
