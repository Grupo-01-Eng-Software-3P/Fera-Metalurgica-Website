package com.fera.metalurgica.model;

import java.math.BigDecimal;

public class ItemOrcamento {
    private Integer quantidade;
    private String material;
    private BigDecimal valor;

    public ItemOrcamento(Integer quantidade, String material, BigDecimal valor) {
        this.quantidade = quantidade;
        this.material = material;
        this.valor = valor;
    }

    // Getters e Setters
    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}