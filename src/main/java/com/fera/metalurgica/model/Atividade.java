package com.fera.metalurgica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "atividade")
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;
    private String evento;
    private String data;
    private String horario;

    public Atividade() {}

    public Atividade(String titulo, String descricao, String evento, String data, String horario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.evento = evento;
        this.data = data;
        this.horario = horario;
    }

    public Long getId() { return id; }
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