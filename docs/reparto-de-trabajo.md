# Qué construye cada uno

Derivado del anteproyecto y del *Plan de Trabajo por Integrante*. Los nombres de
archivo son orientativos; lo que **no** es negociable es en qué carpeta vive cada cosa.

Ruta base (se omite en todo el documento):
`shared/src/commonMain/kotlin/pe/edu/upt/aguatacna/`

---

## Roles transversales

Además de su vertical, cada integrante responde por un encargo que atraviesa
todo el proyecto.

| | Integrante | Rol transversal | Pico de carga |
|---|---|---|---|
| **A** | Cristhian | **Custodio del core.** Revisa todo PR que lo toque. Verifica que no entren frameworks a `domain/` y que el catálogo de versiones no se mueva sin acuerdo. | Semanas 9 a 15, sostenido |
| **B** | Dayan | **Gestión de la información base.** Punto de contacto con EPS Tacna y la Sunass. Mantiene sectorización y cronogramas, y lleva el registro de gestiones como evidencia del informe. | Semanas 14 y 15 |
| **C** | Iker | **Líder de pruebas.** Define la estructura de la suite y el criterio de cobertura. No escribe las pruebas ajenas: revisa que existan y que verifiquen comportamiento, no implementación. | Semanas 12 y 16 |
| **D** | Jimmy | **Despliegue y piloto.** Keystore, ofuscación y AAB firmado. Recluta 20–30 hogares, mide los indicadores y consolida los resultados. | Semanas 16 a 18 |

Jimmy lleva dos funcionalidades medianas en vez de una grande, lo que lo hace el
comodín del equipo: ninguna de sus entregas bloquea a nadie, así que es quien se
reasigna si alguien se atrasa.

---

## Mapa de carpetas: quién toca qué

Git no genera conflictos por carpeta, sino **por archivo**. Dos personas pueden
trabajar en la misma carpeta sobre archivos distintos sin ningún problema.
Por eso lo único realmente peligroso es la lista de archivos compartidos
del final.

```
aguatacna/
│
├── docs/                              lectura para todos · escribe Cristhian
├── .github/CODEOWNERS                 Cristhian
│
├── gradle/
│   ├── libs.versions.toml             ⚠️ COMPARTIDO
│   └── wrapper/                       nadie lo toca
├── build.gradle.kts                   Cristhian
├── settings.gradle.kts                Cristhian
├── gradle.properties                  Cristhian
│
├── androidApp/
│   ├── build.gradle.kts               Cristhian
│   └── src/main/
│       ├── AndroidManifest.xml        ⚠️ COMPARTIDO  (permisos)
│       ├── kotlin/…/MainActivity.kt   Cristhian
│       └── res/                       Cristhian
│
├── iosApp/
│   ├── iosApp/Info.plist              ⚠️ COMPARTIDO  (permisos iOS)
│   └── resto                          solo quien tenga la Mac
│
└── shared/
    ├── build.gradle.kts               ⚠️ COMPARTIDO  (dependencias)
    └── src/
        ├── commonMain/kotlin/pe/edu/upt/aguatacna/
        │   ├── core/db/               Cristhian   ⚠️ AguaTacnaDatabase.kt
        │   ├── core/di/               Cristhian   ⚠️ AppModule.kt
        │   ├── core/navigation/       Cristhian   ⚠️ NavHost.kt
        │   ├── core/network/          Cristhian
        │   ├── core/ui/               Cristhian
        │   ├── core/util/             Cristhian
        │   ├── domain/                Cristhian   (solo lo transversal)
        │   ├── data/                  Cristhian   (solo lo transversal)
        │   └── feature/
        │       ├── reserva/   ←──────  CRISTHIAN   exclusivo
        │       ├── sector/    ←──────  DAYAN       exclusivo
        │       ├── recibo/    ←──────  IKER        exclusivo
        │       └── retos/     ←──────  JIMMY       exclusivo
        │
        ├── androidMain/…/feature/<la tuya>/       mismo dueño
        ├── iosMain/…/feature/<la tuya>/           mismo dueño
        ├── commonTest/…/feature/<la tuya>/        mismo dueño
        └── androidHostTest/…/feature/<la tuya>/   mismo dueño
```

**Cada integrante tiene cuatro carpetas con su nombre**, una en cada source set,
y ya están creadas en el repositorio:

```
shared/src/commonMain/kotlin/pe/edu/upt/aguatacna/feature/<la tuya>/
shared/src/androidMain/kotlin/pe/edu/upt/aguatacna/feature/<la tuya>/
shared/src/iosMain/kotlin/pe/edu/upt/aguatacna/feature/<la tuya>/
shared/src/commonTest/kotlin/pe/edu/upt/aguatacna/feature/<la tuya>/
```

Dentro de esas cuatro carpetas **haces lo que quieras sin avisar a nadie**.
Fuera de ellas, no creas ni editas archivos.

---

## Zona compartida — custodio: Cristhian

Nadie más crea archivos aquí. Se construye en la semana 8, antes de que
los otros tres empiecen.

