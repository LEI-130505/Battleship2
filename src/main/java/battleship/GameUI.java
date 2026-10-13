package battleship;

import com.googlecode.lanterna.TextColor;

public final class GameUI {

	public static final TextColor FRAME = TextColor.ANSI.WHITE;
	public static final TextColor TITLE = TextColor.ANSI.CYAN_BRIGHT;
	public static final TextColor LABEL = TextColor.ANSI.WHITE_BRIGHT;
	public static final TextColor WATER = TextColor.ANSI.BLUE;
	public static final TextColor SHIP = TextColor.ANSI.GREEN;
	public static final TextColor ADJACENT = TextColor.ANSI.YELLOW;
	public static final TextColor HIT = TextColor.ANSI.RED_BRIGHT;
	public static final TextColor MISS = TextColor.ANSI.WHITE;
	public static final TextColor PROMPT = TextColor.ANSI.GREEN_BRIGHT;
	public static final TextColor COMMAND = TextColor.ANSI.GREEN_BRIGHT;
	public static final TextColor INFO = TextColor.ANSI.DEFAULT;
	public static final TextColor WARNING = TextColor.ANSI.YELLOW_BRIGHT;
	public static final TextColor ERROR = TextColor.ANSI.RED_BRIGHT;

	private static final String CSI = "\u001B[";
	private static final String RESET = CSI + "0m";

	private GameUI() {
	}

	public static TextColor colorFor(char marker) {
		switch (marker) {
			case Game.SHIP_MARKER:
				return SHIP;
			case Game.SHOT_SHIP_MARKER:
				return HIT;
			case Game.SHOT_WATER_MARKER:
				return MISS;
			case Game.SHIP_ADJACENT_MARKER:
				return ADJACENT;
			case Game.EMPTY_MARKER:
				return WATER;
			default:
				return FRAME;
		}
	}

	public static String color(TextColor color, String text) {
		return CSI + new String(color.getForegroundSGRSequence()) + "m" + text + RESET;
	}

	public static void print(TextColor color, String text) {
		System.out.print(color(color, text));
	}

	public static void println(TextColor color, String text) {
		System.out.println(color(color, text));
	}

	public static void println() {
		System.out.println();
	}
}
