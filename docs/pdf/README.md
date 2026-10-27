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
| [guia-estudio-src.pdf](guia-estudio-src.pdf) | [docs/guia-estudio-src.md](../guia-estudio-src.md) (se actualiza en cada fase) |

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
node docs/pdf/md2pdf.js docs/guia-estudio-src.md docs/pdf/guia-estudio-src.pdf "Guía de estudio del código"
```

Los paquetes de npm quedan fuera de git (ver `.gitignore`).
