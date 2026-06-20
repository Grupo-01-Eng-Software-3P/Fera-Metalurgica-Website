package com.fera.metalurgica.service;

import com.fera.metalurgica.model.Pedido;
import com.fera.metalurgica.repository.AtividadeRepository;
import com.fera.metalurgica.repository.CategoriaRepository;
import com.fera.metalurgica.repository.MidiaRepository;
import com.fera.metalurgica.repository.PedidoRepository;
import com.fera.metalurgica.repository.ProdutoRepository;
import com.fera.metalurgica.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SistemaServiceAnexoTest {

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
	private Path tempUploadDir;

	@BeforeEach
	void setUp() throws Exception {
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
		tempUploadDir = Files.createTempDirectory("pedido-anexo-test");
		ReflectionTestUtils.setField(service, "uploadDir", tempUploadDir.toString());
	}

	@AfterEach
	void tearDown() throws Exception {
		if (tempUploadDir == null || !Files.exists(tempUploadDir)) {
			return;
		}

		try (var caminhos = Files.walk(tempUploadDir)) {
			caminhos.sorted(java.util.Comparator.reverseOrder())
				.forEach(caminho -> {
					try {
						Files.deleteIfExists(caminho);
					} catch (Exception ignored) {
						// Intencionalmente vazio.
					}
				});
		}
	}

	@Test
	void deveSalvarAnexoDoPedidoERegistrarMetadados() throws Exception {
		when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Pedido pedido = new Pedido();
		pedido.setCliente("Cliente Teste");
		pedido.setTelefone("(45) 99999-9999");
		pedido.setCpf("123.456.789-09");
		pedido.setMaterial("Ferro");
		pedido.setDescricao("Pedido com anexo");

		MockMultipartFile arquivo = new MockMultipartFile(
			"arquivo",
			"referencia.png",
			"image/png",
			new byte[] {1, 2, 3, 4}
		);

		service.adicionarPedido(pedido, arquivo);

		ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
		verify(pedidoRepository).save(pedidoCaptor.capture());

		Pedido salvo = pedidoCaptor.getValue();
		assertNotNull(salvo.getAnexoCaminho());
		assertTrue(salvo.getAnexoCaminho().startsWith("/imagens/pedidos/"));
		assertEquals("referencia.png", salvo.getAnexoNomeOriginal());
		assertEquals("image/png", salvo.getAnexoTipo());

		String nomeArquivo = salvo.getAnexoCaminho().substring("/imagens/pedidos/".length());
		Path arquivoSalvo = tempUploadDir.resolve("pedidos").resolve(nomeArquivo);
		assertTrue(Files.exists(arquivoSalvo));
		assertArrayEquals(new byte[] {1, 2, 3, 4}, Files.readAllBytes(arquivoSalvo));
	}
}
