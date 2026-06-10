package com.fera.metalurgica.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "usuario", nullable = false)
	private String nome;
	private String cargo;
	private LocalDate dataNascimento;

	@Column(unique = true)
	private String email;
	private String senha;

	public Usuario() {}

	public Usuario(Long id, String nome, String cargo, LocalDate dataNascimento, String email, String senha) {
		this.id = id;
		this.nome = nome;
		this.cargo = cargo;
		this.dataNascimento = dataNascimento;
		this.email = email;
		this.senha = senha;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	public String getCargo() { return cargo; }
	public void setCargo(String cargo) { this.cargo = cargo; }
	public LocalDate getDataNascimento() { return dataNascimento; }
	public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getSenha() { return senha; }
	public void setSenha(String senha) { this.senha = senha; }
}
