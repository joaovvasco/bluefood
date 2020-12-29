package br.com.softblue.bluefood.domain.restaurante;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import br.com.softblue.bluefood.domain.usuario.Usuario;
import br.com.softblue.bluefood.infrastructere.web.validation.UploadConstraint;
import br.com.softblue.bluefood.util.FileTypeEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Restaurante extends Usuario{
	
	private static final long serialVersionUID = -4268139178100041150L;
	
	@NotBlank(message = "O campo CNPJ não pode ser vazio")
	@Pattern(regexp = "\\d{14}", message = "O campo CNPJ são 14 dígitos numéricos")
	@Column(nullable = false, length = 14)
	private String cnpj;
	
	@Size(max = 80)
	private String logotipo;
	
	@UploadConstraint(acceptedTypes = FileTypeEnum.PNG, message = "O campo logotipo não é um arquivo de imagem válido!")
	private transient MultipartFile logotipoFile;
	
	@NotNull(message = "O campo taxa de entrega não pode ser vazia")
	@Min(0)
	@Max(99)
	private BigDecimal taxaEntrega;
	
	@NotNull(message = "O campo tempo de entrega não pode ser vazio")
	@Min(0)
	@Max(120)
	private Integer tempoEntregaBase;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "restaurante_has_categoria", 
				joinColumns = @JoinColumn(name = "restaurante_id"), 
				inverseJoinColumns = @JoinColumn(name = "categoria_restaurante_id")
				)
	@Size(min = 1, message = "O restaurante deve ter pelo menos 1 categoria!")
	private Set<CategoriaRestaurante> categorias = new HashSet<CategoriaRestaurante>(0);
	
	@OneToMany(mappedBy = "restaurante")
	private Set<ItemCardapio> itensCardapio = new HashSet<>(0);
	
	public void setNomeImagemLogotipo() {
		
		if(getId() == null)
			throw new IllegalStateException("O registro ainda não possui um ID");
		
		this.logotipo = String.format("%04d-logo.%s", getId(), FileTypeEnum.of(logotipoFile.getContentType()).getExtension());
	}
	
	public String getCategoriasAsString() {
		
		return categorias
				.stream()
				.map(c -> c.getNome())
				.reduce((anterior,posterior) -> anterior+", "+posterior)
				.get();
	}
	
	public Integer calcularTempoEntrega(String cepCliente) {
		
		Integer tempoMedido = 0;
		
		return tempoEntregaBase + tempoMedido;
	}
}
