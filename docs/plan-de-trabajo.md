# Plan de trabajo — TP Integrador Ingeniería Web III (2026)

Backend para la administración de órdenes de carga de gas líquido.
Este documento es el registro vivo del plan: se actualiza fase a fase y no se reescribe.

- **Primera entrega:** 10/11/2026 (segundo parcial: APIs sin GUI, Swagger y Postman).
- **Material de referencia:** repositorio `iw3_2026` de la cátedra (solo lectura).
- **Stack heredado de la cátedra:** Java 21, Spring Boot 4.1.x, Maven Wrapper,
  Spring Data JPA + MySQL 8, Spring Security + JWT (java-jwt).

## 1. Alcance según la consigna

| Categoría | Requisitos |
|---|---|
| **Obligatorio 2º parcial** | Puntos 1 a 5 como API sin interfaz gráfica. Documentación OpenAPI/Swagger. Colección Postman que pruebe todas las APIs. Persistencia, estados secuenciales y registro de cada cambio de estado (fecha/hora, usuario o proceso, observaciones). Descarte de registros de detalle inválidos. |
| **Opcional 2º parcial** | Simulación del circuito completo con *Tests* de Postman. Frecuencia de almacenamiento distinta de la de recepción. Promedios de temperatura, densidad y caudal. |
| **Examen final** | 100 % de los endpoints. Roles de usuario. Alarma de temperatura por e-mail (umbral y destinatarios configurables; sin reenvío hasta ser aceptada). Frontend (login, monitoreo en tiempo real, ETA, tiempo transcurrido, alarmas, conciliaciones). |

Los requisitos del examen final **no** se incorporan al alcance del segundo parcial.

## 2. Estados de la orden (consigna)

Los cambios son secuenciales y unidireccionales; cada cambio queda registrado.

| Estado | Nombre | Lo provoca |
|---|---|---|
| 1 | Pendiente de pesaje inicial | Punto 1: recepción de la orden desde el sistema externo (SAP) |
| 2 | Con pesaje inicial registrado | Punto 2: tara enviada por la balanza (TMS) |
| 3 | Cerrada para carga | Punto 4: endpoint de cierre |
| 4 | Finalizada | Punto 5: pesaje final enviado por la balanza (TMS) |

Los datos de detalle (Punto 3) llegan **mientras la orden está en estado 2**.

## 3. Decisiones aprobadas

| # | Tema | Resolución | Aplica en |
|---|---|---|---|
| 1 | Código demo de la cátedra (Product, Category, cli1, cli2, ScanConfigs) | **Aplicada en la Fase 1.** Quedó en el historial de git. | Fase 1 |
| 2 | Seguridad en el parcial | Se mantiene `anyRequest().authenticated()`. Postman hace login primero con un usuario de prueba sembrado. Roles quedan para el final. | Fases 1–5 |
| 3 | Promedios | Se implementan (`AVG` sobre los detalles almacenados). | Fase 5 |
| 4 | Duplicados | Convención de la cátedra: `FoundException` → HTTP 302. Se documenta en Swagger. | Fase 1 |
| 5 | Idioma | Identificadores en inglés; comentarios y documentación en español. | Todas |
| 6 | Entorno: alinear `pom.xml` a Boot 4.1.1 / java-jwt 4.6.1 y retirar actuator/h2 | **Resuelta: no se cambia.** El `pom.xml` queda como está. | — |
| 7 | Distinguir "activada" de "tara registrada" | Se agrega el campo `activationDateTime` a la orden. | Fase 3 |
| 8 | Densidad | Se valida literal según la consigna: `0 < densidad < 1`. | Fase 3 |
| 9 | Git | Una rama por fase; commits atómicos por funcionalidad, de no más de 100 líneas. | Todas |
| 10 | Renombrar el contenedor del compose a `iw3-gas-mysql` (hallazgo H-4) | **Aplicada en la Fase 1.** | Fase 1 |
| 11 | Permitir `/error` y corregir el `produces` del login (H-1 y H-2, en un mismo cambio) | **Aplicada en la Fase 1.** | Fase 1 |
| 12 | Incorporar `getUserLogged()` en `BaseRestController` (H-10) | **Aplicada en la Fase 1.** | Fase 1 |
| 13 | Integrar `fase-0-auditoria` a `main` antes de abrir la rama de la Fase 1 | **Aplicada:** `main` en `c3b62de`, publicada en GitHub. | Antes de la Fase 1 |
| 14 | Todo cambio a `main` por Pull Request con la aprobación de 3 de los 4 integrantes | **Acordada (14/10/2026).** La protección de rama la configura el dueño del repo | Todas |
| 15 | Código HTTP 409 Conflict (`ConflictException`) para orden en estado incorrecto | **Aplicada en la Fase 2.** Distingue estado incompatible de error sintáctico (400). | Fase 2 |
| 16 | Contraseña de activación de 5 dígitos generada con `SecureRandom` y oculta con `@JsonIgnore` | **Aplicada en la Fase 2.** Solo viaja en la respuesta de la balanza (`TareResponse`). | Fase 2 |
| 17 | Orden de comprobaciones de tara: 404 (orden) → 400 (peso > 0) → 409 (estado), todas fuera del `try/catch` | **Aplicada en la Fase 2.** Evita que `catch (Exception)` las transforme en 500. | Fase 2 |

