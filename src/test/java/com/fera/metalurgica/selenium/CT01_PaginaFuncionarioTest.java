package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT01 – Página do Funcionário (testado com credenciais de Admin)
 * Tipo: Funcional
 */
@DisplayName("CT01 - Página do Funcionário")
public class CT01_PaginaFuncionarioTest extends BaseTest {

    @Test
    @DisplayName("CT01.1 - Admin acessa o dashboard após login")
    void adminAcessaDashboard() {
        loginComoAdmin();
        assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Após login, deve redirecionar para /dashboard. URL: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("CT01.2 - Admin acessa a página de Agenda")
    void adminAcessaAgenda() {
        loginComoAdmin();
        abrirPagina("/agenda");
        assertTrue(driver.getCurrentUrl().contains("/agenda"),
                "URL deve conter /agenda. URL: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("CT01.3 - Admin acessa a página de Orçamentos")
    void adminAcessaOrcamentos() {
        loginComoAdmin();
        abrirPagina("/orcamentos");
        assertTrue(driver.getCurrentUrl().contains("/orcamentos"),
                "URL deve conter /orcamentos. URL: " + driver.getCurrentUrl());
        assertTrue(elementoPresente(By.cssSelector(".btn-brown, button")),
                "Botão de novo orçamento deve estar presente");
    }

    @Test
    @DisplayName("CT01.4 - Admin acessa a página de Mídia")
    void adminAcessaMidia() {
        loginComoAdmin();
        abrirPagina("/midia");
        assertTrue(driver.getCurrentUrl().contains("/midia"),
                "URL deve conter /midia. URL: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("CT01.5 - Acesso bloqueado sem autenticação redireciona para login")
    void acessoBloqueadoSemLogin() {
        abrirPagina("/dashboard");
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "Acesso não autenticado deve redirecionar para /login. URL: " + driver.getCurrentUrl());
    }
}
