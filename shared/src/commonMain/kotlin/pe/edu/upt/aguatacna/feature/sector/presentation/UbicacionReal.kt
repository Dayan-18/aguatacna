package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.runtime.Composable
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada

sealed interface ResultadoUbicacion {
    data class Encontrada(val coordenada: Coordenada) : ResultadoUbicacion
    /** El servicio de ubicación del teléfono está desactivado. */
    data object GpsApagado : ResultadoUbicacion
    /** La ubicación está activa pero no llegó ninguna lectura a tiempo. */
    data object SinSenal : ResultadoUbicacion
}

/**
 * Devuelve una acción que pide la ubicación real del dispositivo.
 * Sin permiso, lo solicita; con permiso, avisa con [onBuscando] y entrega el resultado.
 */
@Composable
expect fun rememberSolicitarUbicacion(
    onBuscando: () -> Unit,
    onResultado: (ResultadoUbicacion) -> Unit
): () -> Unit
