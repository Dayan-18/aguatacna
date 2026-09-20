package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EstadoProyeccion

@Composable
fun TarjetaProyeccion(vista: ReservaVista, modifier: Modifier = Modifier) {
    val estimada = vista.confirmacion == ConfirmacionEstimacion.NO_CONFIRMADA
    val hayDeficit = vista.estado == EstadoProyeccion.NO_ALCANZA
    val colorAgotamiento = when {
        estimada -> Ocre
        hayDeficit -> Coral
        else -> Tinta
    }
    Column(
        modifier = modifier.fillMaxWidth().sombraSuave().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(Modifier.fillMaxWidth().heightIn(min = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Proyección de hoy", fontFamily = FuenteTexto, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Tinta)
            if (estimada) Text("estimada", fontFamily = FuenteTexto, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Ocre)
        }
        HorizontalDivider(color = Divisor)
        FilaDeDato("Se agota", vista.textoAgotamiento, colorAgotamiento)
        vista.textoVuelveElAgua?.let { FilaDeDato("Vuelve el agua", it, Tinta) }
        vista.textoDeficit?.let { FilaDeDato("Déficit", it, if (hayDeficit) Coral else Tinta) }
    }
}

@Composable
private fun FilaDeDato(etiqueta: String, valor: String, colorValor: Color) {
    Row(Modifier.fillMaxWidth().heightIn(min = 22.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, fontFamily = FuenteTexto, fontSize = 12.5.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
        Text(valor, fontFamily = FuenteNumeros, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colorValor)
    }
}

@Composable
fun TarjetasDeConsumo(vista: ReservaVista, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TarjetaCifra("${vista.consumoLitrosPorHora}", "L/h", "consumo estimado")
        TarjetaCifra(vista.litrosPorHabitanteDia?.toString() ?: "—", "L/hab·día", "tu promedio")
    }
}

@Composable
private fun RowScope.TarjetaCifra(cifra: String, unidad: String, descripcion: String) {
    Column(
        modifier = Modifier.weight(1f).sombraSuave().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.Bottom) {
            Text(cifra, fontFamily = FuenteNumeros, fontSize = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.48).sp, color = AguaMedia)
            Text(unidad, Modifier.padding(bottom = 4.dp), fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TintaTenue)
        }
        Text(descripcion, fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TintaSuave)
    }
}

@Composable
fun AvisoDeError(mensaje: String, onCerrar: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Coral.copy(alpha = 0.12f)).padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(mensaje, Modifier.weight(1f), fontFamily = FuenteTexto, fontSize = 13.sp, color = Tinta)
        TextButton(onClick = onCerrar) { Text("Cerrar", fontFamily = FuenteTexto, color = AguaMedia) }
    }
}
