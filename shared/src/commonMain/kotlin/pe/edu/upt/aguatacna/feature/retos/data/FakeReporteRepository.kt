package pe.edu.upt.aguatacna.feature.retos.data

import pe.edu.upt.aguatacna.feature.retos.domain.model.Reporte
import pe.edu.upt.aguatacna.feature.retos.domain.repository.ReporteRepository

class FakeReporteRepository : ReporteRepository {
    private val reportes = mutableListOf<Reporte>()

    override suspend fun guardar(reporte: Reporte) {
        reportes.removeAll { it.id == reporte.id }
        reportes.add(reporte)
    }

    override suspend fun obtenerPendientes(): List<Reporte> =
        reportes.filter { it.pendienteSincronizacion }
}
