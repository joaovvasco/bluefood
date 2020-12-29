package br.com.softblue.bluefood.domain.cliente;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import br.com.softblue.bluefood.domain.usuario.Usuario;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
public class Cliente extends Usuario{
	
	private static final long serialVersionUID = -83099148491122095L;
	
	@NotBlank(message = "O campo CPF não pode ser vazio")
	@Pattern(regexp = "\\d{11}", message = "O campo CPF deve ter 11 dígitos numéricos")
	private String cpf;
	
	@NotBlank(message = "O campo CEP não pode ser vazio")
	@Pattern(regexp = "\\d{8}", message = "O campo CEP deve ter 8 dígitos numéricos")
	private String cep;
	
	public String getFormattedCep() {
		return cep.replaceAll("(\\d{5})\\-?(\\d{3})", "$1-$2");
	}
}
