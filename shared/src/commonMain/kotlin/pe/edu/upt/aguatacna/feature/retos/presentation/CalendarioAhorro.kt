package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.core.ui.theme.Agua
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

@Composable
internal fun CalendarioAhorro(hoy: LocalDate, cumplidos: Set<LocalDate>) {
    var anioVisible by rememberSaveable { mutableIntStateOf(hoy.year) }
    var mesVisible by rememberSaveable { mutableIntStateOf(hoy.monthNumber) }
    Card(
        Modifier.fillMaxWidth().padding(top = 12.dp), shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco), elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            SelectorMes(
                anioVisible, mesVisible,
                onAnterior = {
                    if (mesVisible == 1) { mesVisible = 12; anioVisible-- } else mesVisible--
                },
                onSiguiente = {
                    if (mesVisible == 12) { mesVisible = 1; anioVisible++ } else mesVisible++
                }
            )
            Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("L", "M", "M", "J", "V", "S", "D").forEach { Text(it, color = TintaSuave, fontSize = 8.sp) }
            }
            val celdas = List(LocalDate(anioVisible, mesVisible, 1).dayOfWeek.ordinal) { 0 } +
                (1..diasDelMes(anioVisible, mesVisible)).toList()
            celdas.chunked(7).forEach { semana ->
                Row(Modifier.fillMaxWidth().padding(top = 7.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    repeat(7) { indice ->
                        val dia = semana.getOrElse(indice) { 0 }
                        val fecha = if (dia > 0) LocalDate(anioVisible, mesVisible, dia) else null
                        DiaCalendario(dia, fecha?.let { it in cumplidos } == true, fecha == hoy)
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectorMes(anio: Int, mes: Int, onAnterior: () -> Unit, onSiguiente: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onAnterior, modifier = Modifier.size(30.dp)) {
            Icon(Icons.Default.ChevronLeft, "Mes anterior", tint = Agua)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("CALENDARIO", color = TintaSuave, fontSize = 8.sp)
            Text("${nombreMes(mes)} $anio", color = Tinta, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = onSiguiente, modifier = Modifier.size(30.dp)) {
            Icon(Icons.Default.ChevronRight, "Mes siguiente", tint = Agua)
        }
    }
}

@Composable
private fun DiaCalendario(dia: Int, cumplido: Boolean, actual: Boolean) {
    Box(
        Modifier.size(27.dp).clip(CircleShape)
            .background(if (cumplido) Agua else Blanco)
            .then(if (actual) Modifier.border(2.dp, Ocre, CircleShape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (dia > 0) Text("$dia", color = if (cumplido) Blanco else if (actual) Ocre else Tinta, fontSize = 8.sp, fontWeight = if (actual) FontWeight.Bold else FontWeight.Normal)
    }
}

private fun diasDelMes(anio: Int, mes: Int): Int = when (mes) {
    2 -> if (anio % 400 == 0 || anio % 4 == 0 && anio % 100 != 0) 29 else 28
    4, 6, 9, 11 -> 30
    else -> 31
}

private fun nombreMes(mes: Int) = listOf(
    "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
    "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"
)[mes - 1]
