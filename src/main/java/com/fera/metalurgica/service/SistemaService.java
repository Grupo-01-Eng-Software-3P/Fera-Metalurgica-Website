package com.fera.metalurgica.service;

import com.fera.metalurgica.model.Atividade;
import com.fera.metalurgica.model.Orcamento;
import com.fera.metalurgica.model.Produto;
import com.fera.metalurgica.model.Usuario;
import com.fera.metalurgica.repository.AtividadeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SistemaService {

    private final AtividadeRepository atividadeRepository;

    private List<Produto> produtos = new ArrayList<>();
    private List<Orcamento> orcamentos = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();
    private Long proximoIdUsuario = 1L;

    public SistemaService(AtividadeRepository atividadeRepository) {
        this.atividadeRepository = atividadeRepository;

        produtos.add(new Produto(1L, "Mesa de Ferro", "Mesas"));
        produtos.add(new Produto(2L, "Estante de Metal", "Estantes"));

        Usuario adminMestre = new Usuario();
        adminMestre.setId(proximoIdUsuario++);
        adminMestre.setNome("Lucas Stibbe");
        adminMestre.setCargo("Administrador");
        adminMestre.setDataNascimento("2007-01-19");
        adminMestre.setEmail("admin@fera.com");
        adminMestre.setSenha("1234");
        usuarios.add(adminMestre);
    }

    // AGENDA — agora usa o banco
    public List<Atividade> listarAtividades() {
        return atividadeRepository.findAll();
    }

    public Atividade adicionarAtividade(Atividade a) {
        return atividadeRepository.save(a);
    }

    public List<Atividade> listarPorData(String data) {
        return atividadeRepository.findAll().stream()
                .filter(a -> data.equals(a.getData()))
                .toList();
    }

    // restante igual
    public List<Usuario> listarUsuarios() { return usuarios; }
    public void adicionarUsuario(Usuario u) { usuarios.add(u); }
    public List<Produto> listarProdutos() { return produtos; }
    public List<Orcamento> listarOrcamentos() { return orcamentos; }
    public void adicionarOrcamento(Orcamento o) { orcamentos.add(o); }
    public boolean autenticarAdmin(String email, String senha) {
        for (Usuario u : usuarios) {
            if (email != null && email.equals(u.getEmail())
                    && senha != null && senha.equals(u.getSenha())) {
                return true;
            }
        }
        return false;
    }
}