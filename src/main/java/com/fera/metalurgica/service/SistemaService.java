package com.fera.metalurgica.service;

import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.exception.ResourceNotFoundException;
import com.fera.metalurgica.model.*;
import com.fera.metalurgica.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SistemaService {

	@Autowired private ProdutoRepository produtoRepository;
	@Autowired private PedidoRepository pedidoRepository;
	@Autowired private AtividadeRepository atividadeRepository;
	@Autowired private UsuarioRepository usuarioRepository;
	@Autowired private PasswordEncoder passwordEncoder;

	public record OrcamentosAgrupados(List<Pedido> meusPedidos,
									  List<Pedido> clientesComOrcamento,
									  List<Pedido> clientesPendentes) {}

	@PostConstruct
	public void criarAtividadesIniciais() {
		if (atividadeRepository.count() == 0) {
			atividadeRepository.save(new Atividade("Alerta de Estoque", "Aço 2mm está fora de estoque!", "ALERTA", LocalDate.now(), "Há 17h"));
			atividadeRepository.save(new Atividade("Reunião", "Reunião marcada para amanhã às 9:00.", "REUNIAO", LocalDate.now().plusDays(1), "Ontem"));
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

	// ── USUÁRIO ──────────────────────────────────────────
	public List<Usuario> listarUsuarios() { return usuarioRepository.findAll(); }

	public void adicionarUsuario(Usuario usuario) {
		if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
			throw new BusinessException("E-mail já cadastrado: " + usuario.getEmail());
		}
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		usuarioRepository.save(usuario);
	}

	// ── PRODUTO ──────────────────────────────────────────
	public List<Produto> listarProdutos() { return produtoRepository.findAll(); }

	public void adicionarProduto(Produto produto) {
		if (produto.getNome() == null || produto.getNome().isBlank()) throw new BusinessException("Nome do produto vazio.");
		produtoRepository.save(produto);
	}

	// ── PEDIDO ───────────────────────────────────────────
	public List<Pedido> listarOrcamentos() { return pedidoRepository.findAllByOrderByDataCriacaoDesc(); }

	public OrcamentosAgrupados organizarOrcamentos() {
		List<Pedido> pedidos = listarOrcamentos();
		List<Pedido> meus = new ArrayList<>(), comOrc = new ArrayList<>(), pend = new ArrayList<>();
		for (Pedido p : pedidos) {
			if (p.isCriadoPorAdmin()) meus.add(p);
			else if (p.isOrcamentoFinalizado()) comOrc.add(p);
			else pend.add(p);
		}
		return new OrcamentosAgrupados(meus, comOrc, pend);
	}

	@Transactional
	public Pedido salvarOrcamentoAdmin(Long pedidoId, String cliente, String telefone, String cpf, String material, String medidas, String descricao,
									   List<String> itemNomes, List<String> itemQuantidades, List<String> itemValoresUnitarios,
									   String frete, String maoObra, String observacoesAdmin) {

		Pedido pedido = (pedidoId != null) ? pedidoRepository.findById(pedidoId).orElseThrow() : new Pedido();
		pedido.setCliente(cliente); pedido.setTelefone(telefone); pedido.setCpf(cpf);
		pedido.setMaterial(material); pedido.setMedidas(medidas); pedido.setDescricao(descricao);
		if (pedido.getCriadoPor() == null) pedido.setCriadoPor("ADMIN");

		List<ItemPedido> itens = new ArrayList<>();
		int totalLinhas = Math.max(tamanho(itemNomes), Math.max(tamanho(itemQuantidades), tamanho(itemValoresUnitarios)));

		for (int i = 0; i < totalLinhas; i++) {
			String nome = valorOuVazio(itemNomes, i);
			Integer qtd = parseInteiro(valorOuVazio(itemQuantidades, i));
			BigDecimal valor = parseMoeda(valorOuVazio(itemValoresUnitarios, i));
			if (nome.isBlank() && (qtd == null || qtd <= 0) && valor.compareTo(BigDecimal.ZERO) == 0) continue;

			ItemPedido item = new ItemPedido();
			item.setNomeItem(nome); item.setQuantidade(qtd != null ? qtd : 0); item.setValorUnitario(valor); item.setPedido(pedido);
			itens.add(item);
		}

		pedido.setItens(itens);
		pedido.setValorAdicionais(parseMoeda(frete).add(parseMoeda(maoObra)));
		pedido.setObservacoesAdmin(observacoesAdmin);
		pedido.calcularTotais();
		return pedidoRepository.save(pedido);
	}

	// ── ATIVIDADE ────────────────────────────────────────
	public List<Atividade> listarAtividades() { return atividadeRepository.findAllByOrderByIdDesc(); }
	public Atividade adicionarAtividade(Atividade atividade) { return atividadeRepository.save(atividade); }

	// ── AUXILIARES ───────────────────────────────────────
	private int tamanho(List<?> lista) { return lista == null ? 0 : lista.size(); }
	private String valorOuVazio(List<String> lista, int i) { return (lista == null || i >= lista.size()) ? "" : lista.get(i).trim(); }
	private Integer parseInteiro(String v) { try { return Integer.parseInt(v.replaceAll("[^0-9-]", "")); } catch (Exception e) { return null; } }
	private BigDecimal parseMoeda(String v) {
		String n = v.replaceAll("[^0-9,.-]", "");
		try { return new BigDecimal(n.replace(".", "").replace(",", ".")); } catch (Exception e) { return BigDecimal.ZERO; }
	}
}
