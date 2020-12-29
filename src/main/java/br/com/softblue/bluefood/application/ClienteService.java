package br.com.softblue.bluefood.application;


import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.cliente.ClienteRepository;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import br.com.softblue.bluefood.domain.restaurante.RestauranteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Transactional
	public void save(Cliente cliente) throws ValidationException {
		
		if(!isEmailValid(cliente.getEmail(), cliente.getId()))
			throw new ValidationException("O e-mail está duplicado");
		
		if(cliente.getId() != null) {
			Cliente clientedb = repository.findById(cliente.getId()).orElseThrow(NoSuchElementException::new);
			cliente.setSenha(clientedb.getSenha());
		}else
			cliente.encryptPassword();
		
		repository.save(cliente);
	}
	
	private boolean isEmailValid(String email, Integer id) {
		Restaurante restaurante = restauranteRepository.findByEmail(email);
		
		if(restaurante != null)
			return false;
		
		Cliente cliente = repository.findByEmail(email);
		
		if(cliente != null) {
			
			if(id == null)
				return false;
			
			if(!cliente.getId().equals(id))
				return false;
		}
		
		return true;
	}
}
