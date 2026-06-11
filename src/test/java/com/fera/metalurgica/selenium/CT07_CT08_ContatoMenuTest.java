package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT07 – Campo para Contato
 * CT08 – Menu Superior
 * Tipo: Interface do Usuário e Funcional
 * Cenário: Verifica ícones de redes sociais e estrutura do menu superior.
 */
@DisplayName("CT07 e CT08 - Contato e Menu Superior")
public class CT07_CT08_ContatoMenuTest extends BaseTest {

    // -------------------------------------------------------
    // CT07 – Campos de Contato (redes sociais)
    // -------------------------------------------------------

    @Test
    @DisplayName("CT07.1 - Ícone do Facebook está presente na home")
    void iconeFacebookPresente() {
        abrirPagina("/");
        List<WebElement> icones = driver.findElements(By.cssSelector(".fab.fa-facebook"));
        assertTrue(icones.size() > 0, "Ícone do Facebook deve estar presente na página");
    }

    @Test
    @DisplayName("CT07.2 - Ícone do WhatsApp está presente na home")
    void iconeWhatsappPresente() {
        abrirPagina("/");
        List<WebElement> icones = driver.findElements(By.cssSelector(".fab.fa-whatsapp"));
        assertTrue(icones.size() > 0, "Ícone do WhatsApp deve estar presente na página");
    }

    @Test
    @DisplayName("CT07.3 - Ícone do Instagram está presente e possui href")
    void iconeInstagramComLink() {
        abrirPagina("/");

        // Procura o link que envolve o ícone do Instagram
        List<WebElement> linksInsta = driver.findElements(
                By.cssSelector("a[href*='instagram']"));
        assertTrue(linksInsta.size() > 0,
                "Deve haver pelo menos um link do Instagram na página");

        String href = linksInsta.get(0).getAttribute("href");
        assertTrue(href.contains("instagram"),
                "Link do Instagram deve apontar para instagram.com. href: " + href);
    }

    @Test
    @DisplayName("CT07.4 - Links de contato também estão no rodapé")
    void linksContatoNoRodape() {
        abrirPagina("/");

        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));
        List<WebElement> iconesFooter = footer.findElements(
                By.cssSelector(".fab.fa-facebook, .fab.fa-whatsapp, .fab.fa-instagram"));
        assertTrue(iconesFooter.size() > 0,
                "Ícones de redes sociais devem estar presentes no rodapé");
    }

    // -------------------------------------------------------
    // CT08 – Menu Superior (top nav)
    // -------------------------------------------------------

    @Test
    @DisplayName("CT08.1 - Menu superior está presente na home")
    void menuSuperiorPresente() {
        abrirPagina("/");
        assertTrue(elementoPresente(By.cssSelector(".top-nav, nav")),
                "Menu superior deve estar presente na página inicial");
    }

    @Test
    @DisplayName("CT08.2 - Menu superior contém link para Login/Perfil")
    void menuContemLinkLogin() {
        abrirPagina("/");
        assertTrue(elementoPresente(By.cssSelector("a[href='/login']")),
                "Menu superior deve conter link para /login");
    }

    @Test
    @DisplayName("CT08.3 - Menu superior contém botão para Criar Orçamento")
    void menuContemBotaoOrcamento() {
        abrirPagina("/");
        assertTrue(elementoPresente(By.cssSelector(".nav-links a[href='/pedido']")),
                "Menu superior deve conter link para /pedido (Criar Orçamento)");
    }

    @Test
    @DisplayName("CT08.4 - Clique em 'Criar Orçamento' no menu navega para /pedido")
    void cliqueCriarOrcamentoNavega() {
        abrirPagina("/");

        WebElement btnOrc = aguardarElemento(By.cssSelector(".nav-links a[href='/pedido']"));
        btnOrc.click();

        assertTrue(driver.getCurrentUrl().contains("/pedido"),
                "Clique em Criar Orçamento deve navegar para /pedido");
    }

    @Test
    @DisplayName("CT08.5 - Menu superior também está presente no catálogo")
    void menuSuperiorNoCatalogo() {
        abrirPagina("/catalogo");

        assertTrue(elementoPresente(By.cssSelector("nav, header")),
                "Menu de navegação deve estar presente na página do catálogo");
        assertTrue(elementoPresente(By.cssSelector("a[href='/login']")),
                "Link de login/perfil deve estar no menu do catálogo");
    }
}
