# Especificación · Reserva domiciliaria

**Dueño:** Cristhian Mamani · `feature/reserva/`
**Estado:** borrador para revisión cruzada
**Fuentes:** Anteproyecto §7 · Plan de Trabajo §3 · Constitución · `specs/sector.md`

## Qué resuelve

Saber cuánta agua le queda al hogar, hasta cuándo alcanza y si va a
quedarse sin agua antes de que vuelva el suministro. El dato que produce
es **el déficit**: las horas que el hogar pasaría sin agua entre el
agotamiento proyectado y el siguiente abastecimiento del sector.

## Contrato con otras verticales

**Consume de `feature/sector`** (Dayan, ya existe):

```kotlin
val proximo = ProximoAbastecimiento().calcular(cronogramas, ahora)
val vuelveElAgua = proximo?.inicio   // LocalDateTime, o null si no hay horario
```

**Expone a `feature/retos`** (Jimmy), para el posicionamiento por sector:

```kotlin
interface ReservaRepository {
    fun observarNivel(): Flow<NivelReserva>
    suspend fun litrosPorHabitanteDia(): LitrosPorHabitanteDia?   // null sin datos
    suspend fun registrarLlenado(momento: LocalDateTime): Result<Unit>
    suspend fun declararSinAgua(momento: LocalDateTime): Result<Unit>
}
```

**Puede consumir de `feature/recibo`** (Iker), sin depender de ello: el
consumo estimado por hábitos declarados. Sin él, opero solo con eventos de
llenado (§ Fuente del consumo).

Mientras no exista la implementación real, todos trabajan contra
`FakeReservaRepository`.

## Fórmulas (Anteproyecto §7)

| Cálculo | Definición |
|---|---|
| Consumo diario | `capacidad / días entre dos llenados consecutivos` |
| Litros por habitante y día | `consumo diario / habitantes` |
| Nivel en un instante | `capacidad − consumo horario × horas desde el llenado`, con mínimo 0 |
| Agotamiento proyectado | `último llenado + capacidad / consumo horario` |
| Déficit | `próximo abastecimiento − agotamiento proyectado`, con mínimo 0 |

Todo valor numérico viaja en un objeto de valor (`Litros`, `NivelReserva`,
`VentanaAbastecimiento`); ningún entero suelto.

## Decisiones del plan §3.1

Cada una es un caso de prueba. Son propuestas para la revisión cruzada.

**1. Varios días sin registrar el llenado.** No se asume que se llenó. Si
comenzó una ventana de abastecimiento del sector después del último
llenado y no hay registro, el estado pasa a `SIN_CONFIRMAR`: la
proyección sigue con el último llenado conocido, se muestra como no
confiable y se pide confirmar con una sola acción (plan §3.6). Un
intervalo entre llenados que dura más del doble de la mediana de los
anteriores se considera un olvido y **no entra** al cálculo del consumo.

**2. Fuente del consumo: histórico del hogar, no las últimas 48 horas.**
Con llenados cada 2 o 3 días, 48 horas abarcan un solo intervalo y un día
atípico distorsiona todo. Se usa la **mediana de los últimos 5 intervalos
válidos** (la mediana, por la misma razón que en la spec de sector).
Con menos de 2 intervalos se usa la estimación por hábitos declarados; si
tampoco existe, la capacidad entre 2 días como valor inicial y se marca
`ESTIMACION_INICIAL`.

**3. Cuándo se considera agotada la reserva.** Cuando el nivel proyectado
llega a 0, o cuando el usuario lo declara con "me quedé sin agua". La
declaración manda sobre la proyección.

**4. Sector sin cronograma.** El próximo abastecimiento es `null`: se
muestra el nivel y la hora de agotamiento, **sin déficit** y sin error.
No hay aviso de agotamiento anticipado, porque no hay contra qué comparar.

