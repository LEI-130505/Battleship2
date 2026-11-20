package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários da classe Tasks")
class TasksTest {

	private InputStream originalIn;

	@BeforeEach
	void setUp() {
		originalIn = System.in;
	}

	@AfterEach
	void tearDown() {
		System.setIn(originalIn);
	}

	private void provideInput(String data) {
		System.setIn(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
	}

	private String getBuildFleetInput() {
		return String.join("\n",
				"navio_invalido 0 0 n", // Navio desconhecido (s == null)
				"barca 0 0 n",          // 1
				"barca 0 0 n",          // Colisão (fleet.addShip == false)
				"barca 0 2 n",          // 2
				"barca 0 4 n",          // 3
				"barca 0 6 n",          // 4
				"barca 0 8 n",          // 5
				"caravela 2 0 e",       // 6
				"caravela 2 3 e",       // 7
				"caravela 2 6 e",       // 8
				"nau 4 0 e",            // 9
				"nau 4 4 e",            // 10
				"fragata 6 0 e",        // 11
				"galeao 8 0 e",         // 12
				"barca 6 6 n",          // 13
				"barca 6 8 n",          // 14
				"barca 8 6 n",          // 15
				"barca 8 8 n"           // 16
		) + "\n";
	}

	@Test
	@DisplayName("Deve instanciar a classe Tasks sem erros")
	void testConstructor() {
		assertNotNull(new Tasks());
	}

	@Test
	@DisplayName("Deve processar comandos do menu antes e depois de gerar uma frota aleatória")
	void testMenuNullGuardsAndCommands() {
		String commands = String.join("\n",
				"ajuda",
				"estado",           // myFleet == null
				"mapa",             // myFleet == null
				"rajada",           // game == null
				"simula",           // game == null
				"tiros",            // game == null
				"scoreboard",
				"comando_invalido",
				"gerafrota",        // Inicializa myFleet e game
				"estado",           // myFleet != null
				"mapa",             // myFleet != null
				"tiros",            // game != null
				"rajada A1 A2 A3",  // Dispara rajada de 3 tiros
				"desisto"
		) + "\n";

		provideInput(commands);
		assertDoesNotThrow(Tasks::menu);
	}

	@Test
	@DisplayName("Deve carregar uma frota personalizada através do comando lefrota no menu")
	void testMenuLeFrota() {
		String commands = "lefrota\n" + getBuildFleetInput() + "desisto\n";
		provideInput(commands);

		assertDoesNotThrow(Tasks::menu);
	}

	@Test
	@DisplayName("Deve imprimir a ajuda do menu sem lançar exceções")
	void testMenuHelp() {
		assertDoesNotThrow(Tasks::menuHelp);
	}

	@Test
	@DisplayName("Deve construir uma frota completa ignorando navios inválidos ou sobrepostos")
	void testBuildFleet() {
		Scanner scanner = new Scanner(getBuildFleetInput());
		Fleet fleet = Tasks.buildFleet(scanner);

		assertNotNull(fleet);
		assertEquals(Fleet.FLEET_SIZE, fleet.getShips().size());
	}

	@Test
	@DisplayName("Deve lançar AssertionError quando buildFleet recebe um Scanner nulo")
	void testBuildFleetNullAssertion() {
		assertThrows(AssertionError.class, () -> Tasks.buildFleet(null));
	}

	@Test
	@DisplayName("Deve ler e construir um navio válido a partir do Scanner")
	void testReadShip() {
		Scanner scanner = new Scanner("barca 2 3 n\n");
		Ship ship = Tasks.readShip(scanner);

		assertNotNull(ship);
		assertEquals("Barca", ship.getCategory());
		assertEquals(new Position(2, 3), ship.getPosition());
		assertEquals(Compass.NORTH, ship.getBearing());
	}

	@Test
	@DisplayName("Deve devolver null quando readShip lê um tipo de navio desconhecido")
	void testReadShipUnknown() {
		Scanner scanner = new Scanner("submarino 2 3 n\n");
		assertNull(Tasks.readShip(scanner));
	}

	@Test
	@DisplayName("Deve lançar AssertionError quando readShip recebe um Scanner nulo")
	void testReadShipNullAssertion() {
		assertThrows(AssertionError.class, () -> Tasks.readShip(null));
	}

	@Test
	@DisplayName("Deve ler coordenadas numéricas e devolver a respetiva Position")
	void testReadPosition() {
		Scanner scanner = new Scanner("4 7\n");
		Position pos = Tasks.readPosition(scanner);

		assertEquals(new Position(4, 7), pos);
	}

	@Test
	@DisplayName("Deve lançar AssertionError quando readPosition recebe um Scanner nulo")
	void testReadPositionNullAssertion() {
		assertThrows(AssertionError.class, () -> Tasks.readPosition(null));
	}

	@Test
	@DisplayName("Deve ler posições clássicas em formato compacto (ex: A3, b10)")
	void testReadClassicPositionCompact() {
		Scanner scanner = new Scanner("A3 b10\n");

		IPosition pos1 = Tasks.readClassicPosition(scanner);
		assertEquals(new Position('A', 3), pos1);

		IPosition pos2 = Tasks.readClassicPosition(scanner);
		assertEquals(new Position('B', 10), pos2);
	}

	@Test
	@DisplayName("Deve ler posições clássicas separadas por espaço (ex: C 5)")
	void testReadClassicPositionSpaced() {
		Scanner scanner = new Scanner("C 5\n");
		IPosition pos = Tasks.readClassicPosition(scanner);

		assertEquals(new Position('C', 5), pos);
	}

	@Test
	@DisplayName("Deve lançar IllegalArgumentException para entradas inválidas em readClassicPosition")
	void testReadClassicPositionInvalidInputs() {
		Scanner emptyScanner = new Scanner("");
		assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(emptyScanner));

		Scanner invalidToken = new Scanner("123\n");
		assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(invalidToken));

		Scanner invalidSecondToken = new Scanner("A B\n");
		assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(invalidSecondToken));

		Scanner multiLetterToken = new Scanner("AB 3\n");
		assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(multiLetterToken));
	}
}