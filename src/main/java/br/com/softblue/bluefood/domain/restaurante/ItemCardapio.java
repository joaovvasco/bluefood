package br.com.softblue.bluefood.domain.restaurante;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import br.com.softblue.bluefood.infrastructere.web.validation.UploadConstraint;
import br.com.softblue.bluefood.util.FileTypeEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "item_cardapio")
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemCardapio {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Integer id;
	
	@NotBlank(message = "O campo nome não pode ser vazio")
	@Size(max = 50)
	private String nome;
	
	@NotBlank(message = "O campo categoria não pode ser vazio")
	@Size(max = 25)
	private String categoria;
	
	@NotBlank(message = "O campo descrição não pode ser vazio")
	@Size(max = 80)
	private String descricao;
	
	@Size(max = 50)
	private String imagem;
	
	@NotNull(message = "O campo preço não pode ser vazio")
	@Min(0)
	private BigDecimal preco;
	
	@NotNull
	private Boolean destaque;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "restaurante_id")
	private Restaurante restaurante;
	
	@UploadConstraint(acceptedTypes = FileTypeEnum.PNG, message = "O arquivo não é um arquivo de imagem válido")
	private transient MultipartFile imagemFile;
	
	public void setImagemFileName() {
		
		if(this.getId() == null)
			throw new IllegalStateException("O objeto deve ser primeiro persistido");
		
		this.imagem = String.format("%04d-comida.%s", this.getId(), FileTypeEnum.of(imagemFile.getContentType()).getExtension());
	}
}
