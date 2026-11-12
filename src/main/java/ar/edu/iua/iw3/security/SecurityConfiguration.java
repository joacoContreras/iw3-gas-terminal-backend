package ar.edu.iua.iw3.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ar.edu.iua.iw3.auth.IUserBusiness;
import ar.edu.iua.iw3.auth.custom.CustomAuthenticationManager;
import ar.edu.iua.iw3.auth.filters.JWTAuthorizationFilter;
import ar.edu.iua.iw3.auth.filters.JwtSecret;
import ar.edu.iua.iw3.controllers.Constants;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    @Bean
    PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Origenes que pueden llamar a la API desde un navegador, separados por coma (variable CORS_ALLOWED_ORIGINS).
     * El valor por defecto "*" es el comportamiento anterior (abierto): sirve porque el token viaja en el header
     * Authorization y no en una cookie. En produccion conviene limitarlo al origen del frontend.
     */
    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    /**
     * Configuracion de CORS que usa la cadena de seguridad (http.cors). Antes estaba solo como mapeo de Spring MVC,
     * que corre DESPUES de la autorizacion: el preflight (OPTIONS) del navegador no lleva token, caia en el 401 y el
     * navegador bloqueaba toda llamada entre origenes.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(o -> !o.isEmpty())
                .toList();
        config.setAllowedOrigins(origins.isEmpty() ? List.of("*") : origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        // El alta de ordenes (SAP) responde 201 con el header location
        config.setExposedHeaders(List.of("Location"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Autowired
    private IUserBusiness userBusiness;

    @Autowired
    private RestAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private JwtSecret jwtSecret;

    @Bean
    AuthenticationManager authenticationManager() {
        return new CustomAuthenticationManager(bCryptPasswordEncoder(), userBusiness);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS: https://developer.mozilla.org/es/docs/Web/HTTP/CORS
        // Dentro de la cadena y antes de la autorizacion, con la configuracion de corsConfigurationSource
        http.cors(Customizer.withDefaults());

        // CSRF: https://developer.mozilla.org/es/docs/Glossary/CSRF
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, Constants.URL_LOGIN).permitAll()
                .requestMatchers("/error").permitAll().requestMatchers("/v3/api-docs/**").permitAll().requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll().requestMatchers("/ui/**").permitAll()
                .anyRequest().authenticated());
        // Sin httpBasic: la API solo usa JWT y httpBasic agregaba "WWW-Authenticate: Basic" al 401 (dialogo del navegador)
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // 401 y 403 con el JSON estandar (sin el header WWW-Authenticate)
        http.exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));
        http.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtSecret));
        return http.build();

    }

}
