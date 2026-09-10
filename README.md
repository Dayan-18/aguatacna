# AguaTacna

Aplicación móvil multiplataforma (Android / iOS) para la gestión de la reserva
domiciliaria de agua potable y la anticipación de cortes durante el racionamiento
en la zona urbana de Tacna.

Proyecto de la asignatura **SI-883 — Soluciones Móviles I**
Escuela Profesional de Ingeniería de Sistemas · Universidad Privada de Tacna · 2026-II

---

## Arrancar

1. **JDK 17 o 21.** En Android Studio: `Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK`.
2. Abrir la carpeta raíz del proyecto y esperar el *Gradle sync* completo.
3. Compilar y ejecutar:

```
./gradlew :androidApp:assembleDebug
```

- **Android:** usar la configuración de ejecución `androidApp` en el widget de Run.
- **iOS:** abrir `iosApp/` en Xcode (requiere macOS).

## Pruebas

```
./gradlew :shared:testAndroidHostTest       # lógica de dominio en JVM
./gradlew :shared:iosSimulatorArm64Test     # solo en macOS
```

## Estructura

Todo el código compartido vive en `shared/src/commonMain/kotlin/pe/edu/upt/aguatacna/`.

```
core/       infraestructura compartida: base de datos, cliente HTTP,
            inyección de dependencias, design system, navegación.
            Custodio: @USUARIO-A — todo PR que la toque requiere su revisión.

domain/     SOLO lo transversal (Usuario, PerfilHogar). Sin frameworks.
data/       SOLO implementaciones transversales.

feature/    cada integrante vive aquí dentro y no sale.
├── reserva/    → @USUARIO-A    nivel, proyección de agotamiento, déficit
├── sector/     → @Dayan-18     mapas, cronogramas, puntos de cisterna
├── recibo/     → @USUARIO-C    OCR de boletas, consumo atípico
└── retos/      → @USUARIO-D    gamificación, rachas, reportes
```

Cada `feature/` contiene sus tres capas: `domain/`, `data/`, `presentation/`.
La presentación sigue **MVVM** con un único estado inmutable por pantalla.

## Reglas

Las reglas de fondo están en **[`docs/constitution.md`](docs/constitution.md)**
y la mecánica diaria —quién toca qué, flujo de ramas y PRs— en
**[`docs/flujo-de-trabajo.md`](docs/flujo-de-trabajo.md)**.
Se verifican en cada revisión de PR. Las cuatro que más se incumplen:

1. `domain/` no importa Room, Ktor, Koin, Android ni iOS. Solo Kotlin.
2. Ningún archivo pasa de 150 líneas; ningún PR pasa de 400 líneas.
3. Todo caso de uso tiene prueba unitaria antes de integrarse a `main`.
4. `gradle/libs.versions.toml` no se modifica sin avisar al equipo completo.

`main` está protegida. Una rama por funcionalidad. Nadie integra su propio PR.

## Dudas

Sobre `core/`, Gradle o el arranque → @USUARIO-A.
Sobre una feature → su dueño en la tabla de arriba.
