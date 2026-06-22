package com.fera.metalurgica.service;

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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SistemaServiceFavoritosTest {

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
	}

	@Test
	void deveAlternarEstadoFavoritoDaMidia() {
		Midia midia = new Midia();
		midia.setFavorita(false);

		when(midiaRepository.findById(1L)).thenReturn(Optional.of(midia));
		when(midiaRepository.save(any(Midia.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Midia atualizada = service.alternarFavoritaMidia(1L);

		assertTrue(atualizada.isFavorita());
		assertTrue(midia.isFavorita());
	}

	@Test
	void deveListarMidiasFavoritasDaRepository() {
		Midia midia = new Midia();
		midia.setFavorita(true);
		when(midiaRepository.findByFavoritaTrueOrderByDataUploadDesc()).thenReturn(List.of(midia));

		List<Midia> midias = service.listarMidiasFavoritas();

		assertEquals(1, midias.size());
		assertFalse(midias.isEmpty());
		assertTrue(midias.get(0).isFavorita());
	}
}
