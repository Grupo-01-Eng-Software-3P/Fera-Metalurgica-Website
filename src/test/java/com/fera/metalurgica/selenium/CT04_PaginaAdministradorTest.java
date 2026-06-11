package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT04 – Página do Administrador
 * Tipo: Funcional
 */
@DisplayName("CT04 - Página do Administrador")
public class CT04_PaginaAdministradorTest extends BaseTest {

    @Test
    @DisplayName("CT04.1 - Admin faz login e acessa o dashboard")
    void adminAcessaDashboard() {
        loginComoAdmin();
        assertTrue(driver.getCurrentUrl().contains("/dashboard"),
                "Admin deve ser redirecionado para /dashboard após login. URL: " + driver.getCurrentUrl());
    }

    @Test
    @DisplayName("CT04.2 - Admin acessa a página de Usuários")
    void adminAcessaUsuarios() {
        loginComoAdmin();
        abrirPagina("/usuarios");
        assertTrue(elementoPresente(By.cssSelector(".btn-brown")),
                "Botão '+ Criar Usuário' deve estar visível para o admin");
    }

    @Test
    @DisplayName("CT04.3 - Modal de cadastro de usuário abre corretamente")
    void modalCadastroUsuarioAbre() {
        loginComoAdmin();
        abrirPagina("/usuarios");
        aguardarElemento(By.cssSelector(".btn-brown")).click();
        WebElement modal = aguardarElemento(By.id("modalCadastroUsuario"));
        assertTrue(modal.isDisplayed(), "Modal de cadastro de usuário deve estar visível");
    }

    @Test
    @DisplayName("CT04.4 - Formulário de novo usuário pode ser preenchido")
    void formularioNovoUsuarioPreenchivel() {
        loginComoAdmin();
        abrirPagina("/usuarios");

        aguardarElemento(By.cssSelector(".btn-brown")).click();
        aguardarElemento(By.id("modalCadastroUsuario"));

        WebElement inputNome = aguardarElemento(By.cssSelector("input[name='nome']"));
        inputNome.sendKeys("João Funcionário");

        new Select(driver.findElement(By.cssSelector("select[name='cargo']"))).selectByIndex(1);

        driver.findElement(By.cssSelector("input[name='dataNascimento']")).sendKeys("1995-06-15");
        driver.findElement(By.id("inputEmail")).sendKeys("joao.teste@fera.com");
        driver.findElement(By.id("inputSenha")).sendKeys("Senha@123");

        assertEquals("João Funcionário", inputNome.getAttribute("value"),
                "Campo nome deve conter o valor preenchido");
    }

    @Test
    @DisplayName("CT04.5 - Admin acessa todas as seções do menu")
    void adminAcessaTodasSecoes() {
        loginComoAdmin();
        for (String rota : new String[]{"/agenda", "/midia", "/orcamentos", "/usuarios"}) {
            abrirPagina(rota);
            assertTrue(driver.getCurrentUrl().contains(rota),
                    "Admin deve conseguir acessar " + rota);
        }
    }

    @Test
    @DisplayName("CT04.6 - Campo de busca de usuários funciona")
    void campoBuscaUsuarios() {
        loginComoAdmin();
        abrirPagina("/usuarios");

        WebElement inputBusca = aguardarElemento(By.id("inputPesquisa"));
        inputBusca.sendKeys("admin");

        assertEquals("admin", inputBusca.getAttribute("value"),
                "Campo de busca deve aceitar texto digitado");
    }
}
