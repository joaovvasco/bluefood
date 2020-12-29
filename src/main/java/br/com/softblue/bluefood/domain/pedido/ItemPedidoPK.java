package br.com.softblue.bluefood.domain.pedido;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@SuppressWarnings("serial")
@Embeddable
public class ItemPedidoPK implements Serializable{
	
	@NotNull
	@ManyToOne
	private Pedido pedido;
	
	private Integer ordem;
	
}
