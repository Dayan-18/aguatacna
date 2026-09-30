# Prompt: corrección completa del módulo Recibo (AguaTacna)

> Copia todo lo que está debajo de la línea y pégalo en tu asistente de código (con acceso al repositorio `aguatacna`).

---

## Contexto

Trabajas en el proyecto **AguaTacna** (Kotlin Multiplatform + Compose Multiplatform, Room KMP, Koin, Supabase, Google ML Kit).
El módulo a corregir es:

```
shared/src/commonMain/kotlin/pe/edu/upt/aguatacna/feature/recibo/
shared/src/androidMain/kotlin/pe/edu/upt/aguatacna/feature/recibo/
shared/src/iosMain/kotlin/pe/edu/upt/aguatacna/feature/recibo/
shared/src/commonTest/kotlin/pe/edu/upt/aguatacna/feature/recibo/
shared/src/androidHostTest/kotlin/pe/edu/upt/aguatacna/feature/recibo/
```

Antes de tocar nada lee:

- `docs/constitution.md`: dominio puro, sin conexión como base, UUID local, privacidad, "el ViewModel orquesta, no calcula" y "no se permite código muerto".
- `informacionRecibo.md` (raíz): informe del módulo con las 10 observaciones.
- Cada archivo del módulo, completo.

### Reglas de trabajo

1. Crea una rama `fix/recibo-observaciones` desde `main`. No hagas commit ni push sin que yo lo pida.
2. **No corrijas la observación 8** (límite de 150 líneas por archivo). Si al quitar duplicados algún archivo baja de tamaño, mejor, pero no partas archivos solo por eso.
3. `domain/` solo puede importar Kotlin estándar, corrutinas y kotlinx-datetime. Nada de `infrastructure`, `data`, Room, Koin, Supabase ni Android.
4. Toda la lógica de negocio va en `domain/` (casos de uso o servicios). Los composables solo dibujan y llaman al ViewModel; el ViewModel solo orquesta.
5. Fuera de las carpetas del módulo solo toca lo que se indica aquí. Lista cada archivo externo que cambies, porque necesita la revisión del custodio (Cristhian): `AguaTacnaDatabase.kt`, `InicioAndroid.kt`, `supabase/migrations`, `N8nApiClient.kt`, `docs/specs/recibo.md`.
6. Mantén los textos de la UI en español y el formato peruano ("S/ 74,20", "Set").
7. Al terminar, compila y corre las pruebas:
   ```
   ./gradlew :androidApp:assembleDebug
   ./gradlew :shared:testAndroidHostTest
   ```
   Arregla todo lo que falle antes de dar el trabajo por terminado.

---

## Tarea 1 — Arreglar el OCR del consumo (PROMEDIO m³ / Volumen Fac m³)

### Síntoma

Al fotografiar el recibo de EPS Tacna se leen bien el período, el importe, las fechas, el medidor y el N.º de recibo. Lo único que falla es el **consumo en m³**. En el recibo real aparece así, en la columna "DETALLES DE CONSUMO":

```
PROMEDIO m³        23
Volumen Fac m³     23
Tipo Consumo: PROMEDIO
Consumo: AGOSTO-2026
```

Debe guardarse `consumoM3 = 23`, tomado de **Volumen Fac m³** o, si ese no se lee, de **PROMEDIO m³**.

### Causa probable (verifícala)

`ParserReciboEpsTacna` solo usa `texto.textoPlano`. ML Kit agrupa el texto en **bloques**, y en esta tabla la columna de etiquetas ("PROMEDIO m³", "Volumen Fac m³") y la columna de valores ("23", "23") suelen salir en **bloques distintos**. Entonces el texto plano queda más o menos así:

```
PROMEDIO m³
Volumen Fac m³
Tipo Consumo: PROMEDIO
Consumo: AGOSTO-2026
...
23
23
```

Las regex actuales (`VOLUMEN\s*FAC...(\d+)` y `PROMEDIO\s*(?:M\s*[3³]|M)?...(\d+)`) no encuentran el número detrás de la etiqueta y el consumo queda en `null`. Además, el "³" a veces se lee como `²`, `ª`, `°`, `'` o `?` y rompe la regex.

