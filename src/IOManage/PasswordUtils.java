package IOManage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for password hashing using MD5.
 * All passwords are hashed before storage and compared as hashes during login.
 */
public class PasswordUtils {
    
    /**
     * Hashes a plain text password using MD5 algorithm.
     * @param password The plain text password to hash
     * @return The MD5 hash of the password as a hexadecimal string
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            
            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            return password; // Fallback to plain text if hashing fails
        }
    }
    
    /**
     * Verifies if a plain text password matches a hashed password.
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The stored hashed password
     * @return true if the passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(hashedPassword);
    }
    
    /**
     * Checks if a password is already hashed (32 character hex string).
     * @param password The password to check
     * @return true if the password appears to be already hashed
     */
    public static boolean isHashed(String password) {
        if (password == null || password.length() != 32) {
            return false;
        }
        // Check if all characters are valid hexadecimal
        return password.matches("[0-9a-f]{32}");
    }
}
