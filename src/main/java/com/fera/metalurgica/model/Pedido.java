package com.fera.metalurgica.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome_cliente", nullable = false)
	private String cliente;

	private String telefone;
	private String cpf;

	@Column(nullable = false)
	private String material;

	@Column
	private String medidas;

	@Column(name = "observacao", columnDefinition = "TEXT")
	private String descricao;

	@Column(name = "anexo_caminho")
	private String anexoCaminho;

	@Column(name = "anexo_nome_original")
	private String anexoNomeOriginal;

	@Column(name = "anexo_tipo")
	private String anexoTipo;

	private String criadoPor; // "CLIENTE" ou "ADMIN"

	@Column(name = "data_criacao")
	private LocalDateTime dataCriacao;

	// Parte 2: Orçamento do Admin
	@OneToMany(
		mappedBy = "pedido",
		cascade = CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.EAGER
	)
	private List<ItemPedido> itens = new ArrayList<>();

	private BigDecimal valorTotalMateriais;
	private BigDecimal valorFrete;
	private BigDecimal valorMaoObra;
	private BigDecimal valorAdicionais;
	private BigDecimal valorTotal;
	private String observacoesAdmin;

	// Define data automática ao salvar
	@PrePersist
	public void prePersist() {
		this.dataCriacao = LocalDateTime.now();
	}

	public Pedido() {}

	public Pedido(Long id, String cliente, String telefone, String cpf,
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

	public String getAnexoCaminho() { return anexoCaminho; }
	public void setAnexoCaminho(String anexoCaminho) { this.anexoCaminho = anexoCaminho; }

	public String getAnexoNomeOriginal() { return anexoNomeOriginal; }
	public void setAnexoNomeOriginal(String anexoNomeOriginal) { this.anexoNomeOriginal = anexoNomeOriginal; }

	public String getAnexoTipo() { return anexoTipo; }
	public void setAnexoTipo(String anexoTipo) { this.anexoTipo = anexoTipo; }

	public String getCriadoPor() { return criadoPor; }
	public void setCriadoPor(String criadoPor) { this.criadoPor = criadoPor; }

	public LocalDateTime getDataCriacao() { return dataCriacao; }
	public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

	public List<ItemPedido> getItens() { return itens; }
	public void setItens(List<ItemPedido> itens) {
		this.itens.clear();
		for (ItemPedido item : itens) {
			adicionarItem(item);
		}
		calcularTotais();
	}

	public BigDecimal getValorTotalMateriais() { return valorTotalMateriais; }

	// 👇 ADICIONADO: Setter para o valor total dos materiais 👇
	public void setValorTotalMateriais(BigDecimal valorTotalMateriais) {
		this.valorTotalMateriais = valorTotalMateriais;
	}

	public BigDecimal getValorFrete() { return valorFrete; }
	public void setValorFrete(BigDecimal valorFrete) { this.valorFrete = valorFrete; }

	public BigDecimal getValorMaoObra() { return valorMaoObra; }
	public void setValorMaoObra(BigDecimal valorMaoObra) { this.valorMaoObra = valorMaoObra; }

	public BigDecimal getValorAdicionais() { return valorAdicionais; }
	public void setValorAdicionais(BigDecimal valorAdicionais) { this.valorAdicionais = valorAdicionais; }

	public BigDecimal getValorTotal() { return valorTotal; }

	// 👇 ADICIONADO: Setter para o valor total do pedido 👇
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getObservacoesAdmin() { return observacoesAdmin; }
	public void setObservacoesAdmin(String observacoesAdmin) { this.observacoesAdmin = observacoesAdmin; }

	@Transient
	public boolean isCriadoPorAdmin() {
		return "ADMIN".equalsIgnoreCase(criadoPor);
	}

	@Transient
	public boolean isCriadoPorCliente() {
		return !isCriadoPorAdmin();
	}

	@Transient
	public boolean isOrcamentoGerado() {
		return itens != null && !itens.isEmpty();
	}

	@Transient
	public boolean isOrcamentoPendente() {
		return isCriadoPorCliente() && !isOrcamentoGerado();
	}

	@Transient
	public boolean isTemAnexo() {
		return anexoCaminho != null && !anexoCaminho.isBlank();
	}

	@Transient
	public boolean isAnexoImagem() {
		return isTemAnexo() && anexoTipo != null && anexoTipo.startsWith("image/");
	}

	@Transient
	public boolean isOrcamentoFinalizado() {
		return isCriadoPorCliente() && isOrcamentoGerado();
	}

	// Lógica de negócio

	public void adicionarItem(ItemPedido item) {
		itens.add(item);
		item.setPedido(this);
		calcularTotais();
	}

	public void removerItem(ItemPedido item) {
		itens.remove(item);
		item.setPedido(null);
		calcularTotais();
	}

	public void calcularTotais() {
		BigDecimal totalMateriais = BigDecimal.ZERO;
		if (itens != null) {
			for (ItemPedido item : itens) {
				if (item.getSubtotal() != null) {
					totalMateriais = totalMateriais.add(item.getSubtotal());
				}
			}
		}

		BigDecimal frete = valorFrete != null ? valorFrete : BigDecimal.ZERO;
		BigDecimal maoObra = valorMaoObra != null ? valorMaoObra : BigDecimal.ZERO;

		if (valorFrete != null || valorMaoObra != null) {
			this.valorAdicionais = frete.add(maoObra);
		} else {
			this.valorAdicionais = valorAdicionais != null ? valorAdicionais : BigDecimal.ZERO;
		}

		this.valorTotalMateriais = totalMateriais;
		this.valorTotal = totalMateriais.add(this.valorAdicionais != null ? this.valorAdicionais : BigDecimal.ZERO);
	}
}
