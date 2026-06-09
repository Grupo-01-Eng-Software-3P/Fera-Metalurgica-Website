package com.fera.metalurgica.service;

import com.fera.metalurgica.model.ItemPedido;
import com.fera.metalurgica.model.Pedido;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class OrcamentoPdfService {

    private static final float PAGE_MARGIN = 42f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - (PAGE_MARGIN * 2);
    private static final float LINE_GAP = 14f;
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public byte[] gerarPdf(Pedido pedido) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            try (PdfWriter writer = new PdfWriter(document)) {
                writer.writeCabecalho(pedido);
                writer.writeSection("Dados do cliente");
                writer.writeLinha("Cliente: " + valorOuVazio(pedido.getCliente()));
                writer.writeLinha("Telefone: " + valorOuVazio(pedido.getTelefone()));
                writer.writeLinha("CPF/CNPJ: " + mascararCpf(pedido.getCpf()));
                writer.writeLinha("Material: " + valorOuVazio(pedido.getMaterial()));
                writer.writeLinha("Medidas: " + valorOuVazio(pedido.getMedidas()));
                writer.writeLinha("Origem do pedido: " + valorOuVazio(pedido.getCriadoPor()));
                writer.writeLinha("Descricao do pedido: " + valorOuVazio(pedido.getDescricao()));

                writer.writeSection("Itens orcados");
                List<ItemPedido> itens = pedido.getItens() == null ? List.of() : pedido.getItens();
                if (itens.isEmpty()) {
                    writer.writeParagrafo("Nenhum item foi incluido neste orcamento.");
                } else {
                    int indice = 1;
                    for (ItemPedido item : itens) {
                        writer.writeItem(indice++, item);
                    }
                }

                writer.writeSection("Resumo financeiro");
                writer.writeLinha("Valor dos materiais: " + formatarMoeda(pedido.getValorTotalMateriais()));
                writer.writeLinha("Valor dos adicionais: " + formatarMoeda(pedido.getValorAdicionais()));
                writer.writeLinha("Valor total: " + formatarMoeda(pedido.getValorTotal()), true);

                if (pedido.getObservacoesAdmin() != null && !pedido.getObservacoesAdmin().isBlank()) {
                    writer.writeSection("Observacoes do orcamento");
                    writer.writeParagrafo(pedido.getObservacoesAdmin());
                }

                writer.writeRodape("Documento gerado para envio ao cliente.");
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel gerar o PDF do orcamento.", e);
        }
    }

    private String valorOuVazio(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private String formatarMoeda(java.math.BigDecimal valor) {
        return MOEDA.format(valor == null ? java.math.BigDecimal.ZERO : valor);
    }

    private String mascararCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return "-";
        }

        String digitos = cpf.replaceAll("[^0-9]", "");
        if (digitos.isBlank()) {
            return "-";
        }

        if (digitos.length() <= 2) {
            return "*".repeat(digitos.length());
        }

        String ultimosDigitos = digitos.substring(digitos.length() - 2);
        return "***.***.***-" + ultimosDigitos;
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        return texto
                .replace('\u2013', '-')
                .replace('\u2014', '-')
                .replace('\u2018', '\'')
                .replace('\u2019', '\'')
                .replace('\u201C', '"')
                .replace('\u201D', '"');
    }

    private String removerAcentos(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private final class PdfWriter implements AutoCloseable {
        private final PDDocument document;
        private final PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        private final PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        private PDPageContentStream contentStream;
        private PDPage page;
        private float cursorY;

        private PdfWriter(PDDocument document) throws IOException {
            this.document = document;
            novaPagina();
        }

        private void writeCabecalho(Pedido pedido) throws IOException {
            writeTexto("FERA METALURGICA E MOVEIS INDUSTRIAIS", boldFont, 18f, PAGE_MARGIN, cursorY);
            cursorY -= 18f;
            writeTexto("Orcamento do pedido #" + (pedido.getId() != null ? pedido.getId() : "-"), boldFont, 13f, PAGE_MARGIN, cursorY);
            cursorY -= 12f;

            String data = pedido.getDataCriacao() == null
                    ? DATA_HORA.format(LocalDateTime.now())
                    : DATA_HORA.format(pedido.getDataCriacao());
            writeTexto("Emitido em: " + data, regularFont, 10f, PAGE_MARGIN, cursorY);
            cursorY -= 12f;

            drawLinhaHorizontal();
            cursorY -= 16f;
        }

        private void writeSection(String titulo) throws IOException {
            ensureSpace(30f);
            writeTexto(titulo.toUpperCase(Locale.ROOT), boldFont, 12f, PAGE_MARGIN, cursorY);
            cursorY -= 8f;
            drawLinhaHorizontal();
            cursorY -= 14f;
        }

        private void writeLinha(String texto) throws IOException {
            writeLinha(texto, false);
        }

        private void writeLinha(String texto, boolean destaque) throws IOException {
            List<String> linhas = quebrarTexto(texto, destaque ? boldFont : regularFont, destaque ? 11.5f : 10.5f, CONTENT_WIDTH);
            for (String linha : linhas) {
                ensureSpace(LINE_GAP);
                writeTexto(linha, destaque ? boldFont : regularFont, destaque ? 11.5f : 10.5f, PAGE_MARGIN, cursorY);
                cursorY -= LINE_GAP;
            }
        }

        private void writeParagrafo(String texto) throws IOException {
            List<String> linhas = quebrarTexto(texto, regularFont, 10.5f, CONTENT_WIDTH);
            for (String linha : linhas) {
                ensureSpace(LINE_GAP);
                writeTexto(linha, regularFont, 10.5f, PAGE_MARGIN, cursorY);
                cursorY -= LINE_GAP;
            }
            cursorY -= 4f;
        }

        private void writeItem(int indice, ItemPedido item) throws IOException {
            ensureSpace(38f);
            String nome = item.getNomeItem() == null || item.getNomeItem().isBlank() ? "Item sem nome" : item.getNomeItem();
            String qtd = item.getQuantidade() == null ? "-" : String.valueOf(item.getQuantidade());
            String subtotal = formatarMoeda(item.getSubtotal());
            String valorUnitario = formatarMoeda(item.getValorUnitario());

            writeTexto(indice + ". " + nome, boldFont, 11f, PAGE_MARGIN, cursorY);
            cursorY -= LINE_GAP;
            writeLinha("Quantidade: " + qtd + " | Valor unitario: " + valorUnitario + " | Subtotal: " + subtotal);
            cursorY -= 4f;
        }

        private void writeRodape(String texto) throws IOException {
            ensureSpace(20f);
            drawLinhaHorizontal();
            cursorY -= 10f;
            writeTexto(texto, regularFont, 9f, PAGE_MARGIN, cursorY);
            cursorY -= 12f;
        }

        private void drawLinhaHorizontal() throws IOException {
            contentStream.moveTo(PAGE_MARGIN, cursorY);
            contentStream.lineTo(PAGE_WIDTH - PAGE_MARGIN, cursorY);
            contentStream.stroke();
        }

        private void ensureSpace(float alturaNecessaria) throws IOException {
            if (cursorY - alturaNecessaria < PAGE_MARGIN) {
                novaPagina();
            }
        }

        private void novaPagina() throws IOException {
            if (contentStream != null) {
                contentStream.close();
            }
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            contentStream = new PDPageContentStream(document, page);
            cursorY = PAGE_HEIGHT - PAGE_MARGIN;
        }

        private void writeTexto(String texto, PDFont font, float tamanho, float x, float y) throws IOException {
            String conteudo = normalizarTexto(texto);
            contentStream.beginText();
            try {
                contentStream.setFont(font, tamanho);
                contentStream.newLineAtOffset(x, y);
                try {
                    contentStream.showText(conteudo);
                } catch (IllegalArgumentException ex) {
                    contentStream.showText(removerAcentos(conteudo));
                }
            } finally {
                contentStream.endText();
            }
        }

        private List<String> quebrarTexto(String texto, PDFont font, float tamanho, float larguraMaxima) throws IOException {
            String conteudo = normalizarTexto(texto);
            if (conteudo == null || conteudo.isBlank()) {
                return List.of("-");
            }

            List<String> linhas = new ArrayList<>();
            for (String paragrafo : conteudo.split("\\R", -1)) {
                if (paragrafo.isBlank()) {
                    linhas.add("");
                    continue;
                }

                StringBuilder linhaAtual = new StringBuilder();
                for (String palavra : paragrafo.split("\\s+")) {
                    if (palavra.isBlank()) {
                        continue;
                    }

                    String candidata = linhaAtual.length() == 0
                            ? palavra
                            : linhaAtual + " " + palavra;

                    if (larguraTexto(candidata, font, tamanho) <= larguraMaxima) {
                        linhaAtual.setLength(0);
                        linhaAtual.append(candidata);
                        continue;
                    }

                    if (linhaAtual.length() > 0) {
                        linhas.add(linhaAtual.toString());
                        linhaAtual.setLength(0);
                    }

                    if (larguraTexto(palavra, font, tamanho) <= larguraMaxima) {
                        linhaAtual.append(palavra);
                    } else {
                        quebrarPalavraLonga(palavra, font, tamanho, larguraMaxima, linhas);
                    }
                }

                if (linhaAtual.length() > 0) {
                    linhas.add(linhaAtual.toString());
                }
            }

            return linhas.isEmpty() ? List.of("-") : linhas;
        }

        private void quebrarPalavraLonga(String palavra,
                                         PDFont font,
                                         float tamanho,
                                         float larguraMaxima,
                                         List<String> linhas) throws IOException {
            StringBuilder segmento = new StringBuilder();
            for (char caractere : palavra.toCharArray()) {
                String candidata = segmento + String.valueOf(caractere);
                if (larguraTexto(candidata, font, tamanho) <= larguraMaxima) {
                    segmento.append(caractere);
                } else {
                    if (segmento.length() > 0) {
                        linhas.add(segmento.toString());
                    }
                    segmento.setLength(0);
                    segmento.append(caractere);
                }
            }

            if (segmento.length() > 0) {
                linhas.add(segmento.toString());
            }
        }

        private float larguraTexto(String texto, PDFont font, float tamanho) throws IOException {
            String conteudo = normalizarTexto(texto);
            return font.getStringWidth(conteudo) / 1000f * tamanho;
        }

        @Override
        public void close() throws IOException {
            if (contentStream != null) {
                contentStream.close();
            }
        }
    }
}
