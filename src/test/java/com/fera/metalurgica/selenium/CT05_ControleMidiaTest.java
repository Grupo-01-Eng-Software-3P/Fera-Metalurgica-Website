package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT05 – Controle de Mídia
 * Tipo: Funcional
 */
@DisplayName("CT05 - Controle de Mídia")
public class CT05_ControleMidiaTest extends BaseTest {

    @Test
    @DisplayName("CT05.1 - Página de mídia carrega com categorias listadas")
    void paginaMidiaCarregaCategorias() {
        loginComoAdmin();
        abrirPagina("/midia");
        assertTrue(elementoPresente(By.id("categoriaLista")),
                "Container de categorias deve estar presente");
        assertTrue(driver.findElements(By.cssSelector(".categoria-item")).size() > 0,
                "Deve haver ao menos uma categoria listada");
    }

    @Test
    @DisplayName("CT05.2 - Filtros de mídia estão presentes e clicáveis")
    void filtrosMidiaPresentes() {
        loginComoAdmin();
        abrirPagina("/midia");
        List<WebElement> filtros = driver.findElements(By.cssSelector(".filter-pill"));
        assertTrue(filtros.size() >= 4, "Devem existir ao menos 4 filtros");
        for (WebElement filtro : filtros) {
            clicarJS(filtro);
            assertFalse(driver.getTitle().isEmpty(), "Página não deve quebrar ao clicar no filtro");
        }
    }

    @Test
    @DisplayName("CT05.3 - Campo de busca aceita texto")
    void campoBuscaFunciona() {
        loginComoAdmin();
        abrirPagina("/midia");
        WebElement inputBusca = aguardarElemento(By.id("inputBusca"));
        inputBusca.sendKeys("adega");
        assertEquals("adega", inputBusca.getAttribute("value"));
    }

    @Test
    @DisplayName("CT05.4 - Modal de nova categoria abre corretamente")
    void modalNovaCategoriaAbre() {
        loginComoAdmin();
        abrirPagina("/midia");
        aguardarElemento(By.cssSelector(".btn-nova-categoria")).click();
        WebElement modal = aguardarElemento(By.id("modalCategoria"));
        assertTrue(modal.isDisplayed(), "Modal de nova categoria deve estar visível");
    }

    @Test
    @DisplayName("CT05.5 - Formulário de nova categoria pode ser preenchido")
    void formularioNovaCategoriaPreenchivel() {
        loginComoAdmin();
        abrirPagina("/midia");
        aguardarElemento(By.cssSelector(".btn-nova-categoria")).click();
        aguardarElemento(By.id("modalCategoria"));

        WebElement inputNome = driver.findElement(By.cssSelector("#modalCategoria input[name='nome']"));
        inputNome.sendKeys("Sala de Estar");

        assertEquals("Sala de Estar", inputNome.getAttribute("value"));
    }

    @Test
    @DisplayName("CT05.6 - Navegação para categoria Adega funciona")
    void navegacaoParaCategoriaAdega() {
        loginComoAdmin();
        abrirPagina("/midia");
        aguardarElemento(By.cssSelector("a[href='/midia/adega']")).click();
        assertTrue(driver.getCurrentUrl().contains("/midia/adega"),
                "Deve navegar para /midia/adega");
    }

    @Test
    @DisplayName("CT05.7 - Carrossel de imagens tem botões de navegação")
    void carrosselTemBotoes() {
        loginComoAdmin();
        abrirPagina("/midia");
        assertTrue(driver.findElements(By.cssSelector(".carrossel-btn.esq")).size() > 0,
                "Devem existir botões esquerdos no carrossel");
        assertTrue(driver.findElements(By.cssSelector(".carrossel-btn.dir")).size() > 0,
                "Devem existir botões direitos no carrossel");
    }
}
