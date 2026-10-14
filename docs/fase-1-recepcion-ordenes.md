# Fase 1 — Recepción y creación de órdenes (Punto 1)

Rama: `fase-1-recepcion-ordenes` · Ejecución: 14/10/2026 · Estado: completada (pendientes en la sección 9).

Este archivo tiene dos partes:

- **Parte 1:** informe técnico de lo implementado y verificado.
- **Parte 2:** guía de estudio para la defensa oral.

Convención de rutas: `SRC` = `src/main/java/ar/edu/iua/iw3`.

---

# Parte 1 — Informe técnico

## 1. Alcance y resultado

La fase implementa el Punto 1 de la consigna: un sistema externo (SAP) envía la orden con su
camión, chofer, cliente y producto; el backend los sincroniza por código externo, crea la
orden en estado 1 y registra el primer cambio de estado.

También aplica las decisiones aprobadas: retiro del demo (1), contenedor `iw3-gas-mysql` (10),
`/error` y login (11) y `getUserLogged()` (12).

| Criterio de aceptación | Resultado |
|---|---|
| Alta de orden: 201 con `location` | Cumplido |
| Número de orden repetido: 302 | Cumplido |
| Datos inválidos: 400 informando todos los errores | Cumplido |
| Entidades sincronizadas por `externalCode` (alta, actualización, cisternado) | Cumplido |
| Orden en estado 1 y log de estados con un registro | Cumplido |
| Atomicidad: una falla al final no deja filas parciales | Cumplido |
| Documentación Swagger/OpenAPI | `/v3/api-docs` responde 200 con las 5 rutas; UI en `/swagger-ui.html` |
| Colección Postman | 10 requests, 19 aserciones, 0 fallas (newman 6) |

## 2. Decisiones de diseño

| Tema | Decisión | Alternativa descartada y motivo |
|---|---|---|
| Entrada | `@RequestBody Order` sobre la entidad, como la cátedra con `Product`. `OrderBusiness.add` copia solo los campos permitidos | DTO de entrada: se aparta del enfoque de la cátedra; el riesgo (que el cliente fije `status` o `id`) se anula copiando campos |
| Estado | Enum `OrderStatus` con `AttributeConverter`: columna entera 1–4 y JSON numérico | `ORDINAL` (0–3) o `STRING`: la consigna habla de "estado 1 a 4" |
| Duplicado | `FoundException` → 302 (decisión 4) | 409 Conflict: contradice la convención de la cátedra |
| Datos inválidos | Nueva `BadRequestException` → 400 | `BusinessException` → 500: un 500 indica falla del servidor, no del cliente |
| Sincronización | Buscar por `externalCode`; actualizar o crear. El cisternado solo se reescribe si cambió | Insertar siempre: duplica camiones y repite altas y bajas de cisternas |
| Transacción | `@Transactional(rollbackFor = Exception.class)` en `add` | Sin transacción (como la cátedra): las excepciones de negocio son *checked* y Spring no revierte por ellas |
| Tabla de producto | `gas_products` | `products`: ya existe en bases creadas con el demo (como la de la VM) con otro esquema |
| Fechas | `LocalDateTime` y `hibernate.jdbc.time_zone` | `java.util.Date`: la cátedra lo usa, pero mezcla instantes y zona (hallazgo H-8) |
| JSON de entrada | Binding directo de Jackson | `JsonUtiles`: descartaba enteros (H-6); se retiró con el demo |
| Documentación | `springdoc-openapi-starter-webmvc-ui` 3.1.1 | No está en el material de la cátedra: es una **explicación complementaria** |

## 3. Archivos de la fase

### 3.1 Creados y modificados

