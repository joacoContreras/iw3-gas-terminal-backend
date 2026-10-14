# Fase 0 — Auditoría y línea base

Rama: `fase-0-auditoria` · Ejecución: 14/10/2026 · Estado: completada (pendientes en la sección 8).

Este archivo tiene dos partes:

- **Parte 1:** informe técnico de lo auditado y verificado.
- **Parte 2:** guía de estudio de la base sobre la que se construye el TP.

Convención de rutas: `SRC` = `src/main/java/ar/edu/iua/iw3`.

---

# Parte 1 — Informe técnico

## 1. Alcance y resultado

La Fase 0 **no modifica código**. Su objetivo es saber con qué se cuenta antes de implementar.

| Pregunta | Respuesta |
|---|---|
| ¿Compila? | Sí. `BUILD SUCCESS`, 50 fuentes, Java 21. |
| ¿Arranca contra MySQL? | Sí, en unos 9 s. |
| ¿Funciona la seguridad JWT? | Sí: login, token por header y por query param, rechazo de token inválido. |
| ¿Hay dominio de gas? | No. Todo el código es el proyecto demo de la cátedra (Product, Category, cli1, cli2). |
| ¿Se puede empezar la Fase 1? | Sí, teniendo en cuenta los hallazgos de la sección 5. |

## 2. Archivos de la fase

| Archivo | Acción | Motivo | Responsabilidad |
|---|---|---|---|
| `docs/plan-de-trabajo.md` | Creado | Plan, decisiones aprobadas y forma de trabajo | Documentación |
| `docs/sql/seed-dev-users.sql` | Creado | Usuario de prueba para obtener un JWT | Datos de desarrollo |
| `docs/fase-0-auditoria.md` | Creado | Este informe y la guía de estudio | Documentación |

No se modificó ningún archivo de `src/` ni el `pom.xml`.

## 3. Entorno verificado

| Componente | Versión o estado |
|---|---|
| Java | Temurin 21.0.12 |
| Maven | Wrapper `mvnw`, Maven 3.9.16 |
| Spring Boot | 4.1.0 (la cátedra usa 4.1.1) |
| MySQL | `mysql:8.0` en Docker, contenedor `iw3-gas-mysql`, puerto 3306, base `iw3_db` |
| Docker | 29.2.1 (el daemon estaba apagado y se inició durante la fase) |
| Sistema operativo | Windows 11 |

En esta máquina ya existía el contenedor `iw3-mysql` de la cátedra (puerto 33306, base
`iw3_2026`). Como el `docker-compose.yml` del TP usa el mismo `container_name`,
`docker compose up` falla con "Conflict". La línea base se levantó con un archivo override
**fuera del repo** que solo cambia el nombre a `iw3-gas-mysql`; el contenedor de la cátedra
no se tocó (ver hallazgo H-4).
