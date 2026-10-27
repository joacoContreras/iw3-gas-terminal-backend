# Auditoría integral de la Fase 3 — PR #7

> **VEREDICTO: B — APROBAR CON OBSERVACIONES.** El código cumple los requisitos de la Fase 3 y está integrado sin regresiones. No hay hallazgos críticos ni altos. El **merge** queda condicionado a una regla de proceso: hoy el PR tiene **1 aprobación de las 3 acordadas** (decisión 14).

Auditoría de **solo lectura**: no se modificó código ni documentación existente, no se hicieron commits ni pushes, no se aprobó ni se mergeó el PR. Este archivo es lo único que se creó durante la auditoría.

> **Seguimiento (PR de correcciones):** el PR #7 ya se mergeó. Se corrigieron **F-01** (lecturas simultáneas: bloqueo pesimista de la orden; 150 lecturas simultáneas sin 500 ni deadlocks), **F-02** (valores infinitos: se descartan) y **F-13** (la estampa es siempre la hora de recepción del servidor), con pruebas en `OrderDetailValidatorTest` y en la carpeta 03 de Postman (163 aserciones). Las observaciones de documentación (F-03, F-04, H-15, H-19, H-20) se actualizaron en los informes. El informe de abajo se conserva tal como se escribió.

---

## Apéndice — Contraste con la consigna original (posterior a la auditoría)

**Contexto.** La consigna (`docs/pdf/consigna-trabajo-integrador-2026.pdf`, 9 páginas) estaba en la carpeta Descargas desde el 14/10. La auditoría solo buscó dentro de las carpetas del proyecto y por eso la dio por no disponible: **esa afirmación era incorrecta**. Con el PDF a mano se contrastó lo que había quedado como "no verificado" (R39). El veredicto **sigue siendo B**.

| Aspecto | Consigna | Implementación | Resultado |
|---|---|---|---|
| Estados 1 a 4, secuenciales y unidireccionales, con log de auditoría (fecha, usuario o proceso, observaciones) | pp. 6 y 7 | Fases 1 a 3 | Coincide |
| Contraseña de 5 dígitos; la activación valida orden existente, estado esperado y contraseña, y devuelve el preset | pp. 2 y 6 | `activate` | Coincide |
| Descartar caudal ≤ 0, masa ≤ 0 o menor que la anterior; densidad entre 0 y 1 | p. 2 | `OrderDetailValidator` | Coincide |
| Cabecera con los últimos valores; detalle con el histórico; inicio y fin de carga | pp. 4 y 5 | `Order`, `OrderDetail` | Coincide |
| Entidades, campos opcionales y código externo en `String` | p. 4 | Fase 1 | Coincide |
| Segundo parcial: Swagger y Postman obligatorios; frecuencia de almacenamiento, promedios y simulación en Postman opcionales | p. 8 | Swagger, Postman, se guardan todas las lecturas | Coincide |
| **Estampa de tiempo de las lecturas** | p. 2 lista los datos de detalle recibidos: masa, densidad, temperatura y caudal (**sin estampa**); p. 5: "Estampa de tiempo: **momento de recepción** del dato" | `DetailRequest` acepta un `timestamp` **opcional del cliente** y lo usa en el detalle y la cabecera (D6) | **Diferencia (F-13)** |

**F-13 — La estampa de tiempo debería ser el momento de recepción, no un dato del cliente** (R39) · Parte 2 y decisión D6 · **MEDIO, no bloquea**
- **Evidencia:** la consigna define la estampa como el momento en que el backend recibe el dato y no incluye un `timestamp` entre los datos que envía el sistema de carga. La implementación acepta uno opcional y, si viene, lo usa en lugar de la hora de recepción. El origen está en el resumen de `CLAUDE.md` 4.2, que dice que "cada lectura trae ... estampa de tiempo".
- **Impacto:** si el emisor no envía `timestamp` (lo esperable según la consigna), el comportamiento coincide. Si lo envía, el sistema se aparta de la definición y quedan abiertas las limitaciones H-19 (zona `Z` descartada) y H-20 (fin de carga anterior al inicio al mezclar estampas), que existen solo por esta extensión.
- **Corrección recomendada:** usar siempre la hora del servidor e ignorar (o quitar de `DetailRequest`) el `timestamp`. Implica ajustar los pedidos de la carpeta 03 de Postman que usan estampas fijas para comparar inicio y fin de carga, y la decisión D6 (plan, decisión 23).

**Cambios sobre este informe**
- **R39** pasa de NO VERIFICADO a **PARCIAL** (por F-13).
- **F-05 queda retirado:** la consigna escribe la densidad como "valor entre 0 y 1 (kg/m³)" (p. 2); el comentario del validador copia esa unidad. La inconsistencia está en la consigna, no en el código.
- **F-09** deja de ser una limitación: la consigna original se obtuvo.
- **Totales actualizados:** 42 requisitos · 35 cumplidos · **4 parciales** (R36, R37, R38, R39) · 0 incumplidos · **0 no verificados** · 3 no aplican. Hallazgos: 0 críticos, 0 altos, **2 medios** (F-01 y F-13), **4 bajos** (F-02, F-03, F-04, F-06) y observaciones.

**A confirmar por el equipo (no es un hallazgo del código):** la consigna (p. 8) dice "grupos de hasta tres integrantes" y el repositorio tiene cuatro colaboradores con permiso de escritura.

---

## A. Identificación

