package IOManage;

import Entity.GradingSystem;
import java.io.*;

/**
 * Manager for Letter Grade Grading System (A, B, C, D, E, F)
 * Grade is determined by percentage: (marks / totalMarks) * 100
 * Admin can set minimum percentage thresholds for each grade
 */
public class GradingSystemManager {

    private static GradingSystem gradingSystem;

    public static GradingSystem getGradingSystem() {
        return gradingSystem;
    }

    public static void setGradingSystem(GradingSystem gs) {
        gradingSystem = gs;
        saveToFile();
    }

    public static void loadFromFile() {
        File file = new File("data/grading.txt");

        if (!file.exists()) {
            // Default percentage-based thresholds
            // A >= 80%, B >= 65%, C >= 50%, D >= 40%, E >= 30%, F < 30%
            gradingSystem = new GradingSystem(80.0, 65.0, 50.0, 40.0, 30.0);
            saveToFile();
            System.out.println("grading.txt not found → default letter grade grading created");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            double aMin = 80.0, bMin = 65.0, cMin = 50.0, dMin = 40.0, eMin = 30.0;
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("#")) continue; // Skip comments
                String[] parts = line.split("=");
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                double val = Double.parseDouble(parts[1].trim());

                switch (key) {
                    case "A_MIN" -> aMin = val;
                    case "B_MIN" -> bMin = val;
                    case "C_MIN" -> cMin = val;
                    case "D_MIN" -> dMin = val;
                    case "E_MIN" -> eMin = val;
                }
            }
            gradingSystem = new GradingSystem(aMin, bMin, cMin, dMin, eMin);
            System.out.println("Letter grade grading system loaded successfully");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading grading system: " + e.getMessage());
            gradingSystem = new GradingSystem(80.0, 65.0, 50.0, 40.0, 30.0);
        }
    }

    public static void saveToFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) dataDir.mkdirs();

        try (PrintWriter pw = new PrintWriter(new FileWriter("data/grading.txt"))) {
            pw.println("# Letter Grade Grading System (Percentage-based)");
            pw.println("# Percentage = (marks / totalMarks) * 100");
            pw.println("# A = Excellent, B = Good, C = Satisfactory, D = Pass, E = Marginal, F = Fail");
            pw.println("A_MIN=" + gradingSystem.getAMin());
            pw.println("B_MIN=" + gradingSystem.getBMin());
            pw.println("C_MIN=" + gradingSystem.getCMin());
            pw.println("D_MIN=" + gradingSystem.getDMin());
            pw.println("E_MIN=" + gradingSystem.getEMin());
            System.out.println("Letter grade grading system saved");
        } catch (IOException e) {
            System.out.println("Error saving grading system: " + e.getMessage());
        }
    }
}
