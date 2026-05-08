package com.fera.metalurgica.model;

public class Usuario {

    private Long id; // Mudado para Long para usarmos Auto-Incremento (1, 2, 3...)
    private String nome;
    private String cargo;
    private String dataCadastro; // Adicionado para guardar o que vem do seu construtor
    private String dataNascimento;

    // Nossos novos campos de Login
    private String email;
    private String senha;

    // 1. Construtor Vazio (Obrigatório: O Spring precisa dele para funcionar nos formulários)
    public Usuario() {
    }

    // 2. Construtor Completo (Arrumado com todos os parâmetros)
    // O erro acontece se estiver faltando este bloco aqui:
    public Usuario(Long id, String nome, String cargo, String dataCadastro, String dataNascimento, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.dataCadastro = dataCadastro;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.senha = senha;
    }

    //GETTERS E SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}