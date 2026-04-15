package com.fera.metalurgica.model;

public class Produto {
    private Long id;
    private String nome;
    private String categoria;

    public Produto(Long id, String nome, String categoria) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
}