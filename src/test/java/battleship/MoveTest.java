package battleship;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Move.
 * Cyclomatic Complexity for each method:
 * - Move (constructor): 1
 * - toString: 1
 * - getNumber: 1
 * - getShots: 1
 * - getShotResults: 1
 * - processEnemyFire: 26
 */
public class MoveTest {

    private Ship barca;
    private Ship fragata;

    @BeforeEach
    void setUp() {
        barca = new Barge(Compass.NORTH, new Position(1, 1));
        fragata = new Frigate(Compass.EAST, new Position(5, 5));
    }

    @AfterEach
    void tearDown() {
        barca = null;
        fragata = null;
    }

    // Cria um resultado de tiro: (válido, repetido, navio atingido, afundou)
    private IGame.ShotResult shot(boolean valid, boolean repeated, IShip ship, boolean sunk) {
        return new IGame.ShotResult(valid, repeated, ship, sunk);
    }

    private Move move(IGame.ShotResult... results) {
        return new Move(1, new ArrayList<>(), List.of(results));
    }

    @Test
    @DisplayName("Construtor e getters devolvem os valores passados")
    void constructorAndGetters() {
        List<IPosition> shots = new ArrayList<>();
        List<IGame.ShotResult> results = List.of(shot(true, false, null, false));
        Move m = new Move(7, shots, results);
        assertEquals(7, m.getNumber(), "Number should be 7.");
        assertSame(shots, m.getShots(), "Shots list should be the same.");
        assertSame(results, m.getShotResults(), "Results list should be the same.");
    }

    @Test
    @DisplayName("toString mostra número e tamanhos das listas")
    void toStringTest() {
        Move m = new Move(2, new ArrayList<>(),
                List.of(shot(true, false, null, false), shot(true, true, null, false)));
        assertEquals("Move{number=2, shots=0, results=2}", m.toString());
    }

    @Test
    @DisplayName("Lista vazia sem verbose: todos os tiros são exteriores")
    void emptyNotVerbose() {
        String json = move().processEnemyFire(false);
        assertTrue(json.contains("\"validShots\" : 0"));
        assertTrue(json.contains("\"outsideShots\" : " + Game.NUMBER_SHOTS));
    }

    @Test
    @DisplayName("Só tiros inválidos com verbose")
    void allInvalid() {
        String json = move(shot(false, false, null, false),
                shot(false, false, null, false),
                shot(false, false, null, false)).processEnemyFire(true);
        assertTrue(json.contains("\"validShots\" : 0"));
        assertTrue(json.contains("\"outsideShots\" : 3"));
    }

    @Test
    @DisplayName("Dois tiros repetidos e um exterior")
    void twoRepeated() {
        String json = move(shot(true, true, null, false),
                shot(true, true, null, false),
                shot(false, false, null, false)).processEnemyFire(true);
        assertTrue(json.contains("\"repeatedShots\" : 2"));
        assertTrue(json.contains("\"outsideShots\" : 1"));
    }

    @Test
    @DisplayName("Um tiro repetido e dois exteriores")
    void oneRepeated() {
        String json = move(shot(true, true, null, false),
                shot(false, false, null, false),
                shot(false, false, null, false)).processEnemyFire(true);
        assertTrue(json.contains("\"repeatedShots\" : 1"));
        assertTrue(json.contains("\"outsideShots\" : 2"));
    }

    @Test
    @DisplayName("Um tiro na água")
    void oneWater() {
        String json = move(shot(true, false, null, false)).processEnemyFire(true);
        assertTrue(json.contains("\"validShots\" : 1"));
        assertTrue(json.contains("\"missedShots\" : 1"));
    }

    @Test
    @DisplayName("Dois tiros na água e um repetido")
    void twoWaterOneRepeated() {
        String json = move(shot(true, false, null, false),
                shot(true, false, null, false),
                shot(true, true, null, false)).processEnemyFire(true);
        assertTrue(json.contains("\"missedShots\" : 2"));
        assertTrue(json.contains("\"repeatedShots\" : 1"));
    }

    @Test
    @DisplayName("Um tiro na água e dois repetidos")
    void oneWaterTwoRepeated() {
        String json = move(shot(true, false, null, false),
                shot(true, true, null, false),
                shot(true, true, null, false)).processEnemyFire(true);
        assertTrue(json.contains("\"missedShots\" : 1"));
        assertTrue(json.contains("\"repeatedShots\" : 2"));
    }

    @Test
    @DisplayName("Dois acertos no mesmo navio sem afundar e um repetido")
    void hitsNotSunk() {
        String json = move(shot(true, false, barca, false),
                shot(true, false, barca, false),
                shot(true, true, null, false)).processEnemyFire(true);
        assertTrue(json.contains("\"hits\" : 2"));
        assertTrue(json.contains("\"type\" : \"" + barca.getCategory() + "\""));
        assertTrue(json.contains("\"sunkBoats\" : [ ]"));
    }

    @Test
    @DisplayName("Afunda um navio, acerta noutro e falha um tiro")
    void sunkHitAndWater() {
        String json = move(shot(true, false, fragata, false),
                shot(true, false, barca, true),
                shot(true, false, null, false)).processEnemyFire(true);
        assertTrue(json.contains("\"validShots\" : 3"));
        assertTrue(json.contains("\"count\" : 1"));
        assertTrue(json.contains("\"type\" : \"" + barca.getCategory() + "\""));
        assertTrue(json.contains("\"type\" : \"" + fragata.getCategory() + "\""));
    }

    @Test
    @DisplayName("Só um navio afundado, sem tiros na água")
    void onlySunk() {
        String json = move(shot(true, false, barca, true),
                shot(false, false, null, false),
                shot(false, false, null, false)).processEnemyFire(true);
        assertTrue(json.contains("\"count\" : 1"));
        assertTrue(json.contains("\"hitsOnBoats\" : [ ]"));
    }

    @Test
    @DisplayName("Dois navios do mesmo tipo afundados")
    void twoSunkSameType() {
        String json = move(shot(true, false, barca, true),
                shot(true, false, barca, true),
                shot(true, false, null, false)).processEnemyFire(true);
        assertTrue(json.contains("\"count\" : 2"));
    }

    @Test
    @DisplayName("Sem verbose, com acertos e afundados")
    void notVerboseWithHits() {
        String json = move(shot(true, false, fragata, false),
                shot(true, false, barca, true),
                shot(true, true, null, false)).processEnemyFire(false);
        assertTrue(json.contains("\"validShots\" : 2"));
        assertTrue(json.contains("\"repeatedShots\" : 1"));
    }
}