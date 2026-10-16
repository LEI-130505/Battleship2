/**
 * 
 */
package battleship;

public class Main
{
	/**
	 * Main.
	 *
	 * @param args the args
	 */
	public static void main(String[] args)
    {
		try {
			new BattleshipTUI().run();
		} catch (Exception e) {
			GameUI.println(GameUI.TITLE, "***  Battleship  ***");
			Tasks.menu();
		}
    }
}
