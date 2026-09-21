package pe.edu.upt.aguatacna.feature.recibo.presentation.comun

import androidx.compose.ui.graphics.Color
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.recibo.domain.model.EstadoConsumo

/**
 * Estilo visual por estado de consumo (T-3.4).
 *
 * Reutiliza exclusivamente los colores existentes del tema:
 * - Naranja (Ocre) para atípico
 * - Teal (AguaMedia) para normal
 * - Neutro (TintaSuave) para sin historial / por promedio
 */
data class EstiloEstado(
    val colorPrincipal: Color,
    val colorFondo: Color,
    val colorBorde: Color,
    val chipTexto: String,
    val chipColorTexto: Color,
    val chipColorFondo: Color,
    val chipColorBorde: Color,
    val variacionColor: Color,
    val botonHistorialTexto: String,
    val botonHistorialColor: Color
) {
    companion object {
        // Fondos derivados (mismos que ya se usan en las pantallas)
        private val OcreFondo = Color(0xFFFDF2E7)
        private val OcreBorde = Color(0x33E18228)
        private val TealFondo = Color(0xFFE4F3F4)
        private val TealBorde = Color(0x33087E8B)
        private val NeutroFondo = Color(0xFFF0F4F5)
        private val NeutroBorde = Color(0x33667788)

        fun desde(estado: EstadoConsumo): EstiloEstado = when (estado) {
            is EstadoConsumo.Atipico -> EstiloEstado(
                colorPrincipal = Ocre,
                colorFondo = OcreFondo,
                colorBorde = Color(0xFFFDE0B5),
                chipTexto = estado.chipTexto,
                chipColorTexto = Ocre,
                chipColorFondo = OcreFondo,
                chipColorBorde = OcreBorde,
                variacionColor = Ocre,
                botonHistorialTexto = "Ver histórico y cómo reclamar",
                botonHistorialColor = Ocre
            )

            is EstadoConsumo.Normal -> EstiloEstado(
                colorPrincipal = AguaMedia,
                colorFondo = TealFondo,
                colorBorde = Color(0xFFCAEBED),
                chipTexto = estado.chipTexto,
                chipColorTexto = AguaMedia,
                chipColorFondo = TealFondo,
                chipColorBorde = TealBorde,
                variacionColor = AguaMedia,
                botonHistorialTexto = "Ver histórico",
                botonHistorialColor = AguaMedia
            )

            is EstadoConsumo.SinHistorial -> EstiloEstado(
                colorPrincipal = AguaMedia,
                colorFondo = TealFondo,
                colorBorde = TealBorde,
                chipTexto = estado.chipTexto,
                chipColorTexto = AguaMedia,
                chipColorFondo = TealFondo,
                chipColorBorde = TealBorde,
                variacionColor = AguaMedia,
                botonHistorialTexto = "Ver histórico",
                botonHistorialColor = AguaMedia
            )

            is EstadoConsumo.FacturadoPorPromedio -> EstiloEstado(
                colorPrincipal = TintaSuave,
                colorFondo = NeutroFondo,
                colorBorde = NeutroBorde,
                chipTexto = EstadoConsumo.FacturadoPorPromedio.chipTexto,
                chipColorTexto = TintaSuave,
                chipColorFondo = NeutroFondo,
                chipColorBorde = NeutroBorde,
                variacionColor = TintaSuave,
                botonHistorialTexto = "Ver histórico",
                botonHistorialColor = AguaMedia
            )
        }
    }
}
