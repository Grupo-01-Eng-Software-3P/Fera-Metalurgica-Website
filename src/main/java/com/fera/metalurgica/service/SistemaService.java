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

        // =========================
        // PRODUTOS (mock)
        // =========================
        produtos.add(new Produto(1L, "Mesa de Ferro", "Mesas"));
        produtos.add(new Produto(2L, "Estante de Metal", "Estantes"));

        // =========================
        // AGENDA (ATIVIDADES - COM DATA REAL)
        // =========================
        atividades.add(new Atividade(
                "Entrega de móvel",
                "Instalação completa no cliente",
                "Casa do Cliente",
                "2026-05-14",
                "08:00 - 09:30"
        ));

        atividades.add(new Atividade(
                "Reunião equipe",
                "Planejamento semanal da produção",
                "Escritório",
                "2026-05-14",
                "09:00 - 10:00"
        ));

        atividades.add(new Atividade(
                "Visita técnica",
                "Medir espaço para projeto novo",
                "Cliente externo",
                "2026-05-15",
                "14:00 - 15:00"
        ));

        // =========================
        // USUÁRIO ADMIN PADRÃO
        // =========================
        Usuario adminMestre = new Usuario();
        adminMestre.setId(proximoIdUsuario++);
        adminMestre.setNome("Lucas Stibbe");
        adminMestre.setCargo("Administrador");
        adminMestre.setDataNascimento("2007-01-19");
        adminMestre.setEmail("admin@fera.com");
        adminMestre.setSenha("1234");

        usuarios.add(adminMestre);
    }

    // =========================
    // USUÁRIOS
    // =========================
    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public void adicionarUsuario(Usuario u) {
        usuarios.add(u);
    }

    // =========================
    // PRODUTOS
    // =========================
    public List<Produto> listarProdutos() {
        return produtos;
    }

    // =========================
    // ORÇAMENTOS
    // =========================
    public List<Orcamento> listarOrcamentos() {
        return orcamentos;
    }

    public void adicionarOrcamento(Orcamento o) {
        orcamentos.add(o);
    }

    // =========================
    // AGENDA (ATIVIDADES)
    // =========================
    public List<Atividade> listarAtividades() {
        return atividades;
    }

    public Atividade adicionarAtividade(Atividade a) {
        atividades.add(0, a);
        return a;
    }

    // (OPCIONAL) filtrar por data se quiser evoluir depois
    public List<Atividade> listarPorData(String data) {
        List<Atividade> filtradas = new ArrayList<>();

        for (Atividade a : atividades) {
            if (a.getData() != null && a.getData().equals(data)) {
                filtradas.add(a);
            }
        }
        return filtradas;
    }

    // =========================
    // LOGIN ADMIN
    // =========================
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