**5. El usuario se quedó sin agua antes de lo proyectado.** La
declaración es una observación real: el tanque de capacidad `C` se vació
en `h` horas, así que el consumo observado es `C / h`. Se agrega como un
intervalo válido más (con peso doble frente a uno inferido) y se
recalcula. Una sola declaración no cambia la estimación más de un 30 %.

## Reglas de negocio

**Llenado.** Registrar un llenado deja el nivel en la capacidad completa.
No hay llenado parcial en esta entrega.

**Litros por habitante.** Se calcula con los mismos intervalos válidos que
el consumo. Sin al menos un intervalo, es `null`.

**Déficit positivo.** Si el agotamiento es posterior al próximo
abastecimiento, no hay déficit (0 horas): la reserva alcanza.

**Recomendaciones.** Solo con déficit positivo. Cada una expresa su
impacto en litros; se ofrecen en orden de mayor impacto hasta cubrir el
déficit.

## Criterios de aceptación

| ID | Criterio |
|---|---|
| CA-01 | Una capacidad menor o igual a 0 litros se rechaza. |
| CA-02 | Un valor de litros negativo se rechaza. |
| CA-03 | Un hogar con 0 habitantes se rechaza. |
| CA-04 | Consumo diario = capacidad / días entre llenados (1000 L en 4 días = 250 L/día). |
| CA-05 | Litros por habitante y día = consumo diario / habitantes (250 L/día, 5 personas = 50). |
| CA-06 | Al registrar el llenado, el nivel es la capacidad completa. |
| CA-07 | El nivel baja de forma lineal según el consumo horario. |
| CA-08 | El nivel nunca es menor que 0. |
| CA-09 | Agotamiento = último llenado + capacidad / consumo horario. |
| CA-10 | Déficit = próximo abastecimiento − agotamiento, si es positivo. |
| CA-11 | Si el agotamiento es posterior al próximo abastecimiento, el déficit es 0. |
| CA-12 | Con próximo abastecimiento `null`, no hay déficit ni error. |
| CA-13 | Con un abastecimiento del sector sin llenado registrado, el estado es `SIN_CONFIRMAR`. |
| CA-14 | Un intervalo mayor al doble de la mediana no entra al consumo. |
| CA-15 | El consumo usa la mediana de los últimos 5 intervalos válidos. |
| CA-16 | Con menos de 2 intervalos se usa la estimación por hábitos. |
| CA-17 | Sin intervalos ni hábitos, se usa el valor inicial marcado `ESTIMACION_INICIAL`. |
| CA-18 | "Me quedé sin agua" vacía la reserva en ese instante. |
| CA-19 | Esa declaración agrega un intervalo observado y recalcula el consumo. |
| CA-20 | Una declaración no cambia el consumo estimado más de un 30 %. |
| CA-21 | Sin déficit no se generan recomendaciones. |
| CA-22 | Con déficit, las recomendaciones se ordenan por impacto en litros, de mayor a menor. |
| CA-23 | Litros por habitante y día es `null` sin ningún intervalo válido. |

## Decisiones abiertas

| Decisión | Por qué importa | Fecha límite |
|---|---|---|
| Unidad de la capacidad | El anteproyecto §7 multiplica por 1000 para litros por habitante, lo que supone capacidad en m³. Esta spec usa litros en todo el dominio. Hay que corregir el anteproyecto. | Semana 9 |
| 5 intervalos, doble de la mediana y tope de 30 % | Valores iniciales. Se ajustan con los hogares del piloto. | Semana 16 |
| Llenado parcial | Con tanque elevado y cisterna, no siempre se llena por completo. | Semana 12 |
| Coeficientes de recomendaciones | Litros ahorrados por acción (ducha corta, lavadora llena…). Se toman de Sunass o del docente. | Semana 14 |
| Peso doble de la observación real | Es un criterio propio, sin fuente. | Semana 16 |

## Fuera de esta entrega

Base de datos, tablas en la nube, pantallas, tarea horaria en segundo
plano y avisos locales. Dependen del core o son de las semanas 12 a 15.
