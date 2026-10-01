package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_sector
import org.jetbrains.compose.resources.painterResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada

private const val ESTILO_MAPA = "https://tiles.openfreemap.org/styles/liberty"
private val TACNA_CENTRO = Coordenada(-18.0066, -70.2463)

// GeoJSON escribe primero la longitud y después la latitud.
private fun Coordenada.aPunto() = Feature(geometry = Point(longitud, latitud), properties = null)

/** Mapa real (no interactivo) de la pantalla Registrar domicilio; al tocarlo abre el selector. */
@Composable
fun MapaUbicacionPreview(ubicacion: Coordenada?, onAbrir: () -> Unit, modifier: Modifier = Modifier) {
    val centro = ubicacion ?: TACNA_CENTRO
    val camara = rememberCameraState(CameraPosition(target = Position(centro.longitud, centro.latitud), zoom = 13.0))
    // Cuando llega la ubicación (GPS o pin), la cámara vuela al punto exacto.
    LaunchedEffect(ubicacion) {
        if (ubicacion != null) {
            camara.position = CameraPosition(target = Position(ubicacion.longitud, ubicacion.latitud), zoom = 15.0)
        }
    }
    Box(modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(18.dp))) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            baseStyle = BaseStyle.Uri(ESTILO_MAPA),
            cameraState = camara,
            options = MapOptions(gestureOptions = GestureOptions.AllDisabled)
        ) {
            if (ubicacion != null) {
                val punto = rememberGeoJsonSource(GeoJsonData.Features(FeatureCollection(ubicacion.aPunto())))
                CircleLayer(
                    id = "elegida",
                    source = punto,
                    color = const(AguaMedia),
                    radius = const(9.dp),
                    strokeColor = const(Blanco),
                    strokeWidth = const(3.dp)
                )
            }
        }
        Box(Modifier.matchParentSize().clickable(onClick = onAbrir))
    }
}

/** Mapa a pantalla completa: el usuario mueve el mapa bajo el pin central y confirma el punto. */
@Composable
fun SelectorUbicacion(inicial: Coordenada?, onElegir: (Coordenada) -> Unit, onVolver: () -> Unit) {
    val centro = inicial ?: TACNA_CENTRO
    val camara = rememberCameraState(CameraPosition(target = Position(centro.longitud, centro.latitud), zoom = 14.0))
    Dialog(onDismissRequest = onVolver, properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)) {
        androidx.compose.foundation.layout.Column(Modifier.fillMaxSize().background(Blanco)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onVolver) { Text("← Volver") }
                Text("Marca tu ubicación", fontFamily = FuenteTexto, fontWeight = FontWeight.Bold)
            }
            Box(Modifier.weight(1f).fillMaxWidth()) {
                MaplibreMap(
                    modifier = Modifier.fillMaxSize(),
                    baseStyle = BaseStyle.Uri(ESTILO_MAPA),
                    cameraState = camara,
                    options = MapOptions(gestureOptions = GestureOptions.Standard)
                ) {}
                Icon(
                    painterResource(Res.drawable.ic_sector),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).size(42.dp),
                    tint = AguaMedia
                )
            }
            Box(
                Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(16.dp)).background(AguaMedia)
                    .clickable {
                        val t = camara.position.target
                        onElegir(Coordenada(t.latitude, t.longitude))
                    }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Usar este punto", fontFamily = FuenteTexto, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Blanco)
            }
        }
    }
}
