package com.fera.metalurgica.controller;

import com.fera.metalurgica.model.Atividade;
import com.fera.metalurgica.model.Pedido;
import com.fera.metalurgica.model.Usuario;
import com.fera.metalurgica.service.SistemaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;

@Controller
public class SistemaController {

    private final SistemaService service;

    public SistemaController(SistemaService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("produtos", service.listarProdutos());
        return "home";
    }

	    @GetMapping("/orcamentos")
    public String orcamentos() {
        return "orcamentos";
    }

    @GetMapping("/pedido")
    public String pedidoForm(Model model) {
        return "pedido";
    }

    @PostMapping("/pedido")
    public String salvarPedido(@RequestParam String cliente,
                               @RequestParam String telefone,
                               @RequestParam String cpf,
                               @RequestParam String material,
                               @RequestParam(required = false) String medidas,
                               @RequestParam String descricao,
                               Model model) {

        if (!isCPFValido(cpf)) {
            model.addAttribute("erroCpf", "CPF Inválido. Por favor, verifique os números digitados.");

            // Preserva os dados preenchidos em caso de erro
            model.addAttribute("clientePreenchido", cliente);
            model.addAttribute("telefonePreenchido", telefone);
            model.addAttribute("cpfPreenchido", cpf);
            model.addAttribute("materialPreenchido", material);
            model.addAttribute("medidasPreenchido", medidas);
            model.addAttribute("descricaoPreenchido", descricao);

            return "pedido";
        }

        Pedido novoPedido = new Pedido();
        novoPedido.setCliente(cliente);
        novoPedido.setTelefone(telefone);
        novoPedido.setCpf(cpf);
        novoPedido.setMaterial(material);
        novoPedido.setMedidas(medidas);
        novoPedido.setDescricao(descricao);

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
        model.addAttribute("orcamentos", service.listarOrcamentos());
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

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", service.listarUsuarios());
        return "usuarios";
    }

    @PostMapping("/novo-usuario")
    public String salvarUsuario(@RequestParam String nome,
                                @RequestParam String cargo,
                                @RequestParam String dataNascimento,
                                @RequestParam String email,
                                @RequestParam String senha) {

        LocalDate dataConvertida = LocalDate.parse(dataNascimento);

        Usuario usuario = new Usuario(null, nome, cargo, dataConvertida, email, senha);
        service.adicionarUsuario(usuario);

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

     @GetMapping("/catalogo/biblioteca")
    public String biblioteca(Model model) {
    return "ambientes/biblioteca";
    }
}
