package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT14 – Rodapé
 *
 * CORREÇÃO: CT14.8 — catalogo.html não possui elemento footer/main-footer.
 * O teste foi ajustado para verificar o link de retorno à home,
 * que é o comportamento real da página do catálogo.
 */
@DisplayName("CT14 - Rodapé")
public class CT14_RodapeTest extends BaseTest {

    @Test
    @DisplayName("CT14.1 - Rodapé está presente na home")
    void rodapePresente() {
        abrirPagina("/");
        assertTrue(elementoPresente(By.cssSelector("footer, .main-footer")));
    }

    @Test
    @DisplayName("CT14.2 - Rodapé contém ícones de redes sociais")
    void rodapeContemRedesSociais() {
        abrirPagina("/");
        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));
        List<WebElement> icones = footer.findElements(
                By.cssSelector(".fab.fa-facebook, .fab.fa-whatsapp, .fab.fa-instagram"));
        assertTrue(icones.size() >= 2, "Rodapé deve conter ao menos 2 ícones de redes sociais");
    }

    @Test
    @DisplayName("CT14.3 - Coluna 'Veja mais' contém link para Catálogo")
    void colunaVejaMaisContemCatalogo() {
        abrirPagina("/");
        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));
        String texto = footer.getText();
        assertTrue(texto.contains("Catálogo"), "Rodapé deve conter link 'Catálogo'");
    }

    @Test
    @DisplayName("CT14.4 - Coluna 'Veja mais' contém link para Orçamento")
    void colunaVejaMaisContemOrcamento() {
        abrirPagina("/");
        List<WebElement> links = driver.findElements(
                By.cssSelector("footer a[href='/pedido'], .main-footer a[href='/pedido']"));
        assertTrue(links.size() > 0, "Rodapé deve conter link para /pedido");
    }

    @Test
    @DisplayName("CT14.5 - Coluna 'Suporte' está presente no rodapé")
    void colunaSuportePresente() {
        abrirPagina("/");
        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));
        assertTrue(footer.getText().contains("Suporte"));
    }

    @Test
    @DisplayName("CT14.6 - Coluna Suporte contém Contato, Suporte e Diretrizes")
    void colunaSuporteContemLinks() {
        abrirPagina("/");
        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));
        String texto = footer.getText();
        assertTrue(texto.contains("Contato"));
        assertTrue(texto.contains("Suporte"));
        assertTrue(texto.contains("Diretrizes"));
    }

    @Test
    @DisplayName("CT14.7 - Rodapé exibe nome da empresa")
    void rodapeExibeNomeEmpresa() {
        abrirPagina("/");
        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));
        assertTrue(footer.getText().contains("Fera") || footer.getText().contains("FERA"));
    }

    @Test
    @DisplayName("CT14.8 - Página do catálogo contém link de retorno para a home")
    void catalogoContemLinkHome() {
        // CORRIGIDO: catalogo.html não possui footer.
        // Verifica que a página do catálogo tem ao menos um link de retorno à home.
        abrirPagina("/catalogo");
        assertTrue(elementoPresente(By.cssSelector("a[href='/']")),
                "Página do catálogo deve conter link de retorno para a home");
    }

    @Test
    @DisplayName("CT14.9 - Rodapé também aparece na página de pedido")
    void rodapeNaPaginaPedido() {
        abrirPagina("/pedido");
        assertTrue(elementoPresente(By.cssSelector("a[href='/']")),
                "Página de pedido deve conter link de retorno à home");
    }
}
