package com.fera.metalurgica.model;

public class Categoria {
    private String nome;
    private String descricao;
    private String meta;

    public Categoria(String nome, String descricao, String meta) {
        this.nome = nome;
        this.descricao = descricao;
        this.meta = meta;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getMeta() { return meta; }
}