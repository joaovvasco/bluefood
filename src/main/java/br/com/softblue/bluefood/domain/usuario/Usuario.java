package br.com.softblue.bluefood.domain.usuario;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import br.com.softblue.bluefood.util.StringUtils;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario implements Serializable {

	private static final long serialVersionUID = 7490480927564184188L;
	
	public Usuario() {
		setDataCriacao(LocalDate.now());
	}
	
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NotBlank(message = "O campo nome não pode ser vazio")
	@Length(min = 3, message = "O campo nome deve ter no mínimo 3 letras")
	@Size(max = 80, message = "O nome é muito grande")
	@Column(length = 120, nullable = false)
	private String nome;

	@NotBlank(message = "O campo email não pode ser vazio")
	@Email(message = "O campo e-mail está inválido")
	@Size(max = 80, message = "O e-mail é muito grande")
	@Column(length = 80, nullable = false)
	private String email;

	@NotBlank(message = "O campo senha não pode ser vazio")
	@Size(max = 80, message = "A senha é muito grande")
	@Column(length = 80, nullable = false)
	private String senha;

	@NotBlank(message = "O campo telefone não pode ser vazio")
	@Pattern(regexp = "\\d{10,11}", message = "O campo telefone deve ter 10 ou 11 dígitos numéricos")
	@Column(nullable = false)
	private String telefone;
	
	@Column(columnDefinition = "DATE", name = "data_criacao")
	@Setter(value = AccessLevel.PRIVATE)
	private LocalDate dataCriacao;
	
	public void encryptPassword() {
		this.senha = StringUtils.encrypt(this.senha);
	}

}
