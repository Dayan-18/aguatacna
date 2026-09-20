# Plan de implementación · Reserva domiciliaria

**Rama:** `feat/reserva-dominio` · **Spec:** [`../reserva.md`](../reserva.md)
**Dueño:** Cristhian Mamani · Semanas 9 a 17

## Resumen

Se construye de adentro hacia afuera: primero el dominio puro con sus pruebas
(lo que el docente revisa primero), después la persistencia, la interfaz y por
último la plataforma. Cada capa se puede entregar y probar sin la siguiente.

## Contexto técnico

| | |
|---|---|
| Lenguaje | Kotlin Multiplatform, código compartido en `commonMain` |
| Dependencias del dominio | Solo Kotlin estándar, corrutinas y `kotlinx-datetime` |
| Almacenamiento | Room (semana 10 en adelante), fuente única de verdad |
| Pruebas | `kotlin.test` en `commonTest`, se ejecutan en JVM |
| Comando de pruebas | `./gradlew :shared:testAndroidHostTest` |
| Tiempo | `LocalDateTime`, igual que el contrato de `feature/sector` |

## Revisión de la constitución

| Artículo | Cómo se cumple |
|---|---|
| I. Dominio puro | Nada de Room, Ktor, Koin ni Android en `domain/`. Los objetos de valor usan solo Kotlin. |
| II. Sin conexión | El repositorio lee solo de Room. Reserva no necesita red. |
| III. UUID local | Perfil y eventos apuntan al `usuarioId` local, nunca al de Google. |
| IV. Límites | Un archivo por concepto; PR de máximo 400 líneas (ver tareas). |
| V. Pruebas | Cada criterio CA-xx tiene su prueba antes de integrarse. |
| VII. Contrato antes que código | `ReservaRepository` y su fake salen en el primer PR de datos. |
| IX. Privacidad | Los eventos de llenado son del usuario; a la nube solo se envía el indicador de litros por habitante, sin ubicación. |

## Estructura

Rutas bajo `shared/src/commonMain/kotlin/pe/edu/upt/aguatacna/`:

```text
feature/reserva/
├── domain/
│   ├── model/        Litros · CapacidadLitros · Habitantes · NivelReserva
│   │                 EventoLlenado · IntervaloConsumo · Reserva
│   │                 TipoLlenado · EstadoProyeccion · Deficit · Recomendacion
│   ├── repository/   ReservaRepository
│   └── usecase/      EstimarConsumo · ProyectarAgotamiento · CalcularDeficit
│                     SugerirRecortes · SimularRecortes · RegistrarLlenado · DeclararSinAgua
├── data/
│   ├── local/        EventoLlenadoEntity · ReservaDao
│   ├── mapper/       conversiones entidad ↔ dominio
│   ├── ReservaRepositoryImpl.kt
│   └── FakeReservaRepository.kt
└── presentation/     ReservaUiState · ReservaEvent · ReservaViewModel
                      ReservaScreen · IndicadorNivelReservorio
domain/model/         PerfilHogar (transversal; lo llenan reserva y recibo)
androidMain/.../feature/reserva/   Tarea horaria y aviso local
```

Las pruebas van en `shared/src/commonTest/kotlin/.../feature/reserva/`.

## Decisiones de diseño

- **Entidad `Reserva` con comportamiento**, no un contenedor de datos:
  `nivelEn(momento)`, `agotamientoProyectado()`, `conLlenado(...)` y
  `conSinAgua(...)`. Devuelven una reserva nueva (inmutable).
- **`EstimarConsumo` es un caso de uso aparte.** Recibe los eventos y el perfil
  y devuelve el consumo horario con su origen (`HISTORICO`, `HABITOS`,
  `ESTIMACION_INICIAL`). Así CA-14 a CA-17 se prueban sin la entidad.
- **`CalcularDeficit` recibe el próximo abastecimiento como `LocalDateTime?`.**
  No importa nada de `feature/sector`: A y B solo comparten el contrato del
  valor, así el dominio de reserva no queda acoplado al de sector.
- **Los coeficientes** (5 intervalos, doble de la mediana, 30 %) son constantes
  con nombre en un solo objeto, para ajustarlas con el piloto sin tocar lógica.
- **Zona compartida:** `PerfilHogar` va en `domain/model/` raíz porque lo usan
  reserva y recibo. Se avisa a Iker antes de crearlo.

## Riesgos

| Riesgo | Respuesta |
|---|---|
| El core (`core/db`, `core/di`) sigue vacío y bloquea la persistencia | Es mío como custodio: se hace en la semana 10, antes de las tablas. |
| Unidad m³ frente a litros en el anteproyecto §7 | Cerrar la decisión abierta en semana 9. |
| Dayan cambia el contrato de cronograma | Solo uso `LocalDateTime?`; el impacto es mínimo. |
