package battleship;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Game.
 * Original author: britoeabreu
 * Extended by: 129835 - Afonso da Cota Bonito (testes adicionais para cobertura de linhas e ramos)
 */
public class GameTest {

	private Game game;

	@BeforeEach
	void setUp() {
		game = new Game(new Fleet());
	}

	@AfterEach
	void tearDown() {
		game = null;
	}

	// ------------------------------------------------------------------
	// Auxiliares
	// ------------------------------------------------------------------

	/** Executa o código e devolve o que foi escrito na consola. */
	private static String capture(Runnable action) {
		PrintStream original = System.out;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
		try {
			action.run();
		} finally {
			System.setOut(original);
		}
		return buffer.toString(StandardCharsets.UTF_8);
	}

	private static List<IPosition> shots(IPosition... positions) {
		return new ArrayList<>(Arrays.asList(positions));
	}

	/** Devolve as primeiras n posições do tabuleiro (por linhas). */
	private static List<IPosition> firstPositions(int n) {
		List<IPosition> result = new ArrayList<>();
		for (int r = 0; r < Game.BOARD_SIZE && result.size() < n; r++)
			for (int c = 0; c < Game.BOARD_SIZE && result.size() < n; c++)
				result.add(new Position(r, c));
		return result;
	}

	private static List<IPosition> threeShots() {
		return shots(new Position(2, 3), new Position(2, 4), new Position(2, 5));
	}

	// ------------------------------------------------------------------
	// Testes originais
	// ------------------------------------------------------------------

	@Test
	@DisplayName("O construtor inicializa o jogo com contadores a zero")
	void constructor() {
		assertNotNull(game, "Game instance should not be null after construction.");
		assertNotNull(game.getAlienMoves(), "Shots list should not be null after initialization.");
		assertTrue(game.getAlienMoves().isEmpty(), "Shots list should be empty upon initialization.");
		assertEquals(0, game.getInvalidShots(), "Invalid shots count should be zero upon initialization.");
		assertEquals(0, game.getRepeatedShots(), "Repeated shots count should be zero upon initialization.");
		assertEquals(0, game.getHits(), "Hits count should be zero upon initialization.");
		assertEquals(0, game.getSunkShips(), "Sunk ships count should be zero upon initialization.");
	}

	@Test
	@DisplayName("Um tiro fora do tabuleiro incrementa os tiros inválidos")
	void fire2() {
		Position invalidPosition = new Position(-1, 5);
		game.fireSingleShot(invalidPosition, false);
		assertEquals(1, game.getInvalidShots(), "Invalid shots counter should increase for an invalid shot.");
	}

	@Test
	@DisplayName("Um tiro marcado como repetido incrementa os tiros repetidos")
	void fire3() {
		Position position = new Position(2, 3);
		game.fireSingleShot(position, false);
		game.fireSingleShot(position, true);
		assertEquals(1, game.getRepeatedShots(), "Repeated shots counter should increase for a repeated shot.");
	}

	@Test
	@DisplayName("Uma posição já disparada numa jogada é repetida")
	void repeatedShot1() {
		game.fireShots(threeShots());
		Position position = new Position(2, 3);
		assertTrue(game.repeatedShot(position), "Position (2,3) should be marked as repeated after firing.");
	}

	@Test
	@DisplayName("Uma posição nunca disparada não é repetida")
	void repeatedShot2() {
		Position position = new Position(2, 3);
		assertFalse(game.repeatedShot(position), "Position (2,3) should not be marked as repeated before firing.");
	}

	@Test
	@DisplayName("Depois de uma jogada existe uma jogada inimiga registada")
	void getAlienMoves() {
		game.fireShots(threeShots());
		assertEquals(1, game.getAlienMoves().size(), "Shots list should contain one shot after firing once.");
	}

