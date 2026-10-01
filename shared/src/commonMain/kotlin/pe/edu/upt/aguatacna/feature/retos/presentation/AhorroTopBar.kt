package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.Tenue
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

@Composable
internal fun AhorroTopBar(titulo: String, subtitulo: String, onVolver: () -> Unit) {
    Row(
        Modifier.statusBarsPadding().padding(top = 10.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onVolver,
            modifier = Modifier.size(38.dp).clip(CircleShape).background(Tenue)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Tinta, modifier = Modifier.size(19.dp))
        }
        Column(Modifier.padding(start = 10.dp)) {
            Text(titulo, color = Tinta, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(subtitulo, color = TintaSuave, fontSize = 9.sp)
        }
    }
}
