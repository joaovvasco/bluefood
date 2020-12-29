package br.com.softblue.bluefood.infrastructere.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.softblue.bluefood.application.ClienteService;
import br.com.softblue.bluefood.application.RestauranteService;
import br.com.softblue.bluefood.application.ValidationException;
import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.restaurante.CategoriaRestauranteRepository;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;

@Controller
@RequestMapping(path = "/public")
public class PublicController {
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private RestauranteService restauranteService;
	
	@Autowired
	private CategoriaRestauranteRepository categoriaRepository;
	
	@GetMapping(path = "/cliente")
	public String newCliente(Model model) {
		
		model.addAttribute("cliente", new Cliente());
		
		ControllerHelper.setEditMode(model, false);
		return "cadastro/cliente";
	}
	
	@PostMapping(path="/cliente/save")
	public String cadastrarCliente(
			@ModelAttribute("cliente") @Valid Cliente cliente,
			Errors errors,
			Model model) {
		
		if(!errors.hasErrors()) {
			try {
				clienteService.save(cliente);
				model.addAttribute("msg", "Cliente gravado com sucesso!");
				model.addAttribute("cliente", new Cliente());
			} catch (ValidationException e) {
				errors.rejectValue("email", null, e.getMessage());
			}
		}
		
		ControllerHelper.setEditMode(model, false);
		return "cadastro/cliente";
	}
	
	@GetMapping(path = "/restaurante")
	public String newRestaurante(Model model) {
		model.addAttribute("categorias", categoriaRepository.findAll());
		model.addAttribute("restaurante", new Restaurante());
		
		ControllerHelper.setEditMode(model, false);
		ControllerHelper.addCategoriasToRequest(categoriaRepository, model);
		return "cadastro/restaurante";
	}
	
	@PostMapping(path="/restaurante/save")
	public String cadastrarRestaurante(
			@ModelAttribute("restaurante") @Valid Restaurante restaurante,
			Errors errors,
			Model model) {
		
		if(!errors.hasErrors()) {
			try {
				restauranteService.save(restaurante);
				model.addAttribute("msg", "Restaurante gravado com sucesso!");
				model.addAttribute("restaurante", new Restaurante());
			} catch (ValidationException e) {
				errors.rejectValue("email", null, e.getMessage());
			}
		}
		
		ControllerHelper.setEditMode(model, false);
		ControllerHelper.addCategoriasToRequest(categoriaRepository, model);
		return "cadastro/restaurante";
	}
	
	
}
