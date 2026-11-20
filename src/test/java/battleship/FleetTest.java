package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários da classe Fleet")
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
	@DisplayName("Deve inicializar uma frota vazia através do construtor")
	void testConstructor() {
		assertNotNull(fleet, "A instância de Fleet não deve ser nula.");
		assertTrue(fleet.getShips().isEmpty(), "A lista de navios deve começar vazia.");
	}

	@Test
	@DisplayName("Deve gerar uma frota aleatória completa com createRandom")
	void testCreateRandom() {
		IFleet randomFleet = Fleet.createRandom();
		assertNotNull(randomFleet, "A frota gerada aleatoriamente não deve ser nula.");
		assertEquals(11, randomFleet.getShips().size(), "A frota aleatória deve conter 11 navios.");
	}

	@Test
	@DisplayName("Deve adicionar um navio válido dentro do tabuleiro e sem colisões")
	void testAddShipValid() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		assertTrue(fleet.addShip(ship), "Deve permitir adicionar um navio válido.");
		assertEquals(1, fleet.getShips().size(), "A frota deve passar a ter 1 navio.");
	}

	@Test
	@DisplayName("Não deve adicionar navio quando o limite máximo da frota é ultrapassado")
	void testAddShipLimitExceeded() {
		int added = 0;
		for (int row = 0; row < 10 && added <= Fleet.FLEET_SIZE; row += 2) {
			for (int col = 0; col < 10 && added <= Fleet.FLEET_SIZE; col += 2) {
				assertTrue(fleet.addShip(new Barge(Compass.NORTH, new Position(row, col))));
				added++;
			}
		}
		IShip extraShip = new Barge(Compass.NORTH, new Position(8, 8));
		assertFalse(fleet.addShip(extraShip), "Não deve adicionar navio após atingir o limite máximo.");
	}

	@Test
	@DisplayName("Não deve adicionar navio posicionado fora dos limites do tabuleiro")
	void testAddShipOutsideBoard() {
		IShip shipOutside = new Barge(Compass.NORTH, new Position(99, 99));
		assertFalse(fleet.addShip(shipOutside), "Não deve adicionar navio fora do tabuleiro.");
	}

	@Test
	@DisplayName("Não deve adicionar navio quando existe risco de colisão com outro navio")
	void testAddShipCollisionRisk() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship1);
		assertFalse(fleet.addShip(ship2), "Não deve adicionar navio em posição sobreposta.");
	}

	@Test
	@DisplayName("Deve lançar AssertionError ao tentar adicionar um navio nulo")
	void testAddShipNull() {
		assertThrows(AssertionError.class, () -> fleet.addShip(null));
	}

	@Test
	@DisplayName("Deve devolver a lista correta de navios registados na frota")
	void testGetShips() {
		assertTrue(fleet.getShips().isEmpty(), "A frota deve começar vazia.");
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);
		assertEquals(1, fleet.getShips().size(), "A frota deve ter tamanho 1.");
		assertEquals(ship, fleet.getShips().get(0), "O navio devolvido deve corresponder ao adicionado.");
	}

	@Test
	@DisplayName("Deve filtrar e devolver apenas os navios da categoria solicitada")
	void testGetShipsLike() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(3, 1));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		List<IShip> barges = fleet.getShipsLike("Barca");
		assertEquals(1, barges.size(), "Deve existir exatamente 1 navio da categoria 'Barca'.");
		assertEquals(ship1, barges.get(0), "O navio devolvido deve ser a barca adicionada.");
	}

	@Test
	@DisplayName("Deve lançar AssertionError ao pesquisar navios por categoria nula")
	void testGetShipsLikeNull() {
		assertThrows(AssertionError.class, () -> fleet.getShipsLike(null));
	}

	@Test
	@DisplayName("Deve devolver apenas os navios que ainda estão a flutuar")
	void testGetFloatingShips() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		assertEquals(2, fleet.getFloatingShips().size(), "Inicialmente ambos os navios devem flutuar.");

		ship1.getPositions().get(0).shoot();
		List<IShip> floatingShips = fleet.getFloatingShips();
		assertEquals(1, floatingShips.size(), "Apenas um navio deve continuar a flutuar.");
		assertEquals(ship2, floatingShips.get(0), "O navio a flutuar deve ser ship2.");
	}

	@Test
	@DisplayName("Deve devolver apenas os navios que já foram afundados")
	void testGetSunkShips() {
		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
		fleet.addShip(ship1);
		fleet.addShip(ship2);

		assertTrue(fleet.getSunkShips().isEmpty(), "Inicialmente nenhum navio deve estar afundado.");

		ship1.getPositions().get(0).shoot();
		List<IShip> sunkShips = fleet.getSunkShips();
		assertEquals(1, sunkShips.size(), "Deve existir exatamente 1 navio afundado.");
		assertEquals(ship1, sunkShips.get(0), "O navio afundado deve ser ship1.");
	}

	@Test
	@DisplayName("Deve identificar corretamente o navio numa posição ocupada ou devolver null se vazia")
	void testShipAt() {
		IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		assertEquals(ship, fleet.shipAt(new Position(1, 1)), "Deve devolver o navio na posição (1,1).");
		assertNull(fleet.shipAt(new Position(5, 5)), "Deve devolver null para uma posição livre.");
	}

	@Test
	@DisplayName("Deve lançar AssertionError ao verificar navio numa posição nula")
	void testShipAtNull() {
		assertThrows(AssertionError.class, () -> fleet.shipAt(null));
	}

	@Test
	@DisplayName("Deve detetar corretamente o risco de colisão entre navios através de reflexão")
	void testColisionRisk() throws Exception {
		var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
		method.setAccessible(true);

		IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship1);

		assertTrue((Boolean) method.invoke(fleet, ship2), "Deve detetar risco de colisão em posições sobrepostas.");
		assertFalse((Boolean) method.invoke(fleet, new Barge(Compass.NORTH, new Position(5, 5))),
				"Não deve haver risco de colisão em posições afastadas.");
	}

	@Test
	@DisplayName("Deve lançar AssertionError ao verificar risco de colisão com navio nulo")
	void testColisionRiskNull() throws Exception {
		var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
		method.setAccessible(true);

		var exception = assertThrows(InvocationTargetException.class,
				() -> method.invoke(fleet, (Object) null));
		assertInstanceOf(AssertionError.class, exception.getCause());
	}

	@Test
	@DisplayName("Deve executar todos os métodos de impressão da frota sem lançar exceções")
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
	@DisplayName("Deve lançar AssertionError ao passar argumentos nulos nos métodos de impressão")
	void testPrintNullAssertions() {
		assertThrows(AssertionError.class, () -> fleet.printShips(null));
		assertThrows(AssertionError.class, () -> fleet.printShipsByCategory(null));
	}
}