package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
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
    @DisplayName("CT03.5 - Campos de itens do orçamento são editáveis")
    void camposItensOrcamentoEditaveis() {
        loginComoAdmin();
        abrirPagina("/orcamentos");

        aguardarElemento(By.cssSelector(".btn-brown")).click();
        aguardarElemento(By.id("modalOrcamento"));
        aguardarElemento(By.cssSelector(".btn-flip")).click();

        WebElement inputNomeItem = aguardarElemento(By.cssSelector("input[name='itemNome']"));
        inputNomeItem.sendKeys("Estrutura Metálica");

        assertEquals("Estrutura Metálica", inputNomeItem.getAttribute("value"),
                "Campo nome do item deve aceitar entrada de texto");
    }

    @Test
    @DisplayName("CT03.6 - Lista de orçamentos existentes é exibida")
    void listaOrcamentosExibida() {
        loginComoAdmin();
        abrirPagina("/orcamentos");
        assertTrue(elementoPresente(By.cssSelector("table, .orcamentos-lista, .card, [data-id]")),
                "Container de orçamentos deve estar presente na página");
    }
}
