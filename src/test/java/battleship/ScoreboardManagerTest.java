package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreboardManagerTest {

    private static final Path FILE = Path.of("scoreboard.json");

    private byte[] backup;
    private PrintStream originalOut;
    private PrintStream originalErr;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() throws IOException {
        // Guarda o scoreboard real (se existir) e começa cada teste sem ficheiro
        backup = Files.isRegularFile(FILE) ? Files.readAllBytes(FILE) : null;
        deleteFileOrDirectory();

        // Captura o que é escrito na consola
        originalOut = System.out;
        originalErr = System.err;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setOut(originalOut);
        System.setErr(originalErr);

        deleteFileOrDirectory();
        if (backup != null) {
            Files.write(FILE, backup);
        }
    }

    private void deleteFileOrDirectory() throws IOException {
        Files.deleteIfExists(FILE);
    }

    private String consoleOutput() {
        return outContent.toString(StandardCharsets.UTF_8);
    }

    // ---------- loadScores ----------

    @Test
    @DisplayName("loadScores devolve lista vazia quando o ficheiro não existe")
    void loadScoresNoFile() {
        assertTrue(ScoreboardManager.loadScores().isEmpty());
    }

    @Test
    @DisplayName("loadScores devolve os resultados guardados no ficheiro")
    void loadScoresWithData() {
        ScoreboardManager.saveScore("Normal", 20);
        ScoreboardManager.saveScore("Rapido", 10);

        List<ScoreRecord> scores = ScoreboardManager.loadScores();

        assertEquals(2, scores.size());
        assertEquals("Rapido", scores.get(0).getMode());
        assertEquals(10, scores.get(0).getRounds());
        assertEquals(20, scores.get(1).getRounds());
    }

    @Test
    @DisplayName("loadScores devolve lista vazia quando o ficheiro está vazio")
    void loadScoresEmptyFile() throws IOException {
        Files.writeString(FILE, "");

        assertTrue(ScoreboardManager.loadScores().isEmpty());
    }

    @Test
    @DisplayName("loadScores devolve lista vazia quando o JSON é inválido")
    void loadScoresInvalidJson() throws IOException {
        Files.writeString(FILE, "isto nao e json {{{");

        assertTrue(ScoreboardManager.loadScores().isEmpty());
    }

    @Test
    @DisplayName("loadScores devolve lista vazia quando não consegue ler o ficheiro")
    void loadScoresUnreadable() throws IOException {
        Files.createDirectory(FILE); // uma pasta não pode ser lida como ficheiro

        assertTrue(ScoreboardManager.loadScores().isEmpty());
    }

    // ---------- saveScore ----------

    @Test
    @DisplayName("saveScore cria o ficheiro e guarda o resultado")
    void saveScoreCreatesFile() {
        ScoreboardManager.saveScore("Normal", 15);

        assertTrue(Files.isRegularFile(FILE));
        List<ScoreRecord> scores = ScoreboardManager.loadScores();
        assertEquals(1, scores.size());
        assertEquals("Normal", scores.get(0).getMode());
        assertEquals(15, scores.get(0).getRounds());
        assertTrue(consoleOutput().contains("Resultado guardado no Scoreboard (15 rajadas)"));
    }

    @Test
    @DisplayName("saveScore mantém os resultados ordenados por menos rajadas")
    void saveScoreKeepsSorted() {
        ScoreboardManager.saveScore("Normal", 30);
        ScoreboardManager.saveScore("Normal", 10);
        ScoreboardManager.saveScore("Normal", 20);

        List<ScoreRecord> scores = ScoreboardManager.loadScores();

        assertEquals(3, scores.size());
        assertEquals(10, scores.get(0).getRounds());
        assertEquals(20, scores.get(1).getRounds());
        assertEquals(30, scores.get(2).getRounds());
    }

    @Test
    @DisplayName("saveScore não lança exceção quando não consegue escrever")
    void saveScoreWriteError() throws IOException {
        Files.createDirectory(FILE); // escrever numa pasta falha

        assertDoesNotThrow(() -> ScoreboardManager.saveScore("Normal", 5));
        assertFalse(consoleOutput().contains("Resultado guardado"));
    }

    // ---------- printScoreboard ----------

    @Test
    @DisplayName("printScoreboard indica que não há jogos quando está vazio")
    void printScoreboardEmpty() {
        ScoreboardManager.printScoreboard();

        String out = consoleOutput();
        assertTrue(out.contains("SCOREBOARD"));
        assertTrue(out.contains("Ainda não existem jogos registados no histórico."));
    }

    @Test
    @DisplayName("printScoreboard lista os resultados numerados e ordenados")
    void printScoreboardWithData() {
        ScoreboardManager.saveScore("Normal", 20);
        ScoreboardManager.saveScore("Rapido", 10);
        outContent.reset();

        ScoreboardManager.printScoreboard();

        String out = consoleOutput();
        assertTrue(out.contains("1º - "));
        assertTrue(out.contains("2º - "));
        assertFalse(out.contains("Ainda não existem jogos"));
        assertTrue(out.indexOf("Rapido") < out.indexOf("Normal"));
    }

    // ---------- getScoreboardAsString ----------

    @Test
    @DisplayName("getScoreboardAsString devolve mensagem quando está vazio")
    void getScoreboardAsStringEmpty() {
        assertEquals("Ainda nao existem jogos registados.", ScoreboardManager.getScoreboardAsString());
    }

    @Test
    @DisplayName("getScoreboardAsString devolve o top ordenado e numerado")
    void getScoreboardAsStringWithData() {
        ScoreboardManager.saveScore("Normal", 20);
        ScoreboardManager.saveScore("Rapido", 10);

        String result = ScoreboardManager.getScoreboardAsString();

        assertTrue(result.startsWith("=== TOP SCORES ===\n\n"));
        assertTrue(result.contains("1º - "));
        assertTrue(result.contains("2º - "));
        assertTrue(result.indexOf("Rapido") < result.indexOf("Normal"));
        assertTrue(result.endsWith("\n"));
    }

    @Test
    @DisplayName("Construtor por defeito pode ser instanciado")
    void defaultConstructor() {
        assertNotNull(new ScoreboardManager());
    }
}