# Documentos en PDF

Copias en PDF de los informes de cada fase, para revisarlos sin abrir los `.md`.
La fuente de verdad son los Markdown de `docs/`: **si cambian, hay que regenerar el PDF**.

| PDF | Fuente |
|---|---|
| [fase-0-auditoria.pdf](fase-0-auditoria.pdf) | [docs/fase-0-auditoria.md](../fase-0-auditoria.md) |
| [fase-1-recepcion-ordenes.pdf](fase-1-recepcion-ordenes.pdf) | [docs/fase-1-recepcion-ordenes.md](../fase-1-recepcion-ordenes.md) |
| [fase-2-pesaje-inicial.pdf](fase-2-pesaje-inicial.pdf) | [docs/fase-2-pesaje-inicial.md](../fase-2-pesaje-inicial.md) |
| [fase-2-division-de-trabajo.pdf](fase-2-division-de-trabajo.pdf) | [docs/fase-2-division-de-trabajo.md](../fase-2-division-de-trabajo.md) (guía y prompts de las 4 etapas) |
| [fase-3-carga-tiempo-real.pdf](fase-3-carga-tiempo-real.pdf) | [docs/fase-3-carga-tiempo-real.md](../fase-3-carga-tiempo-real.md) |
| [fase-3-division-de-trabajo.pdf](fase-3-division-de-trabajo.pdf) | [docs/fase-3-division-de-trabajo.md](../fase-3-division-de-trabajo.md) (guía y prompts de las 3 partes, trabajo secuencial) |
| [fase-4-cierre-orden.pdf](fase-4-cierre-orden.pdf) | [docs/fase-4-cierre-orden.md](../fase-4-cierre-orden.md) (informe y guía de estudio de la Fase 4) |
| [fase-5-conciliacion.pdf](fase-5-conciliacion.pdf) | [docs/fase-5-conciliacion.md](../fase-5-conciliacion.md) (informe, auditoría final del segundo parcial y guía de estudio de la Fase 5) |
| [fase-5-division-de-trabajo.pdf](fase-5-division-de-trabajo.pdf) | [docs/fase-5-division-de-trabajo.md](../fase-5-division-de-trabajo.md) (guía y prompts de las 2 partes, trabajo secuencial: Calvo y Alasino) |
| [final-fase-1-roles.pdf](final-fase-1-roles.pdf) | [docs/final-fase-1-roles.md](../final-fase-1-roles.md) (examen final, Fase 1: roles, matriz por endpoint y guía de defensa) |
| [final-fase-2-alarmas.pdf](final-fase-2-alarmas.pdf) | [docs/final-fase-2-alarmas.md](../final-fase-2-alarmas.md) (examen final, Fase 2: alarma por correo, defectos hallados y guía de defensa) |
| [final-fase-3-cierre-backend.pdf](final-fase-3-cierre-backend.pdf) | [docs/final-fase-3-cierre-backend.md](../final-fase-3-cierre-backend.md) (examen final, Fase 3: CORS, 401, filtros del listado, H-5 y guía de defensa) |
| [guia-estudio-src.pdf](guia-estudio-src.pdf) | [docs/guia-estudio-src.md](../guia-estudio-src.md) (se actualiza en cada fase) |
| [Guia_Inicio_Fase_4.pdf](Guia_Inicio_Fase_4.pdf) | [docs/fase-4-guia-de-inicio.md](../fase-4-guia-de-inicio.md) (alcance, plan, checklist y prompt de la Fase 4; responsable: Benjamín Alasino) |

## Documento de referencia (no se regenera)

| PDF | Qué es |
|---|---|
| [consigna-trabajo-integrador-2026.pdf](consigna-trabajo-integrador-2026.pdf) | **Consigna oficial** del trabajo integrador (9 páginas), tal como la entregó la cátedra. Es la fuente que manda ante cualquier duda de requisitos |

## Cómo regenerarlos

Requiere Node.js 18+ y Google Chrome (o Edge; indicar la ruta en `CHROME_PATH`).

```bash
npm install --prefix docs/pdf marked@12 puppeteer-core@23
node docs/pdf/md2pdf.js docs/fase-0-auditoria.md docs/pdf/fase-0-auditoria.pdf "Fase 0 · Auditoría y línea base"
node docs/pdf/md2pdf.js docs/fase-1-recepcion-ordenes.md docs/pdf/fase-1-recepcion-ordenes.pdf "Fase 1 · Recepción de órdenes"
node docs/pdf/md2pdf.js docs/fase-2-pesaje-inicial.md docs/pdf/fase-2-pesaje-inicial.pdf "Fase 2 · Pesaje inicial"
node docs/pdf/md2pdf.js docs/fase-3-carga-tiempo-real.md docs/pdf/fase-3-carga-tiempo-real.pdf "Fase 3 · Carga y datos en tiempo real"
node docs/pdf/md2pdf.js docs/fase-3-division-de-trabajo.md docs/pdf/fase-3-division-de-trabajo.pdf "Fase 3 · División del trabajo"
node docs/pdf/md2pdf.js docs/fase-4-cierre-orden.md docs/pdf/fase-4-cierre-orden.pdf "Fase 4 · Cierre de la orden"
node docs/pdf/md2pdf.js docs/fase-5-conciliacion.md docs/pdf/fase-5-conciliacion.pdf "Fase 5 · Pesaje final y conciliación"
node docs/pdf/md2pdf.js docs/fase-5-division-de-trabajo.md docs/pdf/fase-5-division-de-trabajo.pdf "Fase 5 · División del trabajo"
node docs/pdf/md2pdf.js docs/final-fase-1-roles.md docs/pdf/final-fase-1-roles.pdf "Final · Fase 1 · Roles"
node docs/pdf/md2pdf.js docs/final-fase-2-alarmas.md docs/pdf/final-fase-2-alarmas.pdf "Final · Fase 2 · Alarma de temperatura"
node docs/pdf/md2pdf.js docs/final-fase-3-cierre-backend.md docs/pdf/final-fase-3-cierre-backend.pdf "Final · Fase 3 · Cierre del backend"
node docs/pdf/md2pdf.js docs/guia-estudio-src.md docs/pdf/guia-estudio-src.pdf "Guía de estudio del código"
node docs/pdf/md2pdf.js docs/fase-4-guia-de-inicio.md docs/pdf/Guia_Inicio_Fase_4.pdf "Fase 4 · Guía de inicio (Benjamín Alasino)"
```

Los paquetes de npm quedan fuera de git (ver `.gitignore`).
