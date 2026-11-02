package ar.edu.iua.iw3.auth.controller;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import ar.edu.iua.iw3.auth.User;
import ar.edu.iua.iw3.auth.custom.CustomAuthenticationManager;
import ar.edu.iua.iw3.auth.filters.AuthConstants;
import ar.edu.iua.iw3.controllers.BaseRestController;
import ar.edu.iua.iw3.controllers.Constants;
import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
//import ar.edu.iw3.auth.event.UserEvent;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "Autenticación", description = "Login con token JWT y utilidad de la cátedra")
public class AuthRestController extends BaseRestController {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private IStandartResponseBusiness response;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Operation(summary = "Inicia sesión y devuelve el token JWT",
            description = "Recibe username y password como parámetros y devuelve el token (texto) que hay que enviar "
                    + "como Authorization: Bearer TOKEN en los demás endpoints. El token dura 1 hora. Es el único endpoint público de la API.")
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Token JWT (texto)")
    @ApiResponse(responseCode = "401", description = "Usuario o contraseña incorrectos")
    @ApiResponse(responseCode = "500", description = "Error interno")
    @PostMapping(value = Constants.URL_LOGIN)
    public ResponseEntity<?> loginExternalOnlyToken(@RequestParam String username, @RequestParam String password,
            HttpServletRequest request) {
        Authentication auth = null;
        try {
            auth = authManager.authenticate(((CustomAuthenticationManager) authManager).authWrap(username, password));
        } catch (AuthenticationServiceException e0) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e0, e0.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, e, e.getMessage()),
                    HttpStatus.UNAUTHORIZED);
        }

        User user = (User) auth.getPrincipal();
        String token = JWT.create().withSubject(user.getUsername())

                .withClaim("roles", new ArrayList<String>(user.getAuthoritiesStr()))
                .withClaim("email", user.getEmail())
                .withClaim("version", "1.0.0")
                .withExpiresAt(new Date(System.currentTimeMillis() + AuthConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));

        // applicationEventPublisher.publishEvent(new UserEvent(user, request,
        // UserEvent.TypeEvent.LOGIN));

        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

    @Autowired
    private PasswordEncoder pEncoder;

    @Operation(summary = "Codifica una contraseña con BCrypt (demo de la cátedra)",
            description = "Devuelve el hash BCrypt de la contraseña recibida. Sirve para generar el hash que se guarda en la tabla users. "
                    + "Exige token.")
    @ApiResponse(responseCode = "200", description = "Hash BCrypt (texto)")
    @ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
    @ApiResponse(responseCode = "500", description = "Error interno")
    @GetMapping(value = "/demo/encodepass")
    public ResponseEntity<?> encodepass(@RequestParam String password) {
        try {
            return new ResponseEntity<String>(pEncoder.encode(password), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}