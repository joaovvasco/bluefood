package br.com.softblue.bluefood.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.cliente.ClienteRepository;
import br.com.softblue.bluefood.domain.pedido.Pedido;
import br.com.softblue.bluefood.domain.pedido.Pedido.Status;
import br.com.softblue.bluefood.domain.pedido.PedidoRepository;
import br.com.softblue.bluefood.domain.restaurante.CategoriaRestaurante;
import br.com.softblue.bluefood.domain.restaurante.CategoriaRestauranteRepository;
import br.com.softblue.bluefood.domain.restaurante.ItemCardapio;
import br.com.softblue.bluefood.domain.restaurante.ItemCardapioRepository;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import br.com.softblue.bluefood.domain.restaurante.RestauranteRepository;
import br.com.softblue.bluefood.domain.usuario.Usuario;
import br.com.softblue.bluefood.util.FileTypeEnum;

@Component
public class InsertDataForTesting {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private CategoriaRestauranteRepository categoriaRepository;
	
	@Autowired
	private ItemCardapioRepository itemCardapioRepository;
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Cliente[] clientes = clientes();
		Restaurante[] restaurantes = restaurantes();
		itensCardapio(restaurantes);
		
		Pedido p = new Pedido();
		p.setData(LocalDateTime.now());
		p.setCliente(clientes[0]);
		p.setRestaurante(restaurantes[0]);
		p.setStatus(Status.PRODUCAO);
		p.setSubtotal(BigDecimal.valueOf(10));
		p.setTaxaEntrega(BigDecimal.valueOf(2));
		p.setTotal(BigDecimal.valueOf(12.0));
		
		pedidoRepository.save(p);
	}
	
	private String generateCNPJ() {
		
		return String.format("%014d", 1209853456);
	}
	
//	private String generateCPF() {
//		
//		return String.format("%011d", 0103456247);
//	}
	
	private String generateTelefone() {
		return "0000001119";
	}
	
	private void adjustUsuario(Usuario u, String nome, String email, String senha) {
		u.setNome(nome);
		u.setEmail(email+"@bluefood.com");
		u.setSenha(senha);
		u.setTelefone(generateTelefone());
	}
	
	private String getFileNameFormated(Integer id, String filename,FileTypeEnum filetype) {
		return String.format("%04d-%s.%s", id, filename, filetype.getExtension());
	}
	
	private Restaurante createRestaurante(String nome, String email, Integer idLogo, BigDecimal taxaEntrega,Integer tempoEntregaBase, CategoriaRestaurante... categorias) {
		Restaurante r = new Restaurante();
		adjustUsuario(r, nome, email, "r");
		r.setCnpj(generateCNPJ());
		r.setTaxaEntrega(taxaEntrega);
		Arrays.asList(categorias).forEach(c -> r.getCategorias().add(c));
		r.setLogotipo(getFileNameFormated(idLogo, "logo", FileTypeEnum.PNG));
		r.setTempoEntregaBase(tempoEntregaBase);
		r.encryptPassword();
		return r;
	}
	
