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

### Fase 1 — Recepción y creación de órdenes (completada, **en revisión**, rama `fase-1-recepcion-ordenes`)

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
| `GET /api/v1/orders` | Lista las órdenes (más reciente primero) | 200 |
| `GET /api/v1/orders/{orderNumber}` | Una orden | 200, 404 |
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

| Entidad (clase → tabla) | Notas |
|---|---|
| `Order` → `orders` | `orderNumber` único, camión/chofer/cliente/producto, `plannedLoadDateTime`, `preset`, `status`, `receptionDateTime` |
| `OrderStatusLog` → `order_status_logs` | Estado anterior (`null` al crear) y nuevo, fecha y hora, `actor`, observaciones |
| `Truck`, `TruckTank` → `trucks`, `truck_tanks` | Camión con su cisternado (una fila por cisterna) |
| `Driver`, `Customer`, `Product` → `drivers`, `customers`, `gas_products` | `externalCode` único |
| `User`, `Role` → `users`, `roles`, `userroles` | Autenticación (de la cátedra) |

## 8. Qué falta

### 8.1 Fases del parcial (en orden)

**Fase 2 — Pesaje inicial y activación (Punto 2)** · rama `fase-2-pesaje-inicial`

- Endpoint propuesto: `POST /api/v1/integration/tms/orders/{orderNumber}/tare` con `{ "weight": <kg> }`.
- Reglas: la orden existe (404) y está en estado 1; `weight > 0` (400); guarda la tara y la fecha y hora
  del pesaje inicial; **genera una contraseña entera de 5 dígitos** (10000 a 99999, `SecureRandom`); pasa a
  estado 2 y registra el cambio de estado. Responde la contraseña.
- Estado incorrecto: **409 Conflict** propuesto (confirmar con el equipo; la cátedra no tiene precedente).
- Cambios probables: campos nuevos en `Order` (`tare`, `initialWeighingDateTime`, `activationPassword`),
  `IOrderBusiness`, controller en `integration/tms/controllers`, `Constants.URL_INTEGRATION_TMS`, requests
  de Postman y documentación.

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

- Revisión de la Fase 1 por el equipo y su integración a `main`.
- Confirmar el código HTTP de "estado incorrecto".
- Desplegar el backend del TP en la VM (lo pidió el profesor; ver sección 13).

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

## 10. Hallazgos y deuda abierta

| ID | Tema | Estado |
|---|---|---|
| H-5 | Seguridad heredada: clave JWT fija en `AuthConstants`, credenciales de base por defecto en los `.properties`, `/demo/**` público, CORS abierto, `dev.info.enabled=true` (trazas en las respuestas de error) | Abierto: cerrar en la etapa de seguridad y **antes de cualquier despliegue real** |
| H-9 | `pom.xml` desalineado con la cátedra; `actuator` y `h2` sin uso | Decisión 6: no se cambia |
| N-2 | Dos altas simultáneas del mismo `orderNumber`: una responde 500 en lugar de 302 | Abierto; aceptado para el parcial |
| N-3 | `GET /orders` sin paginación | Abierto |
| N-4 | Las tablas del demo siguen en las bases ya creadas | Inofensivo |
| Roles | Sin autorización por rol; el `User` que arma el filtro JWT no trae roles | Pendiente (final) |
| Clase 9 | Eventos, `EmailBusiness`, `Scheduler` y `@PreAuthorize` no están incorporados. Al copiarlos hay que cambiar `business.execption` por `business.exception` (el TP corrigió el nombre del paquete) | Pendiente (final) |

