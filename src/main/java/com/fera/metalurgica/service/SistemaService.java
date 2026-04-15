package com.fera.metalurgica.service;

import com.fera.metalurgica.model.Produto;
import com.fera.metalurgica.model.Orcamento;
import com.fera.metalurgica.model.Atividade;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SistemaService {

    private List<Produto> produtos = new ArrayList<>();
    private List<Orcamento> orcamentos = new ArrayList<>();
    private List<Atividade> atividades = new ArrayList<>();

    public SistemaService() {
        produtos.add(new Produto(1L, "Mesa de Ferro", "Mesas"));
        produtos.add(new Produto(2L, "Estante de Metal", "Estantes"));
        atividades.add(new Atividade("Aço 2mm está fora de estoque!", "Há 17h"));
        atividades.add(new Atividade("Reunião marcada para amanhã às 9:00.", "Ontem"));
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
        // O índice 0 faz a atividade nova aparecer no topo da lista!
        atividades.add(0, a);
    }
}