//	private Cliente createCliente(String nome, String email) {
//		var c = new Cliente();
//		adjustUsuario(c, nome, email, "c");
//		c.setCpf(generateCPF());
//		c.setCep("12345678");
//		c.encryptPassword();
//		return c;
//	}
	
	private Restaurante[] restaurantes() {
		List<Restaurante> restaurantes = new ArrayList<Restaurante>();
		
		CategoriaRestaurante categoriaPizza = categoriaRepository.findById(1).orElseThrow(NoSuchElementException::new);
		CategoriaRestaurante categoriaSanduiche = categoriaRepository.findById(2).orElseThrow(NoSuchElementException::new);
		CategoriaRestaurante categoriaSobremesa = categoriaRepository.findById(5).orElseThrow(NoSuchElementException::new);
		CategoriaRestaurante categoriaJaponesa = categoriaRepository.findById(6).orElseThrow(NoSuchElementException::new);
		
		Restaurante r = createRestaurante("Bubger King", "bubger.king", 1, BigDecimal.valueOf(3.2), 30, categoriaSanduiche, categoriaSobremesa);
		
		restaurantes.add(restauranteRepository.save(r));
		
		r = createRestaurante("Pizza Brut","pizza.brut", 2, BigDecimal.valueOf(2.9), 45, categoriaPizza);
		
		restaurantes.add(restauranteRepository.save(r));
		
		r = createRestaurante("Wiki Japa","wiki.japa", 3, BigDecimal.valueOf(1.6), 20, categoriaJaponesa);
		
		restaurantes.add(restauranteRepository.save(r));
		
		Restaurante[] array = new Restaurante[restaurantes.size()];
		return restaurantes.toArray(array);
	}
	
	private Cliente[] clientes() {
		List<Cliente> clientes = new ArrayList<Cliente>();
		
		Cliente c = new Cliente();
		c.setNome("João Vasco");
		c.setEmail("joao.vasco@bluefood.com");
		c.setSenha("c");
		c.setCep("12345678");
		c.setCpf("01234567891");
		c.setTelefone("9876543210");
		c.encryptPassword();
		//createCliente("João Vasco", "joao.vasco");
		clientes.add(clienteRepository.save(c));
		
		c = new Cliente();
		c.setNome("Maria Torres");
		c.setEmail("maria.torres@bluefood.com");
		c.setSenha("c");
		c.setCep("12345678");
		c.setCpf("01234567891");
		c.setTelefone("9876543210");
		c.encryptPassword();
		//createCliente("Maria Torres", "maria.torres");
		clientes.add(clienteRepository.save(c));
		
		Cliente[] array = new Cliente[clientes.size()];
		return clientes.toArray(array);
	}
	
	private ItemCardapio[] itensCardapio(Restaurante[] restaurantes) {
		List<ItemCardapio> itensCardapio = new ArrayList<ItemCardapio>();
		
		ItemCardapio ic = createItemCardapio(1, 
				"Double Cheese Burguer Special", 
				"Sanduiche", 
				"Delicioso sanduiche com molho", 
				BigDecimal.valueOf(23.8), 
				true, 
				restaurantes[0]);
		
		itensCardapio.add(itemCardapioRepository.save(ic));
		
		ic = createItemCardapio(2, 
				"Cheese Burguer Simples", 
				"Sanduiche", 
				"Sanduiche que mata a fome", 
				BigDecimal.valueOf(17.8), 
				false, 
				restaurantes[0]);
		
		itensCardapio.add(itemCardapioRepository.save(ic));
		
		ic = createItemCardapio(3, 
				"Sanduiche Natural da Casa", 
				"Sanduiche", 
				"Sanduiche natural com peito de peru", 
				BigDecimal.valueOf(11.8), 
				false, 
				restaurantes[0]);
		
		itensCardapio.add(itemCardapioRepository.save(ic));
		
		ic = createItemCardapio(4, 
				"Refrigerante Tradicional", 
				"Bebida", 
				"Refrigerante com gás", 
				BigDecimal.valueOf(9.0), 
				false, 
				restaurantes[0]);
		
		itensCardapio.add(itemCardapioRepository.save(ic));
		
		ic = createItemCardapio(5, 
				"Suco de Laranja", 
				"Bebida", 
				"Suco natural e docinho", 
				BigDecimal.valueOf(9.0), 
				false, 
				restaurantes[0]);
		
		itensCardapio.add(itemCardapioRepository.save(ic));
		
		ic = createItemCardapio(6, 
				"Pizza de Calabresa", 
				"Pizza", 
				"Pizza saborosa com cebola", 
				BigDecimal.valueOf(38.90), 
				false, 
				restaurantes[1]);
		
		itensCardapio.add(itemCardapioRepository.save(ic));
		
		ic = createItemCardapio(7, 
				"Uramaki", 
				"Japonesa", 
				"Delicioso Uramaki Tradicional", 
				BigDecimal.valueOf(16.90), 
				false, 
				restaurantes[2]);
		
		itensCardapio.add(itemCardapioRepository.save(ic));
		
		ItemCardapio[] array = new ItemCardapio[itensCardapio.size()];
		return itensCardapio.toArray(array);
	}
	
	private ItemCardapio createItemCardapio(Integer idComida, 
			String nome, 
			String categoria, 
			String descricao, 
			BigDecimal preco, 
			Boolean destaque,
			Restaurante restaurante) {
		ItemCardapio ic = new ItemCardapio();
		
		ic.setNome(nome);
		ic.setCategoria(categoria);
		ic.setDescricao(descricao);
		ic.setImagem(getFileNameFormated(idComida, "comida", FileTypeEnum.PNG)); 
		ic.setPreco(preco);
		ic.setDestaque(destaque);
		ic.setRestaurante(restaurante);
		
		return ic;
	}
	
}
