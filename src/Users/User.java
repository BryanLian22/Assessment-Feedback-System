package Users;

import IOManage.UserManager;
import java.util.Scanner;

public abstract class User {
    
    Scanner scan = new Scanner(System.in);
    protected String name;
    protected String email;  
    protected String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    
    public void setName(String name) { 
        this.name = name; 
    }

    public void setEmail(String email) { 
        this.email = email; 
    }

    public void setPassword(String password) { 
        this.password = password; 
    }
    
    public abstract void showMenu();

    public void editProfile() {
        
        UserManager.loadFromFile();     
        
        while (true) {
            System.out.println("1. Edit Name\n2. Edit Email\n3. Reset Password\n4. Cancel\nEnter Option: ");
            String option = scan.nextLine();
            switch (option) {
                case "1" -> {
                    System.out.println("Current Name: " + this.name);
                    System.out.println("Enter new name or type n to cancel: ");
                    String answer = scan.nextLine();
                    if (answer.equalsIgnoreCase("n")) {
                        System.out.println("Canceling...");
                    } else {
                        setName(answer);
                        System.out.println("Name Updated to: " + this.name);
                        UserManager.updateUser(this);
                        UserManager.saveToFile();
                    }
                }
                case "2" -> {
                    System.out.println("Current email: " + this.email);
                    System.out.println("Enter new email or type n to cancel: ");
                    String newEmail = scan.nextLine().trim();
                    
                    if (newEmail.equalsIgnoreCase("n")) {
                        System.out.println("Canceling...");
                    } else if (newEmail.isEmpty()) {
                        System.out.println("Email cannot be empty.");
                    } else {
                        String oldEmail = this.email;
                        
                        // Check if new email already exists in system
                        if (IOManage.EmailUpdateManager.emailExistsInSystem(newEmail)) {
                            System.out.println("✗ Error: Email " + newEmail + " already exists in the system.");
                        } else {
                            // Update email across all files
                            if (IOManage.EmailUpdateManager.updateEmailAcrossSystem(oldEmail, newEmail)) {
                                System.out.println("✓ Email Updated to: " + newEmail);
                            } else {
                                System.out.println("✗ Failed to update email.");
                            }
                        }
                    }
                }
                case "3" -> {
                    int attempt = 1;
                    boolean success = false;

                    while (attempt <= 3) {
                        System.out.print("Enter current password: ");
                        String currentPw = scan.nextLine();

                        if (IOManage.PasswordUtils.verifyPassword(currentPw, this.password)) {
                            int cfAttempt = 1;
                            while (cfAttempt <= 3) {
                                System.out.print("Enter new password: ");
                                String newPw = scan.nextLine();
                                System.out.print("Confirm new password: ");
                                String confPw = scan.nextLine();

                                if (newPw.equals(confPw)) {
                                    setPassword(IOManage.PasswordUtils.hashPassword(newPw));
                                    System.out.println("Password updated successfully");
                                    UserManager.updateUser(this);
                                    UserManager.saveToFile();
                                    success = true;
                                    break;
                                } else {
                                    System.out.println("Passwords do not match. Attempts left: " + (3 - cfAttempt));
                                    cfAttempt++;
                                }
                            }
                            if (!success) System.out.println("Max attempt reached\nExiting...");
                            break;
                            
                        } 
                        else {
                            System.out.println("Incorrect password " + (3 - attempt) + " left");
                            attempt++;
                        }
                    }

                    if (!success) System.out.println("Password not changed");
                }

                case "4" -> {System.out.println("Canceling..."); return;}

                default -> System.out.println("Invalid Option");
            }
        }
        
    }
}
