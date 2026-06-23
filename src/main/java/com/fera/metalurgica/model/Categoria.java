package com.fera.metalurgica.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categoria")
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	@Column(columnDefinition = "TEXT")
	private String descricao;
	private String meta;

	@OneToMany(mappedBy = "categoria", fetch = FetchType.EAGER)
	private List<Midia> midias;

	private String slug;

	public Categoria() {}

	public Categoria(Long id, String nome, String descricao, String meta) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.meta = meta;
	}

	// Getters e Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }

	public String getMeta() { return meta; }
	public void setMeta(String meta) { this.meta = meta; }

	public List<Midia> getMidias() { return midias; }
	public void setMidias(List<Midia> midias) { this.midias = midias; }

	public String getSlug() { return slug; }
	public void setSlug(String slug) { this.slug = slug; }
}
