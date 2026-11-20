package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Fleet with 100% line and branch coverage.
 */
public class FleetTest {

	private Fleet fleet;

	@BeforeEach
	void setUp() {
		fleet = new Fleet();
	}

	@AfterEach
	void tearDown() {
		fleet = null;
	}

	@Test
	void testConstructor() {
		assertNotNull(fleet, "Error: Instance of Fleet should not be null.");
		assertTrue(fleet.getShips().isEmpty(), "Error: Fleet should be initialized with empty ships list.");
	}

	@Test
	void testCreateRandom() {
		IFleet r = Fleet.createRandom();
		assertNotNull(r, "Error: createRandom should return a non-null fleet instance.");
		assertEquals(11, r.getShips().size(), "Error: createRandom should create a fleet with 11 ships.");
	}

	@Test
	void testAddShip1() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		assertTrue(fleet.addShip(ship), "Error: Valid ship should be added successfully.");
		assertEquals(1, fleet.getShips().size(), "Error: Fleet should contain one ship after addition.");
	}

	@Test
	void testAddShip2() {
		// Fleet.addShip checks (ships.size() <= FLEET_SIZE), so it allows FLEET_SIZE + 1 ships.
		// Place barges spaced out (even rows and columns) to avoid collision and stay within a 10x10 board.
		int added = 0;
		for (int row = 0; row < 10 && added <= Fleet.FLEET_SIZE; row += 2) {
			for (int col = 0; col < 10 && added <= Fleet.FLEET_SIZE; col += 2) {
				assertTrue(fleet.addShip(new Barge(Compass.NORTH, new Position(row, col))));
				added++;
			}
		}
		IShip extraShip = new Barge(Compass.NORTH, new Position(8, 8));
		assertFalse(fleet.addShip(extraShip), "Error: Should not add ship when fleet size limit is exceeded.");
	}

	@Test
	void testAddShip3() {
		IShip shipOutside = new Barge(Compass.NORTH, new Position(99, 99));
		assertFalse(fleet.addShip(shipOutside), "Error: Should not add ship outside the board.");
	}

	@Test
	void testAddShip4() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1)); // Overlapping position
		fleet.addShip(ship1);
		assertFalse(fleet.addShip(ship2), "Error: Should not add ship with a collision risk.");
	}

	@Test
	void testAddShipNull() {
		assertThrows(AssertionError.class, () -> fleet.addShip(null));
	}

	@Test
	void testGetShips() {
		assertTrue(fleet.getShips().isEmpty(), "Error: Fleet's ships list should initially be empty.");
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);
		assertEquals(1, fleet.getShips().size(), "Error: Fleet should have size 1 after adding a ship.");
		assertEquals(ship, fleet.getShips().get(0), "Error: Fleet's first ship should match the added ship.");
	}

	@Test
	void testGetShipsLike() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(3, 1));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		List<IShip> barges = fleet.getShipsLike("Barca");
		assertEquals(1, barges.size(), "Error: There should be exactly one ship of category 'Barca'.");
		assertEquals(ship1, barges.get(0), "Error: The ship of category 'Barca' does not match.");
	}

	@Test
	void testGetShipsLikeNull() {
		assertThrows(AssertionError.class, () -> fleet.getShipsLike(null));
	}

	@Test
	void testGetFloatingShips() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		List<IShip> floatingShips = fleet.getFloatingShips();
		assertEquals(2, floatingShips.size(), "Error: All ships should be floating initially.");

		ship1.getPositions().get(0).shoot(); // Sink ship1
		floatingShips = fleet.getFloatingShips();
		assertEquals(1, floatingShips.size(), "Error: Only one ship should be floating after sinking one.");
		assertEquals(ship2, floatingShips.get(0), "Error: The floating ship should match the expected result.");
	}

	@Test
	void testGetSunkShips() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		assertTrue(fleet.getSunkShips().isEmpty(), "Error: No ships should be sunk initially.");

		ship1.getPositions().get(0).shoot(); // Sink ship1
		List<IShip> sunkShips = fleet.getSunkShips();
		assertEquals(1, sunkShips.size(), "Error: Only one ship should be sunk.");
		assertEquals(ship1, sunkShips.get(0), "Error: The sunk ship should match ship1.");
	}

	@Test
	void testShipAt() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		assertEquals(ship, fleet.shipAt(new Position(1, 1)), "Error: Should return the correct ship at the position.");
		assertNull(fleet.shipAt(new Position(5, 5)), "Error: Should return null for empty positions in the fleet.");
	}

	@Test
	void testShipAtNull() {
		assertThrows(AssertionError.class, () -> fleet.shipAt(null));
	}

	@Test
	void testColisionRisk() throws Exception {
		var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
		method.setAccessible(true);

		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1)); // Overlapping position
		fleet.addShip(ship1);

		assertTrue((Boolean) method.invoke(fleet, ship2), "Error: Overlapping ships should be at collision risk.");
		assertFalse((Boolean) method.invoke(fleet, new Barge(Compass.NORTH, new Position(5, 5))),
				"Error: Ships at non-overlapping positions should not have a collision risk.");
	}

	@Test
	void testColisionRiskNull() throws Exception {
		var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
		method.setAccessible(true);

		var exception = assertThrows(java.lang.reflect.InvocationTargetException.class,
				() -> method.invoke(fleet, (Object) null));
		assertInstanceOf(AssertionError.class, exception.getCause());
	}

	@Test
	void testPrintMethods() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		assertDoesNotThrow(() -> fleet.printShips(fleet.getShips()));
		assertDoesNotThrow(fleet::printStatus);
		assertDoesNotThrow(() -> fleet.printShipsByCategory("Barca"));
		assertDoesNotThrow(fleet::printFloatingShips);
		assertDoesNotThrow(fleet::printAllShips);
	}

	@Test
	void testPrintNullAssertions() {
		assertThrows(AssertionError.class, () -> fleet.printShips(null));
		assertThrows(AssertionError.class, () -> fleet.printShipsByCategory(null));
	}
}