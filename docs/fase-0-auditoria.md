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

## 4. Qué existe y qué falta

| Área | Estado | Evidencia |
|---|---|---|
| Dominio de gas (Orden, Camión, Chofer, Cliente, Producto, detalle, log de estados) | **No existe** | Ninguna clase del dominio en `SRC` |
| Arquitectura en capas | Existe (demo) | `SRC/model`, `SRC/model/persistence`, `SRC/business`, `SRC/controllers` |
| Persistencia | Existe | JPA + MySQL con `ddl-auto=update`; sin migraciones |
| Excepciones y respuesta estándar | Existe | `SRC/business/exception/*`, `SRC/util/StandartResponse*.java` |
| Seguridad JWT | Existe | `SRC/security`, `SRC/auth/**`; sin usuarios sembrados |
| Perfiles | Parcial | `SRC/config/profile/*ScanConfig.java`; los perfiles de Maven no activan el de Spring (H-3) |
| Swagger/OpenAPI | **No existe** | Sin dependencia `springdoc` |
| Colección Postman | **No existe** | Solo `src/test/java/ar/edu/iua/iw3/datasets/add_postman.csv` (productos demo) |
| Tests | Mínimo | `Iw3ApplicationTests.contextLoads` |
| Contenido de la clase 9 de la cátedra | No existe | Eventos, `EmailBusiness`, `Scheduler`, `getUserLogged()`, claim `internalId` |
| Validación de entrada | No existe | Igual que en la cátedra (sin `@Valid`) |

## 5. Línea base ejecutada

Pruebas del 14/10/2026 contra MySQL real, con el usuario `admin` del seed.

| # | Verificación | Comando o petición | Resultado |
|---|---|---|---|
| 1 | Compilación | `./mvnw -B -DskipTests compile` | `BUILD SUCCESS`, 50 fuentes, 15 s. Descargó `actuator`, `h2` y `java-jwt 4.4.0`, que no estaban en caché |
| 2 | Test existente | `./mvnw -B test` | 1 test, 0 fallas, 14,7 s |
| 3 | Arranque | `./mvnw -B spring-boot:run` | `Started Iw3Application in 8.8 s` |
| 4 | Sin token | `GET /api/v1/products` | 401 con `WWW-Authenticate: Basic` |
| 5 | Login correcto | `POST /api/v1/login` (`username`, `password`) | 200 `text/plain`, JWT HS512 |
| 6 | Con token | `GET /api/v1/products` + `Bearer` | 200 `[ ]` |
| 7 | Token por query | `GET /api/v1/products?authtoken=<jwt>` | 200 |
| 8 | Token inválido o alterado | `Bearer abc.def.ghi` y firma modificada | 401 |
| 9 | Login fallido | Contraseña incorrecta / usuario inexistente | 401; con contraseña incorrecta el cuerpo viene vacío (H-2) |
| 10 | Negocio: no encontrado | `GET /api/v1/products/999` | 404 + `StandartResponse` |
| 11 | Negocio: alta | `POST /api/v1/products` | 201 + `location: /api/v1/products/1` |
| 12 | Negocio: duplicado | Mismo `POST` | 302 + `StandartResponse` |
| 13 | Negocio: baja | `DELETE /api/v1/products/1`, luego `GET` | 200, luego 404 |
| 14 | Errores sin controller | JSON malformado, `Content-Type` erróneo, ruta inexistente (token válido) | 401 sin cuerpo; se esperaba 400, 415 y 404 (H-1) |
| 15 | Documentación | `GET /v3/api-docs`, `GET /swagger-ui.html` | 401 (no hay springdoc) |
| 16 | Perfil por Maven | `./mvnw -Pmysqldev spring-boot:run` | Log: `Perfil activo default` (H-3) |
| 17 | Perfil por Spring | `./mvnw -Dspring-boot.run.profiles=cli1 spring-boot:run` | Log: `Perfil activo cli1`; `/integration/cli1/products` responde |
| 18 | JSON externo (cli1) | `POST …/cli1/products/b2b` con distintas claves de precio | `price`: 0.0 · `price_product`: 0.0 · `product_price` decimal: 50.5 · `product_price` entero: 0.0 (H-6, H-7) |

Los productos creados durante las pruebas se eliminaron.

## 6. Hallazgos

Cada hallazgo indica evidencia, causa, impacto y propuesta. Ninguno se corrigió en esta fase.

### H-1 — Errores sin controller devuelven 401 sin cuerpo (impacto alto)

- **Evidencia:** prueba 14. El log muestra `DefaultHandlerExceptionResolver: Resolved
  HttpMessageNotReadableException` y `HttpMediaTypeNotSupportedException`.
- **Causa:** Spring resuelve el error (400/415) y el contenedor reenvía a `/error`;
  `anyRequest().authenticated()` lo bloquea porque la sesión es STATELESS.
- **Impacto:** SAP, TMS y el sistema de carga no distinguen un payload inválido de un
  problema de autenticación. Los casos de error de Postman serían engañosos.
- **Propuesta:** permitir `/error` en `SecurityConfiguration` (una línea). Es un desvío menor
  respecto de la cátedra; se aplica en la Fase 1 junto con H-2.

