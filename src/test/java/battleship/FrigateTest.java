package battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Frigate class.
 */
public class FrigateTest {

	private Frigate frigate;

	@BeforeEach
	void setUp() {
		frigate = new Frigate(Compass.NORTH, new Position(5, 5));
	}

	@AfterEach
	void tearDown() {
		frigate = null;
	}

	@Test
	@DisplayName("Construtor com orientação NORTH cria as 4 posições esperadas")
	void testConstructorNorth() {
		assertNotNull(frigate, "Error: Frigate instance should not be null.");
		assertEquals("Fragata", frigate.getCategory(), "Error: Frigate category should be 'Fragata'.");
		assertEquals(Compass.NORTH, frigate.getBearing(), "Error: Frigate bearing is incorrect.");
		assertEquals(4, frigate.getSize(), "Error: Frigate size should be 4.");

		List<IPosition> positions = frigate.getPositions();
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for NORTH.");
		assertEquals(new Position(6, 5), positions.get(1), "Error: Second position is incorrect for NORTH.");
		assertEquals(new Position(7, 5), positions.get(2), "Error: Third position is incorrect for NORTH.");
		assertEquals(new Position(8, 5), positions.get(3), "Error: Fourth position is incorrect for NORTH.");
	}

	@Test
	@DisplayName("Construtor com orientação SOUTH cria as 4 posições esperadas")
	void testConstructorSouth() {
		frigate = new Frigate(Compass.SOUTH, new Position(5, 5));
		List<IPosition> positions = frigate.getPositions();

		assertEquals(Compass.SOUTH, frigate.getBearing(), "Error: Frigate bearing is incorrect.");
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for SOUTH.");
		assertEquals(new Position(6, 5), positions.get(1), "Error: Second position is incorrect for SOUTH.");
		assertEquals(new Position(7, 5), positions.get(2), "Error: Third position is incorrect for SOUTH.");
		assertEquals(new Position(8, 5), positions.get(3), "Error: Fourth position is incorrect for SOUTH.");
	}

	@Test
	@DisplayName("Construtor com orientação EAST cria as 4 posições esperadas")
	void testConstructorEast() {
		frigate = new Frigate(Compass.EAST, new Position(5, 5));
		List<IPosition> positions = frigate.getPositions();

		assertNotNull(frigate, "Error: Frigate instance should not be null.");
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for EAST.");
		assertEquals(new Position(5, 6), positions.get(1), "Error: Second position is incorrect for EAST.");
		assertEquals(new Position(5, 7), positions.get(2), "Error: Third position is incorrect for EAST.");
		assertEquals(new Position(5, 8), positions.get(3), "Error: Fourth position is incorrect for EAST.");
	}

	@Test
	@DisplayName("Construtor com orientação WEST cria as 4 posições esperadas")
	void testConstructorWest() {
		frigate = new Frigate(Compass.WEST, new Position(5, 5));
		List<IPosition> positions = frigate.getPositions();

		assertNotNull(frigate, "Error: Frigate instance should not be null.");
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for WEST.");
		assertEquals(new Position(5, 6), positions.get(1), "Error: Second position is incorrect for WEST.");
		assertEquals(new Position(5, 7), positions.get(2), "Error: Third position is incorrect for WEST.");
		assertEquals(new Position(5, 8), positions.get(3), "Error: Fourth position is incorrect for WEST.");
	}

	@Test
	@DisplayName("getSize devolve 4")
	void testGetSize() {
		assertEquals(4, frigate.getSize(), "Error: Frigate size should be 4.");
	}

	@Test
	@DisplayName("stillFloating é verdadeiro com todas as posições intactas")
	void testStillFloating1() {
		assertTrue(frigate.stillFloating(), "Error: Frigate should still be floating.");
	}

	@Test
	@DisplayName("stillFloating é verdadeiro com apenas uma posição atingida")
	void testStillFloating2() {
		frigate.getPositions().get(0).shoot();
		assertTrue(frigate.stillFloating(), "Error: Frigate should still be floating with one hit position.");
	}

	@Test
	@DisplayName("stillFloating é falso com todas as posições atingidas")
	void testStillFloating3() {
		frigate.getPositions().forEach(IPosition::shoot);
		assertFalse(frigate.stillFloating(), "Error: Frigate should not be floating if all positions are hit.");
	}

	@Test
	@DisplayName("getTopMostPos devolve a linha mais acima")
	void testGetTopMostPos() {
		assertEquals(5, frigate.getTopMostPos(), "Error: The topmost position should be 5.");
	}

	@Test
	@DisplayName("getBottomMostPos devolve a linha mais abaixo")
	void testGetBottomMostPos() {
		assertEquals(8, frigate.getBottomMostPos(), "Error: The bottommost position should be 8.");
	}

	@Test
	@DisplayName("getLeftMostPos devolve a coluna mais à esquerda")
	void testGetLeftMostPos() {
		assertEquals(5, frigate.getLeftMostPos(), "Error: The leftmost position should be 5.");
	}

	@Test
	@DisplayName("getRightMostPos devolve a coluna mais à direita")
	void testGetRightMostPos() {
		assertEquals(5, frigate.getRightMostPos(), "Error: The rightmost position should be 5.");
	}

	@Test
	@DisplayName("Construtor lança NullPointerException com argumentos nulos")
	void testConstructorInvalidInput() {
		assertThrows(NullPointerException.class, () -> new Frigate(null, null),
				"Error: NullPointerException should be thrown for null input.");
		assertThrows(NullPointerException.class, () -> new Frigate(Compass.NORTH, null),
				"Error: NullPointerException should be thrown for null position.");
	}
}