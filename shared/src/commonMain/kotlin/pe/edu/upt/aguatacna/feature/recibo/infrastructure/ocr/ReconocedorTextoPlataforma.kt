package pe.edu.upt.aguatacna.feature.recibo.infrastructure.ocr

import pe.edu.upt.aguatacna.feature.recibo.domain.port.ReconocedorTexto

/** El OCR de cada plataforma: ML Kit en Android; en iOS aún no hay y se pide el ingreso manual. */
expect fun crearReconocedorTexto(): ReconocedorTexto
