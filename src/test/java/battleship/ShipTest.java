package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Ship.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getCategory: 1
 * - getSize: 1
 * - getBearing: 1
 * - getPositions: 1
 * - stillFloating: 2
 * - shoot: 2
 * - occupies: 2
 * - tooCloseTo (IShip): 2
 * - tooCloseTo (IPosition): 2
 * - getTopMostPos: 2
 * - getBottomMostPos: 2
 * - getLeftMostPos: 2
 * - getRightMostPos: 2
 */
public class ShipTest {

    private Ship ship, shipBorder;

    @BeforeEach
    void setUp() {
        // Since Ship is abstract, instantiate it with a concrete subclass (e.g., Barge)
        ship = new Barge(Compass.NORTH, new Position(5, 5));
        shipBorder = new Barge(Compass.NORTH, new Position(0, 0));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    /**
     * Test for the constructor.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testConstructor() {
        assertNotNull(ship, "Error: Ship instance should not be null.");
        assertEquals("Barca", ship.getCategory(), "Error: Ship category is incorrect.");
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing is incorrect.");
        assertEquals(1, ship.getSize(), "Error: Ship size is incorrect.");
        assertFalse(ship.getPositions().isEmpty(), "Error: Ship positions should not be empty.");
    }

    /**
     * Test for the getCategory method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetCategory() {
        assertEquals("Barca", ship.getCategory(), "Error: Ship category should be 'Barca'.");
    }

    /**
     * Test for the getSize method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetSize() {
        assertEquals(1, ship.getSize(), "Error: Ship size should be 1.");
    }

    /**
     * Test for the getBearing method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetBearing() {
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing should be NORTH.");
    }

    /**
     * Test for the getPositions method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetPositions() {
        List<IPosition> positions = ship.getPositions();
        assertNotNull(positions, "Error: Ship positions should not be null.");
        assertEquals(1, positions.size(), "Error: Ship should have exactly one position.");
        assertEquals(5, positions.get(0).getRow(), "Error: Position's row should be 5.");
        assertEquals(5, positions.get(0).getColumn(), "Error: Position's column should be 5.");
    }

    /**
     * Test for the stillFloating method (all positions intact).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testStillFloating1() {
        assertTrue(!ship.isSunk(), "Error: Ship should still be floating.");
    }

    /**
     * Test for the stillFloating method (all positions hit).
     */
    @Test
    void testStillFloating2() {
        ship.getPositions().get(0).shoot();
        assertFalse(!ship.isSunk(), "Error: Ship should no longer be floating after being hit.");
    }

    /**
     * Test for the shoot method (valid position).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testShoot1() {
        Position target = new Position(5, 5);
        ship.shoot(target);
        assertTrue(ship.getPositions().get(0).isHit(), "Error: Position should be marked as hit.");
    }

    /**
     * Test for the shoot method (invalid position).
     */
    @Test
    void testShoot2() {
        Position target = new Position(0, 0);
        ship.shoot(target); // No exception expected
        assertFalse(ship.getPositions().get(0).isHit(), "Error: Position should not be marked as hit for an invalid target.");
    }

    /**
     * Test for the occupies method (position occupied).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testOccupies1() {
        Position pos = new Position(5, 5);
        assertTrue(ship.occupies(pos), "Error: Ship should occupy position (5, 5).");
    }

    /**
     * Test for the occupies method (position not occupied).
     */
    @Test
    void testOccupies2() {
        Position pos = new Position(1, 1);
        assertFalse(ship.occupies(pos), "Error: Ship should not occupy position (1, 1).");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships too close).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testTooCloseToShip1() {
        Ship nearbyShip = new Barge(Compass.NORTH, new Position(5, 6));
        assertTrue(ship.tooCloseTo(nearbyShip), "Error: Ships should be too close.");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships not close).
     */
    @Test
    void testTooCloseToShip2() {
        Ship farShip = new Barge(Compass.NORTH, new Position(10, 10));
        assertFalse(ship.tooCloseTo(farShip), "Error: Ships should not be too close.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions adjacent).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testTooCloseToPosition1() {
        Position pos = new Position(5, 6); // Adjacent position
        assertTrue(ship.tooCloseTo(pos), "Error: Ship should be too close to the given position.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions not adjacent).
     */
    @Test
    void testTooCloseToPosition2() {
        Position pos = new Position(7, 7); // Non-adjacent position
        assertFalse(ship.tooCloseTo(pos), "Error: Ship should not be too close to the given position.");
    }

    /**
     * Test for the getTopMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetTopMostPos() {
        assertEquals(5, ship.getTopMostPos(), "Error: The topmost position should be 5.");
    }

    /**
     * Test for the getBottomMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetBottomMostPos() {
        assertEquals(5, ship.getBottomMostPos(), "Error: The bottommost position should be 5.");
    }

    /**
     * Test for the getLeftMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetLeftMostPos() {
        assertEquals(5, ship.getLeftMostPos(), "Error: The leftmost position should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetRightMostPos() {
        assertEquals(5, ship.getRightMostPos(), "Error: The rightmost position should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetAdjacentPositions() {
        assertEquals(8, ship.getAdjacentPositions().size(), "Error: The number of adjacent positions of a barge in the middle of the tray should be 8.");
        assertEquals(3, shipBorder.getAdjacentPositions().size(), "Error: The number of adjacent positions of a barge in the middle of the tray should be 8.");
    }

    // ------------------------------------------------------------------
    // Testes adicionais (129835 - Afonso da Cota Bonito)
    // ------------------------------------------------------------------

    /** Cria um navio de teste com as posições dadas, pela ordem indicada. */
    private static Ship customShip(Position... positions) {
        Ship s = new Ship("Teste", Compass.NORTH, positions[0], positions.length) { };
        for (Position p : positions)
            s.getPositions().add(p);
        return s;
    }

    @Test
    @DisplayName("buildShip cria o tipo de navio certo para cada categoria")
    void buildShipKinds() {
        assertInstanceOf(Barge.class, Ship.buildShip("barca", Compass.NORTH, new Position(1, 1)));
        assertInstanceOf(Caravel.class, Ship.buildShip("caravela", Compass.NORTH, new Position(1, 1)));
        assertInstanceOf(Carrack.class, Ship.buildShip("nau", Compass.NORTH, new Position(1, 1)));
        assertInstanceOf(Frigate.class, Ship.buildShip("fragata", Compass.NORTH, new Position(1, 1)));
        assertInstanceOf(Galleon.class, Ship.buildShip("galeao", Compass.NORTH, new Position(1, 1)));
    }

    @Test
    @DisplayName("buildShip devolve null para uma categoria desconhecida")
    void buildShipUnknownKind() {
        assertNull(Ship.buildShip("submarino", Compass.NORTH, new Position(1, 1)));
    }

    @Test
    @DisplayName("buildShip rejeita argumentos nulos (requer assertions ativas: -ea)")
    void buildShipNullArguments() {
        assertThrows(AssertionError.class, () -> Ship.buildShip(null, Compass.NORTH, new Position(1, 1)));
        assertThrows(AssertionError.class, () -> Ship.buildShip("barca", null, new Position(1, 1)));
        assertThrows(AssertionError.class, () -> Ship.buildShip("barca", Compass.NORTH, null));
    }

    @Test
    @DisplayName("O construtor rejeita categoria, rumo ou posição nulos")
    void constructorRejectsNulls() {
        assertThrows(NullPointerException.class, () -> new Ship(null, Compass.NORTH, new Position(0, 0), 1) { });
        assertThrows(NullPointerException.class, () -> new Ship("Teste", null, new Position(0, 0), 1) { });
        assertThrows(NullPointerException.class, () -> new Ship("Teste", Compass.NORTH, null, 1) { });
    }

    @Test
    @DisplayName("getPosition devolve a posição inicial do navio")
    void getPosition() {
        assertEquals(new Position(5, 5), ship.getPosition());
    }

    @Test
    @DisplayName("toString inclui a categoria e a posição do navio")
    void testToString() {
        String text = ship.toString();
        assertTrue(text.startsWith("[Barca"));
        assertTrue(text.contains("F6"));
        assertTrue(text.endsWith("]"));
    }

    @Test
    @DisplayName("sink acerta em todas as posições e o navio deixa de flutuar")
    void sinkMultiPositionShip() {
        Ship big = customShip(new Position(0, 0), new Position(0, 1), new Position(0, 2));
        assertTrue(!big.isSunk());
        big.sink();
        assertFalse(!big.isSunk());
        for (IPosition p : big.getPositions())
            assertTrue(p.isHit());
    }

    @Test
    @DisplayName("Um navio com várias posições só deixa de flutuar quando todas são atingidas")
    void stillFloatingMultiPosition() {
        Ship big = customShip(new Position(0, 0), new Position(0, 1), new Position(0, 2));
        big.shoot(new Position(0, 0));
        assertTrue(!big.isSunk());
        big.shoot(new Position(0, 1));
        assertTrue(!big.isSunk());
        big.shoot(new Position(0, 2));
        assertFalse(!big.isSunk());
    }

    @Test
    @DisplayName("Os extremos são calculados com as posições por ordem crescente")
    void extremesAscendingOrder() {
        Ship big = customShip(new Position(3, 3), new Position(4, 4), new Position(5, 5));
        assertEquals(3, big.getTopMostPos());
        assertEquals(5, big.getBottomMostPos());
        assertEquals(3, big.getLeftMostPos());
        assertEquals(5, big.getRightMostPos());
    }

    @Test
    @DisplayName("Os extremos são calculados com as posições por ordem decrescente")
    void extremesDescendingOrder() {
        Ship big = customShip(new Position(5, 5), new Position(4, 4), new Position(3, 3));
        assertEquals(3, big.getTopMostPos());
        assertEquals(5, big.getBottomMostPos());
        assertEquals(3, big.getLeftMostPos());
        assertEquals(5, big.getRightMostPos());
    }

    @Test
    @DisplayName("Os extremos são calculados com as posições desordenadas")
    void extremesMixedOrder() {
        Ship big = customShip(new Position(5, 5), new Position(3, 7), new Position(8, 2));
        assertEquals(3, big.getTopMostPos());
        assertEquals(8, big.getBottomMostPos());
        assertEquals(2, big.getLeftMostPos());
        assertEquals(7, big.getRightMostPos());
    }

    @Test
    @DisplayName("isInsideBoard é verdadeiro para navios dentro do tabuleiro")
    void isInsideBoardInside() {
        assertTrue(ship.isInsideBoard());
        assertTrue(shipBorder.isInsideBoard());
        assertTrue(customShip(new Position(9, 8), new Position(9, 9)).isInsideBoard());
    }

    @Test
    @DisplayName("isInsideBoard é falso quando o navio sai pela esquerda")
    void isInsideBoardLeft() {
        assertFalse(customShip(new Position(5, -1)).isInsideBoard());
    }

    @Test
    @DisplayName("isInsideBoard é falso quando o navio sai pela direita")
    void isInsideBoardRight() {
        assertFalse(customShip(new Position(5, 10)).isInsideBoard());
    }

    @Test
    @DisplayName("isInsideBoard é falso quando o navio sai por cima")
    void isInsideBoardTop() {
        assertFalse(customShip(new Position(-1, 5)).isInsideBoard());
    }

    @Test
    @DisplayName("isInsideBoard é falso quando o navio sai por baixo")
    void isInsideBoardBottom() {
        assertFalse(customShip(new Position(10, 5)).isInsideBoard());
    }

    @Test
    @DisplayName("occupies procura em todas as posições de um navio grande")
    void occupiesMultiPosition() {
        Ship big = customShip(new Position(2, 2), new Position(2, 3), new Position(2, 4));
        assertTrue(big.occupies(new Position(2, 4)));
        assertFalse(big.occupies(new Position(3, 3)));
    }

    @Test
    @DisplayName("tooCloseTo(IShip) deteta proximidade com qualquer posição do outro navio")
    void tooCloseToShipMultiPosition() {
        Ship big = customShip(new Position(2, 2), new Position(2, 3), new Position(2, 4));
        Ship nearEnd = customShip(new Position(0, 0), new Position(3, 5));
        Ship far = customShip(new Position(8, 8), new Position(9, 9));
        assertTrue(big.tooCloseTo(nearEnd));
        assertFalse(big.tooCloseTo(far));
    }

    @Test
    @DisplayName("tooCloseTo(IPosition) deteta proximidade com qualquer posição do navio")
    void tooCloseToPositionMultiPosition() {
        Ship big = customShip(new Position(2, 2), new Position(2, 3), new Position(2, 4));
        assertTrue(big.tooCloseTo(new Position(3, 5)));
        assertFalse(big.tooCloseTo(new Position(6, 6)));
    }

    @Test
    @DisplayName("getAdjacentPositions não repete posições nem inclui as do próprio navio")
    void adjacentPositionsOfTwoPositionShip() {
        Ship two = customShip(new Position(5, 5), new Position(5, 6));
        List<IPosition> adjacent = two.getAdjacentPositions();
        assertEquals(10, adjacent.size());
        assertFalse(adjacent.contains(new Position(5, 5)));
        assertFalse(adjacent.contains(new Position(5, 6)));
    }

    @Test
    @DisplayName("Os métodos com assert rejeitam argumentos nulos ou fora do tabuleiro (requer -ea)")
    void nullAndOutsideArgumentsAreRejected() {
        assertThrows(AssertionError.class, () -> ship.occupies(null));
        assertThrows(AssertionError.class, () -> ship.tooCloseTo((IShip) null));
        assertThrows(AssertionError.class, () -> ship.tooCloseTo((IPosition) null));
        assertThrows(AssertionError.class, () -> ship.shoot(null));
        assertThrows(AssertionError.class, () -> ship.shoot(new Position(-1, 0)));
    }
}