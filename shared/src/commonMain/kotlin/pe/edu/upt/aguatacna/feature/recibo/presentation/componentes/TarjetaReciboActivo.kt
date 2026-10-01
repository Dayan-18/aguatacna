package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.feature.recibo.presentation.ReciboUiState

// Tarjeta principal del recibo activo: mes, estado, importe, vencimiento, consumo y acciones.
@Composable
fun TarjetaReciboActivo(
    state: ReciboUiState.ConDatos,
    onVerHistorial: () -> Unit,
    onRevisarLectura: () -> Unit
) {
    val estiloEstado = EstiloEstado.desde(state.estadoConsumo)
    Column(Modifier.fillMaxWidth().tarjeta(radio = 26.dp, relleno = 20.dp)) {
        EncabezadoReciboActivo(state.mes, estiloEstado)
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = Divisor)
        ImporteYVencimiento(state.importeDisplay, state.fechaVencimiento)
        HorizontalDivider(color = Divisor)
        Spacer(Modifier.height(10.dp))
        ConsumoFacturado(state.recibo.consumoM3)
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onVerHistorial,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = estiloEstado.color)
        ) {
            if (state.esAltoConsumo) {
                Icon(Icons.Outlined.ErrorOutline, null, Modifier.size(18.dp), tint = Blanco)
                Spacer(Modifier.width(8.dp))
            }
            Text("Ver histórico de consumo", style = estilo(14.sp, FontWeight.Bold))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onRevisarLectura,
            modifier = Modifier.fillMaxWidth().height(40.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Outlined.Visibility, null, Modifier.size(16.dp), tint = AguaMedia)
            Spacer(Modifier.width(6.dp))
            Text("Revisar lectura y datos detectados", style = estilo(12.sp, FontWeight.SemiBold, TintaSuave))
        }
    }
}

@Composable
private fun EncabezadoReciboActivo(mes: String, estiloEstado: EstiloEstado) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.Top) {
        Row(Modifier.weight(1f), Arrangement.spacedBy(12.dp), Alignment.CenterVertically) {
            IconoEnCaja(
                Icons.AutoMirrored.Outlined.ReceiptLong, AguaMedia, 44.dp,
                Modifier.caja(12.dp, TealClaro), tamanoIcono = 24.dp
            )
            Column {
                Text("RECIBO ACTIVO", style = estilo(11.sp, FontWeight.Bold, TintaTenue), letterSpacing = 1.sp)
                Text(mes, style = estilo(18.sp, FontWeight.ExtraBold, Tinta))
            }
        }
        BadgeEstado(estiloEstado)
    }
}

@Composable
private fun ImporteYVencimiento(importe: String, vencimiento: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), Arrangement.SpaceBetween, Alignment.Bottom) {
        Column {
            Text("Total a pagar", style = estilo(11.sp, FontWeight.SemiBold, TintaTenue))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("S/", style = estilo(18.sp, FontWeight.Bold, TintaSuave, numeros = true))
                Spacer(Modifier.width(4.dp))
                Text(importe, style = estilo(30.sp, FontWeight.ExtraBold, Tinta, numeros = true))
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Vencimiento", style = estilo(11.sp, FontWeight.SemiBold, TintaTenue))
            Spacer(Modifier.height(4.dp))
            Text(
                vencimiento,
                style = estilo(12.sp, FontWeight.Bold, Rojo),
                modifier = Modifier.caja(8.dp, RojoClaro).padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ConsumoFacturado(consumoM3: Int) {
    Column(Modifier.fillMaxWidth().caja(12.dp, Fondo, Divisor).padding(10.dp)) {
        Text("Consumo facturado", style = estilo(11.sp, color = TintaSuave))
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(consumoM3.toString(), style = estilo(14.sp, FontWeight.Bold, Tinta, numeros = true))
            Spacer(Modifier.width(4.dp))
            Text("m³", style = estilo(11.sp, FontWeight.Medium, TintaSuave))
        }
    }
}
