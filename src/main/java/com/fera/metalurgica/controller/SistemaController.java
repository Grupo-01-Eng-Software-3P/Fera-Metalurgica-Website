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
                                @RequestParam String dataNascimento) {

        // --- LÓGICA DE ID SEQUENCIAL ---
        List<Usuario> usuariosAtuais = service.listarUsuarios();
        int proximoNumero = 1; // Caso a lista esteja vazia, começa no 1

        if (!usuariosAtuais.isEmpty()) {
            // Pega o último usuário cadastrado na lista
            Usuario ultimoUsuario = usuariosAtuais.get(usuariosAtuais.size() - 1);

            // Pega a string do ID (ex: "#08"), tira o "#" e transforma em número inteiro (8)
            int ultimoNumero = Integer.parseInt(ultimoUsuario.getId().replace("#", ""));

            // Soma 1 para o novo usuário
            proximoNumero = ultimoNumero + 1;
        }

        // Monta a string de volta com a hashtag e zero à esquerda (ex: #09, #10, #11)
        String idGerado = String.format("#%02d", proximoNumero);
        // ---------------------------------

        // Pega a data de hoje para o cadastro
        String dataHoje = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Formata a data de nascimento (YYYY-MM-DD para DD/MM/YYYY)
        String[] partesData = dataNascimento.split("-");
        String dataNascFormatada = partesData.length == 3 ? partesData[2] + "/" + partesData[1] + "/" + partesData[0] : dataNascimento;

        // Salva o usuário com o novo ID sequencial
        service.adicionarUsuario(new Usuario(idGerado, nome, cargo, dataHoje, dataNascFormatada));

        return "redirect:/usuarios";
    }

    @GetMapping("/agenda")
    public String agenda() {
        return "agenda";
    }
}