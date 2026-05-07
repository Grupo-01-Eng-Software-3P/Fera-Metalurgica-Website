package com.fera.metalurgica.controller;

import com.fera.metalurgica.model.Atividade;
import com.fera.metalurgica.model.Orcamento;
import com.fera.metalurgica.model.Usuario;
import com.fera.metalurgica.service.SistemaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("orcamentos", service.listarOrcamentos());
        model.addAttribute("atividades", service.listarAtividades());
        return "dashboard";
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
}