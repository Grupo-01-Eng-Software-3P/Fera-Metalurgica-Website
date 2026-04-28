package com.fera.metalurgica.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Usuario {
    private String id;
    private String nome;
    private String cargo;
    private String dataCadastro;
    private String dataNascimento;

    public Usuario(String id, String nome, String cargo, String dataCadastro, String dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.dataCadastro = dataCadastro;
        this.dataNascimento = dataNascimento;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getCargo() { return cargo; }
    public String getDataCadastro() { return dataCadastro; }
    public String getDataNascimento() { return dataNascimento; }
}