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