| Archivo | Acción | Motivo | Responsabilidad |
|---|---|---|---|
| `SRC/model/Truck.java`, `TruckTank.java` | Creados | Camión con su cisternado | Entidades |
| `SRC/model/Driver.java`, `Customer.java` | Creados | Chofer y cliente | Entidades |
| `SRC/model/Product.java` | Reemplazado | Producto de gas (tabla `gas_products`); el demo se retiró | Entidad |
| `SRC/model/Order.java` | Creado | Orden de carga: núcleo del proceso | Entidad |
| `SRC/model/OrderStatus.java`, `OrderStatusConverter.java` | Creados | Estados 1 a 4 y su mapeo a columna entera | Modelo |
| `SRC/model/OrderStatusLog.java` | Creado | Auditoría de cambios de estado | Entidad |
| `SRC/model/persistence/{Truck,Driver,Customer}Repository.java` | Creados | Búsqueda por código externo | Acceso a datos |
| `SRC/model/persistence/ProductRepository.java` | Reemplazado | Idem, para `Product` | Acceso a datos |
| `SRC/model/persistence/OrderRepository.java`, `OrderStatusLogRepository.java` | Creados | Órdenes e historial | Acceso a datos |
| `SRC/business/IOrderBusiness.java`, `OrderBusiness.java` | Creados | Lecturas, alta y sincronización | Negocio |
| `SRC/business/OrderValidator.java` | Creado | Validación de la orden recibida | Negocio |
| `SRC/business/exception/BadRequestException.java` | Creado | Datos inválidos → 400 | Excepción |
| `SRC/controllers/OrderRestController.java` | Creado | Consulta de órdenes e historial | API |
| `SRC/integration/sap/controllers/OrderSapRestController.java` | Creado | `POST /integration/sap/orders` | API |
| `SRC/controllers/Constants.java` | Modificado | `URL_ORDERS`, `URL_INTEGRATION_SAP`; se quitan las del demo | Rutas |
| `SRC/controllers/BaseRestController.java` | Modificado | `getUserLogged()` (decisión 12) | API |
| `SRC/security/SecurityConfiguration.java` | Modificado | Permite `/error` (H-1, decisión 11) | Seguridad |
| `SRC/auth/controller/AuthRestController.java` | Modificado | Quita `produces=text/plain` (H-2, decisión 11) | Seguridad |
| `SRC/config/OpenApiConfig.java` | Creado | Título y esquema Bearer JWT de Swagger | Configuración |
| `SRC/Iw3Application.java` | Modificado | Quita los beans del demo | Arranque |
| `src/main/resources/application.properties` | Modificado | `hibernate.jdbc.time_zone` (H-8) | Configuración |
| `pom.xml` | Modificado | Agrega `springdoc`; quita los perfiles `cli1` y `cli2` | Build |
| `docker-compose.yml`, `README.md` | Modificados | Contenedor `iw3-gas-mysql` (H-4, decisión 10) | Infraestructura |
| `docs/postman/IW3-Gas-Terminal.postman_collection.json` | Creado | Pruebas de la API | Pruebas |
| `docs/fase-1-recepcion-ordenes.md` | Creado | Este informe y la guía | Documentación |

### 3.2 Eliminados (30 archivos, decisión 1)

| Carpeta (bajo `SRC` salvo indicación) | Archivos |
|---|---|
| `integration/cli1/**` | 6 (controller, servicio, interfaz, deserializador, repositorio, entidad) |
| `integration/cli2/**` | 9 (controller, servicio, interfaz, repositorio, proyección, 2 serializadores, 2 entidades) |
| `business` | 4 (`IProductBusiness`, `ProductBusiness`, `ICategoryBusiness`, `CategoryBusiness`) |
| `controllers` | 1 (`ProductRestController`) |
| `model`, `model/persistence` | 2 (`Category`, `CategoryRepository`; `Product` y `ProductRepository` se reemplazaron) |
| `config/profile` | 4 (`ScanConfig` de cli1, cli2, mysqldev y mysqlprod) |
| `util` | 1 (`JsonUtiles`) |
| `src/main/resources` | 2 (`application-cli1.properties`, `application-cli2.properties`) |
| `src/test/.../datasets` | 1 (`add_postman.csv`) |

Los archivos retirados siguen en el historial de git (rama `main`, commit `c3b62de`).

## 4. Recorrido de la petición `POST /api/v1/integration/sap/orders`

| Paso | Componente | Qué hace |
|---|---|---|
| 1 | `JWTAuthorizationFilter.doFilterInternal` | Valida el JWT y deja el usuario en el `SecurityContextHolder` |
| 2 | `SecurityConfiguration` (`anyRequest().authenticated()`) | Sin token válido responde 401 |
| 3 | `DispatcherServlet` + Jackson | Convierte el JSON en `Order` (con `Truck`, `Driver`, `Customer`, `Product`). JSON ilegible: 400. `Content-Type` ajeno: 415 |
| 4 | `OrderSapRestController.add` | Obtiene el usuario con `getUserLogged()` y llama al negocio |
| 5 | `OrderValidator.validate` | Junta todos los errores de entrada; si hay, `BadRequestException` |
| 6 | `OrderBusiness.findByNumber` | Si el número ya existe, `FoundException` |
| 7 | `OrderBusiness.syncTruck/Driver/Customer/Product` | Busca por `externalCode`, actualiza o crea, y guarda |
| 8 | `OrderRepository.save` | `INSERT` en `orders` con estado 1 y fecha de recepción |
| 9 | `OrderBusiness.logStatus` | `INSERT` en `order_status_logs` (de `null` a 1, con el actor) |
| 10 | `OrderSapRestController.add` | Responde 201 con `location: /api/v1/orders/{orderNumber}`, sin cuerpo |

