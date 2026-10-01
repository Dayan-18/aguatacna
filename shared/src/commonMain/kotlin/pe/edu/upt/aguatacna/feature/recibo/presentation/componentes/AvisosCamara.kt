// Pantallas de la cámara que no son la cámara misma: leyendo, error de lectura y permisos denegados.
package pe.edu.upt.aguatacna.feature.recibo.presentation.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Fondo
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave

@Composable
fun PantallaAbriendoCamara() {
    Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Outlined.PhotoCamera, null, Modifier.size(36.dp), tint = Blanco)
            Text("Abriendo cámara…", style = estilo(14.sp, color = Blanco))
        }
    }
}

@Composable
fun PantallaProcesandoOcr() {
    Box(Modifier.fillMaxSize().background(TintaGrafico), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(Modifier.size(52.dp), color = AguaMedia, strokeWidth = 3.dp)
            Text("Leyendo recibo…", style = estilo(20.sp, FontWeight.Bold, Blanco))
            Text(
                "Extrayendo consumo, fechas y lecturas automáticamente",
                style = estilo(13.sp, color = Blanco.copy(alpha = 0.8f)),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PantallaErrorLectura(mensaje: String, onReintentar: () -> Unit, onIngresarManual: () -> Unit, onVolver: () -> Unit) {
    TarjetaAvisoCamara(Icons.Outlined.PhotoCamera, Ocre, OcreClaro, "No pudimos leer tu recibo", mensaje) {
        BotonPrincipalAviso("Tomar otra foto", onReintentar)
        BotonSecundarioAviso("Ingresar datos a mano", onIngresarManual, colorTexto = AguaMedia)
        BotonSecundarioAviso("Cancelar", onVolver)
    }
}

@Composable
fun PantallaPermisoDenegado(onReintentar: () -> Unit, onVolver: () -> Unit) {
    TarjetaAvisoCamara(
        Icons.Outlined.PhotoCamera, Ocre, OcreClaro, "Permiso de cámara necesario",
        "Para digitalizar tu recibo de EPS Tacna automáticamente, necesitamos acceso a la cámara."
    ) {
        BotonPrincipalAviso("Permitir acceso", onReintentar)
        BotonSecundarioAviso("Cancelar", onVolver)
    }
}

@Composable
fun PantallaPermisoDenegadoPermanente(onAbrirAjustes: () -> Unit, onVolver: () -> Unit) {
    TarjetaAvisoCamara(
        Icons.Outlined.Lock, Rojo, RojoClaro, "Acceso a la cámara bloqueado",
        "El permiso fue denegado de forma permanente. Para escanear recibos, por favor actívalo en los ajustes de tu dispositivo."
    ) {
        BotonPrincipalAviso("Abrir ajustes", onAbrirAjustes, icono = Icons.Outlined.Settings)
        BotonSecundarioAviso("Volver", onVolver)
    }
}

// Tarjeta centrada común a los avisos de la cámara: ícono, título, mensaje y botones.
@Composable
private fun TarjetaAvisoCamara(
    icono: ImageVector,
    colorIcono: Color,
    fondoIcono: Color,
    titulo: String,
    mensaje: String,
    acciones: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize().background(Fondo).padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth().tarjeta(radio = 24.dp, relleno = 24.dp, borde = null),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconoEnCaja(icono, colorIcono, 56.dp, Modifier.clip(CircleShape).background(fondoIcono))
            Text(titulo, style = estilo(18.sp, FontWeight.Bold, Tinta), textAlign = TextAlign.Center)
            Text(mensaje, style = estilo(13.sp, color = TintaSuave), textAlign = TextAlign.Center, lineHeight = 18.sp)
            Spacer(Modifier.height(4.dp))
            acciones()
        }
    }
}

@Composable
private fun BotonPrincipalAviso(texto: String, onClick: () -> Unit, icono: ImageVector? = null) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AguaMedia)
    ) {
        if (icono != null) {
            Icon(icono, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(texto, style = estilo(14.sp, FontWeight.Bold))
    }
}

@Composable
private fun BotonSecundarioAviso(texto: String, onClick: () -> Unit, colorTexto: Color = TintaSuave) {
    OutlinedButton(onClick, Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp)) {
        Text(texto, style = estilo(14.sp, FontWeight.SemiBold, colorTexto))
    }
}
