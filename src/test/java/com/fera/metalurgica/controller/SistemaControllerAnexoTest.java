package com.fera.metalurgica.controller;

import com.fera.metalurgica.model.Pedido;
import com.fera.metalurgica.service.SistemaService;
import com.fera.metalurgica.service.ZApiService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class SistemaControllerAnexoTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SistemaService service;

	@MockBean
	private ZApiService zApiService;

	private Path tempUploadDir;

	@BeforeEach
	void setUp() throws Exception {
		doNothing().when(zApiService).enviarNotificacaoOrcamento(anyString(), anyString(), anyString());
		tempUploadDir = Files.createTempDirectory("pedido-anexo-controller-test");
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
	void deveVisualizarAnexoDoPedidoSemRetornar404() throws Exception {
		Pedido pedido = new Pedido();
		pedido.setCliente("Cliente Teste");
		pedido.setTelefone("(45) 99999-9999");
		pedido.setCpf("123.456.789-09");
		pedido.setMaterial("Ferro");
		pedido.setDescricao("Pedido com anexo");

		byte[] conteudo = new byte[] {9, 8, 7, 6};
		MockMultipartFile arquivo = new MockMultipartFile(
			"arquivo",
			"referencia.png",
			"image/png",
			conteudo
		);

		service.adicionarPedido(pedido, arquivo);

		mockMvc.perform(get("/orcamentos/{id}/anexo", pedido.getId()))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_PNG))
			.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("inline")))
			.andExpect(content().bytes(conteudo));
	}
}
