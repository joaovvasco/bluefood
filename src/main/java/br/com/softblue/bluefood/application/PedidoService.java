package br.com.softblue.bluefood.application;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import br.com.softblue.bluefood.domain.pagamento.DadosCartao;
import br.com.softblue.bluefood.domain.pagamento.Pagamento;
import br.com.softblue.bluefood.domain.pagamento.PagamentoException;
import br.com.softblue.bluefood.domain.pagamento.PagamentoRepository;
import br.com.softblue.bluefood.domain.pagamento.StatusPagamento;
import br.com.softblue.bluefood.domain.pedido.Carrinho;
import br.com.softblue.bluefood.domain.pedido.ItemPedido;
import br.com.softblue.bluefood.domain.pedido.ItemPedidoPK;
import br.com.softblue.bluefood.domain.pedido.ItemPedidoRepository;
import br.com.softblue.bluefood.domain.pedido.Pedido;
import br.com.softblue.bluefood.domain.pedido.Pedido.Status;
import br.com.softblue.bluefood.domain.pedido.PedidoRepository;
import br.com.softblue.bluefood.util.SecurityUtils;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Value("${bluefood.sbpay.url}")
	private String url;
	
	@Value("${bluefood.sbpay.token}")
	private String token;
	
	@Transactional(rollbackFor = PagamentoException.class)
	public Pedido criarEPagar(Carrinho carrinho,String numCartao ) throws PagamentoException {
		
		Pedido pedido = new Pedido();
		
		pedido.setData(LocalDateTime.now());
		
		pedido.setCliente(SecurityUtils.loggedCliente());
		pedido.setRestaurante(carrinho.getRestaurante());
		
		pedido.setStatus(Status.PRODUCAO);
		pedido.setTaxaEntrega(carrinho.getRestaurante().getTaxaEntrega());
		
		pedido.setSubtotal(carrinho.getPrecoTotal(false));
		pedido.setTotal(carrinho.getPrecoTotal(true));
		
		pedido = pedidoRepository.save(pedido);
		
		int ordem = 1;
		
		for(ItemPedido itemPedido : carrinho.getItens()) {
			itemPedido.setId(new ItemPedidoPK(pedido, ordem++));
			itemPedidoRepository.save(itemPedido);
		}
		
		validarCartaoEPagar(numCartao);
		
		Pagamento pagamento = new Pagamento();
		pagamento.setData(LocalDateTime.now());
		pagamento.setPedido(pedido);
		pagamento.definirNumeroEBandeira(numCartao);
		
		pagamentoRepository.save(pagamento);
		
		return pedido;
	}
	
	@SuppressWarnings("unchecked")
	private void validarCartaoEPagar(String numCartao) throws PagamentoException{
		DadosCartao dadosCartao = new DadosCartao();
		dadosCartao.setNumeroCartao(numCartao);
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Token", token);
		
		HttpEntity<DadosCartao> request = new HttpEntity<DadosCartao>(dadosCartao, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		
		Map<String, String> response;
		
		try {
			response = restTemplate.postForObject(url, request, Map.class);
		}catch(Exception e) {
			throw new PagamentoException("Não foi possível realizar o pagamento");
		}
		
		
		String str = response.get("status");
		
		if(str == null)
			throw new PagamentoException(response.get("erro"));
		
		StatusPagamento status = StatusPagamento.valueOf(response.get("status"));
		
		if(status != StatusPagamento.AUTORIZADO)
			throw new PagamentoException(status.getDescricao());
		
	}
}
