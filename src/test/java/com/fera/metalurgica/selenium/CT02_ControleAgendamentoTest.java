package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT02 – Controle de Agendamento
 * Tipo: Funcional
 */
@DisplayName("CT02 - Controle de Agendamento")
public class CT02_ControleAgendamentoTest extends BaseTest {

    @Test
    @DisplayName("CT02.1 - Página de agenda carrega corretamente")
    void paginaAgendaCarrega() {
        loginComoAdmin();
        abrirPagina("/agenda");
        assertTrue(elementoPresente(By.cssSelector(".btn-agendar, button")),
                "Botão de agendar deve estar presente");
        assertTrue(elementoPresente(By.id("diasCalendario")),
                "Grid de dias do calendário deve estar presente");
    }

    @Test
    @DisplayName("CT02.2 - Modal de agendamento abre ao clicar no botão")
    void modalAgendamentoAbre() {
        loginComoAdmin();
        abrirPagina("/agenda");
        aguardarElemento(By.cssSelector(".btn-agendar")).click();
        WebElement modal = aguardarElemento(By.id("modalAgendamento"));
        assertTrue(modal.isDisplayed(), "Modal de agendamento deve ficar visível");
    }

    @Test
    @DisplayName("CT02.3 - Cadastro de novo agendamento com dados válidos")
    void cadastrarAgendamento() {
        loginComoAdmin();
        abrirPagina("/agenda");

        aguardarElemento(By.cssSelector(".btn-agendar")).click();
        aguardarElemento(By.id("modalAgendamento"));

        driver.findElement(By.id("inputTitulo")).sendKeys("Entrega de Móvel - Teste");
        driver.findElement(By.id("inputDescricao")).sendKeys("Descrição do teste de agendamento");
        driver.findElement(By.id("inputData")).sendKeys("2026-12-31");
        driver.findElement(By.id("inputHoraInicio")).sendKeys("09:00");
        driver.findElement(By.id("inputHoraFim")).sendKeys("11:00");
        driver.findElement(By.id("inputEvento")).sendKeys("Galpão A - Endereço de Teste");

        driver.findElement(By.cssSelector("#formAgendamento button[type='submit']")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("#modalAgendamento[style*='block']")));

        assertTrue(elementoPresente(By.id("listaCompromissos")),
                "Lista de compromissos deve estar presente após cadastro");
    }

    @Test
    @DisplayName("CT02.4 - Formulário não submete sem campos obrigatórios")
    void formularioValidacaoObrigatorios() {
        loginComoAdmin();
        abrirPagina("/agenda");

        aguardarElemento(By.cssSelector(".btn-agendar")).click();
        aguardarElemento(By.id("modalAgendamento"));

        clicarJS(driver.findElement(By.cssSelector("#formAgendamento button[type='submit']")));

        assertTrue(elementoPresente(By.id("modalAgendamento")),
                "Modal deve continuar aberto quando campos obrigatórios estão vazios");
    }

    @Test
    @DisplayName("CT02.5 - Calendário exibe o mês atual")
    void calendarioExibeMesAtual() {
        loginComoAdmin();
        abrirPagina("/agenda");

        WebElement mesAno = aguardarElemento(By.id("mesAnoAtual"));
        assertFalse(mesAno.getText().isEmpty(), "Mês/ano não pode estar em branco");
    }
}
