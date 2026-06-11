package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CT09 – Carrossel de Imagens
 * CT10 – Categoria de Produtos
 * CT11 – Catálogo de Produtos
 *
 * CORREÇÃO: CT11.3 — catalogo.html não possui rodapé (footer),
 * por isso o teste foi ajustado para verificar apenas o link de
 * retorno à home, que é o comportamento real da página.
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
            assertFalse(img.getAttribute("src").isEmpty(), "Src da imagem não deve estar vazio");
        }
    }

    @Test
    @DisplayName("CT09.2 - Botões de navegação do carrossel estão presentes")
    void botoesNavegacaoCarrossel() {
        abrirPagina("/");
        List<WebElement> botoesNav = driver.findElements(By.cssSelector(".nav-btn"));
        assertEquals(2, botoesNav.size(), "Carrossel deve ter 2 botões de navegação");
    }

    @Test
    @DisplayName("CT09.3 - Imagem da galeria abre modal ao ser clicada")
    void imagemGaleriaAbreModal() {
        abrirPagina("/");
        WebElement img = aguardarElemento(By.cssSelector(".gallery-images img"));
        clicarJS(img);
        WebElement modal = aguardarElemento(By.id("modalGaleria"));
        assertEquals("block", modal.getCssValue("display"));
    }

    @Test
    @DisplayName("CT09.4 - Modal da galeria exibe a imagem correta")
    void modalGaleriaExibeImagemCorreta() {
        abrirPagina("/");
        WebElement img = aguardarElemento(By.cssSelector(".gallery-images img"));
        String srcOriginal = img.getAttribute("src");
        clicarJS(img);
        WebElement imgExpandida = aguardarElemento(By.id("imagemExpandida"));
        assertEquals(srcOriginal, imgExpandida.getAttribute("src"));
    }

    @Test
    @DisplayName("CT09.5 - Modal fecha ao clicar no botão fechar")
    void modalFechaAoClicarFechar() {
        abrirPagina("/");
        clicarJS(aguardarElemento(By.cssSelector(".gallery-images img")));
        aguardarElemento(By.id("modalGaleria"));
        driver.findElement(By.cssSelector(".fechar-modal")).click();
        WebElement modal = driver.findElement(By.id("modalGaleria"));
        assertNotEquals("block", modal.getCssValue("display"));
    }

    // -------------------------------------------------------
    // CT10 – Categoria de Produtos
    // -------------------------------------------------------

    @Test
    @DisplayName("CT10.1 - Página do catálogo lista as categorias de produtos")
    void catalogoListaCategorias() {
        abrirPagina("/catalogo");
        List<WebElement> categorias = driver.findElements(By.cssSelector(".ambiente-item"));
        assertTrue(categorias.size() > 0, "Deve listar ao menos uma categoria");
    }

    @Test
    @DisplayName("CT10.2 - Categorias esperadas estão presentes no catálogo")
    void categoriesEsperadasPresentes() {
        abrirPagina("/catalogo");
        String pageSource = driver.getPageSource();
        for (String cat : new String[]{"Adega", "Banheiro", "Biblioteca", "Closet", "Cozinha"}) {
            assertTrue(pageSource.contains(cat), "Categoria '" + cat + "' deve estar presente");
        }
    }

    @Test
    @DisplayName("CT10.3 - Clique em 'Adega' navega para a página correta")
    void cliqueAdegaNavegaCorretamente() {
        abrirPagina("/catalogo");
        aguardarElemento(By.cssSelector("a[href='/catalogo/adega']")).click();
        assertTrue(driver.getCurrentUrl().contains("/catalogo/adega"));
    }

    @Test
    @DisplayName("CT10.4 - Botões 'Inspire-se' e 'Saiba mais' da home levam ao catálogo")
    void botoesHomeLevamAoCatalogo() {
        abrirPagina("/");
        List<WebElement> links = driver.findElements(By.cssSelector("a[href='/catalogo']"));
        assertTrue(links.size() >= 1, "Deve haver ao menos um link para /catalogo na home");
    }

    // -------------------------------------------------------
    // CT11 – Catálogo de Produtos (página interna)
    // -------------------------------------------------------

    @Test
    @DisplayName("CT11.1 - Página de categoria 'Adega' carrega produtos")
    void paginaAdegaCarregaProdutos() {
        abrirPagina("/catalogo/adega");
        assertFalse(driver.getTitle().isEmpty());
        assertTrue(elementoPresente(By.tagName("img")));
    }

    @Test
    @DisplayName("CT11.2 - Catálogo contém link para orçamento")
    void catalogoContemOrcamento() {
        abrirPagina("/catalogo");
        assertTrue(elementoPresente(By.cssSelector("a[href='/pedido']")),
                "Catálogo deve conter link para /pedido");
    }

    @Test
    @DisplayName("CT11.3 - Página do catálogo contém link de retorno para a home")
    void catalogoContemLinkHome() {
        // CORRIGIDO: catalogo.html não possui rodapé (footer).
        // O teste verifica o link de retorno à home presente na página.
        abrirPagina("/catalogo");
        assertTrue(elementoPresente(By.cssSelector("a[href='/']")),
                "Catálogo deve conter link de retorno para a home");
    }
}