Los pasos 5 a 9 ocurren en una sola transacción. Ante `FoundException` o `BadRequestException`
no se escribió nada; ante cualquier otra falla se revierte todo.

## 5. Funciones y métodos relevantes

**`OrderSapRestController.add`** — `SRC/integration/sap/controllers/OrderSapRestController.java`

- *Responsabilidad:* traducir la petición HTTP del sistema externo en una llamada al negocio y el resultado en un código HTTP.
- *Recibe:* `@RequestBody Order order`. *Devuelve:* `ResponseEntity<?>`: 201 con `location`, o un `StandartResponse` de error.
- *Usa:* `getUserLogged()` (de `BaseRestController`), `IOrderBusiness.add` e `IStandartResponseBusiness.build`.
- *Errores:* `BadRequestException` → 400; `FoundException` → 302; `BusinessException` → 500.

**`OrderBusiness.add`** — `SRC/business/OrderBusiness.java`

- *Responsabilidad:* regla del Punto 1: validar, rechazar repetidos, sincronizar entidades, crear la orden en estado 1 y auditar.
- *Recibe:* `Order order` y `String actor`. *Devuelve:* la `Order` persistida (con `id`, estado y fecha de recepción).
- *Validaciones:* `OrderValidator.validate`; número de orden no repetido.
- *Datos:* lee `orders` y las cuatro tablas externas; escribe en `trucks`, `truck_tanks`, `drivers`, `customers`, `gas_products`, `orders` y `order_status_logs`.
- *Detalle:* crea una `Order` nueva y copia solo `orderNumber`, `plannedLoadDateTime`, `preset` y las entidades sincronizadas; `status` y `receptionDateTime` los fija el backend.
- *Errores:* `BadRequestException`, `FoundException`, `BusinessException` (cualquier otra falla, con rollback).

**`OrderValidator.validate`** — `SRC/business/OrderValidator.java`

- *Responsabilidad:* revisar la orden recibida sin tocar la base.
- *Recibe:* `Order`. *Devuelve:* `List<String>` con un mensaje por problema (vacía si es válida).
- *Reglas:* `orderNumber > 0`; `plannedLoadDateTime` presente; `preset > 0`; camión con código y patente, al menos una cisterna con número y capacidad `> 0` y sin números repetidos; chofer con código, nombre y apellido; cliente con código y razón social; producto con código y nombre; largos acotados a la columna.

**`OrderBusiness.syncTruck`** (y `syncDriver`, `syncCustomer`, `syncProduct`)

- *Responsabilidad:* aplicar el código externo como clave de integración.
- *Detalle:* `findByExternalCode(code.trim())`; si no existe crea una entidad nueva; copia los atributos del cliente (nunca su `id`) y guarda. En el camión compara el cisternado con `sameTanks` y lo reemplaza solo si cambió.

**`OrderBusiness.logStatus`** — registra `OrderStatusLog(order, from, to, ahora, actor, observaciones)`; si el actor llega vacío usa `"sistema"`.

**`OrderStatusConverter`** — `SRC/model/OrderStatusConverter.java`: `convertToDatabaseColumn` guarda el número 1–4 y `convertToEntityAttribute` lo vuelve a enum. Con `autoApply = true` rige para todo atributo `OrderStatus`.

**`BaseRestController.getUserLogged`** — devuelve el `User` del `SecurityContextHolder` (el que arma `JWTAuthorizationFilter`) o `null`. De ahí sale el actor del log.

## 6. Pruebas y verificación

Todo se ejecutó el 14/10/2026 contra MySQL real (`iw3-gas-mysql`), con el usuario `admin` del seed.

