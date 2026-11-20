package battleship;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PdfExporter.
 */
public class PdfExporterTest {

    @TempDir
    Path tempDir;

    private Game game;
    private Ship barca;
    private Ship fragata;

    @BeforeEach
    void setUp() {
        game = new Game(new Fleet());
        barca = new Barge(Compass.NORTH, new Position(1, 1));
        fragata = new Frigate(Compass.EAST, new Position(5, 5));
        game.getMyFleet().addShip(barca);
        game.getMyFleet().addShip(fragata);
    }

    @AfterEach
    void tearDown() {
        game = null;
    }

    private String readPdf(File file) throws IOException {
        try (PDDocument doc = Loader.loadPDF(file)) {
            return new PDFTextStripper().getText(doc);
        }
    }

    private int countPages(File file) throws IOException {
        try (PDDocument doc = Loader.loadPDF(file)) {
            return doc.getNumberOfPages();
        }
    }

    @Test
    @DisplayName("Jogo sem jogadas: aparece (sem jogadas)")
    void exportEmptyGame() throws IOException {
        File file = tempDir.resolve("vazio.pdf").toFile();
        PdfExporter.export(game, file.getPath());

        assertTrue(file.exists(), "O PDF devia ter sido criado.");
        String text = readPdf(file);
        assertTrue(text.contains("Relatorio de Batalha Naval"));
        assertTrue(text.contains("(sem jogadas)"));
        assertEquals(1, countPages(file));
    }

    @Test
    @DisplayName("Jogadas com todos os tipos de resultado")
    void exportAllResultTypes() throws IOException {
        // 1ª rajada: afunda a barca, atinge a fragata, água
        game.fireShots(List.of(new Position(1, 1), new Position(5, 5), new Position(0, 0)));
        // 2ª rajada: fora do tabuleiro, repetido, água
        game.fireShots(List.of(new Position(-1, 5), new Position(0, 0), new Position(9, 9)));

        File file = tempDir.resolve("jogo.pdf").toFile();
        PdfExporter.export(game, file.getPath());

        String text = readPdf(file);
        assertTrue(text.contains("Rajada"));
        assertTrue(text.contains("Navio afundado"));
        assertTrue(text.contains("Navio atingido"));
        assertTrue(text.contains("Agua"));
        assertTrue(text.contains("Fora do tabuleiro"));
        assertTrue(text.contains("Tiro repetido"));
    }

    @Test
    @DisplayName("Muitas jogadas: o PDF passa para uma segunda página")
    void exportManyMovesCreatesNewPage() throws IOException {
        for (int i = 0; i < 20; i++) {
            game.fireShots(List.of(new Position(0, 0), new Position(0, 2), new Position(0, 3)));
        }

        File file = tempDir.resolve("grande.pdf").toFile();
        PdfExporter.export(game, file.getPath());

        assertTrue(countPages(file) >= 2, "60 linhas não cabem numa página.");
    }

    @Test
    @DisplayName("Caminho inválido lança IOException")
    void exportInvalidPathThrows() {
        String badPath = tempDir.resolve("pasta_que_nao_existe").resolve("x.pdf").toString();
        assertThrows(IOException.class, () -> PdfExporter.export(game, badPath));
    }
}