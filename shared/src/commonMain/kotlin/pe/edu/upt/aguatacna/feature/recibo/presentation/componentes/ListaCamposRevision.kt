package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador

private data class FilaCampo(
    val etiqueta: String,
    val valor: String,
    val unidad: String?,
    val color: Color,
    val esDudoso: Boolean,
    val campo: TipoCampoEdicion
)

// Campos detectados (o ingresados a mano); los de baja confianza (< 70 %) llevan un ícono de advertencia.
@Composable
fun ListaCamposRevision(borrador: ReciboBorrador?, onFilaClick: (TipoCampoEdicion) -> Unit) {
    val consumo = borrador?.consumoM3?.valor
    val filas = listOf(
        FilaCampo(
            "Período de consumo", borrador?.periodoConsumo?.valor?.displayCompleto ?: "Seleccionar período",
            null, Tinta, borrador?.periodoConsumo?.esDudoso == true, TipoCampoEdicion.PERIODO
        ),
        FilaCampo(
            "Consumo del período", consumo?.toString() ?: "Sin datos",
            consumo?.let { "m³" }, AguaMedia, borrador?.consumoM3?.esDudoso == true, TipoCampoEdicion.CONSUMO_M3
        ),
        FilaCampo(
            "Importe total", borrador?.importeTotal?.valor?.formatear() ?: "Sin datos",
            null, Tinta, borrador?.importeTotal?.esDudoso == true, TipoCampoEdicion.IMPORTE
        )
    )
    Column {
        EtiquetaSeccion("CAMPOS DETECTADOS", Modifier.padding(start = 4.dp, bottom = 8.dp))
        Column(
            modifier = Modifier.fillMaxWidth().tarjeta(radio = 16.dp, relleno = 16.dp, borde = Divisor.copy(alpha = 0.3f)),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            filas.forEachIndexed { indice, fila ->
                if (indice > 0) HorizontalDivider(color = Divisor)
                FilaCampoRevision(fila) { onFilaClick(fila.campo) }
            }
        }
    }
}

@Composable
private fun FilaCampoRevision(fila: FilaCampo, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(fila.etiqueta, style = estilo(14.sp, FontWeight.Medium, TintaSuave))
            if (fila.esDudoso) Icon(Icons.Outlined.Warning, "Dato dudoso", Modifier.size(16.dp), tint = Ocre)
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(fila.valor, style = estilo(14.sp, FontWeight.Bold, if (fila.esDudoso) Ocre else fila.color, numeros = true))
            if (fila.unidad != null) {
                Spacer(Modifier.width(4.dp))
                Text(fila.unidad, style = estilo(12.sp, FontWeight.Medium, TintaTenue))
            }
        }
    }
}
