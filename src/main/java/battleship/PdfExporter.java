package battleship;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PdfExporter {

    public static final String DEFAULT_FILE = "relatorio_batalhanaval.pdf";

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 16;
    private static final float FONT_SIZE = 11;

    private PdfExporter() {
    }

    public static void export(Game game, String fileName) throws IOException {
        List<String> lines = new ArrayList<>();

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        lines.add("Relatorio de Batalha Naval - " + now);
        lines.add("");

        addMoves(lines, "AS MINHAS JOGADAS", game.getMyMoves());
        addMoves(lines, "JOGADAS DO INIMIGO", game.getAlienMoves());

        writeLines(lines, fileName);
    }

    private static void addMoves(List<String> lines, String title, List<IMove> moves) {
        lines.add(title);
        lines.add("");

        if (moves.isEmpty()) {
            lines.add("   (sem jogadas)");
            lines.add("");
            return;
        }

        for (IMove move : moves) {
            lines.add("Rajada n. " + move.getNumber());
            List<IPosition> shots = move.getShots();
            List<IGame.ShotResult> results = move.getShotResults();
            for (int i = 0; i < shots.size(); i++) {
                lines.add("      " + shots.get(i) + "  ->  " + describe(results.get(i)));
            }
            lines.add("");
        }
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

    private static void writeLines(List<String> lines, String fileName) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDPageContentStream content = null;
            float y = 0;

            for (String line : lines) {
                // Nova pagina: no inicio, ou quando ja nao ha espaco
                if (content == null || y < MARGIN) {
                    if (content != null)
                        content.close();
                    PDPage page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    y = page.getMediaBox().getHeight() - MARGIN;
                }

                content.beginText();
                content.setFont(font, FONT_SIZE);
                content.newLineAtOffset(MARGIN, y);
                content.showText(line);
                content.endText();

                y -= LINE_HEIGHT;
            }

            if (content != null)
                content.close();

            doc.save(fileName);
        }
    }
}