### Qué hacer

1. **Reconstruir el texto por filas usando las posiciones** que ya trae `TextoReconocido.lineas` (`LineaTexto` con `x`, `y`, `ancho`, `alto`). Hoy esa información se ignora.
   - Crea en `infrastructure/ocr/` una función `reconstruirFilas(lineas: List<LineaTexto>): String`:
     - ordena las líneas por su centro vertical (`y + alto/2`);
     - agrupa en la misma fila las líneas cuyo centro vertical difiera como máximo `0.5 × max(alto)` del promedio de la fila;
     - dentro de cada fila ordena por `x` y une con dos espacios; las filas se separan con `\n`.
   - Así "PROMEDIO m³" y "23" quedan en la misma línea: `PROMEDIO M³  23`.
2. En `parsear(texto: TextoReconocido)` extrae los campos **dos veces**: una sobre `textoPlano` y otra sobre `reconstruirFilas(lineas)`. Combínalos campo por campo:
   - usa el de `textoPlano` salvo que su valor sea `null` o tenga menor confianza que el de filas;
   - así no empeora nada de lo que hoy ya funciona.
3. Haz más tolerantes las regex del consumo:
   - Volumen facturado (confianza 0.95): `VOLUMEN\s*FAC[A-Z.]*\s*(?:M(?![A-Z]))?[^\d]{0,4}?(\d{1,4})`
   - Promedio (confianza 0.90): `PROMEDIO\s*M(?![A-Z])[^\d]{0,4}?(\d{1,4})`
     - exige una `M` justo después de PROMEDIO, para no confundirlo con "Tipo Consumo: PROMEDIO" ni con "MEDIDOR";
     - el lookahead `(?![A-Z])` evita tomar palabras como "MEDIDOR" o "MES".
   - Mantén `CONSUMO\s*FACTURADO` como respaldo (0.75).
   - Asegúrate de que la normalización `M3 → M³` y `M 3 → M³` siga funcionando con "PROMEDIO m 3 23".
4. Quita la duplicación de los extractores: crea helpers privados como `buscarEntero(regex, texto, confianza): Campo<Int>?` y `buscarTexto(...)`, en vez de repetir "find → toIntOrNull → Campo" en cada función.
5. **Pruebas nuevas** en `ParserReciboEpsTacnaTest`:
   - un `TextoReconocido` cuyo `textoPlano` tenga etiquetas y valores separados (como el bloque de arriba) y cuyas `lineas` tengan posiciones reales: "PROMEDIO m³" en x=370, y=427, alto=20; "23" en x=578, y=427, alto=20; "Volumen Fac m³" en x=370, y=463; "23" en x=578, y=463. Debe dar `consumoM3 = 23`;
   - "PROMEDIO m² 23", "PROMEDIO mª 23" y "Volumen Fac. m³ 23" dan 23;
   - "Tipo Consumo: PROMEDIO" seguido de "MEDIDOR" **no** produce un consumo falso;
   - las pruebas actuales siguen pasando.

---

## Tarea 2 — Corregir las observaciones del informe (todas menos la 8)

### Observación 1: "Modificar recibo" no se puede guardar

Hoy `RevisionViewModel.confirmar()` rechaza como duplicado cualquier recibo cuyo período ya exista, incluso cuando lo estás editando.

- Agrega a `ReciboBorrador` el campo `idRecibo: String? = null`. `Recibo.aBorrador()` lo llena con `id`.
- Mueve la regla al dominio. `ConfirmarReciboUseCase` recibe el **borrador** y devuelve un resultado sellado:
  ```kotlin
  sealed interface ResultadoConfirmacion {
      data class Guardado(val recibo: Recibo, val reemplazo: Boolean) : ResultadoConfirmacion
      data class Duplicado(val periodo: PeriodoConsumo) : ResultadoConfirmacion
      data object Incompleto : ResultadoConfirmacion
  }
  ```
  - Si el borrador no es confirmable → `Incompleto`.
  - `existente = repository.obtenerPorPeriodo(periodo)`. Si existe **y** `existente.id != borrador.idRecibo` → `Duplicado`.
  - `id = borrador.idRecibo ?: "recibo-$anio-$mes"` → `repository.guardar(borrador.confirmar(id))` → `Guardado`.
