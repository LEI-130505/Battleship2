package battleship;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class BattleshipTUI {

	private static final int TITLE_Y = 0;
	private static final int BOARD_TITLE_Y = 1;
	private static final int HEADER_Y = 2;
	private static final int TOP_Y = 3;
	private static final int ROWS_Y = 4;
	private static final int BOTTOM_Y = 14;
	private static final int LEGEND_Y = 16;
	private static final int STATUS_Y = 17;
	private static final int MESSAGE_Y = 18;
	private static final int HELP1_Y = 19;
	private static final int HELP2_Y = 20;
	private static final int TIMER_Y = 21;
	private static final int INPUT_Y = 22;

	private static final int LEFT_X = 2;
	private static final int RIGHT_X = 30;
	private static final int BLOCK_WIDTH = 24;
	private static final int GRID_INNER = Game.BOARD_SIZE * 2;

	private final Screen screen;
	final MoveTimer moveTimer = new MoveTimer();
	Game game;
	IFleet myFleet;
	String message;
	String moveTime;

	public BattleshipTUI() throws IOException {
		this(createScreen());
	}

	public BattleshipTUI(Terminal terminal) throws IOException {
		this(new TerminalScreen(terminal));
	}

	public BattleshipTUI(Screen screen) {
		this.screen = screen;
	}

	private static Screen createScreen() throws IOException {
		Terminal terminal = new DefaultTerminalFactory().createTerminal();
		return new TerminalScreen(terminal);
	}

	public void run() throws IOException {
		screen.startScreen();
		PrintStream originalOut = System.out;
		try {
			System.setOut(new PrintStream(OutputStream.nullOutputStream(), true));
			message = "Bem-vindo! Use 'gerafrota' ou 'lefrota' para comecar.";
			render();
			boolean running = true;
			while (running) {
				String input = readLine("comando> ");
				if (input == null)
					continue;
				running = handle(input.trim().toLowerCase());
				render();
			}
		} finally {
			System.setOut(originalOut);
			screen.stopScreen();
		}
	}

	boolean handle(String cmd) throws IOException {
		switch (cmd) {
			case "":
				break;
			case "gerafrota":
				myFleet = Fleet.createRandom();
				game = new Game(myFleet);
				game.generateAlienFleet();
				message = "Frota aleatoria gerada. Boa sorte!";
				break;
			case "lefrota":
				Fleet built = buildFleetInteractive();
				if (built != null) {
					myFleet = built;
					game = new Game(built);
					game.generateAlienFleet();
					message = "Frota carregada!";
				}
				break;
			case "estado":
				message = myFleet == null ? "Gere uma frota primeiro." : fleetStatus();
				break;
			case "mapa":
				message = myFleet == null ? "Gere uma frota primeiro." : "Mapa atualizado.";
				break;
			case "rajada":
				if (game == null) {
					message = "Gere uma frota primeiro.";
					break;
				}
				punch();
				break;
			case "simula":
				if (game == null) {
					message = "Gere uma frota primeiro.";
					break;
				}
				simulate();
				break;
			case "tiros":
				message = "Legenda: '.' agua  '#' navio  '*' tiro certeiro  'o' tiro na agua";
				break;
			case "ajuda":
				message = "Comandos: gerafrota lefrota estado mapa rajada simula tiros ajuda pdf desisto";
				break;
			case "pdf":
				if (game == null) {
					message = "Gere uma frota primeiro.";
					break;
				}
				try {
					PdfExporter.export(game, PdfExporter.DEFAULT_FILE);
					message = "PDF gerado: " + PdfExporter.DEFAULT_FILE;
				} catch (IOException e) {
					message = "Nao foi possivel gerar o PDF (esta aberto noutro programa?)";
				}
				break;
			case "desisto":
				return false;
			default:
				message = "Comando invalido: " + cmd;
		}
		return true;
	}

	private Fleet buildFleetInteractive() throws IOException {
		Fleet fleet = new Fleet();
		int created = 0;
		while (created < Fleet.FLEET_SIZE) {
			String line = readLine("navio " + (created + 1) + "/" + Fleet.FLEET_SIZE + " (ex: galeao 3 5 N) > ");
			if (line == null)
				return null;
			if (line.isBlank())
				continue;
			try {
				Ship ship = Tasks.readShip(new Scanner(line));
				if (ship != null && fleet.addShip(ship))
					created++;
				else {
					message = "Navio invalido ou em conflito. Tente de novo.";
					render();
				}
			} catch (RuntimeException e) {
				message = "Dados invalidos: " + e.getMessage();
				render();
			}
		}
		return fleet;
	}

	private void punch() throws IOException {
		moveTimer.start();
		String line = readLine("posicoes (ex: A1 B2 C3) > ");
		if (line == null) {
			message = "Rajada cancelada.";
			return;
		}
		try {
			game.readMyFire(new Scanner(line));
		} catch (IllegalArgumentException e) {
			message = "Rajada invalida: " + e.getMessage();
			return;
		}
		moveTime = moveTimer.stopAndFormat();
		if (game.getAlienRemainingShips() == 0) {
			message = "VITORIA! Afundaste toda a frota inimiga!";
			return;
		}
		game.randomEnemyFire();
		if (game.getRemainingShips() == 0)
			message = "Maldito sejas, Java Sparrow, eu voltarei, glub glub glub ...";
		else
			message = "Rajada disparada! O inimigo respondeu.";
	}

	private void simulate() throws IOException {
		while (game.getAlienRemainingShips() > 0 && game.getRemainingShips() > 0) {
			game.randomMyFire();
			if (game.getAlienRemainingShips() > 0)
				game.randomEnemyFire();
			render();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
		message = game.getAlienRemainingShips() == 0
				? "VITORIA! Afundaste toda a frota inimiga!"
				: "Maldito sejas, Java Sparrow, eu voltarei, glub glub glub ...";
	}

	private String fleetStatus() {
		String mine = "Minha frota: " + myFleet.getFloatingShips().size() + " a flutuar, "
				+ myFleet.getSunkShips().size() + " afundados";
		if (game == null)
			return mine;
		return mine + "  |  Inimiga: " + game.getAlienRemainingShips() + " a flutuar, "
				+ game.getAlienSunkShips() + " afundados";
	}

	void render() throws IOException {
		screen.clear();
		TextGraphics tg = screen.newTextGraphics();

		tg.setForegroundColor(GameUI.TITLE);
		tg.putString(2, TITLE_Y, "\u2693  BATTLESHIP  \u2693");

		if (myFleet != null)
			drawBoard(tg, LEFT_X, "MEU TABULEIRO",
					Game.buildBoard(myFleet, game == null ? List.of() : game.getAlienMoves(), true));

		if (game != null)
			drawBoard(tg, RIGHT_X, "TABULEIRO INIMIGO",
					Game.buildBoard(game.getAlienFleet(), game.getMyMoves(), true));

		drawLegend(tg);

		if (myFleet != null) {
			tg.setForegroundColor(GameUI.LABEL);
			tg.putString(2, STATUS_Y, "Minha frota:");
			tg.setForegroundColor(GameUI.SHIP);
			tg.putString(15, STATUS_Y, myFleet.getFloatingShips().size() + " a flutuar,");
			tg.setForegroundColor(GameUI.HIT);
			tg.putString(30, STATUS_Y, myFleet.getSunkShips().size() + " afundados");
		}

		if (game != null) {
			tg.setForegroundColor(GameUI.LABEL);
			tg.putString(50, STATUS_Y, "Inimiga:");
			tg.setForegroundColor(GameUI.SHIP);
			tg.putString(59, STATUS_Y, game.getAlienRemainingShips() + " a flutuar,");
			tg.setForegroundColor(GameUI.HIT);
			tg.putString(74, STATUS_Y, game.getAlienSunkShips() + " afundados");
		}

		if (message != null) {
			tg.setForegroundColor(GameUI.WARNING);
			tg.putString(2, MESSAGE_Y, message);
		}

		tg.setForegroundColor(GameUI.INFO);
		tg.putString(2, HELP1_Y, "Comandos: gerafrota  lefrota  estado  mapa  rajada  simula  tiros  ajuda  pdf  desisto");
		tg.putString(2, HELP2_Y, "rajada A1 B2 C3 ataca o inimigo (ele responde)   |   simula = jogo automatico");

		if (moveTime != null) {
			tg.setForegroundColor(GameUI.TITLE);
			tg.putString(2, TIMER_Y, "Tempo gasto na jogada: " + moveTime);
		}

		screen.refresh();
	}

	private void drawBoard(TextGraphics tg, int blockX, String title, char[][] map) {
		int gridX = blockX + 2;
		int titleX = blockX + Math.max(0, (BLOCK_WIDTH - title.length()) / 2);

		tg.setForegroundColor(GameUI.TITLE);
		tg.putString(titleX, BOARD_TITLE_Y, title);

		tg.setForegroundColor(GameUI.FRAME);
		for (int c = 0; c < Game.BOARD_SIZE; c++)
			tg.putString(gridX + 1 + 2 * c, HEADER_Y, String.valueOf(c + 1));

		tg.putString(gridX, TOP_Y, "\u250C" + "\u2500".repeat(GRID_INNER) + "\u2510");

		for (int r = 0; r < Game.BOARD_SIZE; r++) {
			int y = ROWS_Y + r;
			tg.setForegroundColor(GameUI.LABEL);
			tg.putString(blockX, y, String.valueOf((char) ('A' + r)));
			tg.setForegroundColor(GameUI.FRAME);
			tg.putString(gridX, y, "\u2502");
			for (int c = 0; c < Game.BOARD_SIZE; c++) {
				char marker = map[r][c];
				tg.setForegroundColor(GameUI.colorFor(marker));
				tg.putString(gridX + 1 + 2 * c, y, " " + marker);
			}
			tg.setForegroundColor(GameUI.FRAME);
			tg.putString(gridX + 1 + GRID_INNER, y, "\u2502");
		}

		tg.setForegroundColor(GameUI.FRAME);
		tg.putString(gridX, BOTTOM_Y, "\u2514" + "\u2500".repeat(GRID_INNER) + "\u2518");
	}

	private void drawLegend(TextGraphics tg) {
		int x = 2;
		tg.setForegroundColor(GameUI.LABEL);
		tg.putString(x, LEGEND_Y, "Legenda:");
		x += "Legenda:".length() + 2;
		x = drawLegendEntry(tg, x, Game.EMPTY_MARKER, "agua");
		x = drawLegendEntry(tg, x, Game.SHIP_MARKER, "navio");
		x = drawLegendEntry(tg, x, Game.SHOT_SHIP_MARKER, "tiro certeiro");
		drawLegendEntry(tg, x, Game.SHOT_WATER_MARKER, "tiro na agua");
	}

	private int drawLegendEntry(TextGraphics tg, int x, char marker, String label) {
		tg.setForegroundColor(GameUI.colorFor(marker));
		tg.putString(x, LEGEND_Y, String.valueOf(marker));
		tg.setForegroundColor(GameUI.INFO);
		tg.putString(x + 2, LEGEND_Y, label);
		return x + 2 + label.length() + 2;
	}

	private String readLine(String prefix) throws IOException {
		StringBuilder buffer = new StringBuilder();
		while (true) {
			TextGraphics tg = screen.newTextGraphics();
			int startX = 1 + prefix.length();
			int width = Math.max(0, screen.getTerminalSize().getColumns() - startX - 1);
			tg.setForegroundColor(GameUI.INFO);
			tg.fillRectangle(new TerminalPosition(startX, INPUT_Y), new TerminalSize(width, 1), ' ');
			tg.setForegroundColor(GameUI.PROMPT);
			tg.putString(1, INPUT_Y, prefix);
			tg.setForegroundColor(GameUI.LABEL);
			tg.putString(startX, INPUT_Y, buffer.toString());
			screen.setCursorPosition(new TerminalPosition(startX + Math.min(buffer.length(), width), INPUT_Y));
			screen.refresh();

			KeyStroke key = screen.readInput();
			if (key == null)
				continue;
			switch (key.getKeyType()) {
				case Character:
					if (key.getCharacter() != '\t' && key.getCharacter() != 0)
						buffer.append(key.getCharacter());
					break;
				case Backspace:
					if (buffer.length() > 0)
						buffer.deleteCharAt(buffer.length() - 1);
					break;
				case Enter:
					return buffer.toString();
				case Escape:
					return null;
				default:
					break;
			}
		}
	}
}
