package IOManage;

import java.io.*;
import java.util.ArrayList;

/**
 * Utility class to migrate existing plain text passwords to MD5 hashes.
 * Run this once to convert all existing user passwords in users.txt.
 */
public class PasswordMigration {
    
    /**
     * Migrates all existing plain text passwords to MD5 hashes.
     * This should be run once after implementing MD5 password hashing.
     */
    public static void migratePasswordsToMD5() {
        System.out.println("Starting password migration to MD5...");
        
        ArrayList<String[]> users = new ArrayList<>();
        
        // Read existing users.txt
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    // Check if password is already hashed (32 char hex string)
                    String password = parts[2];
                    if (!PasswordUtils.isHashed(password)) {
                        // Hash the plain text password
                        parts[2] = PasswordUtils.hashPassword(password);
                        System.out.println("Migrated password for user: " + parts[1]);
                    } else {
                        System.out.println("Password already hashed for user: " + parts[1]);
                    }
                    users.add(parts);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users.txt: " + e.getMessage());
            return;
        }
        
        // Write back with hashed passwords
        try (PrintWriter pw = new PrintWriter(new FileWriter("data/users.txt"))) {
            for (String[] parts : users) {
                pw.println(String.join(",", parts));
            }
            System.out.println("Password migration completed successfully!");
            System.out.println("Total users migrated: " + users.size());
        } catch (IOException e) {
            System.out.println("Error writing users.txt: " + e.getMessage());
        }
    }
    
    /**
     * Main method to run the migration.
     * Run this once: java IOManage.PasswordMigration
     */
    public static void main(String[] args) {
        migratePasswordsToMD5();
    }
}
