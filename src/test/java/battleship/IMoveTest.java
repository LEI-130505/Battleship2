package battleship;

import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for IMove.
 * Cyclomatic Complexity for each method:
 * - readMove: 2
 */
public class IMoveTest {

    @Test
    @DisplayName("readMove lê 3 tiros do Scanner")
    void readMoveWithShots() {
        Scanner sc = new Scanner("3 1 1 2 2 3 3");
        Move move = IMove.readMove(5, sc);

        assertEquals(5, move.getNumber(), "O número da jogada devia ser 5.");
        List<IPosition> shots = move.getShots();
        assertEquals(3, shots.size(), "Deviam ter sido lidos 3 tiros.");
        assertEquals(new Position(1, 1), shots.get(0));
        assertEquals(new Position(2, 2), shots.get(1));
        assertEquals(new Position(3, 3), shots.get(2));
        assertTrue(move.getShotResults().isEmpty(), "Ainda não há resultados.");
    }

    @Test
    @DisplayName("readMove com 0 tiros devolve jogada sem tiros")
    void readMoveWithoutShots() {
        Scanner sc = new Scanner("0");
        Move move = IMove.readMove(1, sc);

        assertEquals(1, move.getNumber());
        assertTrue(move.getShots().isEmpty(), "Não devia haver tiros.");
    }
}