| # | Verificación | Resultado |
|---|---|---|
| 1 | `./mvnw -B -q -DskipTests compile` en cada paso | `BUILD SUCCESS` |
| 2 | Arranque (`spring-boot:run`) | `Started Iw3Application in 13,1 s`; Hibernate creó `trucks`, `truck_tanks`, `drivers`, `customers`, `gas_products`, `orders`, `order_status_logs` |
| 3 | `POST` sin token | 401 |
| 4 | `POST` válido con `preset` entero (15000) | 201, `location: /api/v1/orders/90001` |
| 5 | `GET /orders/90001` | 200; `status: 1`; camión con 2 cisternas; fecha de recepción asignada |
| 6 | `GET /orders/90001/status-log` | 1 registro: `fromStatus: null`, `toStatus: 1`, `actor: admin` |
| 7 | Mismo `POST` otra vez | 302 con `StandartResponse` |
| 8 | Orden con campos inválidos | 400 con los 10 errores en un solo mensaje |
| 9 | JSON malformado, fecha inválida, `Content-Type` erróneo | 400, 400, 415 |
| 10 | Orden inexistente, número no numérico, ruta inexistente | 404, 400, 404 |
| 11 | Login con contraseña incorrecta | 401 **con** `StandartResponse` |
| 12 | Sincronización: mismo camión con otra patente y mismas cisternas | 201; 1 camión, patente actualizada, cisternas con los mismos ids |
| 13 | Sincronización: mismo camión con 3 cisternas | 201; cisternas reemplazadas (3 filas nuevas) |
| 14 | Sincronización: cliente con razón social distinta | Una sola fila de cliente, con la razón social nueva |
| 15 | Atomicidad: descripción de producto de 300 caracteres (columna de 255) | 500 y **0 filas** de camión, chofer, cliente, producto y orden |
| 16 | `GET /v3/api-docs` y `/swagger-ui.html` | 200 con las 5 rutas; 302 a `/swagger-ui/index.html` |
| 17 | `newman run` de la colección | 10 requests, 19 aserciones, 0 fallas |

La colección ejecutada con newman es idéntica byte a byte a `docs/postman/IW3-Gas-Terminal.postman_collection.json`.

### Fechas: medición del hallazgo H-8

| Caso | JVM | URL | `hibernate.jdbc.time_zone` | En la base | En el JSON |
|---|---|---|---|---|---|
| Corrida inicial | Argentina | dev | no | 17:49:33 | 14:49:33 |
| B | UTC | dev | no | 17:51:57 | 14:51:57 |
| C | UTC | prod | no | 17:52:20 | 14:52:20 |
| D | Argentina | dev | sí | 14:52:42 | 14:52:42 |
| E | UTC | dev | sí | 14:53:04 | 14:53:04 |
| F | UTC | prod | sí | 14:53:26 | 14:53:26 |

Sin la propiedad, MySQL recibía la hora en UTC (3 h adelantada) aunque el JSON se viera bien.
Con la propiedad, la base guarda la hora de Argentina en los seis escenarios.

## 7. Cambios de comportamiento respecto de la Fase 0

| Petición | Fase 0 | Fase 1 |
|---|---|---|
| JSON malformado con token válido | 401 sin cuerpo | 400 |
| `Content-Type` erróneo | 401 sin cuerpo | 415 |
| Ruta inexistente | 401 sin cuerpo | 404 |
| Login con contraseña incorrecta | 401 sin cuerpo | 401 con `StandartResponse` |
| `/api/v1/products` y `/categories` | 200 | 404 (retirados) |
| Fechas en la base | UTC | Hora de Argentina |

## 8. Hallazgos y limitaciones de la fase

| ID | Hallazgo | Estado |
|---|---|---|
| N-1 | `OrderValidator.validate` no invocaba la validación de chofer, cliente y producto: al partir el commit se perdieron las tres llamadas. Lo detectó la prueba del 400 | Corregido (`fix(business)`) |
| N-2 | Dos `POST` simultáneos con el mismo número de orden: uno viola la restricción única y responde 500 en lugar de 302 | Pendiente: se acepta para el parcial |
| N-3 | `GET /api/v1/orders` devuelve todas las órdenes, sin paginación | Pendiente: a revisar con el frontend |
| N-4 | Las tablas del demo (`products`, `categories`, `cli1_products`, `cli2_*`) siguen en las bases ya creadas; `ddl-auto=update` no borra | Inofensivo: el TP usa `gas_products` |
| N-5 | Las respuestas de error pesan unos 15 KB porque `dev.info.enabled=true` incluye la traza (H-5) | Pendiente para la etapa de seguridad |
| N-6 | La ejecución con newman usó `npx newman@6` (descarga a la caché de npm; no se agrega al proyecto) | Informativo |

Sin cambios: H-5 (secretos, `/demo/**`, CORS abierto) y H-9 (desalineación del `pom.xml`, decisión 6).

## 9. Pendientes y criterios de la fase

