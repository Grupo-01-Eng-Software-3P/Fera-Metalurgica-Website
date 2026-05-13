package com.fera.metalurgica.controller;

import com.fera.metalurgica.model.Atividade;
import com.fera.metalurgica.model.Orcamento;
import com.fera.metalurgica.model.Usuario;
import com.fera.metalurgica.service.SistemaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.InputMismatchException;

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
        // Se vier algum erro no redirecionamento, a tela vai mostrar
        return "orcamento"; // Reaproveitando o formulário de pedido do cliente
    }

    @PostMapping("/pedido")
    public String salvarPedido(@RequestParam String cliente,
                                  @RequestParam String telefone,
                                  @RequestParam String cpf,
                                  @RequestParam String material,
                                  @RequestParam String medidas,
                                  @RequestParam String descricao,
                                  Model model) {

        if (!isCPFValido(cpf)) {
            model.addAttribute("erroCpf", "CPF Inválido. Por favor, verifique os números digitados.");

            // Para não perder os dados que o cliente já digitou ao dar erro
            model.addAttribute("clientePreenchido", cliente);
            model.addAttribute("telefonePreenchido", telefone);
            model.addAttribute("cpfPreenchido", cpf);
            model.addAttribute("materialPreenchido", material); // NOVO
            model.addAttribute("medidasPreenchido", medidas);
            model.addAttribute("descricaoPreenchido", descricao);

            return "orcamento";
        }

        // Cria o pedido definindo "CLIENTE" como criador
        service.adicionarOrcamento(
                new Orcamento((long)(Math.random()*1000), cliente, telefone, cpf, material, medidas, descricao, "CLIENTE")
        );

        return "redirect:/";
    }

    // Função auxiliar para validar CPF
    private boolean isCPFValido(String cpf) {
        // Remove tudo que não for número (pontos e traços)
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) return false;

        // Verifica se todos os números são iguais (ex: 111.111.111-11 é inválido mas passaria na conta)
        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            // Calculo do 1o Dígito Verificador
            int soma = 0, peso = 10;
            for (int i = 0; i < 9; i++) {
                int num = (int) (cpf.charAt(i) - 48);
                soma += (num * peso);
                peso--;
            }
            int r = 11 - (soma % 11);
            char dig10 = (r == 10 || r == 11) ? '0' : (char) (r + 48);

            // Calculo do 2o Dígito Verificador
            soma = 0; peso = 11;
            for (int i = 0; i < 10; i++) {
                int num = (int) (cpf.charAt(i) - 48);
                soma += (num * peso);
                peso--;
            }
            r = 11 - (soma % 11);
            char dig11 = (r == 10 || r == 11) ? '0' : (char) (r + 48);

            // Confere se os dígitos calculados batem com os que foram digitados
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

    @PostMapping("/nova-atividade")
    public String salvarAtividade(@RequestParam String descricao) {
        service.adicionarAtividade(new Atividade(descricao, "Agora"));
        return "redirect:/dashboard";
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

        Usuario usuario = new Usuario(
                null, nome, cargo, dataConvertida, email, senha);

        service.adicionarUsuario(usuario);

        return "redirect:/usuarios";
    }

    @GetMapping("/agenda")
    public String agenda() {
        return "agenda";
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
}