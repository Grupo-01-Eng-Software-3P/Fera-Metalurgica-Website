package com.fera.metalurgica.controller;

import com.fera.metalurgica.dto.*;
import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.exception.ResourceNotFoundException;
import com.fera.metalurgica.model.*;
import com.fera.metalurgica.service.OrcamentoPdfService;
import com.fera.metalurgica.service.SistemaService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;

@Controller
public class SistemaController {

	private final SistemaService service;
	private final OrcamentoPdfService pdfService;

	public SistemaController(SistemaService service, OrcamentoPdfService pdfService) {
		this.service = service;
		this.pdfService = pdfService;
	}

	@ModelAttribute("nomeUsuario")
	public String nomeUsuarioLogado(Authentication authentication) {
		if (!usuarioAutenticado(authentication)) {
			return "Visitante";
		}

		return service.buscarNomeUsuarioLogado(authentication.getName());
	}

	@ModelAttribute("usuarioEhAdmin")
	public boolean usuarioEhAdmin(Authentication authentication) {
		return usuarioAutenticado(authentication)
			&& authentication.getAuthorities().stream()
			.anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
	}

	private boolean usuarioAutenticado(Authentication authentication) {
		return authentication != null
			&& authentication.isAuthenticated()
			&& authentication.getAuthorities().stream()
			.noneMatch(authority -> "ROLE_ANONYMOUS".equals(authority.getAuthority()));
	}

