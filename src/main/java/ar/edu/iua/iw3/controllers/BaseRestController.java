package ar.edu.iua.iw3.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ar.edu.iua.iw3.auth.User;

public class BaseRestController {

	/**
	 * Devuelve el usuario autenticado del request actual: el principal que arma
	 * JWTAuthorizationFilter a partir del token. Retorna null si no hay usuario.
	 */
	protected User getUserLogged() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return null;
		}
		return (User) auth.getPrincipal();
	}

}
