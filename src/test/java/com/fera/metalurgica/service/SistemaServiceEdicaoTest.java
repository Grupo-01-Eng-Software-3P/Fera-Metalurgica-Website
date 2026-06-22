package com.fera.metalurgica.service;

import com.fera.metalurgica.model.Categoria;
import com.fera.metalurgica.model.Midia;
import com.fera.metalurgica.repository.AtividadeRepository;
import com.fera.metalurgica.repository.CategoriaRepository;
import com.fera.metalurgica.repository.MidiaRepository;
import com.fera.metalurgica.repository.PedidoRepository;
import com.fera.metalurgica.repository.ProdutoRepository;
import com.fera.metalurgica.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SistemaServiceEdicaoTest {

	@Mock
	private ProdutoRepository produtoRepository;
	@Mock
	private PedidoRepository pedidoRepository;
	@Mock
	private UsuarioRepository usuarioRepository;
	@Mock
	private CategoriaRepository categoriaRepository;
	@Mock
	private MidiaRepository midiaRepository;
	@Mock
	private AtividadeRepository atividadeRepository;
	@Mock
	private ZApiService zApiService;
	@Mock
	private PasswordEncoder passwordEncoder;

	@TempDir
	Path tempDir;

	private SistemaService service;

	@BeforeEach
	void setUp() {
		service = new SistemaService(
			produtoRepository,
			pedidoRepository,
			usuarioRepository,
			categoriaRepository,
			midiaRepository,
			atividadeRepository,
			zApiService,
			passwordEncoder
		);
		ReflectionTestUtils.setField(service, "uploadDir", tempDir.toString());
	}

	@Test
	void deveAtualizarCategoriaERegerarSlug() {
		Categoria categoria = new Categoria();
		categoria.setId(1L);
		categoria.setNome("Antiga Categoria");
		categoria.setDescricao("Descricao antiga");
		categoria.setSlug("antiga-categoria");

		when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
		when(categoriaRepository.findByNomeIgnoreCase("Nova Categoria")).thenReturn(Optional.empty());
		when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Categoria atualizada = service.atualizarCategoria(1L, " Nova Categoria ", " Nova descricao ");

		assertEquals("Nova Categoria", atualizada.getNome());
		assertEquals("Nova descricao", atualizada.getDescricao());
		assertEquals("nova-categoria", atualizada.getSlug());
	}

	@Test
	void deveAtualizarMidiaMantendoFavoritaQuandoNaoTrocaArquivo() throws Exception {
		Categoria categoria = new Categoria();
		categoria.setId(10L);
		categoria.setSlug("salas");

		Midia midia = new Midia();
		midia.setId(2L);
		midia.setNome("Imagem antiga");
		midia.setDescricao("Descricao antiga");
		midia.setCaminho("/imagens/antiga.png");
		midia.setFavorita(true);
		midia.setCategoria(categoria);

		when(midiaRepository.findById(2L)).thenReturn(Optional.of(midia));
		when(midiaRepository.save(any(Midia.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Midia atualizada = service.atualizarMidia(2L, " Nova imagem ", " Nova descricao ", null);

		assertEquals("Nova imagem", atualizada.getNome());
		assertEquals("Nova descricao", atualizada.getDescricao());
		assertEquals("/imagens/antiga.png", atualizada.getCaminho());
		assertTrue(atualizada.isFavorita());
	}

	@Test
	void deveAtualizarMidiaComNovoArquivo() throws Exception {
		Categoria categoria = new Categoria();
		categoria.setId(10L);
		categoria.setSlug("salas");

		Midia midia = new Midia();
		midia.setId(2L);
		midia.setNome("Imagem antiga");
		midia.setDescricao("Descricao antiga");
		midia.setCaminho("/imagens/antiga.png");
		midia.setFavorita(false);
		midia.setCategoria(categoria);

		when(midiaRepository.findById(2L)).thenReturn(Optional.of(midia));
		when(midiaRepository.save(any(Midia.class))).thenAnswer(invocation -> invocation.getArgument(0));

		MockMultipartFile arquivo = new MockMultipartFile(
			"arquivo",
			"nova-imagem.png",
			"image/png",
			"conteudo".getBytes(StandardCharsets.UTF_8)
		);

		Midia atualizada = service.atualizarMidia(2L, "Imagem nova", "Descricao nova", arquivo);

		assertEquals("Imagem nova", atualizada.getNome());
		assertEquals("Descricao nova", atualizada.getDescricao());
		assertTrue(atualizada.getCaminho().startsWith("/imagens/"));
		assertTrue(Files.exists(tempDir.resolve(atualizada.getCaminho().replace("/imagens/", ""))));
	}
}
