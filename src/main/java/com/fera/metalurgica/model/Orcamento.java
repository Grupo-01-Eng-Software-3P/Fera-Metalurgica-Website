package com.fera.metalurgica.model;

import java.math.BigDecimal;
import java.util.List;

public class Orcamento {

    // Parte 1: Pedido do Cliente
    private Long id;
    private String cliente;
    private String telefone; // Novo
    private String cpf;      // Novo
    private String material;
    private String medidas;
    private String descricao;
    private String criadoPor; // "CLIENTE" ou "ADMIN"

    // Parte 2: Orçamento do Admin
    private List<ItemOrcamento> itens;
    private BigDecimal valorTotalMateriais;
    private BigDecimal valorAdicionais;
    private BigDecimal valorTotal;
    private String observacoesAdmin;

    // Construtor para o Pedido do Cliente
    public Orcamento(Long id, String cliente, String telefone, String cpf, String material, String medidas, String descricao, String criadoPor) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public List<ItemOrcamento> getItens() {
        return itens;
    }

    public void setItens(List<ItemOrcamento> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorTotalMateriais() {
        return valorTotalMateriais;
    }

    public void setValorTotalMateriais(BigDecimal valorTotalMateriais) {
        this.valorTotalMateriais = valorTotalMateriais;
    }

    public BigDecimal getValorAdicionais() {
        return valorAdicionais;
    }

    public void setValorAdicionais(BigDecimal valorAdicionais) {
        this.valorAdicionais = valorAdicionais;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getObservacoesAdmin() {
        return observacoesAdmin;
    }

    public void setObservacoesAdmin(String observacoesAdmin) {
        this.observacoesAdmin = observacoesAdmin;
    }
}