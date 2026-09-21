package pe.edu.upt.aguatacna.feature.retos.domain.usecase

import pe.edu.upt.aguatacna.feature.retos.domain.model.LitrosPorHabitanteDia
import pe.edu.upt.aguatacna.feature.retos.domain.model.PosicionSector
import pe.edu.upt.aguatacna.feature.retos.domain.model.RetoUsuario
import pe.edu.upt.aguatacna.feature.retos.domain.repository.RetosRepository

class ObtenerRetosSemana(private val repositorio: RetosRepository) {
    suspend fun ejecutar() = repositorio.obtenerRetosActivos()
}

class MarcarRetoCumplido(private val repositorio: RetosRepository) {
    suspend fun ejecutar(retoId: String) {
        repositorio.guardarCumplimiento(RetoUsuario(retoId, repositorio.fechaActual(), true))
    }
}

class CompararConSector(private val repositorio: RetosRepository) {
    suspend fun ejecutar(sectorId: String, consumo: LitrosPorHabitanteDia): PosicionSector? {
        val promedio = repositorio.obtenerPromedioSector(sectorId) ?: return null
        return PosicionSector(consumo, LitrosPorHabitanteDia(promedio))
    }
}
