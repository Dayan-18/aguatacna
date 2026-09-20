# Especificación · Reserva domiciliaria

**Dueño:** Cristhian Mamani · `feature/reserva/`
**Estado:** borrador para revisión cruzada (v2, alineada con el Figma)
**Fuentes:** Anteproyecto §7 · Plan de Trabajo §3 · Constitución · `specs/sector.md` ·
[Figma AguaTacna](https://www.figma.com/design/jNSK9uzGlMub59LwBEQi5I/AguaTacna)

## Qué resuelve

Saber cuánta agua le queda al hogar, hasta cuándo alcanza y si va a
quedarse sin agua antes de que vuelva el suministro. El dato que produce
es **el déficit**: las horas que el hogar pasaría sin agua entre el
agotamiento proyectado y el siguiente abastecimiento del sector.

## Pantallas de referencia (Figma)

| Pantalla | Nodo | Qué exige al dominio |
|---|---|---|
| 02 Configurar hogar | `6:54` | Tipo (Tanque elevado, Cisterna, Bidones), capacidad, habitantes, hábitos, consumo estimado inicial |
| 03 Mi reserva | `5:2` | Nivel en litros y %, estado de la proyección, agotamiento, déficit, consumo en L/h, L/hab·día |
| 04 Registrar llenado | `8:2` | Llenado completo, a la mitad, "no llegó" |
| 05 Qué recortar | `8:51` | Recomendaciones seleccionables, ahorro, horas ganadas, horas que aún faltan |
| 19 Te quedaste sin agua | `53:2` | Previsualizar qué cambia al declarar que se acabó el agua, ahora o a una hora anterior |
| 20 Sector sin cronograma | `53:55` | Sin horario no hay déficit ni proyección (es una pantalla de sector) |
| 21 Estimación no confirmada | `54:2` | Llenado asumido con opción de confirmarlo o corregir la hora |

Las pantallas 19, 20 y 21 resolvieron lo que antes no tenía diseño: "me quedé sin agua" (CA-18),
sector sin cronograma (CA-12) y la estimación no confirmada (CA-13).

## Contrato con otras verticales

**Consume de `feature/sector`** (Dayan, ya existe):

```kotlin
val proximo = ProximoAbastecimiento().calcular(cronogramas, ahora)
val vuelveElAgua = proximo?.inicio   // LocalDateTime, o null si no hay horario
```

Para el llenado asumido (CA-13) hace falta además saber **cuándo empezó el
último abastecimiento del sector** (`LocalDateTime?`). Se le pide a Dayan;
mientras tanto se trabaja contra el fake.

**Expone a `feature/retos`** (Jimmy) y a la interfaz:

```kotlin
interface ReservaRepository {
    fun observarReserva(): Flow<Reserva?>   // null sin llenados ni abastecimientos
    suspend fun litrosPorHabitanteDia(): LitrosPorHabitanteDia?   // null sin datos
    suspend fun registrarLlenado(momento: LocalDateTime, tipo: TipoLlenado): Result<Unit>
    suspend fun registrarSinLlegada(momento: LocalDateTime): Result<Unit>
    suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit>
}
enum class TipoLlenado { COMPLETO, MITAD }
```

**Puede consumir de `feature/recibo`** (Iker), sin depender de ello: el
consumo estimado por hábitos declarados. Sin él, opero solo con eventos de
llenado (§ Fuente del consumo).

Mientras no exista la implementación real, todos trabajan contra
`FakeReservaRepository`.

## Fórmulas (Anteproyecto §7)

| Cálculo | Definición |
|---|---|
| Consumo diario | `litros consumidos / días del intervalo` (con llenado completo y tanque vaciado: `capacidad / días entre llenados`) |
| Litros por habitante y día | `consumo diario / habitantes` |
| Nivel en un instante | `nivel tras el llenado − consumo horario × horas transcurridas`, con mínimo 0 |
| Agotamiento proyectado | `momento del llenado + nivel tras el llenado / consumo horario` |
| Déficit | `próximo abastecimiento − agotamiento proyectado`, con mínimo 0 |
| Ganas (recortes) | `ahorro seleccionado / consumo horario` |
| Aún faltan | `déficit − ganas`, con mínimo 0 |

Todo valor numérico viaja en un objeto de valor (`Litros`, `NivelReserva`,
`VentanaAbastecimiento`); ningún entero suelto. Con un llenado diario de
1100 L y 4 habitantes, el consumo diario es 1100 L y los L/hab·día son 275,
como muestra el Figma. El ritmo de 82 L/h es distinto: es lo que se gasta
mientras hay agua (ver decisión 2).

## Decisiones del plan §3.1

Cada una es un caso de prueba. Son propuestas para la revisión cruzada.

**1. Varios días sin registrar el llenado: se asume.** Como dice el Figma
(pantalla 04): si el sector abasteció y el usuario no confirma, la app
**asume un llenado completo al inicio de esa ventana** y marca la
estimación como `NO_CONFIRMADA`. Reglas:
- El llenado asumido **no entra** al cálculo del consumo (CA-26): no es un dato.
- Cuando el usuario confirma (completo o a la mitad) reemplaza al asumido (CA-27).
- "No llegó" descarta el asumido: el nivel sigue bajando (CA-25).
- Sin cronograma no se asume nada (CA-34): no hay ventana en qué basarse.
- Si pasan varias ventanas seguidas sin confirmar, el aviso pide
  confirmación con una sola acción (plan §3.6).

Un intervalo entre llenados que dura más del doble de la mediana de los
anteriores se considera un olvido y **no entra** al cálculo (CA-14).

**2. Fuente del consumo: histórico del hogar, no las últimas 48 horas.**
Con llenados cada 2 o 3 días, 48 horas abarcan un solo intervalo y un día
atípico distorsiona todo. Se usa la **mediana de los últimos 5 intervalos
válidos**. Hay dos clases de intervalo:
- `OBSERVADO`: terminó con "me quedé sin agua". El tanque se vació de
  verdad, así que `capacidad / horas` es el consumo real.
- `POR_LLENADO`: terminó en otro llenado. Solo es una cota: si el tanque se
  vació a media tarde, `capacidad / horas entre llenados` **subestima** el
  ritmo, porque el tanque estuvo seco parte del tiempo.

Si hay al menos un intervalo `OBSERVADO` entre los últimos 5, se usan solo
esos (CA-28). Con menos de 2 intervalos se usa la estimación por hábitos
declarados; si tampoco existe, la capacidad entre 2 días, marcada
`ESTIMACION_INICIAL`.

**3. Cuándo se considera agotada la reserva.** Cuando el nivel proyectado
llega a 0, o cuando el usuario lo declara con "me quedé sin agua". La
declaración manda sobre la proyección.

**4. Sector sin cronograma.** El próximo abastecimiento es `null`: se
muestra el nivel y la hora de agotamiento, **sin déficit** y sin error.
No hay aviso de agotamiento anticipado ni estado de proyección.

**5. El usuario se quedó sin agua antes de lo proyectado.** La
declaración es una observación real: el tanque se vació en `h` horas, así
que el consumo es `nivel inicial / h`. Se agrega como intervalo
`OBSERVADO` y se recalcula. Una sola declaración no cambia la estimación
más de un 30 %, para que un error de dedo no la desordene.

## Reglas de negocio

**Llenado.** Tres acciones, como en la pantalla 04:
- *Sí, está lleno*: el nivel pasa a la capacidad completa.
- *Llenó a la mitad*: el nivel pasa al 50 % de la capacidad (no suma al
  nivel anterior).
- *No llegó*: el agua no llegó al sector; no hay llenado.

**Estado de la proyección** (el chip de la pantalla 03):
`NO_ALCANZA` si hay déficit; `AJUSTADA` si el margen antes del
abastecimiento es menor al umbral; `COMODA` si es mayor. Sin cronograma no
hay estado.

**Recomendaciones.** Solo con déficit positivo. Cada una expresa su
impacto en litros, se puede marcar o desmarcar, y solo se ofrecen las que
corresponden a los hábitos declarados (no se sugiere "no regar el jardín"
a quien no riega). Valores iniciales tomados del Figma:

| Recomendación | Litros |
|---|---|
| Postergar el lavado de ropa | 90 |
| Dos duchas de 5 minutos | 60 |
| No regar el jardín hoy | 80 |
| Cerrar el caño al lavar platos | 45 |

Con 275 L seleccionados y 82 L/h: ganas 3 h 20 min. Con déficit de
10 h 20 min, aún faltan 7 h.

## Criterios de aceptación

| ID | Criterio |
|---|---|
| CA-01 | Una capacidad menor o igual a 0 litros se rechaza. |
| CA-02 | Un valor de litros negativo se rechaza. |
| CA-03 | Un hogar con 0 habitantes se rechaza. |
| CA-04 | Consumo diario = capacidad / días entre llenados (1000 L en 4 días = 250 L/día). |
| CA-05 | Litros por habitante y día = consumo diario / habitantes (250 L/día, 5 personas = 50). |
| CA-06 | Al registrar un llenado completo, el nivel es la capacidad completa. |
| CA-07 | El nivel baja de forma lineal según el consumo horario. |
| CA-08 | El nivel nunca es menor que 0. |
| CA-09 | Agotamiento = momento del llenado + nivel tras el llenado / consumo horario. |
| CA-10 | Déficit = próximo abastecimiento − agotamiento, si es positivo. |
| CA-11 | Si el agotamiento es posterior al próximo abastecimiento, el déficit es 0. |
| CA-12 | Con próximo abastecimiento `null`, no hay déficit ni error. |
| CA-13 | Si el sector abasteció y no hay confirmación, se asume un llenado completo al inicio de la ventana y la estimación es `NO_CONFIRMADA`. |
| CA-14 | Un intervalo mayor al doble de la mediana no entra al consumo. |
| CA-15 | El consumo usa la mediana de los últimos 5 intervalos válidos. |
| CA-16 | Con menos de 2 intervalos se usa la estimación por hábitos. |
| CA-17 | Sin intervalos ni hábitos, se usa el valor inicial marcado `ESTIMACION_INICIAL`. |
| CA-18 | "Me quedé sin agua" vacía la reserva en ese instante. |
| CA-19 | Esa declaración agrega un intervalo `OBSERVADO` y recalcula el consumo. |
| CA-20 | Una declaración no cambia el consumo estimado más de un 30 %. |
| CA-21 | Sin déficit no se generan recomendaciones. |
| CA-22 | Con déficit, las recomendaciones se ordenan por impacto en litros, de mayor a menor. |
| CA-23 | Litros por habitante y día es `null` sin ningún intervalo válido. |
| CA-24 | "Llenó a la mitad" deja el nivel en el 50 % de la capacidad, sin sumar al anterior. |
| CA-25 | "No llegó" descarta el llenado asumido de esa ventana; el nivel sigue bajando. |
| CA-26 | Un llenado asumido no entra al cálculo del consumo. |
| CA-27 | Confirmar un llenado reemplaza al asumido de esa ventana. |
| CA-28 | Si hay intervalos `OBSERVADO` entre los últimos 5, el consumo usa solo esos; con uno basta. |
| CA-29 | Ahorro seleccionado = suma de los litros de las recomendaciones marcadas. |
| CA-30 | Ganas = ahorro seleccionado / consumo horario (275 L a 82 L/h = 3 h 20 min). |
| CA-31 | Aún faltan = déficit − ganas, con mínimo 0. |
| CA-32 | Solo se ofrecen recomendaciones acordes a los hábitos declarados. |
| CA-33 | El estado es `NO_ALCANZA` con déficit, `AJUSTADA` con margen menor al umbral y `COMODA` con margen mayor. |
| CA-34 | Sin cronograma no se asume ningún llenado. |

## Decisiones

| Decisión | Estado |
|---|---|
| Unidad de la capacidad | **Resuelta en el código:** todo el dominio y el Figma usan litros. Falta corregir el texto del anteproyecto §7, que multiplica por 1000 y supone m³ (ese documento no está en el repositorio). |
| Consulta de Dayan: inicio del último abastecimiento | **Resuelta:** `UltimoAbastecimiento` (PR de sector #6) y `AbastecimientosDeSector` la conectan. |
| Persistir el consumo vigente | **Resuelta:** `perfil_hogar.consumoVigenteLitrosHora`. |
| Pantallas faltantes en el Figma | **Resuelta:** el Figma trae las pantallas 19, 20 y 21 y la app las implementa (20 es de sector). |
| Umbral de `AJUSTADA` frente a `COMODA` | **Abierta:** 2 horas de margen, valor propio a validar. |
| "Llenó a la mitad" como 50 % fijo | **Abierta:** validar con hogares reales. |
| 5 intervalos, doble de la mediana y tope de 30 % | **Abierta:** valores iniciales, se ajustan con el piloto (T036). |
| Coeficientes de las recomendaciones | **Abierta:** los del Figma son de partida; falta una fuente (Sunass o el docente). |
| Coeficientes de consumo por hábitos | **Abierta:** `EstimadorPorHabitosProvisional` usa órdenes de magnitud propios. Iker debe aportar el estimador real de `feature/recibo`. |
| Indicador "días con registro" (14 de 14) | **Abierta:** aparece en la pantalla 04 y no está definido. |
| Regla de migraciones de Room | **Abierta, del equipo:** el esquema sigue en la versión 1 y se ha regenerado sin migración. Hay que acordar hasta cuándo se permite (propuesta: hasta la primera instalación en un dispositivo real). |
| Backend en la nube y autenticación | **Abierta, del equipo (hito H2):** sin ella no se pueden crear las tablas en la nube (T022) ni la sincronización. |

## Estado de la implementación

Hecho y probado en JVM (dominio con 96,6 % de cobertura, ver `docs/cobertura.md`): cálculos, llenado
asumido, déficit, "me quedé sin agua", recomendaciones, repositorio real sobre Room, UUID local,
inyección con Koin, pantallas Mi reserva, Registrar llenado, Configuración y Qué recortar, tarea
horaria y avisos en Android.

**Sin verificar en un dispositivo:** la interfaz, el arranque de la base de datos, la tarea horaria,
las notificaciones y la acción "Sí, lo llené" compilan y pasan las pruebas, pero nadie las ha visto
correr (T034). El código de iOS tampoco se ha compilado, porque el equipo trabaja en Windows.

## Fuera de esta entrega

Base de datos, tablas en la nube, pantallas, tarea horaria en segundo
plano y avisos locales. Dependen del core o son de las semanas 12 a 15.
