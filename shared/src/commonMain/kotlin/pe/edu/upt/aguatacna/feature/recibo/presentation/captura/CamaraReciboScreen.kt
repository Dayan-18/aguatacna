package pe.edu.upt.aguatacna.feature.recibo.presentation.captura

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import pe.edu.upt.aguatacna.core.util.EstadoPermisoCamara
import pe.edu.upt.aguatacna.core.util.rememberCapturaFoto
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador

/**
 * Pantalla principal del flujo de captura de recibo.
 * Abre la cámara del sistema y pasa directamente a procesamiento OCR sin doble confirmación.
 */
@Composable
fun CamaraReciboScreen(
    onReciboDetectado: (ReciboBorrador) -> Unit,
    onIngresarManual: () -> Unit,
    onVolver: () -> Unit,
    viewModel: CapturaViewModel = viewModel { CapturaViewModel.desdeInyeccion() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var camaraIniciada by rememberSaveable { mutableStateOf(false) }

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
                onIngresarManual = onIngresarManual,
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
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF7ED)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = Ocre,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                "No pudimos leer tu recibo",
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
            Button(
                onClick = onReintentar,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AguaMedia)
            ) {
                Text("Tomar otra foto", fontFamily = FuenteTexto, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onIngresarManual,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ingresar datos a mano", fontFamily = FuenteTexto, color = AguaMedia, fontWeight = FontWeight.SemiBold)
            }
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar", fontFamily = FuenteTexto, color = TintaSuave)
            }
        }
    }
}

@Composable
private fun PantallaPermisoDenegado(
    onReintentar: () -> Unit,
    onVolver: () -> Unit
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
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF7ED)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = Ocre, modifier = Modifier.size(28.dp))
            }
            Text("Permiso de cámara necesario", fontFamily = FuenteTexto, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Tinta, textAlign = TextAlign.Center)
            Text(
                "Para digitalizar tu recibo de EPS Tacna automáticamente, necesitamos acceso a la cámara.",
                fontFamily = FuenteTexto,
                fontSize = 13.sp,
                color = TintaSuave,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onReintentar,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AguaMedia)
            ) {
                Text("Permitir acceso", fontFamily = FuenteTexto, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar", fontFamily = FuenteTexto, color = TintaSuave)
            }
        }
    }
}

@Composable
private fun PantallaPermisoDenegadoPermanente(
    onAbrirAjustes: () -> Unit,
    onVolver: () -> Unit
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
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEF2F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(28.dp))
            }
            Text("Acceso a la cámara bloqueado", fontFamily = FuenteTexto, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Tinta, textAlign = TextAlign.Center)
            Text(
                "El permiso fue denegado de forma permanente. Para escanear recibos, por favor actívalo en los ajustes de tu dispositivo.",
                fontFamily = FuenteTexto,
                fontSize = 13.sp,
                color = TintaSuave,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onAbrirAjustes,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AguaMedia)
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Abrir ajustes", fontFamily = FuenteTexto, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Volver", fontFamily = FuenteTexto, color = TintaSuave)
            }
        }
    }
}
