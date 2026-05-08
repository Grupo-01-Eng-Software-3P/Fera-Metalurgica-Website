package com.fera.metalurgica.controller;

import com.fera.metalurgica.model.Atividade;
import com.fera.metalurgica.model.Orcamento;
import com.fera.metalurgica.model.Usuario;
import com.fera.metalurgica.service.SistemaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/orcamento")
    public String orcamentoForm() {
        return "orcamento";
    }

    @PostMapping("/orcamento")
    public String salvarOrcamento(@RequestParam String cliente,
                                  @RequestParam String material,
                                  @RequestParam String medidas,
                                  @RequestParam String descricao) {

        service.adicionarOrcamento(
                new Orcamento((long)(Math.random()*1000), cliente, material, medidas, descricao)
        );

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String fazerLogin(@RequestParam String email,
                             @RequestParam String senha,
                             Model model) {

        boolean autorizado = service.autenticarAdmin(email, senha);

        if (autorizado) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("erro", "E-mail ou senha incorretos. Tente novamente.");
            return "login";
        }
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
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String senha) {

        List<Usuario> usuariosAtuais = service.listarUsuarios();
        Long proximoNumero = 1L;

        if (!usuariosAtuais.isEmpty()) {
            Usuario ultimoUsuario = usuariosAtuais.get(usuariosAtuais.size() - 1);
            proximoNumero = ultimoUsuario.getId() + 1L;
        }

        String dataHoje = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        String[] partesData = dataNascimento.split("-");
        String dataNascFormatada = partesData.length == 3 ? partesData[2] + "/" + partesData[1] + "/" + partesData[0] : dataNascimento;

        // Salva o usuário com o novo ID Long, e os novos campos email e senha
        service.adicionarUsuario(new Usuario(proximoNumero, nome, cargo, dataHoje, dataNascFormatada, email, senha));

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
}