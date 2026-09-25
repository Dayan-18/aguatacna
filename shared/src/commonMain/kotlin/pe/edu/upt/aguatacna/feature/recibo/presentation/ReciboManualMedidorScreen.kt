package pe.edu.upt.aguatacna.feature.recibo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.ui.theme.Blanco
import pe.edu.upt.aguatacna.core.ui.theme.Divisor
import pe.edu.upt.aguatacna.core.ui.theme.FuenteTexto
import pe.edu.upt.aguatacna.core.ui.theme.IconosClarosEnBarraDeEstado
import pe.edu.upt.aguatacna.core.ui.theme.Tinta
import pe.edu.upt.aguatacna.core.ui.theme.TintaTenue
import pe.edu.upt.aguatacna.core.util.RelojDelSistema
import pe.edu.upt.aguatacna.feature.recibo.data.BorradorReciboStore
import pe.edu.upt.aguatacna.feature.recibo.domain.model.Campo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.OrigenDatos
import pe.edu.upt.aguatacna.feature.recibo.domain.model.PeriodoConsumo
import pe.edu.upt.aguatacna.feature.recibo.domain.model.ReciboBorrador
import pe.edu.upt.aguatacna.feature.recibo.domain.usecase.CorregirCampoUseCase
import pe.edu.upt.aguatacna.feature.recibo.presentation.componentes.*

private val FondoPantalla = Color(0xFFF1F6F8)
private val DigitoTexto = Color(0xFF14232C)
private val BotonGuardar = Color(0xFF098093)

