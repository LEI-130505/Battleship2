package battleship;

import java.util.List;

public record PrintBoardConfig(IFleet fleet, List<IMove> moves, boolean showShots, boolean showLegend) {
}