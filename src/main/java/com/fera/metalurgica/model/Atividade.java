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

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getEvento() { return evento; }
    public LocalDate getData() { return data; }
    public String getHorario() { return horario; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setEvento(String evento) { this.evento = evento; }
    public void setData(LocalDate data) { this.data = data; }
    public void setHorario(String horario) { this.horario = horario; }
}