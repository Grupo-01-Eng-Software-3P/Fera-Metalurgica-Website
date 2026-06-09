package com.fera.metalurgica.service;

import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.exception.ResourceNotFoundException;
import com.fera.metalurgica.model.Atividade;
import com.fera.metalurgica.model.ItemPedido;
import com.fera.metalurgica.model.Pedido;
import com.fera.metalurgica.model.Produto;
import com.fera.metalurgica.model.Usuario;
import com.fera.metalurgica.repository.AtividadeRepository;
import com.fera.metalurgica.repository.PedidoRepository;
import com.fera.metalurgica.repository.ProdutoRepository;
import com.fera.metalurgica.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

@Service
public class SistemaService {

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

    @PostConstruct
    public void criarAtividadesIniciais() {
        if (atividadeRepository.count() == 0) {
            atividadeRepository.save(new Atividade(
                    "Alerta de Estoque", "Aço 2mm está fora de estoque!", "ALERTA",
                    LocalDate.now(), "Há 17h"
            ));
            atividadeRepository.save(new Atividade(
                    "Reunião", "Reunião marcada para amanhã às 9:00.", "REUNIAO",
                    LocalDate.now().plusDays(1), "Ontem"
            ));
        }
    }

    @PostConstruct
    public void criarProdutosIniciais() {
        if (produtoRepository.count() == 0) {
            produtoRepository.save(new Produto(null, "Mesa de Ferro", "Mesas"));
            produtoRepository.save(new Produto(null, "Estante de Metal", "Estantes"));
        }
    }

    @PostConstruct
    public void criarAdmin() {
        if (usuarioRepository.findByEmail("admin@fera.com") == null) {
            Usuario admin = new Usuario();
            admin.setNome("Lucas Stibbe");
            admin.setCargo("Administrador");
            admin.setDataNascimento(LocalDate.of(2007, 1, 19));
            admin.setEmail("admin@fera.com");
            admin.setSenha(passwordEncoder.encode("1234"));
            usuarioRepository.save(admin);
        }
    }

	// USUARIO

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public void adicionarUsuario(Usuario usuario) {

		if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
			throw new BusinessException("E-mail já cadastrado: " + usuario.getEmail());
		}
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
    }

	public Usuario buscarUsuarioPorId(Long id) {
		return usuarioRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
	}


	// PRODUTO

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    public void adicionarProduto(Produto produto) {

		if (produto.getNome() == null || produto.getNome().isBlank()) {
			throw new BusinessException("O nome do produto não pode ser vazio.");
		}
		produtoRepository.save(produto);
    }

	public Produto buscarProdutoPorId(Long id) {
		return produtoRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
	}


	// PEDIDO

    public List<Pedido> listarOrcamentos() {
        return pedidoRepository.findAllByOrderByDataCriacaoDesc();
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
	}


	// ATIVIDADE

    public List<Atividade> listarAtividades() {
        return atividadeRepository.findAllByOrderByIdDesc();
    }

    public Atividade adicionarAtividade(Atividade atividade) {

        if (atividade.getTitulo() == null || atividade.getTitulo().isBlank()) {
			throw new BusinessException("O título da atividade não pode ser vazio.");
		}
		return atividadeRepository.save(atividade);
    }

    public List<Atividade> listarPorData(LocalDate data) {
        return atividadeRepository.findAll().stream()
                .filter(a -> data.equals(a.getData()))
                .toList();
    }

    private int tamanho(List<?> lista) {
        return lista == null ? 0 : lista.size();
    }

    private String valorOuVazio(List<String> lista, int indice) {
        if (lista == null || indice >= lista.size() || lista.get(indice) == null) {
            return "";
        }
        return lista.get(indice).trim();
    }

    private Integer parseInteiro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        String normalizado = valor.replaceAll("[^0-9-]", "");
        if (normalizado.isBlank() || normalizado.equals("-")) {
            return null;
        }

        try {
            return Integer.parseInt(normalizado);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private BigDecimal parseMoeda(String valor) {
        if (valor == null || valor.isBlank()) {
            return BigDecimal.ZERO;
        }

        String normalizado = valor.trim()
                .replace("R$", "")
                .replaceAll("\\s+", "")
                .replaceAll("[^0-9,.-]", "");

        if (normalizado.isBlank()) {
            return BigDecimal.ZERO;
        }

        if (normalizado.contains(",") && normalizado.contains(".")) {
            if (normalizado.lastIndexOf(',') > normalizado.lastIndexOf('.')) {
                normalizado = normalizado.replace(".", "").replace(",", ".");
            } else {
                normalizado = normalizado.replace(",", "");
            }
        } else if (normalizado.contains(",")) {
            normalizado = normalizado.replace(".", "").replace(",", ".");
        } else if (normalizado.indexOf('.') != normalizado.lastIndexOf('.')) {
            int ultimaPosicao = normalizado.lastIndexOf('.');
            normalizado = normalizado.substring(0, ultimaPosicao).replace(".", "")
                    + "."
                    + normalizado.substring(ultimaPosicao + 1);
        }

        try {
            return new BigDecimal(normalizado);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }
}
