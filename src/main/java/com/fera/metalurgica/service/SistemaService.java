package com.fera.metalurgica.service;

import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.exception.ResourceNotFoundException;
import com.fera.metalurgica.dto.*;
import com.fera.metalurgica.model.*;
import com.fera.metalurgica.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.nio.file.*;
import java.io.IOException;

@Service
public class SistemaService {

	private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter HORA_BR = DateTimeFormatter.ofPattern("HH:mm");

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
	public void corrigirSlugsCategorias() {
		for (Categoria categoria : categoriaRepository.findAll()) {
			if (categoria.getSlug() == null || categoria.getSlug().isBlank()) {
				categoria.setSlug(gerarSlugCategoria(categoria.getNome()));
				categoriaRepository.save(categoria);
			}
		}
	}

	@PostConstruct
	public void garantirUsuarioAdmin() {
		for (Usuario usuario : usuarioRepository.findAll()) {
			String senhaAtual = usuario.getSenha();
			if (senhaAtual != null && !senhaAtual.isBlank() && !senhaAtual.startsWith("$2")) {
				usuario.setSenha(passwordEncoder.encode(senhaAtual));
				usuarioRepository.save(usuario);
			}
		}

		Usuario admin = usuarioRepository.findByEmailIgnoreCase("admin@fera.com")
			.orElseGet(Usuario::new);

		admin.setNome("Lucas Stibbe");
		admin.setCargo("Administrador");
		admin.setDataNascimento(LocalDate.of(2007, 1, 19));
		admin.setEmail("admin@fera.com");
		admin.setSenha(passwordEncoder.encode("1234"));

		usuarioRepository.save(admin);
	}

	@PostConstruct
	public void removerAtividadesIniciais() {
		atividadeRepository.findAll().stream()
			.filter(this::isAtividadeInicialAntiga)
			.forEach(atividadeRepository::delete);
	}

	@PostConstruct
	public void criarProdutosIniciais() {
		if (produtoRepository.count() == 0) {
			produtoRepository.save(new Produto(null, "Mesa de Ferro", "Mesas"));
			produtoRepository.save(new Produto(null, "Estante de Metal", "Estantes"));
		}
	}


	// USUARIO

	public List<Usuario> listarUsuarios() {
		return usuarioRepository.findAll();
	}

	public String buscarNomeUsuarioLogado(String email) {
		if (email == null || email.isBlank()) {
			return "Visitante";
		}

		return usuarioRepository.findByEmailIgnoreCase(email)
			.map(Usuario::getNome)
			.filter(nome -> nome != null && !nome.isBlank())
			.orElse(email);
	}

