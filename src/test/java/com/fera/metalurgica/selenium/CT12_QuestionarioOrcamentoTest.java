package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT12 – Questionário de Orçamento
 */
@DisplayName("CT12 - Questionário de Orçamento")
public class CT12_QuestionarioOrcamentoTest extends BaseTest {

    @Test
    @DisplayName("CT12.1 - Página /pedido carrega corretamente sem login")
    void paginaPedidoCarrega() {
        abrirPagina("/pedido");
        assertTrue(driver.getCurrentUrl().contains("/pedido"));
        assertTrue(elementoPresente(By.cssSelector("form")));
    }

    @Test
    @DisplayName("CT12.2 - Acesso via botão 'Criar Orçamento' no menu")
    void acessoViaMenuCriarOrcamento() {
        abrirPagina("/");
        aguardarElemento(By.cssSelector("a[href='/pedido']")).click();
        assertTrue(driver.getCurrentUrl().contains("/pedido"));
    }

    @Test
    @DisplayName("CT12.3 - Acesso via botão 'Faça seu orçamento' na seção CTA")
    void acessoViaSecaoCTA() {
        abrirPagina("/");
        WebElement btnCTA = aguardarElemento(By.cssSelector("a[href='/pedido'].btn-white-solid"));
        clicarJS(btnCTA);
        assertTrue(driver.getCurrentUrl().contains("/pedido"));
    }

    @Test
    @DisplayName("CT12.4 - Todos os campos obrigatórios estão presentes")
    void camposObrigatoriosPresentes() {
        abrirPagina("/pedido");
        assertTrue(elementoPresente(By.cssSelector("input[name='cliente']")));
        assertTrue(elementoPresente(By.cssSelector("input[name='telefone']")));
        assertTrue(elementoPresente(By.cssSelector("input[name='cpf']")));
        assertTrue(elementoPresente(By.cssSelector("input[name='material']")));
        assertTrue(elementoPresente(By.cssSelector("textarea[name='descricao']")));
    }

    @Test
    @DisplayName("CT12.5 - Campos opcionais (medidas e arquivo) estão presentes")
    void camposOpcionaisPresentes() {
        abrirPagina("/pedido");
        assertTrue(elementoPresente(By.cssSelector("input[name='medidas']")));
        assertTrue(elementoPresente(By.cssSelector("input[name='arquivo']")));
    }

    @Test
    @DisplayName("CT12.6 - Preenchimento de formulário com dados válidos")
    void preenchimentoComDadosValidos() {
        abrirPagina("/pedido");
        WebElement inputCliente = driver.findElement(By.cssSelector("input[name='cliente']"));
        inputCliente.sendKeys("Carlos da Silva");
        driver.findElement(By.id("telefone")).sendKeys("(41) 98888-7777");
        driver.findElement(By.id("cpf")).sendKeys("529.982.247-25");
        driver.findElement(By.cssSelector("input[name='material']")).sendKeys("Aço Inox");
        driver.findElement(By.cssSelector("textarea[name='descricao']")).sendKeys("Suporte para equipamentos de cozinha industrial");
        assertEquals("Carlos da Silva", inputCliente.getAttribute("value"));
    }

    @Test
    @DisplayName("CT12.7 - Submissão com CPF inválido exibe mensagem de erro")
    void submissaoComCPFInvalido() {
        abrirPagina("/pedido");

        driver.findElement(By.cssSelector("input[name='cliente']")).sendKeys("Teste CPF Invalido");
        driver.findElement(By.id("telefone")).sendKeys("(41) 99999-0000");

        // Digita CPF inválido e sai do campo para disparar o evento de validação JS
        WebElement campoCPF = driver.findElement(By.id("cpf"));
        campoCPF.sendKeys("111.111.111-11");
        campoCPF.sendKeys("\t"); // Tab para perder o foco

        driver.findElement(By.cssSelector("input[name='material']")).sendKeys("Ferro");
        driver.findElement(By.cssSelector("textarea[name='descricao']")).sendKeys("Teste");

        // Tenta submeter
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Aguarda o span de erro ficar visível (validarCPFInput() seta display:block)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cpf-error")));

        WebElement erro = driver.findElement(By.id("cpf-error"));
        assertTrue(erro.isDisplayed(), "Mensagem 'CPF Inválido!' deve estar visível");
        assertTrue(driver.getCurrentUrl().contains("/pedido"),
                "Deve permanecer em /pedido quando CPF for inválido");
    }

    @Test
    @DisplayName("CT12.8 - Formulário não submete sem campos obrigatórios")
    void formularioNaoSubmeteSemObrigatorios() {
        abrirPagina("/pedido");
        clicarJS(driver.findElement(By.cssSelector("button[type='submit']")));
        assertTrue(driver.getCurrentUrl().contains("/pedido"));
    }

    @Test
    @DisplayName("CT12.9 - Submissão com dados completos e CPF válido redireciona para home")
    void submissaoCompletaRedirecionaHome() {
        abrirPagina("/pedido");
        driver.findElement(By.cssSelector("input[name='cliente']")).sendKeys("Ana Souza");
        driver.findElement(By.id("telefone")).sendKeys("(41) 97777-6666");
        driver.findElement(By.id("cpf")).sendKeys("529.982.247-25");
        driver.findElement(By.cssSelector("input[name='material']")).sendKeys("Madeira e Ferro");
        driver.findElement(By.cssSelector("textarea[name='descricao']")).sendKeys("Estante para sala de estar");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
        assertEquals(BASE_URL + "/", driver.getCurrentUrl());
    }
}
