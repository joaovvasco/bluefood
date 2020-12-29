package br.com.softblue.bluefood.infrastructere.web.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.softblue.bluefood.application.ClienteService;
import br.com.softblue.bluefood.application.RestauranteService;
import br.com.softblue.bluefood.application.ValidationException;
import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.cliente.ClienteRepository;
import br.com.softblue.bluefood.domain.pedido.Pedido;
import br.com.softblue.bluefood.domain.pedido.PedidoRepository;
import br.com.softblue.bluefood.domain.restaurante.CategoriaRestaurante;
import br.com.softblue.bluefood.domain.restaurante.CategoriaRestauranteRepository;
import br.com.softblue.bluefood.domain.restaurante.ItemCardapio;
import br.com.softblue.bluefood.domain.restaurante.ItemCardapioRepository;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import br.com.softblue.bluefood.domain.restaurante.RestauranteRepository;
import br.com.softblue.bluefood.domain.restaurante.filter.SearchFilter;
import br.com.softblue.bluefood.util.SecurityUtils;

@Controller
@RequestMapping(path = "/cliente")
public class ClienteController {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private RestauranteService restauranteService;
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private CategoriaRestauranteRepository categoriasRepository;
	
	@Autowired
	private ItemCardapioRepository itemCardapioRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@GetMapping(path = "/home")
	public String home(Model model) {
		
		List<CategoriaRestaurante> categorias = getCategorias();
		
		model.addAttribute("categorias", categorias);
		model.addAttribute("filtro", new SearchFilter());
		
		List<Pedido> pedidosAnteriores = pedidoRepository.listPedidosByCliente(SecurityUtils.loggedCliente().getId());
		
		model.addAttribute("pedidos", pedidosAnteriores);
		
		return "cliente/home";
	}
	
	@GetMapping(path="/busca")
	public String busca(
			@ModelAttribute("filtro") SearchFilter filtro,
			@RequestParam(name = "cmd", required = false) String command,
			Model model
			) {
		
		filtro.processFilter(command);
		
		List<Restaurante> restauranteFiltrados = restauranteService.find(filtro);
		
		List<CategoriaRestaurante> categorias = getCategorias();
		
		model.addAttribute("categorias", categorias);
		model.addAttribute("restaurantes", restauranteFiltrados);
		
		return "cliente/busca";
	}
	
	@GetMapping(path = "/edit")
	public String edit(Model model) {
		Integer id = SecurityUtils.loggedCliente().getId();
		Cliente cliente = clienteRepository.findById(id).get();
		
		model.addAttribute("cliente", cliente);
		ControllerHelper.setEditMode(model, true);
		
		return "cadastro/cliente";
	}
	
	@PostMapping(path="/save")
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
		
		model.addAttribute("cliente", cliente);
		ControllerHelper.setEditMode(model, true);
		return "cadastro/cliente";
	}
	
	@GetMapping(path = "/restauranteCardapio")
	public String restauranteCardapio(
			@RequestParam("restauranteId") Integer restauranteId,
			@RequestParam(value = "categoria", required = false) String categoria,
			Model model
			) {
		
		Restaurante restaurante = restauranteRepository.findById(restauranteId).orElseThrow(NoSuchElementException::new);
		String cep = SecurityUtils.loggedCliente().getCep();
		
		model.addAttribute("restaurante", restaurante);
		model.addAttribute("cep", cep);
		
		List<String> categoriasCardapio = itemCardapioRepository.findCategorias(restauranteId);
		model.addAttribute("categorias", categoriasCardapio);
		
		List<ItemCardapio> itensCardapio;
		
		if(categoria == null)
			itensCardapio = itemCardapioRepository.findByRestaurante_IdOrderByNome(restauranteId);
		else
			itensCardapio = itemCardapioRepository.findByRestaurante_IdAndCategoriaOrderByNome(restauranteId, categoria);
		
		List<ItemCardapio> itensCardapioDestaque = itensCardapio.stream().filter(item -> item.getDestaque() == true).collect(Collectors.toList());
		List<ItemCardapio> itensCardapioSemDestaque = itensCardapio.stream().filter(item -> item.getDestaque() == false).collect(Collectors.toList());
		
		model.addAttribute("itensCardapioDestaque", itensCardapioDestaque);
		model.addAttribute("itensCardapioSemDestaque", itensCardapioSemDestaque);
		model.addAttribute("categoriaSelecionada", categoria);
		
		return "cliente/restaurante-cardapio";
	}
	
	private List<CategoriaRestaurante> getCategorias(){
		return categoriasRepository.findAll(Sort.by("nome"));
	}
}
