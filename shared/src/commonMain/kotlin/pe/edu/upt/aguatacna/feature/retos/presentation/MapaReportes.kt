package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reporte
import pe.edu.upt.aguatacna.feature.retos.domain.model.TipoReporte

private const val ESTILO_MAPA = "https://tiles.openfreemap.org/styles/liberty"

@Composable
fun MapaReportes() {
    val camara = rememberCameraState(
        CameraPosition(target = Position(-70.2372, -17.9841), zoom = 12.5)
    )
    MaplibreMap(
        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)),
        baseStyle = BaseStyle.Uri(ESTILO_MAPA),
        cameraState = camara,
        options = MapOptions(gestureOptions = GestureOptions.AllDisabled)
    ) {
        val fuente = rememberGeoJsonSource(
            GeoJsonData.Features(FeatureCollection(reportesVisibles.map { it.aPunto() }))
        )
        CircleLayer(
            id = "incidencias",
            source = fuente,
            color = const(Coral),
            radius = const(8.dp),
            strokeColor = const(Color.White),
            strokeWidth = const(2.dp)
        )
    }
}

private fun Reporte.aPunto() = org.maplibre.spatialk.geojson.Feature(
    geometry = Point(longitud ?: 0.0, latitud ?: 0.0),
    properties = null
)

private val reportesVisibles = listOf(
    Reporte("publico-1", TipoReporte.BAJA_PRESION, "Presión baja", -17.981, -70.231),
    Reporte("publico-2", TipoReporte.CORTE_NO_PROGRAMADO, "Corte reportado", -17.990, -70.244)
)
