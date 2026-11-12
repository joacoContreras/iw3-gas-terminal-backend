package ar.edu.iua.iw3.business;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.model.AlarmConfig;
import ar.edu.iua.iw3.model.persistence.AlarmConfigRepository;
import lombok.extern.slf4j.Slf4j;

/** Administra la configuracion global de la alarma de temperatura. */
@Service
@Slf4j
public class AlarmConfigBusiness implements IAlarmConfigBusiness {

	static final int MAX_RECIPIENTS = 20;
	private static final int MAX_EMAIL_LENGTH = 255;
	/** Formato razonable de correo (algo@dominio.tld, sin espacios, comas ni punto y coma). */
	private static final Pattern EMAIL = Pattern.compile("^[^@\\s,;]+@[^@\\s,;]+\\.[^@\\s,;]+$");

	@Autowired
	private AlarmConfigRepository configDAO;

	@Value("${alarm.default-threshold:30.0}")
	private double defaultThreshold;

	@Override
	public AlarmConfig get() throws BusinessException {
		try {
			return configDAO.findById(AlarmConfig.SINGLE_ID).orElseGet(this::defaultConfig);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	private AlarmConfig defaultConfig() {
		AlarmConfig c = new AlarmConfig();
		c.setTemperatureThreshold(defaultThreshold);
		c.setRecipientEmails(new ArrayList<>());
		return c;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public AlarmConfig update(AlarmConfig in) throws BadRequestException, BusinessException {
		// Validaciones fuera del try: si no, el 400 saldria como 500
		List<String> errors = new ArrayList<>();
		Double threshold = in == null ? null : in.getTemperatureThreshold();
		if (threshold == null || threshold.isNaN() || threshold.isInfinite()) {
			errors.add("temperatureThreshold es obligatorio y debe ser un número finito");
		}
		List<String> emails = normalizeEmails(in == null ? null : in.getRecipientEmails(), errors);
		if (!errors.isEmpty()) {
			throw BadRequestException.builder().message(String.join("; ", errors)).build();
		}
		try {
			// Se copian solo los campos permitidos; el id es siempre el de la unica fila
			AlarmConfig config = new AlarmConfig();
			config.setTemperatureThreshold(threshold);
			config.setRecipientEmails(emails);
			return configDAO.saveAndFlush(config);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	/** Recorta, valida y quita repetidos (sin distinguir mayusculas); acumula los problemas en errors. */
	private List<String> normalizeEmails(List<String> raw, List<String> errors) {
		if (raw == null) {
			errors.add("recipientEmails es obligatorio (puede ser una lista vacía)");
			return List.of();
		}
		Map<String, String> unique = new LinkedHashMap<>();
		for (String item : raw) {
			String email = item == null ? "" : item.trim();
			if (email.isEmpty()) {
				errors.add("hay un correo vacío en recipientEmails");
			} else if (email.length() > MAX_EMAIL_LENGTH || !EMAIL.matcher(email).matches()) {
				errors.add("correo inválido: " + (email.length() > 60 ? email.substring(0, 60) + "…" : email));
			} else {
				unique.putIfAbsent(email.toLowerCase(Locale.ROOT), email);
			}
		}
		if (unique.size() > MAX_RECIPIENTS) {
			errors.add("recipientEmails admite como máximo " + MAX_RECIPIENTS + " correos");
		}
		return new ArrayList<>(unique.values());
	}
}
