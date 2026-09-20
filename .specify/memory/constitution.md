# Constitución de AguaTacna

La constitución vigente del proyecto vive en [`docs/constitution.md`](../../docs/constitution.md)
y es la única fuente. **Léela completa antes de continuar.** Este archivo solo
existe para que Spec Kit la encuentre; no se edita ni se regenera aquí.

Reglas que más pesan al especificar y planificar:

- Dominio puro: sin Room, Ktor, Koin ni Android (Art. I).
- Sin conexión como base (Art. II).
- Identidad del usuario: UUID local inmutable (Art. III).
- Límites: archivo 150 líneas, función 40, PR 400 (Art. IV).
- Todo criterio de aceptación de la spec se convierte en prueba unitaria (Art. V).
- El contrato existe antes que el código (Art. VII).

Las especificaciones se guardan en `docs/specs/<vertical>.md`, con la misma
estructura que `docs/specs/sector.md`.
