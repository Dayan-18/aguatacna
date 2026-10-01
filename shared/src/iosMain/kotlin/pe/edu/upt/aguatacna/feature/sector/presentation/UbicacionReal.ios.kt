package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.runtime.Composable

// iOS: la ubicación real con CoreLocation queda para después; por ahora pide marcar en el mapa.
@Composable
actual fun rememberSolicitarUbicacion(
    onBuscando: () -> Unit,
    onResultado: (ResultadoUbicacion) -> Unit
): () -> Unit = { onResultado(ResultadoUbicacion.SinSenal) }
