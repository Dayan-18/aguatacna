package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.runtime.Composable
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada

// iOS: la ubicación real con CoreLocation queda para después; por ahora entrega null
// y la pantalla usa la coordenada de prueba.
@Composable
actual fun rememberSolicitarUbicacion(onResultado: (Coordenada?) -> Unit): () -> Unit =
    { onResultado(null) }
