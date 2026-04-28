package com.fera.metalurgica.model;

public class Orcamento {
    private Long id;
    private String cliente;
    private String material;
    private String medidas;
    private String descricao;

    public Orcamento(Long id, String cliente, String material, String medidas, String descricao) {
        this.id = id;
        this.cliente = cliente;
        this.material = material;
        this.medidas = medidas;
        this.descricao = descricao;
    }

    public Long getId() { return id; }
    public String getCliente() { return cliente; }
    public String getMaterial() { return material; }
    public String getMedidas() { return medidas; }
    public String getDescricao() { return descricao; }
}

