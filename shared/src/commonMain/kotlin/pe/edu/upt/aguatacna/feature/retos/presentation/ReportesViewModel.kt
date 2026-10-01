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
import pe.edu.upt.aguatacna.feature.sector.domain.model.Coordenada
import pe.edu.upt.aguatacna.feature.sector.domain.model.Sector
import pe.edu.upt.aguatacna.feature.sector.domain.repository.SectorRepository
import pe.edu.upt.aguatacna.feature.sector.domain.usecase.ResolverSector
import pe.edu.upt.aguatacna.data.local.UsuarioDao

data class ReporteUbicacionUi(
    val buscando: Boolean = true,
    val coordenada: Coordenada? = null,
    val sector: Sector? = null,
    val mensaje: String = "Cargando tu domicilio registrado…",
    val esError: Boolean = false
)

class ReportesViewModel(
    private val repositorio: ReporteRepository = FakeReporteRepository(),
    private val sectores: SectorRepository? = null,
    private val obtenerSectorGuardado: suspend () -> String? = { null }
) : ViewModel() {
    private val _reportes = MutableStateFlow<List<Reporte>>(emptyList())
    val reportes: StateFlow<List<Reporte>> = _reportes.asStateFlow()
    private val _ubicacion = MutableStateFlow(ReporteUbicacionUi())
    val ubicacion: StateFlow<ReporteUbicacionUi> = _ubicacion.asStateFlow()
    private val resolverSector = ResolverSector()

    init {
        viewModelScope.launch { _reportes.value = repositorio.obtenerPendientes().reversed() }
        cargarUbicacionRegistrada()
    }

    private fun cargarUbicacionRegistrada() {
        viewModelScope.launch {
            runCatching {
                val repositorioSector = sectores ?: return@runCatching null
                val coordenada = repositorioSector.obtenerUbicacionCasa() ?: return@runCatching null
                val disponibles = repositorioSector.obtenerSectores()
                val idGuardado = obtenerSectorGuardado()
                val sector = disponibles.firstOrNull { it.id == idGuardado }
                    ?: resolverSector.resolver(coordenada, disponibles)
                coordenada to sector
            }.onSuccess { resultado ->
                val coordenada = resultado?.first
                val sector = resultado?.second
                _ubicacion.value = ReporteUbicacionUi(
                    coordenada = coordenada,
                    sector = sector,
                    mensaje = if (coordenada != null && sector != null) {
                        "Usaremos el domicilio que registraste al iniciar la aplicación"
                    } else {
                        "No encontramos un domicilio registrado. Regístralo primero en Mi sector."
                    },
                    esError = coordenada == null || sector == null
                )
            }.onFailure {
                _ubicacion.value = ReporteUbicacionUi(
                    mensaje = "No se pudo cargar tu domicilio y sector registrados.",
                    esError = true
                )
            }
        }
    }

    fun guardar(tipo: TipoReporte, foto: ByteArray?, alGuardar: () -> Unit) {
        val coordenada = _ubicacion.value.coordenada ?: return
        viewModelScope.launch {
            val reporte = Reporte(
                id = nuevoUuid(),
                tipo = tipo,
                descripcion = etiquetaTipo(tipo),
                latitud = coordenada.latitud,
                longitud = coordenada.longitud,
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
            val usuarioDao = koin.get<UsuarioDao>()
            return ReportesViewModel(koin.get(), koin.get()) { usuarioDao.obtener()?.sectorId }
        }
    }
}