| Dato | Valor |
|---|---|
| Proyecto | `iw3-gas-terminal-backend` (Ingeniería Web III, TP integrador) |
| Fase auditada | Fase 3: activación de la carga y lecturas del caudalímetro (Punto 3) |
| PR | **#7** "Fase 3 carga tiempo real", autor `tomas-calvo01`, creado el 27/10/2026 17:30 UTC |
| Rama base | `main` (`bd2d009`, merge del PR #6 de la Fase 2) |
| Rama del PR | `fase-3-carga-tiempo-real` |
| Commit revisado | `8ee435e` (cabeza de la rama; coincide con `origin/fase-3-carga-tiempo-real`) |
| Fecha de auditoría | 27/10/2026 |
| Estado del PR | Abierto, **no borrador**, descripción **vacía**, `mergeable: MERGEABLE`, `mergeStateStatus: CLEAN`, 35 commits, 30 archivos (+4774 / −43), 0 comentarios |
| Reviews | 1: `joacoContreras` **APPROVED** (27/10/2026 17:41 UTC) |
| Checks remotos | **No hay**: ni checks reportados ni workflows en el repo (no existe `.github/workflows`) |
| Conflictos | Ninguno (simulación con `git merge-tree` contra `origin/main`) |
| Regla de GitHub en `main` | 1 aprobación requerida, descartar aprobaciones al pushear, resolver conversaciones |

### Limitaciones de la auditoría

1. **La consigna original no se había localizado al auditar** (solo se buscó dentro de las carpetas del proyecto; estaba en Descargas). Se contrastó después: ver el apéndice. El único PDF de la materia dentro del proyecto es `IW3_Guia_Estudio_Clases.pdf` (24 páginas), que `CLAUDE.md` define como **resumen personal**, no oficial. La auditoría se hizo sobre el resumen de `CLAUDE.md` §4 y el plan.
2. **Independencia limitada.** Esta auditoría la hizo el mismo asistente que implementó la Parte 1 (a pedido de Martín González) y que redactó el documento de división del trabajo. Para mitigarlo, las conclusiones se apoyan en ejecuciones reproducibles y en el diff del PR, y se declara lo que no pudo comprobarse. La atribución de autoría es documental (commits y plan).
3. **Entorno de pruebas:** MySQL real (`iw3-gas-mysql`) pero sobre una **base aislada y descartable** (`iw3_audit`, ya eliminada). Se encendió Docker Desktop para la auditoría. La base de desarrollo `iw3_db` no se tocó (4 órdenes antes y después).
4. **No se abrió Swagger UI en un navegador:** se verificó el JSON de `/v3/api-docs`.
5. **No se reprodujo** la simulación del estado 3 ni la prueba con *trigger* que describe Calvo (secciones 7.6 y 7.7 de su informe). Sí se reprodujo la atomicidad con una restricción `CHECK` temporal.

---

## B. Veredicto ejecutivo

**B — Aprobar con observaciones.** Fundamentos:

- De los **42 requisitos** relevados, **35 están CUMPLIDOS**, 4 son PARCIALES, 0 INCUMPLIDOS, 0 NO VERIFICADOS y 3 NO APLICAN a la fase (totales actualizados con la consigna original; ver el apéndice).
- **0 hallazgos críticos y 0 altos.** 2 medios (F-01 y F-13), 4 bajos y observaciones.
- Se **reprodujeron de forma independiente** los números del informe de Calvo: 11 tests en verde, newman con 65 pedidos y 160 aserciones sin fallas, y una batería propia de 76 comprobaciones sin fallas.
- Las tres partes se integran: el circuito tara → activación → lecturas → histórico funciona de punta a punta y las Fases 1 y 2 siguen pasando (46 aserciones).
- No se emite **A** porque hay una diferencia con la consigna original (la estampa de tiempo, F-13), no existen checks automáticos que verificar y la documentación tiene imprecisiones menores.
- El **merge** depende de reunir las aprobaciones acordadas (decisión 14): hoy hay 1 de 3. Es una condición de proceso, no un defecto del código.

---

## C. Alcance y metodología

**Qué se revisó:** el diff completo de la rama contra `main` (30 archivos, 18 de `src`), los tres bloques de trabajo (Gonzalez, Contreras, Calvo), la colección Postman, los documentos de la fase y su coherencia con el código, y el comportamiento real de la aplicación.

**Qué quedó fuera:** las Fases 4 y 5, roles, alarmas y frontend (examen final), y la configuración de producción y de la VM.

**Documentos y material consultados**

| Fuente | Uso |
|---|---|
| `CLAUDE.md` (versión de la rama) | Reglas de trabajo, convenciones, documentación y estado |
| `docs/plan-de-trabajo.md` | Alcance, decisiones 1 a 28, fases y criterios |
| `docs/fase-3-division-de-trabajo.md` | Reparto, contrato D1 a D10, 19 criterios de aceptación y bloques de estado |
| `docs/fase-3-carga-tiempo-real.md` | Informe técnico, auditoría de Calvo y guía de estudio |
| `docs/fase-2-pesaje-inicial.md`, `docs/guia-estudio-src.md` | Contexto y consistencia |
| `IW3_Guia_Estudio_Clases.pdf` (24 pp.) | Material del profesor (resumen personal): pp. 2, 5, 7, 9, 13-14, 15-16, 17-18 y 20-22 |
| `iw3_2026/iw3/src/...` (repo del profesor) | `ProductRestController`, `Product`, `ProductRepository` y la estructura de paquetes, para contrastar patrones |
| Código de la rama | Todos los archivos de `src` del PR, leídos completos |
| `docs/postman/IW3-Gas-Terminal.postman_collection.json` | Inspección estática y ejecución con newman |

**Comandos ejecutados** (sección H) y los resultados están en la sección H.

---

## D. Matriz de requisitos

Estados: **CUMPLIDO**, **PARCIAL**, **INCUMPLIDO**, **NO VERIFICADO**, **NO APLICA**. "AUD" es la batería de auditoría de esta revisión; "PM" es un pedido de la carpeta 03 de Postman.

| ID | Requisito | Fuente | Resp. | Implementación | Evidencia | Estado | Sev. |
|---|---|---|---|---|---|---|---|
| R01 | El sistema de carga envía número de orden y contraseña | Consigna 4.1 | Gonzalez | `OrderLoaderRestController.activate`, `ActivationRequest` | AUD 200; PM "Activación correcta" | CUMPLIDO | — |
| R02 | Se valida que la orden exista (404) | Consigna 4.1 | Gonzalez | `OrderBusiness.activate` → `load` | AUD 404; PM | CUMPLIDO | — |
| R03 | Se valida el estado esperado (2); si no, 409 | Consigna 4.1, D7 | Gonzalez | `OrderBusiness.activate` | AUD 409 con orden en estado 1; PM | CUMPLIDO | — |
| R04 | Se valida la contraseña; si es incorrecta, 403 y no se activa | Consigna 4.1, D1 | Gonzalez | `ForbiddenException`, `activate` | AUD 403 y `activationDateTime` sigue `null`; PM | CUMPLIDO | — |
| R05 | Se devuelve el preset | Consigna 4.1 | Gonzalez | `ActivationResponse(preset)` | AUD `{ "preset": 15000.0 }` | CUMPLIDO | — |
| R06 | La orden sigue en estado 2 y la activación no escribe historial | Consigna 4.1, D9 | Gonzalez | `activate` (no toca `status` ni `logStatus`) | AUD estado 2 y `/status-log` con 2 registros | CUMPLIDO | — |
| R07 | `activationDateTime` distingue "activada" de "con tara" | Plan, decisión 7 | Gonzalez | `Order.activationDateTime` | AUD fecha presente | CUMPLIDO | — |
| R08 | Activación idempotente | D2 | Gonzalez | `if (order.getActivationDateTime() == null)` | AUD repetir: 200, mismo preset, misma fecha | CUMPLIDO | — |
| R09 | Cuerpo incompleto o inválido → 400; orden de comprobaciones 400 → 404 → 409 → 403 | D7 | Gonzalez | `activate` (comprobaciones fuera del `try`) | AUD: `{}`, sin campos, `"abc"`, JSON roto → 400; `{orderNumber: 999999999}` → 400; estado 1 + contraseña mala → 409; sin token → 401 | CUMPLIDO | — |
| R10 | La lectura trae masa, densidad, temperatura, caudal y estampa | Consigna 4.2 | Contreras | `DetailRequest`, `OrderDetail` | AUD lecturas válidas; PM | CUMPLIDO | — |
| R11 | Descartar caudal ≤ 0 | Consigna 4.2 | Contreras | `OrderDetailValidator.discardReasons` | AUD caudal 0 y −5; test unitario | CUMPLIDO | — |
| R12 | Descartar masa ≤ 0 | Consigna 4.2 | Contreras | ídem | AUD masa 0 y −1; test unitario | CUMPLIDO | — |
| R13 | Descartar masa menor que la anterior; una masa igual es válida | Consigna 4.2, D5 | Contreras | ídem + `order.getLastAccumulatedMass()` | AUD 200 < 250 descartada; 250 igual guardada | CUMPLIDO | — |
| R14 | Densidad entre 0 y 1, sin incluirlos; fuera de rango se descarta | Consigna 4.2, decisión 8, D4 | Contreras | ídem | AUD 0, 1, 1.5 y −0.2 descartadas | CUMPLIDO | — |
| R15 | La cabecera guarda el último valor válido de cada atributo | Consigna 4.2 | Contreras | `OrderDetailBusiness.add` | AUD cabecera 250/0.53/19.1/3700 y sin cambios tras descartes | CUMPLIDO | — |
| R16 | El detalle guarda el histórico (solo lecturas válidas) | Consigna 4.2 | Contreras | `OrderDetail`, `OrderDetailRepository` | AUD `COUNT(*)` = 5 (las 5 válidas); 0 en órdenes sin activar | CUMPLIDO | — |
| R17 | Inicio de carga = primer registro válido | Consigna 4.2 | Contreras | `add` (`if getLoadingStartDateTime() == null`) | AUD inicio `10:00:00` y no cambia | CUMPLIDO | — |
| R18 | Fin de carga = último registro válido | Consigna 4.2, D8 | Contreras | `add` (`setLoadingEndDateTime`) | AUD fin `10:00:05` tras la 2.ª lectura | CUMPLIDO | — |
| R19 | Se guardan todas las lecturas válidas (sin reducir frecuencia) | Consigna 4.2 (opcional) | Contreras | `add` guarda cada lectura válida | AUD 100/100 guardadas | CUMPLIDO | — |
| R20 | Lecturas solo con la orden en estado 2 y activada (409); inexistente 404; sin token 401; orden 404 → 400 → 409 → descarte | Plan 8.1, D7 | Contreras | `add` (comprobaciones fuera del `try`) | AUD: estado 1 → 409; sin activar → 409; `{}` a inexistente → 404; `{}` a estado 1 → 400; lectura inválida a orden sin activar → 409 | CUMPLIDO | — |
| R21 | Campo ausente, `null`, JSON roto, texto o `timestamp` inválido → 400 | D4 | Contreras | `Double` en `DetailRequest`; `add` | AUD 6 casos → 400 | CUMPLIDO | — |
| R22 | Siempre 200 con `{ saved, reason }`, guardada o descartada | D3 | Contreras | `DetailResult`, `DetailResponse` | AUD todas las lecturas | CUMPLIDO | — |
| R23 | `timestamp` opcional; si falta, hora del servidor | D6 | Contreras | `add` (`LocalDateTime.now()`) | AUD sin `timestamp` guardada | CUMPLIDO | — |
| R24 | Atomicidad: detalle y cabecera juntos o ninguno; una falla de base responde con `StandartResponse` | Plan, `CLAUDE.md` 6 | Contreras / Calvo | `@Transactional(rollbackFor)` + `saveAndFlush` | AUD con `CHECK` temporal: 500 con `StandartResponse`, detalle revertido, cabecera sin cambios; en paralelo, filas = lecturas aceptadas | CUMPLIDO | — |
| R25 | La contraseña no se publica (JSON, mensajes, logs) | Decisión 16 | Todos | `@JsonIgnore`, mensaje fijo del 403 | AUD: ni respuestas ni log de 29.080 líneas la contienen; Swagger sin `activationPassword` | CUMPLIDO | — |
| R26 | Swagger/OpenAPI de los endpoints | Consigna (parcial obligatorio) | Todos | `@Operation`, `@ApiResponse`, `@Schema` | AUD `/v3/api-docs`: activación 200/400/403/404/409; lecturas 200/400/404/409; `GET details` 200/404 con `page` y `size`; 4 DTO con ejemplos | CUMPLIDO | — |
| R27 | Colección Postman que pruebe las APIs | Consigna (parcial obligatorio) | Calvo | carpeta 03 (44 pedidos, 114 aserciones) | newman 65 pedidos, 160 aserciones, 0 fallas | CUMPLIDO | — |
| R28 | Persistencia sobre una base con órdenes existentes | Plan | Gonzalez | `ddl-auto=update`, columnas nulables, FK | AUD: 7 columnas nulables, FK a `orders`, `reading_timestamp` NOT NULL | CUMPLIDO | — |
| R29 | Sin regresiones sobre las Fases 1 y 2 | Plan | Todos | cambios aditivos | newman: 46 aserciones de las carpetas 00-02 pasan; `git diff --numstat -- src` sin líneas borradas | CUMPLIDO | — |
| R30 | Histórico paginado `GET /orders/{n}/details` | Decisión 11 (ratificada por Martín) | Calvo | `OrderRestController.details`, `OrderDetailBusiness.list`, `OrderDetailRepository.findByOrder` | AUD: `size` acotado a 100, `sort` ignorado, ids ascendentes, 404, 401 | CUMPLIDO | — |
| R31 | Eficiencia con ≈ 1 lectura por segundo | Plan 8.1 | Contreras | masa anterior desde la cabecera | AUD 100 lecturas: 35,5 ms promedio, sin degradación | CUMPLIDO | — |
| R32 | Documentación obligatoria: informe, guía A–J con ≥ 10 preguntas y PDF | `CLAUDE.md` 3.8 | Calvo | `docs/fase-3-carga-tiempo-real.md` y su PDF | 16 preguntas con 16 respuestas; PDF al día | CUMPLIDO (con F-03, F-05) | BAJO |
| R33 | Actualizar `CLAUDE.md` y el plan al cerrar la fase | `CLAUDE.md` 15.6 | Calvo | secciones 2, 5, 6, 7, 8, 9, 10 y 14 | Diff revisado: estado real, decisiones 18 a 28, hallazgos | CUMPLIDO (con F-04) | BAJO |
| R34 | Metodología de la cátedra (capas, `I*Business`, excepciones *checked*, `StandartResponse`, `Constants`, Lombok) | `CLAUDE.md` 3.2 y 6 | Todos | todas las clases nuevas | Contraste con `ProductRestController` y `Product` del profesor (sección I) | CUMPLIDO | — |
| R35 | Git: commits atómicos ≤ 100 líneas, formato `tipo(ámbito)`, sin coautoría de Claude, sin push a `main` | `CLAUDE.md` 3.7 | Todos | historial | 35/35 con formato correcto; ninguno con coautoría; 4 con > 100 líneas, todos dentro de las excepciones (documento completo e informe nuevos, JSON de Postman) | CUMPLIDO | — |
| R36 | PR como borrador y con la aprobación de 3 de 4 integrantes | `CLAUDE.md` 3.7, decisión 14 | Calvo / equipo | PR #7 | **No es borrador, sin descripción, 1 aprobación** (la de Joaquín) | **PARCIAL** | BAJO (F-06) y condición previa al merge |
| R37 | Robustez ante lecturas simultáneas de una misma orden | Plan (eficiencia), H-15 | Contreras | `OrderDetailBusiness.add` | AUD: 30 simultáneas → 9 × 200 y 21 × 500; en otra corrida 19 × 200 y 11 × 500; causa: *deadlock* de MySQL | **PARCIAL** | MEDIO (F-01) |
| R38 | Validación de datos externos: valores no finitos | Calidad | Contreras | `OrderDetailValidator` | AUD: `1e999` en masa, caudal o temperatura → 500 | **PARCIAL** | BAJO (F-02) |
| R39 | Campos y reglas iguales a la consigna original | Consigna (PDF) | Contreras | `DetailRequest`, `OrderDetailBusiness.add` | Contrastado con el PDF después de la auditoría (ver apéndice): reglas y entidades coinciden; **difiere la estampa de tiempo** (F-13) | **PARCIAL** | MEDIO (F-13) |
| R40 | Roles, alarmas por e-mail y frontend | Examen final | — | — | Fuera de alcance de la fase | NO APLICA | — |
| R41 | Frecuencia de almacenamiento configurable | Consigna (opcional) | — | — | Se guardan todas las lecturas | NO APLICA | — |
| R42 | Cierre de la orden y estado 3 | Fase 4 | — | — | `add` y `activate` rechazan todo estado distinto de 2 (lectura de código); no reproducido por mí | NO APLICA | — |

**Resumen:** 42 requisitos · **35 cumplidos** · 4 parciales (R36, R37, R38, R39) · 0 incumplidos · 0 no verificados · 3 no aplican.

---

## E. Auditoría por integrante

La atribución es **documental**: se apoya en el historial de Git y en el plan.

### Gonzalez — Parte 1 (modelo y activación): 7 commits de código y 5 de documentación

| Aspecto | Evaluación |
|---|---|
| Entregó lo asignado | Sí: 7 campos de `Order`, `OrderDetail` y su repositorio, `ForbiddenException`, `Constants.URL_INTEGRATION_LOADER`, `activate`, DTO y endpoint de activación |
| Corrección | R01 a R09 cumplidas con evidencia propia (76 comprobaciones sin fallas) |
| Arquitectura | Respeta las capas, `IOrderBusiness`/`OrderBusiness`, excepciones *checked*, `StandartResponse` y `Constants` |
| Dependencias dejadas | Completas: Contreras las usó sin cambiar el modelo (las entidades y campos no se modificaron después) |
| Cambios posteriores a sus commits | Solo dos: `activate` pasó a `saveAndFlush` (Calvo, `4ed54c8`) y el controller recibió el endpoint de lecturas (Contreras, `3861b3e`). El `fix` es correcto y consistente con el de Contreras |
| Defectos propios | Ninguno funcional. `activate` usaba `save`, que dejaba un error de base al confirmar sin `StandartResponse` (H-14, ya corregido) |

### Contreras — Parte 2 (lecturas y descarte): 8 commits de código y 2 de documentación

| Aspecto | Evaluación |
|---|---|
| Entregó lo asignado | Sí: `OrderDetailValidator` con 8 tests, `DetailResult`, `IOrderDetailBusiness`, `OrderDetailBusiness.add`, DTO y endpoint |
| Corrección | R10 a R23 cumplidas |
| Uso de lo de Gonzalez | Correcto: reutiliza `IOrderBusiness.load`, `OrderDetail`, los campos de cabecera y el controller sin tocar `activate` |
| Duplicación | Ninguna: el descarte vive solo en el validador |
| Decisión técnica propia | `saveAndFlush` en la actualización de la cabecera, para que un error de base salga como `StandartResponse` (comprobado en esta auditoría) |
| Defectos | **F-01** (500 por *deadlock* con lecturas simultáneas) y **F-02** (500 con valores infinitos); **F-05** (comentario del validador) |

### Calvo — Parte 3 (pruebas, Postman, auditoría y documentación): 6 commits de código/test y 7 de documentación

| Aspecto | Evaluación |
|---|---|
| Entregó lo asignado | Sí: carpeta Postman 03, informe con matriz de auditoría, guía de 16 preguntas, correcciones de `CLAUDE.md` / plan / informe de la Fase 2, PDF regenerados y PR |
| Integración de las partes | Correcta: no sobrescribió nada (cero líneas borradas en `src` y en la colección) |
| Cambios fuera de su archivo | Dos, documentados: `OrderBusiness.activate` (H-14) y la consulta del histórico (decisión 11, ratificada luego por Martín) |
| Pruebas | Declara 160 aserciones, 11 tests y 100 lecturas a 79,6 ms: **las dos primeras se reprodujeron exactas**, y la tercera da 35,5 ms en esta máquina (la diferencia es del equipo) |
| Documentación | Precisa en nombres y conteos; ver F-03 y F-04 |
| Defectos | F-03, F-04 y F-06 (todos documentales o de proceso) |

### Aprobación existente

La única aprobación del PR es la de `joacoContreras`, que es autor de la Parte 2 y dueño del repositorio. Es una aprobación válida para GitHub, pero no es independiente del trabajo. Faltan las de Martín González y Benjamín Alasino.

---

## F. Hallazgos

Ninguno **bloquea** el merge del código.

### Críticos y altos
Ninguno.

### Medio

**F-01 — Lecturas simultáneas de una misma orden: 500 por *deadlock* de MySQL** (R37) · Parte 2 · no bloquea
- **Archivo y método:** `OrderDetailBusiness.add`; el informe lo describe como H-15.
- **Evidencia:** 30 lecturas válidas simultáneas a la misma orden: en una corrida 9 respondieron 200 y **21 respondieron 500**; en otra corrida 19 y 11. El log muestra `Deadlock found when trying to get lock; try restarting transaction` en el `update orders`. Las filas guardadas coinciden con las lecturas aceptadas (no hay escrituras parciales).
- **Esperado:** el plan acepta la limitación (H-15, análoga a H-12 y N-2) y el informe dice que dos lecturas "pueden pisarse".
- **Observado:** además de pisarse, **fallan con 500** una parte importante de las lecturas válidas. La descripción de H-15 subestima el comportamiento.
- **Impacto:** con un caudalímetro que manda una lectura por segundo y se procesa en unos 35 ms no se solapan. Si hubiera reintentos o varios emisores sobre la misma orden, se perderían lecturas.
- **Corrección recomendada:** documentar el comportamiento real en H-15; opcionalmente, bloqueo pesimista de la orden al recibir una lectura o reintento ante *deadlock*.

### Bajos

**F-02 — Valores infinitos en una lectura responden 500** (R38) · Parte 2 · no bloquea
- **Archivo y método:** `OrderDetailValidator.discardReasons` / `OrderDetailBusiness.add`.
- **Evidencia:** `{"accumulatedMass":1e999,...}`, `"flowRate":1e999` y `"temperature":1e999` → 500; el log dice `'∞' is not a valid numeric or approximate numeric value`. Con `1.7e308` responde 200.
- **Impacto:** un valor fuera de rango del sensor no se descarta ni da 400. Se revierte sin dejar datos parciales.
- **Corrección:** tratar los valores no finitos (`Double.isFinite`) como motivo de descarte.

**F-03 — La guía atribuye algunos temas a clases que no los cubren** (R32) · Parte 3 · no bloquea
- **Archivo:** `docs/fase-3-carga-tiempo-real.md`, guía B.1 y B.5.
- **Evidencia:** B.1 cita `@Transactional(rollbackFor = Exception.class)` como "clase 3 de la cátedra" y B.5 cita las relaciones JPA como "clases 2 y 7". En `IW3_Guia_Estudio_Clases.pdf` la clase 3 trata REST y excepciones de dominio (p. 7), `@ManyToOne` aparece en la clase 6 (pp. 13-14) y `@Transactional` solo figura en un `@Modifying` de repositorio de la clase 7 (pp. 15-16). `CLAUDE.md` pide marcar lo complementario.
- **Corrección:** marcar `@Transactional(rollbackFor)` como complementario y citar la clase 6 para `@ManyToOne`.

**F-04 — Estados desactualizados en documentos** (R33) · Parte 3 / registro de traspasos
- **Evidencia:** en `docs/fase-3-division-de-trabajo.md` las filas del registro de las Partes 1 y 2 dicen "(local, sin push)" y la fila F del bloque de la Parte 3 dice "Pendiente (cuando lo pida Calvo)"; el informe (§11) dice "PR a `main` como borrador". El PR #7 ya existe y la rama está pusheada.
- **Corrección:** actualizar esos estados.

**F-05 — (RETIRADO) Comentario del validador sobre la unidad de la densidad** · Parte 2 · no bloquea
> Retirado tras ver la consigna: ella misma escribe "valor entre 0 y 1 (kg/m³)" (p. 2) y el comentario copia esa unidad.
- **Archivo:** `OrderDetailValidator.java`, línea 21: `@param density densidad recibida (kg/m3)`. La regla es `0 < densidad < 1`, sin esa unidad.
- **Corrección:** quitar la unidad.

**F-06 — El PR no es borrador y no tiene descripción** (R36) · Calvo · no bloquea
- **Evidencia:** `isDraft: false`, cuerpo de 0 caracteres. `CLAUDE.md` 3.7 pide abrir los PR como borrador mientras GitHub no exija las 3 aprobaciones, y la división pedía describir los cambios.
- **Corrección:** agregar la descripción en GitHub y, si se quiere respetar la regla, volver a borrador.

### Observaciones

- **F-07** — Las reglas de `activate` y `add` solo se prueban con Postman (más el validador y el generador con JUnit): no hay pruebas de integración en Java. Cumple la consigna (que exige Postman), pero la cobertura automática de Maven es limitada.
- **F-08** — `registerTare` (Fase 2) sigue usando `save`, el mismo patrón de H-14. El informe lo reconoce como fuera de alcance.
- **F-09** — (Actualizada) La consigna original se localizó después de la auditoría: ver el apéndice. `IW3_Guia_Estudio_Clases.pdf` sigue siendo un resumen personal, no un documento oficial.
- **F-10** — Calvo aprobó por su cuenta la decisión 11 y modificó `activate`; Martín la ratificó. Los documentos la registran como "aprobada por Tomás Calvo" y el equipo decidió dejarlo así.
- **F-11** — Limitaciones aceptadas y documentadas: contraseña de 5 dígitos sin límite de intentos (H-17), token de 1 hora (H-18), `timestamp` con `Z` que pierde la zona (H-19), fin de carga anterior al inicio si se mezclan lecturas con y sin `timestamp` (H-20) y números aceptados como texto (H-21).
- **F-12** — No hay integración continua: ningún check automático respalda el PR.

---

## G. Auditoría técnica

### Arquitectura y responsabilidades
Correcta y coherente con el material del profesor: `integration/loader/controllers` solo traduce HTTP; las reglas están en `business` (`OrderBusiness.activate`, `OrderDetailBusiness.add`, `OrderDetailValidator`); los repositorios no tienen reglas. El validador es una función pura sin Spring. No hay duplicación: `OrderDetailBusiness` reutiliza `IOrderBusiness.load`. No hay código de las Fases 4 o 5 ni dependencias nuevas (`pom.xml` sin cambios).

### Entidades y JPA
- `OrderDetail.order`: `@ManyToOne(optional = false, fetch = LAZY)` con `@JsonIgnore`; no carga ni publica la orden.
- `Order` **no** tiene `@OneToMany` de detalles (verificado en el JSON: no hay lista).
- Las 7 columnas nuevas de `orders` son nulables; `order_details` tiene FK a `orders` y `reading_timestamp`, las magnitudes y `id_order` como NOT NULL.
- Sin riesgo de N+1 en el histórico: la consulta trae solo las lecturas.

### Controladores y API REST
Rutas coherentes bajo `/api/v1/integration/loader` y `/api/v1/orders/{n}/details`; códigos 200, 400, 401, 403, 404, 409 y 500 verificados con pedidos reales; el cuerpo de error es `StandartResponse`. El `GET` de histórico ignora el `sort` del cliente y acota `size` a 100. Valores raros de paginación (`size=0`, `page=-1`, `page=abc`) responden 200 con los valores por defecto, igual que `GET /orders`.

### Servicios y reglas
Las comprobaciones van fuera del `try` y en el orden de D7, verificado con pedidos que combinan dos errores. `@Transactional(rollbackFor = Exception.class)` y `saveAndFlush` aseguran que un error de base salga como `StandartResponse` y revierta todo (comprobado con un `CHECK` temporal en la base aislada).

### Validaciones y excepciones
Ausente = 400, inválido = descarte (por el uso de `Double`/`Integer`). `!(x > 0)` descarta `NaN`. Brecha: valores infinitos (F-02).

### Seguridad
Los tres endpoints exigen JWT (401 verificado). La contraseña no aparece en JSON, mensajes de error, respuestas ni en el log de 29.080 líneas. Los 500 incluyen la traza en `devInfo` por `dev.info.enabled=true` (hallazgo heredado H-5, fuera de esta fase). Limitaciones aceptadas: sin límite de intentos y token de 1 hora.

### Calidad del código
Sin imports sin usar en los archivos de la fase, comentarios en español, identificadores en inglés. Un comentario inexacto (F-05).

---

## H. Pruebas

| Comando o prueba | Requisito cubierto | Resultado | Evidencia | Limitaciones |
|---|---|---|---|---|
| `./mvnw -B -q -DskipTests compile` | Integridad | **OK** | exit 0 | — |
| `./mvnw -B test` (base aislada `iw3_audit`) | R11 a R14, `contextLoads` | **11 tests, 0 fallas** (2 generador, 8 validador, 1 `contextLoads`) | `BUILD SUCCESS` | Sin pruebas de integración en Java |
| Arranque de la app sobre una base nueva | R28 | **OK**, 12,4 s, 0 `ERROR` | log | — |
| `npx newman@6 run ...` (colección completa) | R27, R29 | **65 pedidos, 160 aserciones, 0 fallas** | salida de newman | Corre sobre la base aislada |
| Batería de auditoría propia (76 comprobaciones) | R01 a R31 | **76 PASS, 0 FAIL** | script temporal, ya eliminado | No es parte del repo |
| Atomicidad con `CHECK` temporal (base aislada) | R24 | **OK**: 500 con `StandartResponse`, detalle revertido | AUD | — |
| 100 lecturas seguidas | R31 | 35,5 ms promedio; primeras 10: 40,6 ms; últimas 10: 32,9 ms | AUD | Hardware local |
| 30 lecturas simultáneas | R37 | **9 × 200 y 21 × 500** (*deadlock*) | log y AUD | Es un caso límite |
| Valores `1e999` | R38 | **500** | log y AUD | — |
| `GET /v3/api-docs` | R26 | OK | AUD | No se abrió Swagger UI |
| Inspección estática de Postman | R27 | OK: sin tokens ni credenciales reales (solo `admin/admin123` de desarrollo), variables propias de la carpeta 03 definidas, 3 pedidos sin token | Script | — |
| Simulación del estado 3 y *trigger* de Calvo | R42 | **No reproducido** | — | Se verificó por lectura de código |

Casos cubiertos por la batería: éxito, datos inválidos, campos ausentes, recursos inexistentes, conflictos de estado, orden de comprobaciones, atomicidad, concurrencia y valores extremos. Regresiones sobre la Fase 2 por newman (46 aserciones).

---

## I. Documentación

| Documento | Estado | Observaciones |
|---|---|---|
| Informe técnico (`docs/fase-3-carga-tiempo-real.md`) | **Completo con errores menores** | 22 referencias `Clase.método` verificadas; las 16 rutas `.java` existen; los 18 archivos de `src` del PR figuran en el inventario; los conteos (160, 11, 44, 114) son ciertos. Ver F-04 |
| Guía de estudio (secciones A–J) | **Completa con errores menores** | 16 preguntas con 16 respuestas; ejemplos coherentes con la API. Ver F-03 |
| Swagger/OpenAPI | **Correcto** | Verificado en `/v3/api-docs` |
| Postman | **Correcto** | 44 pedidos y 114 aserciones en la carpeta 03; cero líneas borradas en la colección |
| PDF | **Al día** | Los 4 PDF son posteriores a su Markdown |
| `CLAUDE.md` y plan | **Actualizados** | Estado real tras la Fase 3, decisiones 18 a 28, la fila de despliegue y el 409 resuelto |
| Referencias al material del profesor | **Verificables, con un error de atribución** | Ver F-03 |

### Contraste con el material del profesor

| Aspecto | Material | Código de la Fase 3 |
|---|---|---|
| Capas Model → Repository → Business → Controller | Guía p. 2 | Respeta |
| Interfaz + implementación con `@Autowired` | Guía p. 5 (clase 2) | `IOrderBusiness`/`OrderBusiness`, `IOrderDetailBusiness`/`OrderDetailBusiness` |
| Controllers con `try/catch` por método, `ResponseEntity<?>`, `StandartResponse` | Guía p. 7 (clase 3); `ProductRestController` del repo | Mismo estilo en `OrderLoaderRestController` |
| Tipos objeto para representar "sin valor" | Guía p. 9 (clase 4, `Long` frente a `long`) | `Double`/`Integer` en los DTO y en los campos opcionales de la entidad |
| `@ManyToOne` | Guía pp. 13-14 (clase 6) | `OrderDetail.order` |
| `@Modifying` / consultas propias | Guía pp. 15-16 (clase 7) | `@Query` JPQL en `findByOrder` |
| Obtener el usuario del token | Guía pp. 20-22 (clase 9) | No se necesita en esta fase |

**Desvíos justificados y documentados** (marcados como "Nuestro" o "complementario" en `CLAUDE.md`): `@Transactional(rollbackFor)` en la capa de negocio, DTO por sistema externo, `PagedModel`, 403 y 409 como excepciones nuevas, y `LAZY`.

---

## J. Integración y regresiones

- **Integración:** el circuito completo (SAP → tara → activación → lecturas → histórico) funciona. Los puntos de unión se probaron: el controller de Gonzalez recibe el endpoint de Contreras sin romper la activación; `OrderDetailBusiness` usa `IOrderBusiness.load`; el histórico de Calvo usa el repositorio de Gonzalez con la consulta que agregó.
- **Regresiones:** ninguna. newman mantiene las 46 aserciones de las carpetas 00 a 02; no hay líneas borradas en `src`; los endpoints de las Fases 1 y 2 responden igual.
- **Cambios sobre archivos de otra parte:** solo dos (`OrderBusiness.activate` y `OrderDetailRepository`), ambos aditivos o de una línea y documentados.
- **Compatibilidad hacia adelante:** `add` y `activate` rechazan todo estado distinto de 2 (por lectura de código), por lo que una Fase 4 que pase la orden a estado 3 las bloquea sin tocar esta fase.

---

## K. Acciones requeridas antes del merge

**Ninguna corrección de código es obligatoria.**

**Condición de proceso (decisión 14):**
1. Reunir las **3 aprobaciones** acordadas. Hoy hay 1 (Joaquín). Faltan **Martín González y Benjamín Alasino**. GitHub permitiría mergear con 1, pero contradice el acuerdo del equipo.
2. Tener presente que si se pushea cualquier commit nuevo al PR, la regla del repositorio **descarta las aprobaciones** y habría que repetirlas.

**Recomendadas, no bloqueantes**
1. Agregar una descripción al PR (F-06).
2. Actualizar los estados desactualizados de los documentos (F-04) y corregir las dos atribuciones de la guía (F-03) y el comentario del validador (F-05). Cada cambio al PR requiere repetir las aprobaciones, así que conviene agruparlos en un solo push o hacerlos en un PR posterior.
3. Registrar H-15 con el comportamiento real: 500 por *deadlock* con lecturas simultáneas (F-01).
4. Decidir si se tratan los valores no finitos como descarte (F-02), en esta fase o en la Fase 5.

**Para volver a auditar** (si se agregan commits): repetir compilación, `./mvnw -B test` y newman, y revisar solo el diff nuevo.

**Pendientes ajenos al PR:** que el dueño del repositorio suba el ruleset a 3 aprobaciones y active el borrado de ramas al mergear, y redesplegar la VM (sigue con la Fase 1).

---

## L. Conclusión

1. **¿La Fase 3 cumple con la consigna?** Sí, con una diferencia: 35 de los 39 requisitos aplicables están cumplidos con evidencia y 4 son parciales (robustez, proceso y la estampa de tiempo, que la consigna define como momento de recepción, F-13). Contrastado con el PDF original.
2. **¿Respeta el material y la metodología del profesor?** Sí. Sigue los patrones de las clases 2, 3, 4 y 6 y declara sus desvíos. La guía tiene una imprecisión al citar las clases de dos temas (F-03).
3. **¿Las tres partes están correctamente implementadas e integradas?** Sí. Cada parte cumple lo asignado y el circuito completo funciona sin regresiones. Los dos defectos técnicos encontrados (F-01 y F-02) afectan casos límite de la Parte 2 y ninguno bloquea.
4. **¿La documentación es correcta y suficiente?** Suficiente y en general precisa, con imprecisiones menores (F-03, F-04, F-05).
5. **¿Las pruebas aportan evidencia suficiente?** Sí para los requisitos de la fase: 11 tests, newman con 160 aserciones y la batería de auditoría de 76 comprobaciones. La cobertura automática en Java es acotada (F-07) y no hay integración continua (F-12).
6. **¿El PR está listo para aprobarse?** **Sí**, con las observaciones de este informe.
7. **¿Está listo para mergearse?** **Técnicamente sí. Según las reglas del equipo, todavía no:** hay 1 de las 3 aprobaciones acordadas.
8. **¿Qué riesgos residuales existen?** Pérdida de lecturas con 500 si hubiera solicitudes simultáneas sobre la misma orden (F-01); un valor infinito produce 500 (F-02); sin límite de intentos para la contraseña de 5 dígitos y token de 1 hora (limitaciones de seguridad para el final); la estampa de tiempo que el cliente puede enviar y la consigna no prevé (F-13); y la VM sin redesplegar.
