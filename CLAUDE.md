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

**Última actualización:** 23/10/2026, tras completar y verificar la Fase 2 en la rama `fase-2-pesaje-inicial`.

| | |
|---|---|
| **Última fase completada** | **Fase 2 — Pesaje inicial y activación (Punto 2): implementada y probada en `fase-2-pesaje-inicial`** (23/10/2026) |
| **Qué sigue** | **Fase 3 — Carga y datos en tiempo real (Punto 3): pendiente.** Requiere mergear Fase 2 y autorización del usuario |
| `main` | Contiene las Fases 0 y 1 completas (`a330219`); recibirá la Fase 2 por PR con 3 aprobaciones |
| Revisión de la Fase 1 | El PR #2 se mergeó sin revisiones registradas en GitHub, pero el equipo **revisó después el código integrado en `main` y está todo bien** (informado por el usuario, 19/10/2026) |
| Despliegue | La Fase 1 **ya está desplegada en la VM**; se redesplegará al integrar la Fase 2 |

| Fase | Contenido | Rama | Estado |
|---|---|---|---|
| 0 | Auditoría y línea base | `fase-0-auditoria` | Completada e integrada en `main` |
| 1 | Recepción de órdenes (Punto 1) | `fase-1-recepcion-ordenes` | Completada e integrada en `main` (PR #2) |
| 2 | Pesaje inicial y activación (Punto 2) | `fase-2-pesaje-inicial` | Completada y probada (espera PR a `main`) |
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
| Segundo parcial (opcional) | *Tests* de Postman que simulen el circuito completo; frecuencia de almacenamiento configurable; promedios (alcanza con el último valor) |
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

### Fase 2 — Pesaje inicial y activación (Punto 2, completada en rama `fase-2-pesaje-inicial`)

- **División del trabajo entre 4 personas:** Etapa A (Martín González: modelo, 409 y generador de claves), Etapa B (Tomás Calvo: regla transaccional `registerTare`), Etapa C (Benjamín Alasino: endpoint TMS) y Etapa D (Joaquín Contreras: Postman, pruebas y documentación).
- **Modelo:** `Order` incorpora `tare` (`Double`), `initialWeighingDateTime` (`LocalDateTime`) y `activationPassword` (`Integer`, con `@JsonIgnore`). Columnas nulables para coexistir con órdenes previas.
- **Excepción nueva:** `ConflictException` (checked) → HTTP 409 Conflict ante orden en estado distinto de 1.
- **Generador seguro:** `ActivationPasswordGenerator` genera enteros de 5 dígitos (10000–99999) con `SecureRandom`.
- **Punto 2:** `OrderBusiness.registerTare` valida existencia (404), peso positivo (400) y estado pendiente de pesaje inicial (409) fuera del `try/catch`. Dentro de `@Transactional(rollbackFor = Exception.class)`, guarda tara, estampa y clave, transiciona al estado 2 y registra auditoría en `order_status_logs`.
- **Endpoint TMS:** `POST /api/v1/integration/tms/orders/{orderNumber}/tare` con DTOs `TareRequest` y `TareResponse`.
- **Postman y pruebas:** Carpeta `02 - Fase 2: pesaje inicial (TMS)` (11 pedidos, 27 aserciones). Corridas con newman 6: 21 pedidos, 46 aserciones ejecutadas con 0 fallas (las 19 de Fase 1 permanecen intactas).
- **Informe y guía:** `docs/fase-2-pesaje-inicial.md` completo con informe técnico y guía de estudio A–J (12 preguntas de defensa con respuestas).

## 6. Arquitectura y convenciones del código

```
Sistema externo ── HTTP + JWT ──▶ Controller ──▶ Business (I*Business) ──▶ Repository ──▶ MySQL
```

| Paquete (bajo `src/main/java/ar/edu/iua/iw3`) | Contenido |
|---|---|
| `controllers` | `BaseRestController` (`getUserLogged()`), `Constants` (URLs), `OrderRestController` |
| `integration/<sistema>/controllers` | Endpoints por sistema externo. Hoy `sap`; más adelante `tms` y `loader` |
| `business`, `business/exception` | `I*Business` + `*Business`, `OrderValidator`; excepciones de negocio |
| `model`, `model/persistence` | Entidades JPA y repositorios Spring Data |
| `auth`, `security` | Usuarios, roles, JWT y reglas de acceso (de la cátedra) |
| `config` | `OpenApiConfig` (Swagger con esquema Bearer) |
| `util` | `StandartResponse` y su servicio (respuesta de error estándar) |

**Convenciones (las de la cátedra, salvo lo marcado):**

- `IXxxBusiness` + `XxxBusiness` con `@Service`, inyección `@Autowired` en el campo, DAOs llamados `xxxDAO`.
- Excepciones *checked* → HTTP: `NotFoundException` 404, `FoundException` **302** (convención de la
  cátedra para duplicados), `BusinessException` 500 y `BadRequestException` **400** (extensión nuestra).
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
- Seguridad: todo exige JWT salvo `POST /api/v1/login`, `/error`, la documentación y `/demo/**`. El token
  es HS512, vence en 1 h y viaja en `Authorization: Bearer <jwt>` (o `?authtoken=`).
- Perfiles de Spring: `mysqldev` y `mysqlprod` (properties). Se activan con
  `-Dspring-boot.run.profiles=<perfil>` o `SPRING_PROFILES_ACTIVE`; el `-P` de Maven **no** los activa (H-3).

## 7. API y modelo actuales

| Método y ruta | Qué hace | Respuestas |
|---|---|---|
| `POST /api/v1/login` | Formulario `username`/`password`; devuelve el JWT en texto | 200, 401 (con JSON) |
| `POST /api/v1/integration/sap/orders` | **Punto 1**: recibe la orden | 201 + `location`, 302, 400, 401, 500 |
| `POST /api/v1/integration/tms/orders/{orderNumber}/tare` | **Punto 2**: registra la tara y devuelve la contraseña de activación | 200, 400, 401, 404, 409, 500 |
| `GET /api/v1/orders?page=&size=` | Lista paginada de órdenes (más reciente primero). `page` desde 0; `size` por defecto 20, máximo 100. Responde `{ content, page: { size, number, totalElements, totalPages } }` | 200 |
| `GET /api/v1/orders/{orderNumber}` | Una orden (no expone `activationPassword`) | 200, 404 |
| `GET /api/v1/orders/{orderNumber}/status-log` | Historial de estados | 200, 404 |
| `GET /demo/encodepass?password=` | Hash BCrypt (demo de la cátedra, **público**, cerrar antes de desplegar) | 200 |
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

| Entidad (clase → tabla) | Notas |
|---|---|
| `Order` → `orders` | `orderNumber` único, camión/chofer/cliente/producto, `plannedLoadDateTime`, `preset`, `status`, `receptionDateTime`, `tare`, `initialWeighingDateTime`, `activationPassword` (con `@JsonIgnore`) |
| `OrderStatusLog` → `order_status_logs` | Estado anterior (`null` al crear) y nuevo, fecha y hora, `actor`, observaciones |
| `Truck`, `TruckTank` → `trucks`, `truck_tanks` | Camión con su cisternado (una fila por cisterna) |
| `Driver`, `Customer`, `Product` → `drivers`, `customers`, `gas_products` | `externalCode` único |
| `User`, `Role` → `users`, `roles`, `userroles` | Autenticación (de la cátedra) |

## 8. Qué falta

### 8.1 Fases del parcial (en orden)

**Fase 2 — Pesaje inicial y activación (Punto 2)** · rama `fase-2-pesaje-inicial`
- **Estado: COMPLETADA** en su rama (23/10/2026). Pendiente: revisión cruzada del equipo y PR a `main` con 3 aprobaciones.

**Fase 3 — Carga y datos en tiempo real (Punto 3)** · rama `fase-3-carga-tiempo-real`

- Activación: `POST /api/v1/integration/loader/activation` con `{ orderNumber, password }`; valida que la
  orden exista, esté en estado 2 y la contraseña sea correcta; devuelve el `preset` y guarda
  `activationDateTime` (decisión 7).
- Detalle: `POST /api/v1/integration/loader/orders/{orderNumber}/details` con masa, densidad, temperatura
  y caudal; solo en estado 2 y con la orden activada.
- Validar densidad `0 < d < 1` (decisión 8) y **descartar** caudal ≤ 0, masa ≤ 0 o masa menor que la anterior.
- Entidad nueva `OrderDetail`; la cabecera de `Order` guarda los últimos valores y su estampa; fechas de
  inicio y fin de carga. Para el parcial alcanza con guardar todos los registros.
- A definir: qué responde un registro descartado (propuesta: 200 indicando que no se guardó) y si la
  activación es idempotente. Llegan datos cada ~1 s: cuidar la eficiencia de la escritura.

**Fase 4 — Cierre de la orden (Punto 4)** · rama `fase-4-cierre-orden`

- `POST /api/v1/integration/loader/orders/{orderNumber}/close`: estado 2 → 3. Desde ahí se rechazan los
  detalles. Rechazar transiciones inválidas (cerrar dos veces, o desde el estado 1).

**Fase 5 — Pesaje final, conciliación y circuito completo (Punto 5)** · rama `fase-5-conciliacion`

- `POST /api/v1/integration/tms/orders/{orderNumber}/final-weight`: estado 3 → 4 y devuelve la conciliación.
- `GET /api/v1/orders/{orderNumber}/reconciliation`: solo para estado 4.
- Promedios con `AVG` sobre los detalles almacenados (decisión 3).
- Swagger completo, Postman consolidado con *Tests* que simulen el circuito 1 → 4, pruebas de integración y
  verificación de los requisitos obligatorios del parcial.

### 8.2 Para el examen final

Roles por endpoint (SAP, TMS, sistema de carga, operadores) · alarma de temperatura por e-mail con umbral y
destinatarios configurables y sin reenvío hasta que se acepte (usar las clases de la cátedra: eventos,
`EmailBusiness`, `Scheduler`) · frontend completo · endurecimiento de seguridad (H-5) · despliegue.

### 8.3 Pendientes transversales

- ~~Revisión de la Fase 1 por el equipo~~ — hecha, sin observaciones (19/10/2026).
- **Proteger `main`** (decisión 14): exigir Pull Request con la aprobación de 3 de los 4 integrantes. Solo el dueño
  del repo (`joacoContreras`, único admin) puede configurarlo. Hoy hay **3 cuentas con permiso de escritura**: para
  llegar a 3 aprobaciones hay que sumar al cuarto integrante. En GitHub el autor del PR **no** cuenta como aprobación.
- Confirmar el código HTTP de "estado incorrecto".
- ~~Desplegar el backend del TP en la VM~~ — hecho (19/10/2026; ver sección 13). Hay que redesplegar al cerrar cada fase.

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
| 3 | Implementar los promedios (`AVG`) | Fase 5 |
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

## 10. Hallazgos y deuda abierta

| ID | Tema | Estado |
|---|---|---|
| H-5 | Seguridad heredada: clave JWT fija en `AuthConstants`, credenciales de base por defecto en los `.properties`, `/demo/**` público, CORS abierto, `dev.info.enabled=true` (trazas en las respuestas de error) | Abierto: cerrar en la etapa de seguridad y **antes de cualquier despliegue real** |
| H-9 | `pom.xml` desalineado con la cátedra; `actuator` y `h2` sin uso | Decisión 6: no se cambia |
| H-12 | Concurrencia en taras simultáneas sobre la misma orden: dos peticiones concurrentes podrían pasar la comprobación de estado | Aceptado para el parcial (análogo a N-2 de Fase 1) |
| N-2 | Dos altas simultáneas del mismo `orderNumber`: una responde 500 en lugar de 302 | Aceptado: se deja el 500 (decisión del usuario, 19/10/2026) |
| N-3 | `GET /orders` sin paginación | Resuelto (19/10/2026): `page` y `size`, respuesta con `PagedModel` |
| N-4 | Las tablas del demo siguen en las bases ya creadas | Inofensivo |
| Roles | Sin autorización por rol; el `User` que arma el filtro JWT no trae roles | Pendiente (final) |
| Clase 9 | Eventos, `EmailBusiness`, `Scheduler` y `@PreAuthorize` no están incorporados. Al copiarlos hay que cambiar `business.execption` por `business.exception` (el TP corrigió el nombre del paquete) | Pendiente (final) |

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

- La VM (Ubuntu con Docker) corre tres contenedores: `iw3-app` (backend), `iw3-mysql-prod` y `nginx`.
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
- Sin base accesible, la app muere en ~15 s con código 1 y consume mucha CPU: con reinicio automático es un bucle.
- El TP usa la tabla `gas_products`, por lo que no choca con la `products` del demo de la base de producción.

## 14. Mapa de documentos

| Documento | Contenido |
|---|---|
| `CLAUDE.md` | Este archivo: contexto, estado y reglas |
| `docs/plan-de-trabajo.md` | Alcance, estados, decisiones, modelo y endpoints propuestos, fases, forma de trabajo |
| `docs/fase-0-auditoria.md` | Informe de la Fase 0 y guía de estudio |
| `docs/fase-1-recepcion-ordenes.md` | Informe de la Fase 1 y guía de estudio |
| `docs/pdf/` | PDF de las fases y script para regenerarlos |
| `docs/postman/` | Colección Postman |
| `docs/sql/seed-dev-users.sql` | Usuario de prueba (solo desarrollo) |
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
