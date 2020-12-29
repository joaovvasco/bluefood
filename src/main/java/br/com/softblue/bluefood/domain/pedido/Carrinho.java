package br.com.softblue.bluefood.domain.pedido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.softblue.bluefood.domain.restaurante.ItemCardapio;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import lombok.Getter;

@Getter
public class Carrinho {
	
	private List<ItemPedido> itens = new ArrayList<>();
	
	private Restaurante restaurante;
	
	public void adicionarItem(ItemCardapio itemCardapio, Integer quantidade, String observacoes) throws RestauranteDiferenteException{
		
		if(restaurante == null)
			restaurante = itemCardapio.getRestaurante();
		
		if(!restaurante.getId().equals(itemCardapio.getRestaurante().getId()))
			throw new RestauranteDiferenteException();

		if(!exists(itemCardapio)) {
			
			ItemPedido itemPedido = new ItemPedido();
			itemPedido.setItemCardapio(itemCardapio);
			itemPedido.setObservacoes(observacoes);
			itemPedido.setQuantidade(quantidade);
			itemPedido.setPreco(itemCardapio.getPreco());
			
			itens.add(itemPedido);
		}
		
	}
	
	public void adicionarItem(ItemPedido itemPedido) {
		try {
			adicionarItem(itemPedido.getItemCardapio(), itemPedido.getQuantidade(),itemPedido.getObservacoes());
		} catch (RestauranteDiferenteException e) {
		}
	}
	
	public void removerItem(ItemCardapio itemCardapio) {
//		for(Iterator<ItemPedido> iterator = itens.iterator();iterator.hasNext();) {
//			var itemPedido = iterator.next();
//			
//			if(itemPedido.getItemCardapio().getId().equals(itemCardapio.getId())) {
//				iterator.remove();
//				break;
//			}
//		}
		
		itens.removeIf(p -> p.getItemCardapio().getId().equals(itemCardapio.getId()));
		
//		if(vazio())
//			restaurante = null;
	}
	
	public BigDecimal getPrecoTotal(boolean adicionarTaxaEntrega) {
		
		BigDecimal valorTotal = itens.stream()
				.map(itemPedido -> itemPedido.getPrecoCalculado())
				.reduce((acumulador, operando) -> acumulador.add(operando))
				.get();
		
		return adicionarTaxaEntrega? valorTotal.add(restaurante.getTaxaEntrega()) : valorTotal;
	}
	
	public boolean vazio() {
		return itens.size() == 0;
	}
	
	public void limpar() {
		itens.clear();
		restaurante = null;
	}
	
	private boolean exists(ItemCardapio itemCardapio) {
		return itens.stream().anyMatch(item -> item.getItemCardapio().getId().equals(itemCardapio.getId()));
	}
}
