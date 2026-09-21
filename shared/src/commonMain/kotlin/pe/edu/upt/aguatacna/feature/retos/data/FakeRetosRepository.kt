package pe.edu.upt.aguatacna.feature.retos.data

import kotlinx.datetime.LocalDate
import pe.edu.upt.aguatacna.feature.retos.domain.model.Reto
import pe.edu.upt.aguatacna.feature.retos.domain.model.RetoUsuario
import pe.edu.upt.aguatacna.feature.retos.domain.repository.RetosRepository

class FakeRetosRepository(private val hoy: LocalDate) : RetosRepository {
    private val cumplimientos = mutableListOf<RetoUsuario>()

    override suspend fun obtenerRetosActivos(): List<Reto> = retosDePrueba
    override suspend fun obtenerCumplimientos(): List<RetoUsuario> = cumplimientos.toList()
    override suspend fun guardarCumplimiento(cumplimiento: RetoUsuario) {
        cumplimientos.removeAll { it.retoId == cumplimiento.retoId && it.fecha == cumplimiento.fecha }
        cumplimientos.add(cumplimiento)
    }
    override suspend fun obtenerPromedioSector(sectorId: String): Double? = 118.0
    override suspend fun fechaActual(): LocalDate = hoy
}

private val retosDePrueba = listOf(
    Reto("ducha-corta", "Ducha breve", "Reduce tu ducha a cinco minutos.", 40),
    Reto("reusar-agua", "Reutiliza agua", "Usa agua de lavado para limpiar pisos.", 25),
    Reto("revisar-fugas", "Revisa fugas", "Verifica que no haya goteos en casa.", 15)
)