	@GetMapping("/login")
	public String login() { return "login"; }

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("produtos", service.listarProdutos());
		return "home";
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		List<Pedido> todos = service.listarOrcamentos();
		LocalDate agora = LocalDate.now();
		model.addAttribute("orcamentosMes", todos.stream().filter(p -> p.getDataCriacao() != null && p.getDataCriacao().getMonthValue() == agora.getMonthValue()).count());
		model.addAttribute("orcamentosConcluidos", todos.stream().filter(p -> p.getItens() != null && !p.getItens().isEmpty()).count());
		model.addAttribute("atividades", service.listarAtividadesRecentesDashboard());
		return "dashboard";
	}

	// ── ORÇAMENTOS ───────────────────────────────────────
	@GetMapping("/orcamentos")
	public String orcamentos(Model model) {
		var orcamentos = service.organizarOrcamentos();
		model.addAttribute("orcamentos", service.listarOrcamentos());
		model.addAttribute("orcamentosMeus", orcamentos.meusPedidos());
		model.addAttribute("orcamentosClientesComOrcamento", orcamentos.clientesComOrcamento());
		model.addAttribute("orcamentosClientesPendentes", orcamentos.clientesPendentes());
		return "orcamentos";
	}

	@PostMapping("/pedido")
	public String salvarPedido(@Valid @ModelAttribute("pedidoDTO") PedidoDTO dto,
							   BindingResult result,
							   @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
							   Model model) {

		if (result.hasErrors() || !isCPFValido(dto.getCpf())) {
			if (!isCPFValido(dto.getCpf())) {
				model.addAttribute("erroCpf", "CPF Inválido. Por favor, verifique os números digitados.");
			}
			model.addAttribute("pedidoDTO", dto);
			return "pedido";
		}

		Pedido novoPedido = new Pedido();
		novoPedido.setCliente(dto.getCliente());
		novoPedido.setTelefone(dto.getTelefone());
		novoPedido.setCpf(dto.getCpf());
		novoPedido.setMaterial(dto.getMaterial());
		novoPedido.setMedidas(dto.getMedidas());
		novoPedido.setDescricao(dto.getDescricao());
		novoPedido.setCriadoPor("CLIENTE");

		try {
			service.adicionarPedido(novoPedido, arquivo);
		} catch (BusinessException ex) {
			model.addAttribute("erro", ex.getMessage());
			model.addAttribute("pedidoDTO", dto);
			return "pedido";
		}

		return "redirect:/pedido/confirmacao?nome=" + novoPedido.getCliente();
	}

	@GetMapping("/pedido")
	public String pedidoForm(Model model) {
		model.addAttribute("pedidoDTO", new PedidoDTO());
		return "pedido";
	}

	@GetMapping("/orcamentos/{id}/pdf")
	public ResponseEntity<byte[]> baixarPdfOrcamento(@PathVariable Long id) {
		Pedido pedido = service.buscarPedidoPorId(id);
		byte[] pdf = pdfService.gerarPdf(pedido);
		String nomeArquivo = "orcamento-" + id + ".pdf";

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_PDF)
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
			.contentLength(pdf.length)
			.body(pdf);
	}

	@GetMapping("/orcamentos/{id}/anexo")
	public ResponseEntity<Resource> visualizarAnexoOrcamento(@PathVariable Long id) throws IOException {
		Pedido pedido = service.buscarPedidoPorId(id);
		var arquivoPath = service.localizarArquivoAnexoPedido(pedido);
		Resource resource = new UrlResource(arquivoPath.toUri());

		if (!resource.exists() || !resource.isReadable()) {
			throw new ResourceNotFoundException("Anexo não encontrado para o pedido: " + id);
		}

		MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
		String tipoArquivo = pedido.getAnexoTipo();
		if (tipoArquivo != null && !tipoArquivo.isBlank()) {
			try {
				mediaType = MediaType.parseMediaType(tipoArquivo);
			} catch (IllegalArgumentException ignored) {
				mediaType = MediaType.APPLICATION_OCTET_STREAM;
			}
		}

		String nomeArquivo = pedido.getAnexoNomeOriginal();
		if (nomeArquivo == null || nomeArquivo.isBlank()) {
			nomeArquivo = arquivoPath.getFileName().toString();
		}

		ContentDisposition contentDisposition = ContentDisposition.inline()
			.filename(nomeArquivo, StandardCharsets.UTF_8)
			.build();

		return ResponseEntity.ok()
			.contentType(mediaType)
			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
			.body(resource);
	}

	@PostMapping("/orcamentos/salvar")
	 public String salvarOrcamento(@ModelAttribute OrcamentoAdminDTO dto,
 						  RedirectAttributes redirectAttributes) {

		service.salvarOrcamentoAdmin(dto);

		redirectAttributes.addFlashAttribute("orcamentoSalvo", "Orçamento salvo com sucesso.");
		return "redirect:/orcamentos";
	}

	// ── MÍDIA E AGENDA ───────────────────────────────────
	@GetMapping("/midia")
	public String midia(Model model) {
		model.addAttribute("categorias", service.listarCategorias());
		return "midia";
	}

	@GetMapping("/nova-categoria")
	public String novaCategoriaForm(Model model) {
		if (!model.containsAttribute("categoriaDTO")) {
			model.addAttribute("categoriaDTO", new CategoriaDTO());
		}
		return "nova-categoria";
	}

	@GetMapping("/midia/{slug}")
	public String midiaCategoria(@PathVariable String slug, Model model) {
		Categoria categoria = service.listarCategorias().stream()
			.filter(c -> c.getSlug() != null && c.getSlug().equals(slug))
			.findFirst()
			.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + slug));

		model.addAttribute("categoria", categoria);
		model.addAttribute("midias", categoria.getMidias());
		model.addAttribute("categoriaId", categoria.getId());
		return "midia-categoria";
	}

	@PostMapping("/nova-imagem")
	public String salvarImagem(@RequestParam("nome") String nome,
							   @RequestParam("descricao") String descricao,
							   @RequestParam("categoriaId") Long categoriaId,
							   @RequestParam("arquivo") MultipartFile arquivo,
							   RedirectAttributes redirectAttributes) {
		try {
			service.adicionarImagem(nome, descricao, categoriaId, arquivo);
			redirectAttributes.addFlashAttribute("sucesso", "Imagem adicionada com sucesso!");
		} catch (BusinessException ex) {
			redirectAttributes.addFlashAttribute("erro", ex.getMessage());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Categoria categoria = service.buscarCategoriaPorId(categoriaId);
		return "redirect:/midia/" + categoria.getSlug();
	}

	@PostMapping("/nova-categoria")
	public String salvarCategoria(@Valid @ModelAttribute("categoriaDTO") CategoriaDTO dto,
								  BindingResult result,
								  RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			FieldError fieldError = result.getFieldError();
			redirectAttributes.addFlashAttribute("categoriaDTO", dto);
			redirectAttributes.addFlashAttribute("erro", fieldError != null
				? fieldError.getDefaultMessage()
				: "Verifique os campos preenchidos.");
			return "redirect:/midia";
		}

		try {
			service.adicionarCategoria(dto);
			redirectAttributes.addFlashAttribute("sucesso", "Categoria criada com sucesso!");
		} catch (BusinessException ex) {
			redirectAttributes.addFlashAttribute("erro", ex.getMessage());
			redirectAttributes.addFlashAttribute("categoriaDTO", dto);
		}

		return "redirect:/midia";
	}

	@GetMapping("/agenda")
	public String agenda(Model model) {
		return "agenda";
	}

	@GetMapping("/agenda/dados")
	@ResponseBody
	public List<Atividade> listarDadosAgenda() {
		return service.listarAtividades();
	}

	@PostMapping("/agenda")
	@ResponseBody
	public String salvarAgendamento(@RequestBody Atividade atividade) {
		service.salvarAtividade(atividade);
		return "Sucesso";
	}

	// ── USUÁRIOS ─────────────────────────────────────────
	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", service.listarUsuarios());

		if (!model.containsAttribute("usuarioDTO")) {
			model.addAttribute("usuarioDTO", new UsuarioDTO());
		}

		return "usuarios";
	}

	@PostMapping("/novo-usuario")
	public String salvarUsuario(@Valid @ModelAttribute("usuarioDTO") UsuarioDTO dto,
								BindingResult result,
								RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			FieldError fieldError = result.getFieldError();
			redirectAttributes.addFlashAttribute("usuarioDTO", dto);
			redirectAttributes.addFlashAttribute("erro", fieldError != null
				? fieldError.getDefaultMessage()
				: "Verifique os campos preenchidos.");
			redirectAttributes.addFlashAttribute("erroCampo", fieldError != null
				? fieldError.getField()
				: "");
			return "redirect:/usuarios";
		}

		try {
			LocalDate dataConvertida = LocalDate.parse(dto.getDataNascimento());
			Usuario usuario = new Usuario(
				null,
				dto.getNome(),
				dto.getCargo(),
				dataConvertida,
				dto.getEmail(),
				dto.getSenha()
			);
			service.adicionarUsuario(usuario);
			redirectAttributes.addFlashAttribute("sucesso", "Usuário cadastrado com sucesso!");

		} catch (BusinessException ex) {
			redirectAttributes.addFlashAttribute("erro", ex.getMessage());
			redirectAttributes.addFlashAttribute("erroCampo", "email");
			redirectAttributes.addFlashAttribute("usuarioDTO", dto);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.usuarioDTO",
				new org.springframework.validation.BeanPropertyBindingResult(dto, "usuarioDTO"));
		}
		return "redirect:/usuarios";
	}

	// ── CATÁLOGO DINÂMICO ────────────────────────────────
	@GetMapping("/catalogo")
	public String catalogo(Model model) { model.addAttribute("categorias", service.listarCategorias()); return "catalogo"; }

	@GetMapping("/catalogo/ambiente/{slug}")
	public String catalogoAmbiente(@PathVariable String slug, Model model) {
		var categoria = service.listarCategorias().stream().filter(c -> c.getNome().equalsIgnoreCase(slug)).findFirst().orElse(null);
		if (categoria == null) return "redirect:/catalogo";
		model.addAttribute("categoria", categoria);
		model.addAttribute("midias", service.listarMidiasPorCategoria(categoria.getId()));
		return "catalogo-ambiente";
	}

	@GetMapping("/pedido/confirmacao")
	public String pedidoConfirmacao(@RequestParam(value = "nome", required = false) String nome, Model model) {
    model.addAttribute("nomeCliente", nome);
    return "pedido-confirmacao";
}

	// ── AUXILIARES ───────────────────────────────────────
	private boolean isCPFValido(String cpf) {
		cpf = cpf.replaceAll("[^0-9]", "");
		if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;
		try {
			int soma = 0, peso = 10;
			for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - 48) * peso--;
			int r = 11 - (soma % 11);
			char d10 = (r >= 10) ? '0' : (char) (r + 48);
			soma = 0; peso = 11;
			for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - 48) * peso--;
			r = 11 - (soma % 11);
			char d11 = (r >= 10) ? '0' : (char) (r + 48);
			return (d10 == cpf.charAt(9)) && (d11 == cpf.charAt(10));
		} catch (Exception e) { return false; }
	}

	private String limparCampo(String valor) {
		if (valor == null) {
			return null;
		}
		String limpo = valor.trim();
		return limpo.isBlank() ? null : limpo;
	}

	private void preencherCamposPedido(RedirectAttributes ra, String cliente, String telefone, String cpf,
									   String material, String medidas, String descricao) {
		ra.addFlashAttribute("clientePreenchido", cliente);
		ra.addFlashAttribute("telefonePreenchido", telefone);
		ra.addFlashAttribute("cpfPreenchido", cpf);
		ra.addFlashAttribute("materialPreenchido", material);
		ra.addFlashAttribute("medidasPreenchido", medidas);
		ra.addFlashAttribute("descricaoPreenchido", descricao);
	}
}
