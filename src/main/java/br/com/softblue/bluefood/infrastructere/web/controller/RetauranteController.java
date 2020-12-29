package br.com.softblue.bluefood.infrastructere.web.controller;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.softblue.bluefood.application.RelatorioService;
import br.com.softblue.bluefood.application.RestauranteService;
import br.com.softblue.bluefood.application.ValidationException;
import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.pedido.Pedido;
import br.com.softblue.bluefood.domain.pedido.PedidoRepository;
import br.com.softblue.bluefood.domain.pedido.RelatorioItemFaturamento;
import br.com.softblue.bluefood.domain.pedido.RelatorioItensFilter;
import br.com.softblue.bluefood.domain.pedido.RelatorioPedidoFilter;
import br.com.softblue.bluefood.domain.restaurante.CategoriaRestauranteRepository;
import br.com.softblue.bluefood.domain.restaurante.ItemCardapio;
import br.com.softblue.bluefood.domain.restaurante.ItemCardapioRepository;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import br.com.softblue.bluefood.domain.restaurante.RestauranteRepository;
import br.com.softblue.bluefood.util.SecurityUtils;

@Controller
@RequestMapping(path = "/restaurante")
public class RetauranteController {
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CategoriaRestauranteRepository categoriaRestauranteRepository;
	
	@Autowired
	private ItemCardapioRepository itemCardapioRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private RestauranteService restauranteService;
	
	@Autowired
	private RelatorioService relatorioService;
	
	@GetMapping(path = "/home")
	public String home(Model model) {
		Integer restauranteId = SecurityUtils.loggedRestaurante().getId();
		List<Pedido> pedidos = 	pedidoRepository.findByRestaurante_IdOrderByDataDesc(restauranteId);
		model.addAttribute("pedidos",pedidos);
		
		return "restaurante/home";
	}
	
	@GetMapping(path = "/edit")
	public String edit(Model model) {
		Integer id = SecurityUtils.loggedRestaurante().getId();
		Restaurante restaurante = restauranteRepository.findById(id).get();
		
		model.addAttribute("restaurante", restaurante);
		ControllerHelper.setEditMode(model, true);
		ControllerHelper.addCategoriasToRequest(categoriaRestauranteRepository, model);
		
		return "cadastro/restaurante";
	}
	
	@PostMapping(path="/save")
	public String cadastrarCliente(
			@ModelAttribute("restaurante") @Valid Restaurante restaurante,
			Errors errors,
			Model model) {
		
		if(!errors.hasErrors()) {
			try {
				restauranteService.save(restaurante);
				model.addAttribute("msg", "Restaurante gravado com sucesso!");
				model.addAttribute("cliente", new Cliente());
			} catch (ValidationException e) {
				errors.rejectValue("email", null, e.getMessage());
			}
		}
		
		ControllerHelper.addCategoriasToRequest(categoriaRestauranteRepository, model);
		
		model.addAttribute("restaurante", restaurante);
		ControllerHelper.setEditMode(model, true);
		return "cadastro/restaurante";
	}
	
	@GetMapping(path = "/comidas")
	public String viewComidas(Model model) {
		Integer restauranteId = SecurityUtils.loggedRestaurante().getId();
		Restaurante restaurante = restauranteRepository.findById(restauranteId).orElseThrow(NoSuchElementException::new);
		
		List<ItemCardapio> itensCardapio = itemCardapioRepository.findByRestaurante_IdOrderByNome(restauranteId);
		
		model.addAttribute("itensCardapio", itensCardapio);
		model.addAttribute("itemCardapio", new ItemCardapio());
		
		model.addAttribute("restaurante", restaurante);
		
		return "restaurante/comidas";
	}
	
	@GetMapping(path = "/comidas/remover")
	public String remover(
			Model model,
			@RequestParam("itemId") Integer itemId
			) {
		
		
		itemCardapioRepository.deleteById(itemId);
		
		return "redirect:/restaurante/comidas";
	}
	
	@PostMapping(path = "/comidas/cadastrar")
	public String cadastrar(
			@Valid @ModelAttribute("itemCardapio") ItemCardapio itemCardapio,
			Errors errors,
			Model model
			) {
		
		if(errors.hasErrors()) {
			Integer restauranteId = SecurityUtils.loggedRestaurante().getId();
			Restaurante restaurante = restauranteRepository.findById(restauranteId).orElseThrow(NoSuchElementException::new);
			model.addAttribute("restaurante", restaurante);
			
			List<ItemCardapio> itensCardapio = itemCardapioRepository.findByRestaurante_IdOrderByNome(restauranteId);
			model.addAttribute("itensCardapio",itensCardapio);
			
			return "restaurante/comidas";
		}
		
		restauranteService.saveItemCardapio(itemCardapio);
		
		return "redirect:/restaurante/comidas";
	}
	
	@GetMapping(path = "/pedido")
	public String localizarPedido(
			@RequestParam(name = "pedidoId") Integer pedidoId,
			Model model
			) {
		
		Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(NoSuchElementException::new);
		model.addAttribute("pedido",pedido);
		
		return "restaurante/pedido";
	}
	
	@PostMapping(path = "/pedido/proximoStatus")
	public String proximoStatus(
			@RequestParam("pedidoId") Integer pedidoId, 
			Model model
			) {
		
		Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(NoSuchElementException::new);
		model.addAttribute("pedido",pedido);
		pedido.definirProximoStatus();
		pedidoRepository.save(pedido);
		model.addAttribute("msg","Status alterado com sucesso");
		
		return "restaurante/pedido";
	}
	
	@GetMapping(path = "/relatorio/pedidos")
	public String relatorioPedidos(
			@ModelAttribute(value = "filter") RelatorioPedidoFilter filter,
			Model model
			) {

		List<Pedido> pedidos = relatorioService.listPedidos(SecurityUtils.loggedRestaurante().getId(), filter);
		model.addAttribute("pedidos",pedidos);
		model.addAttribute("filter",filter);
		
		return "restaurante/relatorioPedidos";
	}
	
	@GetMapping(path = "/relatorio/itens")
	public String relatorioItens(
			@ModelAttribute(value = "filter") RelatorioItensFilter filter,
			Model model
			) {
		
		Integer restauranteId = SecurityUtils.loggedRestaurante().getId();
		
		List<ItemCardapio> itensCardapio = itemCardapioRepository.findByRestaurante_IdOrderByNome(restauranteId);
		
		model.addAttribute("itensCardapio",itensCardapio);
		
		List<RelatorioItemFaturamento> itensCalculados = relatorioService.calcularFaturamentoItens(restauranteId, filter);
		model.addAttribute("itensCalculados", itensCalculados);
		
		model.addAttribute("filtro",filter);
		
		return "restaurante/relatorioItens";
	}
}
