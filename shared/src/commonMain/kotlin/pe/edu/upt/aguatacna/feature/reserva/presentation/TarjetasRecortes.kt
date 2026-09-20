package pe.edu.upt.aguatacna.feature.reserva.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Coral
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

@Composable
fun TarjetaDeHorarios(vista: QueRecortarVista, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilaDeValor("Se agota", vista.textoAgotamiento, Coral)
        FilaDeValor("Vuelve el agua", vista.textoVuelveElAgua, Tinta)
    }
}

@Composable
fun ListaDeRecortes(opciones: List<OpcionDeRecorte>, onEvento: (QueRecortarEvent) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("QUÉ PUEDES RECORTAR", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TintaSuave)
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Blanco).padding(vertical = 4.dp)) {
            opciones.forEachIndexed { indice, opcion ->
                if (indice > 0) HorizontalDivider(Modifier.padding(horizontal = 16.dp), color = Tenue)
                FilaDeRecorte(opcion) { onEvento(QueRecortarEvent.Alternar(opcion.recomendacion)) }
            }
        }
    }
}

@Composable
private fun FilaDeRecorte(opcion: OpcionDeRecorte, onAlternar: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = opcion.elegida,
            onCheckedChange = { onAlternar() },
            colors = CheckboxDefaults.colors(checkedColor = AguaMedia)
        )
        Text(opcion.recomendacion.descripcion, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, color = Tinta)
        Text("${opcion.litros} L", Modifier.padding(end = 8.dp), fontFamily = FuenteNumeros, fontWeight = FontWeight.Bold, color = AguaMedia)
    }
}

@Composable
fun ResumenDeAhorro(vista: QueRecortarVista, modifier: Modifier = Modifier) {
    val colorFaltan = if (vista.cubreElDeficit) AguaMedia else Coral
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Tenue).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilaDeValor("Ahorro seleccionado", "${vista.ahorroLitros} L", AguaMedia)
        FilaDeValor("Ganas", vista.textoGanas, AguaMedia)
        FilaDeValor("Aún faltan", vista.textoFaltan, colorFaltan)
    }
}

@Composable
private fun FilaDeValor(etiqueta: String, valor: String, colorValor: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, style = MaterialTheme.typography.bodyLarge, color = TintaSuave)
        Text(valor, style = MaterialTheme.typography.bodyLarge, fontFamily = FuenteNumeros, fontWeight = FontWeight.Bold, color = colorValor)
    }
}
