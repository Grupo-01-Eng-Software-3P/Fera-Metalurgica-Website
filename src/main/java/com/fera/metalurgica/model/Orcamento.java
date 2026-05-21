package com.fera.metalurgica.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orcamento")
public class Orcamento {

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

    private String criadoPor; // "CLIENTE" ou "ADMIN"

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    // Parte 2: Orçamento do Admin
    @OneToMany(
            mappedBy = "orcamento",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<ItemOrcamento> itens = new ArrayList<>();

    private BigDecimal valorTotalMateriais;
    private BigDecimal valorAdicionais;
    private BigDecimal valorTotal;
    private String observacoesAdmin;

    // Define data automática ao salvar
    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }

    public Orcamento() {}

    public Orcamento(Long id, String cliente, String telefone, String cpf,
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

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public List<ItemOrcamento> getItens() { return itens; }
    public void setItens(List<ItemOrcamento> itens) {
        this.itens.clear();
        for (ItemOrcamento item : itens) {
            adicionarItem(item);
        }
        calcularTotais();
    }

    public BigDecimal getValorTotalMateriais() { return valorTotalMateriais; }

    public BigDecimal getValorAdicionais() { return valorAdicionais; }
    public void setValorAdicionais(BigDecimal valorAdicionais) { this.valorAdicionais = valorAdicionais; }

    public BigDecimal getValorTotal() { return valorTotal; }

    public String getObservacoesAdmin() { return observacoesAdmin; }
    public void setObservacoesAdmin(String observacoesAdmin) { this.observacoesAdmin = observacoesAdmin; }

    // Lógica de negócio

    public void adicionarItem(ItemOrcamento item) {
        itens.add(item);
        item.setOrcamento(this);
        calcularTotais();
    }

    public void removerItem(ItemOrcamento item) {
        itens.remove(item);
        item.setOrcamento(null);
        calcularTotais();
    }

    public void calcularTotais() {
        BigDecimal totalMateriais = BigDecimal.ZERO;
        if (itens != null) {
            for (ItemOrcamento item : itens) {
                totalMateriais = totalMateriais.add(item.getSubtotal());
            }
        }
        this.valorTotalMateriais = totalMateriais;
        this.valorTotal = (valorAdicionais != null)
                ? totalMateriais.add(valorAdicionais)
                : totalMateriais;
    }
}