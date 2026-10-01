package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.feature.recibo.presentation.TECLA_BORRAR

private val TECLAS = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf(",", "0", TECLA_BORRAR)
)

// Teclado en pantalla (0–9, coma opcional y borrar) para escribir el valor del medidor o del importe.
@Composable
fun TecladoNumericoMedidor(permiteComa: Boolean, onDigitoPulsado: (String) -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TECLAS.forEach { fila ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                fila.forEach { tecla ->
                    val habilitada = tecla != "," || permiteComa
                    OutlinedButton(
                        onClick = { onDigitoPulsado(tecla) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = habilitada,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Blanco,
                            disabledContainerColor = Blanco.copy(alpha = 0.3f)
                        ),
                        border = null
                    ) {
                        if (tecla == TECLA_BORRAR) {
                            Icon(Icons.AutoMirrored.Filled.Backspace, "Borrar", Modifier.size(20.dp), tint = DigitoTexto)
                        } else {
                            val color = if (habilitada) DigitoTexto else DigitoTexto.copy(alpha = 0.25f)
                            Text(tecla, style = estilo(19.sp, FontWeight.Bold, color, numeros = true))
                        }
                    }
                }
            }
        }
    }
}
