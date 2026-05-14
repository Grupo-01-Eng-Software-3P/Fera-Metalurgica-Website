package com.fera.metalurgica.service;

import com.fera.metalurgica.model.Produto;
import com.fera.metalurgica.model.Orcamento;
import com.fera.metalurgica.model.Atividade;
import com.fera.metalurgica.model.Usuario;

import com.fera.metalurgica.repository.*;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SistemaService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private OrcamentoRepository orcamentoRepository;
    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void criarAtividadesIniciais() {

        if (atividadeRepository.count() == 0) {
            atividadeRepository.save(
                    new Atividade(
                            "Aço 2mm está fora de estoque!",
                            "Há 17h"
                    )
            );
            atividadeRepository.save(
                    new Atividade(
                            "Reunião marcada para amanhã às 9:00.",
                            "Ontem"
                    )
            );
        }

    }

    @PostConstruct
    public void criarProdutosIniciais() {

        if (produtoRepository.count() == 0) {
            produtoRepository.save(
                    new Produto(
                            null,
                            "Mesa de Ferro",
                            "Mesas"
                    )
            );
            produtoRepository.save(
                    new Produto(
                            null,
                            "Estante de Metal",
                            "Estantes"
                    )
            );
        }
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
        return produtoRepository.findAll();
    }

    public List<Orcamento> listarOrcamentos() {
        return orcamentoRepository.findAll();
    }

    public List<Atividade> listarAtividades() {

        return atividadeRepository.findAllByOrderByIdDesc();
    }

    public void adicionarProduto(Produto produto) {
        produtoRepository.save(produto);
    }

    public void adicionarOrcamento(Orcamento orcamento) {
        if (orcamento.getItens() != null) {

            for (var item : orcamento.getItens()) {

                item.setOrcamento(orcamento);
            }
        }
        orcamento.calcularTotais();
        orcamentoRepository.save(orcamento);
    }

    public void adicionarAtividade(Atividade atividade) {
        atividadeRepository.save(atividade);
    }
}