package pe.edu.upt.aguatacna.core.ui.theme

import androidx.compose.runtime.Composable

/**
 * Los íconos de la barra de estado (hora, batería) deben verse sobre el fondo de la pantalla:
 * claros sobre las cabeceras oscuras y oscuros sobre las claras. Cada pantalla indica cuál necesita.
 */
@Composable
expect fun IconosClarosEnBarraDeEstado(claros: Boolean)
