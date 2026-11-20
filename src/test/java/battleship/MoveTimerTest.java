package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MoveTimer.
 * Cyclomatic Complexity for each method:
 * - start: 1
 * - stopAndFormat: 2
 */
public class MoveTimerTest {

    private MoveTimer timer;

    @BeforeEach
    void setUp() {
        timer = new MoveTimer();
    }

    @AfterEach
    void tearDown() {
        timer = null;
    }

    @Test
    @DisplayName("Parar sem ter arrancado devolve 00:00:00.000")
    void stopWithoutStart() {
        assertEquals("00:00:00.000", timer.stopAndFormat());
    }

    @Test
    @DisplayName("Arrancar e parar devolve o tempo no formato HH:mm:ss.SSS")
    void startAndStop() {
        timer.start();
        String time = timer.stopAndFormat();
        assertTrue(time.matches("\\d{2}:\\d{2}:\\d{2}\\.\\d{3}"),
                "Formato inválido: " + time);
        assertTrue(time.startsWith("00:00:0"), "Devia ter passado menos de 10 segundos.");
    }
}