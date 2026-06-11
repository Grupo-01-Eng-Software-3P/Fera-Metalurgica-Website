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

	@GetMapping("/login")
	public String login() { return "login"; }

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("produtos", service.listarProdutos());
		return "home";
	}

	@GetMapping("/pedido")
	public String pedido() {
		return "pedido";
	}

	@PostMapping("/pedido")
	public String salvarPedido(@RequestParam(required = false) String cliente,
							   @RequestParam(required = false) String telefone,
							   @RequestParam(required = false) String cpf,
							   @RequestParam(required = false) String material,
							   @RequestParam(required = false) String medidas,
							   @RequestParam(required = false) String descricao,
							   RedirectAttributes ra) {
		String clienteLimpo = limparCampo(cliente);
		String telefoneLimpo = limparCampo(telefone);
		String cpfLimpo = limparCampo(cpf);
		String materialLimpo = limparCampo(material);
		String medidasLimpa = limparCampo(medidas);
		String descricaoLimpa = limparCampo(descricao);

		if (clienteLimpo == null || telefoneLimpo == null || cpfLimpo == null || materialLimpo == null || descricaoLimpa == null) {
			preencherCamposPedido(ra, clienteLimpo, telefoneLimpo, cpfLimpo, materialLimpo, medidasLimpa, descricaoLimpa);
			ra.addFlashAttribute("erro", "Preencha os campos obrigatórios antes de enviar.");
			return "redirect:/pedido";
		}

		if (!isCPFValido(cpfLimpo)) {
			preencherCamposPedido(ra, clienteLimpo, telefoneLimpo, cpfLimpo, materialLimpo, medidasLimpa, descricaoLimpa);
			ra.addFlashAttribute("erroCpf", "CPF inválido!");
			return "redirect:/pedido";
		}

		try {
			service.adicionarPedido(new Pedido(null, clienteLimpo, telefoneLimpo, cpfLimpo, materialLimpo, medidasLimpa, descricaoLimpa, "CLIENTE"));
			ra.addFlashAttribute("sucesso", "Orçamento enviado com sucesso! Em breve entraremos em contato.");
		} catch (Exception ex) {
			preencherCamposPedido(ra, clienteLimpo, telefoneLimpo, cpfLimpo, materialLimpo, medidasLimpa, descricaoLimpa);
			ra.addFlashAttribute("erro", "Não foi possível enviar seu orçamento. Tente novamente.");
		}

		return "redirect:/pedido";
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		List<Pedido> todos = service.listarOrcamentos();
		LocalDate agora = LocalDate.now();
		model.addAttribute("orcamentosMes", todos.stream().filter(p -> p.getDataCriacao() != null && p.getDataCriacao().getMonthValue() == agora.getMonthValue()).count());
		model.addAttribute("orcamentosConcluidos", todos.stream().filter(p -> p.getItens() != null && !p.getItens().isEmpty()).count());
		model.addAttribute("atividades", service.listarAtividades());
		return "dashboard";
	}

	// ── ORÇAMENTOS ───────────────────────────────────────
	@GetMapping("/orcamentos")
	public String orcamentos(Model model) {
		var orcamentos = service.organizarOrcamentos();
		model.addAttribute("orcamentosMeus", orcamentos.meusPedidos());
		model.addAttribute("orcamentosClientesComOrcamento", orcamentos.clientesComOrcamento());
		model.addAttribute("orcamentosClientesPendentes", orcamentos.clientesPendentes());
		return "orcamentos";
	}

	@PostMapping("/orcamentos/salvar")
	public String salvarOrcamento(@RequestParam(required = false) Long pedidoId, @RequestParam String cliente,
								  @RequestParam String telefone, @RequestParam String cpf, @RequestParam String material,
								  @RequestParam(required = false) String medidas, @RequestParam String descricao,
								  @RequestParam(required = false) List<String> itemNome, @RequestParam(required = false) List<String> itemQuantidade,
								  @RequestParam(required = false) List<String> itemValorUnitario, @RequestParam(required = false) String frete,
								  @RequestParam(required = false) String maoObra, @RequestParam(required = false) String observacoesAdmin,
								  RedirectAttributes ra) {
		service.salvarOrcamentoAdmin(pedidoId, cliente, telefone, cpf, material, medidas, descricao, itemNome, itemQuantidade, itemValorUnitario, frete, maoObra, observacoesAdmin);
		ra.addFlashAttribute("orcamentoSalvo", "Orçamento processado!");
		return "redirect:/orcamentos";
	}

	// ── MÍDIA E AGENDA ───────────────────────────────────
	@GetMapping("/midia")
	public String midia() { return "midia"; }

	@GetMapping("/agenda")
	public String agenda(Model model) {
		return "agenda";
	}

	// Novos métodos para a API da Agenda (JSON)
	@GetMapping("/agenda/dados")
	@ResponseBody
	public List<Atividade> listarDadosAgenda() {
		return service.listarAtividades();
	}

	@PostMapping("/agenda")
	@ResponseBody
	public String salvarAgendamento(@RequestBody Atividade atividade) {
		// Certifique-se de que no seu service existe o método salvarAtividade
		service.salvarAtividade(atividade);
		return "Sucesso";
	}

	// ── USUÁRIOS ─────────────────────────────────────────
	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", service.listarUsuarios());
		if (!model.containsAttribute("erro")) {
			model.addAttribute("erro", null);
		}
		if (!model.containsAttribute("nomePreenchido")) {
			model.addAttribute("nomePreenchido", null);
			model.addAttribute("cargoPreenchido", null);
			model.addAttribute("dataNascimentoPreenchido", null);
			model.addAttribute("emailPreenchido", null);
		}
		return "usuarios";
	}

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
