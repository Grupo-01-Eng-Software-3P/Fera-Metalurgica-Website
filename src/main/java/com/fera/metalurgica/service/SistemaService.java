package com.fera.metalurgica.service;

import com.fera.metalurgica.dto.OrcamentosDTO;
import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.model.*;
import com.fera.metalurgica.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SistemaService {

	private final ProdutoRepository produtoRepository;
	private final PedidoRepository pedidoRepository;
	private final UsuarioRepository usuarioRepository;
	private final CategoriaRepository categoriaRepository;
	private final MidiaRepository midiaRepository;
	private final AtividadeRepository atividadeRepository;

	public SistemaService(ProdutoRepository produtoRepository,
						  PedidoRepository pedidoRepository,
						  UsuarioRepository usuarioRepository,
						  CategoriaRepository categoriaRepository,
						  MidiaRepository midiaRepository,
						  AtividadeRepository atividadeRepository) {
		this.produtoRepository = produtoRepository;
		this.pedidoRepository = pedidoRepository;
		this.usuarioRepository = usuarioRepository;
		this.categoriaRepository = categoriaRepository;
		this.midiaRepository = midiaRepository;
		this.atividadeRepository = atividadeRepository;
	}

	// ── MÉTODOS DE ATIVIDADE (AGENDA) ──
	public List<Atividade> listarAtividades() {
		return atividadeRepository.findAll();
	}

	public void salvarAtividade(Atividade atividade) {
		atividadeRepository.save(atividade);
	}

	// ── MÉTODOS DE USUÁRIOS ──
	public List<Usuario> listarUsuarios() {
		return usuarioRepository.findAll();
	}

	public void adicionarUsuario(Usuario usuario) {
		if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
			throw new BusinessException("E-mail já cadastrado!");
		}
		usuarioRepository.save(usuario);
	}

	// ── MÉTODOS DE ORÇAMENTOS ──
	public List<Pedido> listarOrcamentos() {
		return pedidoRepository.findAll();
	}

	public OrcamentosDTO organizarOrcamentos() {
		List<Pedido> todos = pedidoRepository.findAll();
		return new OrcamentosDTO(
			todos.stream().filter(p -> "ADM".equals(p.getCriadoPor())).toList(),
			todos.stream().filter(p -> p.getValorTotal() != null).toList(),
			todos.stream().filter(p -> p.getValorTotal() == null).toList()
		);
	}

	public void salvarOrcamentoAdmin(Long id, String cliente, String tel, String cpf, String mat,
									 String med, String desc, List<String> itens, List<String> qtds,
									 List<String> vals, String frete, String maoObra, String obs) {
		// Mantenha aqui sua lógica de persistência existente
	}

	// ── MÉTODOS DE PRODUTOS E CATÁLOGO ──
	public List<Produto> listarProdutos() {
		return produtoRepository.findAll();
	}

	public List<Categoria> listarCategorias() {
		return categoriaRepository.findAll();
	}

	public List<Midia> listarMidiasPorCategoria(Long id) {
		return midiaRepository.findByCategoriaId(id);
	}
}
