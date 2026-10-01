package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import pe.edu.upt.aguatacna.feature.retos.data.FakeRetosRepository
import pe.edu.upt.aguatacna.feature.retos.domain.repository.RetosRepository
import pe.edu.upt.aguatacna.feature.retos.domain.usecase.CalcularRacha
import pe.edu.upt.aguatacna.feature.retos.domain.usecase.CompararConSector
import pe.edu.upt.aguatacna.feature.retos.domain.usecase.ActualizarRetoDelDia
import pe.edu.upt.aguatacna.feature.retos.domain.usecase.ObtenerRetosSemana
import pe.edu.upt.aguatacna.feature.retos.domain.model.LitrosPorHabitanteDia
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import org.koin.mp.KoinPlatform

class RetosViewModel(private val repositorio: RetosRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RetosUiState())
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun alternarReto(retoId: String) = viewModelScope.launch {
        val marcar = retoId !in _uiState.value.cumplidos
        ActualizarRetoDelDia(repositorio).ejecutar(retoId, marcar)
        cargar(if (marcar) "Reto cumplido. ¡Sigue así!" else "Reto de hoy corregido.")
    }

    private fun cargar(mensaje: String? = null) = viewModelScope.launch {
        val retos = ObtenerRetosSemana(repositorio).ejecutar()
        val cumplimientos = repositorio.obtenerCumplimientos()
        val hoy = repositorio.fechaActual()
        val posicion = CompararConSector(repositorio).ejecutar(
            sectorId = "CN-04",
            consumo = LitrosPorHabitanteDia(275.0)
        )
        _uiState.value = RetosUiState(
            cargando = false,
            retos = retos,
            cumplidos = cumplimientos.filter { it.cumplido && it.fecha == hoy }.map { it.retoId }.toSet(),
            diasCumplidos = cumplimientos.filter { it.cumplido }.map { it.fecha }.toSet(),
            hoy = hoy,
            racha = CalcularRacha().calcular(cumplimientos, hoy),
            posicionSector = posicion,
            mensaje = mensaje
        )
    }

    companion object {
        @OptIn(ExperimentalTime::class)
        fun conDatosDePrueba(): RetosViewModel = RetosViewModel(
            FakeRetosRepository(
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            )
        )

        fun desdeInyeccion(): RetosViewModel {
            val koin = KoinPlatform.getKoinOrNull() ?: return conDatosDePrueba()
            return RetosViewModel(koin.get())
        }
    }
}
