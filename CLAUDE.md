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

**Última actualización:** 12/11/2026, tras implementar la Fase 3 del examen final (rama `final-fase-3-cierre-backend`, sin integrar todavía); las Fases 1 y 2 ya están en `main` (PR #18).

| | |
|---|---|
| **Última fase completada** | **Fase 5 — Pesaje final y conciliación (Punto 5): implementada, probada, auditada e integrada en `main`** (PR #14, 02/11/2026; Parte 1: Tomás Calvo, Parte 2: Benjamín Alasino). Con ella quedan los Puntos 1 a 5 del segundo parcial. Después, en la rama `cierre-segundo-parcial` (sin PR todavía): `/demo/**` cerrado, chequeo del deploy corregido, Swagger completo y carpeta Postman 07 (sección 5) |
| **Qué sigue** | Integrar la Fase 3 a `main`; después el **frontend** (repo `iw3-gas-terminal-frontend`, Fases 4 a 10 de su `docs/plan-de-trabajo.md`), que se conecta al contrato congelado en `docs/contrato-api.md`. Pendientes de la VM (no hechos): definir `JWT_SECRET` y `CORS_ALLOWED_ORIGINS`, usuarios por rol y cuenta SMTP real |
| `main` | Contiene las Fases 0 a 5 (Fase 2 por el PR #6; Fase 3 por los PR #7 y #9; Fase 4 por el PR #12, 31/10/2026; Fase 5 por el PR #14, 02/11/2026) y la guía de inicio de la Fase 4 (PR #11). **El 02/11/2026 se reescribió la historia de `main`** con un push forzado (respaldo temporal ya borrado): se quitaron los commits duplicados y los trailers `Co-Authored-By: Claude` y quedaron 286 commits con el mismo contenido. Si alguien tiene un `main` anterior, debe correr `git fetch` y `git reset --hard origin/main` |
| Revisión de la Fase 1 | El PR #2 se mergeó sin revisiones registradas en GitHub, pero el equipo **revisó después el código integrado en `main` y está todo bien** (informado por el usuario, 19/10/2026) |
| Despliegue | La VM (`jc755.duckdns.org`) se redespliega sola en cada push a `main` (`.github/workflows/deploy.yml`). Verificado el 02/11/2026: corre la Fase 5 y el circuito completo 1 → 4 funcionó con el usuario `martin`. El chequeo del workflow sondea `/v3/api-docs` y exige 200 (decisión 48). Detalles en la sección 13 |

| Fase | Contenido | Rama | Estado |
|---|---|---|---|
| 0 | Auditoría y línea base | `fase-0-auditoria` | Completada e integrada en `main` |
| 1 | Recepción de órdenes (Punto 1) | `fase-1-recepcion-ordenes` | Completada e integrada en `main` (PR #2) |
| 2 | Pesaje inicial y activación (Punto 2) | `fase-2-pesaje-inicial` | Completada e integrada en `main` (PR #6) |
| 3 | Carga y datos en tiempo real (Punto 3) | `fase-3-carga-tiempo-real` | Completada e integrada en `main` (PR #7; correcciones en el PR #9) |
| 4 | Cierre de la orden (Punto 4) | `fase-4-cierre-orden` | Completada e integrada en `main` (PR #12) |
| 5 | Pesaje final, conciliación y circuito completo (Punto 5) | `fase-5-conciliacion` | Completada e integrada en `main` (PR #14) |
| — | Cierre del segundo parcial: Swagger completo, carpeta Postman 07, `/demo/**` cerrado y chequeo del deploy | `cierre-segundo-parcial` | Hecha en la rama (espera PR a `main`) |

**No avanzar de fase sin autorización explícita del usuario.**

**Examen final (desde el 12/11/2026).** El `main` queda como la entrega del parcial; el trabajo del final va en ramas
`final-fase-N-<tema>` (plan y reglas en `CLAUDE.md` y `docs/plan-de-trabajo.md` del repo `iw3-gas-terminal-frontend`):

| Fase del final | Contenido | Rama | Estado |
|---|---|---|---|
| 1 | Roles que regulan el consumo de las APIs | `final-fase-1-roles` | **Completada e integrada en `main` (12/11/2026, PR #18)** y desplegada. 138 tests y Postman 186 pedidos / 428 aserciones sin fallas al cerrarla. Informe: `docs/final-fase-1-roles.md` |
| 2 | Alarma de temperatura por e-mail | `final-fase-2-alarmas` | **Completada e integrada en `main` (12/11/2026, PR #18)** y desplegada (chequeo de salud en verde; las 4 rutas de alarmas figuran en `/v3/api-docs` de la VM). 215 tests, Postman 219 pedidos / 483 aserciones y escenario real con SMTP de mentira (42 verificaciones) sin fallas. Informe: `docs/final-fase-2-alarmas.md`. **Falta probar con una cuenta SMTP real** (variables `SMTP_USER` y `SMTP_PASS`) |
| 3 | Filtro y orden del listado, CORS (H-30), 401 sin `WWW-Authenticate`, H-5 y contrato de la API | `final-fase-3-cierre-backend` | **Implementada y probada (12/11/2026), sin integrar**: 248 tests, Postman 246 pedidos / 539 aserciones, escenario real (26 + 8 verificaciones) y prueba con Chrome real desde `localhost:3000`. Informe: `docs/final-fase-3-cierre-backend.md`; contrato: `docs/contrato-api.md` |

**Dónde va cada cosa (aclarado el 12/11/2026):** los cambios de **backend** (Fases 1 a 3 del final) van a este repo, en ramas `final-fase-N-<tema>`;
los cambios de **frontend** y el plan de trabajo van a `iw3-gas-terminal-frontend`. Los dos repos se comunican solo por HTTP.

**`origin/main` y el cierre del parcial (resuelto el 12/11/2026).** El PR #16 («Fase 5 conciliación», 05/11) se mergeó desde una rama con la historia anterior a la limpieza y dejó `main` sin el cierre del
segundo parcial; el workflow de deploy corrió en verde sobre ese commit. El PR #18 restituyó ese contenido y sumó las Fases 1 y 2: `main` quedó con un árbol **idéntico** al probado (`merge/final-fase-2-alarmas`), y el deploy
terminó en verde. **Lección:** las ramas deben partir de `origin/main` actualizado; una rama con historia reescrita da conflictos al mergearla (el PR #17, abierto desde la rama original, tenía 162 commits y 10 archivos en conflicto).
**Pendiente de decisión del equipo:** `origin/main` conserva 120 commits con `Co-Authored-By: Claude` (de la rama vieja del PR #16); quitarlos exige un `push --force` y que todos reseteen sus copias y borren sus ramas viejas, o vuelve a pasar lo mismo.

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
   - Una rama por fase (`fase-N-<tema>`), creada desde `main` actualizado (con la fase anterior ya integrada).
   - **Commits atómicos por funcionalidad, de no más de 100 líneas** (agregadas + borradas).
   - Mensaje en español con el formato `tipo(ámbito): descripción` (`feat`, `fix`, `docs`, `chore`,
     `test`, `build`, `refactor`).
   - **Sin coautoría de Claude:** los commits y los Pull Request **no** llevan la línea
     `Co-Authored-By: Claude …` ni la firma "Generated with Claude Code". Esta regla tiene prioridad sobre
     la atribución que Claude Code agrega por defecto.
   - **Push y merge a `main` solo cuando el usuario lo pide.**
   - **Todo cambio a `main` entra por Pull Request.** Acuerdo del equipo (14/10/2026): no se mergea hasta que
     aprueben **3 de los 4 integrantes**. GitHub todavía **no lo exige** (ver 8.3): hasta entonces, abrir los PR
     como *borrador* (`draft`) y no mergear sin esas aprobaciones.
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

## 4. La consigna, resumida

> **Fuente oficial:** `docs/pdf/consigna-trabajo-integrador-2026.pdf` (copia de la consigna de la cátedra). Este
> resumen es una guía rápida: **ante cualquier duda manda el PDF**.

### 4.1 El proceso (cinco puntos)

| Punto | Qué ocurre | Estado resultante |
|---|---|---|
| 1 | SAP envía la orden: número, camión, chofer, cliente, producto, fecha prevista de carga y preset (kg). Las entidades se relacionan por **código externo** (string) | **1** Pendiente de pesaje inicial |
| 2 | La balanza (TMS) envía la **tara**; se genera una **contraseña de activación** de 5 dígitos asociada a la orden | **2** Con pesaje inicial registrado |
| 3 | El sistema de carga envía contraseña + número de orden; si la orden existe, está en el estado esperado y la contraseña es correcta, el backend **devuelve el preset**. Luego llegan datos en tiempo real | sigue en **2** |
| 4 | Un endpoint **cierra** la orden y bloquea nuevos datos de detalle | **3** Cerrada para carga |
| 5 | La balanza envía el **pesaje final**; el backend devuelve la **conciliación** | **4** Finalizada |

- Los cambios de estado son **secuenciales y unidireccionales**.
- Cada cambio queda registrado: fecha y hora, usuario o proceso, observaciones (log de auditoría).
- El diagrama de estados del PDF rotula el estado 3 como "proceso de carga", pero el anexo y el requisito
  del final ("tiempo transcurrido, solo estado 2") indican que **los datos de carga llegan en estado 2**.

### 4.2 Datos de carga (Punto 3)

- Cada lectura trae: **masa acumulada** (kg, creciente), **densidad** (entre 0 y 1), **temperatura** (°C),
  **caudal** (kg/h) y estampa de tiempo. El caudalímetro es másico.
- **Descartar** el registro si: caudal ≤ 0, masa acumulada ≤ 0 o masa menor que la anterior.
- La **cabecera** de la orden guarda el último valor válido de cada atributo; el **detalle** guarda el
  histórico. La frecuencia de almacenamiento puede ser menor que la de recepción (opcional para el parcial:
  alcanza con guardar todos).
- Fechas de la orden: recepción inicial, pesaje inicial, inicio de carga (primer registro válido), fin de
  carga (último registro válido) y pesaje final.

### 4.3 Conciliación (Punto 5)

| Dato | Cálculo |
|---|---|
| Pesaje inicial (tara) y pesaje final | Recibidos de la balanza |
| Producto cargado | Última masa acumulada |
| Neto por balanza | Pesaje final − pesaje inicial |
| Diferencia balanza vs. caudalímetro | Neto por balanza − producto cargado |
| Promedios de temperatura, densidad y caudal | Sobre los datos almacenados |

La conciliación se consulta con un endpoint específico, **solo para órdenes en estado 4**.

### 4.4 Qué se exige en cada instancia

| Instancia | Requisitos |
|---|---|
| **Segundo parcial (obligatorio)** | Puntos 1 a 5 como API **sin interfaz gráfica**; documentación **OpenAPI/Swagger**; **colección Postman** que pruebe todas las APIs |
| Segundo parcial (opcional) | *Tests* de Postman que simulen el circuito completo; frecuencia de almacenamiento configurable (**no se implementa**, decisión 49); promedios (alcanza con el último valor) |
| **Examen final** | 100 % de los endpoints; **roles** de usuario; **alarma de temperatura** por e-mail (umbral y destinatarios configurables, sin reenvío hasta que un usuario autorizado la acepte); **frontend** con login, monitoreo en tiempo real (número, estado, camión, preset, carga actual, últimas temperatura, densidad y caudal, filtros y orden), ETA, tiempo transcurrido (solo estado 2), gestión de alarmas y visualización de conciliaciones (solo estado 4) |

Los requisitos del examen final **no** se incorporan al alcance del parcial.

## 5. Qué se hizo

### Fase 0 — Auditoría y línea base (completada, integrada en `main`)

- El repo era una **copia del proyecto de clase** (Product/Category, integraciones cli1/cli2, JWT) sin
  ninguna línea del dominio de gas. No se tocó código: se midió.
- **Línea base ejecutada** contra MySQL real (18 verificaciones): compila, arranca, `contextLoads`, login,
  JWT, convención de errores y perfiles.
- **11 hallazgos** (H-1 a H-11) con evidencia, causa, impacto y propuesta.
- Se crearon el plan (`docs/plan-de-trabajo.md`), el informe con su guía (`docs/fase-0-auditoria.md`) y
  el seed del usuario de prueba (`docs/sql/seed-dev-users.sql`).

### Fase 1 — Recepción y creación de órdenes (completada e integrada en `main`, rama `fase-1-recepcion-ordenes`)

- **Retiro del demo** de la cátedra (decisión 1): `Product`/`Category`, `integration/cli1`, `cli2`,
  `ScanConfig`, `JsonUtiles` y el dataset de productos. Siguen en el historial de git.
- **Correcciones aprobadas:** contenedor `iw3-gas-mysql` (H-4), `/error` público (H-1), login sin
  `produces=text/plain` (H-2), `getUserLogged()` (H-10) y fechas guardadas con hora de Argentina (H-8).
- **Dominio:** `Truck` + `TruckTank`, `Driver`, `Customer`, `Product`, `Order`, `OrderStatusLog` y el enum
  `OrderStatus` (estados 1 a 4, columna numérica).
- **Punto 1:** `OrderBusiness.add` valida (400), rechaza el número repetido (302), sincroniza las cuatro
  entidades por `externalCode`, crea la orden en estado 1 y registra el primer cambio de estado, todo en
  **una transacción** (si algo falla no queda nada).
- **Swagger** (springdoc) y **colección Postman** (10 requests, 19 aserciones, 0 fallas con newman).
- **Informe y guía** en `docs/fase-1-recepcion-ordenes.md` y PDF de las Fases 0 y 1 en `docs/pdf/`.
- **Un bug propio** se detectó probando y se corrigió (el validador no revisaba chofer, cliente ni producto).
- **Integración:** PR #2 mergeado a `main` el 14/10/2026 (`a330219`) por otro integrante, sin revisiones registradas.

### Fase 2 — Pesaje inicial y activación (Punto 2, completada e integrada en `main` por el PR #6)

- **División del trabajo entre 4 personas:** Etapa A (Martín González: modelo, 409 y generador de claves), Etapa B (Tomás Calvo: regla transaccional `registerTare`), Etapa C (Benjamín Alasino: endpoint TMS) y Etapa D (Joaquín Contreras: Postman, pruebas y documentación).
- **Modelo:** `Order` incorpora `tare` (`Double`), `initialWeighingDateTime` (`LocalDateTime`) y `activationPassword` (`Integer`, con `@JsonIgnore`). Columnas nulables para coexistir con órdenes previas.
- **Excepción nueva:** `ConflictException` (checked) → HTTP 409 Conflict ante orden en estado distinto de 1.
- **Generador seguro:** `ActivationPasswordGenerator` genera enteros de 5 dígitos (10000–99999) con `SecureRandom`.
- **Punto 2:** `OrderBusiness.registerTare` valida existencia (404), peso positivo (400) y estado pendiente de pesaje inicial (409) fuera del `try/catch`. Dentro de `@Transactional(rollbackFor = Exception.class)`, guarda tara, estampa y clave, transiciona al estado 2 y registra auditoría en `order_status_logs`.
- **Endpoint TMS:** `POST /api/v1/integration/tms/orders/{orderNumber}/tare` con DTOs `TareRequest` y `TareResponse`.
- **Postman y pruebas:** Carpeta `02 - Fase 2: pesaje inicial (TMS)` (11 pedidos, 27 aserciones). Corridas con newman 6: 21 pedidos, 46 aserciones ejecutadas con 0 fallas (las 19 de Fase 1 permanecen intactas).
- **Informe y guía:** `docs/fase-2-pesaje-inicial.md` completo con informe técnico y guía de estudio A–J (12 preguntas de defensa con respuestas).

### Fase 3 — Carga y datos en tiempo real (Punto 3, completada e integrada en `main` por los PR #7 y #9)

- **Trabajo secuencial en 3 partes** (`docs/fase-3-division-de-trabajo.md`): Parte 1 (Martín González: modelo y activación), Parte 2 (Joaquín Contreras: lecturas y descarte) y Parte 3 (Tomás Calvo: Postman, auditoría y documentación). Decisiones D1 a D10 = decisiones 18 a 27.
- **Modelo:** `Order` suma 7 campos nulables (`activationDateTime`, `loadingStartDateTime`, `loadingEndDateTime`, `lastAccumulatedMass`, `lastDensity`, `lastTemperature`, `lastFlowRate`). Entidad nueva `OrderDetail` (tabla `order_details`, `@ManyToOne LAZY` con `@JsonIgnore`). `Order` **no** tiene lista de detalles.
- **Activación:** `OrderBusiness.activate` (400 → 404 → 409 → 403, fuera del `try`), idempotente, no escribe historial; `ForbiddenException` → 403.
- **Lecturas:** `IOrderDetailBusiness`/`OrderDetailBusiness.add` (404 → 400 → 409 → descarte); `OrderDetailValidator` descarta caudal ≤ 0, masa ≤ 0, masa menor que la última válida y densidad fuera de (0, 1); siempre 200 con `{ saved, reason }`; detalle y cabecera en una transacción con `saveAndFlush`.
- **Histórico (decisión 11 = 28):** `GET /api/v1/orders/{orderNumber}/details` paginado (`OrderDetailBusiness.list`, `OrderDetailRepository.findByOrder`), orden fijo por `id`, tope de 100.
- **Pruebas:** carpeta Postman `03 - Fase 3: carga (sistema de carga)` (hoy 45 pedidos y 117 aserciones, tras las correcciones de la auditoría); newman al cerrar la fase: 65 pedidos y 160 aserciones, 0 fallas. `./mvnw -B test`: 11 tests, 0 fallas. Auditoría con matriz de trazabilidad, simulación de estado 3 (409) y prueba de atomicidad.
- **Hallazgos:** H-14 (defecto menor: una falla de base al confirmar la activación no respondía con `StandartResponse`) **corregido** con `saveAndFlush` (`4ed54c8`); limitaciones H-15 a H-22.
- **Informe y guía:** `docs/fase-3-carga-tiempo-real.md` (16 preguntas con respuestas y qué debe explicar cada integrante).

### Fase 4 — Cierre de la orden (Punto 4, completada e integrada en `main` por el PR #12)

- **Responsable único:** Benjamín Alasino (autorizada por Martín González el 27/10/2026). Decisiones 29 a 32.
- **Negocio:** `IOrderBusiness.close`/`OrderBusiness.close`: 404 → 409 fuera del `try`; busca con `findByOrderNumberForUpdate` (helper privado `loadForUpdate`, duplicado del de `OrderDetailBusiness`); estado 2 → 3 con `saveAndFlush`; fila en `order_status_logs` ("Orden cerrada para carga", usuario del token). No toca fechas de carga ni últimos valores; no hay campos nuevos.
- **Endpoint:** `POST /api/v1/integration/loader/orders/{orderNumber}/close` en `OrderLoaderRestController`, sin cuerpo, con Swagger.
- **Bloqueo de lecturas:** sin código nuevo; el 409 de `OrderDetailBusiness.add` (Fase 3) se activa con el estado 3. Verificado.
- **Pruebas:** `OrderBusinessCloseTest` (7 tests con Mockito): `./mvnw -B test` 19 tests, 0 fallas. Carpeta Postman `04 - Fase 4: cierre (sistema de carga)` (20 pedidos, 41 aserciones): newman 86 pedidos y 204 aserciones, 0 fallas. Concurrencia: 50 lecturas + 1 cierre en paralelo (dos corridas) sin 500, una sola fila 2→3 y cabecera = máximo guardado; dos cierres simultáneos dan 200 y 409.
- **Hallazgos:** H-23 a H-25 (observaciones y una limitación; ninguno bloquea).
- **Informe y guía:** `docs/fase-4-cierre-orden.md` (15 preguntas con respuestas).

### Fase 5 — Pesaje final y conciliación (Punto 5, completada e integrada en `main` por el PR #14)

- **Trabajo secuencial en 2 partes** (`docs/fase-5-division-de-trabajo.md`, autorizada por Martín González el 02/11/2026): Parte 1 (Tomás Calvo: negocio, endpoints y arreglos) y Parte 2 (Benjamín Alasino: Postman, concurrencia, auditoría y documentación). Decisiones D1 a D14 = decisiones 33 a 46.
- **Modelo:** `Order` suma `finalWeight` y `finalWeighingDateTime` (opcionales). `OrderDetailRepository.averagesByOrder` (`AVG`) con la proyección `ReadingAverages`. La conciliación (`Reconciliation`) **no se guarda**.
- **Negocio:** `registerFinalWeight` (404 → 400 → 409 → 409 → 400, fuera del `try`; con bloqueo; estado 3 → 4; fila "Pesaje final registrado") y `reconciliation` (solo estado 4); una sola función `buildReconciliation`. Arreglos: `requireValidWeight` en la tara (H-26) y `loadForUpdate` en `activate` (H-25, y con eso H-16).
- **Endpoints:** `POST /api/v1/integration/tms/orders/{orderNumber}/final-weight` y `GET /api/v1/orders/{orderNumber}/reconciliation`.
- **Pruebas:** `./mvnw -B test` 40 tests, 0 fallas. Postman: carpetas `05` (44 pedidos, 98 aserciones) y `06 - Circuito completo (1 a 4)` (21 pedidos, 56 aserciones); newman 151 pedidos y 358 aserciones, 0 fallas. Concurrencia: pesaje final doble, activación contra cierre, activación doble y 50 lecturas con un cierre, sin 500 y sin estados inconsistentes.
- **Auditoría final** del segundo parcial contra el PDF: Puntos 1 a 5 implementados y verificados; observaciones H-27 a H-29.
- **Informe y guía:** `docs/fase-5-conciliacion.md` (16 preguntas con respuestas y qué debe explicar cada integrante).

### Cierre del segundo parcial (02/11/2026, rama `cierre-segundo-parcial`, sin PR todavía)

- **Historial de `main` limpio:** push forzado que quitó los commits duplicados y los trailers `Co-Authored-By: Claude` (286 commits, mismo contenido); la rama `fase-5-conciliacion` se borró de GitHub.
- **`/demo/**` cerrado** (decisión 47): `GET /demo/encodepass` exige token. Con token sigue devolviendo el hash BCrypt.
- **Chequeo del deploy corregido** (decisión 48): sondea `/v3/api-docs` y exige 200 (antes llamaba a `/api/v1/products`, ruta retirada, y aceptaba 200, 401 o 403).
- **Swagger completo** (decisión 50, resuelve H-27): los 13 endpoints tienen `@Operation`, y los protegidos declaran 401 y 500 y el 404 donde corresponde; el login figura sin candado (`@SecurityRequirements`) y con su tag *Autenticación*.
- **Postman:** carpeta `07 - Demo de la cátedra (encodepass)` (3 pedidos y 6 aserciones: login propio, `/demo/encodepass` sin token 401 y con token 200 con un hash BCrypt de 60 caracteres). Cubre las 13 rutas del documento OpenAPI (resuelve H-28). Newman: **154 pedidos y 364 aserciones, 0 fallas**; las carpetas 00 a 06 no cambiaron. `./mvnw -B test`: 40 tests.
- **Prueba en la VM** (verificada a mano): el login con `martin` (usuario `ROLE_ADMIN` creado en la base de la VM, que no tenía usuarios) y el circuito 1 → 4 con la orden de prueba `97001` dieron los resultados esperados: pesaje final, conciliación idéntica en el POST y el GET, historial de 4 filas y segundo pesaje 409.
- **No se hace** la frecuencia de almacenamiento configurable (decisión 49): se guardan todas las lecturas válidas, como permite la consigna.

## 6. Arquitectura y convenciones del código

```
Sistema externo ── HTTP + JWT ──▶ Controller ──▶ Business (I*Business) ──▶ Repository ──▶ MySQL
```

| Paquete (bajo `src/main/java/ar/edu/iua/iw3`) | Contenido |
|---|---|
| `controllers` | `BaseRestController` (`getUserLogged()`), `Constants` (URLs), `OrderRestController` |
| `integration/<sistema>/controllers`, `integration/<sistema>/model` | Endpoints y DTO por sistema externo: `sap` (`OrderSapRestController`), `tms` (`OrderTmsRestController`, `TareRequest`, `TareResponse`, `FinalWeightRequest`) y `loader` (`OrderLoaderRestController`, `ActivationRequest`/`Response`, `DetailRequest`/`Response`) |
| `business`, `business/exception` | `I*Business` + `*Business` (`IOrderBusiness`, `IOrderDetailBusiness`), `OrderValidator`, `OrderDetailValidator`, `DetailResult`, `Reconciliation`, `ActivationPasswordGenerator`; excepciones de negocio |
| `model`, `model/persistence` | Entidades JPA y repositorios Spring Data (más la proyección `ReadingAverages`) |
| `auth`, `security` | Usuarios, roles, JWT y reglas de acceso (de la cátedra) |
| `config` | `OpenApiConfig` (Swagger con esquema Bearer) |
| `util` | `StandartResponse` y su servicio (respuesta de error estándar) |

**Convenciones (las de la cátedra, salvo lo marcado):**

- `IXxxBusiness` + `XxxBusiness` con `@Service`, inyección `@Autowired` en el campo, DAOs llamados `xxxDAO`.
- Excepciones *checked* → HTTP: `NotFoundException` 404, `FoundException` **302** (convención de la
  cátedra para duplicados), `BusinessException` 500, y extensiones nuestras: `BadRequestException` **400**,
  `ConflictException` **409** (estado incorrecto) y `ForbiddenException` **403** (contraseña de activación incorrecta).
- Controller: `try/catch` por método, `ResponseEntity<?>`, error con
  `IStandartResponseBusiness.build(status, ex, message)` (JSON `message`, `code`, `devInfo`), y alta con 201
  + header `location`. La clase `StandartResponse` conserva la errata de la cátedra.
- Todas las URLs viven en `Constants`, bajo `/api/v1`.
- Entidades: Lombok (`@Getter @Setter @NoArgsConstructor @AllArgsConstructor`), `@Id` con `IDENTITY`;
  las que vienen de afuera llevan **`externalCode` (String, único)**.
- **Nuestro:** el `@RequestBody` se enlaza a la entidad, pero el negocio **copia solo los campos
  permitidos** (el cliente no fija `id`, estado ni fechas del backend).
- **Nuestro:** `@Transactional(rollbackFor = Exception.class)` en lo que escribe varias tablas (las
  excepciones de negocio son *checked* y Spring no revierte por ellas).
- Validación manual en el negocio, juntando todos los errores en un solo mensaje (sin Bean Validation).
- **No usar JSON dinámico tipo `JsonUtiles`:** descartaba números enteros (H-6). Usar el binding de Jackson.
- Fechas: `LocalDateTime`; `spring.jpa.properties.hibernate.jdbc.time_zone` hace que la base guarde la
  hora de Argentina.
- Tablas: `orders` (ORDER es reservada) y `gas_products` (la tabla `products` del demo existe en bases
  viejas con otro esquema). `ddl-auto=update`: crea y agrega, **nunca borra ni renombra**.
- Seguridad: el CORS está dentro de la cadena de seguridad (preflight sin token; orígenes por `CORS_ALLOWED_ORIGINS`, por defecto `*`); el 401 y el 403 llevan el JSON estándar y **no** hay `httpBasic` ni `WWW-Authenticate`; la clave JWT sale de `JWT_SECRET` (sin definirla rige la de `AuthConstants`). Todo exige JWT salvo `POST /api/v1/login`, `/error` y la documentación (desde el 02/11/2026 `/demo/**` también exige token). El token
  es HS512, vence en 1 h y viaja en `Authorization: Bearer <jwt>` (o `?authtoken=`).
- Perfiles de Spring: `mysqldev` y `mysqlprod` (properties). Se activan con
  `-Dspring-boot.run.profiles=<perfil>` o `SPRING_PROFILES_ACTIVE`; el `-P` de Maven **no** los activa (H-3).

## 7. API y modelo actuales

| Método y ruta | Qué hace | Respuestas |
|---|---|---|
| `POST /api/v1/login` | Formulario `username`/`password`; devuelve el JWT en texto. Único endpoint público | 200, 401 (con JSON), 500 |
| `POST /api/v1/integration/sap/orders` | **Punto 1**: recibe la orden | 201 + `location`, 302, 400, 401, 500 |
| `POST /api/v1/integration/tms/orders/{orderNumber}/tare` | **Punto 2**: registra la tara y devuelve la contraseña de activación | 200, 400, 401, 404, 409, 500 |
| `POST /api/v1/integration/loader/activation` | **Punto 3**: activa la carga con `{ orderNumber, password }` y devuelve `{ preset }`. Idempotente; la orden sigue en estado 2 | 200, 400, 401, 403, 404, 409, 500 |
| `POST /api/v1/integration/loader/orders/{orderNumber}/details` | **Punto 3**: una lectura del caudalímetro. Siempre 200 con `{ saved, reason }` (guardada o descartada) | 200, 400, 401, 404, 409, 500 |
| `POST /api/v1/integration/loader/orders/{orderNumber}/close` | **Punto 4**: cierra la orden (estado 2 → 3); desde ahí las lecturas dan 409. Sin cuerpo; registra el cambio con el usuario del token y responde la orden. No es idempotente | 200, 401, 404, 409, 500 |
| `POST /api/v1/integration/tms/orders/{orderNumber}/final-weight` | **Punto 5**: registra el pesaje final (estado 3 → 4, fila "Pesaje final registrado") y devuelve la conciliación. No es idempotente; peso igual a la tara válido, menor 400 | 200, 400, 401, 404, 409, 500 |
| `GET /api/v1/orders/{orderNumber}/reconciliation` | **Punto 5**: conciliación, solo para órdenes en estado 4 (se calcula, no se guarda) | 200, 401, 404, 409, 500 |
| `GET /api/v1/orders?status=&sort=&page=&size=` | Lista paginada de órdenes. `status` (1 a 4) filtra por estado; `sort=campo,dirección` ordena por `orderNumber`, `plannedLoadDateTime`, `receptionDateTime`, `status` o `preset` (otro campo → 400); sin `sort`, la más reciente primero, y el número de orden desempata. `page` desde 0; `size` por defecto 20, máximo 100. Responde `{ content, page: { size, number, totalElements, totalPages } }` | 200, 400, 401, 403, 500 |
| `GET /api/v1/orders/{orderNumber}` | Una orden (no expone `activationPassword`) | 200, 401, 404, 500 |
| `GET /api/v1/orders/{orderNumber}/status-log` | Historial de estados | 200, 401, 404, 500 |
| `GET /api/v1/orders/{orderNumber}/details?page=&size=` | Lecturas guardadas de la orden, paginadas por `id` ascendente (`size` 20 por defecto, máximo 100; se ignora `sort`). Responde `{ content, page }` | 200, 401, 404, 500 |
| `GET /api/v1/alarms?status=&orderNumber=&page=&size=` | **Final, Fase 2**: alarmas de temperatura, la más reciente primero. Roles: ADMIN, SUPERVISOR, OPERADOR | 200 `{ content, page }`, 400, 401, 403, 500 |
| `POST /api/v1/alarms/{id}/acknowledge` | **Final, Fase 2**: acepta una alarma pendiente (cuerpo opcional `{ observation }`; el usuario sale del token). Roles: ADMIN, SUPERVISOR | 200, 400, 401, 403, 404, 409, 500 |
| `GET /api/v1/alarms/config` y `PUT /api/v1/alarms/config` | **Final, Fase 2**: umbral (°C) y correos de destino. GET: ADMIN y SUPERVISOR; PUT: solo ADMIN | 200, 400, 401, 403, 500 |
| `GET /demo/encodepass?password=` | Hash BCrypt (demo de la cátedra). **Exige token** desde el 02/11/2026 (decisión 47) | 200, 401, 500 |
| `/swagger-ui.html`, `/v3/api-docs` | Documentación OpenAPI | públicos |

Cuerpo de `POST /integration/sap/orders` (resumido; ejemplo completo en la colección Postman):

```json
{ "orderNumber": 90001, "plannedLoadDateTime": "2026-10-20T08:00:00", "preset": 15000,
  "truck": { "externalCode": "TRK-001", "plate": "AB123CD",
             "tanks": [ { "number": 1, "capacityLiters": 10000 }, { "number": 2, "capacityLiters": 5000 } ] },
  "driver": { "externalCode": "DRV-001", "name": "Juan", "lastName": "Perez" },
  "customer": { "externalCode": "CUS-001", "companyName": "Gas del Sur SA" },
  "product": { "externalCode": "PRD-GLP", "name": "GLP" } }
```

Cuerpo de `POST /integration/tms/orders/{orderNumber}/tare`: `{ "weight": 14500.0 }`. Respuesta 200: `{ "activationPassword": 48213 }`.

Cuerpo de `POST /integration/loader/activation`: `{ "orderNumber": 90001, "password": 48213 }`. Respuesta 200: `{ "preset": 15000.0 }`.
Cuerpo de `POST /integration/loader/orders/{orderNumber}/details`: `{ "accumulatedMass": 1250.5, "density": 0.52,
"temperature": 18.4, "flowRate": 3600.0 }` (la estampa la pone el servidor al recibir; un `timestamp` del cliente se ignora).
Respuesta 200: `{ "saved": true, "reason": null }` o `{ "saved": false, "reason": "..." }`.

`POST /integration/loader/orders/{orderNumber}/close` no lleva cuerpo; responde la orden (como `GET /orders/{n}`) con `"status": 3`.

Cuerpo de `POST /integration/tms/orders/{orderNumber}/final-weight`: `{ "weight": 16490.0 }`. Respuesta 200 (igual que `GET /orders/{n}/reconciliation`),
con tara 14500 y lecturas de 1000 kg (20 °C, 0.5, 3000 kg/h) y 2000 kg (22 °C, 0.5, 3600 kg/h):

```json
{ "orderNumber": 90001, "tare": 14500.0, "finalWeight": 16490.0, "loadedProduct": 2000.0, "scaleNet": 1990.0,
  "difference": -10.0, "averageTemperature": 21.0, "averageDensity": 0.5, "averageFlowRate": 3300.0 }
```

Sin lecturas: `loadedProduct` 0.0 y promedios `null`. Los valores no se redondean.

**Pruebas actuales** (medidas el 02/11/2026): `./mvnw -B test` → 40 tests; colección Postman → 154 pedidos y 364 aserciones, 0 fallas (carpetas 00 a 04: 86 y 204; 05: 44 y 98; 06: 21 y 56; 07: 3 y 6). Los 13 endpoints del documento OpenAPI tienen al menos un pedido.

| Entidad (clase → tabla) | Notas |
|---|---|
| `Order` → `orders` | `orderNumber` único, camión/chofer/cliente/producto, `plannedLoadDateTime`, `preset`, `status`, `receptionDateTime`, `tare`, `initialWeighingDateTime`, `activationPassword` (con `@JsonIgnore`), `activationDateTime`, `loadingStartDateTime`, `loadingEndDateTime`, `lastAccumulatedMass`, `lastDensity`, `lastTemperature`, `lastFlowRate`, `finalWeight`, `finalWeighingDateTime`. Sin lista de detalles |
| `OrderDetail` → `order_details` | Lecturas válidas del caudalímetro: `accumulatedMass`, `density`, `temperature`, `flowRate`, `timestamp` (columna `reading_timestamp`); `@ManyToOne LAZY` a `Order` con `@JsonIgnore` (columna `id_order`) |
| `OrderStatusLog` → `order_status_logs` | Estado anterior (`null` al crear) y nuevo, fecha y hora, `actor`, observaciones |
| `Truck`, `TruckTank` → `trucks`, `truck_tanks` | Camión con su cisternado (una fila por cisterna) |
| `Driver`, `Customer`, `Product` → `drivers`, `customers`, `gas_products` | `externalCode` único |
| `User`, `Role` → `users`, `roles`, `userroles` | Autenticación (de la cátedra) |

## 8. Qué falta

### 8.1 Fases del parcial (en orden)

**Fase 2 — Pesaje inicial y activación (Punto 2)** · rama `fase-2-pesaje-inicial`
- **Estado: COMPLETADA e integrada en `main`** (PR #6, `bd2d009`).

**Fase 3 — Carga y datos en tiempo real (Punto 3)** · rama `fase-3-carga-tiempo-real`

- **Estado: COMPLETADA e integrada en `main`** (PR #7; correcciones de la auditoría en el PR #9; ver `docs/fase-3-carga-tiempo-real.md`).
  H-14 corregido y decisión 11 implementada (27/10/2026).

- Activación: `POST /api/v1/integration/loader/activation` con `{ orderNumber, password }`; valida que la
  orden exista, esté en estado 2 y la contraseña sea correcta; devuelve el `preset` y guarda
  `activationDateTime` (decisión 7).
- Detalle: `POST /api/v1/integration/loader/orders/{orderNumber}/details` con masa, densidad, temperatura
  y caudal; solo en estado 2 y con la orden activada.
- Validar densidad `0 < d < 1` (decisión 8) y **descartar** caudal ≤ 0, masa ≤ 0 o masa menor que la anterior.
- Entidad nueva `OrderDetail`; la cabecera de `Order` guarda los últimos valores y su estampa; fechas de
  inicio y fin de carga. Para el parcial alcanza con guardar todos los registros.
- Resuelto (decisiones 20 y 19): un registro descartado responde 200 con `saved: false` y la activación es
  idempotente. Eficiencia medida: 100 lecturas seguidas, ≈ 80 ms cada una en local, sin degradación.
- Agregado (decisión 28): `GET /api/v1/orders/{orderNumber}/details` paginado para consultar el histórico.

**Fase 4 — Cierre de la orden (Punto 4)** · rama `fase-4-cierre-orden`

- **Estado: COMPLETADA e integrada en `main`** (PR #12, 31/10/2026; ver `docs/fase-4-cierre-orden.md`). La decisión 30 quedó
  confirmada el 02/11/2026.
- `POST /api/v1/integration/loader/orders/{orderNumber}/close`: estado 2 → 3. Desde ahí se rechazan los
  detalles. Rechazar transiciones inválidas (cerrar dos veces, o desde el estado 1).

**Fase 5 — Pesaje final, conciliación y circuito completo (Punto 5)** · rama `fase-5-conciliacion`

- **Estado: COMPLETADA, verificada e integrada en `main`** (PR #14, 02/11/2026; ver `docs/fase-5-conciliacion.md`).
- `POST /api/v1/integration/tms/orders/{orderNumber}/final-weight`: estado 3 → 4 y devuelve la conciliación.
- `GET /api/v1/orders/{orderNumber}/reconciliation`: solo para estado 4.
- Promedios con `AVG` sobre los detalles almacenados (decisión 3).
- Postman consolidado con *Tests* que simulen el circuito 1 → 4 y verificación de los requisitos obligatorios del
  parcial: hechos. Swagger completo y carpeta 07: ver «Cierre del segundo parcial» (sección 5).

### 8.2 Para el examen final

~~Roles por endpoint (SAP, TMS, sistema de carga, operadores)~~ (hecho en `final-fase-1-roles`) · alarma de temperatura por e-mail con umbral y
destinatarios configurables y sin reenvío hasta que se acepte (usar las clases de la cátedra: eventos,
`EmailBusiness`, `Scheduler`) · frontend completo · endurecimiento de seguridad (H-5) · despliegue. **Hecho en `final-fase-2-alarmas` (12/11/2026):** alarma por e-mail con eventos de Spring y `EmailBusiness` (sin reintentos; el `Scheduler` quedó sin usar).

### 8.3 Pendientes transversales

- ~~Revisión de la Fase 1 por el equipo~~ — hecha, sin observaciones (19/10/2026).
- **Proteger `main`** (decisión 14): exigir Pull Request con la aprobación de 3 de los 4 integrantes. Solo el dueño
  del repo (`joacoContreras`, único admin) puede configurarlo. **Estado real (02/11/2026):** existe un *ruleset*
  ("iw3 - Estandar de Repositorios") pero su *enforcement* está **deshabilitado**: GitHub no exige las aprobaciones y los PR
  se mergean con las que haya (el PR #12 entró con una). El acuerdo de las 3 aprobaciones se cumple a mano. En GitHub el
  autor del PR **no** cuenta como aprobación.
- ~~Desplegar el backend del TP en la VM~~ — hecho (19/10/2026; ver sección 13). Se redespliega solo al mergear a `main`:
  hoy el workflow de GitHub Actions lo hace en cada push a `main`.

- ~~**Cierre del segundo parcial:** abrir el PR de `cierre-segundo-parcial` a `main`~~ — hecho (PR #15, 02/11/2026). El PR #16 lo pisó el 05/11 y el PR #18 lo restituyó el 12/11/2026 (ver la sección 2). La colección Postman actual (`docs/postman/IW3-Gas-Terminal.postman_collection.json`) tiene 10 carpetas (00 a 09) y 219 pedidos.
- **Decisión 30:** confirmada el 02/11/2026 (se deja como está; el enunciado no define condiciones para el cierre).

### 8.4 Cómo proteger `main` (lo hace el dueño del repo, `joacoContreras`)

En GitHub: *Settings → Branches → Add branch ruleset* (o una regla de protección clásica) sobre `main`:

- Activar **Require a pull request before merging**.
- **Required approvals:** cuenta solo a *otras* personas (el autor del PR no suma). Con 4 integrantes, **3**
  exige que aprueben los otros tres; **2** equivale a "3 de 4" contando al autor. Elegir según el acuerdo.
- Activar **Dismiss stale pull request approvals when new commits are pushed** y
  **Require approval of the most recent reviewable push**.
- Activar **Block force pushes** y **Do not allow bypassing the above settings** (incluye a los admin).
- Solo cuentan las aprobaciones de cuentas con **permiso de escritura** (*Settings → Collaborators*).

Por consola, con `gh` autenticado como admin:

```bash
gh api -X PUT repos/joacoContreras/iw3-gas-terminal-backend/branches/main/protection --input - <<'JSON'
{ "required_status_checks": null, "enforce_admins": true, "restrictions": null,
  "required_pull_request_reviews": { "required_approving_review_count": 3,
      "dismiss_stale_reviews": true, "require_last_push_approval": true },
  "allow_force_pushes": false, "allow_deletions": false }
JSON
```

## 9. Decisiones tomadas

| # | Decisión | Estado |
|---|---|---|
| 1 | Retirar el código demo de la cátedra | Aplicada (Fase 1) |
| 2 | Mantener `anyRequest().authenticated()`; Postman hace login con un usuario de prueba; roles al final | Vigente |
| 3 | Implementar los promedios (`AVG`) | Aplicada (Fase 5) |
| 4 | Duplicado: `FoundException` → 302, como la cátedra | Aplicada |
| 5 | Identificadores en inglés; comentarios y documentación en español | Vigente |
| 6 | **No** alinear el `pom.xml` con la cátedra (Boot 4.1.1, java-jwt 4.6.1) | Vigente |
| 7 | Campo `activationDateTime` para distinguir "activada" de "tara registrada" | Fase 3 |
| 8 | Validar densidad `0 < d < 1` | Fase 3 |
| 9 | Una rama por fase y commits atómicos de ≤100 líneas | Vigente |
| 10 | Contenedor MySQL `iw3-gas-mysql` (evita chocar con el `iw3-mysql` de la cátedra) | Aplicada |
| 11 | Permitir `/error` y corregir el `produces` del login | Aplicada |
| 12 | `getUserLogged()` en `BaseRestController` | Aplicada |
| 13 | Integrar la Fase 0 a `main` | Aplicada |
| 14 | Todo cambio a `main` por Pull Request con la aprobación de 3 de los 4 integrantes | Acordada; la protección de rama la debe configurar el dueño del repo (8.3) |
| 15 | Código HTTP 409 Conflict (`ConflictException`) para orden en estado incorrecto | Aplicada (Fase 2) |
| 16 | Contraseña de activación de 5 dígitos generada con `SecureRandom` y oculta con `@JsonIgnore` | Aplicada (Fase 2) |
| 17 | Orden de comprobaciones de tara: 404 (orden) → 400 (peso > 0) → 409 (estado), todas fuera del `try/catch` | Aplicada (Fase 2) |
| 18 | (D1) Contraseña de activación incorrecta → 403 con `ForbiddenException`; el mensaje no repite contraseñas | Aplicada (Fase 3) |
| 19 | (D2) Activación idempotente: 200, mismo preset, `activationDateTime` sin cambios | Aplicada (Fase 3) |
| 20 | (D3) Lectura: siempre 200 con `{ saved, reason }` (guardada o descartada) | Aplicada (Fase 3) |
| 21 | (D4) Densidad fuera de (0, 1) se descarta; campo ausente → 400 | Aplicada (Fase 3) |
| 22 | (D5) Masa igual a la anterior es válida; "anterior" = última masa válida de la cabecera | Aplicada (Fase 3) |
| 23 | (D6) La estampa de la lectura es siempre la hora de recepción del servidor; el `timestamp` del cliente se ignora | Aplicada (Fase 3) y corregida tras la auditoría (F-13) |
| 24 | (D7) Orden de comprobaciones: activación 400 → 404 → 409 → 403; lecturas 404 → 400 → 409 → descarte | Aplicada (Fase 3) |
| 25 | (D8) `loadingEndDateTime` es también la estampa de los últimos valores (sin campo duplicado) | Aplicada (Fase 3) |
| 26 | (D9) La activación no agrega registros al historial de estados | Aplicada (Fase 3) |
| 27 | (D10) `IOrderDetailBusiness` aparte; la activación va en `IOrderBusiness` | Aplicada (Fase 3) |
| 28 | (Decisión 11 de la división) `GET /orders/{orderNumber}/details` paginado, orden fijo por `id`, tope de 100 | Aplicada (Fase 3; aprobada por Tomás Calvo el 27/10/2026) |
| 29 | Solo se cierra desde el estado 2; los estados 1, 3 y 4 dan 409; cerrar dos veces **no** es idempotente | Aplicada (Fase 4) |
| 30 | El cierre no exige activación ni lecturas previas: alcanza con el estado 2 | Aplicada (Fase 4); **confirmada el 02/11/2026**: se deja así (la consigna no exige activación ni lecturas para cerrar) |
| 31 | El cierre busca la orden con `findByOrderNumberForUpdate` (`PESSIMISTIC_WRITE`) para serializarse con las lecturas | Aplicada (Fase 4) |
| 32 | Una fila 2→3 en `order_status_logs` (usuario del token, "Orden cerrada para carga") y 200 con la orden; sin campos nuevos ni cambios en fechas de carga o últimos valores | Aplicada (Fase 4) |
| 33 | (D1) Pesaje final solo desde el estado 3; 1, 2 y 4 dan 409; no es idempotente | Aplicada (Fase 5) |
| 34 | (D2) Orden 404 → 400 (peso ausente, no finito o ≤ 0) → 409 (estado) → 400 (menor que la tara); peso igual a la tara válido | Aplicada (Fase 5) |
| 35 | (D3) Pesaje final con `findByOrderNumberForUpdate`, comprobaciones fuera del `try`, `rollbackFor` y `saveAndFlush`; el GET no bloquea | Aplicada (Fase 5) |
| 36 | (D4) Se guardan solo `finalWeight` y `finalWeighingDateTime` (hora del servidor); la conciliación se calcula con una sola función | Aplicada (Fase 5) |
| 37 | (D5) Producto cargado = última masa; neto = final − tara; diferencia = neto − producto; `AVG` simple; sin redondeo | Aplicada (Fase 5) |
| 38 | (D6) Orden sin lecturas: producto cargado 0.0 y promedios `null`; no bloquea el pesaje final | Aplicada (Fase 5) |
| 39 | (D7) El POST responde 200 con la conciliación; el GET solo en estado 4 (otros 409) | Aplicada (Fase 5) |
| 40 | (D8) Una fila 3→4 con el usuario y "Pesaje final registrado"; el estado 4 es final | Aplicada (Fase 5) |
| 41 | (D9) Lógica en `OrderBusiness` con `OrderDetailRepository` directo (evita la dependencia circular); POST en `OrderTmsRestController`, GET en `OrderRestController` | Aplicada (Fase 5) |
| 42 | (D10) Mensajes con el estilo previo; Postman compara fragmentos | Aplicada (Fase 5) |
| 43 | (D11) Carpetas Postman 05 y 06 con variables propias; las 00 a 04 no se tocan | Aplicada (Fase 5) |
| 44 | (D12) Fuera de alcance: frecuencia de almacenamiento, roles, alarmas, frontend y seguridad | Vigente |
| 45 | (D13) La tara rechaza un peso infinito con 400 (`requireValidWeight`, compartido con el pesaje final) | Aplicada (Fase 5; arreglo de la Fase 2, H-26) |
| 46 | (D14) `activate` busca la orden con bloqueo de fila | Aplicada (Fase 5; arreglo de la Fase 3, H-25 y H-16) |
| 47 | `/demo/**` deja de ser público: `GET /demo/encodepass` exige token | Aplicada (cierre del parcial, 02/11/2026; parte de H-5) |
| 48 | El chequeo del deploy sondea `/v3/api-docs` y exige HTTP 200 | Aplicada (cierre del parcial, 02/11/2026; parte de H-5) |
| 49 | No se implementa la frecuencia de almacenamiento configurable (opcional del parcial): se guardan todas las lecturas válidas, como permite la consigna | Vigente (decisión del usuario, 02/11/2026) |
| 50 | Swagger completo: `@Operation` en los 13 endpoints, 401 y 500 en los protegidos, 404 donde corresponde; carpeta Postman 07 para `/demo/encodepass` | Aplicada (cierre del parcial, 02/11/2026; resuelve H-27 y H-28) |

## 10. Hallazgos y deuda abierta

| ID | Tema | Estado |
|---|---|---|
| H-5 | Seguridad heredada: clave JWT fija en `AuthConstants`, credenciales de base por defecto en los `.properties` (incluso `mysqlprod` trae una contraseña por defecto escrita en el archivo), CORS abierto. `dev.info.enabled` (trazas en las respuestas de error) es `true` en el perfil por defecto y en `mysqldev`, y `false` en `mysqlprod`. Resueltos el 02/11/2026: `/demo/**` público (decisión 47) y el chequeo del *workflow* de deploy sobre `/api/v1/products` (decisión 48) | **Parcialmente resuelto en la Fase 3 (12/11/2026):** la clave JWT y los orígenes CORS salen de `JWT_SECRET` y `CORS_ALLOWED_ORIGINS`. **Abierto:** definir esas variables en la VM (hasta entonces rigen el valor del repositorio y CORS abierto), y las credenciales de base por defecto de los `.properties` |
| H-9 | `pom.xml` desalineado con la cátedra; `actuator` y `h2` sin uso | Decisión 6: no se cambia |
| H-12 | Concurrencia en taras simultáneas sobre la misma orden: dos peticiones concurrentes podrían pasar la comprobación de estado | Sigue aceptado para el parcial (análogo a N-2 de Fase 1; la Fase 5 solo cambió el chequeo del peso) |
| H-14 | Una falla de base al confirmar la activación respondía 500 con el formato de Spring, no `StandartResponse` (`activate` usaba `save`) | Resuelto (27/10/2026): `saveAndFlush` en `OrderBusiness.activate` (`4ed54c8`), verificado |
| H-15, H-16 | H-15 (lecturas simultáneas: deadlock y masa anterior vieja) **resuelto** con bloqueo pesimista de la orden (F-01); H-16 (activaciones simultáneas de la misma orden) **resuelto** como efecto colateral de D14 (verificado: un solo `UPDATE` de `activation_date_time`) | Corregidos |
| H-17, H-18 | Contraseña de 5 dígitos sin límite de intentos; token de 1 h en cargas largas | Pendiente (seguridad del final) |
| H-19, H-20 | `timestamp` con `Z` / estampas mezcladas: **resueltos** al usar siempre la hora de recepción del servidor (F-13) | Corregidos |
| H-21, H-22 | Jackson acepta números como texto; el 500 de `BusinessException` sale con `message: null` | Observación; no se cambia |
| H-23 | Un `orderNumber` no numérico en la URL da 400 con el formato de Spring, no `StandartResponse` (todos los endpoints con `{orderNumber}`) | Observación; no se cambia |
| H-24 | `loadForUpdate` duplicado en `OrderBusiness` y `OrderDetailBusiness` | Deuda menor; unificar con acuerdo del equipo |
| H-25 | `activate` buscaba sin bloqueo: una primera activación simultánea con el cierre podía reescribir el estado 2 (solo posible por la decisión 30) | **Resuelto** (Fase 5, D14): `loadForUpdate` en `activate`; 20 de 20 activaciones simultáneas con el cierre terminaron en estado 3 |
| H-26 | `POST …/tare` con `1e999` respondía 500 con el formato de Spring | **Resuelto** (Fase 5, D13): 400 «weight debe ser un número finito» |
| H-27 | Swagger incompleto fuera de lo nuevo: login y `/demo/encodepass` sin `@Operation`; `GET /orders/{n}` y `/status-log` no declaran el 404; solo el cierre declara 401 y 500 | **Resuelto** (02/11/2026, decisión 50): anotaciones en los 13 endpoints |
| H-28 | `GET /demo/encodepass` (demo de la cátedra) no tiene pedido en Postman | **Resuelto** (02/11/2026): carpeta 07 (sin token 401, con token 200) |
| H-29 | `registerTare` guarda con `save` y no con `saveAndFlush` (lección de H-14) | Deuda menor; fuera del alcance de D13 |
| N-2 | Dos altas simultáneas del mismo `orderNumber`: una responde 500 en lugar de 302 | Aceptado: se deja el 500 (decisión del usuario, 19/10/2026) |
| N-3 | `GET /orders` sin paginación | Resuelto (19/10/2026): `page` y `size`, respuesta con `PagedModel` |
| N-4 | Las tablas del demo siguen en las bases ya creadas | Inofensivo |
| Roles | Sin autorización por rol; el `User` que arma el filtro JWT no trae roles | **Resuelto** en `final-fase-1-roles` (12/11/2026): `@PreAuthorize` en los 12 endpoints protegidos y 403 con JSON estándar. Las autoridades viajan en el `Authentication`, no en el `User` |
| Clase 9 | Eventos, `EmailBusiness`, `Scheduler` y `@PreAuthorize` | **Incorporados** eventos, `EmailBusiness` (con el paquete corregido a `business.exception`) y `@PreAuthorize` (Fases 1 y 2 del final). Sin usar: `Scheduler` (solo haría falta para reintentar correos). No se adoptó el multicaster asíncrono global de la cátedra: rompe `@TransactionalEventListener` |

## 11. Cómo ponerlo en marcha en tu carpeta

El repo es **autocontenido**: no usa rutas absolutas ni depende de la carpeta de la cátedra.

**Requisitos:** Git, **JDK 21**, Docker (Docker Desktop en Windows y macOS) y, de forma opcional, Node.js 18+
para correr Postman por consola y regenerar los PDF. Maven no hace falta: se usa `mvnw`.

```bash
git clone https://github.com/joacoContreras/iw3-gas-terminal-backend.git
cd iw3-gas-terminal-backend
docker compose up -d                      # MySQL 8.0: localhost:3306, base iw3_db, root/root (solo desarrollo)
./mvnw spring-boot:run                    # Windows: mvnw.cmd spring-boot:run  ->  http://localhost:8080
```

**Usuario de prueba** (una vez, con la app ya iniciada para que Hibernate cree las tablas):

```bash
docker exec -i iw3-gas-mysql mysql -uroot -proot iw3_db < docs/sql/seed-dev-users.sql
# PowerShell: Get-Content docs/sql/seed-dev-users.sql | docker exec -i iw3-gas-mysql mysql -uroot -proot iw3_db
```

**Comprobar que anda:**

- `POST http://localhost:8080/api/v1/login` con `username=admin` y `password=admin123` devuelve un JWT.
- Swagger: `http://localhost:8080/swagger-ui.html` (botón *Authorize* con el JWT).
- Postman: importar `docs/postman/IW3-Gas-Terminal.postman_collection.json` y ejecutar primero la carpeta
  `00 - Autenticación`. Por consola: `npx newman@6 run docs/postman/IW3-Gas-Terminal.postman_collection.json`.
- Tests de Maven: `./mvnw test` (la base debe estar arriba).

**Problemas frecuentes**

| Síntoma | Causa | Solución |
|---|---|---|
| `container name "/iw3-mysql" is already in use` | Tenés el contenedor de la cátedra (o un clon anterior a la Fase 1) | `git pull` en `main`: el contenedor del TP es `iw3-gas-mysql` |
| `Communications link failure` o `Connection refused` al arrancar | MySQL no está arriba | Iniciar Docker, `docker compose up -d` y esperar ~20 s la primera vez |
| `port is already allocated` (3306) | Otro MySQL local | Detenerlo, o cambiar el mapeo del compose y la URL de `application.properties` |
| 401 en todo | Falta el usuario de prueba o el token | Correr el seed, loguearse y enviar `Authorization: Bearer <jwt>` |
| `release version 21 not supported` | JDK menor a 21 | Instalar JDK 21 y revisar `JAVA_HOME` |
| `Permission denied: ./mvnw` (Linux y macOS) | Falta el bit de ejecución | `chmod +x mvnw` |
| El IDE no compila (Lombok) | Sin *annotation processing* | Activarlo en el IDE o compilar con `./mvnw` |
| Dice "Perfil activo default" usando `-P` | El `-P` de Maven no activa perfiles de Spring | `-Dspring-boot.run.profiles=<perfil>` |
| La base conserva columnas viejas | `ddl-auto=update` no borra | `docker compose down -v` y volver a levantar (borra también el seed) |

**Con Claude Code:** abrir la terminal en la **raíz del repo** y ejecutar `claude`; este archivo se carga
solo. Primer mensaje sugerido: *"Leé CLAUDE.md y docs/plan-de-trabajo.md y decime en qué fase estamos y
qué falta"*. La memoria personal de Claude **no** se comparte entre integrantes: la fuente común es este
archivo. Antes de seguir, `git pull`. Para trabajar en paralelo: una persona por fase y por rama, y no editar
a la vez `CLAUDE.md` ni `docs/plan-de-trabajo.md`.

## 12. Comandos útiles

```bash
./mvnw -B -DskipTests compile            # compilar
./mvnw -B test                           # tests (necesita MySQL)
./mvnw -B -DskipTests package            # genera target/ROOT.war (perfil war-build por defecto)
java -jar target/ROOT.war --spring.profiles.active=mysqlprod   # como producción; usar SPRING_DATASOURCE_*
docker exec iw3-gas-mysql mysql -uroot -proot iw3_db -e "SELECT order_number, status FROM orders;"
docker compose down                      # detiene la base y conserva los datos
```

Limpiar los datos de prueba del dominio (no toca `users` ni `roles`):

```sql
SET FOREIGN_KEY_CHECKS=0;
DELETE FROM order_status_logs; DELETE FROM orders; DELETE FROM truck_tanks; DELETE FROM trucks;
DELETE FROM drivers; DELETE FROM customers; DELETE FROM gas_products;
SET FOREIGN_KEY_CHECKS=1;
```

## 13. Despliegue en la VM

- La VM (Ubuntu con Docker) corre cuatro contenedores: `iw3-app` (backend), `iw3-frontend`, `iw3-mysql-prod` y `nginx`.
- **Incidente del 14/10/2026:** `iw3-app` ejecutaba una imagen construida con un commit anterior a
  `344cc78`, donde `AuthRestController` ya existía pero `SecurityConfiguration` todavía no definía el bean
  `AuthenticationManager`. Fallaba al arrancar y la política `unless-stopped` lo reinició 334 veces: CPU al
  100 %. Se cortó con `docker update --restart=no iw3-app` y `docker stop iw3-app`.
- **Hecho:** el backend del TP (Fase 1) ya está desplegado en la VM (informado por el usuario, 19/10/2026).
- **Cómo desplegar:**
  - Construir una imagen nueva desde la rama aprobada.
  - Variables `SPRING_PROFILES_ACTIVE=mysqlprod` y `SPRING_DATASOURCE_URL`, `_USERNAME`, `_PASSWORD`;
    **no** depender de los valores por defecto del repo (H-5).
  - El host de la base debe ser el nombre del servicio MySQL en la misma red de Docker.
  - Política de reinicio `on-failure:3`, no `unless-stopped`.
  - Crear un usuario y sus roles en la base de producción (el seed es solo de desarrollo).
- **Usuario de prueba en la VM (02/11/2026):** la base de producción no tenía usuarios; se creó `martin` con `ROLE_ADMIN` a mano (las tres columnas `account_*` hay que fijarlas en 1: su valor por defecto es 0 y con 0 el login falla). El hash BCrypt se generó fuera de la VM, con `htpasswd -nbBC 10` (devuelve `$2y$`; Spring lo acepta, y se puede cambiar por `$2a$`). Las contraseñas de la VM y de su base **no** se guardan en el repo.
- **Datos de prueba que quedaron en la base de la VM:** la orden `97001` (estado 4) y el camión, chofer, cliente y producto `VM-*`.
- Sin base accesible, la app muere en ~15 s con código 1 y consume mucha CPU: con reinicio automático es un bucle.
- El TP usa la tabla `gas_products`, por lo que no choca con la `products` del demo de la base de producción.

## 14. Mapa de documentos

| Documento | Contenido |
|---|---|
| `CLAUDE.md` | Este archivo: contexto, estado y reglas |
| `docs/plan-de-trabajo.md` | Alcance, estados, decisiones, modelo y endpoints propuestos, fases, forma de trabajo |
| `docs/fase-0-auditoria.md` | Informe de la Fase 0 y guía de estudio |
| `docs/fase-1-recepcion-ordenes.md` | Informe de la Fase 1 y guía de estudio |
| `docs/fase-2-pesaje-inicial.md` | Informe de la Fase 2 y guía de estudio |
| `docs/fase-3-carga-tiempo-real.md` | Informe de la Fase 3, auditoría final y guía de estudio |
| `docs/fase-4-cierre-orden.md` | Informe de la Fase 4 y guía de estudio |
| `docs/fase-4-guia-de-inicio.md` | Alcance, plan y prompt de la Fase 4 (Benjamín Alasino) |
| `docs/fase-5-conciliacion.md` | Informe de la Fase 5, auditoría final del segundo parcial y guía de estudio |
| `docs/final-fase-1-roles.md` | Examen final, Fase 1: roles, matriz por endpoint, pruebas y guía de defensa |
| `docs/final-fase-2-alarmas.md` | Examen final, Fase 2: alarma de temperatura por e-mail, decisiones, pruebas, defectos hallados y guía de defensa |
| `docs/final-fase-3-cierre-backend.md` | Examen final, Fase 3: CORS (H-30), 401 sin `WWW-Authenticate`, filtros y orden del listado, H-5, pruebas con Chrome real y guía de defensa |
| `docs/contrato-api.md` | **Contrato de la API congelado** (generado desde `/v3/api-docs` y respuestas reales; para el frontend) |
| `docs/fase-2-division-de-trabajo.md`, `docs/fase-3-division-de-trabajo.md`, `docs/fase-5-division-de-trabajo.md` | División del trabajo, prompts y registro de cada parte |
| `docs/guia-estudio-src.md` | Mapa y ayuda memoria del código de `src` (se actualiza en cada fase) |
| `docs/pdf/` | PDF de las fases y script para regenerarlos |
| `docs/pdf/consigna-trabajo-integrador-2026.pdf` | **Consigna oficial del trabajo integrador** (referencia; no se regenera) |
| `docs/postman/` | Colección Postman (carpetas 00 a 07; la 06 es el circuito completo con *Tests* y la 07 prueba el demo de la cátedra) |
| `docs/sql/seed-dev-users.sql` | Usuarios de prueba, uno por rol (solo desarrollo; contraseña `<usuario>123`) |
| `README.md` | Descripción general y base de datos |

**Material de la cátedra** (fuera de este repo, solo lectura): `github.com/magm3333/iw3_2026`, con un commit por
clase: Spring Boot y Maven (1), JPA (2), REST y excepciones (3), perfiles (4), herencia y multi-cliente (6),
serializadores y consultas (7), JWT (8), eventos, mail, *scheduler* y `@PreAuthorize` (9). La
`IW3_Guia_Estudio_Clases.pdf` que circula entre nosotros es un **resumen personal** armado desde esos commits,
no un documento oficial. La cátedra no tiene ejemplos de Swagger, Postman, Bean Validation ni DTO: en esos
puntos la explicación es **complementaria** y así se marca en las guías.

## 15. Protocolo al iniciar una sesión

1. Leer este archivo, `docs/plan-de-trabajo.md` y el documento de la fase en curso.
2. `git status` y `git branch --show-current`: confirmar rama y árbol limpio. `git pull` si corresponde.
3. Preguntar qué se hace. **No empezar una fase sin aprobación explícita.**
4. Antes de probar, verificar el entorno: Docker, contenedor `iw3-gas-mysql`, usuario de prueba, puerto 8080 libre.
5. Probar de verdad (compilar, ejecutar, llamar a los endpoints) y reportar solo lo que se ejecutó.
6. Al cerrar una fase: actualizar las secciones 2, 5, 8 y 10 de este archivo, redactar el informe y la guía,
   generar el PDF, presentar el resumen ejecutivo y **esperar la autorización** para continuar.
7. Push y merge a `main` solo si el usuario lo pide y con las aprobaciones acordadas (decisión 14).
