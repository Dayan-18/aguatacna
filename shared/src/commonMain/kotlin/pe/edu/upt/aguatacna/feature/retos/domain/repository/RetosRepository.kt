package pe.edu.upt.aguatacna.feature.retos.domain.repository

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reto
import pe.edu.upt.aguatacna.feature.retos.domain.model.RetoUsuario

interface RetosRepository {
    suspend fun obtenerRetosActivos(): List<Reto>
    suspend fun obtenerCumplimientos(): List<RetoUsuario>
    suspend fun guardarCumplimiento(cumplimiento: RetoUsuario)
    suspend fun obtenerPromedioSector(sectorId: String): Double?
    suspend fun fechaActual(): LocalDate
}
