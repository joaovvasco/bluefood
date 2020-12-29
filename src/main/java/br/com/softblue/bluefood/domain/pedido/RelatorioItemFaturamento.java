package br.com.softblue.bluefood.domain.pedido;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RelatorioItemFaturamento {

	private String nome;
	private Long quantidade;
	private BigDecimal faturamento;
}
