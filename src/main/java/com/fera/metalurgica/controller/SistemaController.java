package com.fera.metalurgica.controller;

import com.fera.metalurgica.dto.*;
import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.model.*;
import com.fera.metalurgica.service.OrcamentoPdfService;
import com.fera.metalurgica.service.SistemaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("produtos", service.listarProdutos());
        return "home";
    }

	@GetMapping("/orcamentos")
    public String orcamentos(Model model) {
        var orcamentos = service.organizarOrcamentos();
        model.addAttribute("orcamentos", service.listarOrcamentos());
        model.addAttribute("orcamentosMeus", orcamentos.meusPedidos());
        model.addAttribute("orcamentosClientesComOrcamento", orcamentos.clientesComOrcamento());
        model.addAttribute("orcamentosClientesPendentes", orcamentos.clientesPendentes());
        return "orcamentos";
    }

	@GetMapping("/pedido")
	public String pedidoForm(Model model) {
		model.addAttribute("pedidoDTO", new PedidoDTO()); // inicializa vazio
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

    @PostMapping("/pedido")
    public String salvarPedido(@Valid @ModelAttribute("pedidoDTO") PedidoDTO dto,
                               BindingResult result,
                               Model model) {

		if (result.hasErrors() || !isCPFValido(dto.getCpf())) {
			if (!isCPFValido(dto.getCpf())) {
				model.addAttribute("erroCpf", "CPF Inválido. Por favor, verifique os números digitados.");
			}
			model.addAttribute("pedidoDTO", dto); // preserva os campos preenchidos
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

        service.adicionarPedido(novoPedido);

        return "redirect:/";
    }

    // Valida CPF com cálculo dos dígitos verificadores
    private boolean isCPFValido(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0, peso = 10;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - 48) * peso--;
            }
            int r = 11 - (soma % 11);
            char dig10 = (r == 10 || r == 11) ? '0' : (char) (r + 48);

            soma = 0; peso = 11;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - 48) * peso--;
            }
            r = 11 - (soma % 11);
            char dig11 = (r == 10 || r == 11) ? '0' : (char) (r + 48);

            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        } catch (InputMismatchException e) {
            return false;
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
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

    @GetMapping("/midia")
    public String midia(Model model) {
        return "midia";
    }

    @GetMapping("/midia/{slug}")
    public String midiaCategoria(@PathVariable String slug, Model model) {
        model.addAttribute("categoria", new com.fera.metalurgica.model.Categoria(
                slug, "Descrição da categoria " + slug, "5MB · faz 2 dias"));
        return "midia-categoria";
    }

    @GetMapping("/agenda")
    public String agenda() {
        return "agenda";
    }

    @GetMapping("/agenda/dados")
    @ResponseBody
    public List<Atividade> listarAgenda() {
        return service.listarAtividades();
    }

    @PostMapping("/agenda")
    @ResponseBody
    public Atividade salvarAgenda(@RequestBody Atividade atividade) {
        service.adicionarAtividade(atividade);
        return atividade;
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
		}


		return "redirect:/usuarios";
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("produtos", service.listarProdutos());
        return "catalogo";
    }

    @GetMapping("/catalogo/adega")
    public String adega(Model model) {
    return "ambientes/adega";
    }

    @GetMapping("/catalogo/banheiro")
    public String banheiro(Model model) {
        return "ambientes/banheiro";
    }

	@GetMapping("/catalogo/ambiente/{slug}")
	public String catalogoAmbiente(@PathVariable String slug, Model model) {
		var categoria = service.listarCategorias().stream().filter(c -> c.getNome().equalsIgnoreCase(slug)).findFirst().orElse(null);
		if (categoria == null) return "redirect:/catalogo";
		model.addAttribute("categoria", categoria);
		model.addAttribute("midias", service.listarMidiasPorCategoria(categoria.getId()));
		return "catalogo-ambiente";
	}

    @GetMapping("/catalogo/closet")
    public String closet(Model model) {
        return "ambientes/closet";
    }

    @GetMapping("/catalogo/cozinha")
    public String cozinha(Model model) {
        return "ambientes/cozinha";
    }

	@PostMapping("/orcamentos/salvar")
	public String salvarOrcamento(@ModelAttribute OrcamentoAdminDTO dto,
	                              RedirectAttributes redirectAttributes) {

		service.salvarOrcamentoAdmin(dto);

		redirectAttributes.addFlashAttribute("orcamentoSalvo", "Orçamento salvo com sucesso.");
		return "redirect:/orcamentos";
	}
}
