package ar.edu.iua.iw3.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Modifica el comportamiento de spring
@EnableWebSecurity // Habilita
@EnableMethodSecurity(prePostEnabled = true) // Cada endpoint pueda tenre seguridad pre (autenticacion) y post (filtre resultados)
public class SecurityConfiguration {
    @Bean // Lo hace candidato de instanciacion
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS: https://developer.mozilla.org/es/docs/Web/HTTP/CORS
        // CSRF: https://developer.mozilla.org/es/docs/Glossary/CSRF
        http.cors(CorsConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll() // deshabilitar seguridad
                .anyRequest().authenticated());
        return http.build();
    }

}