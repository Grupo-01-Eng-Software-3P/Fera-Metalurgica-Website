package com.fera.metalurgica.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class OrcamentoAdminDTO {

	private Long pedidoId;

	@NotBlank(message = "O nome do cliente é obrigatório.")
	private String cliente;

	@NotBlank(message = "O telefone é obrigatório.")
	private String telefone;

	@NotBlank(message = "O CPF é obrigatório.")
	private String cpf;

	@NotBlank(message = "O material é obrigatório.")
	private String material;

	private String medidas;

	@NotBlank(message = "A descrição é obrigatória.")
	private String descricao;

	private List<ItemPedidoDTO> itens = new ArrayList<>();

	private String frete;
	private String maoObra;
	private String observacoesAdmin;

	public Long getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(Long pedidoId) {
		this.pedidoId = pedidoId;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getMedidas() {
		return medidas;
	}

	public void setMedidas(String medidas) {
		this.medidas = medidas;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<ItemPedidoDTO> getItens() {
		return itens;
	}

	public void setItens(List<ItemPedidoDTO> itens) {
		this.itens = itens;
	}

	public String getFrete() {
		return frete;
	}

	public void setFrete(String frete) {
		this.frete = frete;
	}

	public String getMaoObra() {
		return maoObra;
	}

	public void setMaoObra(String maoObra) {
		this.maoObra = maoObra;
	}

	public String getObservacoesAdmin() {
		return observacoesAdmin;
	}

	public void setObservacoesAdmin(String observacoesAdmin) {
		this.observacoesAdmin = observacoesAdmin;
	}
}