| Pendiente | Cuándo |
|---|---|
| Código HTTP para "estado incorrecto" (propuesta: 409 o 400) | Fase 2 |
| Roles por endpoint (SAP, TMS, sistema de carga) | Examen final |
| Subir la rama y abrir la integración a `main` | Cuando se pida |
| Desplegar en la VM: imagen nueva desde esta rama y variables `SPRING_DATASOURCE_*` | Por definir |

| Criterio | Estado |
|---|---|
| Funcionalidad implementada | Cumplido |
| Metodología de la cátedra respetada (capas, `I*Business`, excepciones, `StandartResponse`, `Constants`) | Cumplido; desvíos justificados en la sección 2 |
| Verificación con pruebas ejecutadas | Cumplido (sección 6) |
| Archivos documentados | Cumplido (sección 3) |
| Funciones explicadas | Cumplido (sección 5) |
| Recorrido de los datos | Cumplido (secciones 4 y E) |
| Guía de estudio A–J con preguntas y respuestas | Cumplido |
| Limitaciones registradas | Cumplido (sección 8) |

---

# Parte 2 — Guía de estudio de la Fase 1

## A. Objetivo de la fase

El Punto 1 es la **puerta de entrada** del circuito: sin una orden creada no hay tara, ni carga,
ni cierre, ni conciliación. SAP envía los datos base y el backend debe:

1. Aceptar solo datos válidos y rechazar un número de orden repetido.
2. Sincronizar camión, chofer, cliente y producto usando el **código externo** como clave de
   integración (la consigna lo pide como `string` para no depender de ids internos).
3. Crear la orden en estado 1 y dejar registrado quién y cuándo la recibió.

Además prepara lo que usan todas las fases siguientes: el modelo, el log de estados y la
documentación Swagger y Postman.

## B. Conceptos teóricos involucrados

| Concepto | Clase | Dónde está en el TP |
|---|---|---|
| Entidad JPA, `@ManyToOne`, `@OneToMany` | 2 y 6 | `SRC/model/Order.java`, `SRC/model/Truck.java` |
| Interfaz + implementación + `@Autowired` | 2 | `IOrderBusiness` y `OrderBusiness` |
| Excepciones de dominio traducidas a HTTP (404, 302, 500) | 3 | `OrderSapRestController.add` |
| Código externo único como clave de integración (`codCli1`) | 6 | `externalCode` de `Truck`, `Driver`, `Customer`, `Product` |
| Paquete de integración por sistema externo | 6 | `SRC/integration/sap/controllers` |
| Recepción de JSON de un sistema externo | 7 | `@RequestBody Order` (la cátedra usó un deserializador propio) |
| Usuario autenticado en el controller | 8 y 9 | `BaseRestController.getUserLogged` |

**Explicación complementaria (no vista en clase):**

- *`@Transactional` y `rollbackFor`:* una transacción agrupa varias escrituras. Spring revierte
  por defecto solo ante excepciones *unchecked*; las de negocio son *checked*, por eso se indica
  `rollbackFor = Exception.class`.
- *`AttributeConverter`:* traduce un tipo Java (el enum) a un valor de columna (el entero).
- *OpenAPI / springdoc:* genera la especificación a partir de los controllers y anotaciones.
- *`LocalDateTime` y zona horaria:* no lleva zona; el driver la aplica al hablar con MySQL.

## C. Arquitectura y organización del código

```
SAP ── HTTP + JWT ──▶ OrderSapRestController      integration/sap/controllers
                          │  Order (JSON → entidad)
                          ▼
                      OrderBusiness (IOrderBusiness)         business
                       ├─ OrderValidator            reglas de entrada
                       ├─ sync* (Truck, Driver, Customer, Product)
                       └─ logStatus
                          ▼
                      Repositorios Spring Data               model/persistence
                          ▼
                      MySQL: orders, order_status_logs, trucks, truck_tanks,
                             drivers, customers, gas_products
```

| Pieza | Responsabilidad | Por qué está ahí |
|---|---|---|
| `OrderSapRestController` | HTTP ↔ negocio: código de estado y `location` | Es la única capa que conoce HTTP |
| `OrderValidator` | Reglas de la entrada | Separa "es válida" de "se guarda"; se prueba sin base |
| `OrderBusiness` | Sincroniza, crea y audita en una transacción | Las reglas del proceso van en negocio, no en el controller |
| `*Repository` | Acceso a datos por código externo | Spring Data genera las consultas |
| `OrderStatus` + converter | Estados 1 a 4 | Un solo lugar define qué estados existen |
| `OrderStatusLog` | Auditoría | La consigna pide registrar cada cambio de estado |
