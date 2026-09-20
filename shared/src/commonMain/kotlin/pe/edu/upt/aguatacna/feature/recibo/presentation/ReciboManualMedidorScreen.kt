package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upt.aguatacna.core.ui.theme.AguaMedia
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteNumeros
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaSuave
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.ui.theme.sombraSuave

// Colores específicos del diseño manual medidor
private val FondoPantalla = Color(0xFFF1F6F8)
private val InfoFondo = Color(0xC0DCF0F4) // 75% opacidad
private val InfoTexto = Color(0xFF16606A)
private val InfoIcono = Color(0xFF0D7D8A)
private val DigitoFondo = Color(0xFFF0F6F8)
private val DigitoTexto = Color(0xFF14232C)
private val CursorColor = Color(0xFF0992A5)
private val CursorBorde = Color(0xFF0992A5)
private val BadgeFondo = Color(0x99DBE8ED) // 60% opacidad
private val BadgeTexto = Color(0xFF52707F)
private val TeclaFondo = Blanco
private val TeclaSombra = Color(0x08000000)
private val BotonGuardar = Color(0xFF098093)
private val MetaTexto = Color(0xFF7F8F99)
private val MetaValor = Color(0xFF1E2E38)

/**
 * Pantalla de lectura manual del medidor.
 * Corresponde al diseño de manualMedidor.html.
 */
@Composable
fun ReciboManualMedidorScreen(onVolver: () -> Unit) {
    IconosClarosEnBarraDeEstado(claros = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPantalla)
            .statusBarsPadding()
    ) {
        // ── Parte superior: Header + Info + Tarjeta de dígitos ──
        Column(modifier = Modifier.weight(1f)) {
            EncabezadoMedidor(onVolver)
            CalloutInformativo()
            TarjetaLectura()
        }

        // ── Parte inferior: Teclado numérico + Botón guardar ──
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            TecladoNumerico()
            Spacer(Modifier.height(20.dp))
            BotonGuardarLectura()
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Header
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun EncabezadoMedidor(onVolver: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(
                onClick = onVolver,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Blanco.copy(alpha = 0.8f))
                    .border(1.dp, Divisor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(20.dp),
                    tint = Tinta
                )
            }
            Column {
                Text(
                    "Lectura del medidor",
                    fontFamily = FuenteTexto,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DigitoTexto
                )
                Text(
                    "Opcional · una vez al mes",
                    fontFamily = FuenteTexto,
                    fontSize = 11.sp,
                    color = TintaTenue
                )
            }
        }
        // Badge "Secundaria"
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(BadgeFondo)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(BadgeTexto))
            Text("Secundaria", fontFamily = FuenteTexto, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = BadgeTexto)
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Callout informativo
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun CalloutInformativo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(InfoFondo)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = null,
            tint = InfoIcono,
            modifier = Modifier.size(16.dp)
        )
        Text(
            "En Tacna el medidor está bajo la vereda. Úsala solo si sospechas una fuga: no hace falta para que la app funcione.",
            fontFamily = FuenteTexto,
            fontSize = 11.sp,
            color = InfoTexto,
            lineHeight = 15.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// ──────────────────────────────────────────────────────────────────────
// Tarjeta de lectura con dígitos
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TarjetaLectura() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .sombraSuave(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Blanco)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Etiqueta
        Text(
            "LECTURA ACTUAL EN M³",
            fontFamily = FuenteTexto,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = TintaTenue,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // Dígitos
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dígito 1
            CajaDigito("1", activa = false)
            // Dígito 2
            CajaDigito("3", activa = false)
            // Dígito 3
            CajaDigito("1", activa = false)
            // Slot activo con cursor
            CajaDigito(null, activa = true)
        }

        Spacer(Modifier.height(20.dp))

        // Lectura anterior
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Lectura anterior",
                fontFamily = FuenteTexto,
                fontSize = 12.sp,
                color = MetaTexto
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text("1 284", fontFamily = FuenteNumeros, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MetaValor)
                Spacer(Modifier.width(3.dp))
                Text("m³", fontFamily = FuenteTexto, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MetaTexto)
            }
        }
    }
}

@Composable
private fun CajaDigito(digito: String?, activa: Boolean) {
    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (activa) Blanco else DigitoFondo)
            .then(
                if (activa) Modifier.border(2.dp, CursorBorde, RoundedCornerShape(16.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (digito != null) {
            Text(
                digito,
                fontFamily = FuenteNumeros,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DigitoTexto
            )
        } else {
            // Cursor parpadeante (visual estático, la animación requiere lógica)
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(50))
                    .background(CursorColor)
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Teclado numérico
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun TecladoNumerico() {
    val teclas = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(",", "0", "←")
    )

    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        teclas.forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                fila.forEach { tecla ->
                    OutlinedButton(
                        onClick = { /* Solo visual */ },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = TeclaFondo
                        ),
                        border = null
                    ) {
                        if (tecla == "←") {
                            Icon(
                                Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Borrar",
                                tint = DigitoTexto,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                tecla,
                                fontFamily = FuenteNumeros,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = DigitoTexto
                            )
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────
// Botón guardar lectura
// ──────────────────────────────────────────────────────────────────────

@Composable
private fun BotonGuardarLectura() {
    Button(
        onClick = { /* Solo visual */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BotonGuardar)
    ) {
        Text(
            "Guardar lectura",
            fontFamily = FuenteTexto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