- `RevisionViewModel` ya no recibe `ReciboRepository`: solo llama al caso de uso y traduce el resultado a mensajes.
- `FakeReciboRepository.guardar` debe imitar a Room: reutilizar el id del período existente y no dejar dos filas con el mismo id cuando cambia el período.
- Pruebas: editar un recibo existente se guarda y reemplaza; crear otro del mismo mes da `Duplicado`; cambiar el período de un recibo editado lo mueve sin duplicarlo.

### Observación 2: el dominio depende de la infraestructura

- `EscanearReciboUseCase(reconocedor: ReconocedorTexto, parser: ParserRecibo)` sin valor por defecto. Borra el import de `ParserReciboEpsTacna`.
- En `ReciboModule`: `single<ParserRecibo> { ParserReciboEpsTacna }` y `factory { EscanearReciboUseCase(get(), get()) }`.
- Actualiza `EscanearReciboUseCaseTest` para pasar el parser.

### Observación 3 + pedido Sunass: un solo criterio de "Alto consumo"

Decisión: **se elimina la regla Sunass** (más del 100 % sobre el promedio) y **todo lo relacionado a reclamos ante Sunass**. Queda una sola regla: **consumo > 100 m³ = Alto consumo**, y lo único que ve el usuario es un **mensaje corto y tranquilo**, sin pasos de reclamo ni textos alarmantes.

- `EvaluadorConsumo`:
  - `const val LIMITE_M3 = 100`;
  - `fun promedio(mesesPrevios: List<Int>): Int?` (hasta 6 meses, `null` si no hay);
  - `fun variacionPorcentaje(consumo: Int, promedio: Int?): Int?`;
  - `evaluar(...)` devuelve, en este orden: `> LIMITE_M3` → `Atipico`; tipo `PROMEDIO` → `FacturadoPorPromedio`; sin meses previos → `SinHistorial`; si no → `Normal`;
  - borra `MESES_MINIMOS_HISTORIAL`, `UMBRAL_ATIPICO_PORCENTAJE`, `umbralAtipicoM3` y el comentario del art. 88.
- `ObservarResumenUseCase`, `ObservarHistorialUseCase` y `HistorialViewModel` dejan de calcular estado o porcentajes por su cuenta: **todos usan `EvaluadorConsumo`**. Así se elimina la lógica triplicada.
- `EstadoConsumo`:
  - `excesoPorcentaje` pasa a `Int?`, y `variacionTexto` muestra "—" cuando es null;
  - mensaje de `Atipico`: "`{Mes}` superó los 100 m³. Revisa si hay alguna fuga en casa.";
  - cambia el comentario "según la regla de Sunass" por uno neutro.
- `ObservarHistorialUseCase`: la ventana es **los 6 meses que terminan en el recibo más reciente**. Hoy empieza en el más antiguo y puede incluir meses futuros vacíos.
- UI (dentro del módulo):
  - borra `DialogoComoReclamar`, `PasoReclamo`, `BotonesAccionHistorial`, el parámetro `onComoReclamar` y el estado `mostrarDialogoReclamo`/`mostrarDialogoReclamo()` del `HistorialViewModel`;
  - cambia "Variación Sunass" por "Variación" y "Ver histórico y cómo reclamar" por "Ver histórico de consumo";
  - `AlertaEstadoHistorial` solo muestra el mensaje corto;
  - el gráfico dibuja la línea en `EvaluadorConsumo.LIMITE_M3` y colorea con `slot.esAtipico`, no con un `100` escrito a mano.
