package com.fera.metalurgica.service;

import com.fera.metalurgica.exception.BusinessException;
import com.fera.metalurgica.exception.ResourceNotFoundException;
import com.fera.metalurgica.model.Atividade;
import com.fera.metalurgica.model.Orcamento;
import com.fera.metalurgica.model.Produto;
import com.fera.metalurgica.model.Usuario;
import com.fera.metalurgica.repository.AtividadeRepository;
import com.fera.metalurgica.repository.OrcamentoRepository;
import com.fera.metalurgica.repository.ProdutoRepository;
import com.fera.metalurgica.repository.UsuarioRepository;
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
            atividadeRepository.save(new Atividade(
                    "Alerta de Estoque", "Aço 2mm está fora de estoque!", "ALERTA",
                    LocalDate.now(), "Há 17h"
            ));
            atividadeRepository.save(new Atividade(
                    "Reunião", "Reunião marcada para amanhã às 9:00.", "REUNIAO",
                    LocalDate.now().plusDays(1), "Ontem"
            ));
        }
    }

    @PostConstruct
    public void criarProdutosIniciais() {
        if (produtoRepository.count() == 0) {
            produtoRepository.save(new Produto(null, "Mesa de Ferro", "Mesas"));
            produtoRepository.save(new Produto(null, "Estante de Metal", "Estantes"));
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
            admin.setSenha(passwordEncoder.encode("1234"));
            usuarioRepository.save(admin);
        }
    }

	// USUARIO

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public void adicionarUsuario(Usuario usuario) {

		if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
			throw new BusinessException("E-mail já cadastrado: " + usuario.getEmail());
		}
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
    }

	public Usuario buscarUsuarioPorId(Long id) {
		return usuarioRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
	}


	// PRODUTO

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    public void adicionarProduto(Produto produto) {

		if (produto.getNome() == null || produto.getNome().isBlank()) {
			throw new BusinessException("O nome do produto não pode ser vazio.");
		}
		produtoRepository.save(produto);
    }

	public Produto buscarProdutoPorId(Long id) {
		return produtoRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
	}


	// ORCAMENTO

    public List<Orcamento> listarOrcamentos() {
        return orcamentoRepository.findAll();
    }

    public void adicionarOrcamento(Orcamento orcamento) {

		if (orcamento.getItens() == null || orcamento.getItens().isEmpty()) {
			throw new BusinessException("O orçamento deve ter pelo menos um item.");
		}
		for (var item : orcamento.getItens()) {
			item.setOrcamento(orcamento);
		}

        orcamento.calcularTotais();
        orcamentoRepository.save(orcamento);
    }

	public Orcamento buscarOrcamentoPorId(Long id) {
		return orcamentoRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado com id: " + id));
	}


	// ATIVIDADE

    public List<Atividade> listarAtividades() {
        return atividadeRepository.findAllByOrderByIdDesc();
    }

    public Atividade adicionarAtividade(Atividade atividade) {

        if (atividade.getTitulo() == null || atividade.getTitulo().isBlank()) {
			throw new BusinessException("O título da atividade não pode ser vazio.");
		}
		return atividadeRepository.save(atividade);
    }

    public List<Atividade> listarPorData(LocalDate data) {
		List<Atividade> resultado = atividadeRepository.findAll().stream()
			.filter(a -> data.equals(a.getData()))
			.toList();

		if (resultado.isEmpty()) {
			throw new ResourceNotFoundException("Nenhuma atividade encontrada para a data: " + data);
		}

		return resultado;
	}
}
