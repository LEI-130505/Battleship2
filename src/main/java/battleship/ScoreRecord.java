package battleship;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScoreRecord implements Comparable<ScoreRecord> {
    private String mode;
    private int rounds;
    private String dateTime;

    public ScoreRecord() {
    }

    public ScoreRecord(String mode, int rounds) {
        this.mode = mode;
        this.rounds = rounds;
        this.dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getMode() {
        return mode;
    }

    public int getRounds() {
        return rounds;
    }

    public String getDateTime() {
        return dateTime;
    }

    @Override
    public int compareTo(ScoreRecord other) {
        // menos rajadas = melhor desempenho
        return Integer.compare(this.rounds, other.rounds);
    }

    @Override
    public String toString() {
        return String.format("[%s] Modo: %-10s | Rajadas até vencer: %2d", dateTime, mode, rounds);
    }
}