// Pantalla de corrección manual: permite editar consumo, lecturas, importe y período.
@Composable
fun ReciboManualMedidorScreen(
    onVolver: () -> Unit,
    campo: TipoCampoEdicion = TipoCampoEdicion.CONSUMO_M3,
    borradorStore: BorradorReciboStore = KoinPlatform.getKoin().get(),
    corregirUseCase: CorregirCampoUseCase = CorregirCampoUseCase()
) {
    val ahora = remember { RelojDelSistema().ahora() }
    val periodoActual = remember(ahora) { PeriodoConsumo(ahora.year, ahora.monthNumber) }
    var borrador = borradorStore.borrador.value
    if (borrador == null) {
        val nuevo = ReciboBorrador(
            periodoConsumo = Campo(periodoActual, confianza = 1f),
            consumoM3 = Campo(null, 1f),
            importeTotal = Campo(null, 1f),
            origen = OrigenDatos.MANUAL
        )
        borradorStore.guardar(nuevo)
        borrador = nuevo
    }

    val valorDetectado = remember(campo, borrador) {
        when (campo) {
            TipoCampoEdicion.CONSUMO_M3 -> borrador.consumoM3.valor?.let { if (it == 0) "" else it.toString() } ?: ""
            TipoCampoEdicion.LECTURA_ANTERIOR -> borrador.lecturaAnteriorM3.valor?.let { if (it == 0) "" else it.toString() } ?: ""
            TipoCampoEdicion.LECTURA_ACTUAL -> borrador.lecturaActualM3.valor?.let { if (it == 0) "" else it.toString() } ?: ""
            TipoCampoEdicion.IMPORTE -> borrador.importeTotal.valor?.let {
                val soles = it.centimos / 100
                val cent = it.centimos % 100
                if (it.centimos == 0L) "" else "$soles,${cent.toString().padStart(2, '0')}"
            } ?: ""
            TipoCampoEdicion.PERIODO -> borrador.periodoConsumo.valor?.displayCompleto ?: ""
        }
    }

    var entrada by remember(valorDetectado) {
        mutableStateOf(if (valorDetectado == "0" || valorDetectado == "0,00") "" else valorDetectado)
    }
    var periodoSeleccionado by remember(borrador) {
        mutableStateOf(borrador.periodoConsumo.valor ?: periodoActual)
    }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    val permiteComa = campo == TipoCampoEdicion.IMPORTE
    val maxDigitos = when (campo) {
        TipoCampoEdicion.CONSUMO_M3 -> 3 // 0..999 m³
        TipoCampoEdicion.IMPORTE -> 7 // Ej: 1234,56
        else -> 6 // Lecturas de medidor hasta 999999
    }

    IconosClarosEnBarraDeEstado(claros = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPantalla)
            .statusBarsPadding()
    ) {
        // Parte superior: Header + Tarjeta de entrada
        Column(modifier = Modifier.weight(1f)) {
            ReciboBarraSuperior(
                onVolver = onVolver,
                titulo = if (campo == TipoCampoEdicion.PERIODO) "Seleccionar período" else "Corregir ${campo.nombre}",
                subtitulo = if (campo == TipoCampoEdicion.PERIODO) {
                    "Recibo para: ${periodoSeleccionado.displayCompleto}"
                } else {
                    "Valor actual: ${if (valorDetectado.isBlank()) "Sin datos" else "$valorDetectado ${campo.unidad}"}"
                }
            )

            if (campo == TipoCampoEdicion.PERIODO) {
                SelectorPeriodoMeses(
                    periodoSeleccionado = periodoSeleccionado,
                    onSeleccionarPeriodo = { periodoSeleccionado = it }
                )
            } else {
                DisplayDigitosMedidor(
                    campoEtiqueta = "${campo.nombre} en ${campo.unidad}".uppercase(),
                    digitos = entrada,
                    unidad = campo.unidad,
                    valorDetectado = if (valorDetectado.isBlank()) "0" else valorDetectado,
                    errorMensaje = errorMensaje,
                    maxDigitos = maxDigitos
                )
            }
        }

        // Parte inferior: Teclado numérico táctil + Botón guardar
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            if (campo != TipoCampoEdicion.PERIODO) {
                TecladoNumericoMedidor(
                    permiteComa = permiteComa,
                    onDigitoPulsado = { tecla ->
                        errorMensaje = null
                        if (tecla == "←") {
                            if (entrada.isNotEmpty()) {
                                entrada = entrada.dropLast(1)
                            }
                        } else if (tecla == ",") {
                            if (permiteComa && !entrada.contains(',')) {
                                entrada = if (entrada.isEmpty()) "0," else entrada + ","
                            }
                        } else {
                            if (permiteComa && entrada.contains(',')) {
                                val partes = entrada.split(',')
                                if (partes.size > 1 && partes[1].length >= 2) {
                                    return@TecladoNumericoMedidor
                                }
                            }
                            if (entrada == "0" && tecla != ",") {
                                entrada = tecla
                            } else if (entrada.length < maxDigitos) {
                                entrada += tecla
                            }
                        }
                    }
                )
                Spacer(Modifier.height(20.dp))
            }

            BotonGuardarCorreccion(
                onGuardar = {
                    when (campo) {
                        TipoCampoEdicion.PERIODO -> {
                            borradorStore.actualizar { borradorActual ->
                                corregirUseCase(borradorActual, CorregirCampoUseCase.CampoEditable.Periodo(periodoSeleccionado))
                            }
                            onVolver()
                        }
                        TipoCampoEdicion.CONSUMO_M3 -> {
                            val numero = entrada.toIntOrNull()
                            if (numero == null || numero !in 0..999) {
                                errorMensaje = "Ingresa un consumo válido entre 0 y 999 m³"
                            } else {
                                borradorStore.actualizar { borradorActual ->
                                    corregirUseCase(borradorActual, CorregirCampoUseCase.CampoEditable.ConsumoM3(numero))
                                }
                                onVolver()
                            }
                        }
                        TipoCampoEdicion.LECTURA_ANTERIOR -> {
                            val numero = entrada.toIntOrNull()
                            if (numero == null || numero < 0) {
                                errorMensaje = "Ingresa una lectura válida"
                            } else {
                                val actual = borrador.lecturaActualM3.valor
                                if (actual != null && numero > actual) {
                                    errorMensaje = "La lectura anterior no puede superar la actual ($actual m³)"
                                } else {
                                    borradorStore.actualizar { borradorActual ->
                                        corregirUseCase(borradorActual, CorregirCampoUseCase.CampoEditable.LecturaAnterior(numero))
                                    }
                                    onVolver()
                                }
                            }
                        }
                        TipoCampoEdicion.LECTURA_ACTUAL -> {
                            val numero = entrada.toIntOrNull()
                            if (numero == null || numero < 0) {
                                errorMensaje = "Ingresa una lectura válida"
                            } else {
                                val anterior = borrador.lecturaAnteriorM3.valor
                                if (anterior != null && numero < anterior) {
                                    errorMensaje = "La lectura actual debe ser mayor o igual a la anterior ($anterior m³)"
                                } else {
                                    borradorStore.actualizar { borradorActual ->
                                        corregirUseCase(borradorActual, CorregirCampoUseCase.CampoEditable.LecturaActual(numero))
                                    }
                                    onVolver()
                                }
                            }
                        }
                        TipoCampoEdicion.IMPORTE -> {
                            val normalizado = entrada.replace(',', '.')
                            val importeDouble = normalizado.toDoubleOrNull()
                            if (importeDouble == null || importeDouble <= 0 || importeDouble > 99999.0) {
                                errorMensaje = "Ingresa un importe válido mayor a S/ 0"
                            } else {
                                val centimos = (importeDouble * 100).toLong()
                                borradorStore.actualizar { borradorActual ->
                                    corregirUseCase(borradorActual, CorregirCampoUseCase.CampoEditable.Importe(centimos))
                                }
                                onVolver()
                            }
                        }
                    }
                }
            )
        }
    }
}


@Composable
private fun BotonGuardarCorreccion(onGuardar: () -> Unit) {
    Button(
        onClick = onGuardar,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BotonGuardar)
    ) {
        Text(
            text = "Guardar corrección",
            fontFamily = FuenteTexto,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
