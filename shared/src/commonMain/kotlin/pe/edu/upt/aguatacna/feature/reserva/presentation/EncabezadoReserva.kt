package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_ajustes
import aguatacna.shared.generated.resources.ic_avisos
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.AguaClara
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.AguaProfunda
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.feature.reserva.domain.model.ConfirmacionEstimacion
import pe.edu.upt.aguatacna.feature.reserva.domain.model.EstadoProyeccion

@Composable
fun EncabezadoReserva(vista: ReservaVista, onAjustes: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .background(Brush.linearGradient(listOf(AguaProfunda, AguaMedia, Agua)))
    ) {
        CirculoDecorativo(Modifier.align(Alignment.TopStart))
        Column(Modifier.statusBarsPadding().padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 40.dp)) {
            FilaSuperior(vista, onAjustes)
            Spacer(Modifier.height(30.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp), verticalAlignment = Alignment.CenterVertically) {
                IndicadorNivelReservorio(vista.porcentaje / 100f)
                LecturaDelNivel(vista)
            }
            Spacer(Modifier.height(28.dp))
            Text(vista.textoLlenado, fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Blanco.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun CirculoDecorativo(modifier: Modifier) {
    Box(
        modifier
            .offset(x = 190.dp, y = (-130).dp)
            .size(340.dp)
            .clip(CircleShape)
            .background(Brush.horizontalGradient(listOf(AguaClara.copy(alpha = 0.5f), AguaClara.copy(alpha = 0f))))
    )
}

@Composable
private fun FilaSuperior(vista: ReservaVista, onAjustes: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 44.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(vista.saludo, fontFamily = FuenteTexto, fontSize = 13.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium, color = Blanco.copy(alpha = 0.75f))
            Text(vista.subtituloHogar, fontFamily = FuenteTexto, fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.Bold, color = Blanco)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BotonDeIcono(painterResource(Res.drawable.ic_avisos), "Avisos") {}
            BotonDeIcono(painterResource(Res.drawable.ic_ajustes), "Editar mi hogar", onAjustes)
        }
    }
}

@Composable
private fun BotonDeIcono(icono: Painter, descripcion: String, onClick: () -> Unit) {
    Box(
        Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(Blanco.copy(alpha = 0.16f)).clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = descripcion, modifier = Modifier.size(20.dp), tint = Blanco)
    }
}

@Composable
private fun LecturaDelNivel(vista: ReservaVista) {
    val sinConfirmar = vista.confirmacion == ConfirmacionEstimacion.NO_CONFIRMADA
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
            Text(
                formatearMiles(vista.nivelLitros), fontFamily = FuenteTexto, fontSize = 60.sp,
                fontWeight = FontWeight.ExtraBold, letterSpacing = (-1.8).sp, color = Blanco
            )
            Text("L", Modifier.padding(bottom = 12.dp), fontFamily = FuenteTexto, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Blanco.copy(alpha = 0.8f))
        }
        Text(
            if (sinConfirmar) "litros estimados" else "litros disponibles",
            fontFamily = FuenteTexto, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Blanco.copy(alpha = 0.75f)
        )
        Spacer(Modifier.height(14.dp))
        EtiquetaDeEstado(vista.estado, sinConfirmar)
        Spacer(Modifier.height(10.dp))
        Text(
            "${vista.porcentaje} % de ${formatearMiles(vista.capacidadLitros)} L",
            fontFamily = FuenteNumeros, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Blanco.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun EtiquetaDeEstado(estado: EstadoProyeccion?, sinConfirmar: Boolean) {
    val (texto, fondo, colorTexto) = when {
        sinConfirmar -> Triple("Sin confirmar", Ocre, Blanco)
        estado == EstadoProyeccion.COMODA -> Triple("Reserva cómoda", Blanco, AguaMedia)
        estado == EstadoProyeccion.AJUSTADA -> Triple("Reserva justa", Ocre, Blanco)
        estado == EstadoProyeccion.NO_ALCANZA -> Triple("No te alcanza", Coral, Blanco)
        else -> Triple("Sin horario del sector", Blanco.copy(alpha = 0.25f), Blanco)
    }
    Row(
        Modifier.clip(RoundedCornerShape(99.dp)).background(fondo).padding(start = 10.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(colorTexto))
        Text(texto, fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colorTexto)
    }
}
