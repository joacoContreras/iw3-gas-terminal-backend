package ar.edu.iua.iw3.util;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.web.util.HtmlUtils;

import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.TemperatureAlarm;

/** Immutable mail snapshot, rendered inside the transaction with AetherLoad's visual identity. */
public record TemperatureAlarmEmail(String subject, String text, String html) {

	private static final Locale AR = Locale.forLanguageTag("es-AR");
	private static final DateTimeFormatter WHEN = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public static TemperatureAlarmEmail from(Order order, TemperatureAlarm alarm) {
		String number = String.valueOf(alarm.getOrderNumber());
		String temperature = String.format(AR, "%.1f °C", alarm.getTemperature());
		String threshold = String.format(AR, "%.1f °C", alarm.getThreshold());
		String when = alarm.getTriggeredAt().format(WHEN) + " (hora de Argentina, UTC−03:00)";
		String plate = order.getTruck().getPlate();
		String customer = order.getCustomer().getCompanyName();
		String product = order.getProduct().getName();
		String action = "Solicitamos al personal responsable verificar las condiciones de la operación y gestionar "
				+ "la alarma desde el módulo Alarmas de AetherLoad, de acuerdo con los procedimientos internos de la terminal.";
		String policy = "Este aviso se emite una sola vez por alarma. No se generarán nuevos avisos para esta orden "
				+ "mientras la alarma permanezca pendiente de aceptación por un usuario autorizado.";
		String text = "AetherLoad | Notificación operativa\n\nAlerta de temperatura — Orden #" + number
				+ "\n\nSe ha registrado una temperatura superior al umbral configurado durante la operación de carga."
				+ "\nEstado al momento del aviso: Pendiente de aceptación"
				+ "\n\nTemperatura registrada: " + temperature + "\nUmbral configurado: " + threshold
				+ "\n\nOrden: " + number + "\nCamión: " + plate + "\nCliente: " + customer
				+ "\nProducto: " + product + "\nFecha y hora: " + when
				+ "\n\nAcción requerida\n" + action + "\n\n" + policy
				+ "\n\nAetherLoad · Gestión de operaciones de carga\nNotificación automática. No responda a este correo.\n";
		String rows = row("Orden de carga", "#" + number) + row("Camión · Patente", plate)
				+ row("Cliente", customer) + row("Producto", product) + row("Fecha y hora del evento", when);
		// Tables and inline styles work without external CSS, images, scripts or web fonts.
		String html = """
				<!doctype html>
				<html lang="es">
				<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
				<meta name="color-scheme" content="dark"><title>Alerta de temperatura · AetherLoad</title></head>
				<body style="margin:0;padding:0;background-color:#001827;color:#F4F8FB;font-family:'Segoe UI',Helvetica,sans-serif;">
				<div style="display:none;max-height:0;overflow:hidden;mso-hide:all;">Orden #%s: temperatura superior al umbral configurado. Se requiere revisión operativa.</div>
				<table role="presentation" width="100%%" cellpadding="0" cellspacing="0" bgcolor="#001827" style="background-color:#001827;">
				<tr><td align="center" style="padding:32px 12px;">
				<table role="presentation" width="600" cellpadding="0" cellspacing="0" style="width:100%%;max-width:600px;border:1px solid #285362;border-radius:16px;background-color:#073747;">
				<tr><td style="padding:28px 24px;background-color:#002B3B;border-top:4px solid #00D6D2;border-radius:16px 16px 0 0;">
				<p style="margin:0;color:#F4F8FB;font-size:26px;font-weight:700;letter-spacing:-1px;">AetherLoad<span style="color:#00D6D2;">.</span></p>
				<p style="margin:8px 0 0;color:#ABC3CE;font-size:12px;letter-spacing:2px;text-transform:uppercase;">Notificación operativa</p>
				</td></tr>
				<tr><td style="padding:28px 24px 16px;">
				<p style="margin:0 0 12px;color:#FFB4BF;font-size:12px;font-weight:600;letter-spacing:1px;text-transform:uppercase;">Requiere atención · Temperatura</p>
				<h1 style="margin:0;color:#F4F8FB;font-size:26px;line-height:1.25;font-weight:600;">Alerta en la orden #%s</h1>
				<p style="margin:16px 0;color:#ABC3CE;font-size:15px;line-height:1.6;">Se ha registrado una temperatura superior al umbral configurado durante la operación de carga.</p>
				<p style="margin:0;color:#FFB4BF;font-size:13px;line-height:1.5;">Estado al momento del aviso: <strong>Pendiente de aceptación</strong></p>
				</td></tr>
				<tr><td style="padding:8px 24px 24px;">
				<table role="presentation" width="100%%" cellpadding="0" cellspacing="0"><tr>
				<td width="50%%" valign="top" style="padding:16px 12px;background-color:#002B3B;border-left:3px solid #FB7185;">
				<p style="margin:0 0 8px;color:#ABC3CE;font-size:12px;line-height:1.5;">Temperatura registrada</p>
				<p style="margin:0;color:#FFB4BF;font-size:26px;font-weight:600;">%s</p></td>
				<td width="50%%" valign="top" style="padding:16px 12px;background-color:#002B3B;border-left:1px solid #285362;">
				<p style="margin:0 0 8px;color:#ABC3CE;font-size:12px;line-height:1.5;">Umbral configurado</p>
				<p style="margin:0;color:#F4F8FB;font-size:26px;font-weight:600;">%s</p></td>
				</tr></table>
				</td></tr>
				<tr><td style="padding:0 24px 24px;">
				<h2 style="margin:0 0 12px;color:#F4F8FB;font-size:16px;font-weight:600;">Detalle de la operación</h2>
				<table width="100%%" cellpadding="0" cellspacing="0" style="border-collapse:collapse;font-size:14px;line-height:1.5;">%s</table>
				</td></tr>
				<tr><td style="padding:0 24px 24px;">
				<table role="presentation" width="100%%" cellpadding="0" cellspacing="0"><tr><td style="padding:16px;background-color:#002B3B;border-left:3px solid #00D6D2;">
				<h2 style="margin:0 0 8px;color:#00D6D2;font-size:15px;font-weight:600;">Acción requerida</h2>
				<p style="margin:0;color:#F4F8FB;font-size:14px;line-height:1.6;">%s</p>
				</td></tr></table>
				<p style="margin:16px 0 0;color:#ABC3CE;font-size:12px;line-height:1.6;">%s</p>
				</td></tr>
				<tr><td style="padding:20px 24px;border-top:1px solid #285362;">
				<p style="margin:0;color:#ABC3CE;font-size:12px;line-height:1.6;">AetherLoad · Gestión de operaciones de carga<br>Notificación automática. No responda a este correo.</p>
				</td></tr></table>
				</td></tr></table></body></html>
				""".formatted(escape(number), escape(number), escape(temperature), escape(threshold), rows,
						escape(action), escape(policy));
		return new TemperatureAlarmEmail("[AetherLoad] Alerta de temperatura | Orden #" + number, text, html);
	}

	private static String row(String label, String value) {
		return "<tr><th scope=\"row\" align=\"left\" valign=\"top\" style=\"width:38%;padding:12px 8px 12px 0;border-bottom:1px solid #285362;color:#ABC3CE;font-weight:400;\">"
				+ escape(label) + "</th><td style=\"padding:12px 0;border-bottom:1px solid #285362;color:#F4F8FB;overflow-wrap:anywhere;word-break:break-word;\">"
				+ escape(value) + "</td></tr>";
	}

	private static String escape(String value) {
		return HtmlUtils.htmlEscape(value == null || value.isBlank() ? "Sin datos" : value, "UTF-8");
	}
}