```
core/db/          AguaTacnaDatabase.kt · DatabaseBuilderFactory.kt (expect)
core/network/     HttpClientFactory.kt · ApiResult.kt
core/di/          AppModule.kt · CoreModule.kt · PlatformModule.kt (expect)
core/ui/          theme/ (Color, Type, Theme) · components/ (Boton, Tarjeta, Carga)
core/navigation/  NavHost.kt · Rutas.kt
core/util/        capturaFoto.kt (expect) ← la usan Iker y Jimmy, por eso vive aquí

domain/model/     Usuario.kt · PerfilHogar.kt      ← solo lo transversal
data/local/       UsuarioEntity.kt · UsuarioDao.kt
```

---

## A · Cristhian — `feature/reserva/`

El núcleo del producto: cuánta agua queda y hasta cuándo alcanza.
**Tablas propias:** `perfil_hogar`, `evento_llenado`.

```
domain/model/       NivelReserva · EventoLlenado · TipoReservorio
                    CapacidadLitros · ProyeccionAgotamiento · Deficit
domain/repository/  ReservaRepository
domain/usecase/     RegistrarLlenado · CalcularNivelActual
                    ProyectarAgotamiento · CalcularDeficit · SugerirRecortes
data/local/         PerfilHogarEntity · EventoLlenadoEntity · ReservaDao
data/               ReservaRepositoryImpl · FakeReservaRepository
presentation/       ReservaUiState · ReservaEvent · ReservaViewModel
                    ReservaScreen · IndicadorNivelReservorio
```

**Plataforma:** WorkManager en `androidMain/.../feature/reserva/`
(recálculo horario de la proyección y alerta de agotamiento).

Aquí viven las fórmulas del anteproyecto §7. Son las que más peso tienen
en las pruebas unitarias de la semana 16.

---

## B · Dayan — `feature/sector/`

Horarios de abastecimiento, mapa y cisternas.
**Tablas propias:** `sector`, `cronograma`, `punto_cisterna`, `confirmacion_horario`.

```
domain/model/       Sector · SectorId · Cronograma · VentanaAbastecimiento
                    PuntoCisterna · Coordenada · Distrito
                    ConfirmacionHorario
domain/repository/  SectorRepository
domain/usecase/     ObtenerCronogramaVigente · ProximoAbastecimiento
                    BuscarCisternasCercanas · AsociarDomicilioASector
                    ConfirmarLlegadaDelAgua · ConsolidarCronogramaColaborativo
data/local/         SectorEntity · CronogramaEntity · PuntoCisternaEntity
                    ConfirmacionHorarioEntity · SectorDao
data/remote/        SectorDto · CronogramaDto · SectorApi
data/               SectorRepositoryImpl · FakeSectorRepository
presentation/       SectorUiState · SectorEvent · SectorViewModel
                    SectorScreen · MapaCisternas · TarjetaHorario
```

**El modelo colaborativo es la fuente principal, no un parche.** Los propios
usuarios confirman la hora de llegada y de corte del agua, y el sistema promedia
por sector. Los datos oficiales de la EPS lo complementan cuando estén
disponibles, no al revés.

**Dos tareas con máxima prioridad, antes que cualquier código:**

1. Preparar y presentar la solicitud formal de información a EPS Tacna
   (sectorización, cronogramas vigentes, puntos de reparto). Es la tarea con
   mayor plazo externo de todo el proyecto y no depende de nadie.
2. Publicar la interfaz de cronograma con su fake. Cristhian está bloqueado
   sin ella.

**Plataforma:** ubicación en tiempo de ejecución y FCM (alertas de corte),
más el mapa: Google Maps en `androidMain`, MapKit en `iosMain`, unidos
con `expect/actual`.

`ProximoAbastecimiento` es la pieza de la que depende Cristhian para
calcular el déficit. **Publícala temprano**, aunque sea contra el fake.

---

## C · Iker — `feature/recibo/`

Digitalización de la boleta de EPS Tacna y asistente hídrico.
**Tablas propias:** `recibo`, `consulta_asistente`.

```
domain/model/       Recibo · PeriodoFacturacion · LecturaMedidor
                    ConsumoM3 · ConsumoAtipico
domain/repository/  ReciboRepository
domain/usecase/     DigitalizarRecibo · ConfirmarCamposExtraidos
                    CalcularLineaBase · DetectarConsumoAtipico
data/local/         ReciboEntity · ReciboDao
data/remote/        OcrApi · ReciboOcrDto · AsistenteApi
data/               ReciboRepositoryImpl · FakeReciboRepository
presentation/       ReciboUiState · ReciboEvent · ReciboViewModel
                    ReciboScreen · ConfirmarCamposScreen · AsistenteScreen
```

**Plataforma:** cámara. Usa `core/util/capturaFoto.kt`, no crees otra.

Junta unas 10 boletas reales de EPS Tacna la primera semana. Sin muestras
reales vas a diseñar el parser contra un recibo imaginario.

---

## D · Jimmy — `feature/retos/`

Gamificación y reportes ciudadanos.
**Tablas propias:** `reto`, `reto_usuario`, `reporte`.

