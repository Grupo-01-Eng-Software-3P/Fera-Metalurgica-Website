package com.fera.metalurgica.dto;

import jakarta.validation.constraints.*;

public class UsuarioDTO {

	@NotBlank(message = "Nome é obrigatório")
	private String nome;

	@NotBlank(message = "Cargo é obrigatório")
	private String cargo;

	@NotNull(message = "Data de nascimento é obrigatória")
	private String dataNascimento;

	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email inválido")
	private String email;

	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 4, message = "Senha deve ter ao menos 4 caracteres")
	private String senha;

	public UsuarioDTO() {
		this.cargo = "";
	}


	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }

	public String getCargo() { return cargo; }
	public void setCargo(String cargo) { this.cargo = cargo; }

	public String getDataNascimento() { return dataNascimento; }
	public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getSenha() { return senha; }
	public void setSenha(String senha) { this.senha = senha; }
}
