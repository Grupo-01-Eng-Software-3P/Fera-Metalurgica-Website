package com.fera.metalurgica.model;

public class Atividade {

    private String titulo;
    private String descricao;
    private String evento;
    private String data;     // yyyy-MM-dd
    private String horario;  // "08:00 - 09:00"

    public Atividade() {}

    public Atividade(String titulo, String descricao, String evento, String data, String horario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.evento = evento;
        this.data = data;
        this.horario = horario;
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getEvento() { return evento; }
    public String getData() { return data; }
    public String getHorario() { return horario; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setEvento(String evento) { this.evento = evento; }
    public void setData(String data) { this.data = data; }
    public void setHorario(String horario) { this.horario = horario; }
}