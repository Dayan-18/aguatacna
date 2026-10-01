package pe.edu.upt.aguatacna.feature.retos

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import pe.edu.upt.aguatacna.feature.retos.domain.model.RetoUsuario
import pe.edu.upt.aguatacna.feature.retos.domain.usecase.CalcularRacha

class CalcularRachaTest {
    @Test
    fun cuenta_dias_consecutivos_hasta_hoy() {
        val hoy = LocalDate(2026, 9, 20)
        val cumplimientos = listOf(
            RetoUsuario("a", LocalDate(2026, 9, 20), true),
            RetoUsuario("b", LocalDate(2026, 9, 19), true),
            RetoUsuario("c", LocalDate(2026, 9, 18), true)
        )

        assertEquals(3, CalcularRacha().calcular(cumplimientos, hoy).dias)
    }

    @Test
    fun conserva_racha_mientras_el_dia_actual_esta_pendiente() {
        val cumplimientos = listOf(RetoUsuario("a", LocalDate(2026, 9, 19), true))

        assertEquals(1, CalcularRacha().calcular(cumplimientos, LocalDate(2026, 9, 20)).dias)
    }
}
