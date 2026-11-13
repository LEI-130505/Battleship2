package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreRecordTest {

    @Test
    @DisplayName("Construtor vazio deixa os campos com valores por defeito")
    void defaultConstructor() {
        ScoreRecord record = new ScoreRecord();

        assertNull(record.getMode());
        assertEquals(0, record.getRounds());
        assertNull(record.getDateTime());
    }

    @Test
    @DisplayName("Construtor com argumentos guarda o modo e as rajadas")
    void constructorStoresModeAndRounds() {
        ScoreRecord record = new ScoreRecord("Normal", 25);

        assertEquals("Normal", record.getMode());
        assertEquals(25, record.getRounds());
    }

    @Test
    @DisplayName("getDateTime devolve data no formato yyyy-MM-dd HH:mm:ss")
    void getDateTimeFormat() {
        ScoreRecord record = new ScoreRecord("Normal", 10);

        assertNotNull(record.getDateTime());
        assertTrue(record.getDateTime().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    @DisplayName("compareTo devolve negativo quando tem menos rajadas")
    void compareToLess() {
        ScoreRecord better = new ScoreRecord("Normal", 10);
        ScoreRecord worse = new ScoreRecord("Normal", 20);

        assertTrue(better.compareTo(worse) < 0);
    }

    @Test
    @DisplayName("compareTo devolve positivo quando tem mais rajadas")
    void compareToGreater() {
        ScoreRecord better = new ScoreRecord("Normal", 10);
        ScoreRecord worse = new ScoreRecord("Normal", 20);

        assertTrue(worse.compareTo(better) > 0);
    }

    @Test
    @DisplayName("compareTo devolve zero quando as rajadas são iguais")
    void compareToEqual() {
        ScoreRecord a = new ScoreRecord("Normal", 15);
        ScoreRecord b = new ScoreRecord("Rápido", 15);

        assertEquals(0, a.compareTo(b));
    }

    @Test
    @DisplayName("toString contém data, modo e rajadas formatados")
    void testToString() {
        ScoreRecord record = new ScoreRecord("Normal", 7);

        String expected = String.format("[%s] Modo: %-10s | Rajadas até vencer: %2d",
                record.getDateTime(), "Normal", 7);

        assertEquals(expected, record.toString());
        assertTrue(record.toString().contains("Modo: Normal"));
        assertTrue(record.toString().contains("Rajadas até vencer:  7"));
    }
}