	@Test
	@DisplayName("O número de navios a flutuar diminui quando um navio é afundado")
	void getRemainingShips() {
		IFleet fleet = game.getMyFleet();
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));

		fleet.addShip(ship1);
		assertEquals(1, game.getRemainingShips(), "Just one ship was created!");
		fleet.addShip(ship2);
		assertEquals(2, game.getRemainingShips(), "Two ships were created!");
		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Remaining ships count should be 1 after sinking one of two ships.");
	}

	// ------------------------------------------------------------------
	// Acessores
	// ------------------------------------------------------------------

	@Test
	@DisplayName("getMyFleet devolve a frota passada ao construtor")
	void getMyFleetReturnsConstructorFleet() {
		IFleet fleet = new Fleet();
		Game g = new Game(fleet);
		assertSame(fleet, g.getMyFleet());
	}

	@Test
	@DisplayName("No início a frota inimiga existe e as listas de jogadas estão vazias")
	void initialAlienFleetAndMoves() {
		assertNotNull(game.getAlienFleet());
		assertTrue(game.getAlienFleet().getShips().isEmpty());
		assertTrue(game.getMyMoves().isEmpty());
		assertTrue(game.getAlienMoves().isEmpty());
	}

	@Test
	@DisplayName("generateAlienFleet cria uma frota com 11 navios todos a flutuar")
	void generateAlienFleet() {
		game.generateAlienFleet();
		assertEquals(11, game.getAlienFleet().getShips().size());
		assertEquals(11, game.getAlienRemainingShips());
		assertEquals(0, game.getAlienSunkShips());
	}

	@Test
	@DisplayName("Afundar um navio inimigo atualiza os navios restantes e afundados")
	void alienRemainingAndSunkShips() {
		game.generateAlienFleet();
		game.getAlienFleet().getShips().get(0).sink();
		assertEquals(10, game.getAlienRemainingShips());
		assertEquals(1, game.getAlienSunkShips());
	}

	// ------------------------------------------------------------------
	// fireSingleShot
	// ------------------------------------------------------------------

	@Test
	@DisplayName("fireSingleShot fora do tabuleiro devolve um resultado inválido")
	void fireSingleShotInvalid() {
		IGame.ShotResult result = game.fireSingleShot(new Position(-1, 0), false);
		assertFalse(result.valid());
		assertFalse(result.repeated());
		assertNull(result.ship());
		assertFalse(result.sunk());
	}

	@Test
	@DisplayName("fireSingleShot na água devolve um tiro válido sem navio")
	void fireSingleShotWater() {
		IGame.ShotResult result = game.fireSingleShot(new Position(0, 0), false);
		assertTrue(result.valid());
		assertFalse(result.repeated());
		assertNull(result.ship());
		assertFalse(result.sunk());
		assertEquals(0, game.getHits());
	}

	@Test
	@DisplayName("fireSingleShot num navio que continua a flutuar conta um acerto sem afundar")
	void fireSingleShotHitNotSunk() {
		game.getMyFleet().addShip(new Frigate(Compass.EAST, new Position(5, 5)));
		IGame.ShotResult result = game.fireSingleShot(new Position(5, 5), false);
		assertTrue(result.valid());
		assertNotNull(result.ship());
		assertFalse(result.sunk());
		assertEquals(1, game.getHits());
		assertEquals(0, game.getSunkShips());
	}

	@Test
	@DisplayName("fireSingleShot que afunda um navio conta o acerto e o afundamento")
	void fireSingleShotSunk() {
		game.getMyFleet().addShip(new Barge(Compass.NORTH, new Position(2, 3)));
		IGame.ShotResult result = game.fireSingleShot(new Position(2, 3), false);
		assertTrue(result.valid());
		assertNotNull(result.ship());
		assertTrue(result.sunk());
		assertEquals(1, game.getHits());
		assertEquals(1, game.getSunkShips());
		assertEquals(0, game.getRemainingShips());
	}

	@Test
	@DisplayName("fireSingleShot marcado como repetido devolve um resultado repetido")
	void fireSingleShotRepeatedFlag() {
		IGame.ShotResult result = game.fireSingleShot(new Position(4, 4), true);
		assertTrue(result.valid());
		assertTrue(result.repeated());
		assertNull(result.ship());
		assertEquals(1, game.getRepeatedShots());
	}

	@Test
	@DisplayName("fireSingleShot numa posição já disparada em jogadas anteriores é repetido")
	void fireSingleShotRepeatedFromHistory() {
		game.fireShots(threeShots());
		IGame.ShotResult result = game.fireSingleShot(new Position(2, 3), false);
		assertTrue(result.repeated());
		assertEquals(1, game.getRepeatedShots());
	}

	// ------------------------------------------------------------------
	// fireShots
	// ------------------------------------------------------------------

	@Test
	@DisplayName("fireShots com menos de 3 tiros lança IllegalArgumentException")
	void fireShotsTooFew() {
		assertThrows(IllegalArgumentException.class,
				() -> game.fireShots(shots(new Position(1, 1), new Position(2, 2))));
	}

	@Test
	@DisplayName("fireShots com mais de 3 tiros lança IllegalArgumentException")
	void fireShotsTooMany() {
		assertThrows(IllegalArgumentException.class,
				() -> game.fireShots(shots(new Position(1, 1), new Position(2, 2),
						new Position(3, 3), new Position(4, 4))));
	}

	@Test
	@DisplayName("fireShots com uma posição duplicada na mesma jogada conta um tiro repetido")
	void fireShotsDuplicateInSameMove() {
		game.fireShots(shots(new Position(2, 3), new Position(2, 3), new Position(2, 5)));
		assertEquals(1, game.getRepeatedShots());
		assertEquals(0, game.getInvalidShots());
	}

	@Test
	@DisplayName("fireShots com tiros fora do tabuleiro conta tiros inválidos")
	void fireShotsInvalidShots() {
		game.fireShots(shots(new Position(-1, 0), new Position(0, 10), new Position(0, 0)));
		assertEquals(2, game.getInvalidShots());
		assertEquals(1, game.getAlienMoves().size());
	}

	@Test
	@DisplayName("fireShots numera as jogadas inimigas sequencialmente")
	void fireShotsMoveNumbers() {
		game.fireShots(threeShots());
		game.fireShots(shots(new Position(6, 3), new Position(6, 4), new Position(6, 5)));
		assertEquals(1, game.getAlienMoves().get(0).getNumber());
		assertEquals(2, game.getAlienMoves().get(1).getNumber());
	}

	// ------------------------------------------------------------------
	// fireMyShots / myRepeatedShot
	// ------------------------------------------------------------------

	@Test
	@DisplayName("fireMyShots com número errado de tiros lança IllegalArgumentException")
	void fireMyShotsWrongSize() {
		assertThrows(IllegalArgumentException.class,
				() -> game.fireMyShots(shots(new Position(1, 1))));
	}

	@Test
	@DisplayName("fireMyShots regista a jogada e numera-a")
	void fireMyShotsRegistersMove() {
		game.fireMyShots(threeShots());
		game.fireMyShots(shots(new Position(6, 3), new Position(6, 4), new Position(6, 5)));
		assertEquals(2, game.getMyMoves().size());
		assertEquals(1, game.getMyMoves().get(0).getNumber());
		assertEquals(2, game.getMyMoves().get(1).getNumber());
	}

	@Test
	@DisplayName("myRepeatedShot reconhece posições já disparadas por mim")
	void myRepeatedShot() {
		assertFalse(game.myRepeatedShot(new Position(2, 3)));
		game.fireMyShots(threeShots());
		assertTrue(game.myRepeatedShot(new Position(2, 3)));
		assertFalse(game.myRepeatedShot(new Position(9, 9)));
	}

	@Test
	@DisplayName("fireMyShots distingue tiros inválidos, repetidos, na água e em navios")
	void fireMyShotsResults() {
		Ship barge = new Barge(Compass.NORTH, new Position(2, 3));
		Ship frigate = new Frigate(Compass.EAST, new Position(6, 5));
		game.getAlienFleet().addShip(barge);
		game.getAlienFleet().addShip(frigate);

		// 1.ª jogada: acerta na barca (afunda), fora do tabuleiro, na água
		game.fireMyShots(shots(new Position(2, 3), new Position(-1, 0), new Position(9, 9)));
		List<IGame.ShotResult> first = game.getMyMoves().get(0).getShotResults();
		assertTrue(first.get(0).valid());
		assertTrue(first.get(0).sunk());
		assertNotNull(first.get(0).ship());
		assertFalse(first.get(1).valid());
		assertTrue(first.get(2).valid());
		assertNull(first.get(2).ship());

		// 2.ª jogada: repetido do histórico, acerto na fragata sem afundar, duplicado na mesma jogada
		game.fireMyShots(shots(new Position(2, 3), new Position(6, 5), new Position(6, 5)));
		List<IGame.ShotResult> second = game.getMyMoves().get(1).getShotResults();
		assertTrue(second.get(0).repeated());
		assertNotNull(second.get(1).ship());
		assertFalse(second.get(1).sunk());
		assertTrue(second.get(2).repeated());

		assertEquals(1, game.getAlienSunkShips());
		assertEquals(1, game.getAlienRemainingShips());
	}

	// ------------------------------------------------------------------
	// Tiros aleatórios
	// ------------------------------------------------------------------

	@Test
	@DisplayName("randomEnemyFire gera e regista uma jogada com 3 tiros")
	void randomEnemyFire() {
		String json = game.randomEnemyFire();
		assertNotNull(json);
		assertTrue(json.contains("row"));
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(Game.NUMBER_SHOTS, game.getAlienMoves().get(0).getShots().size());
	}

	@Test
	@DisplayName("randomMyFire gera e regista uma jogada minha com 3 tiros")
	void randomMyFire() {
		String json = game.randomMyFire();
		assertNotNull(json);
		assertEquals(1, game.getMyMoves().size());
		assertEquals(Game.NUMBER_SHOTS, game.getMyMoves().get(0).getShots().size());
	}

	@Test
	@DisplayName("randomMyFire não dispara nas posições adjacentes a navios inimigos afundados")
	void randomMyFireAvoidsAdjacentOfSunkShips() {
		Ship sunk = new Barge(Compass.NORTH, new Position(5, 5));
		game.getAlienFleet().addShip(sunk);
		sunk.sink();

		game.randomMyFire();

		List<IPosition> fired = game.getMyMoves().get(0).getShots();
		for (IPosition adjacent : sunk.getAdjacentPositions())
			assertFalse(fired.contains(adjacent), "Não devia disparar em " + adjacent);
	}

	@Test
	@DisplayName("randomEnemyFire com menos de 3 posições disponíveis repete tiros para completar a jogada")
	void randomEnemyFireFewCandidates() {
		// Só ficam 2 posições por disparar
		game.getAlienMoves().add(new Move(1, firstPositions(98), new ArrayList<>()));

		game.randomEnemyFire();

		assertEquals(2, game.getAlienMoves().size());
		List<IPosition> fired = game.getAlienMoves().get(1).getShots();
		assertEquals(Game.NUMBER_SHOTS, fired.size());
		assertEquals(1, game.getRepeatedShots());
	}

	@Test
	@DisplayName("randomMyFire com menos de 3 posições disponíveis completa a jogada com um tiro repetido")
	void randomMyFireFewCandidates() {
		game.getMyMoves().add(new Move(1, firstPositions(98), new ArrayList<>()));

		game.randomMyFire();

		assertEquals(2, game.getMyMoves().size());
		assertEquals(Game.NUMBER_SHOTS, game.getMyMoves().get(1).getShots().size());
	}

	// ------------------------------------------------------------------
	// readEnemyFire / readMyFire
	// ------------------------------------------------------------------

	@Test
	@DisplayName("readEnemyFire lê posições no formato compacto (A1 B2 C3)")
	void readEnemyFireCompact() {
		String json = game.readEnemyFire(new Scanner("A1 B2 C3\n"));
		assertNotNull(json);
		assertEquals(1, game.getAlienMoves().size());
		List<IPosition> read = game.getAlienMoves().get(0).getShots();
		assertEquals(new Position('A', 1), read.get(0));
		assertEquals(new Position('B', 2), read.get(1));
		assertEquals(new Position('C', 3), read.get(2));
	}

	@Test
	@DisplayName("readEnemyFire lê posições com espaço e mistura de formatos (letra minúscula incluída)")
	void readEnemyFireSpacedAndMixed() {
		game.readEnemyFire(new Scanner("b 2 C3 D 4\n"));
		List<IPosition> read = game.getAlienMoves().get(0).getShots();
		assertEquals(new Position('B', 2), read.get(0));
		assertEquals(new Position('C', 3), read.get(1));
		assertEquals(new Position('D', 4), read.get(2));
	}

	@Test
	@DisplayName("readEnemyFire ignora tokens a mais depois dos 3 tiros")
	void readEnemyFireIgnoresExtraTokens() {
		game.readEnemyFire(new Scanner("A1 A2 A3 A4\n"));
		assertEquals(3, game.getAlienMoves().get(0).getShots().size());
	}

	@Test
	@DisplayName("readEnemyFire com menos de 3 posições lança IllegalArgumentException")
	void readEnemyFireTooFew() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(new Scanner("A1 A2\n")));
		assertTrue(ex.getMessage().contains("exatamente"));
		assertTrue(game.getAlienMoves().isEmpty());
	}

	@Test
	@DisplayName("readEnemyFire com uma coluna sem linha lança IllegalArgumentException")
	void readEnemyFireIncompletePosition() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(new Scanner("A\n")));
		assertTrue(ex.getMessage().contains("incompleta"));
	}

	@Test
	@DisplayName("readEnemyFire com formato inválido lança IllegalArgumentException")
	void readEnemyFireInvalidFormat() {
		assertThrows(IllegalArgumentException.class,
				() -> game.readEnemyFire(new Scanner("11 22 33\n")));
	}

	@Test
	@DisplayName("readMyFire lê e regista a minha jogada")
	void readMyFire() {
		String json = game.readMyFire(new Scanner("A1 B2 C3\n"));
		assertNotNull(json);
		assertEquals(1, game.getMyMoves().size());
		assertEquals(new Position('B', 2), game.getMyMoves().get(0).getShots().get(1));
	}

	@Test
	@DisplayName("readMyFire com posições a menos lança IllegalArgumentException")
	void readMyFireTooFew() {
		assertThrows(IllegalArgumentException.class,
				() -> game.readMyFire(new Scanner("A1\n")));
		assertTrue(game.getMyMoves().isEmpty());
	}

	// ------------------------------------------------------------------
	// jsonShots
	// ------------------------------------------------------------------

	@Test
	@DisplayName("jsonShots devolve o JSON com a linha e a coluna de cada tiro")
	void jsonShots() {
		String[] result = new String[1];
		capture(() -> result[0] = Game.jsonShots(
				shots(new Position('A', 1), new Position('B', 2), new Position('C', 3))));
		assertNotNull(result[0]);
		assertTrue(result[0].contains("\"row\""));
		assertTrue(result[0].contains("\"column\""));
		assertTrue(result[0].contains("\"A\""));
		assertTrue(result[0].contains("\"C\""));
	}

	@Test
	@DisplayName("jsonShots com lista vazia devolve um array JSON vazio")
	void jsonShotsEmpty() {
		String[] result = new String[1];
		capture(() -> result[0] = Game.jsonShots(new ArrayList<>()));
		assertTrue(result[0].startsWith("["));
		assertFalse(result[0].contains("row"));
	}

	// ------------------------------------------------------------------
	// buildBoard
	// ------------------------------------------------------------------

	@Test
	@DisplayName("buildBoard sem navios devolve um tabuleiro 10x10 só com água")
	void buildBoardEmpty() {
		char[][] map = Game.buildBoard(new Fleet(), new ArrayList<>(), true);
		assertEquals(Game.BOARD_SIZE, map.length);
		for (char[] row : map) {
			assertEquals(Game.BOARD_SIZE, row.length);
			for (char cell : row)
				assertEquals(Game.EMPTY_MARKER, cell);
		}
	}

	@Test
	@DisplayName("buildBoard marca as posições dos navios")
	void buildBoardShips() {
		IFleet fleet = new Fleet();
		fleet.addShip(new Barge(Compass.NORTH, new Position(1, 1)));
		char[][] map = Game.buildBoard(fleet, new ArrayList<>(), false);
		assertEquals(Game.SHIP_MARKER, map[1][1]);
		assertEquals(Game.EMPTY_MARKER, map[0][0]);
	}

	@Test
	@DisplayName("buildBoard sem mostrar tiros não altera as posições atingidas")
	void buildBoardHidesShots() {
		IFleet fleet = new Fleet();
		fleet.addShip(new Barge(Compass.NORTH, new Position(1, 1)));
		List<IMove> moves = new ArrayList<>();
		moves.add(new Move(1, shots(new Position(1, 1), new Position(3, 3), new Position(4, 4)), new ArrayList<>()));
		char[][] map = Game.buildBoard(fleet, moves, false);
		assertEquals(Game.SHIP_MARKER, map[1][1]);
		assertEquals(Game.EMPTY_MARKER, map[3][3]);
	}

	@Test
	@DisplayName("buildBoard mostra tiros certeiros, tiros na água e ignora tiros fora do tabuleiro")
	void buildBoardShowsShots() {
		IFleet fleet = new Fleet();
		fleet.addShip(new Barge(Compass.NORTH, new Position(1, 1)));
		List<IMove> moves = new ArrayList<>();
		moves.add(new Move(1, shots(new Position(1, 1), new Position(3, 3), new Position(-1, 5)), new ArrayList<>()));
		char[][] map = Game.buildBoard(fleet, moves, true);
		assertEquals(Game.SHOT_SHIP_MARKER, map[1][1]);
		assertEquals(Game.SHOT_WATER_MARKER, map[3][3]);
	}

	@Test
	@DisplayName("buildBoard marca as posições adjacentes só de navios afundados")
	void buildBoardAdjacentOfSunkShip() {
		IFleet fleet = new Fleet();
		Ship sunk = new Barge(Compass.NORTH, new Position(5, 5));
		Ship floating = new Barge(Compass.NORTH, new Position(8, 1));
		fleet.addShip(sunk);
		fleet.addShip(floating);
		sunk.sink();

		char[][] map = Game.buildBoard(fleet, new ArrayList<>(), false);

		assertEquals(Game.SHIP_MARKER, map[5][5]);
		assertEquals(Game.SHIP_ADJACENT_MARKER, map[4][4]);
		assertEquals(Game.SHIP_ADJACENT_MARKER, map[6][6]);
		assertEquals(Game.SHIP_ADJACENT_MARKER, map[5][6]);
		// O navio que continua a flutuar não revela adjacentes
		assertEquals(Game.SHIP_MARKER, map[8][1]);
		assertEquals(Game.EMPTY_MARKER, map[7][0]);
	}

	@Test
	@DisplayName("buildBoard transforma um tiro numa posição adjacente em tiro na água")
	void buildBoardShotOnAdjacent() {
		IFleet fleet = new Fleet();
		Ship sunk = new Barge(Compass.NORTH, new Position(5, 5));
		fleet.addShip(sunk);
		sunk.sink();
		List<IMove> moves = new ArrayList<>();
		moves.add(new Move(1, shots(new Position(4, 4), new Position(5, 5), new Position(0, 0)), new ArrayList<>()));

		char[][] map = Game.buildBoard(fleet, moves, true);

		assertEquals(Game.SHOT_WATER_MARKER, map[4][4]);
		assertEquals(Game.SHOT_SHIP_MARKER, map[5][5]);
		assertEquals(Game.SHOT_WATER_MARKER, map[0][0]);
	}

	// ------------------------------------------------------------------
	// Impressão no ecrã
	// ------------------------------------------------------------------

	@Test
	@DisplayName("printBoard com legenda escreve a legenda")
	void printBoardWithLegend() {
		String out = capture(() -> Game.printBoard(new Fleet(), new ArrayList<>(), false, true));
		assertTrue(out.contains("LEGENDA"));
		assertTrue(out.contains("10"));
	}

	@Test
	@DisplayName("printBoard sem legenda não escreve a legenda")
	void printBoardWithoutLegend() {
		String out = capture(() -> Game.printBoard(new Fleet(), new ArrayList<>(), false, false));
		assertFalse(out.contains("LEGENDA"));
		assertTrue(out.contains("A"));
	}

	@Test
	@DisplayName("printMyBoard escreve o meu tabuleiro")
	void printMyBoard() {
		game.getMyFleet().addShip(new Barge(Compass.NORTH, new Position(1, 1)));
		String out = capture(() -> game.printMyBoard(true, true));
		assertTrue(out.contains("LEGENDA"));
	}

	@Test
	@DisplayName("printAlienBoard escreve o tabuleiro inimigo")
	void printAlienBoard() {
		game.generateAlienFleet();
		String out = capture(() -> game.printAlienBoard(true, false));
		assertFalse(out.isEmpty());
		assertFalse(out.contains("LEGENDA"));
	}

	@Test
	@DisplayName("over escreve a mensagem de fim de jogo")
	void over() {
		String out = capture(() -> game.over());
		assertTrue(out.contains("Maldito sejas"));
	}

	// ------------------------------------------------------------------
	// Pré-condições (assert) e escolha aleatória com poucas posições
	// ------------------------------------------------------------------

	@Test
	@DisplayName("Os métodos com assert rejeitam argumentos nulos (requer assertions ativas: -ea)")
	void nullArgumentsAreRejected() {
		assertThrows(AssertionError.class, () -> Game.printBoard(null, new ArrayList<>(), false, false));
		assertThrows(AssertionError.class, () -> Game.printBoard(new Fleet(), null, false, false));
		assertThrows(AssertionError.class, () -> Game.buildBoard(null, new ArrayList<>(), false));
		assertThrows(AssertionError.class, () -> Game.buildBoard(new Fleet(), null, false));
		assertThrows(AssertionError.class, () -> Game.jsonShots(null));
		assertThrows(AssertionError.class, () -> game.readEnemyFire(null));
		assertThrows(AssertionError.class, () -> game.readMyFire(null));
		assertThrows(AssertionError.class, () -> game.fireShots(null));
		assertThrows(AssertionError.class, () -> game.fireSingleShot(null, false));
		assertThrows(AssertionError.class, () -> game.fireMyShots(null));
		assertThrows(AssertionError.class, () -> game.myRepeatedShot(null));
		assertThrows(AssertionError.class, () -> game.repeatedShot(null));
	}

	@Test
	@DisplayName("fireMyShots rejeita uma posição nula dentro da lista de tiros")
	void fireMyShotsNullPosition() {
		assertThrows(AssertionError.class,
				() -> game.fireMyShots(shots(null, new Position(1, 1), new Position(2, 2))));
	}

	@Test
	@DisplayName("Com exatamente 3 posições livres, a jogada aleatória escolhe 3 tiros distintos")
	void randomFireExactlyThreeCandidates() {
		// A escolha é aleatória: repete-se para que a rejeição de tiros duplicados aconteça
		for (int i = 0; i < 50; i++) {
			Game g = new Game(new Fleet());
			g.getMyMoves().add(new Move(1, firstPositions(97), new ArrayList<>()));
			g.randomMyFire();
			List<IPosition> fired = g.getMyMoves().get(1).getShots();
			assertEquals(3, fired.size());
			assertEquals(3, new java.util.HashSet<>(fired).size(), "Os 3 tiros devem ser distintos");
		}
	}

	@Test
	@DisplayName("Com 2 posições livres, a jogada aleatória escolhe as duas e repete uma")
	void randomFireTwoCandidates() {
		for (int i = 0; i < 50; i++) {
			Game g = new Game(new Fleet());
			g.getMyMoves().add(new Move(1, firstPositions(98), new ArrayList<>()));
			g.randomMyFire();
			List<IPosition> fired = g.getMyMoves().get(1).getShots();
			assertEquals(3, fired.size());
			assertEquals(2, new java.util.HashSet<>(fired).size());
		}
	}
}
