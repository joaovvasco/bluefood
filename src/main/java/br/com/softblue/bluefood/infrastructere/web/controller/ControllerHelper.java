package br.com.softblue.bluefood.infrastructere.web.controller;

import org.springframework.ui.Model;

import br.com.softblue.bluefood.domain.restaurante.CategoriaRestauranteRepository;

public class ControllerHelper {
	
	
	public static void setEditMode(Model model, boolean editMode) {
		model.addAttribute("isEditMode", editMode);
	}
	
	public static void addCategoriasToRequest(CategoriaRestauranteRepository repository, Model model){
		model.addAttribute("categorias", repository.findAll());
	}
}
