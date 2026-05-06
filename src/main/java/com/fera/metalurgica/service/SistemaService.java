package com.fera.metalurgica.service;

import com.fera.metalurgica.model.Produto;
import com.fera.metalurgica.model.Orcamento;
import com.fera.metalurgica.model.Atividade;
import com.fera.metalurgica.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SistemaService {

    private List<Produto> produtos = new ArrayList<>();
    private List<Orcamento> orcamentos = new ArrayList<>();
    private List<Atividade> atividades = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();

    private Long proximoIdUsuario = 1L;

    public SistemaService() {
        produtos.add(new Produto(1L, "Mesa de Ferro", "Mesas"));
        produtos.add(new Produto(2L, "Estante de Metal", "Estantes"));
        atividades.add(new Atividade("Aço 2mm está fora de estoque!", "Há 17h"));
        atividades.add(new Atividade("Reunião marcada para amanhã às 9:00.", "Ontem"));

        Usuario adminMestre = new Usuario();
        adminMestre.setId(proximoIdUsuario++);
        adminMestre.setNome("Lucas Stibbe");
        adminMestre.setCargo("Administrador");
        adminMestre.setDataNascimento("2007-01-19");
        adminMestre.setEmail("admin@fera.com");
        adminMestre.setSenha("1234");

        usuarios.add(adminMestre);
    }

    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public void adicionarUsuario(Usuario u) {
        usuarios.add(u);
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

    public boolean autenticarAdmin(String email, String senha) {
        for (Usuario u : usuarios) {
            if (email != null && email.equals(u.getEmail()) && senha != null && senha.equals(u.getSenha())) {
                return true;
            }
        }
        return false;
    }
}