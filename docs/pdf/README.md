# Documentos en PDF

Copias en PDF de los informes de cada fase, para revisarlos sin abrir los `.md`.
La fuente de verdad son los Markdown de `docs/`: **si cambian, hay que regenerar el PDF**.

| PDF | Fuente |
|---|---|
| [fase-0-auditoria.pdf](fase-0-auditoria.pdf) | [docs/fase-0-auditoria.md](../fase-0-auditoria.md) |
| [fase-1-recepcion-ordenes.pdf](fase-1-recepcion-ordenes.pdf) | [docs/fase-1-recepcion-ordenes.md](../fase-1-recepcion-ordenes.md) |

## Cómo regenerarlos

Requiere Node.js 18+ y Google Chrome (o Edge; indicar la ruta en `CHROME_PATH`).

```bash
npm install --prefix docs/pdf marked@12 puppeteer-core@23
node docs/pdf/md2pdf.js docs/fase-0-auditoria.md docs/pdf/fase-0-auditoria.pdf "Fase 0 · Auditoría y línea base"
node docs/pdf/md2pdf.js docs/fase-1-recepcion-ordenes.md docs/pdf/fase-1-recepcion-ordenes.pdf "Fase 1 · Recepción de órdenes"
```

Los paquetes de npm quedan fuera de git (ver `.gitignore`).
