package pe.edu.upt.aguatacna.feature.retos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upt.aguatacna.feature.retos.data.FakeReporteRepository
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reporte
import pe.edu.upt.aguatacna.feature.retos.domain.model.TipoReporte
import pe.edu.upt.aguatacna.feature.retos.domain.repository.ReporteRepository
import pe.edu.upt.aguatacna.feature.retos.domain.usecase.CrearReporte
import org.koin.mp.KoinPlatform
import pe.edu.upt.aguatacna.core.util.nuevoUuid

class ReportesViewModel(
    private val repositorio: ReporteRepository = FakeReporteRepository()
) : ViewModel() {
    private val _reportes = MutableStateFlow<List<Reporte>>(emptyList())
    val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()

    init {
        viewModelScope.launch { _reportes.value = repositorio.obtenerPendientes().reversed() }
    }

    fun guardar(tipo: TipoReporte, foto: ByteArray?, alGuardar: () -> Unit) {
        viewModelScope.launch {
            val reporte = Reporte(
                id = nuevoUuid(),
                tipo = tipo,
                descripcion = etiquetaTipo(tipo),
                latitud = -17.9841,
                longitud = -70.2372,
                fotoUri = if (foto != null) "foto-local" else null,
                fotoBytes = foto
            )
            CrearReporte(repositorio).ejecutar(reporte)
            _reportes.value = repositorio.obtenerPendientes().reversed()
            alGuardar()
        }
    }

    companion object {
        fun desdeInyeccion(): ReportesViewModel {
            val koin = KoinPlatform.getKoinOrNull() ?: return ReportesViewModel()
            return ReportesViewModel(koin.get())
        }
    }
}
