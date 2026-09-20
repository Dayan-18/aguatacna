package pe.edu.upt.aguatacna.feature.reserva.domain.repository

import pe.edu.upt.aguatacna.feature.reserva.domain.model.Aviso

/** Recuerda qué avisos ya se mostraron, para no repetir el mismo cada hora. */
interface RegistroDeAvisos {
    fun yaSeAviso(clave: String): Boolean
    fun marcarComoAvisado(clave: String)
}

/** Muestra el aviso al usuario; cada plataforma lo hace a su manera. */
interface Notificador {
    fun mostrar(aviso: Aviso)
}
