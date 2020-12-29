package br.com.softblue.bluefood.util;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import br.com.softblue.bluefood.infrastructere.web.security.LoggedUser;

public class SecurityUtils {
	
	public static LoggedUser loggedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(auth instanceof AnonymousAuthenticationToken)
			return null;
		
		return (LoggedUser) auth.getPrincipal();
	}
	
	public static Cliente loggedCliente() {
		LoggedUser loggedUser = loggedUser();
		
		if(loggedUser == null)
			throw new IllegalStateException("Não existe usuário logado!");
		
		if(!(loggedUser.getUsuario() instanceof Cliente))
			throw new IllegalStateException("O usuário logado não é um cliente!");
		
		return (Cliente) loggedUser.getUsuario();
	}
	
	public static Restaurante loggedRestaurante() {
		LoggedUser loggedUser = loggedUser();
		
		if(loggedUser == null)
			throw new IllegalStateException("Não existe usuário logado!");
		
		if(!(loggedUser.getUsuario() instanceof Restaurante))
			throw new IllegalStateException("O usuário logado não é um restaurante!");
		
		return (Restaurante) loggedUser.getUsuario();
	}
	
}
