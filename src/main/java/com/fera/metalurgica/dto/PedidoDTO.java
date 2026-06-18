package com.fera.metalurgica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PedidoDTO {

	private Long id;

	@NotBlank(message = "O nome do cliente é obrigatório.")
	@Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres.")
	private String cliente;

	@NotBlank(message = "O telefone é obrigatório.")
	private String telefone;

	@NotBlank(message = "O CPF é obrigatório.")
	private String cpf;

	@NotBlank(message = "O material é obrigatório.")
	private String material;

	private String medidas;

	@NotBlank(message = "A descrição do projeto é obrigatória.")
	private String descricao;

	private String criadoPor;

	// Construtores

	public PedidoDTO() {}

	public PedidoDTO(Long id, String cliente, String telefone, String cpf,
	                 String material, String medidas, String descricao, String criadoPor) {
		this.id = id;
		this.cliente = cliente;
		this.telefone = telefone;
		this.cpf = cpf;
		this.material = material;
		this.medidas = medidas;
		this.descricao = descricao;
		this.criadoPor = criadoPor;
	}

	// Getters e Setters

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getCliente() { return cliente; }
	public void setCliente(String cliente) { this.cliente = cliente; }

	public String getTelefone() { return telefone; }
	public void setTelefone(String telefone) { this.telefone = telefone; }

	public String getCpf() { return cpf; }
	public void setCpf(String cpf) { this.cpf = cpf; }

	public String getMaterial() { return material; }
	public void setMaterial(String material) { this.material = material; }

	public String getMedidas() { return medidas; }
	public void setMedidas(String medidas) { this.medidas = medidas; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }

	public String getCriadoPor() { return criadoPor; }
	public void setCriadoPor(String criadoPor) { this.criadoPor = criadoPor; }
}
