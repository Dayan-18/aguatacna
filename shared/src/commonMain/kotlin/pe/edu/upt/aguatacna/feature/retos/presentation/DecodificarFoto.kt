package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.compose.ui.graphics.ImageBitmap

expect fun decodificarFoto(bytes: ByteArray): ImageBitmap?
