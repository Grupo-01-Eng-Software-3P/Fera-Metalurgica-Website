package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT03 – Controle de Orçamento
 * Tipo: Funcional
 */
@DisplayName("CT03 - Controle de Orçamento")
public class CT03_ControleOrcamentoTest extends BaseTest {

    @Test
    @DisplayName("CT03.1 - Página de orçamentos carrega corretamente")
    void paginaOrcamentosCarrega() {
        loginComoAdmin();
        abrirPagina("/orcamentos");
        assertTrue(elementoPresente(By.cssSelector(".btn-brown")),
                "Botão '+ Novo Orçamento' deve estar presente");
    }

    @Test
    @DisplayName("CT03.2 - Modal de novo orçamento abre corretamente")
    void modalNovoOrcamentoAbre() {
        loginComoAdmin();
        abrirPagina("/orcamentos");
        aguardarElemento(By.cssSelector(".btn-brown")).click();
        WebElement modal = aguardarElemento(By.id("modalOrcamento"));
        assertTrue(modal.isDisplayed(), "Modal de orçamento deve ficar visível");
    }

    @Test
    @DisplayName("CT03.3 - Formulário de pedido do cliente pode ser preenchido")
    void formularioPedidoClientePreenchivel() {
        loginComoAdmin();
        abrirPagina("/orcamentos");

        aguardarElemento(By.cssSelector(".btn-brown")).click();
        aguardarElemento(By.id("modalOrcamento"));

        WebElement inputCliente = aguardarElemento(By.cssSelector("#formPedidoCliente input[name='cliente']"));
        inputCliente.sendKeys("Maria Teste");
        driver.findElement(By.cssSelector("input[name='telefone']")).sendKeys("(41) 99999-1234");
        driver.findElement(By.cssSelector("input[name='cpf']")).sendKeys("529.982.247-25");
        driver.findElement(By.cssSelector("input[name='material']")).sendKeys("Ferro e Vidro");
        driver.findElement(By.cssSelector("textarea[name='descricao']")).sendKeys("Prateleira industrial para galpão");

        assertEquals("Maria Teste", inputCliente.getAttribute("value"),
                "Campo cliente deve conter o valor digitado");
    }

    @Test
    @DisplayName("CT03.4 - Botão 'Virar para Orçamento' troca para o lado admin")
    void botaoVirarCartaoFunciona() {
        loginComoAdmin();
        abrirPagina("/orcamentos");

        aguardarElemento(By.cssSelector(".btn-brown")).click();
        aguardarElemento(By.id("modalOrcamento"));
        aguardarElemento(By.cssSelector(".btn-flip")).click();

        assertTrue(elementoPresente(By.id("formOrcamentoAdmin")),
                "Formulário do orçamento admin deve existir no DOM após virar o cartão");
    }

    @Test
    @DisplayName("CT03.5 - Modal preenche frete e mão de obra separadamente")
    void modalPreencheAdicionaisSeparados() {
        loginComoAdmin();
        abrirPagina("/orcamentos");

        java.util.Map<String, Object> pedido = new java.util.HashMap<>();
        pedido.put("id", 15);
        pedido.put("cliente", "Cliente Teste");
        pedido.put("telefone", "(45) 99999-0000");
        pedido.put("cpf", "123.456.789-09");
        pedido.put("material", "Ferro");
        pedido.put("medidas", "2m x 1m");
        pedido.put("descricao", "Orçamento de teste");
        pedido.put("criadoPor", "ADMIN");
        pedido.put("valorFrete", "50.00");
        pedido.put("valorMaoObra", "120.00");
        pedido.put("item1Nome", "Estrutura");
        pedido.put("item1Quantidade", "2");
        pedido.put("item1ValorUnitario", "100.00");

        ((JavascriptExecutor) driver).executeScript("abrirModalNovoOrcamento(arguments[0]);", pedido);

        assertEquals("50.00", aguardarElemento(By.cssSelector("input[name='frete']")).getAttribute("value"));
        assertEquals("120.00", aguardarElemento(By.cssSelector("input[name='maoObra']")).getAttribute("value"));
        assertEquals("R$\u00A0370,00", aguardarElemento(By.id("valorTotalCalculado")).getAttribute("value"));
    }

    @Test
    @DisplayName("CT03.6 - Frete inicia vazio quando o pedido não tem valor informado")
    void freteIniciaVazioSemValorInformado() {
        loginComoAdmin();
        abrirPagina("/orcamentos");

        java.util.Map<String, Object> pedido = new java.util.HashMap<>();
        pedido.put("id", 16);
        pedido.put("cliente", "Cliente Sem Frete");
        pedido.put("telefone", "(45) 99999-0000");
        pedido.put("cpf", "123.456.789-09");
        pedido.put("material", "Aço");
        pedido.put("medidas", "1m x 1m");
        pedido.put("descricao", "Orçamento sem frete informado");
        pedido.put("criadoPor", "CLIENTE");
        pedido.put("valorAdicionais", "0.00");

        ((JavascriptExecutor) driver).executeScript("abrirModalNovoOrcamento(arguments[0]);", pedido);

        assertEquals("", aguardarElemento(By.cssSelector("input[name='frete']")).getAttribute("value"));
        assertEquals("", aguardarElemento(By.cssSelector("input[name='maoObra']")).getAttribute("value"));
        assertEquals("R$\u00A00,00", aguardarElemento(By.id("valorTotalCalculado")).getAttribute("value"));
    }

    @Test
    @DisplayName("CT03.7 - Campos de itens do orçamento são editáveis")
    void camposItensOrcamentoEditaveis() {
        loginComoAdmin();
        abrirPagina("/orcamentos");

        aguardarElemento(By.cssSelector(".btn-brown")).click();
        aguardarElemento(By.id("modalOrcamento"));
        aguardarElemento(By.cssSelector(".btn-flip")).click();

        WebElement inputNomeItem = aguardarElemento(By.cssSelector("input[name='itens[0].nome']"));
        inputNomeItem.sendKeys("Estrutura Metálica");

        assertEquals("Estrutura Metálica", inputNomeItem.getAttribute("value"),
                "Campo nome do item deve aceitar entrada de texto");
    }

    @Test
    @DisplayName("CT03.8 - Lista de orçamentos existentes é exibida")
    void listaOrcamentosExibida() {
        loginComoAdmin();
        abrirPagina("/orcamentos");
        assertTrue(elementoPresente(By.cssSelector("table, .orcamentos-lista, .card, [data-id]")),
                "Container de orçamentos deve estar presente na página");
    }
}
