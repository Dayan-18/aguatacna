package pe.edu.upt.aguatacna.feature.reserva.domain.model

// Los litros salen del Figma (pantalla 05) y son valores iniciales; falta una fuente (spec, decisiones abiertas).
enum class Recomendacion(
    val descripcion: String,
    val litrosQueAhorra: Litros,
    private val corresponde: (HabitosDelHogar) -> Boolean
) {
    POSTERGAR_LAVADO("Postergar el lavado de ropa", Litros(90.0), { it.usaLavadora }),
    DUCHAS_CORTAS("Dos duchas de 5 minutos", Litros(60.0), { it.duchasPorDia > 0 }),
    NO_REGAR("No regar el jardín hoy", Litros(80.0), { it.riegaJardin }),
    CERRAR_EL_CANO("Cerrar el caño al lavar platos", Litros(45.0), { true });

    /** Solo se sugiere lo que el hogar hace: no se propone "no regar" a quien no riega. */
    fun correspondeA(habitos: HabitosDelHogar): Boolean = corresponde(habitos)
}
