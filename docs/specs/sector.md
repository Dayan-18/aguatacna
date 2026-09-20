# Especificación · Sector, cronogramas y cisternas

**Dueño:** Dayan Jahuira · `feature/sector/`
**Estado:** borrador para revisión cruzada
**Fuentes:** Anteproyecto §6.2 y §7 · Plan de Trabajo §4 · Constitución

## Qué resuelve

Saber a qué sector pertenece el hogar, cuándo le llega el agua, cuándo vuelve
si ya se cortó, y dónde hay un camión cisterna cerca. El dato más importante
que produce es **la próxima ventana de abastecimiento**, que `feature/reserva`
usa para calcular el déficit.

## Contrato con feature/reserva

```kotlin
val proximo = ProximoAbastecimiento().calcular(cronogramas, ahora)
val vuelveElAgua = proximo?.inicio   // LocalDateTime, o null si no hay horario

val ultimo = UltimoAbastecimiento().calcular(cronogramas, ahora)
val llegoElAgua = ultimo?.inicio     // desde cuándo asumir el llenado
```

`null` significa que el sector no tiene cronograma futuro cargado. Reserva
debe mostrar la proyección sin calcular déficit, no un error.

Mientras no exista la implementación real, se trabaja contra
`FakeSectorRepository`.

## Reglas de negocio

**Ventana de abastecimiento.** El agua llega a la hora de inicio y se corta a
la hora de fin. El inicio cuenta como abastecido; el fin ya no.

**Próximo abastecimiento.** Es el cronograma con el inicio más cercano *después*
de ahora. Si el agua está llegando en este momento, el próximo es el siguiente
día: el tanque se está llenando, y lo que interesa para el déficit es cuándo
vuelve después del corte.

**Último abastecimiento.** Es el cronograma ya empezado con el inicio más
reciente. Si el agua está llegando en este momento, es el de ahora. `feature/reserva`
lo usa para asumir el llenado cuando el usuario no lo registra, y marcar esa
estimación como no confirmada.

**Sector del domicilio.** Se asigna el sector cuyo centro está más cerca de la
ubicación del usuario. En el servidor se guarda el sector, nunca la coordenada
(constitución, artículo IX).

**Cisternas cercanas.** Se muestran las que están dentro del radio, de la más
cercana a la más lejana, incluidas las que ya terminaron su reparto, marcadas
con su estado.

**Cronograma colaborativo.** Los vecinos confirman cuándo llegó y cuándo se
cortó el agua. Con al menos 3 llegadas y 3 cortes del mismo sector y fecha,
se estima el horario con la **mediana** de cada grupo: si un vecino se
equivoca y marca las 3 p.m., la mediana casi no se mueve y el promedio sí.

## Criterios de aceptación

| ID | Criterio |
|---|---|
| CA-01 | Un cronograma que termina antes de empezar se rechaza al crearse. |
| CA-02 | El inicio de la ventana cuenta como abastecido; el fin no. |
| CA-03 | La duración de 5:00 a 9:00 es de 240 minutos. |
| CA-04 | El próximo abastecimiento es el de inicio más cercano en el futuro. |
| CA-05 | Si el agua está llegando ahora, el próximo es el del día siguiente. |
| CA-06 | Antes del horario de hoy, el próximo es el de hoy. |
| CA-07 | Sin cronogramas futuros, el próximo abastecimiento es `null`. |
| CA-08 | El cronograma vigente es el que contiene el momento actual, o `null`. |
| CA-09 | Se asigna el sector de centro más cercano; sin sectores, `null`. |
| CA-10 | Las cisternas fuera del radio se descartan. |
| CA-11 | Las cisternas se ordenan de la más cercana a la más lejana. |
| CA-12 | Con menos de 3 llegadas o 3 cortes no se estima horario. |
| CA-13 | Un reporte erróneo no desplaza el horario estimado. |
| CA-14 | Se ignoran confirmaciones de otro sector o de otra fecha. |
| CA-15 | El horario estimado se marca con fuente colaborativa. |
| CA-16 | Si la llegada estimada no es anterior al corte, no se estima horario. |
| CA-17 | Un grado de latitud equivale a unos 111,19 km. |
| CA-18 | Una coordenada fuera de rango se rechaza. |
| CA-19 | El último abastecimiento es el más reciente que ya empezó. |
| CA-20 | Si el agua está llegando ahora, el último es el de ahora. |
| CA-21 | Antes del horario de hoy, el último es el de ayer. |
| CA-22 | Sin abastecimientos previos, el último es `null`. |

## Decisiones abiertas

| Decisión | Por qué importa |
|---|---|
| Mínimo de 3 confirmaciones | Es un punto de partida. Hay que validarlo en el piloto. |
| Qué significa un cronograma de tipo `EMERGENCIA` | El anteproyecto §7 lo nombra pero no lo define. |
| Si hay horario de EPS y colaborativo el mismo día, cuál manda | Hoy se devuelven ambos sin prioridad. |
| Ventanas que cruzan la medianoche | No se soportan. En Tacna no se observaron. |
| Mapa con MapLibre en lugar de Google Maps | El anteproyecto §5.3 dice Google Maps SDK. MapLibre es gratuito, no pide tarjeta ni API key y funciona en Android e iOS con un solo código. Hay que avisar al docente y actualizar §5.3. |

## Fuera de esta entrega

Base de datos, cliente HTTP, pantallas, permisos de ubicación y notificaciones
push. Dependen del core o son de las semanas 14 y 15.
