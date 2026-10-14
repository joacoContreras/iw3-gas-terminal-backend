# CLAUDE.md — Contexto del proyecto

> Leé este archivo completo antes de tocar nada. Es la memoria compartida del equipo: dice **dónde
> estamos, qué se hizo, qué falta y cómo trabajar**. Actualizalo al cerrar cada fase (sección 2).

## 1. Qué es este proyecto

- **Materia:** Ingeniería Web III, 2026. **Trabajo práctico integrador**, en grupo de hasta 3 personas.
- **Qué se construye:** un sistema (backend, base de datos, API documentada y, para el final, frontend y
  despliegue) que administra **órdenes de carga de gas líquido**. Recibe las órdenes de SAP, registra la
  tara y el pesaje final de la balanza (TMS), recibe en tiempo real los datos del caudalímetro del sistema
  de carga y calcula la conciliación.
- **Repositorio:** `iw3-gas-terminal-backend` (GitHub: `joacoContreras/iw3-gas-terminal-backend`).
- **Stack:** Java 21, Spring Boot 4.1.0, Maven Wrapper, Spring Data JPA/Hibernate, MySQL 8.0 en Docker,
  Spring Security con JWT (java-jwt), Lombok, springdoc-openapi 3.1.1.
- **Paquete raíz:** `ar.edu.iua.iw3`. **Idioma:** documentación y comentarios en español;
  identificadores (clases, métodos, campos y JSON) en inglés.
- **Primera entrega:** 10/11/2026 (anteúltima clase); incluye los requisitos del segundo parcial. Examen final: después.

## 2. Dónde estamos hoy

**Última actualización:** 14/10/2026.

| | |
|---|---|
| **Fase en curso** | **Fase 1 — Recepción y creación de órdenes (Punto 1): implementada, probada y en revisión del equipo** |
| **Qué sigue** | **Fase 2 — Pesaje inicial y activación (Punto 2): NO iniciada.** Espera la revisión de la Fase 1 |
| Dato pendiente para la Fase 2 | Código HTTP de "estado incorrecto" (recomendado: 409 Conflict) |
| Rama de la Fase 1 | `fase-1-recepcion-ordenes`: publicada en GitHub, **sin integrar a `main`** |
| `main` | Llega hasta la Fase 0 (merge `c3b62de`) |

| Fase | Contenido | Rama | Estado |
|---|---|---|---|
| 0 | Auditoría y línea base | `fase-0-auditoria` | Completada e integrada en `main` |
| 1 | Recepción de órdenes (Punto 1) | `fase-1-recepcion-ordenes` | Completada, **en revisión**, sin integrar |
| 2 | Pesaje inicial y activación (Punto 2) | `fase-2-pesaje-inicial` | Pendiente |
| 3 | Carga y datos en tiempo real (Punto 3) | `fase-3-carga-tiempo-real` | Pendiente |
| 4 | Cierre de la orden (Punto 4) | `fase-4-cierre-orden` | Pendiente |
| 5 | Pesaje final, conciliación y circuito completo (Punto 5) | `fase-5-conciliacion` | Pendiente |

**No avanzar de fase sin autorización explícita del usuario.**

## 3. Reglas de trabajo (acordadas con el equipo)

1. **Una fase por vez.** Antes de empezar una fase se presenta: objetivo, archivos probables,
   dependencias y criterios de aceptación; se espera la aprobación. Al terminarla se presenta un resumen
   ejecutivo y se espera la autorización para continuar.
2. **Respetar la metodología de la cátedra** (sección 6). Si hace falta apartarse, se justifica, se
   documenta y se avisa. La prioridad es el enfoque de la cátedra, no una arquitectura "más moderna".
3. **Trabajar solo en este repo.** El repositorio de la cátedra y sus materiales son referencia, no se tocan.
4. **Sin sorpresas:** no introducir dependencias innecesarias, no refactorizar fuera de la fase, no
   eliminar funcionalidad sin razón explicada, no ampliar el alcance sin consultar.
5. **Honestidad:** no afirmar que una prueba pasó si no se ejecutó; no ocultar errores; si falta un dato,
   decirlo antes de asumirlo; distinguir **requisito explícito** de **decisión de diseño**.
6. **Seguridad:** no inventar credenciales ni subir secretos. Las de este repo (`root/root`,
   `admin/admin123`) son **solo de desarrollo local**.
7. **Git:**
   - Una rama por fase (`fase-N-<tema>`), creada desde la rama anterior o desde `main` según se indique.
   - **Commits atómicos por funcionalidad, de no más de 100 líneas** (agregadas + borradas).
   - Mensaje en español con el formato `tipo(ámbito): descripción` (`feat`, `fix`, `docs`, `chore`,
     `test`, `build`, `refactor`). Los commits que hace Claude llevan el trailer
     `Co-Authored-By: Claude Sonnet 5.5 <noreply@anthropic.com>`.
   - **Push y merge a `main` solo cuando el usuario lo pide.**
8. **Documentación obligatoria por fase** en `docs/fase-N-<tema>.md`:
   - *Informe técnico:* inventario de archivos (archivo, acción, motivo, responsabilidad), qué se
     implementó y por qué, funciones y métodos (parámetros, retorno, validaciones, dependencias, errores),
     recorrido de cada petición, pruebas ejecutadas con resultados.
   - *Guía de estudio A–J:* A objetivo · B conceptos teóricos (con clase de la cátedra; marcar lo
     complementario) · C arquitectura · D código paso a paso · E trazabilidad de datos · F ejemplo
     práctico · G preguntas del profesor (**≥10**) · H respuestas modelo · I errores frecuentes ·
     J lista de comprobación para rendir.
   - Generar el PDF en `docs/pdf/` (ver `docs/pdf/README.md`) y actualizar este archivo.
9. **Una fase está terminada** cuando: la funcionalidad está implementada; respeta la cátedra; está
   verificada con pruebas **ejecutadas**; los archivos y funciones están documentados; el recorrido de los
   datos está explicado; existe la guía con preguntas y respuestas; las limitaciones están registradas y
   los criterios de aceptación cumplidos.
10. **Objetivo final:** llegar al segundo parcial con un backend funcional, probado y documentado, y que
    **cada integrante pueda explicar cómo funciona cada parte** (la evaluación es individual).

