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
import pe.edu.upt.aguatacna.feature.retos.domain.usecase.MarcarRetoCumplido
import pe.edu.upt.aguatacna.feature.retos.domain.usecase.ObtenerRetosSemana
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RetosViewModel(private val repositorio: RetosRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RetosUiState())
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    init { cargar() }

    fun completar(retoId: String) = viewModelScope.launch {
        MarcarRetoCumplido(repositorio).ejecutar(retoId)
        cargar("Reto cumplido. ¡Sigue así!")
    }

    private fun cargar(mensaje: String? = null) = viewModelScope.launch {
        val retos = ObtenerRetosSemana(repositorio).ejecutar()
        val cumplimientos = repositorio.obtenerCumplimientos()
        _uiState.value = RetosUiState(
            cargando = false,
            retos = retos,
            cumplidos = cumplimientos.filter { it.cumplido }.map { it.retoId }.toSet(),
            racha = CalcularRacha().calcular(cumplimientos, repositorio.fechaActual()),
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
    }
}
