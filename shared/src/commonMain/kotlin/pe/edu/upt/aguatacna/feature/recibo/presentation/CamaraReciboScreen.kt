package pe.edu.upt.aguatacna.feature.recibo.presentation

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.Ocre
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave
import pe.edu.upt.aguatacna.core.util.EstadoPermisoCamara
import pe.edu.upt.aguatacna.core.util.rememberCapturaFoto
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador

// Abre la cámara del sistema y pasa directo a procesamiento OCR, sin doble confirmación.
@Composable
fun CamaraReciboScreen(
    onReciboDetectado: (ReciboBorrador) -> Unit,
    onIngresarManual: () -> Unit,
    onVolver: () -> Unit,
    viewModel: CapturaViewModel = viewModel { CapturaViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val capturaFoto = rememberCapturaFoto(
        onFotoCapturada = { bytes ->
            viewModel.onFotoCapturada(bytes)
        },
        onError = { _ ->
            // Si el usuario canceló la cámara o hubo error al abrir, volver limpiamente
            onVolver()
        }
    )

    // Al entrar a la pantalla, reiniciar estado para evitar mostrar errores previos y abrir cámara
    LaunchedEffect(Unit) {
        viewModel.reiniciar()
        capturaFoto.tomarFoto()
    }

    // Al detectar con éxito, navegar a revisión
    LaunchedEffect(uiState) {
        val state = uiState
        if (state is CapturaUiState.Exito) {
            onReciboDetectado(state.borrador)
        }
    }

    when (val state = uiState) {
        is CapturaUiState.Inactivo -> {
            when (capturaFoto.estado) {
                EstadoPermisoCamara.DenegadoPermanente -> {
                    PantallaPermisoDenegadoPermanente(
                        onAbrirAjustes = { capturaFoto.abrirAjustes() },
                        onVolver = onVolver
                    )
                }

                EstadoPermisoCamara.Denegado -> {
                    PantallaPermisoDenegado(
                        onReintentar = { capturaFoto.tomarFoto() },
                        onVolver = onVolver
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.PhotoCamera,
                                contentDescription = null,
                                tint = Blanco,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                "Abriendo cámara…",
                                fontFamily = FuenteTexto,
                                color = Blanco,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        is CapturaUiState.Procesando -> {
            PantallaProcesandoOcr()
        }

        is CapturaUiState.Error -> {
            PantallaErrorLectura(
                mensaje = state.mensaje,
                onReintentar = {
                    viewModel.reiniciar()
                    capturaFoto.tomarFoto()
                },
                onIngresarManual = {
                    viewModel.iniciarManual()
                    onIngresarManual()
                },
                onVolver = onVolver
            )
        }

        is CapturaUiState.Exito -> {
            // Se maneja en LaunchedEffect
        }
    }
}

@Composable
private fun PantallaProcesandoOcr() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            CircularProgressIndicator(
                color = AguaMedia,
                strokeWidth = 3.dp,
                modifier = Modifier.size(52.dp)
            )
            Text(
                "Leyendo recibo…",
                fontFamily = FuenteTexto,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Blanco
            )
            Text(
                "Extrayendo consumo, fechas y lecturas automáticamente",
                fontFamily = FuenteTexto,
                fontSize = 13.sp,
                color = Blanco.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PantallaErrorLectura(
    mensaje: String,
    onReintentar: () -> Unit,
    onIngresarManual: () -> Unit,
    onVolver: () -> Unit
) {
    TarjetaAvisoCamara(
        icono = Icons.Outlined.PhotoCamera,
        colorIcono = Ocre,
        fondoIcono = FondoIconoOcre,
        titulo = "No pudimos leer tu recibo",
        mensaje = mensaje
    ) {
        BotonPrincipalAviso("Tomar otra foto", onReintentar)
        BotonSecundarioAviso("Ingresar datos a mano", onIngresarManual, colorTexto = AguaMedia)
        BotonSecundarioAviso("Cancelar", onVolver)
    }
}

@Composable
private fun PantallaPermisoDenegado(
    onReintentar: () -> Unit,
    onVolver: () -> Unit
) {
    TarjetaAvisoCamara(
        icono = Icons.Outlined.PhotoCamera,
        colorIcono = Ocre,
        fondoIcono = FondoIconoOcre,
        titulo = "Permiso de cámara necesario",
        mensaje = "Para digitalizar tu recibo de EPS Tacna automáticamente, necesitamos acceso a la cámara."
    ) {
        BotonPrincipalAviso("Permitir acceso", onReintentar)
        BotonSecundarioAviso("Cancelar", onVolver)
    }
}

@Composable
private fun PantallaPermisoDenegadoPermanente(
    onAbrirAjustes: () -> Unit,
    onVolver: () -> Unit
) {
    TarjetaAvisoCamara(
        icono = Icons.Outlined.Lock,
        colorIcono = Color(0xFFDC2626),
        fondoIcono = Color(0xFFFEF2F2),
        titulo = "Acceso a la cámara bloqueado",
        mensaje = "El permiso fue denegado de forma permanente. Para escanear recibos, por favor actívalo en los ajustes de tu dispositivo."
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F7F7))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .sombraSuave(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Blanco)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(fondoIcono),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(28.dp))
            }
            Text(
                titulo,
                fontFamily = FuenteTexto,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Tinta,
                textAlign = TextAlign.Center
            )
            Text(
                mensaje,
                fontFamily = FuenteTexto,
                fontSize = 13.sp,
                color = TintaSuave,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
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
            Icon(icono, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(texto, fontFamily = FuenteTexto, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BotonSecundarioAviso(texto: String, onClick: () -> Unit, colorTexto: Color = TintaSuave) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(texto, fontFamily = FuenteTexto, color = colorTexto, fontWeight = FontWeight.SemiBold)
    }
}

private val FondoIconoOcre = Color(0xFFFFF7ED)
