package pe.edu.upt.aguatacna.feature.retos.domain.usecase

import pe.edu.upt.aguatacna.feature.retos.domain.model.Reporte
import pe.edu.upt.aguatacna.feature.retos.domain.repository.ReporteRepository

class CrearReporte(private val repositorio: ReporteRepository) {
    suspend fun ejecutar(reporte: Reporte) = repositorio.guardar(reporte)
}