	public void adicionarUsuario(Usuario usuario) {
		if (usuarioRepository.findByEmailIgnoreCase(usuario.getEmail()).isPresent()) {
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

	public Produto buscarProdutoPorId(Long id) {
		return produtoRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
	}


	// PEDIDO

	public List<Pedido> listarOrcamentos() {
		return pedidoRepository.findAllByOrderByDataCriacaoDesc();
	}

	public OrcamentosDTO organizarOrcamentos() {
		List<Pedido> pedidos = listarOrcamentos();
		List<Pedido> meusPedidos = new ArrayList<>();
		List<Pedido> clientesComOrcamento = new ArrayList<>();
		List<Pedido> clientesPendentes = new ArrayList<>();

		for (Pedido pedido : pedidos) {
			if (pedido.isOrcamentoGerado()) {
				clientesComOrcamento.add(pedido);
			} else if (pedido.isCriadoPorAdmin()) {
				meusPedidos.add(pedido);
			} else {
				clientesPendentes.add(pedido);
			}
		}

		return new OrcamentosDTO(meusPedidos, clientesComOrcamento, clientesPendentes);
	}


	@Transactional
	public Pedido salvarOrcamentoAdmin(OrcamentoAdminDTO dto) {

		Pedido pedido = (dto.getPedidoId() != null)
			? pedidoRepository.findById(dto.getPedidoId())
			.orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"))
			: new Pedido();

		pedido.setCliente(dto.getCliente());
		pedido.setTelefone(dto.getTelefone());
		pedido.setCpf(dto.getCpf());
		pedido.setMaterial(dto.getMaterial());
		pedido.setMedidas(dto.getMedidas());
		pedido.setDescricao(dto.getDescricao());

		if (pedido.getCriadoPor() == null) {
			pedido.setCriadoPor("ADMIN");
		}

		List<ItemPedido> itens = new ArrayList<>();

		for (ItemPedidoDTO itemDTO : dto.getItens()) {
			String nome        = itemDTO.getNome() != null ? itemDTO.getNome().trim() : "";
			Integer quantidade = parseInteiro(itemDTO.getQuantidade());
			BigDecimal valor   = parseMoeda(itemDTO.getValorUnitario());

			if (nome.isBlank() && (quantidade == null || quantidade <= 0) && valor.compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			ItemPedido item = new ItemPedido();
			item.setNomeItem(nome);
			item.setMaterial(nome);
			item.setQuantidade(quantidade != null ? quantidade : 0);
			item.setValorUnitario(valor);
			itens.add(item);
		}

		pedido.setItens(itens);
		pedido.setValorAdicionais(parseMoeda(dto.getFrete()).add(parseMoeda(dto.getMaoObra())));
		pedido.setObservacoesAdmin(dto.getObservacoesAdmin());
		pedido.calcularTotais();

		return pedidoRepository.save(pedido);
	}

	public void adicionarPedido(Pedido pedido) {
		adicionarPedido(pedido, null);
	}

	@Transactional
	public void adicionarPedido(Pedido pedido, MultipartFile arquivo) {
		if (pedido.getItens() != null) {
			for (ItemPedido item : pedido.getItens()) {
				item.setPedido(pedido);
			}
		}
		if (pedido.getCriadoPor() == null) {
			pedido.setCriadoPor("CLIENTE");
		}

		if (arquivo != null && !arquivo.isEmpty()) {
			salvarAnexoPedido(pedido, arquivo);
		}

		pedido.calcularTotais();
		pedidoRepository.save(pedido);

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

	public void salvarAtividade(Atividade atividade) {
		if (atividade.getId() != null && atividade.getDataCriacao() == null) {
			atividadeRepository.findById(atividade.getId())
				.map(Atividade::getDataCriacao)
				.ifPresent(atividade::setDataCriacao);
		}
		atividadeRepository.save(atividade);
	}

	public List<Atividade> listarAtividades() {
		return atividadeRepository.findAllByOrderByIdDesc();
	}

	public List<Atividade> listarAtividadesRecentesDashboard() {
		LocalDate hoje = LocalDate.now();
		LocalDateTime agora = LocalDateTime.now();
		List<Atividade> atividades = new ArrayList<>();

		for (Atividade atividade : atividadeRepository.findAll()) {
			if (atividade.getData() == null) {
				continue;
			}

			if (atividade.getData().equals(hoje)) {
				atividades.add(criarAtividadeRecente(
					atividade.getTitulo(),
					"Compromisso: " + valorOuPadrao(atividade.getTitulo(), "Sem título"),
					atividade.getEvento(),
					atividade.getData(),
					valorOuPadrao(atividade.getHorario(), "Hoje")
				));
			} else if (atividade.getData().isAfter(hoje) && foiCriadaNasUltimas24Horas(atividade, agora)) {
				atividades.add(criarAtividadeRecente(
					atividade.getTitulo(),
					"Compromisso criado para o dia " + DATA_BR.format(atividade.getData())
						+ ": " + valorOuPadrao(atividade.getTitulo(), "Sem título"),
					atividade.getEvento(),
					atividade.getData(),
					DATA_BR.format(atividade.getData())
				));
			}
		}

		for (Pedido pedido : pedidoRepository.findAllByOrderByDataCriacaoDesc()) {
			if (pedido.getDataCriacao() == null || !pedido.getDataCriacao().toLocalDate().equals(hoje)) {
				continue;
			}

			String numero = pedido.getId() != null ? pedido.getId().toString() : "-";
			String descricao = pedido.isOrcamentoGerado()
				? "Orçamento Nº" + numero + " criado"
				: "Pedido Nº" + numero + " realizado";

			atividades.add(criarAtividadeRecente(
				descricao,
				descricao,
				pedido.isOrcamentoGerado() ? "ORCAMENTO" : "PEDIDO",
				hoje,
				HORA_BR.format(pedido.getDataCriacao())
			));
		}

		return atividades.stream()
			.sorted(Comparator
				.comparing((Atividade atividade) -> atividade.getData() == null ? LocalDate.MIN : atividade.getData())
				.reversed()
				.thenComparing(Atividade::getId, Comparator.nullsLast(Comparator.reverseOrder())))
			.toList();
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

	private boolean isAtividadeInicialAntiga(Atividade atividade) {
		return ("Alerta de Estoque".equals(atividade.getTitulo()) && "ALERTA".equals(atividade.getEvento()))
			|| ("Reunião".equals(atividade.getTitulo()) && "REUNIAO".equals(atividade.getEvento()));
	}

	private boolean foiCriadaNasUltimas24Horas(Atividade atividade, LocalDateTime agora) {
		return atividade.getDataCriacao() != null
			&& !atividade.getDataCriacao().isAfter(agora)
			&& !atividade.getDataCriacao().isBefore(agora.minusHours(24));
	}

	private Atividade criarAtividadeRecente(String titulo, String descricao, String evento, LocalDate data, String horario) {
		return new Atividade(titulo, descricao, evento, data, horario);
	}

	private String valorOuPadrao(String valor, String padrao) {
		return valor == null || valor.isBlank() ? padrao : valor;
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


	private void salvarAnexoPedido(Pedido pedido, MultipartFile arquivo) {
		String nomeOriginal = limparNomeArquivoOriginal(arquivo.getOriginalFilename());
		String tipoArquivo = detectarTipoArquivoAnexo(arquivo, nomeOriginal);

		if (!anexoEhSuportado(tipoArquivo, nomeOriginal)) {
			throw new BusinessException("O anexo do pedido deve ser uma imagem ou PDF.");
		}

		String nomeArquivo = UUID.randomUUID() + "_" + gerarNomeArquivoAnexo(nomeOriginal, tipoArquivo);
		Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("pedidos");

		try {
			Files.createDirectories(uploadPath);
			Files.copy(arquivo.getInputStream(), uploadPath.resolve(nomeArquivo), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new BusinessException("Erro ao salvar o anexo do pedido: " + e.getMessage());
		}

		pedido.setAnexoCaminho("/imagens/pedidos/" + nomeArquivo);
		pedido.setAnexoNomeOriginal(nomeOriginal);
		pedido.setAnexoTipo(tipoArquivo);
	}

	private boolean anexoEhSuportado(String tipoArquivo, String nomeOriginal) {
		return anexoEhImagem(tipoArquivo, nomeOriginal)
			|| "application/pdf".equalsIgnoreCase(tipoArquivo);
	}

	private boolean anexoEhImagem(String tipoArquivo, String nomeOriginal) {
		if (tipoArquivo != null && tipoArquivo.toLowerCase(Locale.ROOT).startsWith("image/")) {
			return true;
		}

		if (nomeOriginal == null) {
			return false;
		}

		String nome = nomeOriginal.toLowerCase(Locale.ROOT);
		return nome.endsWith(".png")
			|| nome.endsWith(".jpg")
			|| nome.endsWith(".jpeg")
			|| nome.endsWith(".gif")
			|| nome.endsWith(".webp")
			|| nome.endsWith(".bmp");
	}

	private String detectarTipoArquivoAnexo(MultipartFile arquivo, String nomeOriginal) {
		String tipo = arquivo.getContentType();
		if (tipo != null && !tipo.isBlank()) {
			return tipo.toLowerCase(Locale.ROOT);
		}

		if (nomeOriginal == null) {
			return null;
		}

		String nome = nomeOriginal.toLowerCase(Locale.ROOT);
		if (nome.endsWith(".png")) return "image/png";
		if (nome.endsWith(".jpg") || nome.endsWith(".jpeg")) return "image/jpeg";
		if (nome.endsWith(".gif")) return "image/gif";
		if (nome.endsWith(".webp")) return "image/webp";
		if (nome.endsWith(".bmp")) return "image/bmp";
		if (nome.endsWith(".pdf")) return "application/pdf";
		return null;
	}

	private String limparNomeArquivoOriginal(String nomeOriginal) {
		if (nomeOriginal == null || nomeOriginal.isBlank()) {
			return "anexo";
		}

		String nome = nomeOriginal.replace("\\", "/");
		int indice = nome.lastIndexOf('/');
		if (indice >= 0 && indice < nome.length() - 1) {
			nome = nome.substring(indice + 1);
		}

		nome = nome.trim();
		return nome.isBlank() ? "anexo" : nome;
	}

	private String sanitizarNomeArquivo(String nomeOriginal) {
		String nome = nomeOriginal
			.replaceAll("[^a-zA-Z0-9._-]", "_")
			.replaceAll("_+", "_");
		return nome.isBlank() ? "anexo" : nome;
	}

	private String gerarNomeArquivoAnexo(String nomeOriginal, String tipoArquivo) {
		String base = removerExtensao(sanitizarNomeArquivo(nomeOriginal));
		String extensao = extensaoPadrao(tipoArquivo);
		return extensao.isBlank() ? base : base + extensao;
	}

	private String removerExtensao(String nomeArquivo) {
		int indice = nomeArquivo.lastIndexOf('.');
		if (indice <= 0) {
			return nomeArquivo;
		}

		return nomeArquivo.substring(0, indice);
	}

	private String extensaoPadrao(String tipoArquivo) {
		if (tipoArquivo == null || tipoArquivo.isBlank()) {
			return "";
		}

		return switch (tipoArquivo.toLowerCase(Locale.ROOT)) {
			case "image/png" -> ".png";
			case "image/jpeg" -> ".jpg";
			case "image/gif" -> ".gif";
			case "image/webp" -> ".webp";
			case "image/bmp" -> ".bmp";
			case "application/pdf" -> ".pdf";
			default -> "";
		};
	}

	public Path localizarArquivoAnexoPedido(Pedido pedido) {
		if (pedido == null || !pedido.isTemAnexo()) {
			throw new ResourceNotFoundException("Pedido não possui anexo.");
		}

		String nomeArquivo = Paths.get(pedido.getAnexoCaminho()).getFileName().toString();
		Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize().resolve("pedidos");
		Path arquivo = baseDir.resolve(nomeArquivo).normalize();

		if (!arquivo.startsWith(baseDir)) {
			throw new BusinessException("Caminho de anexo inválido.");
		}

		return arquivo;
	}

	// MIDIA / CATEGORIA

	public List<Categoria> listarCategorias() {
		return categoriaRepository.findAll();
	}

	public Categoria adicionarCategoria(CategoriaDTO dto) {
		String nome = normalizarTexto(dto.getNome());
		if (nome == null) {
			throw new BusinessException("O nome da categoria não pode ser vazio.");
		}

		if (categoriaRepository.findByNomeIgnoreCase(nome).isPresent()) {
			throw new BusinessException("Já existe uma categoria com o nome: " + nome);
		}

		Categoria categoria = new Categoria(null, nome, normalizarTexto(dto.getDescricao()), null);
		categoria.setSlug(gerarSlugCategoria(nome));
		return categoriaRepository.save(categoria);
	}

	public List<Midia> listarMidiasPorCategoria(Long id) {
		return midiaRepository.findByCategoriaId(id);
	}

	public Optional<Midia> buscarMidiaPrincipalDaCategoria(Long categoriaId) {
		return midiaRepository.findByCategoriaIdAndPrincipalTrue(categoriaId);
	}

	public List<Midia> listarMidiasFavoritas() {
		return midiaRepository.findByFavoritaTrueOrderByDataUploadDesc();
	}

	@Transactional
	public Midia alternarFavoritaMidia(Long midiaId) {
		Midia midia = midiaRepository.findById(midiaId)
			.orElseThrow(() -> new ResourceNotFoundException("Mídia não encontrada com id: " + midiaId));

		midia.setFavorita(!midia.isFavorita());
		return midiaRepository.save(midia);
	}

	@Transactional
	public Midia definirMidiaPrincipal(Long midiaId) {
		Midia midia = midiaRepository.findById(midiaId)
			.orElseThrow(() -> new ResourceNotFoundException("Mídia não encontrada com id: " + midiaId));

		Long categoriaId = midia.getCategoria().getId();

		// Se já é principal, desmarca (toggle)
		if (midia.isPrincipal()) {
			midia.setPrincipal(false);
			return midiaRepository.save(midia);
		}

		// Desmarca qualquer outra principal da categoria e marca esta
		midiaRepository.desmarcarPrincipalDaCategoria(categoriaId);
		midia.setPrincipal(true);
		return midiaRepository.save(midia);
	}

	@Transactional
	public String deletarMidia(Long midiaId) {
		Midia midia = midiaRepository.findById(midiaId)
			.orElseThrow(() -> new ResourceNotFoundException("Mídia não encontrada com id: " + midiaId));

		String slugCategoria = midia.getCategoria() != null ? midia.getCategoria().getSlug() : null;

		deletarArquivoMidia(midia.getCaminho());
		midiaRepository.delete(midia);

		return slugCategoria;
	}

	public void adicionarProduto(Produto produto) {
		if (produto.getNome() == null || produto.getNome().isBlank()) {
			throw new BusinessException("O nome do produto não pode ser vazio.");
		}
		produtoRepository.save(produto);
	}

	@Value("${upload.dir}")
	private String uploadDir;

	public void adicionarImagem(String nome, String descricao, Long categoriaId, MultipartFile arquivo) throws IOException {
		Categoria categoria = categoriaRepository.findById(categoriaId)
			.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + categoriaId));

		Midia midia = new Midia();
		midia.setNome(normalizarTexto(nome));
		midia.setDescricao(normalizarTexto(descricao));
		midia.setCaminho(salvarArquivoMidia(arquivo));
		midia.setTipo(arquivo.getContentType());
		midia.setFavorita(false);
		midia.setPrincipal(false);
		midia.setCategoria(categoria);
		midiaRepository.save(midia);
	}

	public Categoria buscarCategoriaPorId(Long id) {
		return categoriaRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com id: " + id));
	}

	@Transactional
	public Categoria atualizarCategoria(Long id, String nome, String descricao) {
		Categoria categoria = buscarCategoriaPorId(id);
		String nomeLimpo = normalizarTexto(nome);

		if (nomeLimpo == null) {
			throw new BusinessException("O nome da categoria não pode ser vazio.");
		}

		categoriaRepository.findByNomeIgnoreCase(nomeLimpo)
			.filter(existing -> !existing.getId().equals(id))
			.ifPresent(existing -> {
				throw new BusinessException("Já existe uma categoria com o nome: " + nomeLimpo);
			});

		categoria.setNome(nomeLimpo);
		categoria.setDescricao(normalizarTexto(descricao));
		categoria.setSlug(gerarSlugCategoria(nomeLimpo));
		return categoriaRepository.save(categoria);
	}

	public Midia buscarMidiaPorId(Long id) {
		return midiaRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Mídia não encontrada com id: " + id));
	}

	@Transactional
	public Midia atualizarMidia(Long id, String nome, String descricao, MultipartFile arquivo) throws IOException {
		Midia midia = buscarMidiaPorId(id);
		String nomeLimpo = normalizarTexto(nome);

		if (nomeLimpo == null) {
			throw new BusinessException("O nome da imagem não pode ser vazio.");
		}

		midia.setNome(nomeLimpo);
		midia.setDescricao(normalizarTexto(descricao));

		if (arquivo != null && !arquivo.isEmpty()) {
			deletarArquivoMidia(midia.getCaminho());
			midia.setCaminho(salvarArquivoMidia(arquivo));
			midia.setTipo(arquivo.getContentType());
		}

		return midiaRepository.save(midia);
	}

	private void deletarArquivoMidia(String caminho) {
		if (caminho == null || caminho.isBlank()) {
			return;
		}

		try {
			String nomeArquivo = Paths.get(caminho).getFileName().toString();
			Path arquivo = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(nomeArquivo);

			if (arquivo.startsWith(Paths.get(uploadDir).toAbsolutePath().normalize())) {
				Files.deleteIfExists(arquivo);
			}
		} catch (IOException e) {
			System.err.println("Aviso: não foi possível deletar o arquivo antigo de mídia: " + e.getMessage());
		}
	}

	private String normalizarTexto(String valor) {
		if (valor == null) {
			return null;
		}

		String limpo = valor.trim();
		return limpo.isBlank() ? null : limpo;
	}

	private String gerarSlugCategoria(String nome) {
		String texto = normalizarTexto(nome);
		if (texto == null) {
			return "";
		}

		String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
			.replaceAll("\\p{M}+", "");

		return semAcentos
			.toLowerCase(Locale.ROOT)
			.replaceAll("[^a-z0-9\\s]", "")
			.trim()
			.replaceAll("\\s+", "-");
	}

	private String salvarArquivoMidia(MultipartFile arquivo) throws IOException {
		String nomeOriginal = limparNomeArquivoOriginal(arquivo.getOriginalFilename());
		String nomeArquivo = UUID.randomUUID() + "_" + nomeOriginal;
		Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
		Path destino = uploadPath.resolve(nomeArquivo);

		Files.createDirectories(uploadPath);
		Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

		return "/imagens/" + nomeArquivo;
	}
}