- **Fuera del módulo, solo lo que choca con Recibo:**
  - `feature/asistente/data/remote/N8nApiClient.kt`: en la respuesta de demostración cambia "verificar si supera el 100% de tu promedio para iniciar un reclamo formal ante Sunass" por "revisar si supera el límite de 100 m³";
  - `docs/specs/recibo.md`: quita la referencia a "Sunass (art. 88)" y describe la regla de 100 m³;
  - **no toques** `README.md`, `docs/reparto-de-trabajo.md` ni `docs/specs/reserva.md`: mencionan a Sunass, pero no chocan con Recibo.
- Actualiza `EvaluadorConsumoTest`, `ObservarHistorialUseCaseTest` y `FakeReciboRepositoryTest`:
  - 33 m³ ahora es Normal y 120 m³ es Alto consumo;
  - un recibo por PROMEDIO de 120 m³ también es Alto consumo;
  - borra las aserciones de umbral al 100 %.

### Observación 4: valores escritos a mano

- `ReciboBorrador.confirmar()` no debe inventar datos:
  - borra `PeriodoConsumo(2026, 8)`, `numeroMedidor = "0412887"`, la emisión "día 28" y el vencimiento "día 11";
  - lo que falte queda en `null`;
  - `esConfirmable` = período + consumo + importe.
- `ReciboFotoScreen` no debe crear un borrador de agosto 2026. Usa el período actual (ver "Reloj" abajo).
- `TarjetaDocumentoRecibo`: cambia "Suministro 0412887" por "Sin número de medidor".
- Crea `ReciboBorrador.vacio(periodo: PeriodoConsumo)` (origen MANUAL) y úsalo en **todos** los lugares donde hoy se arma a mano el borrador vacío: `ReciboScreen` (2 veces), `ReciboManualMedidorScreen` y `ReciboFotoScreen`.
- Reloj:
  - agrega `PeriodoConsumo.de(fecha: LocalDate)`;
  - obtén la fecha del `Reloj` inyectado por Koin (`single<Reloj>` ya existe en `moduloCore`), no con `RelojDelSistema()` suelto;
  - `SelectorPeriodoMeses` recibe `periodoActual` por parámetro.
- Actualiza la prueba que esperaba `"11 Set 2026"` como vencimiento inventado.

### Observación 5: `ValidadorRecibo` y `SemillaDepuracion` sin uso

- `ValidadorRecibo`:
  - agrega `validar(borrador: ReciboBorrador)`, que valide con los valores disponibles aunque falten campos;
  - `RevisionViewModel` expone `advertencias: StateFlow<List<String>>`;
  - `ReciboFotoScreen` las muestra con el mismo componente de aviso que usa el error de duplicado (ver Tarea 3);
  - las advertencias no bloquean la confirmación.
- `SemillaDepuracion` solo la usan las pruebas: muévela a `commonTest/.../recibo/SemillaRecibos.kt`, con agosto = 120 m³ para probar el Alto consumo, y bórrala de `data/`.

### Observación 6: sin OCR en iOS

- Crea en `commonMain/.../recibo/infrastructure/ocr/ReconocedorTextoPlataforma.kt`:
  ```kotlin
  expect fun crearReconocedorTexto(): ReconocedorTexto
  ```
- `androidMain/.../ReconocedorTextoPlataforma.android.kt` → `actual fun crearReconocedorTexto(): ReconocedorTexto = ReconocedorTextoAndroid()`.
- `iosMain/.../ReconocedorTextoPlataforma.ios.kt` → un reconocedor que devuelva `Result.failure(UnsupportedOperationException("En iOS aún no se puede leer el recibo con la cámara. Ingresa los datos a mano."))`. La pantalla de error ya ofrece "Ingresar datos a mano". Deja un comentario del *porqué* y la opción futura de Apple Vision (`VNRecognizeTextRequest`).
- `ReciboModule`: `single<ReconocedorTexto> { crearReconocedorTexto() }`.
- `core/di/InicioAndroid.kt` (**archivo del core**): borra `single<ReconocedorTexto> { ReconocedorTextoAndroid() }` y sus dos imports, para que no quede una definición duplicada.

