package pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr

import pe.edu.upt.aguatacna.feature.recibo.domain.port.ReconocedorTexto

actual fun crearReconocedorTexto(): ReconocedorTexto = ReconocedorTextoAndroid()
