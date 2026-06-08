package com.fera.metalurgica.controller;

import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.model.*;
import com.fera.metalurgica.service.OrcamentoPdfService;
import com.fera.metalurgica.service.SistemaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class SistemaController {

	private final SistemaService service;
	private final OrcamentoPdfService pdfService;

	public SistemaController(SistemaService service, OrcamentoPdfService pdfService) {
		this.service = service;
		this.pdfService = pdfService;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("produtos", service.listarProdutos());
		return "home";
	}

	// ── ORÇAMENTOS & KANBAN ──────────────────────────────
	@GetMapping("/orcamentos")
	public String orcamentos(Model model) {
		var orcamentos = service.organizarOrcamentos();
		model.addAttribute("orcamentosMeus", orcamentos.meusPedidos());
		model.addAttribute("orcamentosClientesComOrcamento", orcamentos.clientesComOrcamento());
		model.addAttribute("orcamentosClientesPendentes", orcamentos.clientesPendentes());
		return "orcamentos";
	}

	@PostMapping("/orcamentos/salvar")
	public String salvarOrcamento(@RequestParam(required = false) Long pedidoId,
								  @RequestParam String cliente, @RequestParam String telefone,
								  @RequestParam String cpf, @RequestParam String material,
								  @RequestParam(required = false) String medidas, @RequestParam String descricao,
								  @RequestParam(required = false) List<String> itemNome,
								  @RequestParam(required = false) List<String> itemQuantidade,
								  @RequestParam(required = false) List<String> itemValorUnitario,
								  @RequestParam(required = false) String frete,
								  @RequestParam(required = false) String maoObra,
								  @RequestParam(required = false) String observacoesAdmin,
								  RedirectAttributes ra) {

		service.salvarOrcamentoAdmin(pedidoId, cliente, telefone, cpf, material, medidas, descricao,
			itemNome, itemQuantidade, itemValorUnitario, frete, maoObra, observacoesAdmin);
		ra.addFlashAttribute("orcamentoSalvo", "Orçamento processado!");
		return "redirect:/orcamentos";
	}

	@GetMapping("/orcamentos/{id}/pdf")
	public ResponseEntity<byte[]> baixarPdfOrcamento(@PathVariable Long id) {
		Pedido p = service.buscarPedidoPorId(id);
		byte[] pdf = pdfService.gerarPdf(p);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"orcamento-" + id + ".pdf\"")
			.body(pdf);
	}

	// ── PEDIDO CLIENTE ───────────────────────────────────
	@GetMapping("/pedido") public String pedidoForm() { return "pedido"; }

	@PostMapping("/pedido")
	public String salvarPedido(@RequestParam String cliente, @RequestParam String telefone,
							   @RequestParam String cpf, @RequestParam String material,
							   @RequestParam(required = false) String medidas, @RequestParam String descricao,
							   Model model) {
		if (!isCPFValido(cpf)) { model.addAttribute("erroCpf", "CPF Inválido."); return "pedido"; }
		Pedido p = new Pedido();
		p.setCliente(cliente); p.setTelefone(telefone); p.setCpf(cpf);
		p.setMaterial(material); p.setMedidas(medidas); p.setDescricao(descricao);
		p.setCriadoPor("CLIENTE");
		service.adicionarPedido(p);
		return "redirect:/";
	}

	// ── DASHBOARD ────────────────────────────────────────
	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		List<Pedido> todos = service.listarOrcamentos();
		LocalDate agora = LocalDate.now();
		model.addAttribute("orcamentosMes", todos.stream().filter(p -> p.getDataCriacao() != null && p.getDataCriacao().getMonthValue() == agora.getMonthValue()).count());
		model.addAttribute("orcamentosConcluidos", todos.stream().filter(p -> p.getItens() != null && !p.getItens().isEmpty()).count());
		model.addAttribute("atividades", service.listarAtividades());
		return "dashboard";
	}

	// ── USUÁRIOS ─────────────────────────────────────────
	@GetMapping("/usuarios") public String usuarios(Model model) { model.addAttribute("usuarios", service.listarUsuarios()); return "usuarios"; }

	@PostMapping("/novo-usuario")
	public String salvarUsuario(@RequestParam String nome, @RequestParam String cargo,
								@RequestParam String dataNascimento, @RequestParam String email,
								@RequestParam String senha, RedirectAttributes ra) {
		try {
			service.adicionarUsuario(new Usuario(null, nome, cargo, LocalDate.parse(dataNascimento), email, senha));
			ra.addFlashAttribute("sucesso", "Usuário cadastrado!");
		} catch (BusinessException ex) { ra.addFlashAttribute("erro", ex.getMessage()); }
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
}
