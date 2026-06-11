package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT06 – Página do Cliente
 * Tipo: Interface do Usuário e Funcional
 * Cenário: Cliente acessa a home page sem autenticação, verifica elementos
 *          de UI e funcionalidade de navegação.
 */
@DisplayName("CT06 - Página do Cliente (Home)")
public class CT06_PaginaClienteTest extends BaseTest {

    @Test
    @DisplayName("CT06.1 - Home page carrega sem necessidade de login")
    void homepageCarregaSemLogin() {
        abrirPagina("/");

        String titulo = driver.getTitle();
        assertFalse(titulo.isEmpty(), "Título da página não deve estar vazio");
        assertTrue(titulo.contains("Fera") || titulo.contains("Home"),
                "Título deve mencionar Fera ou Home. Título: " + titulo);
    }

    @Test
    @DisplayName("CT06.2 - Logo FERA está visível no menu superior")
    void logoFeraVisivel() {
        abrirPagina("/");

        WebElement logo = aguardarElemento(By.cssSelector(".logo h2, .logo, nav h2"));
        assertTrue(logo.getText().contains("FERA"),
                "Logo FERA deve estar visível no cabeçalho");
    }

    @Test
    @DisplayName("CT06.3 - Botão 'Criar Orçamento' está presente e aponta para /pedido")
    void botaoCriarOrcamentoPresente() {
        abrirPagina("/");

        WebElement btnOrcamento = aguardarElemento(By.cssSelector("a[href='/pedido']"));
        assertTrue(btnOrcamento.isDisplayed(),
                "Botão 'Criar Orçamento' deve estar visível na home");
    }

    @Test
    @DisplayName("CT06.4 - Link 'Inspire-se' leva ao catálogo")
    void linkInspireseLevaCatalogo() {
        abrirPagina("/");

        WebElement btnInspire = aguardarElemento(By.cssSelector("a[href='/catalogo']"));
        btnInspire.click();

        assertTrue(driver.getCurrentUrl().contains("/catalogo"),
                "Botão Inspire-se deve navegar para /catalogo");
    }

    @Test
    @DisplayName("CT06.5 - Seção de benefícios exibe 4 cards")
    void secaoBeneficiosExibeCards() {
        abrirPagina("/");

        List<WebElement> cards = driver.findElements(By.cssSelector(".benefit-card"));
        assertEquals(4, cards.size(),
                "Seção 'Por que escolher FERA?' deve exibir exatamente 4 cards de benefícios");
    }

    @Test
    @DisplayName("CT06.6 - Galeria de projetos exibe 3 imagens")
    void galeriaProjetosExibeImagens() {
        abrirPagina("/");

        List<WebElement> imagens = driver.findElements(By.cssSelector(".gallery-images img"));
        assertEquals(3, imagens.size(),
                "Galeria de projetos deve exibir 3 imagens");
    }

    @Test
    @DisplayName("CT06.7 - Modal da galeria abre ao clicar em uma imagem")
    void modalGaleriaAbreAoClicar() {
        abrirPagina("/");

        WebElement primeiraImagem = aguardarElemento(By.cssSelector(".gallery-images img"));
        clicarJS(primeiraImagem);

        WebElement modal = aguardarElemento(By.id("modalGaleria"));
        // O modal é exibido via JS com style="display:block"
        String display = modal.getCssValue("display");
        assertEquals("block", display,
                "Modal da galeria deve estar visível após clicar em uma imagem");
    }

    @Test
    @DisplayName("CT06.8 - Botão 'Faça seu orçamento' na seção CTA está funcional")
    void botaoFacaOrcamentoCTA() {
        abrirPagina("/");

        WebElement btnCTA = aguardarElemento(By.cssSelector(".btn-white-solid[href='/pedido']"));
        assertTrue(btnCTA.isDisplayed(),
                "Botão 'Faça seu orçamento' deve estar visível na seção CTA");
    }

    @Test
    @DisplayName("CT06.9 - Link de Perfil/Login está no menu superior")
    void linkLoginNoMenu() {
        abrirPagina("/");

        WebElement linkPerfil = aguardarElemento(By.cssSelector("a[href='/login']"));
        assertTrue(linkPerfil.isDisplayed(),
                "Link de Perfil/Login deve estar visível no menu superior");
    }
}
