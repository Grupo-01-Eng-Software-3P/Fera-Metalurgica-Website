package com.fera.metalurgica.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class ZApiService {

	@Value("${zapi.instance-id}")
	private String instanceId;

	@Value("${zapi.token}")
	private String token;

	@Value("${zapi.client-token}")
	private String clientToken;

	@Value("${zapi.numero-admin}")
	private String numeroAdmin;

	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();

	private String getUrl() {
		return "https://api.z-api.io/instances/" + instanceId + "/token/" + token + "/send-text";
	}

    public void enviarNotificacaoOrcamento(String nomeCliente, String telefoneCliente, String descricao) {
        try {
            String numeroCliente = "55" + telefoneCliente.replaceAll("[^0-9]", "");

            // Mensagem para o CLIENTE
            String mensagemCliente = "Olá, *" + nomeCliente + "*! 🎉\n\n"
                    + "Recebemos sua solicitação de orçamento na *Fera Metalúrgica*!\n\n"
                    + "📋 *Descrição:* " + descricao + "\n\n"
                    + "Em breve nossa equipe entrará em contato. Obrigado!";

            // Mensagem para o ADMINISTRADOR
            String mensagemAdmin = "🔔 *Novo Orçamento Recebido!*\n\n"
                    + "👤 *Cliente:* " + nomeCliente + "\n"
                    + "📱 *Telefone:* " + telefoneCliente + "\n"
                    + "📋 *Descrição:* " + descricao + "\n\n"
                    + "Acesse o painel para visualizar o pedido completo.";

			enviar(numeroCliente, mensagemCliente);

			Thread.sleep(1000); //Delay entre envios da API para não ter problemas com rate limit. Poderíamos
									 //usar @Async, mas, como é um uso pequeno, sleep já serve (eu acho ne)

            enviar(numeroAdmin, mensagemAdmin);

        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação Z-API: " + e.getMessage());
        }
    }

	private void enviar(String numero, String mensagem) throws Exception {
		String body = mapper.writeValueAsString(Map.of(
			"phone", numero,
			"message", mensagem
		));

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(getUrl()))
			.header("Content-Type", "application/json")
			.header("Client-Token", clientToken)
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() < 200 || response.statusCode() >= 300) {
			throw new RuntimeException("Erro Z-API [" + response.statusCode() + "]: " + response.body());
		}

		System.out.println("Z-API response: " + response.body());
	}
}
