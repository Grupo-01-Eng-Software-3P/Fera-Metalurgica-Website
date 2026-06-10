package com.fera.metalurgica.service;

import com.fera.metalurgica.dto.OrcamentosDTO;
import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.model.*;
import com.fera.metalurgica.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SistemaService {

<<<<<<< HEAD
	private final ProdutoRepository produtoRepository;
	private final PedidoRepository pedidoRepository;
	private final UsuarioRepository usuarioRepository;
	private final CategoriaRepository categoriaRepository;
	private final MidiaRepository midiaRepository;
	private final AtividadeRepository atividadeRepository;
=======
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private AtividadeRepository atividadeRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ZApiService zApiService;
>>>>>>> 4379e127ffcb59a38dbef89521c21fa82484ad80

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

<<<<<<< HEAD
	public void salvarAtividade(Atividade atividade) {
		atividadeRepository.save(atividade);
=======

	// PEDIDO

    public List<Pedido> listarOrcamentos() {
        return pedidoRepository.findAllByOrderByDataCriacaoDesc();
    }

    public OrcamentosAgrupados organizarOrcamentos() {
        List<Pedido> pedidos = listarOrcamentos();
        List<Pedido> meusPedidos = new ArrayList<>();
        List<Pedido> clientesComOrcamento = new ArrayList<>();
        List<Pedido> clientesPendentes = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            if (pedido.isCriadoPorAdmin()) {
                meusPedidos.add(pedido);
            } else if (pedido.isOrcamentoFinalizado()) {
                clientesComOrcamento.add(pedido);
            } else {
                clientesPendentes.add(pedido);
            }
        }

        return new OrcamentosAgrupados(meusPedidos, clientesComOrcamento, clientesPendentes);
    }

    @Transactional
    public Pedido salvarOrcamentoAdmin(Long pedidoId,
                                       String cliente,
                                       String telefone,
                                       String cpf,
                                       String material,
                                       String medidas,
                                       String descricao,
                                       List<String> itemNomes,
                                       List<String> itemQuantidades,
                                       List<String> itemValoresUnitarios,
                                       String frete,
                                       String maoObra,
                                       String observacoesAdmin) {

        Pedido pedido = (pedidoId != null)
                ? pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"))
                : new Pedido();

        pedido.setCliente(cliente);
        pedido.setTelefone(telefone);
        pedido.setCpf(cpf);
        pedido.setMaterial(material);
        pedido.setMedidas(medidas);
        pedido.setDescricao(descricao);

        if (pedido.getCriadoPor() == null) {
            pedido.setCriadoPor("ADMIN");
        }

        List<ItemPedido> itens = new ArrayList<>();
        int totalLinhas = Math.max(
                Math.max(tamanho(itemNomes), tamanho(itemQuantidades)),
                tamanho(itemValoresUnitarios)
        );

        for (int i = 0; i < totalLinhas; i++) {
            String nome = valorOuVazio(itemNomes, i);
            Integer quantidade = parseInteiro(valorOuVazio(itemQuantidades, i));
            BigDecimal valorUnitario = parseMoeda(valorOuVazio(itemValoresUnitarios, i));

            if (nome.isBlank() && (quantidade == null || quantidade <= 0) && valorUnitario.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            ItemPedido item = new ItemPedido();
            item.setNomeItem(nome);
            item.setMaterial(nome);
            item.setQuantidade(quantidade != null ? quantidade : 0);
            item.setValorUnitario(valorUnitario);
            itens.add(item);
        }

        pedido.setItens(itens);
        pedido.setValorAdicionais(parseMoeda(frete).add(parseMoeda(maoObra)));
        pedido.setObservacoesAdmin(observacoesAdmin);
        pedido.calcularTotais();

        return pedidoRepository.save(pedido);
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
>>>>>>> 4379e127ffcb59a38dbef89521c21fa82484ad80
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
