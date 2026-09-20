# Tareas · Reserva domiciliaria

**Spec:** [`../reserva.md`](../reserva.md) · **Plan:** [`plan.md`](plan.md)
**Convención:** cada tarea nombra los criterios CA-xx que cubre. Se escribe
la prueba primero, se ve fallar y luego se implementa. Un PR por grupo,
máximo 400 líneas.

## Semana 9 · Especificación y dominio

### PR 1 · Objetos de valor (hecho)
- [x] T001 `Litros`, `CapacidadLitros`, `Habitantes` con pruebas · CA-01, CA-02, CA-03, CA-08

### PR 2 · Nivel y estimación del consumo
- [x] T002 `NivelReserva`, `TipoLlenado` (`COMPLETO`, `MITAD`) y confirmación de la estimación (`CONFIRMADA`, `NO_CONFIRMADA`) · CA-06, CA-24
- [x] T003 `EventoLlenado` (origen `REAL` o `ASUMIDO`) e `IntervaloConsumo` (`OBSERVADO`, `POR_LLENADO`, duración en horas) · CA-26
- [x] T004 `EstimarConsumo`: mediana de los últimos 5 intervalos · CA-04, CA-15
- [x] T005 Descartar intervalos mayores al doble de la mediana · CA-14
- [x] T006 Fallback: hábitos declarados y valor inicial · CA-16, CA-17
- [x] T007 Litros por habitante y día · CA-05, CA-23
- [x] T007b Los intervalos `OBSERVADO` prevalecen sobre los `POR_LLENADO` · CA-28

### PR 3 · Proyección
- [x] T008 `Reserva.nivelEn(momento)`, lineal y con mínimo 0 · CA-07
- [x] T009 `Reserva.agotamientoProyectado` (vive en la entidad, sin caso de uso aparte) · CA-09
- [x] T010 Llenado asumido al inicio de la ventana del sector, marcado `NO_CONFIRMADA` · CA-13, CA-34
- [x] T010b Confirmar reemplaza al asumido y "no llegó" lo descarta · CA-25, CA-27

### Cierre de la semana
- [ ] T011 Resolver la decisión abierta de la unidad (litros o m³) y corregir el anteproyecto §7
- [ ] T012 Pedir revisión cruzada de la spec (hito H1). Incluir a Dayan: consulta del inicio del último abastecimiento del sector
- [ ] T012b Pedir al Figma las pantallas faltantes: "me quedé sin agua", sector sin cronograma, estimación no confirmada
- [ ] T013 `ReservaRepository` y `FakeReservaRepository` con datos de prueba (hito H1)

## Semana 10 · Déficit y core

- [ ] T014 `CalcularDeficit` con próximo abastecimiento `LocalDateTime?` · CA-10, CA-11, CA-12
- [ ] T015 `DeclararSinAgua`: vacía la reserva y agrega intervalo observado · CA-18, CA-19
- [ ] T016 Tope de cambio del 30 % por declaración · CA-20
- [ ] T017 Core: `AguaTacnaDatabase` con las 4 features y `core/di` con módulos Koin vacíos (aviso previo al equipo)
- [ ] T018 Migración 1 y esquema exportado (Artículo VI)

## Semana 11 · Persistencia

- [ ] T019 `PerfilHogar` en el dominio raíz (avisar a Iker) y `PerfilHogarEntity`
- [ ] T020 `EventoLlenadoEntity` y `ReservaDao` con `Flow` del nivel actual
- [ ] T021 Mappers y `ReservaRepositoryImpl` leyendo solo de Room
- [ ] T022 Tablas en la nube (`perfil_hogar`, `evento_llenado`) con acceso por usuario (hito H3)

## Semana 12 · Interfaz

- [ ] T023 `ReservaUiState` y `ReservaEvent`, un estado inmutable por pantalla
- [ ] T024 `ReservaViewModel` con el flujo del nivel
- [ ] T025 `IndicadorNivelReservorio` (composable máximo 60 líneas)
- [ ] T026 `ReservaScreen` (Figma 03) con hora de agotamiento, déficit, L/h y L/hab·día
- [ ] T026b Pantalla "Registrar llenado" (Figma 04): completo, a la mitad, no llegó
- [ ] T027 Configuración inicial del hogar (Figma 02): tipo (Tanque elevado, Cisterna, Bidones), capacidad, habitantes, hábitos
- [ ] T028 Integrar el déficit y cambiar el fake de cronograma por el real cuando exista

**H4 (semana 13): MVP con perfil del hogar, reserva con proyección y operación sin conexión.**

## Semana 14 · Recomendaciones

- [ ] T029 `Recomendacion` y `SugerirRecortes`, por impacto en litros y según hábitos · CA-21, CA-22, CA-32
- [ ] T029b `SimularRecortes`: ahorro seleccionado, horas ganadas y horas que aún faltan · CA-29, CA-30, CA-31
- [ ] T029c Estado de la proyección (`COMODA`, `AJUSTADA`, `NO_ALCANZA`) con umbral configurable · CA-33
- [ ] T030 Pantalla "Qué recortar" (Figma 05) con casillas y resumen de ahorro

## Semana 15 · Plataforma

- [ ] T031 Tarea periódica horaria de recálculo en `androidMain`
- [ ] T032 Aviso local si la proyección se agota antes del siguiente abastecimiento
- [ ] T033 Notificación de confirmación cuando no se registró el llenado
- [ ] T034 Verificar en dispositivo físico (hito H5)

## Semanas 16 y 17 · Cierre

- [ ] T035 Cobertura de `domain/` de reserva ≥ 70 % (hito H6)
- [ ] T036 Ajustar coeficientes con datos del piloto
- [ ] T037 Refactor y revisión de límites de tamaño

## Recurrente (custodio del core)

- [ ] Revisar todo PR que toque `core/`, `domain/` raíz o el catálogo de versiones
- [ ] Ninguna dependencia de framework en `domain/`
