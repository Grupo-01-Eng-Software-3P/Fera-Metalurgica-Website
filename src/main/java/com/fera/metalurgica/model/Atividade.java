package com.fera.metalurgica.model;

public class Atividade {
    private String descricao;
    private String tempo;

    public Atividade(String descricao, String tempo) {
        this.descricao = descricao;
        this.tempo = tempo;
    }

    public String getDescricao() { return descricao; }
    public String getTempo() { return tempo; }
}