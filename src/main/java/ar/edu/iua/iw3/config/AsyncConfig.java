package ar.edu.iua.iw3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/** Habilita @Async (el aviso de la alarma sale en otro hilo). Usa el ejecutor por defecto de Spring Boot. */
@Configuration
@EnableAsync
public class AsyncConfig {
}
