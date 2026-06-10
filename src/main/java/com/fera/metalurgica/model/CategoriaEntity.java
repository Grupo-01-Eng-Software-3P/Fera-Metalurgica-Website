package com.fera.metalurgica.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categoria")
public class CategoriaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	@OneToMany(mappedBy = "categoria", fetch = FetchType.EAGER)
	private List<Midia> midias;

	public CategoriaEntity() {}

	public CategoriaEntity(String nome) {
		this.nome = nome;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	public List<Midia> getMidias() { return midias; }
	public void setMidias(List<Midia> midias) { this.midias = midias; }
}