### Observación 7: sincronización en la nube con cuenta de Google

Objetivo: con **SIN_CUENTA** todo sigue siendo local. Con **GOOGLE**, los recibos se copian a Supabase y se recuperan en otro teléfono. Sigue el patrón de `feature/reserva/data/sync/` (`NubeReserva`, `NubeReservaSupabase`, `SincronizadorReserva`).

1. **Migración** `supabase/migrations/20260929_recibo_tabla_inicial.sql`:
   ```sql
   create table public.recibo (
     usuario_id uuid not null default auth.uid() references auth.users(id) on delete cascade,
     id text not null,
     anio integer not null check (anio between 2000 and 2100),
     mes integer not null check (mes between 1 and 12),
     consumo_m3 integer not null check (consumo_m3 >= 0),
     importe_centimos bigint not null check (importe_centimos >= 0),
     fecha_emision date,
     fecha_vencimiento date,
     tipo_consumo text not null,
     lectura_anterior_m3 integer,
     lectura_actual_m3 integer,
     numero_medidor text,
     numero_recibo text,
     origen text not null,
     actualizado_en timestamptz not null default now(),
     primary key (usuario_id, id)
   );
   create trigger recibo_actualizado before update on public.recibo
     for each row execute function public.tocar_actualizado_en();
   alter table public.recibo enable row level security;
   create policy "recibo: cada usuario lo suyo" on public.recibo
     for all to authenticated
     using ((select auth.uid()) = usuario_id) with check ((select auth.uid()) = usuario_id);
   ```
   La clave es `(usuario_id, id)`, no el período, para que al mover un recibo de mes se actualice la misma fila.
2. `data/sync/NubeReciboSupabase.kt`:
   - DTO `@Serializable ReciboNube` con `@SerialName` en snake_case;
   - `descargar(): List<ReciboEntity>?` devuelve `null` si no hay sesión;
   - `subir(recibos: List<ReciboEntity>)` hace `upsert` con `usuario_id = supabase.auth.currentUserOrNull()?.id`.
3. `data/sync/SincronizadorRecibo.kt`, con las mismas reglas que Reserva (Room es la fuente de verdad y esta versión solo suma):
   - los recibos remotos cuyo `id` **y** período no existen localmente se insertan en Room;
   - todos los locales se suben con upsert (en conflictos gana el teléfono);
   - los borrados no se propagan (documéntalo);
   - `mantenerSincronizado()` combina `haySesion` con `dao.observarTodos()`, aplica `debounce(2000)` y sincroniza si hay sesión;
   - los errores de red devuelven `false`, sin romper la app.
4. Registro en Koin (`ReciboModule`), como en `ReservaModule`:
   `SincronizadorRecibo(dao = get(), nube = NubeReciboSupabase(get()), haySesion = get<SupabaseClient>().auth.sessionStatus.map { it is SessionStatus.Authenticated })`.
5. Arranque **sin tocar `MainActivity`**: `ReciboViewModel` recibe el sincronizador (nullable, para vistas previas) y en `init` lanza `viewModelScope.launch { sincronizador?.mantenerSincronizado() }`. El VM vive mientras vive la Activity.
6. Pruebas en `androidHostTest` con `FakeReciboDao` y una `NubeRecibo` falsa:
   - sin sesión no hace nada;
   - un teléfono vacío restaura desde la nube;
   - lo local se sube;
   - no se duplica un período que ya existe localmente.
7. Recuérdame en tu resumen que **hay que aplicar la migración en el proyecto Supabase "AguaTacna"**.

### Observación 9: lógica dentro de la UI

- `ReciboScreen`: borra `KoinPlatform.getKoin()`, `rememberCoroutineScope` y la consulta al repositorio. Las acciones van a los ViewModels:
  - `ReciboViewModel.iniciarManual()` y `ReciboViewModel.prepararRevision(recibo)`;
  - `HistorialViewModel.prepararEdicion(periodo, onListo)`, que busca el recibo → `aBorrador()` o `ReciboBorrador.vacio(periodo)` → lo guarda en el store;
  - `CapturaViewModel.iniciarManual()`, para que "Ingresar datos a mano" desde la cámara no reutilice un borrador viejo.
