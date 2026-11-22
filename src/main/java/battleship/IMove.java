package battleship;

import java.util.List;

/**
 * IMove
 *
 * @author Your Name
 * Date: 26/02/2026
 * Time: 18:32
 */
public interface IMove {

	@Override
	String toString();

	int getNumber();

	List<IPosition> getShots();

	List<IGame.ShotResult> getShotResults();

	String processEnemyFire(boolean verbose);
}
