package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
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
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.feature.sector.domain.model.CisternaCercana
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada

// Mapas de OpenStreetMap servidos por OpenFreeMap: gratuitos y sin API key.
private const val ESTILO_MAPA = "https://tiles.openfreemap.org/styles/liberty"

@Composable
fun MapaCisternas(
    casa: Coordenada,
    cisternas: List<CisternaCercana>,
    interactivo: Boolean,
    modifier: Modifier
) {
    val camara = rememberCameraState(
        CameraPosition(target = Position(casa.longitud, casa.latitud), zoom = 12.5)
    )
    val gestos = if (interactivo) GestureOptions.Standard else GestureOptions.AllDisabled

    MaplibreMap(
        modifier = modifier,
        baseStyle = BaseStyle.Uri(ESTILO_MAPA),
        cameraState = camara,
        options = MapOptions(gestureOptions = gestos)
    ) {
        val puntosCisterna = rememberGeoJsonSource(
            GeoJsonData.Features(FeatureCollection(cisternas.map { it.punto.ubicacion.aPunto() }))
        )
        CircleLayer(
            id = "cisternas",
            source = puntosCisterna,
            color = const(Coral),
            radius = const(8.dp),
            strokeColor = const(Blanco),
            strokeWidth = const(2.dp)
        )

        val puntoCasa = rememberGeoJsonSource(GeoJsonData.Features(FeatureCollection(casa.aPunto())))
        CircleLayer(
            id = "casa",
            source = puntoCasa,
            color = const(AguaMedia),
            radius = const(9.dp),
            strokeColor = const(Blanco),
            strokeWidth = const(3.dp)
        )
    }
}

// Mapa fijo dentro de la pantalla de Sector. Una capa encima recibe el toque para que
// el arrastre mueva la página y no el mapa; el zoom se hace en la pantalla completa.
@Composable
fun VistaPreviaMapa(
    casa: Coordenada,
    cisternas: List<CisternaCercana>,
    onAbrir: () -> Unit,
    modifier: Modifier
) {
    Box(modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(18.dp))) {
        MapaCisternas(casa, cisternas, interactivo = false, modifier = Modifier.fillMaxSize())
        Box(Modifier.matchParentSize().clickable(onClick = onAbrir))
        Box(Modifier.align(Alignment.BottomEnd).padding(12.dp)) {
            Etiqueta("Ver mapa completo  ›", fondo = AguaMedia, colorTexto = Blanco)
        }
    }
}

// GeoJSON escribe primero la longitud y después la latitud, al revés de lo habitual.
private fun Coordenada.aPunto() = Feature(geometry = Point(longitud, latitud), properties = null)
