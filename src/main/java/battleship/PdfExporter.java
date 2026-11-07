package battleship;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exporta as jogadas do jogo (as minhas e as do inimigo) para um ficheiro PDF,
 * em forma de tabela com as colunas Rajada, Coordenadas e Resultado.
 */
public class PdfExporter {

    /** Nome por omissão do ficheiro gerado. */
    public static final String DEFAULT_FILE = "relatorio_batalhanaval.pdf";

    /** Numero maximo de paginas do relatorio. */
    private static final int maxPages = 50;

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 16;
    private static final float FONT_SIZE = 11;
    private static final float TITLE_SIZE = 16;
    private static final float[] COLUMN_X = {MARGIN, MARGIN + 80, MARGIN + 200};
    private static final String[] TABLE_HEADER = {"Rajada", "Coordenadas", "Resultado"};

    private PdfExporter() {
    }

    /**
     * Gera o relatório em PDF.
     *
     * @param game     o jogo cujas jogadas serão exportadas
     * @param fileName nome do ficheiro PDF a criar
     * @throws IOException se não for possível escrever o ficheiro
     */
    public static void export(Game game, String fileName) throws IOException {
        try (PDDocument doc = new PDDocument();
             PageWriter writer = new PageWriter(doc)) {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            writer.title("Relatorio de Batalha Naval");
            writer.text("Gerado em: " + now);
            writer.skipLine();

            addSection(writer, "AS MINHAS JOGADAS", game.getMyMoves());
            addSection(writer, "JOGADAS DO INIMIGO", game.getAlienMoves());

            writer.close(); // a página tem de estar fechada antes de gravar
            doc.save(fileName);
        }
    }

    private static void addSection(PageWriter writer, String title, List<IMove> moves) throws IOException {
        writer.subtitle(title + " (" + countShots(moves) + " tiros)");
        if (moves.isEmpty()) {
            writer.text("   (sem jogadas)");
        } else {
            writer.startTable(TABLE_HEADER);
            for (IMove move : moves) {
                addMove(writer, move);
            }
            writer.endTable();
        }
        writer.skipLine();
    }

    private static void addMove(PageWriter writer, IMove move) throws IOException {
        List<IPosition> shots = move.getShots();
        List<IGame.ShotResult> results = move.getShotResults();
        int count = Math.min(shots.size(), results.size());
        for (int i = 0; i < count; i++) {
            writer.row(new String[]{
                    String.valueOf(move.getNumber()),
                    String.valueOf(shots.get(i)),
                    describe(results.get(i))});
        }
    }

    private static int countShots(List<IMove> moves) {
        int total = 0;
        for (IMove move : moves) {
            total += move.getShots().size();
        }
        return total;
    }

    private static String describe(IGame.ShotResult result) {
        if (!result.valid())
            return "Fora do tabuleiro";
        if (result.repeated())
            return "Tiro repetido";
        if (result.ship() == null)
            return "Agua";
        if (result.sunk())
            return "Navio afundado (" + result.ship().getCategory() + ")";
        return "Navio atingido (" + result.ship().getCategory() + ")";
    }

    /**
     * Escreve texto e linhas de tabela no PDF, mudando de página quando necessário
     * e repetindo o cabeçalho da tabela no topo de cada página nova.
     */
    private static final class PageWriter implements Closeable {
        private final PDDocument doc;
        private final PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        private final PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        private PDPageContentStream content;
        private float y;
        private String[] tableHeader;

        PageWriter(PDDocument doc) {
            this.doc = doc;
        }

        void title(String text) throws IOException {
            line(text, bold, TITLE_SIZE);
        }

        void subtitle(String text) throws IOException {
            line(text, bold, FONT_SIZE);
        }

        void text(String text) throws IOException {
            line(text, regular, FONT_SIZE);
        }

        void skipLine() {
            y -= LINE_HEIGHT;
        }

        void startTable(String[] header) throws IOException {
            tableHeader = header;
            drawRow(header, bold);
        }

        void endTable() {
            tableHeader = null;
        }

        void row(String[] cells) throws IOException {
            if (ensurePage() && tableHeader != null) {
                drawRow(tableHeader, bold);
            }
            drawRow(cells, regular);
        }

        private void line(String text, PDType1Font font, float size) throws IOException {
            ensurePage();
            write(text, font, size, MARGIN);
            y -= LINE_HEIGHT;
        }

        private void drawRow(String[] cells, PDType1Font font) throws IOException {
            ensurePage();
            for (int i = 0; i < cells.length; i++) {
                write(cells[i], font, FONT_SIZE, COLUMN_X[i]);
            }
            y -= LINE_HEIGHT;
        }

        private void write(String text, PDType1Font font, float size, float x) throws IOException {
            content.beginText();
            content.setFont(font, size);
            content.newLineAtOffset(x, y);
            content.showText(text);
            content.endText();
        }

        /** Abre uma página nova se ainda não houver nenhuma ou se a atual estiver cheia. */
        private boolean ensurePage() throws IOException {
            if (content != null && y >= MARGIN) {
                return false;
            }
            close();
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            content = new PDPageContentStream(doc, page);
            y = page.getMediaBox().getHeight() - MARGIN;
            return true;
        }

        @Override
        public void close() throws IOException {
            if (content != null) {
                content.close();
                content = null;
            }
        }
    }
}