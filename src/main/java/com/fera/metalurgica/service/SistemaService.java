package com.fera.metalurgica.service;

import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.exception.ResourceNotFoundException;
import com.fera.metalurgica.model.*;
import com.fera.metalurgica.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SistemaService {

	@Autowired private ProdutoRepository produtoRepository;
	@Autowired private PedidoRepository pedidoRepository;
	@Autowired private AtividadeRepository atividadeRepository;
	@Autowired private UsuarioRepository usuarioRepository;
	@Autowired private CategoriaRepository categoriaRepository;
	@Autowired private MidiaRepository midiaRepository;
	@Autowired private PasswordEncoder passwordEncoder;

	@Value("${upload.dir:uploads/}")
	private String uploadDir;

	public record OrcamentosAgrupados(List<Pedido> meusPedidos,
									  List<Pedido> clientesComOrcamento,
									  List<Pedido> clientesPendentes) {}

	// ── INIT ─────────────────────────────────────────────
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

	// ── USUÁRIOS & PRODUTOS ──────────────────────────────
	public List<Usuario> listarUsuarios() { return usuarioRepository.findAll(); }
	public void adicionarUsuario(Usuario usuario) {
		if (usuarioRepository.findByEmail(usuario.getEmail()) != null) throw new BusinessException("E-mail já cadastrado.");
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		usuarioRepository.save(usuario);
	}

	public List<Produto> listarProdutos() { return produtoRepository.findAll(); }
	public void adicionarProduto(Produto p) { if(p.getNome() == null || p.getNome().isBlank()) throw new BusinessException("Nome vazio."); produtoRepository.save(p); }

	// ── ORÇAMENTOS (PEDIDOS) ─────────────────────────────
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
	public Pedido salvarOrcamentoAdmin(Long pedidoId, String cliente, String telefone, String cpf, String material,
									   String medidas, String descricao, List<String> itemNomes, List<String> itemQuantidades,
									   List<String> itemValoresUnitarios, String frete, String maoObra, String obs) {

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
		pedido.setObservacoesAdmin(obs);
		pedido.calcularTotais();
		return pedidoRepository.save(pedido);
	}

	// ── MÍDIA & ATIVIDADE ────────────────────────────────
	public List<Midia> listarMidiasPorCategoria(Long catId) { return midiaRepository.findByCategoria(categoriaRepository.findById(catId).orElse(null)); }

	public Midia adicionarMidia(MultipartFile arquivo, String nome, String desc, Long catId) throws IOException {
		CategoriaEntity cat = categoriaRepository.findById(catId).orElseThrow();
		Path pasta = Paths.get(uploadDir).toAbsolutePath().normalize();
		Files.createDirectories(pasta);
		String nomeArquivo = System.currentTimeMillis() + "_" + StringUtils.cleanPath(arquivo.getOriginalFilename());
		Files.copy(arquivo.getInputStream(), pasta.resolve(nomeArquivo), StandardCopyOption.REPLACE_EXISTING);

		Midia midia = new Midia();
		midia.setNome(nome); midia.setDescricao(desc); midia.setCaminho("/uploads/" + nomeArquivo);
		midia.setCategoria(cat);
		return midiaRepository.save(midia);
	}

	public List<Atividade> listarAtividades() { return atividadeRepository.findAllByOrderByIdDesc(); }
	public Atividade adicionarAtividade(Atividade a) { return atividadeRepository.save(a); }

	// ── HELPERS ──────────────────────────────────────────
	private int tamanho(List<?> l) { return l == null ? 0 : l.size(); }
	private String valorOuVazio(List<String> l, int i) { return (l == null || i >= l.size()) ? "" : l.get(i).trim(); }
	private Integer parseInteiro(String v) { try { return Integer.parseInt(v.replaceAll("[^0-9-]", "")); } catch (Exception e) { return null; } }
	private BigDecimal parseMoeda(String v) {
		String n = v.replaceAll("[^0-9,.-]", "");
		try { return new BigDecimal(n.replace(".", "").replace(",", ".")); } catch (Exception e) { return BigDecimal.ZERO; }
	}
}
