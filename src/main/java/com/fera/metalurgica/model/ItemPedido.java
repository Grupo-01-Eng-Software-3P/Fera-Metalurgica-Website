package com.fera.metalurgica.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeItem;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private String material;
    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    public ItemPedido() {
    }

    public ItemPedido(Long id, String nomeItem, Integer quantidade, BigDecimal valorUnitario, String material) {
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

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
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
