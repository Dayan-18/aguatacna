package pe.edu.upt.aguatacna.feature.sector.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aguatacna.shared.generated.resources.Res
import aguatacna.shared.generated.resources.ic_atras
import org.jetbrains.compose.resources.painterResource
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue

@Composable
fun RegistrarDomicilioScreen(onVolver: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize().background(Fondo).verticalScroll(rememberScrollState())
    ) {
        EncabezadoRegistro(onVolver)
        VistaPreviaSectorMapa(
            etiquetaSector = "SECTOR 04",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
        BotonesUbicacion(
            onUsarUbicacion = {},
            onMarcarEnMapa = {},
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        TarjetaSectorDetectado(
            sectorDetectado = "Ciudad Nueva 04",
            distrito = "Ciudad Nueva",
            continuidad = "4 h/día",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun EncabezadoRegistro(onVolver: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Blanco).statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 22.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        BotonAtras(onVolver)
        Column(Modifier.padding(top = 2.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                "¿Dónde vives?",
                fontFamily = FuenteTexto,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.19).sp,
                color = Tinta
            )
            Text(
                "Para saber a qué sector perteneces",
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TintaTenue
            )
        }
    }
}

@Composable
private fun BotonAtras(onClick: () -> Unit) {
    Box(
        Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(Fondo)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(Res.drawable.ic_atras),
            contentDescription = "Volver",
            modifier = Modifier.size(20.dp),
            tint = Tinta
        )
    }
}
