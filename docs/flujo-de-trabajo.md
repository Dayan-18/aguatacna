# Cómo trabajamos sin pisarnos

Complementa `constitution.md`, que tiene las reglas de fondo.
Esto es la mecánica del día a día.

---

## 1. Zonas de propiedad

**Regla de oro: no edites archivos fuera de tu zona.** Si necesitas un cambio
en la zona de otro, se lo pides; no lo haces tú. Esta sola regla elimina
la mayoría de los conflictos.

| | Integrante | Zona | Ruta bajo `shared/src/commonMain/kotlin/pe/edu/upt/aguatacna/` |
|---|---|---|---|
| **A** | Cristhian Carlos Mamani Cori | Core + Reserva | `core/**` y `feature/reserva/**` |
| **B** | Dayan Elvis Jahuira Pilco | Sector y mapas | `feature/sector/**` |
| **C** | Iker Alberto Sierra Ruiz | Recibo e IA | `feature/recibo/**` |
| **D** | Jimmy Llica Mamani | Retos y reportes | `feature/retos/**` |

Dentro de tu zona tienes tus tres capas y haces lo que quieras:

```
feature/<tuya>/
├── domain/         modelos, interfaces de repositorio, casos de uso
├── data/           entidades Room, DAOs, DTOs, mappers, repositorio real
└── presentation/   UiState, Event, ViewModel, Screen
```

### Zona compartida

`core/**`, los archivos de Gradle y `domain/` + `data/` de la raíz son de
**Cristhian**, que es el custodio. Cualquiera puede proponer cambios ahí,
pero él los revisa.
GitHub lo asigna solo por el `CODEOWNERS`.

En `domain/` y `data/` de la raíz va **únicamente lo transversal**: `Usuario`,
`PerfilHogar`, el cliente HTTP base. Todo lo demás va dentro de tu feature.
Si los cuatro empezamos a meter entidades ahí, chocamos todos los días.

---

## 2. Los cuatro archivos que causan el 90% de los conflictos

Son "registros": archivos que todos necesitan modificar para enchufar su feature.

| Archivo | Qué se registra |
|---|---|
| `AguaTacnaDatabase.kt` | las entidades Room de las 4 features |
| `AppModule.kt` (Koin) | los 4 módulos de inyección |
| `NavHost` | las 4 rutas de navegación |
| `gradle/libs.versions.toml` | todas las dependencias |

**Se llenan UNA sola vez, el día 1, entre los cuatro, en un solo PR.**
Se declaran las 4 features aunque estén vacías: entidades con un solo campo,
módulos Koin vacíos, pantallas que solo muestran su nombre.

Después de ese día casi no se vuelven a abrir, y cada quien trabaja en su carpeta.

**`libs.versions.toml` es el más peligroso.** Una subida de versión no coordinada
rompe el build de los cuatro. Solo A lo toca, y avisando al grupo antes.

---

## 3. No esperes a nadie

Si tu feature necesita algo de otro, **no te bloquees**: define la interfaz en
tu `domain/` y escribe un fake en tu `data/` que devuelva datos de mentira.
Trabajas contra el fake y lo cambias cuando llegue el real.

```kotlin
// feature/sector/domain/repository/SectorRepository.kt
interface SectorRepository {
    suspend fun obtenerCronograma(sectorId: String): Result<List<Cronograma>>
}

// feature/sector/data/FakeSectorRepository.kt
class FakeSectorRepository : SectorRepository {
    override suspend fun obtenerCronograma(sectorId: String) =
        Result.success(listOf(Cronograma("S1", LocalTime(5, 0), LocalTime(11, 0))))
}
```

Nadie debería decir "no avancé porque estoy esperando a X".

---

## 4. Flujo de git

**Al empezar a trabajar, siempre:**

```bash
git checkout main
git pull
git checkout -b feat/sector-cronograma-vigente
```

Ramas: `feat/<zona>-<qué>`. Ej: `feat/recibo-ocr-boleta`, `feat/retos-racha`.

**Al terminar, antes de abrir el PR:**

```bash
git pull --rebase origin main
```

Si el rebase da conflicto, es tuyo y lo resuelves tú, no el revisor.

**Reglas de PR:**

- Máximo 400 líneas modificadas. Si es más, pártelo.
- Requiere 1 aprobación de alguien distinto al autor.
- **Nadie integra su propio PR.**
- Si el PR toca `core/`, lo aprueba A.

**Sube tu rama al menos una vez al día**, aunque esté a medias. Una rama que
vive una semana en tu máquina es un conflicto garantizado. Y si tu laptop
muere, se pierde la semana.

### Por qué no usamos GitFlow

Nuestro flujo es **GitHub Flow**: `main` protegida, una rama por funcionalidad,
PR con revisión. Nada más.

GitFlow (`develop`, `release/*`, `hotfix/*`) existe para mantener varias
versiones en producción a la vez. Nosotros tenemos una sola versión viva y dos
entregas. `develop` solo agregaría un merge extra por feature y un segundo lugar
donde resolver los mismos conflictos.

Lo único que tomamos de GitFlow son los **tags de versión**, que marcan el commit
exacto que se presentó:

```bash
git tag -a v0.1.0 -m "Entrega Unidad II - semana 13"
git push origin v0.1.0
```

- `v0.1.0` → entrega de la Unidad II (semana 13)
- `v1.0.0` → sustentación final (semana 18)

---

## 5. Antes de pedir revisión, verifica

```bash
./gradlew :androidApp:assembleDebug
./gradlew :shared:testAndroidHostTest
```

Si no compila o los tests fallan, no lo mandes a revisión.

---

## 6. Qué NO se sube nunca

`local.properties`, `*.jks`, `*.keystore`, `google-services.json`, `.env`,
ni ninguna clave de API dentro del código.

**El repositorio es público.** Una clave subida queda en el historial de git
aunque la borres después. Si se te escapa una, avisa al grupo de inmediato:
hay que rotarla, no basta con borrar el archivo.

---

## 7. Si igual sale un conflicto

Pasa. No lo resuelvas a la fuerza ni borres el trabajo del otro.

1. Mira qué archivo es.
2. Si está fuera de tu zona → habla con su dueño antes de tocar nada.
3. Si es uno de los cuatro registros del punto 2 → avisa al grupo, porque
   probablemente alguien más lo está editando en este momento.

---

## 8. Cierre de sprint

Quince minutos cada dos semanas: qué se comprometió, qué se entregó, qué se
arrastra. Si alguien arrastra más del 30% dos sprints seguidos, se reasigna
trabajo. Es aritmética, no un reproche.
