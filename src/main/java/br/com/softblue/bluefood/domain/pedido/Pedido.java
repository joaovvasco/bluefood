package br.com.softblue.bluefood.domain.pedido;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import br.com.softblue.bluefood.domain.cliente.Cliente;
import br.com.softblue.bluefood.domain.restaurante.Restaurante;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "pedido")
public class Pedido implements Serializable{

	public enum Status{
		PRODUCAO(1, "Em produção", false),
		ENTREGA(2, "Saiu para entrega", false),
		CONCLUIDO(3, "Concluído", true);
		
		int ordem;
		String descricao;
		boolean ultimo;
		
		Status(int ordem, String descricao, boolean ultimo){
			this.ordem = ordem;
			this.descricao = descricao;
			this.ultimo = ultimo;
		}
		
		public int getOrdem() {
			return ordem;
		}
		
		public String getDescricao() {
			return descricao;
		}
		
		private Status fromOrdem(int ordem) {
			for(Status status : Status.values()){
				if(status.ordem == ordem)
					return status;
			}
			
			return null;
		}
		
		public Status proximoStatus() {
			
			if(this.ultimo)
				return this;
			
			return fromOrdem(this.ordem + 1);
		}
		
		public boolean isUltimo() {
			return ultimo;
		}
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
	private LocalDateTime data;
	
	@NotNull
	private Status status;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Cliente cliente;
	
	@NotNull
	@ManyToOne
	private Restaurante restaurante;
	
	@NotNull
	private BigDecimal subtotal;
	
	@NotNull
	private BigDecimal total;
	
	@NotNull
	@Column(name = "taxa_entrega")
	private BigDecimal taxaEntrega;
	
	@OneToMany(mappedBy="id.pedido", fetch = FetchType.EAGER)
	private Set<ItemPedido> itensPedido;
	
	public String getFormattedId() {
		return String.format("#%04d", id);
	}
	
	public void definirProximoStatus() {
		this.status = this.status.proximoStatus();
	}
}
