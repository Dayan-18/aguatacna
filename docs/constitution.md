# Constitución del proyecto YakuTacna

**Versión:** 1.0.0
**Ratificada:** 2026-09-08
**Última enmienda:** 2026-09-08

Este documento define las reglas no negociables del proyecto. Toda especificación, plan técnico y tarea debe cumplirlas. Ante conflicto entre este documento y cualquier otro, prevalece este.

---

## I. El dominio es puro e independiente

La capa `domain/` no importa nada de Room, Ktor, Koin, Android, iOS ni ningún framework. Solo Kotlin estándar, corrutinas y kotlinx-datetime.

Toda la lógica de negocio —cálculo de nivel de reserva, proyección de agotamiento, cálculo de déficit, detección de consumo atípico— vive en `domain/` y es ejecutable en un test de JVM sin emulador, sin base de datos y sin red.

**Razón:** es lo que permite testear el núcleo del producto en milisegundos y lo que hace que la app sea portable entre plataformas.

**Verificación:** si el IDE sugiere un import de `androidx.*`, `io.ktor.*` u `org.koin.*` dentro de `domain/`, el diseño está mal. Se rechaza el PR.

---

## II. Sin conexión como base

La base de datos local es la única fuente de verdad. La red es un mecanismo de sincronización, nunca un requisito de funcionamiento.

Todas las funciones esenciales —perfil del hogar, registro de eventos de llenado, cálculo y proyección de reserva, consulta del cronograma del sector— operan sin conexión.

Ninguna pantalla muestra un spinner indefinido esperando la red. Si no hay conexión, se muestran los datos locales con un indicador de última sincronización.

**Razón:** los sectores periféricos de Tacna, que son los más afectados por el racionamiento, tienen la peor cobertura de datos.

---

## III. La identidad del usuario es un UUID local e inmutable

Al instalar la aplicación se genera un UUID que identifica al usuario para siempre. Todas las tablas apuntan a ese identificador.

Nunca se usa como clave el `sub` de Google ni el identificador del proveedor de autenticación. Esos valores se almacenan como campos adicionales del usuario, nunca como llave.

```kotlin
@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey val id: String,          // UUID local, inmutable
    val googleSub: String? = null,       // se llena al autenticar
    val authProviderId: String? = null,  // uuid del proveedor, si existe
    val sectorId: String? = null
)
```

La aplicación funciona en modo invitado. La autenticación es opcional y solo se solicita para funciones que la requieren: ranking por sector, reportes ciudadanos y sincronización entre dispositivos.

**Razón:** permite decidir el proveedor de autenticación y el backend más adelante sin migrar datos.

---

## IV. Límites de tamaño

| Unidad | Límite | Acción al excederlo |
|---|---|---|
| Archivo de código | 150 líneas | Se divide antes de integrar |
| Función | 40 líneas | Se extrae en funciones con nombre |
| Composable | 60 líneas | Se extrae en composables hijos |
| Parámetros de función | 5 | Se agrupa en un objeto de datos |
| Pull request | 400 líneas modificadas | Se parte en varios PR |

No cuentan para el límite de archivo: imports, líneas en blanco, y archivos autogenerados.

Excepciones permitidas y que deben justificarse en el PR: archivos de configuración de Gradle, definiciones de tema del design system, y clases de datos que reflejan un esquema externo.

**Razón:** un archivo de 150 líneas se revisa completo en una pantalla. Es lo que hace posible que cuatro personas revisen el código de las demás sin que la revisión se vuelva un trámite.

**Verificación:** el revisor rechaza el PR indicando el archivo y su tamaño. No es negociable en la revisión; si el autor cree que la excepción aplica, la justifica en la descripción del PR antes de solicitar revisión.

---

## V. La lógica de dominio se prueba

Todo caso de uso, servicio de dominio y función de cálculo tiene prueba unitaria antes de integrarse a `main`.

Cobertura mínima de la capa `domain/`: 70 %.

Los criterios de aceptación escritos en la especificación de la funcionalidad se traducen directamente en pruebas. No se escriben pruebas nuevas desde cero en la semana 16: se extraen de las especificaciones ya aprobadas.

La UI y las capas de datos no requieren cobertura obligatoria.

---

## VI. El esquema de base de datos se versiona

`exportSchema = true` siempre. Los archivos JSON de `schemas/` se commitean al repositorio.

Toda modificación del esquema incrementa la versión y aporta su migración explícita.

`fallbackToDestructiveMigration` está prohibido fuera del entorno local de desarrollo.

**Razón:** durante el piloto con hogares reales, perder los datos de un usuario por una migración descuidada invalida la medición de indicadores.

---

## VII. El contrato existe antes que el código

Ninguna funcionalidad se implementa sin su especificación aprobada por al menos otro integrante.