```
domain/model/       Reto · RetoUsuario · Racha · Insignia
                    Reporte · TipoReporte · LitrosPorHabitanteDia
domain/repository/  RetosRepository · ReporteRepository
domain/usecase/     ObtenerRetosSemana · MarcarRetoCumplido
                    CalcularRacha · CompararConSector · CrearReporte
data/local/         RetoEntity · RetoUsuarioEntity · ReporteEntity · RetosDao
data/remote/        RankingApi · ReporteApi
data/               RetosRepositoryImpl · FakeRetosRepository
presentation/       RetosUiState · RetosEvent · RetosViewModel
                    RetosScreen · ReportesScreen · MapaReportes
```

**Plataforma:** cámara y galería. Usa `core/util/capturaFoto.kt`, no crees otra.

---

## Tu zona incluye también las carpetas de plataforma

No es solo `commonMain`. Cuando necesites una capacidad del teléfono:

```
shared/src/androidMain/kotlin/pe/edu/upt/aguatacna/feature/<la tuya>/
shared/src/iosMain/kotlin/pe/edu/upt/aguatacna/feature/<la tuya>/
```

El `expect` va en tu carpeta de `commonMain`; los `actual` en las de plataforma.
Nunca resuelvas diferencias de plataforma con condicionales dentro de
`commonMain`.

---

## Los archivos que TODOS necesitamos tocar

Estos no tienen dueño exclusivo y son la fuente real de conflictos.
**Se llenan una sola vez, el día 1, entre los cuatro, en un solo PR**,
declarando las 4 features aunque estén vacías.

| Archivo | Qué se registra ahí |
|---|---|
| `core/db/AguaTacnaDatabase.kt` | las entidades Room de las 4 features |
| `core/di/AppModule.kt` | los 4 módulos de Koin |
| `core/navigation/NavHost.kt` | las 4 rutas de navegación |
| `gradle/libs.versions.toml` | todas las dependencias |
| `androidApp/src/main/AndroidManifest.xml` | permisos: cámara, ubicación, notificaciones |
| `iosApp/iosApp/Info.plist` | descripciones de uso de cámara y ubicación |

Después de ese día casi no se vuelven a abrir. Si alguno necesita un cambio
posterior, avisa al grupo antes de tocarlo.

---

## Quién desbloquea a quién

```
Cristhian ──core listo──────────> los otros tres   ⚠ pendiente
Dayan ──contrato de cronograma──> Cristhian        cálculo de déficit · BLOQUEANTE
Iker ──línea base del recibo────> Cristhian        afina la proyección · no bloqueante
Cristhian ──litros por hab/día──> Jimmy            ranking por sector
Dayan ──identificador de sector─> Jimmy            posicionamiento anónimo
```

Salvo el core, **ninguna de estas dependencias justifica quedarse parado**:
se define la interfaz, se escribe un fake y se sigue.

---

## Hitos de control

| | Fecha | Criterio de aceptación |
|---|---|---|
| **H1** | fin semana 9 | Las 4 especificaciones redactadas y revisadas de forma cruzada. Todos los fakes disponibles: nadie bloqueado por otro. |
| **H2** | fin semana 9 | Decidido y documentado el servicio de datos en la nube y el proveedor de autenticación. Aprobación del docente para sustituir Retrofit y Hilt. |
| **H3** | fin semana 10 | Decidido el alcance de iOS. Todas las tablas propias creadas con sus políticas de acceso. |
| **H4** | semana 13 | MVP operativo: perfil del hogar, reserva con proyección, sector con cronograma, retos y operación sin conexión. |
| **H5** | fin semana 15 | Permisos, tareas en segundo plano y notificaciones funcionando en dispositivo físico. |
| **H6** | fin semana 16 | Cobertura de la capa de dominio ≥ 70 %. |
| **H7** | fin semana 17 | Compilado firmado instalado y verificado en dispositivo físico. |
| **H8** | semana 18 | Indicadores del piloto medidos y sustentación preparada, con participación de los cuatro. |

---

## Decisiones abiertas

Sin resolver al 2026-09-12. Se cierran en la próxima reunión de equipo.

| Decisión | Por qué importa |
|---|---|
| **El core no está construido todavía.** El *Plan de Trabajo* asume que sí, y el calendario arranca en la semana 9 sobre esa base. En `origin/main` no hay dependencias de Room, Ktor ni Koin, y `core/db/`, `core/di/` y `core/navigation/` están vacías. | Si no se entrega esta semana, todo el calendario corre. Mitigación inmediata: agregar `kotlinx-datetime` y `kotlinx-coroutines` al catálogo para que los cuatro escriban dominio y pruebas en paralelo. |
| **El "panel administrativo"** aparece en la vertical de sector (carga inicial de sectorización y publicación de cronogramas), pero no está en el anteproyecto, no existe en el repositorio y no tiene dueño. | Es un cliente web adicional. O se asigna, o sale del alcance. |
| **Quién toca `androidApp/` para la firma.** Jimmy lidera el despliegue, pero la configuración de firma vive en `androidApp/build.gradle.kts`, que el `CODEOWNERS` asigna a Cristhian. | Hay que resolverlo antes de la semana 17, no durante. |
