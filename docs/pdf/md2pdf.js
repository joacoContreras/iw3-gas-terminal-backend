// Convierte un documento Markdown del repo a PDF (A4, con numeración de páginas).
//
// Requisitos: Node.js 18+, Google Chrome o Edge instalado, y los paquetes:
//   npm install --prefix docs/pdf marked@12 puppeteer-core@23
// Uso (desde la raíz del repo):
//   node docs/pdf/md2pdf.js docs/fase-0-auditoria.md docs/pdf/fase-0-auditoria.pdf "Fase 0 · Auditoría y línea base"
// Si Chrome no está en la ruta por defecto: definir la variable CHROME_PATH.
const fs = require('fs');
const path = require('path');
const { marked } = require('marked');
const puppeteer = require('puppeteer-core');

const [input, output, footerLabel] = process.argv.slice(2);
if (!input || !output || !footerLabel) {
  console.error('Uso: node md2pdf.js <entrada.md> <salida.pdf> "<etiqueta de pie>"');
  process.exit(1);
}
const CHROME = process.env.CHROME_PATH || 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';

const css = `
  @page { size: A4; }
  * { box-sizing: border-box; }
  body { font-family: "Segoe UI", Arial, sans-serif; font-size: 10pt; line-height: 1.45; color: #1b1f24; margin: 0; }
  .kicker { font-size: 8.5pt; letter-spacing: .04em; text-transform: uppercase; color: #5b6b7b; margin-bottom: 6px; }
  h1 { font-size: 20pt; color: #1f4e79; border-bottom: 2px solid #1f4e79; padding-bottom: 4px; margin: 0 0 10px; page-break-after: avoid; }
  h2 { font-size: 14pt; color: #1f4e79; border-bottom: 1px solid #c9d6e2; padding-bottom: 2px; margin: 20px 0 8px; page-break-after: avoid; }
  h3 { font-size: 11.5pt; color: #2b5d8a; margin: 14px 0 6px; page-break-after: avoid; }
  p { margin: 6px 0; }
  ul, ol { padding-left: 22px; margin: 6px 0; }
  li { margin: 2px 0; }
  li:has(> input[type=checkbox]) { list-style: none; margin-left: -18px; }
  li input[type=checkbox] { margin-right: 6px; }
  table { border-collapse: collapse; width: 100%; margin: 8px 0 12px; font-size: 8.6pt; }
  thead { display: table-header-group; }
  tr { page-break-inside: avoid; }
  th { background: #1f4e79; color: #fff; text-align: left; }
  th, td { border: 1px solid #b8c4d0; padding: 3px 6px; vertical-align: top; overflow-wrap: anywhere; }
  th.nw, td.nw { white-space: nowrap; overflow-wrap: normal; }
  tbody tr:nth-child(even) td { background: #f3f6f9; }
  code { font-family: Consolas, "Cascadia Mono", "Courier New", monospace; font-size: .92em; background: #eef1f4; padding: 0 3px; border-radius: 3px; }
  th code { background: rgba(255,255,255,.2); color: #fff; }
  pre { background: #f6f8fa; border: 1px solid #d0d7de; border-radius: 4px; padding: 8px 10px; font-size: 8pt;
        line-height: 1.35; white-space: pre-wrap; overflow-wrap: anywhere; page-break-inside: avoid; margin: 8px 0; }
  pre code { background: none; padding: 0; font-size: inherit; }
  blockquote { border-left: 4px solid #1f4e79; margin: 8px 0; padding: 4px 12px; background: #eef4fa; color: #243447; }
  blockquote p { margin: 3px 0; }
  hr { border: 0; border-top: 1px solid #c9d6e2; margin: 18px 0; }
  strong { color: #11263c; }
`;

(async () => {
  const md = fs.readFileSync(input, 'utf8');
  let body = marked.parse(md, { gfm: true, breaks: false });
  // Las celdas cortas (números de fila, palabras sueltas) no se parten en columnas angostas
  body = body.replace(new RegExp('<(td|th)>([^<\\s]{1,16}|[^<]{1,4})</(td|th)>', 'g'), '<$1 class="nw">$2</$3>');
  const html = `<!doctype html><html lang="es"><head><meta charset="utf-8"><title>${footerLabel}</title>
<style>${css}</style></head><body>
<div class="kicker">Ingeniería Web III · TP Integrador 2026 · iw3-gas-terminal-backend</div>
${body}
</body></html>`;

  const browser = await puppeteer.launch({ executablePath: CHROME, headless: true, args: ['--no-sandbox'] });
  const page = await browser.newPage();
  await page.setContent(html, { waitUntil: 'load' });
  const footer = `<div style="width:100%;font-family:Arial,sans-serif;font-size:7.5px;color:#5b6b7b;padding:0 14mm;
      display:flex;justify-content:space-between;"><span>IW3 · Terminal de gas licuado — ${footerLabel}</span>
      <span>Página <span class="pageNumber"></span> de <span class="totalPages"></span></span></div>`;
  await page.pdf({
    path: output, format: 'A4', printBackground: true, displayHeaderFooter: true,
    headerTemplate: '<span></span>', footerTemplate: footer,
    margin: { top: '16mm', bottom: '16mm', left: '14mm', right: '14mm' },
  });
  await browser.close();
  console.log('OK', path.basename(output), (fs.statSync(output).size / 1024).toFixed(0) + ' KB');
})().catch((e) => { console.error('ERROR', e.message); process.exit(1); });
