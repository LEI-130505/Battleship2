package battleship;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.virtual.DefaultVirtualTerminal;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BattleshipTUITest
 */
class BattleshipTUITest {

	private Screen newVirtualScreen() throws IOException {
		DefaultVirtualTerminal terminal = new DefaultVirtualTerminal(new TerminalSize(100, 30));
		TerminalScreen screen = new TerminalScreen(terminal);
		screen.startScreen();
		return screen;
	}

	private String snapshot(Screen screen) {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < screen.getTerminalSize().getRows(); row++) {
			for (int col = 0; col < screen.getTerminalSize().getColumns(); col++) {
				var character = screen.getBackCharacter(col, row);
				sb.append(character.getCharacterString());
			}
		}
		return sb.toString();
	}

	private boolean hasMarkerWithColor(Screen screen, char marker, TextColor color) {
		for (int row = 0; row < screen.getTerminalSize().getRows(); row++)
			for (int col = 0; col < screen.getTerminalSize().getColumns(); col++)
				if (screen.getBackCharacter(col, row).getCharacterString().equals(String.valueOf(marker))
						&& screen.getBackCharacter(col, row).getForegroundColor().equals(color))
					return true;
		return false;
	}

	@Test
	void colorForMapsEveryMarker() {
		assertEquals(GameUI.WATER, GameUI.colorFor(Game.EMPTY_MARKER));
		assertEquals(GameUI.SHIP, GameUI.colorFor(Game.SHIP_MARKER));
		assertEquals(GameUI.HIT, GameUI.colorFor(Game.SHOT_SHIP_MARKER));
		assertEquals(GameUI.MISS, GameUI.colorFor(Game.SHOT_WATER_MARKER));
		assertEquals(GameUI.ADJACENT, GameUI.colorFor(Game.SHIP_ADJACENT_MARKER));
		assertEquals(TextColor.ANSI.BLUE, GameUI.colorFor(Game.EMPTY_MARKER));
		assertEquals(TextColor.ANSI.WHITE, GameUI.colorFor(Game.SHOT_WATER_MARKER));
	}

	@Test
	void rendersBothBoardsAndColorsInFixedPositions() throws IOException {
		Screen screen = newVirtualScreen();
		BattleshipTUI tui = new BattleshipTUI(screen);

		IFleet fleet = new Fleet();
		fleet.addShip(new Barge(Compass.NORTH, new Position(0, 0)));
		Game game = new Game(fleet);
		game.fireShots(List.of(new Position(0, 0), new Position(5, 5), new Position(6, 6)));

		tui.myFleet = fleet;
		tui.game = game;
		tui.render();

		String first = snapshot(screen);
		assertTrue(first.contains("MEU TABULEIRO"), "My board title must be drawn");
		assertTrue(first.contains("TABULEIRO INIMIGO"), "Opponent board title must be drawn");
		assertTrue(hasMarkerWithColor(screen, Game.SHIP_MARKER, GameUI.SHIP), "Ships must be green");
		assertTrue(hasMarkerWithColor(screen, Game.SHOT_SHIP_MARKER, GameUI.HIT), "Hits must be red");
		assertTrue(hasMarkerWithColor(screen, Game.SHOT_WATER_MARKER, GameUI.MISS), "Misses must be white");
		assertTrue(hasMarkerWithColor(screen, Game.EMPTY_MARKER, GameUI.WATER), "Water must be blue");

		tui.render();
		assertEquals(first, snapshot(screen), "Redrawing must reuse the same fixed positions (no scroll)");

		screen.stopScreen();
	}

	@Test
	void firingAtAlienMarksHitsOnOpponentBoard() {
		Game game = new Game(new Fleet());
		game.generateAlienFleet();
		assertTrue(game.getAlienRemainingShips() > 0, "Alien fleet must have floating ships");

		IShip target = game.getAlienFleet().getFloatingShips().get(0);
		IPosition hit = target.getPositions().get(0);

		List<IPosition> shots = new java.util.ArrayList<>();
		shots.add(hit);
		for (int r = 0; r < Game.BOARD_SIZE && shots.size() < Game.NUMBER_SHOTS; r++)
			for (int c = 0; c < Game.BOARD_SIZE && shots.size() < Game.NUMBER_SHOTS; c++)
				if (!shots.contains(new Position(r, c)))
					shots.add(new Position(r, c));

		game.fireMyShots(shots);

		assertEquals(1, game.getMyMoves().size(), "Player move must be recorded");
		char[][] map = Game.buildBoard(game.getAlienFleet(), game.getMyMoves(), true);
		assertEquals(Game.SHOT_SHIP_MARKER, map[hit.getRow()][hit.getColumn()], "Hit must appear on opponent board");

		game.randomEnemyFire();
		assertEquals(1, game.getAlienMoves().size(), "Enemy retaliation must be recorded");
	}
}
