package pe.edu.upt.aguatacna.feature.retos.domain.repository

import pe.edu.upt.aguatacna.feature.retos.domain.model.Reporte

interface ReporteRepository {
    suspend fun guardar(reporte: Reporte)
    suspend fun obtenerPendientes(): List<Reporte>
}
