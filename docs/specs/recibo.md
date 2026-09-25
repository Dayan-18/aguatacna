# Especificación y Documentación Técnica del Módulo Recibo - AguaTacna

> **Ubicación del código fuente**: [`shared/src/commonMain/kotlin/pe/edu/upt/aguatacna/feature/recibo`](file:///d:/Codigos/Moviles/primer%20intento/aguatacna/shared/src/commonMain/kotlin/pe/edu/upt/aguatacna/feature/recibo)

Para acceder a la versión completa e indexada de esta documentación, consulta:
- [DOCUMENTACION_RECIBO.md](file:///d:/Codigos/Moviles/primer%20intento/DOCUMENTACION_RECIBO.md) en la raíz del proyecto.
- El plan base de tareas e ingeniería en: [plan-modulo-recibo.md](file:///d:/Codigos/Moviles/primer%20intento/skills/apartado%20completo%20de%20camara/plan-modulo-recibo.md).

---

## Resumen Ejecutivo

El módulo `recibo` implementa la captura, digitalización mediante OCR local, revisión, corrección manual, persistencia en Room SQLite y análisis histórico de los recibos de agua de la **EPS Tacna S.A.**, aplicando las reglas de protección al usuario establecidas por la **Sunass (art. 88)**.

### Sub-paquetes:
- **`domain`**: Modelos inmutables (`Recibo`, `Dinero`, `PeriodoConsumo`, `EstadoConsumo`, `Campo<T>`), reglas de negocio de Sunass (`EvaluadorConsumo`), validaciones de coherencia (`ValidadorRecibo`) y casos de uso (`EscanearReciboUseCase`, `ConfirmarReciboUseCase`, `CorregirCampoUseCase`, `ObservarResumenUseCase`, `ObservarHistorialUseCase`).
- **`infrastructure/ocr`**: Parser regex para recibos de EPS Tacna (`ParserReciboEpsTacna`) y reconocimiento con Google ML Kit para Android (`ReconocedorTextoAndroid`).
- **`data`**: Store reactivo en memoria (`BorradorReciboStore`), implementación SQLite con Room KMP (`ReciboRepositoryRoom`), DAO y entidades locales.
- **`presentation`**: Pantalla principal (`ReciboScreen`), Historial con gráfico de 6 meses y umbral de 100 m³ (`ReciboHistorialScreen`), Revisión de borrador (`ReciboFotoScreen`), Corrección manual con teclado táctil (`ReciboManualMedidorScreen`) y Cámara (`CamaraReciboScreen`).
