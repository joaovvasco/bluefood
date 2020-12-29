package br.com.softblue.bluefood.domain.restaurante.filter;

import br.com.softblue.bluefood.util.StringUtils;
import lombok.Data;

@Data
public class SearchFilter {
	
	public enum SearchType{
		TEXTO, CATEGORIA;
	} 
	
	public enum Order{
		TAXA, TEMPO;
	}
	
	public enum Command{
		ENTREGA_GRATIS, MAIOR_TEMPO, MENOR_TEMPO, MAIOR_TAXA, MENOR_TAXA;
	}
	
	private SearchType searchType;

	private Command cmd;
	
	private String texto;
	
	private Integer categoriaId;
	
	private boolean entregaGratis;
	
	private Order order = Order.TAXA;
	
	private boolean asc;
	
	private void processFilter() {
		if(searchType == SearchType.TEXTO)
			categoriaId = null;
		else if (searchType == SearchType.CATEGORIA)
			texto = null;
	}
	
	public void processFilter(String cmdString) {
		processFilter();
		
		if(StringUtils.isBlank(cmdString))
			return;
		
		Command cmd = Command.valueOf(cmdString);
			
		if(cmd == Command.ENTREGA_GRATIS)
			entregaGratis = !entregaGratis;
		
		if(cmd == Command.MAIOR_TAXA) {
			order = Order.TAXA;
			asc = false;
		}
		
		if(cmd == Command.MENOR_TAXA) {
			order = Order.TAXA;
			asc = true;
		}
		
		if(cmd == Command.MAIOR_TEMPO) {
			order = Order.TEMPO;
			asc = false;
		}
		
		if(cmd == Command.MENOR_TEMPO) {
			order = Order.TEMPO;
			asc = true;
		}
	}
}