Convención adicional: el *push* y el *merge* a `main` se hacen solo por pedido explícito.

## 4. Modelo de datos propuesto

Propuesta de diseño (no es un requisito de la consigna). Toda entidad que viene de un
sistema externo lleva `externalCode` (String, único), como `codCli1` en la cátedra.

| Entidad | Atributos principales | Relaciones |
|---|---|---|
| `Truck` | externalCode, plate, description\* | 1—N `TruckTank` |
| `TruckTank` | number, capacityLiters (cisternado) | N—1 `Truck` |
| `Driver` | externalCode, name, lastName, document\* | — |
| `Customer` | externalCode, companyName, contact\* | — |
| `Product` | externalCode, name, description\* | — |
| `Order` | orderNumber (único), plannedLoadDateTime, preset, status (1–4), activationPassword, tare, finalWeight, activationDateTime, fechas del ciclo, últimos valores de carga | N—1 Truck, Driver, Customer, Product |
| `OrderDetail` | accumulatedMass, density, temperature, flowRate, timestamp | N—1 `Order` |
| `OrderStatusLog` | fromStatus, toStatus, dateTime, actor (usuario o proceso), observations\* | N—1 `Order` |

\* opcional.

## 5. Endpoints propuestos

Rutas bajo `/api/v1` (`Constants.URL_BASE`). Son una propuesta; se confirman en cada fase.

| Punto | Método y ruta | Entrada | Salida |
|---|---|---|---|
| 1 | `POST /integration/sap/orders` | Orden y sus entidades, con códigos externos | 201 + `location`; 302 si ya existe |
| 2 | `POST /integration/tms/orders/{orderNumber}/tare` | Tara (kg) | Contraseña de activación (5 dígitos) |
| 3 | `POST /integration/loader/activation` | `orderNumber`, `password` | Preset (kg) |
| 3 | `POST /integration/loader/orders/{orderNumber}/details` | Masa, densidad, temperatura, caudal | 200; tratamiento del registro inválido a definir en Fase 3 |
| 4 | `POST /integration/loader/orders/{orderNumber}/close` | — | 200; orden en estado 3 |
| 5 | `POST /integration/tms/orders/{orderNumber}/final-weight` | Pesaje final (kg) | Conciliación; orden en estado 4 |
| 5 | `GET /orders/{orderNumber}/reconciliation` | — | Conciliación (solo estado 4) |

