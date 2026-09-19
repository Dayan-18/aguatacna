package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.feature.sector.domain.model.CisternaCercana
import pe.edu.upt.aguatacna.feature.sector.domain.model.Cronograma
import pe.edu.upt.aguatacna.feature.sector.domain.model.EstadoCisterna
import pe.edu.upt.aguatacna.feature.sector.domain.model.TipoConfirmacion

@Composable
private fun Tarjeta(modifier: Modifier, contenido: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { contenido() }
    }
}

@Composable
fun Etiqueta(texto: String, fondo: Color, colorTexto: Color) {
    Text(
        texto,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = colorTexto
    )
}

@Composable
fun TarjetaHorarioDeHoy(cronograma: Cronograma?, aguaLlegando: Boolean, modifier: Modifier) {
    Tarjeta(modifier) {
        Rotulo("HORARIO DE HOY")
        if (cronograma == null) {
            Text("Tu sector no tiene horario cargado para hoy")
            return@Tarjeta
        }
        BarraDelDia(cronograma)
        Text(
            "${cronograma.horaInicio.aTexto()} – ${cronograma.horaFin.aTexto()}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AguaMedia
        )
        Text(
            if (aguaLlegando) "El agua está llegando ahora" else "Sin servicio en este momento",
            color = if (aguaLlegando) Agua else TintaSuave
        )
    }
}

// Cada segmento ocupa una fracción del día: el ancho de la barra representa 24 horas.
@Composable
private fun BarraDelDia(cronograma: Cronograma) {
    val inicio = cronograma.horaInicio.toSecondOfDay() / 86_400f
    val fin = cronograma.horaFin.toSecondOfDay() / 86_400f
    Row(Modifier.fillMaxWidth().height(30.dp).clip(RoundedCornerShape(8.dp)).background(Tenue)) {
        Spacer(Modifier.weight(inicio.coerceAtLeast(0.001f)))
        Box(Modifier.weight(fin - inicio).fillMaxHeight().background(Agua))
        Spacer(Modifier.weight((1f - fin).coerceAtLeast(0.001f)))
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf("0:00", "6:00", "12:00", "18:00", "24:00").forEach {
            Text(it, style = MaterialTheme.typography.labelSmall, color = TintaSuave)
        }
    }
}

@Composable
fun TarjetaConfirmacion(
    confirmaciones: Int,
    mensaje: String?,
    onConfirmar: (TipoConfirmacion) -> Unit,
    modifier: Modifier
) {
    Tarjeta(modifier) {
        Rotulo("¿LLEGÓ O SE CORTÓ EL AGUA?")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { onConfirmar(TipoConfirmacion.LLEGADA) }, modifier = Modifier.weight(1f)) {
                Text("Llegó el agua")
            }
            OutlinedButton(onClick = { onConfirmar(TipoConfirmacion.CORTE) }, modifier = Modifier.weight(1f)) {
                Text("Se cortó")
            }
        }
        Text("$confirmaciones confirmaciones hoy en tu sector", color = TintaSuave)
        if (mensaje != null) Text(mensaje, color = Agua, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TarjetaCisterna(cisterna: CisternaCercana, modifier: Modifier) {
    val punto = cisterna.punto
    Tarjeta(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(punto.nombre, fontWeight = FontWeight.Bold)
                Text("Atiende ${punto.horarioInicio.aTexto()} – ${punto.horarioFin.aTexto()}", color = TintaSuave)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(kmATexto(cisterna.distanciaKm), fontWeight = FontWeight.Bold, color = AguaMedia)
                EtiquetaEstado(punto.estado)
            }
        }
    }
}

@Composable
private fun EtiquetaEstado(estado: EstadoCisterna) = when (estado) {
    EstadoCisterna.ACTIVO -> Etiqueta("Activo", fondo = Tenue, colorTexto = AguaMedia)
    EstadoCisterna.EN_RUTA -> Etiqueta("En ruta", fondo = Ocre.copy(alpha = 0.15f), colorTexto = Ocre)
    EstadoCisterna.TERMINADO -> Etiqueta("Terminado", fondo = Tenue, colorTexto = TintaSuave)
}

@Composable
private fun Rotulo(texto: String) {
    Text(texto, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TintaSuave)
}
