package com.fera.metalurgica.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoriaDTO {

	@NotBlank(message = "O nome da categoria é obrigatório.")
	private String nome;

	private String descricao;

	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
}
