package com.fera.metalurgica.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "atividade")
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;
    private String evento;
    private LocalDate data;
    private String horario;

    public Atividade() {}

    public Atividade(String titulo, String descricao, String evento, LocalDate data, String horario) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.evento = evento;
        this.data = data;
        this.horario = horario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}