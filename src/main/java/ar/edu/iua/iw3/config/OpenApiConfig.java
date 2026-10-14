package ar.edu.iua.iw3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Configuracion de la documentacion OpenAPI (Swagger).
 * Declara el esquema de seguridad Bearer JWT para poder probar los endpoints
 * protegidos desde Swagger UI con el boton "Authorize".
 */
@Configuration
public class OpenApiConfig {

	private static final String SECURITY_SCHEME = "bearerAuth";

	@Bean
	OpenAPI openApi() {
		return new OpenAPI()
				.info(new Info().title("IW3 - Terminal de gas licuado")
						.description("API para la administración de órdenes de carga de gas líquido.")
						.version("v1"))
				.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
				.components(new Components().addSecuritySchemes(SECURITY_SCHEME,
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
	}
}
