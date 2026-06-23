package com.fera.metalurgica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "midia")
public class Midia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	@Column(columnDefinition = "TEXT")
	private String descricao;

	private String caminho;

	private String tipo;

	@Column(name = "data_upload")
	private LocalDateTime dataUpload;

	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@Column(nullable = false)
	private boolean favorita;

	@Column(nullable = false)
	private boolean principal;

	@PrePersist
	public void prePersist() {
		this.dataUpload = LocalDateTime.now();
	}

	public Midia() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }

	public String getCaminho() { return caminho; }
	public void setCaminho(String caminho) { this.caminho = caminho; }

	public String getTipo() { return tipo; }
	public void setTipo(String tipo) { this.tipo = tipo; }

	public LocalDateTime getDataUpload() { return dataUpload; }
	public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }

	public Categoria getCategoria() { return categoria; }
	public void setCategoria(Categoria categoria) { this.categoria = categoria; }

	public Usuario getUsuario() { return usuario; }
	public void setUsuario(Usuario usuario) { this.usuario = usuario; }

	public boolean isFavorita() { return favorita; }
	public void setFavorita(boolean favorita) { this.favorita = favorita; }

	public boolean isPrincipal() { return principal; }
	public void setPrincipal(boolean principal) { this.principal = principal; }
}
