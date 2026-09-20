package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.AguaProfunda
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EstadoProyeccion

@Composable
fun EncabezadoReserva(vista: ReservaVista) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.linearGradient(listOf(AguaProfunda, AguaMedia)))
            .padding(horizontal = 24.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Mi reserva", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Blanco)
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
            IndicadorNivelReservorio(vista.porcentaje / 100f, estimado = vista.confirmacion == ConfirmacionEstimacion.NO_CONFIRMADA)
            NivelEnLitros(vista)
        }
        Text(vista.textoUltimoLlenado, style = MaterialTheme.typography.bodySmall, color = Blanco.copy(alpha = 0.75f))
    }
}

@Composable
private fun NivelEnLitros(vista: ReservaVista) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(formatearMiles(vista.nivelLitros), fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, color = Blanco)
            Text(" L", style = MaterialTheme.typography.titleMedium, color = Blanco.copy(alpha = 0.75f), modifier = Modifier.padding(bottom = 8.dp))
        }
        Text("litros disponibles", style = MaterialTheme.typography.bodyMedium, color = Blanco.copy(alpha = 0.75f))
        Etiqueta(textoDeEstado(vista.estado), colorDeEstado(vista.estado))
        if (vista.confirmacion == ConfirmacionEstimacion.NO_CONFIRMADA) Etiqueta("Estimado, sin confirmar", Ocre)
        Text(
            "${vista.porcentaje} % de ${formatearMiles(vista.capacidadLitros)} L",
            style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, color = Blanco.copy(alpha = 0.85f)
        )
    }
}

@Composable
fun Etiqueta(texto: String, fondo: Color) {
    Text(
        texto,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(fondo).padding(horizontal = 12.dp, vertical = 5.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = Blanco
    )
}

private fun textoDeEstado(estado: EstadoProyeccion?) = when (estado) {
    EstadoProyeccion.COMODA -> "Reserva cómoda"
    EstadoProyeccion.AJUSTADA -> "Reserva justa"
    EstadoProyeccion.NO_ALCANZA -> "No te alcanza"
    null -> "Sin horario del sector"
}

private fun colorDeEstado(estado: EstadoProyeccion?) = when (estado) {
    EstadoProyeccion.COMODA -> AguaMedia
    EstadoProyeccion.AJUSTADA -> Ocre
    EstadoProyeccion.NO_ALCANZA -> Coral
    null -> Blanco.copy(alpha = 0.25f)
}