### H-2 — Login fallido pierde el cuerpo (impacto medio)

- **Evidencia:** prueba 9. El log muestra `HttpMessageNotWritableException: No converter for
  [class StandartResponse]`.
- **Causa:** `AuthRestController.loginExternalOnlyToken` declara `produces = text/plain`,
  pero en error devuelve un `StandartResponse` (JSON).
- **Impacto:** el 401 es correcto por casualidad (vía H-1) y el mensaje se pierde. Si solo se
  corrige H-1, el login fallido pasaría a 500 (inferido; verificar al corregir).
- **Propuesta:** corregir H-1 y H-2 en el mismo cambio, con prueba de login fallido.

### H-3 — Los perfiles de Maven no activan el perfil de Spring (impacto medio)

- **Evidencia:** pruebas 16 y 17.
- **Causa:** `application.properties` no referencia `@spring.profiles.active@` y
  `spring-boot:run` no propaga las propiedades de los perfiles de Maven.
- **Impacto:** el README y Postman deben usar `-Dspring-boot.run.profiles=<perfil>` o la
  variable `SPRING_PROFILES_ACTIVE`. Sin perfil no se aplica ningún `ScanConfig`.
- **Propuesta:** documentar el comando correcto. Al retirar cli1/cli2 (decisión 1) solo
  quedan `mysqldev` y `mysqlprod`.

### H-4 — Colisión de nombre del contenedor MySQL (impacto medio)

- **Evidencia:** `docker compose up` falla con "container name /iw3-mysql is already in use".
- **Causa:** el compose del TP usa `container_name: iw3-mysql`, igual al contenedor de la
  cátedra (que además usa el puerto 33306 y otra base).
- **Impacto:** el README no se puede seguir tal cual en esta máquina.
- **Propuesta:** renombrar a `iw3-gas-mysql` en `docker-compose.yml` y en los comandos del
  README. Decisión pendiente (ver sección 8).

### H-5 — Seguridad heredada de la cátedra (impacto medio, antes de desplegar)

- **Evidencia:** lectura de `SRC/auth/filters/AuthConstants.java`,
  `src/main/resources/application*.properties` y `SRC/security/SecurityConfiguration.java`.
- **Hechos:** la clave de firma del JWT está fija en el código; hay credenciales de base por
  defecto en `application.properties` y `application-mysqlprod.properties`; `/demo/**` es
  público (incluye `/demo/encodepass`); CORS acepta cualquier origen; `dev.info.enabled=true`
  en el perfil por defecto hace que las respuestas de error incluyan la traza completa
  (visible en la prueba 10).
- **Propuesta:** mover secretos a variables de entorno y cerrar `/demo/**` en la etapa de
  seguridad del examen final. Para el parcial, no subir credenciales reales.

### H-6 — `JsonUtiles.getDouble` descarta números enteros (heredado de la cátedra)

- **Evidencia:** prueba 18: `"product_price": 50` se guardó como 0.0 y `50.5` como 50.5.
- **Causa:** el método exige `isDouble()` y el entero `50` es un `IntNode`.
- **Impacto:** un `preset` de 5000 o una masa de 1500 se leerían como 0 sin error.
- **Propuesta:** no reutilizar `JsonUtiles` para los payloads del TP; usar el *binding*
  directo de Jackson sobre clases de entrada. Si se reutiliza, corregir con `isNumber()`.

### H-7 — Alias con espacios en `ProductCli1JsonDeserializer` (propio del fork)

- **Evidencia:** prueba 18: las claves `price` y `price_product` dieron 0.0.
- **Causa:** la cátedra usa `"a,b,c".split(",")`; el TP tiene `"a, b, c".split(",")`, por lo
  que los alias posteriores al primero conservan un espacio y nunca coinciden.
- **Propuesta:** desaparece al retirar el código demo (decisión 1). Si el patrón vuelve a
  usarse, separar con `split("\\s*,\\s*")`.

### H-8 — Zona horaria (riesgo a verificar en la Fase 1)

- **Hechos:** la URL JDBC usa `serverTimezone=UTC`; la aplicación fija
  `America/Argentina/Buenos_Aires` en `Iw3Application.run()`, **después** de iniciar el
  contexto (el log muestra `Initial TimeZone` luego de `Started`).
- **Impacto posible:** desfase de 3 h en las fechas y horas de la orden.
- **Propuesta:** probar con el primer timestamp real (`OrderStatusLog`) y, si hay desfase,
  fijar `hibernate.jdbc.time_zone`.

### H-9 a H-11 — Menores

| ID | Hallazgo | Propuesta |
|---|---|---|
| H-9 | `pom.xml` desalineado: Boot 4.1.0 vs 4.1.1, `java-jwt` 4.4.0 vs 4.6.1, `actuator` y `h2` sin uso. Compila y arranca igual. | Alinear es opcional (decisión 6 pendiente). |
| H-10 | Falta `BaseRestController.getUserLogged()` y el resto de la clase 9. `Iw3Application` inyecta beans que no usa. | `getUserLogged()` entra en la Fase 1 (actor del log de estados). Lo demás se va con el demo. |
| H-11 | Avisos de arranque: `HHH90000025` (dialecto explícito) y `open-in-view` activo. | Sin acción. |