Errores: 404 orden inexistente, 302 duplicado, 500 error de negocio, con el formato
`StandartResponse` de la cátedra. El código para "estado incorrecto" quedó definido como 409 Conflict en la Fase 2.

## 6. Fases

| Fase | Rama | Entrega | Criterio de aceptación |
|---|---|---|---|
| 0 | `fase-0-auditoria` | Línea base ejecutada, hallazgos y guía de estudio de la base. Sin cambios de código. | Se sabe qué existe, qué falta y si se puede implementar. **(Completada e integrada en main)** |
| 1 | `fase-1-recepcion-ordenes` | Retiro del código demo. Entidades, orden en estado 1, log de estados, `springdoc` y primera colección Postman. | Alta 201; duplicado 302; entidades sincronizadas por `externalCode`; log con un registro. **(Completada e integrada en main)** |
| 2 | `fase-2-pesaje-inicial` | Tara, contraseña de 5 dígitos, estado 1→2. | Casos válidos y errores (orden inexistente, estado incorrecto 409, tara inválida 400). **(Completada y probada en rama)** |
| 3 | `fase-3-carga-tiempo-real` | Activación (devuelve preset), detalle con descarte, cabecera, `activationDateTime`, densidad `0 < d < 1`. | Registros inválidos descartados; cabecera con el último valor válido. |
| 4 | `fase-4-cierre-orden` | Cierre 2→3 y bloqueo de nuevos detalles. | Detalle posterior rechazado; transiciones inválidas rechazadas. |
| 5 | `fase-5-conciliacion` | Pesaje final 3→4, conciliación con promedios, Swagger completo, Postman consolidado con *Tests*. | Circuito completo 1→4 verificado; consulta solo en estado 4. |

## 7. Forma de trabajo

### Git
- Una rama por fase, con el nombre de la tabla anterior.
- Commits atómicos por funcionalidad, de no más de 100 líneas cada uno, con mensaje en
  español (`tipo(ámbito): descripción`). Por ejemplo: entidad y repositorio, servicio,
  controller, documentación.
- Push y merge a `main`: solo por pedido explícito, y por Pull Request con la aprobación de 3 de los 4 integrantes.

### Documentación por fase
Cada fase deja `docs/fase-N-<tema>.md` con dos partes:

1. **Informe técnico:** inventario de archivos, implementación, funciones y métodos,
   recorrido de las peticiones y pruebas ejecutadas.
2. **Guía de estudio (secciones A–J):** objetivo, teoría, arquitectura, código paso a paso,
   trazabilidad de datos, ejemplo, preguntas, respuestas, errores frecuentes y lista de
   comprobación para rendir.

### Definición de terminado
Una fase termina cuando: la funcionalidad está implementada y probada; se respetó la
metodología de la cátedra; los archivos y funciones están documentados; el recorrido de los
datos está explicado; la guía tiene al menos diez preguntas con respuesta; las limitaciones
están registradas y los criterios de aceptación fueron verificados.

## 8. Notas técnicas verificadas

Comprobadas ejecutando sobre una copia del commit de `main` (14/10/2026).

- **Paquete de excepciones.** El TP usa `ar.edu.iua.iw3.business.exception`; la cátedra
  conserva la errata `...business.execption`. Los archivos de la clase 9 (`EmailBusiness`,
  `Scheduler`, `MailRestController`) importan `execption`: copiados tal cual al TP fallan con
  `package ar.edu.iua.iw3.business.execption does not exist`. Al incorporarlos hay que
  cambiar ese import.
- **Base de datos inalcanzable en producción.** Con el perfil `mysqlprod` y un host de base
  que no resuelve, la aplicación termina en unos 15 s con código de salida 1, tras consumir
  unos 33 s de CPU. Con reinicio automático del contenedor eso es un bucle de arranques que
  mantiene la CPU alta. Con la base accesible, la CPU en reposo es de 0 %.
