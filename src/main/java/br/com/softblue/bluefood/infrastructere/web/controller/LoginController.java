package br.com.softblue.bluefood.infrastructere.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping(path={"/", "/login"})
	public String login() {
		return "login/login";
	}
	
	@GetMapping(path="/loginError")
	public String loginError(Model model) {
		model.addAttribute("msg", "Credenciais inválidas!");
		
		return "login/login";
	}
	
	
}