Cuando una funcionalidad depende de otra, se define primero la interfaz en `domain/` y se implementa un fake en `data/`. Quien depende trabaja contra el fake; no espera al código real.

Las especificaciones son de comportamiento observable, no de configuración. Si al escribir una especificación se está describiendo cómo configurar una herramienta, esa tarea no corresponde al flujo de especificaciones y va como issue con lista de verificación.

---

## VIII. Propiedad, revisión e integración

Cada integrante es dueño de una carpeta bajo `feature/` y de su conjunto de tablas en el servicio de datos. Ser dueño significa responder por que llegue completa, no que nadie más pueda tocarla.

`core/` no tiene dueño exclusivo: tiene custodio. Todo PR que modifique `core/` requiere revisión del custodio.

Ningún PR se integra sin la revisión de un integrante distinto al autor. Nadie integra su propio PR.

`main` está protegida. Una rama por funcionalidad, nombrada con el identificador de su especificación.

**Modificar `gradle/libs.versions.toml` requiere aviso previo al equipo completo.** Una subida de versión no coordinada rompe el build de los cuatro.

---

## IX. Privacidad por diseño

En el servidor se almacena la ubicación del usuario a nivel de sector, nunca la coordenada exacta del domicilio.

La comparación de consumo entre hogares es anónima. Ninguna vista expone identidad de otros usuarios.

Cada usuario accede únicamente a sus propios registros, garantizado por políticas de seguridad a nivel de fila.

No se registran eventos con información personal identificable. No se embeben credenciales ni claves en el código fuente.

El usuario puede eliminar su cuenta y todos sus datos desde la propia aplicación.

**Razón:** Ley N.º 29733 de Protección de Datos Personales, y porque los datos de consumo de agua revelan patrones de presencia en el domicilio.

---

## X. Las decisiones aplazadas llevan fecha

Toda decisión que se posterga se registra con responsable y fecha límite. Una decisión aplazada sin fecha es una decisión olvidada.

| Decisión pendiente | Responsable | Fecha límite |
|---|---|---|
| Backend: Supabase o servidor propio | Equipo | Cierre semana 9 |
| Proveedor de autenticación con Google | Equipo | Cierre semana 9 |
| Target iOS con paridad parcial, o descartarlo | Equipo | Cierre semana 10 |
| Aprobación del docente para sustituir Retrofit y Hilt | Equipo | Semana 8 |

---

## Restricciones tecnológicas

- Kotlin Multiplatform con Compose Multiplatform. Android es la plataforma principal, versión mínima 8.0.
- Arquitectura limpia en tres capas dentro del módulo compartido. MVVM en presentación con un único estado inmutable por pantalla. Modelado táctico de dominio con value objects y servicios donde la regla lo justifique.
- Persistencia con Room, definición de esquema por código, migraciones explícitas.
- Cliente HTTP con Ktor. Serialización con kotlinx.serialization.
- Inyección de dependencias con Koin.
- Las capacidades específicas de plataforma se resuelven con `expect/actual`, nunca con condicionales por plataforma dentro de código común.
- Toda dependencia nueva se verifica en klibs.io antes de comprometerla en una especificación.

---

## Alcance y recorte

El producto mínimo viable evaluable comprende: perfil del hogar, gestión de la reserva, asociación a sector, consulta de cronograma, recomendaciones de ahorro y operación sin conexión.

Si es necesario reducir alcance, el orden de recorte es fijo y no se discute en el momento:

1. Reporte ciudadano de incidencias
2. Asistente hídrico
3. Comparación anónima por sector
4. Digitalización del recibo

**Razón:** decidir el orden de recorte cuando ya hay presión de tiempo convierte una decisión técnica en una discusión emocional.

---

## No se permite

- Abstraer "por si acaso". Una interfaz por repositorio es suficiente. No se construyen capas genéricas de proveedores intercambiables.
- Lógica de negocio dentro de composables o de ViewModels. El ViewModel orquesta, no calcula.
- Estado mutable compartido fuera del ViewModel.
- Comentarios que explican qué hace el código. Si hace falta, el nombre está mal. Se comenta solo el porqué de una decisión no obvia.
- Código muerto o comentado en `main`.

---

## Gobernanza

Esta constitución se enmienda por acuerdo de los cuatro integrantes, documentando el cambio y su motivo, e incrementando la versión.

Versionado: MAYOR para eliminación o redefinición de un principio, MENOR para adición de un principio o sección, PARCHE para precisiones que no cambian el fondo.

Al cierre de cada sprint, quince minutos de revisión: qué se comprometió, qué se entregó, qué se arrastra. Si un integrante arrastra más del 30 % dos sprints seguidos, se reasigna trabajo. Es aritmética, no un juicio.

El cumplimiento de esta constitución se verifica en cada revisión de PR. El revisor que aprueba un PR que la incumple es corresponsable.
