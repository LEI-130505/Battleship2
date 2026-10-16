package battleship;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreboardManager {
    private static final String FILE_PATH = "scoreboard.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static List<ScoreRecord> loadScores() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<ScoreRecord>>() {}.getType();
            List<ScoreRecord> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Erro ao ler o scoreboard: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveScore(String mode, int rounds) {
        List<ScoreRecord> scores = loadScores();
        scores.add(new ScoreRecord(mode, rounds));
        Collections.sort(scores);

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(scores, writer);
            System.out.println(">>> Resultado guardado no Scoreboard (" + rounds + " rajadas)!");
        } catch (Exception e) {
            System.err.println("Erro ao guardar no scoreboard: " + e.getMessage());
        }
    }

    public static void printScoreboard() {
        List<ScoreRecord> scores = loadScores();
        System.out.println("\n================ SCOREBOARD ================");
        if (scores.isEmpty()) {
            System.out.println("Ainda não existem jogos registados no histórico.");
        } else {
            Collections.sort(scores);
            for (int i = 0; i < scores.size(); i++) {
                System.out.println((i + 1) + "º - " + scores.get(i));
            }
        }
        System.out.println("============================================\n");
    }
}