- `ReciboManualMedidorScreen`: crea `CorreccionViewModel(store, corregirCampo, reloj)` con:
  - `valorInicial(campo): String`;
  - `guardar(campo, entrada, periodo): String?`, que devuelve el mensaje de error o `null` si se aplicó.

  El composable solo maneja las teclas y muestra el error. Saca también la edición de la entrada con el teclado a una función pura `aplicarTecla(entrada, tecla, permiteComa, maxDigitos): String`.
- `CorregirCampoUseCase` valida y devuelve `sealed ResultadoCorreccion { Aplicada(borrador); Invalida(mensaje) }`. Reglas (hoy duplicadas en la pantalla):
  - consumo entre 0 y 999;
  - lecturas ≥ 0, con anterior ≤ actual;
  - importe > 0 y ≤ 99 999.
- **Bug de dinero**: `(importeDouble * 100).toLong()` trunca (78,29 → 7 828). Usa `Dinero.parsear(entrada)`, que redondea.
- Agrega pruebas de `CorregirCampoUseCase` para cada validación.

### Observación 10: el borrador se pierde si el sistema cierra la app

- Crea `data/local/ReciboBorradorEntity.kt` y `ReciboBorradorDao.kt`:
  - tabla `recibo_borrador` con una sola fila `id = 1` y una columna `contenido` (JSON);
  - DTO `@Serializable BorradorGuardado` con `CampoGuardado(valor: String?, confianza: Float, corregido: Boolean)` por campo, más `origen` e `idRecibo`;
  - funciones de mapeo en ambos sentidos. Sin datos personales.
- `BorradorReciboStore(dao: ReciboBorradorDao? = null, scope = CoroutineScope(SupervisorJob() + Dispatchers.Default))`:
  - al crearse lee el borrador guardado (`compareAndSet(null, guardado)`) y luego escucha `_borrador` para escribirlo o borrarlo;
  - con `dao = null` funciona solo en memoria (pruebas y vistas previas).
- **Archivos del core:**
  - `AguaTacnaDatabase.kt`: agrega `ReciboBorradorEntity`, sube a `version = 5`, agrega `AutoMigration(from = 4, to = 5)` y `abstract fun reciboBorradorDao()`;
  - `InicioAndroid.kt`: `single { base.reciboBorradorDao() }`.
- Compila para que Room genere `shared/schemas/.../5.json` e inclúyelo.
- Prueba: el borrador se guarda, se restaura al crear otro store con el mismo DAO falso y se borra al confirmar.

---

## Tarea 3 — Quitar código repetido y simplificar

Revisa **todo** el módulo y unifica lo que hace lo mismo. Como mínimo:

