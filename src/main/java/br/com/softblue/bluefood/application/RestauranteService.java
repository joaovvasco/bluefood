package br.com.softblue.bluefood.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.cliente.ClienteRepository;
import br.com.softblue.bluefood.domain.restaurante.ItemCardapio;
import br.com.softblue.bluefood.domain.restaurante.ItemCardapioRepository;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import br.com.softblue.bluefood.domain.restaurante.RestauranteRepository;
import br.com.softblue.bluefood.domain.restaurante.filter.SearchFilter;
import br.com.softblue.bluefood.util.SecurityUtils;

@Service
public class RestauranteService {

	@Autowired
	private RestauranteRepository repository;

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ItemCardapioRepository itemCardapioRepository;
	
	@Autowired
	private ImageService imageService;

	public List<Restaurante> find(SearchFilter filtro) {
		
		List<Restaurante> restaurantes = null;
		
		switch(filtro.getSearchType()) {
			case TEXTO : 
				restaurantes = repository.findByNomeIgnoreCaseContaining(filtro.getTexto());
				break;
			
			case CATEGORIA: 
				restaurantes = repository.findByCategorias_Id(filtro.getCategoriaId());
			break;
			default: throw new IllegalStateException("O tipo de busca " + filtro.getSearchType() + "não é suportado");
		}
		

		return restaurantes.stream().filter(r -> {

			if (filtro.isEntregaGratis() && r.getTaxaEntrega().compareTo(BigDecimal.ZERO) > 0
					|| !filtro.isEntregaGratis() && r.getTaxaEntrega().compareTo(BigDecimal.ZERO) == 0) {
				return false;
			}

			return true;
		}).sorted((anterior, posterior) -> {
			Integer result = null;
			
			String cepCliente = SecurityUtils.loggedCliente().getCep();
			
			switch (filtro.getOrder()) {
				case TAXA: result = anterior.getTaxaEntrega().compareTo(posterior.getTaxaEntrega());
				break;
			case TEMPO: 
				result = anterior.calcularTempoEntrega(cepCliente).compareTo(posterior.calcularTempoEntrega(cepCliente));
				break;
			default: throw new IllegalStateException("O valor da ordenação " + filtro.getOrder() + " não é válido");
			};

			if (!filtro.isAsc())
				return -1 * result;

			return result;
		}).collect(Collectors.toList());

	}

	@Transactional
	public void save(Restaurante restaurante) throws ValidationException {

		if (!isEmailValid(restaurante.getEmail(), restaurante.getId()))
			throw new ValidationException("E-mail duplicado!");

		if (restaurante.getId() != null) {
			Restaurante restauranteDB = repository.findById(restaurante.getId()).orElseThrow(NoSuchElementException::new);
			restaurante.setSenha(restauranteDB.getSenha());
			restaurante.setLogotipo(restauranteDB.getLogotipo());
			restaurante = repository.save(restaurante);
		} else {
			restaurante.encryptPassword();
			restaurante = repository.save(restaurante);

			restaurante.setNomeImagemLogotipo();
			imageService.uploadLogotipo(restaurante.getLogotipoFile(), restaurante.getLogotipo());
		}
	}
	
	@Transactional
	public void saveItemCardapio(ItemCardapio itemCardapio) {
		itemCardapio = itemCardapioRepository.save(itemCardapio);
		itemCardapio.setImagemFileName();
		imageService.uploadComidas(itemCardapio.getImagemFile(), itemCardapio.getImagem());
	}
	
	private boolean isEmailValid(String email, Integer id) {
		Cliente cliente = clienteRepository.findByEmail(email);

		if (cliente != null)
			return false;

		Restaurante restaurante = repository.findByEmail(email);

		if (restaurante != null) {
			if (id == null)
				return false;

			if (!restaurante.getId().equals(id))
				return false;
		}

		return true;
	}

}
