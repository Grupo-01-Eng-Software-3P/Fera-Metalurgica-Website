package com.fera.metalurgica.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ZApiService {

    private static final String INSTANCE_ID = "3F44D0B81CC1B1E17D5BFE743DE93AC5";
    private static final String TOKEN = "FC292ECA4C7B66FF7C0D2679";
    private static final String CLIENT_TOKEN = "Fe5dcb05195e64af1bc9eeb80200053a3S";
    private static final String NUMERO_ADMIN = "120363425141616950-group";
    private static final String URL = "https://api.z-api.io/instances/" + INSTANCE_ID + "/token/" + TOKEN + "/send-text";

    public void enviarNotificacaoOrcamento(String nomeCliente, String telefoneCliente, String descricao) {
        try {
            String numeroCliente = "55" + telefoneCliente.replaceAll("[^0-9]", "");

            // Mensagem para o CLIENTE
            String mensagemCliente = "Olá, *" + nomeCliente + "*! 🎉\n\n"
                    + "Recebemos sua solicitação de orçamento na *Fera Metalúrgica*!\n\n"
                    + "📋 *Descrição:* " + descricao + "\n\n"
                    + "Em breve nossa equipe entrará em contato. Obrigado!";

            enviar(numeroCliente, mensagemCliente);

            // Mensagem para o ADMINISTRADOR
            String mensagemAdmin = "🔔 *Novo Orçamento Recebido!*\n\n"
                    + "👤 *Cliente:* " + nomeCliente + "\n"
                    + "📱 *Telefone:* " + telefoneCliente + "\n"
                    + "📋 *Descrição:* " + descricao + "\n\n"
                    + "Acesse o painel para visualizar o pedido completo.";

            enviar(NUMERO_ADMIN, mensagemAdmin);

        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação Z-API: " + e.getMessage());
        }
    }

    private void enviar(String numero, String mensagem) throws Exception {
        String body = "{\"phone\":\"" + numero + "\",\"message\":\"" + mensagem + "\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("Client-Token", CLIENT_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Z-API response: " + response.body());
    }
}