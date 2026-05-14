package com.fera.metalurgica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orcamento")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_cliente", nullable = false)
    private String cliente;

    @Column(nullable = false)
    private String material;

    @Column
    private String medidas;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    // CONSTRUTOR VAZIO (obrigatório pro Spring/JPA)
    public Orcamento() {
    }

    // CONSTRUTOR COMPLETO (caso queira usar manualmente)
    public Orcamento(Long id, String cliente, String material, String medidas, String descricao) {
        this.id = id;
        this.cliente = cliente;
        this.material = material;
        this.medidas = medidas;
        this.descricao = descricao;
    }

    // DEFINE DATA AUTOMÁTICA AO SALVAR
    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }

    // GETTERS
    public Long getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public String getMaterial() {
        return material;
    }

    public String getMedidas() {
        return medidas;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    // SETTERS
    public void setId(Long id) {
        this.id = id;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setMedidas(String medidas) {
        this.medidas = medidas;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}