package com.fera.metalurgica.service;

import com.fera.metalurgica.exception.ResourceNotFoundException;
import com.fera.metalurgica.dto.OrcamentosDTO;
import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.model.*;
import com.fera.metalurgica.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.List;

@Service
public class SistemaService {

	private final ProdutoRepository produtoRepository;
	private final PedidoRepository pedidoRepository;
	private final UsuarioRepository usuarioRepository;
	private final CategoriaRepository categoriaRepository;
	private final MidiaRepository midiaRepository;
	private final AtividadeRepository atividadeRepository;
	private final ZApiService zApiService;
	private final PasswordEncoder passwordEncoder;

	public SistemaService(ProdutoRepository produtoRepository,
						  PedidoRepository pedidoRepository,
						  UsuarioRepository usuarioRepository,
						  CategoriaRepository categoriaRepository,
						  MidiaRepository midiaRepository,
						  AtividadeRepository atividadeRepository,
						  ZApiService zApiService,
						  PasswordEncoder passwordEncoder) {
		this.produtoRepository = produtoRepository;
		this.pedidoRepository = pedidoRepository;
		this.usuarioRepository = usuarioRepository;
		this.categoriaRepository = categoriaRepository;
		this.midiaRepository = midiaRepository;
		this.atividadeRepository = atividadeRepository;
		this.zApiService = zApiService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostConstruct
	public void garantirUsuarioAdmin() {
		boolean adminExiste = false;

		for (Usuario usuario : usuarioRepository.findAll()) {
			if ("admin@fera.com".equalsIgnoreCase(usuario.getEmail())) {
				adminExiste = true;
				if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
					usuario.setSenha(passwordEncoder.encode("1234"));
					usuarioRepository.save(usuario);
					continue;
				}
			}

			String senhaAtual = usuario.getSenha();
			if (senhaAtual != null && !senhaAtual.isBlank() && !senhaAtual.startsWith("$2")) {
				usuario.setSenha(passwordEncoder.encode(senhaAtual));
				usuarioRepository.save(usuario);
			}
		}

		if (!adminExiste) {
			Usuario admin = new Usuario(
				null,
				"Lucas Stibbe",
				"Administrador",
				LocalDate.of(2007, 1, 19),
				"admin@fera.com",
				passwordEncoder.encode("1234")
			);
			usuarioRepository.save(admin);
		}
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
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
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

	public void adicionarPedido(Pedido pedido) {
		if (pedido.getItens() != null) {
			for (ItemPedido item : pedido.getItens()) {
				item.setPedido(pedido);
			}
		}
		if (pedido.getCriadoPor() == null) {
			pedido.setCriadoPor("CLIENTE");
		}
		pedido.calcularTotais();
		pedidoRepository.save(pedido);

		// Notificação WhatsApp
		zApiService.enviarNotificacaoOrcamento(
			pedido.getCliente(),
			pedido.getTelefone(),
			pedido.getDescricao()
		);
	}

	public Pedido buscarPedidoPorId(Long id) {
    return pedidoRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com id: " + id));
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
