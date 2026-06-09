package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT12 – Questionário de Orçamento
 * Tipo: Funcional
 * Cenário: Cliente acessa o formulário de orçamento e preenche os dados.
 *          Campos obrigatórios: nome, telefone, cpf, material, descrição.
 *          Campos opcionais: medidas, anexo.
 */
@DisplayName("CT12 - Questionário de Orçamento")
public class CT12_QuestionarioOrcamentoTest extends BaseTest {

    @Test
    @DisplayName("CT12.1 - Página /pedido carrega corretamente sem login")
    void paginaPedidoCarrega() {
        abrirPagina("/pedido");

        assertTrue(driver.getCurrentUrl().contains("/pedido"),
                "Página de pedido deve ser acessível sem login");
        assertTrue(elementoPresente(By.cssSelector("form")),
                "Formulário de pedido deve estar presente");
    }

    @Test
    @DisplayName("CT12.2 - Acesso via botão 'Criar Orçamento' no menu")
    void acessoViaMenuCriarOrcamento() {
        abrirPagina("/");

        aguardarElemento(By.cssSelector("a[href='/pedido']")).click();

        assertTrue(driver.getCurrentUrl().contains("/pedido"),
                "Clicar em 'Criar Orçamento' deve navegar para /pedido");
    }

    @Test
    @DisplayName("CT12.3 - Acesso via botão 'Faça seu orçamento' na seção CTA")
    void acessoViaSecaoCTA() {
        abrirPagina("/");

        WebElement btnCTA = aguardarElemento(By.cssSelector("a[href='/pedido'].btn-white-solid"));
        clicarJS(btnCTA);

        assertTrue(driver.getCurrentUrl().contains("/pedido"),
                "Botão 'Faça seu orçamento' deve navegar para /pedido");
    }

    @Test
    @DisplayName("CT12.4 - Todos os campos obrigatórios estão presentes")
    void camposObrigatoriosPresentes() {
        abrirPagina("/pedido");

        // Campos obrigatórios por RF-12
        assertTrue(elementoPresente(By.cssSelector("input[name='cliente']")),  "Campo nome");
        assertTrue(elementoPresente(By.cssSelector("input[name='telefone']")),  "Campo telefone");
        assertTrue(elementoPresente(By.cssSelector("input[name='cpf']")),       "Campo CPF");
        assertTrue(elementoPresente(By.cssSelector("input[name='material']")),  "Campo material");
        assertTrue(elementoPresente(By.cssSelector("textarea[name='descricao']")), "Campo descrição");
    }

    @Test
    @DisplayName("CT12.5 - Campos opcionais (medidas e arquivo) estão presentes")
    void camposOpcionaisPresentes() {
        abrirPagina("/pedido");

        assertTrue(elementoPresente(By.cssSelector("input[name='medidas']")),
                "Campo Medidas deve estar presente (opcional)");
        assertTrue(elementoPresente(By.cssSelector("input[name='arquivo']")),
                "Campo Anexar Referência deve estar presente (opcional)");
    }

    @Test
    @DisplayName("CT12.6 - Preenchimento de formulário com dados válidos")
    void preenchimentoComDadosValidos() {
        abrirPagina("/pedido");

        driver.findElement(By.cssSelector("input[name='cliente']")).sendKeys("Carlos da Silva");
        driver.findElement(By.id("telefone")).sendKeys("(41) 98888-7777");
        driver.findElement(By.id("cpf")).sendKeys("529.982.247-25"); // CPF matematicamente válido
        driver.findElement(By.cssSelector("input[name='material']")).sendKeys("Aço Inox");
        driver.findElement(By.cssSelector("input[name='medidas']")).sendKeys("1,5m x 0,8m");
        driver.findElement(By.cssSelector("textarea[name='descricao']")).sendKeys("Suporte para equipamentos de cozinha industrial");

        // Verifica que os campos mantêm os valores
        assertEquals("Carlos da Silva",
                driver.findElement(By.cssSelector("input[name='cliente']")).getAttribute("value"));
        assertEquals("Aço Inox",
                driver.findElement(By.cssSelector("input[name='material']")).getAttribute("value"));
    }

    @Test
    @DisplayName("CT12.7 - Submissão com CPF inválido exibe erro")
    void submissaoComCPFInvalido() {
        abrirPagina("/pedido");

        driver.findElement(By.cssSelector("input[name='cliente']")).sendKeys("Teste CPF Invalido");
        driver.findElement(By.id("telefone")).sendKeys("(41) 99999-0000");
        driver.findElement(By.id("cpf")).sendKeys("111.111.111-11"); // CPF inválido
        driver.findElement(By.cssSelector("input[name='material']")).sendKeys("Ferro");
        driver.findElement(By.cssSelector("textarea[name='descricao']")).sendKeys("Teste de CPF inválido");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Página deve retornar /pedido com mensagem de erro ou o elemento de erro deve aparecer
        String urlAtual = driver.getCurrentUrl();
        boolean erroExibido = elementoPresente(By.id("cpf-error")) &&
                driver.findElement(By.id("cpf-error")).isDisplayed();
        boolean redirecionouDePedido = urlAtual.contains("/pedido");

        assertTrue(erroExibido || redirecionouDePedido,
                "CPF inválido deve exibir erro ou manter o usuário na página /pedido");
    }

    @Test
    @DisplayName("CT12.8 - Formulário não submete sem campos obrigatórios")
    void formularioNaoSubmeteSemObrigatorios() {
        abrirPagina("/pedido");

        // Tenta submeter vazio
        WebElement btnSubmit = driver.findElement(By.cssSelector("button[type='submit']"));
        clicarJS(btnSubmit);

        // Deve continuar em /pedido
        assertTrue(driver.getCurrentUrl().contains("/pedido"),
                "Formulário não deve ser submetido sem campos obrigatórios");
    }

    @Test
    @DisplayName("CT12.9 - Submissão com dados completos e CPF válido redireciona para home")
    void submissaoCompletaRedirecionaHome() {
        abrirPagina("/pedido");

        driver.findElement(By.cssSelector("input[name='cliente']")).sendKeys("Ana Souza");
        driver.findElement(By.id("telefone")).sendKeys("(41) 97777-6666");
        driver.findElement(By.id("cpf")).sendKeys("529.982.247-25");
        driver.findElement(By.cssSelector("input[name='material']")).sendKeys("Madeira e Ferro");
        driver.findElement(By.cssSelector("textarea[name='descricao']")).sendKeys("Estante para sala de estar com acabamento em madeira");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Após submissão bem-sucedida, o controller redireciona para /
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
        assertEquals(BASE_URL + "/", driver.getCurrentUrl(),
                "Após envio bem-sucedido do pedido, deve redirecionar para a home");
    }
}
