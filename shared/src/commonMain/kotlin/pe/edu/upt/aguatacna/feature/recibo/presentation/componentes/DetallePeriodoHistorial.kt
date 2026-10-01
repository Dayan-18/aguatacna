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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.BarraHistorialSlot

// Desglose del último mes: consumo, promedio de los meses previos, variación, importe y opción de editar.
@Composable
fun DetallePeriodoHistorial(slot: BarraHistorialSlot, onModificarRecibo: (PeriodoConsumo) -> Unit) {
    val destacado = if (slot.esAltoConsumo) Ocre else Tinta
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 20.dp)) {
        EtiquetaSeccion("DETALLE DEL PERÍODO", Modifier.padding(start = 4.dp, bottom = 8.dp))
        Column(
            Modifier.fillMaxWidth().tarjeta(radio = 24.dp, relleno = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FilaDetalle("Consumo de ${slot.periodo.mesLargo.lowercase()}", slot.consumoM3?.let { "$it m³" }, destacado)
            FilaDetalle("Promedio histórico", slot.promedioPrevio?.let { "$it m³" }, Tinta)
            FilaDetalle("Variación", textoVariacion(slot.variacionPorcentaje), destacado)
            HorizontalDivider(color = BordeTarjeta)
            FilaDetalle("Importe facturado", slot.importeTotal?.formatear(), Tinta)
            HorizontalDivider(color = BordeTarjeta)
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { onModificarRecibo(slot.periodo) }
                    .padding(vertical = 4.dp),
                Arrangement.Center,
                Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Edit, null, Modifier.size(14.dp), tint = AguaMedia)
                Spacer(Modifier.width(6.dp))
                Text("Modificar factura", style = estilo(12.sp, FontWeight.SemiBold, AguaMedia))
            }
        }
    }
}

// Fila "etiqueta ...... valor"; si el valor termina en " m³" la unidad se dibuja más pequeña.
@Composable
private fun FilaDetalle(etiqueta: String, valor: String?, color: Color) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(etiqueta, style = estilo(12.sp, FontWeight.Medium, TintaSuave))
        Row(verticalAlignment = Alignment.Bottom) {
            val cifra = valor?.removeSuffix(" m³") ?: "—"
            Text(cifra, style = estilo(14.sp, FontWeight.Bold, color, numeros = true))
            if (valor != null && cifra != valor) {
                Spacer(Modifier.width(3.dp))
                Text("m³", style = estilo(12.sp, FontWeight.SemiBold, color))
            }
        }
    }
}

private fun textoVariacion(porcentaje: Int?): String? = when {
    porcentaje == null -> null
    porcentaje >= 0 -> "+$porcentaje %"
    else -> "$porcentaje %"
}
