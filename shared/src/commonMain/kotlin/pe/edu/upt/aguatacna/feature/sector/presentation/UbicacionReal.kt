package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.runtime.Composable
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada

/**
 * Devuelve una acción que pide la ubicación real del dispositivo.
 * En Android solicita el permiso y lee el GPS; en el emulador o sin ubicación disponible
 * entrega `null` (la pantalla cae a una coordenada de prueba). iOS se implementará después.
 */
@Composable
expect fun rememberSolicitarUbicacion(onResultado: (Coordenada?) -> Unit): () -> Unit
