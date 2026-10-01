// Chips de estado: el del consumo (Alto consumo / Normal) y los de la revisión (Leído / Manual / Revisa los datos).
package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo

// Colores del chip según el estado de consumo: Ocre si es alto consumo (> 100 m³), AguaMedia si no.
data class EstiloEstado(val texto: String, val color: Color, val fondo: Color, val borde: Color) {
    companion object {
        private val ALTO_CONSUMO = EstiloEstado("Alto consumo", Ocre, Color(0xFFFEF2E6), OcreBorde)
        private val NORMAL = EstiloEstado("Normal", AguaMedia, TealClaro, Color(0xFFCAEBED))

        fun desde(estado: EstadoConsumo): EstiloEstado =
            if (estado == EstadoConsumo.ALTO_CONSUMO) ALTO_CONSUMO else NORMAL
    }
}

@Composable
fun BadgeEstado(estilo: EstiloEstado, modifier: Modifier = Modifier) =
    ChipEstado(estilo.texto, estilo.color, estilo.fondo, estilo.borde, modifier)

// Píldora con un punto de color y un texto corto.
@Composable
fun ChipEstado(texto: String, color: Color, fondo: Color, borde: Color?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.caja(50.dp, fondo, borde).padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(color))
        Text(texto, style = estilo(11.sp, FontWeight.Bold, color), maxLines = 1, softWrap = false)
    }
}