| Dónde | Repetición | Cómo unificar |
|---|---|---|
| `CamaraReciboScreen` | `PantallaErrorLectura`, `PantallaPermisoDenegado` y `PantallaPermisoDenegadoPermanente` son la misma tarjeta con otro ícono, texto y botones | Un solo `TarjetaAvisoCamara(icono, colorIcono, fondoIcono, titulo, mensaje, acciones)` |
| `ReciboFotoScreen` | 3 chips casi iguales ("Revisa los datos", "Manual", "Leído") | Un `ChipRevision(texto, colorTexto, fondo, borde)` |
| `ReciboFotoScreen` | La tarjeta naranja del error de duplicado | Un `AvisoRevision(texto)` reutilizado para duplicados y advertencias |
| `ReciboHistorialScreen` | `ReciboBarraSuperior("Tu histórico", ...)` repetida en 3 ramas | Sacarla fuera del `when` |
| `EstiloEstado.desde` | `Normal` y `SinHistorial` idénticos salvo el texto del chip | Un constructor privado `estiloTeal(chip)` |
| `ObservarResumen`, `ObservarHistorial`, `HistorialViewModel` | Cálculo de promedio, variación y estado repetido 3 veces | `EvaluadorConsumo` |
| `ReciboScreen`, `ManualMedidor`, `FotoScreen` | Borrador vacío armado a mano 4 veces | `ReciboBorrador.vacio(periodo)` |
| `ReciboScreen`, `ManualMedidor`, `SelectorPeriodoMeses` | `PeriodoConsumo(ahora.year, ahora.monthNumber)` 3 veces | `PeriodoConsumo.de(fecha)` + `Reloj` inyectado |
| `RevisionViewModel` + `ConfirmarReciboUseCase` | Doble consulta de existencia por período | Una sola, en el caso de uso |
| `CorregirCampoUseCase` | 5 ramas `copy(valor, confianza = 1f, corregidoPorUsuario = true)` | `fun <T> Campo<T>.corregir(valor: T)` |
| `Recibo.aBorrador()` | 10 veces `Campo(x, confianza = 1f, corregidoPorUsuario = false)` | Helper `Campo.confirmado(valor)` |
| `ReciboManualMedidorScreen` | Validación de lecturas duplicada y en la UI | `CorregirCampoUseCase` |
| `ParserReciboEpsTacna` | 10 extractores con el mismo patrón | Helpers `buscarEntero` / `buscarTexto` / `buscarFecha` |

Si encuentras más repeticiones, unifícalas también y anótalas en el resumen.

---

## Tarea 4 — Limpieza de restos

Al terminar, busca y elimina:

- imports sin uso en todos los archivos tocados (en especial `AlertDialog`, `verticalScroll`, `Button`, `KoinPlatform`, `rememberCoroutineScope` y `RelojDelSistema` donde ya no se usen);
- referencias a `Sunass`, `art. 88`, `reclam`, `umbralAtipicoM3`, `MESES_MINIMOS_HISTORIAL`, `0412887`, `PeriodoConsumo(2026, 8)` y `SemillaDepuracion` en el código de producción. Verifícalo con:
  ```
  grep -rniE "sunass|art\. 88|reclam|umbralAtipico|0412887|PeriodoConsumo\(2026, 8\)|SemillaDepuracion" shared/src/commonMain shared/src/androidMain shared/src/iosMain
  ```
  Debe devolver vacío, salvo el texto sobre reclamos ante EPS del asistente, que no choca;
- carpetas o archivos que queden vacíos (deja los `.gitkeep`);
- código comentado o muerto.

---

## Tarea 5 — Actualizar `informacionRecibo.md`

Reescribe el informe de la raíz para que refleje el estado **nuevo**:

- conteo de archivos, líneas y funciones recalculado con comandos, no estimado;
- requerimientos funcionales y no funcionales actualizados: regla única de 100 m³, sincronización con cuenta, borrador persistente, iOS con ingreso manual;
- diagramas Mermaid actualizados (flujo, secuencia, C3 y C4 del módulo), incluyendo `CorreccionViewModel`, `SincronizadorRecibo`, `NubeReciboSupabase`, `ReciboBorradorDao` y el resultado sellado de `ConfirmarReciboUseCase`. C1 y C2: agrega la tabla `recibo` en Supabase y `recibo_borrador` en Room;
- sección "Con cuenta / sin cuenta" reescrita con el nuevo comportamiento;
- sección de observaciones: marca las corregidas, deja la 8 como pendiente y agrega las nuevas que encuentres.

---

## Tarea 6 — Resumen final (obligatorio)

Termina con tres listas:

1. **Archivos modificados**: ruta + qué cambió en una línea. Marca con ⚠️ los que están fuera de `feature/recibo`.
2. **Archivos eliminados**: ruta + por qué.
3. **Archivos creados**: ruta + para qué.

Después agrega:

- el resultado de `assembleDebug` y de `testAndroidHostTest` (cuántas pruebas pasaron);
- los pasos manuales pendientes (aplicar la migración de Supabase y pedir la revisión de Cristhian para los archivos ⚠️);
- cualquier decisión que hayas tenido que tomar y por qué.
