package IOManage;

import java.io.*;
import java.util.ArrayList;

public class EmailUpdateManager {
    
    private static final String[] DATA_FILES = {
        "data/users.txt",
        "data/module.txt",
        "data/classgroup.txt",
        "data/comments.txt",
        "data/marks.txt",
        "data/assessments.txt",
        "data/report.txt"
    };
    
    /**
     * Check if an email already exists in any data file
     */
    public static boolean emailExistsInSystem(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        for (String filePath : DATA_FILES) {
            File file = new File(filePath);
            if (!file.exists()) continue;
            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    if (lineContainsEmail(line, email)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                System.out.println("Warning: Could not read " + filePath);
            }
        }
        
        return false;
    }
    
    /**
     * Check if a line contains the email as a complete field
     */
    private static boolean lineContainsEmail(String line, String email) {
        String[] parts = line.split("[,|\\s]+");
        for (String part : parts) {
            if (part.trim().equalsIgnoreCase(email)) return true;
        }
        return false;
    }
    
    /**
     * Update email across all system files
     */
    public static boolean updateEmailAcrossSystem(String oldEmail, String newEmail) {
        if (oldEmail == null || newEmail == null || oldEmail.isEmpty() || newEmail.isEmpty()) {
            System.out.println("Error: Email cannot be empty");
            return false;
        }
        
        if (oldEmail.equalsIgnoreCase(newEmail)) {
            return true;
        }
        
        // Check if new email already exists
        if (emailExistsInSystem(newEmail)) {
            System.out.println("Error: Email " + newEmail + " already exists in the system.");
            return false;
        }
        
        System.out.println("\n--- Updating email across all files ---");
        System.out.println("Old: " + oldEmail);
        System.out.println("New: " + newEmail);
        
        boolean allSuccess = true;
        
        // Update each file
        for (String filePath : DATA_FILES) {
            boolean success = updateEmailInFile(oldEmail, newEmail, filePath);
            System.out.println((success ? "  ✓ " : "  ✗ ") + filePath);
            allSuccess &= success;
        }
        
        // Reload all managers
        try {
            UserManager.loadFromFile();
            ModuleManager.loadAllModules();
            ClassManager.loadFromFile();
            CommentManager.loadFromFile();
            System.out.println("✓ All managers reloaded");
        } catch (Exception e) {
            System.out.println("Warning: Error reloading managers: " + e.getMessage());
        }
        
        return allSuccess;
    }
    
    /**
     * Update email in a specific file
     */
    private static boolean updateEmailInFile(String oldEmail, String newEmail, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return true;

        ArrayList<String> lines = new ArrayList<>();
        boolean emailFound = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) { lines.add(line); continue; }
                String updatedLine = line.replace(oldEmail, newEmail);
                if (!updatedLine.equals(line)) emailFound = true;
                lines.add(updatedLine);
            }
        } catch (IOException e) {
            System.out.println("Error reading " + filePath + ": " + e.getMessage());
            return false;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String l : lines) pw.println(l);
            return true;
        } catch (IOException e) {
            System.out.println("Error writing " + filePath + ": " + e.getMessage());
            return false;
        }
    }
}
