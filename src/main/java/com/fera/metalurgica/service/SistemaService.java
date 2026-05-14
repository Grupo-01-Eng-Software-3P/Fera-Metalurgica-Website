package com.fera.metalurgica.service;

import com.fera.metalurgica.model.Produto;
import com.fera.metalurgica.model.Orcamento;
import com.fera.metalurgica.model.Atividade;
import com.fera.metalurgica.model.Usuario;

import com.fera.metalurgica.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SistemaService {

    private List<Produto> produtos = new ArrayList<>();
    private List<Orcamento> orcamentos = new ArrayList<>();
    private List<Atividade> atividades = new ArrayList<>();

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public SistemaService() {
        produtos.add(new Produto(1L, "Mesa de Ferro", "Mesas"));
        produtos.add(new Produto(2L, "Estante de Metal", "Estantes"));
        atividades.add(new Atividade("Aço 2mm está fora de estoque!", "Há 17h"));
        atividades.add(new Atividade("Reunião marcada para amanhã às 9:00.", "Ontem"));

    }

    @PostConstruct
    public void criarAdmin() {

        if (usuarioRepository.findByEmail("admin@fera.com") == null) {

            Usuario admin = new Usuario();

            admin.setNome("Lucas Stibbe");
            admin.setCargo("Administrador");
            admin.setDataNascimento(LocalDate.of(2007, 1, 19));

            admin.setEmail("admin@fera.com");

            admin.setSenha(
                    passwordEncoder.encode("1234")
            );

            usuarioRepository.save(admin);
        }
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public void adicionarUsuario(Usuario usuario) {

        usuario.setSenha(
                passwordEncoder.encode(
                        usuario.getSenha()
                )
        );

        usuarioRepository.save(usuario);
    }

    public List<Produto> listarProdutos() {
        return produtos;
    }

    public List<Orcamento> listarOrcamentos() {
        return orcamentos;
    }

    public List<Atividade> listarAtividades() {
        return atividades;
    }

    public void adicionarOrcamento(Orcamento o) {
        orcamentos.add(o);
    }

    public void adicionarAtividade(Atividade a) {
        atividades.add(0, a);
    }
}