package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT09 – Carrossel de Imagens (Galeria de Projetos)
 * CT10 – Categoria de Produtos
 * CT11 – Catálogo de Produtos
 * Tipo: Interface do Usuário, Funcional, Carga (estrutural)
 * Cenário: Acesso ao site como cliente, navegação pelo catálogo.
 */
@DisplayName("CT09/CT10/CT11 - Carrossel, Categorias e Catálogo")
public class CT09_CT10_CT11_CatalogoProdutosTest extends BaseTest {

    // -------------------------------------------------------
    // CT09 – Carrossel de Imagens
    // -------------------------------------------------------

    @Test
    @DisplayName("CT09.1 - Galeria exibe imagens na home")
    void galeriaExibeImagens() {
        abrirPagina("/");

        List<WebElement> imgs = driver.findElements(By.cssSelector(".gallery-images img"));
        assertFalse(imgs.isEmpty(), "Galeria deve exibir imagens na home");

        for (WebElement img : imgs) {
            String src = img.getAttribute("src");
            assertNotNull(src, "Cada imagem da galeria deve ter atributo src");
            assertFalse(src.isEmpty(), "Atributo src não deve estar vazio");
        }
    }

    @Test
    @DisplayName("CT09.2 - Botões de navegação do carrossel estão presentes")
    void botoesNavegacaoCarrossel() {
        abrirPagina("/");

        List<WebElement> botoesNav = driver.findElements(By.cssSelector(".nav-btn"));
        assertEquals(2, botoesNav.size(),
                "Carrossel deve ter 2 botões de navegação (esquerda e direita)");
    }

    @Test
    @DisplayName("CT09.3 - Imagem da galeria abre modal ao ser clicada")
    void imagemGaleriaAbreModal() {
        abrirPagina("/");

        WebElement img = aguardarElemento(By.cssSelector(".gallery-images img"));
        clicarJS(img);

        WebElement modal = aguardarElemento(By.id("modalGaleria"));
        assertEquals("block", modal.getCssValue("display"),
                "Modal deve ficar visível após clique na imagem");
    }

    @Test
    @DisplayName("CT09.4 - Modal da galeria exibe a imagem correta")
    void modalGaleriaExibeImagemCorreta() {
        abrirPagina("/");

        WebElement img = aguardarElemento(By.cssSelector(".gallery-images img"));
        String srcOriginal = img.getAttribute("src");
        clicarJS(img);

        WebElement imgExpandida = aguardarElemento(By.id("imagemExpandida"));
        String srcExpandida = imgExpandida.getAttribute("src");

        assertEquals(srcOriginal, srcExpandida,
                "Imagem expandida no modal deve ser a mesma clicada");
    }

    @Test
    @DisplayName("CT09.5 - Modal fecha ao chamar fecharModal()")
    void modalFechaAoClicarFechar() {
        abrirPagina("/");

        clicarJS(aguardarElemento(By.cssSelector(".gallery-images img")));
        aguardarElemento(By.id("modalGaleria"));

        WebElement btnFechar = driver.findElement(By.cssSelector(".fechar-modal"));
        btnFechar.click();

        WebElement modal = driver.findElement(By.id("modalGaleria"));
        assertNotEquals("block", modal.getCssValue("display"),
                "Modal deve fechar após clicar no botão X");
    }

    // -------------------------------------------------------
    // CT10 – Categoria de Produtos
    // -------------------------------------------------------

    @Test
    @DisplayName("CT10.1 - Página do catálogo lista as categorias de produtos")
    void catalogoListaCategorias() {
        abrirPagina("/catalogo");

        List<WebElement> categorias = driver.findElements(By.cssSelector(".ambiente-item"));
        assertTrue(categorias.size() > 0,
                "Página do catálogo deve listar ao menos uma categoria");
    }

    @Test
    @DisplayName("CT10.2 - Categorias esperadas estão presentes no catálogo")
    void categoriesEsperadasPresentes() {
        abrirPagina("/catalogo");

        String[] categoriesEsperadas = {"Adega", "Banheiro", "Biblioteca", "Closet", "Cozinha"};
        String pageSource = driver.getPageSource();

        for (String cat : categoriesEsperadas) {
            assertTrue(pageSource.contains(cat),
                    "Categoria '" + cat + "' deve estar presente no catálogo");
        }
    }

    @Test
    @DisplayName("CT10.3 - Clique em 'Adega' navega para a página correta")
    void cliqueAdegaNavegaCorretamente() {
        abrirPagina("/catalogo");

        WebElement linkAdega = aguardarElemento(By.cssSelector("a[href='/catalogo/adega']"));
        linkAdega.click();

        assertTrue(driver.getCurrentUrl().contains("/catalogo/adega"),
                "Clique em Adega deve navegar para /catalogo/adega");
    }

    @Test
    @DisplayName("CT10.4 - Botões 'Inspire-se' e 'Saiba mais' da home levam ao catálogo")
    void botoesHomeLevamAoCatalogo() {
        abrirPagina("/");

        List<WebElement> linksCatalogo = driver.findElements(
                By.cssSelector("a[href='/catalogo']"));
        assertTrue(linksCatalogo.size() >= 1,
                "Deve haver pelo menos um link para /catalogo na home (Inspire-se / Saiba mais)");
    }

    // -------------------------------------------------------
    // CT11 – Catálogo de Produtos (página interna)
    // -------------------------------------------------------

    @Test
    @DisplayName("CT11.1 - Página de categoria 'Adega' carrega produtos")
    void paginaAdegaCarregaProdutos() {
        abrirPagina("/catalogo/adega");

        // Verifica que a página carregou (título ou imagem presente)
        assertFalse(driver.getTitle().isEmpty(), "Página da Adega deve carregar com um título");
        assertTrue(elementoPresente(By.tagName("img")),
                "Página da Adega deve exibir pelo menos uma imagem de produto");
    }

    @Test
    @DisplayName("CT11.2 - Rodapé do catálogo contém link para orçamento")
    void rodapeCatalogoContemOrcamento() {
        abrirPagina("/catalogo");

        assertTrue(elementoPresente(By.cssSelector("a[href='/pedido']")),
                "Catálogo deve conter link para /pedido (Orçamento)");
    }

    @Test
    @DisplayName("CT11.3 - Catálogo é acessível pelo link no rodapé da home")
    void catalogoAcessivelPeloRodape() {
        abrirPagina("/");

        // Rola até o rodapé e clica no link Catálogo
        List<WebElement> linksRodape = driver.findElements(
                By.cssSelector("footer a[href='/catalogo'], .footer-column a[href='/catalogo']"));
        assertFalse(linksRodape.isEmpty(),
                "Rodapé deve conter link para /catalogo");
    }
}
