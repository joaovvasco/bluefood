package br.com.softblue.bluefood.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.softblue.bluefood.domain.pedido.Pedido;
import br.com.softblue.bluefood.domain.pedido.PedidoRepository;
import br.com.softblue.bluefood.domain.pedido.RelatorioItemFaturamento;
import br.com.softblue.bluefood.domain.pedido.RelatorioItensFilter;
import br.com.softblue.bluefood.domain.pedido.RelatorioPedidoFilter;
import br.com.softblue.bluefood.util.ListUtils;

@Service
public class RelatorioService {

	@Autowired
	private PedidoRepository pedidoRepository;
	
	
	public List<Pedido> listPedidos(Integer restauranteId, RelatorioPedidoFilter filtro){
		
		Integer pedidoId = filtro.getPedidoId();
		
		if(pedidoId != null) {
			Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(NoSuchElementException::new);
			
			return ListUtils.of(pedido);
		}
		
		LocalDate dataInicial = filtro.getDataInicial();
		LocalDate dataFinal = filtro.getDataFinal();
		
		if(dataInicial == null)
			return ListUtils.of();
		
		if(dataFinal == null)
			dataFinal = LocalDate.now();
		
		return pedidoRepository.findByDateInterval(
				restauranteId, 
				LocalDateTime.of(dataInicial, LocalTime.of(0, 0)), 
				LocalDateTime.of(dataFinal, LocalTime.of(23, 59)));
	}
	
	public List<RelatorioItemFaturamento> calcularFaturamentoItens(Integer restauranteId, RelatorioItensFilter filtro){
		
		List<Object[]> itensObj;
		
		Integer itemId = filtro.getItemId();
		LocalDate dataInicial = filtro.getDataInicial();
		LocalDate dataFinal = filtro.getDataFinal();
		
		if(dataInicial == null)
			return ListUtils.of();
		
		if(dataFinal == null)
			dataFinal = LocalDate.now();
		
		if(itemId != 0) {
			itensObj = pedidoRepository.findItensPorFaturamento(restauranteId, 
					itemId,
					LocalDateTime.of(dataInicial, LocalTime.of(0, 0)), 
					LocalDateTime.of(dataFinal, LocalTime.of(23, 59)));
		}else {
			itensObj = pedidoRepository.findItensPorFaturamento(restauranteId,
					LocalDateTime.of(dataInicial, LocalTime.of(0, 0)), 
					LocalDateTime.of(dataFinal, LocalTime.of(23, 59)));
		}
		
		
		
		List<RelatorioItemFaturamento> resultado = itensObj.stream().map(o -> {
			RelatorioItemFaturamento rif = new RelatorioItemFaturamento();
			rif.setNome((String)o[0]);
			rif.setQuantidade((Long)o[1]);
			rif.setFaturamento((BigDecimal)o[2]);
			
			return rif;
		}).collect(Collectors.toList());
		
		return resultado;
	}
}
