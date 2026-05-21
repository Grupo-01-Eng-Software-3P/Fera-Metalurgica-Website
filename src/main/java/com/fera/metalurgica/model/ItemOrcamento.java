package com.fera.metalurgica.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fera.metalurgica.model.Orcamento;

@Entity
@Table(name = "item_orcamento")
public class ItemOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeItem;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private String material;
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "orcamento_id")
    private Orcamento orcamento;

    public ItemOrcamento() {
    }

    public ItemOrcamento(Long id, String nomeItem, Integer quantidade, BigDecimal valorUnitario, String material) {
        this.id = id;
        this.nomeItem = nomeItem;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.material = material;
        this.subtotal = valorUnitario.multiply(
                BigDecimal.valueOf(quantidade)
        );
    }

    // Getters e Setters

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public void setNomeItem(String nomeItem) {
        this.nomeItem = nomeItem;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        atualizarSubtotal();
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
        atualizarSubtotal();
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    private void atualizarSubtotal() {

        if (valorUnitario != null && quantidade != null) {

            this.subtotal =
                    valorUnitario.multiply(
                            BigDecimal.valueOf(quantidade)
                    );
        }
    }
}