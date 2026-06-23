package com.fera.metalurgica.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CT15 - Confirmação de Pedido")
public class CT15_ConfirmacaoPedidoTest extends BaseTest {

    @Test
    @DisplayName("CT15.1 - Cards da confirmação ficam alinhados em desktop")
    void cardsDaConfirmacaoFicamAlinhados() {
        abrirPagina("/pedido/confirmacao?nome=Cliente%20Teste");

        List<WebElement> cards = driver.findElements(By.cssSelector(".confirmacao-info .info-card"));
        assertEquals(3, cards.size(), "A seção de confirmação deve exibir 3 cards");

        int primeiraLinhaY = cards.get(0).getRect().getY();
        int primeiraLargura = cards.get(0).getRect().getWidth();
        int primeiraAltura = cards.get(0).getRect().getHeight();

        for (WebElement card : cards) {
            assertEquals(primeiraLinhaY, card.getRect().getY(), "Os cards devem começar na mesma altura");
            assertEquals(primeiraLargura, card.getRect().getWidth(), "Os cards devem ter a mesma largura");
            assertEquals(primeiraAltura, card.getRect().getHeight(), "Os cards devem ter a mesma altura");
            assertTrue(card.isDisplayed(), "Cada card deve estar visível");
        }
    }
}
