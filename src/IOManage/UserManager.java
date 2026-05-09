package IOManage;

import Users.AcademicLeader;
import Users.AdminStaff;
import Users.Lecturer;
import Users.Student;
import Users.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class UserManager { //for managing user account, log in, registeration, etc//

    // static memory to store all users //
    public static ArrayList<User> users = new ArrayList<>();

    // add a new user //
    public static void addUser(User u) {
        users.add(u);
    }

    // login function - now uses MD5 hashed password comparison
    public static User login(String email, String password) {
        String hashedPassword = PasswordUtils.hashPassword(password);
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(hashedPassword)) {
                return u;
            }
        }
        System.out.println("Invalid email or password");
        return null;
    }

    // save all users to a text file
    public static void saveToFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File userFile = new File(dataDir, "users.txt");
        if (!userFile.exists()) {
            try {
                userFile.createNewFile();
            } catch (IOException e) {
            }
        }

        try (PrintWriter pwr = new PrintWriter(new FileWriter("data/users.txt"))) {
            for (User u : users) {
                switch (u) {
                    case Lecturer l ->
                        pwr.println(l.getName() + "," + l.getEmail() + "," + l.getPassword() + "," + "Lecturer," + l.getLeaderEmail());
                    case AcademicLeader al -> {
                        String joined = String.join(";", al.getLecturerEmails());
                        pwr.println(al.getName() + "," + al.getEmail() + "," + al.getPassword() + "," + "AcademicLeader" + "," + joined);
                    }
                    case Student s ->
                        pwr.println(s.getName() + "," + s.getEmail() + "," + s.getPassword() + "," + "Student");
                    case AdminStaff a ->
                        pwr.println(a.getName() + "," + a.getEmail() + "," + a.getPassword() + "," + "AdminStaff");
                    default ->
                        System.out.println("Type not found");

                }
            }
            System.out.println("Users saved successfully");
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static void updateUser(User u) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equalsIgnoreCase(u.getEmail())) {
                users.set(i, u);
                break;
            }
        }
    }

    // load users from text file
    public static void loadFromFile() {
        users.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 4) {
                    System.out.println("Corrupted line skipped: " + line);
                }

                String name = parts[0];
                String email = parts[1];
                String password = parts[2];
                String type = parts[3];

                User u = null;

                switch (type) {
                    case "Lecturer" -> {
                        String leaderEmail = parts.length >= 5 ? parts[4] : "";
                        u = new Lecturer(name, email, password, leaderEmail);
                    }
                    case "AcademicLeader" -> {
                        AcademicLeader al = new AcademicLeader(name, email, password);

                        if (parts.length >= 5 && !parts[4].isEmpty()) {
                            String[] list = parts[4].split(";");
                            for (String lecEmail : list) {
                                al.addLecturerEmail(lecEmail);
                            }
                        }
                        u = al;
                    }
                    case "Student" ->
                        u = new Student(name, email, password);

                    case "AdminStaff" ->
                        u = new AdminStaff(name, email, password);
                    default ->
                        System.out.println("Unknown type skipped");
                }
                if (u != null) {
                    users.add(u);
                }
            }

            System.out.println("Users loaded successfully");
        } catch (IOException e) {
            System.out.println("users.txt not found, no user loaded");
        }

    }

    public static User findByEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Check if a user has critical dependencies that would prevent deletion
     * @param user The user to check
     * @return A message describing the dependencies, or null if no critical dependencies
     */
    public static String checkUserDependencies(User user) {
        StringBuilder warnings = new StringBuilder();
        String email = user.getEmail();
        
        // Load fresh data
        ModuleManager.loadAllModules();
        ClassManager.loadFromFile();
        
        if (user instanceof Lecturer) {
            // Check if lecturer is the sole lecturer for any module
            for (Entity.Module m : ModuleManager.modules) {
                if (m.getLecturerEmails().contains(email)) {
                    if (m.getLecturerEmails().size() == 1) {
                        warnings.append("• Sole lecturer for module: ").append(m.getCode()).append(" - ").append(m.getName()).append("\n");
                    }
                }
            }
            
            // Check if lecturer is assigned to any classes
            for (Entity.ClassGroup cg : ClassManager.classGroups) {
                if (cg.getLecturerEmails() != null && cg.getLecturerEmails().contains(email)) {
                    if (cg.getLecturerEmails().size() == 1) {
                        warnings.append("• Sole lecturer for class: ").append(cg.getClassCode()).append("\n");
                    }
                }
            }
        } else if (user instanceof AcademicLeader) {
            // Check if academic leader manages any modules
            for (Entity.Module m : ModuleManager.modules) {
                if (m.getAcademicLeaderEmail().equalsIgnoreCase(email)) {
                    warnings.append("• Managing module: ").append(m.getCode()).append(" - ").append(m.getName()).append("\n");
                }
            }
        } else if (user instanceof Student) {
            // Check if student is enrolled in modules or classes
            int moduleCount = 0;
            int classCount = 0;
            
            for (Entity.Module m : ModuleManager.modules) {
                if (m.getStudentEmails().contains(email)) {
                    moduleCount++;
                }
            }
            
            for (Entity.ClassGroup cg : ClassManager.classGroups) {
                if (cg.getStudentEmails() != null && cg.getStudentEmails().contains(email)) {
                    classCount++;
                }
            }
            
            if (moduleCount > 0) {
                warnings.append("• Enrolled in ").append(moduleCount).append(" module(s)\n");
            }
            if (classCount > 0) {
                warnings.append("• Registered in ").append(classCount).append(" class(es)\n");
            }
        }
        
        return warnings.length() > 0 ? warnings.toString() : null;
    }

    /**
     * Remove all references to a user from modules and classes before deletion
     * @param user The user being deleted
     */
    public static void cleanupUserReferences(User user) {
        String email = user.getEmail();
        
        ModuleManager.loadAllModules();
        ClassManager.loadFromFile();
        
        if (user instanceof Lecturer) {
            // Remove from all modules
            for (Entity.Module m : ModuleManager.modules) {
                if (m.getLecturerEmails().contains(email)) {
                    m.removeLecturer(email);
                }
            }
            ModuleManager.saveToFile();
            
            // Remove from all classes
            for (Entity.ClassGroup cg : ClassManager.classGroups) {
                if (cg.getLecturerEmails() != null && cg.getLecturerEmails().contains(email)) {
                    cg.removeLecturer(email);
                }
            }
            ClassManager.saveToFile();
            
            // Remove from academic leader's list
            for (User u : users) {
                if (u instanceof AcademicLeader al) {
                    if (al.getLecturerEmails().contains(email)) {
                        al.getLecturerEmails().remove(email);
                    }
                }
            }
        } else if (user instanceof Student) {
            // Remove from all modules
            for (Entity.Module m : ModuleManager.modules) {
                if (m.getStudentEmails().contains(email)) {
                    m.removeStudent(email);
                }
            }
            ModuleManager.saveToFile();
            
            // Remove from all classes
            for (Entity.ClassGroup cg : ClassManager.classGroups) {
                if (cg.getStudentEmails() != null && cg.getStudentEmails().contains(email)) {
                    cg.getStudentEmails().remove(email);
                }
            }
            ClassManager.saveToFile();
        }
        // Note: AcademicLeader deletion should be blocked if managing modules
    }
}
