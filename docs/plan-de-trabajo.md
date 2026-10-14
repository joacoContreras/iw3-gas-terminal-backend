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
| 1 | Código demo de la cátedra (Product, Category, cli1, cli2, ScanConfigs) | Se retira en un commit aparte al inicio de la Fase 1. Queda en el historial de git. | Fase 1 |
| 2 | Seguridad en el parcial | Se mantiene `anyRequest().authenticated()`. Postman hace login primero con un usuario de prueba sembrado. Roles quedan para el final. | Fases 1–5 |
| 3 | Promedios | Se implementan (`AVG` sobre los detalles almacenados). | Fase 5 |
| 4 | Duplicados | Convención de la cátedra: `FoundException` → HTTP 302. Se documenta en Swagger. | Fase 1 |
| 5 | Idioma | Identificadores en inglés; comentarios y documentación en español. | Todas |
| 6 | Entorno: alinear `pom.xml` a Boot 4.1.1 / java-jwt 4.6.1 y retirar actuator/h2 | **Pendiente de respuesta.** La Fase 0 se ejecutó con el `pom.xml` actual. | Fase 0 |
| 7 | Distinguir "activada" de "tara registrada" | Se agrega el campo `activationDateTime` a la orden. | Fase 3 |
| 8 | Densidad | Se valida literal según la consigna: `0 < densidad < 1`. | Fase 3 |
| 9 | Git | Una rama por fase; commits atómicos por funcionalidad, de no más de 100 líneas. | Todas |

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
`StandartResponse` de la cátedra. El código para "estado incorrecto" se define en la Fase 2.
