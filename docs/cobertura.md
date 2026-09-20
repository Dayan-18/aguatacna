# Cobertura de pruebas

Meta del equipo: **70 %** de la capa de dominio (constitución, artículo V; hito H6).

## Medición del 2026-09-21

| Paquete | Líneas | Ramas |
|---|---|---|
| `feature/reserva/domain` | 258 / 267 = **96,6 %** | 147 / 172 = 85,5 % |
| — `model` | 113 / 120 = 94,2 % | 56 / 72 = 77,8 % |
| — `usecase` | 145 / 147 = 98,6 % | 91 / 100 = 91,0 % |
| `feature/sector/domain` | 92 / 92 = 100 % | 37 / 42 = 88,1 % |

Lo poco que queda sin cubrir en reserva son validaciones de constructores de objetos de valor
(`ConsumoHorario`, `Habitantes`, `LitrosPorHabitanteDia`) y una rama de `HistorialReserva`.

## Cómo medirla

Las pruebas de host ya generan los datos de cobertura (`enableCoverage = true` en
`shared/build.gradle.kts`). Falta convertirlos en un informe con la herramienta de línea de
comandos de JaCoCo, que no forma parte del proyecto:

```bash
./gradlew :shared:testAndroidHostTest

curl -L -o jacococli.jar \
  https://repo1.maven.org/maven2/org/jacoco/org.jacoco.cli/0.8.12/org.jacoco.cli-0.8.12-nodeps.jar

java -jar jacococli.jar report \
  shared/build/outputs/unit_test_code_coverage/androidHostTest/testAndroidHostTest.exec \
  --classfiles shared/build/classes/kotlin/android/main \
  --sourcefiles shared/src/commonMain/kotlin \
  --html shared/build/reports/cobertura
```

Con `--csv archivo.csv` en lugar de `--html` se obtiene una tabla por clase, de la que se suman las
líneas de cada paquete `domain`. Abrir `shared/build/reports/cobertura/index.html` muestra las
líneas sin cubrir.

## Qué no se mide

La interfaz y las capas de datos no requieren cobertura obligatoria (constitución, artículo V).
Los `Worker`, las notificaciones y el arranque de Android solo se pueden comprobar en un
dispositivo (tarea T034).
