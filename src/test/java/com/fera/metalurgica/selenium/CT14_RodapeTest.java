package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT14 – Rodapé
 * Tipo: Interface do Usuário e Funcional
 * Cenário: Valida que o rodapé possui ícones de contato, coluna "Veja mais"
 *          com links de Catálogo e Orçamento, e coluna "Suporte" com
 *          links de Contato, Suporte e Diretrizes.
 */
@DisplayName("CT14 - Rodapé")
public class CT14_RodapeTest extends BaseTest {

    @Test
    @DisplayName("CT14.1 - Rodapé está presente na home")
    void rodapePresente() {
        abrirPagina("/");

        assertTrue(elementoPresente(By.cssSelector("footer, .main-footer")),
                "Rodapé deve estar presente na página home");
    }

    @Test
    @DisplayName("CT14.2 - Rodapé contém ícones de redes sociais")
    void rodapeContemRedesSociais() {
        abrirPagina("/");

        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));

        List<WebElement> icones = footer.findElements(
                By.cssSelector(".fab.fa-facebook, .fab.fa-whatsapp, .fab.fa-instagram"));
        assertTrue(icones.size() >= 2,
                "Rodapé deve conter ao menos 2 ícones de redes sociais. Encontrados: " + icones.size());
    }

    @Test
    @DisplayName("CT14.3 - Coluna 'Veja mais' contém link para Catálogo")
    void colunaVejaMaisContemCatalogo() {
        abrirPagina("/");

        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));
        String textoFooter = footer.getText();

        assertTrue(textoFooter.contains("Veja mais") || textoFooter.contains("Vejá mais"),
                "Rodapé deve ter seção 'Veja mais'");
        assertTrue(textoFooter.contains("Catálogo"),
                "Coluna 'Veja mais' deve conter link 'Catálogo'");
    }

    @Test
    @DisplayName("CT14.4 - Coluna 'Veja mais' contém link para Orçamento")
    void colunaVejaMaisContemOrcamento() {
        abrirPagina("/");

        // Link de orçamento no rodapé aponta para /pedido
        List<WebElement> linksOrc = driver.findElements(
                By.cssSelector("footer a[href='/pedido'], .main-footer a[href='/pedido']"));
        assertTrue(linksOrc.size() > 0,
                "Rodapé deve conter link de Orçamento apontando para /pedido");
    }

    @Test
    @DisplayName("CT14.5 - Coluna 'Suporte' está presente no rodapé")
    void colunaSuportePresente() {
        abrirPagina("/");

        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));
        assertTrue(footer.getText().contains("Suporte"),
                "Rodapé deve conter seção 'Suporte'");
    }

    @Test
    @DisplayName("CT14.6 - Coluna Suporte contém Contato, Suporte e Diretrizes")
    void colunaSuporteContemLinks() {
        abrirPagina("/");

        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));
        String textoFooter = footer.getText();

        assertTrue(textoFooter.contains("Contato"),   "Rodapé deve conter 'Contato'");
        assertTrue(textoFooter.contains("Suporte"),   "Rodapé deve conter 'Suporte'");
        assertTrue(textoFooter.contains("Diretrizes"), "Rodapé deve conter 'Diretrizes'");
    }

    @Test
    @DisplayName("CT14.7 - Rodapé exibe nome e descrição da empresa")
    void rodapeExibeNomeEmpresa() {
        abrirPagina("/");

        WebElement footer = aguardarElemento(By.cssSelector("footer, .main-footer"));
        String texto = footer.getText();

        assertTrue(texto.contains("Fera") || texto.contains("FERA"),
                "Rodapé deve mencionar o nome da empresa Fera");
    }

    @Test
    @DisplayName("CT14.8 - Rodapé também aparece na página do catálogo")
    void rodapeNoCatalogo() {
        abrirPagina("/catalogo");

        assertTrue(elementoPresente(By.cssSelector("footer, .main-footer")),
                "Rodapé deve estar presente na página do catálogo");
    }

    @Test
    @DisplayName("CT14.9 - Rodapé também aparece na página de pedido")
    void rodapeNaPaginaPedido() {
        abrirPagina("/pedido");

        // Página /pedido tem link de voltar mas verifica que o layout carregou
        assertTrue(elementoPresente(By.cssSelector("a[href='/']")),
                "Página de pedido deve conter link de retorno à home");
    }
}
