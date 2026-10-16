package battleship;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;

/**
 * Mede o tempo gasto em cada jogada, usando o StopWatch do Apache Commons Lang3.
 * Requisito: issue #1 (relógio com o tempo gasto em cada jogada).
 */
public class MoveTimer {

    private StopWatch stopWatch;

    /** Arranca o cronómetro no início da jogada. */
    public void start() {
        stopWatch = StopWatch.createStarted();
    }

    /** Pára o cronómetro e devolve o tempo formatado (HH:mm:ss.SSS). */
    public String stopAndFormat() {
        if (stopWatch == null) {
            return "00:00:00.000";
        }
        stopWatch.stop();
        return DurationFormatUtils.formatDuration(stopWatch.getTime(), "HH:mm:ss.SSS");
    }
}