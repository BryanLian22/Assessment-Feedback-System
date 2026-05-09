package Users;

import App.Menu;
import Entity.ClassGroup;
import Entity.Module;
import IOManage.ClassManager;
import IOManage.ModuleManager;
import IOManage.UserManager;
import java.util.ArrayList;
import java.util.Scanner;

public class AdminStaff extends User {

    public AdminStaff(String name, String email, String password) {
        super(name, email, password);
    }

    private static final Scanner scan = new Scanner(System.in);

    @Override
    public void showMenu() {
        Menu.adminMenu(this);
    }

    public void manageUsers() {
        UserManager.loadFromFile();

        boolean manage = true;
        while (manage == true) {
            System.out.println("===================\nMANAGE USERS\n===================");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Error: " + e);
            }

            System.out.printf("%-20s %-30s %-15s\n", "Name", "Email", "Type");
            System.out.println("-----------------------------------------------------------------");

            for (User u : UserManager.users) {
                String type = "";
                if (u instanceof AdminStaff) {
                    type = "AdminStaff";
                } else if (u instanceof AcademicLeader) {
                    type = "AcademicLeader";
                } else if (u instanceof Lecturer) {
                    type = "Lecturer";
                } else if (u instanceof Student) {
                    type = "Student";
                }

                System.out.printf("%-20s %-30s %-15s\n", u.getName(), u.getEmail(), type);
            }

            System.out.println("1. Create Account");
            System.out.println("2. Edit Account");
            System.out.println("3. Delete Account");
            System.out.println("4. Exit");
            System.out.println("Enter your option: ");
            String option = scan.nextLine();
            switch (option) {
                case "1" -> {
                    System.out.println("Enter name: ");
                    String cname = scan.nextLine();
                    System.out.println("Enter Email: ");
                    String cemail = scan.nextLine();
                    System.out.println("Enter Password: ");
                    String cpassword = scan.nextLine();
                    String ctype = "";
                    while (true) {
                        System.out.println("Select User Type\n1. Student\n2. Lecturer\n3. Academic Leader\n4. Admin Staff");
                        System.out.println("Enter Option: ");
                        String input = scan.nextLine();

                        switch (input) {
                            case "1" -> {
                                ctype = "Student";
                            }
                            case "2" -> {
                                ctype = "Lecturer";
                            }
                            case "3" -> {
                                ctype = "AcademicLeader";
                            }
                            case "4" -> {
                                ctype = "AdminStaff";
                            }
                            default -> {
                                System.out.println("Invalid Option, try again.");
                                continue;
                            }
                        }
                        break;
                    }

                    User newUser = switch (ctype) {
                        case "Student" ->
                            new Student(cname, cemail, cpassword);
                        case "Lecturer" ->
                            new Lecturer(cname, cemail, cpassword, "");
                        case "AcademicLeader" ->
                            new AcademicLeader(cname, cemail, cpassword);
                        case "AdminStaff" ->
                            new AdminStaff(cname, cemail, cpassword);
                        default ->
                            null;
                    };

                    if (newUser != null) {
                        UserManager.addUser(newUser);
                        UserManager.saveToFile();
                        System.out.println("User created successfully!");
                    }
                }
                case "2" -> {
                    System.out.println("Enter email for edit: ");
                    String target = scan.nextLine();
                    User targetUser = UserManager.findByEmail(target);

                    if (targetUser == null) {
                        System.out.println("User not found");
                        break;
                    }
                    System.out.println("Editing account for: " + targetUser.getName() + "," + targetUser.getEmail());
                    boolean edit = true;
                    while (edit == true) {
                        System.out.println("1. Edit Name\n2. Edit Email\n3. Reset Password\n4. Exit");
                        String eoption = scan.nextLine();
                        switch (eoption) {
                            case "1" -> {
                                System.out.println("Enter new name (" + targetUser.getName() + "): ");
                                System.out.println("Leave empty to cancel");
                                String newName = scan.nextLine();
                                if (!newName.isEmpty()) {
                                    targetUser.setName(newName);
                                }
                                User updatedUser = targetUser;
                                UserManager.updateUser(updatedUser);
                                UserManager.saveToFile();

                            }
                            case "2" -> {
                                System.out.println("Enter new email (" + targetUser.getEmail() + "): ");
                                System.out.println("Leave empty to cancel");
                                String newEmail = scan.nextLine().trim();
                                
                                if (!newEmail.isEmpty()) {
                                    String oldEmail = targetUser.getEmail();
                                    
                                    // Check if new email already exists
                                    if (IOManage.EmailUpdateManager.emailExistsInSystem(newEmail)) {
                                        System.out.println("✗ Error: Email " + newEmail + " already exists in the system.");
                                    } else {
                                        // Update email across all files
                                        if (IOManage.EmailUpdateManager.updateEmailAcrossSystem(oldEmail, newEmail)) {
                                            System.out.println("✓ Email updated successfully!");
                                        } else {
                                            System.out.println("✗ Failed to update email.");
                                        }
                                    }
                                }
                            }
                            case "3" -> {
                                boolean ctn = false;
                                for (int i = 0; i < 3; i++) {
                                    System.out.println("Enter ADMIN current password: ");
                                    String currentPw = scan.nextLine();
                                    if (currentPw.equals(this.password)) {
                                        ctn = true;
                                        break;
                                    } else {
                                        System.out.println("Password Incorrect, " + (2 - i) + " attempt left");
                                    }
                                }
                                if (!ctn) {
                                    System.out.println("Maximum attempt reached");
                                    break;
                                }

                                System.out.println("Enter new password for " + targetUser.getName() + ": ");
                                System.out.println("Leave empty to cancel");
                                String newPassword = scan.nextLine();
                                if (!newPassword.isEmpty()) {
                                    targetUser.setPassword(newPassword);
                                }
                                User updatedUser = targetUser;
                                UserManager.updateUser(updatedUser);
                                UserManager.saveToFile();
                            }
                            case "4" -> {
                                System.out.println("Exiting....");
                                edit = false;
                            }
                            default ->
                                System.out.println("Invalid Option");
                        }

                    }

                }
                case "3" -> {
                    System.out.println("Enter email for delete: ");
                    String targetEmail = scan.nextLine();

                    User targetUser = UserManager.findByEmail(targetEmail);

                    if (targetUser == null) {
                        System.out.println("User not found.");
                    } else {
                        // Check for critical dependencies
                        String dependencies = UserManager.checkUserDependencies(targetUser);
                        
                        if (dependencies != null) {
                            System.out.println("\n⚠️ WARNING: This user has critical dependencies:");
                            System.out.println(dependencies);
                            
                            // Block deletion for sole lecturers and academic leaders with modules
                            if (targetUser instanceof Lecturer && dependencies.contains("Sole lecturer")) {
                                System.out.println("❌ Cannot delete: User is the sole lecturer for one or more modules.");
                                System.out.println("Please assign another lecturer before deleting this user.");
                                break;
                            }
                            if (targetUser instanceof AcademicLeader && dependencies.contains("Managing module")) {
                                System.out.println("❌ Cannot delete: User is managing one or more modules.");
                                System.out.println("Please reassign or delete the modules before deleting this user.");
                                break;
                            }
                            
                            System.out.println("Proceed with deletion anyway? (Y/N)");
                        } else {
                            System.out.println("Are you sure you want to delete " + targetUser.getName() + " (" + targetUser.getEmail() + ")? (Y/N)");
                        }
                        
                        String confirm = scan.nextLine();
                        if (confirm.equalsIgnoreCase("Y")) {
                            // Cleanup references before deletion
                            UserManager.cleanupUserReferences(targetUser);
                            UserManager.users.remove(targetUser);
                            UserManager.saveToFile();
                            System.out.println("User deleted successfully!");
                        } else {
                            System.out.println("Deletion canceled.");
                        }
                    }
                }
                case "4" -> {
                    System.out.println("Exiting...");
                    manage = false;
                }
                default ->
                    System.out.println("Invalid Option");
            }
        }
    }

    public void assignLecturers() {
        UserManager.loadFromFile();

        ArrayList<AcademicLeader> leaders = new ArrayList<>();
        for (User u : UserManager.users) {
            if (u instanceof AcademicLeader academicLeader) {
                leaders.add(academicLeader);
            }
        }

        if (leaders.isEmpty()) {
            System.out.println("No Academic Leaders found.");
            return;
        }

        while (true) {
            System.out.println("========== ASSIGN LECTURERS TO ACADEMIC LEADER ==========");
            System.out.println("\nAcademic Leaders:");
            for (AcademicLeader al : leaders) {
                System.out.println("- " + al.getName() + " (" + al.getEmail() + ")");
            }

            System.out.print("\nEnter Academic Leader's email or [exit] to exit: ");
            String leaderEmail = scan.nextLine().trim();

            if (leaderEmail.equalsIgnoreCase("exit")) {
                break;
            }

            AcademicLeader selectedLeader = null;
            for (AcademicLeader al : leaders) {
                if (al.getEmail().equalsIgnoreCase(leaderEmail)) {
                    selectedLeader = al;
                    break;
                }
            }

            if (selectedLeader == null) {
                System.out.println("No Academic Leader with that email.");
                continue;
            }

            ArrayList<Lecturer> availableLecturers = new ArrayList<>();
            for (User u : UserManager.users) {
                if (u instanceof Lecturer lecturer) {
                    boolean assigned = false;
                    for (AcademicLeader al : leaders) {
                        if (al.getLecturerEmails().contains(lecturer.getEmail())) {
                            assigned = true;
                            break;
                        }
                    }
                    if (!assigned) {
                        availableLecturers.add(lecturer);
                    }
                }
            }

            while (true) {
                System.out.println("\nCurrently assigned lecturers: "
                        + (selectedLeader.getLecturerEmails().isEmpty() ? "[none]" : selectedLeader.getLecturerEmails()));

                System.out.println("\nAvailable Lecturers:");
                if (availableLecturers.isEmpty()) {
                    System.out.println("[none]");
                } else {
                    for (Lecturer l : availableLecturers) {
                        System.out.println("- " + l.getName() + " (" + l.getEmail() + ")");
                    }
                }

                System.out.println("\nOptions: [add] | [remove] | [done]");
                System.out.print("Enter option: ");
                String option = scan.nextLine().trim();

                if (option.equalsIgnoreCase("done")) {
                    break;
                } else if (option.equalsIgnoreCase("add")) {
                    System.out.print("Enter lecturer email to ADD: ");
                    String lectEmail = scan.nextLine().trim();

                    Lecturer found = null;
                    for (Lecturer l : availableLecturers) {
                        if (l.getEmail().equalsIgnoreCase(lectEmail)) {
                            found = l;
                            break;
                        }
                    }

                    if (found == null) {
                        System.out.println("No available lecturer found with that email.");
                        continue;
                    }

                    selectedLeader.addLecturerEmail(found.getEmail());
                    availableLecturers.remove(found);
                    System.out.println("Assigned: " + found.getName() + " (" + found.getEmail() + ")");

                } else if (option.equalsIgnoreCase("remove")) {
                    System.out.print("Enter lecturer email to REMOVE: ");
                    String lectEmail = scan.nextLine().trim();

                    if (selectedLeader.getLecturerEmails().removeIf(e -> e.equalsIgnoreCase(lectEmail))) {
                        for (User u : UserManager.users) {
                            if (u instanceof Lecturer lect && lect.getEmail().equalsIgnoreCase(lectEmail)) {
                                availableLecturers.add(lect);
                                break;
                            }
                        }
                        System.out.println("Removed: " + lectEmail);
                    } else {
                        System.out.println("This lecturer is not assigned to the selected leader.");
                    }

                } else {
                    System.out.println("Unknown option. Type add / remove / done.");
                }
            }

            UserManager.updateUser(selectedLeader);
            UserManager.saveToFile();
            System.out.println("Lecturer assignments saved successfully!\n");
        }
    }

    public void defineGradingSystem() {
        IOManage.GradingSystemManager.loadFromFile();
        Entity.GradingSystem gs = IOManage.GradingSystemManager.getGradingSystem();

        boolean manage = true;
        while (manage) {
            System.out.println("===================\nMANAGE LETTER GRADE SYSTEM\n===================");
            System.out.println("Percentage Formula: (marks / totalMarks) * 100");
            System.out.println("\nCurrent Minimum Percentage Thresholds:");
            System.out.printf("A_MIN (Excellent):     %.1f%%%n", gs.getAMin());
            System.out.printf("B_MIN (Good):          %.1f%%%n", gs.getBMin());
            System.out.printf("C_MIN (Satisfactory):  %.1f%%%n", gs.getCMin());
            System.out.printf("D_MIN (Pass):          %.1f%%%n", gs.getDMin());
            System.out.printf("E_MIN (Marginal):      %.1f%%%n", gs.getEMin());
            System.out.println("(Anything below E_MIN is F - Fail)");

            System.out.println("\nOptions:");
            System.out.println("1. Change A_MIN (Excellent)");
            System.out.println("2. Change B_MIN (Good)");
            System.out.println("3. Change C_MIN (Satisfactory)");
            System.out.println("4. Change D_MIN (Pass)");
            System.out.println("5. Change E_MIN (Marginal)");
            System.out.println("6. Exit");
            System.out.print("Enter option: ");
            String option = scan.nextLine();

            try {
                switch (option) {
                    case "1" -> {
                        System.out.print("Enter new A_MIN (0-100): ");
                        double newA = Double.parseDouble(scan.nextLine());
                        if (newA <= 100 && newA > gs.getBMin()) {
                            gs.setAMin(newA);
                            System.out.println("A_MIN updated successfully!");
                        } else {
                            System.out.printf("Invalid value! Must be > B_MIN (%.1f) and <= 100.%n", gs.getBMin());
                        }
                    }
                    case "2" -> {
                        System.out.print("Enter new B_MIN (0-100): ");
                        double newB = Double.parseDouble(scan.nextLine());
                        if (newB < gs.getAMin() && newB > gs.getCMin() && newB >= 0) {
                            gs.setBMin(newB);
                            System.out.println("B_MIN updated successfully!");
                        } else {
                            System.out.printf("Invalid value! Must be < A_MIN (%.1f) and > C_MIN (%.1f).%n", gs.getAMin(), gs.getCMin());
                        }
                    }
                    case "3" -> {
                        System.out.print("Enter new C_MIN (0-100): ");
                        double newC = Double.parseDouble(scan.nextLine());
                        if (newC < gs.getBMin() && newC > gs.getDMin() && newC >= 0) {
                            gs.setCMin(newC);
                            System.out.println("C_MIN updated successfully!");
                        } else {
                            System.out.printf("Invalid value! Must be < B_MIN (%.1f) and > D_MIN (%.1f).%n", gs.getBMin(), gs.getDMin());
                        }
                    }
                    case "4" -> {
                        System.out.print("Enter new D_MIN (0-100): ");
                        double newD = Double.parseDouble(scan.nextLine());
                        if (newD < gs.getCMin() && newD > gs.getEMin() && newD >= 0) {
                            gs.setDMin(newD);
                            System.out.println("D_MIN updated successfully!");
                        } else {
                            System.out.printf("Invalid value! Must be < C_MIN (%.1f) and > E_MIN (%.1f).%n", gs.getCMin(), gs.getEMin());
                        }
                    }
                    case "5" -> {
                        System.out.print("Enter new E_MIN (0-100): ");
                        double newE = Double.parseDouble(scan.nextLine());
                        if (newE < gs.getDMin() && newE >= 0) {
                            gs.setEMin(newE);
                            System.out.println("E_MIN updated successfully!");
                        } else {
                            System.out.printf("Invalid value! Must be < D_MIN (%.1f) and >= 0.%n", gs.getDMin());
                        }
                    }
                    case "6" ->
                        manage = false;
                    default ->
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }

            IOManage.GradingSystemManager.setGradingSystem(gs);
        }
    }

    public void manageClasses() {
        ModuleManager.loadAllModules();
        ClassManager.loadFromFile();
        int choice;

        do {
            System.out.println("\n===== Manage Classes =====");
            System.out.println("1. Add Class");
            System.out.println("2. View Classes");
            System.out.println("3. Edit Class");
            System.out.println("4. Delete Class");
            System.out.println("0. Back to Menu");
            System.out.print("Enter choice: ");
            choice = scan.nextInt();
            scan.nextLine(); // consume newline

            switch (choice) {
                case 1 -> { // Add Class
                    System.out.print("Enter Class Code: ");
                    String classCode = scan.nextLine().trim();

                    System.out.print("Enter Module Code: ");
                    String moduleCode = scan.nextLine().trim();

                    Module module = ModuleManager.findModuleByCode(moduleCode);
                    if (module == null) {
                        System.out.println("Module not found. Please add the module first.");
                        break;
                    }

                    System.out.print("Enter Class Time (e.g., Tuesday 12pm-2pm): ");
                    String time = scan.nextLine().trim();

                    System.out.print("Enter Classroom (e.g., Audi1): ");
                    String classroom = scan.nextLine().trim();

                    ClassGroup existing = ClassManager.findByClassCode(classCode);
                    if (existing != null) {
                        System.out.println("Class code already exists.");
                        break;
                    }

                    ClassGroup cg = new ClassGroup(classCode, module, time, classroom);
                    ClassManager.addClassGroup(cg);
                    System.out.println("Class added successfully!");
                }

                case 2 -> { // View Classes
                    ClassManager.loadFromFile(); // refresh list
                    if (ClassManager.classGroups.isEmpty()) {
                        System.out.println("No classes found.");
                    } else {
                        System.out.println("\nClass List:");
                        for (ClassGroup cg : ClassManager.classGroups) {
                            System.out.println("Class Code: " + cg.getClassCode()
                                    + " | Module: " + cg.getModule().getCode()
                                    + " | Time: " + cg.getTime()
                                    + " | Classroom: " + cg.getClassroom());
                        }
                    }
                }

                case 3 -> { // Edit Class
                    System.out.print("Enter Class Code to edit: ");
                    String code = scan.nextLine().trim();
                    ClassGroup cg = ClassManager.findByClassCode(code);
                    if (cg == null) {
                        System.out.println("Class not found.");
                        break;
                    }

                    System.out.print("Enter new Class Code (or leave blank to keep): ");
                    String newCode = scan.nextLine().trim();
                    if (!newCode.isEmpty()) {
                        cg.setClassCode(newCode);
                    }

                    System.out.print("Enter new Module Code (or leave blank to keep): ");
                    String newModuleCode = scan.nextLine().trim();
                    if (!newModuleCode.isEmpty()) {
                        Module newModule = ModuleManager.findModuleByCode(newModuleCode);
                        if (newModule != null) {
                            cg.setModule(newModule);
                        } else {
                            System.out.println("Module not found. Module not updated.");
                        }
                    }

                    System.out.print("Enter new Class Time (or leave blank to keep): ");
                    String newTime = scan.nextLine().trim();
                    if (!newTime.isEmpty()) {
                        cg.setTime(newTime);
                    }

                    System.out.print("Enter new Classroom (or leave blank to keep): ");
                    String newClassroom = scan.nextLine().trim();
                    if (!newClassroom.isEmpty()) {
                        cg.setClassroom(newClassroom);
                    }

                    ClassManager.updateClassGroup(cg);
                    System.out.println("Class updated successfully!");
                }

                case 4 -> { // Delete Class
                    System.out.print("Enter Class Code to delete: ");
                    String code = scan.nextLine().trim();
                    ClassGroup cg = ClassManager.findByClassCode(code);
                    if (cg == null) {
                        System.out.println("Class not found.");
                        break;
                    }
                    ClassManager.deleteClassGroup(cg);
                    System.out.println("Class deleted successfully!");
                }

                case 0 ->
                    System.out.println("Returning to main menu...");
                default ->
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 0);
    }

    /**
     * Menu for managing student module registrations
     */
    public void manageStudentModules() {
        ModuleManager.loadAllModules();
        UserManager.loadFromFile();

        System.out.println("\n========== MANAGE STUDENT MODULES ==========");
        System.out.println("1. Assign Student to Module");
        System.out.println("2. Remove Student from Module");
        System.out.println("3. Switch Student Module");
        System.out.println("0. Back");
        System.out.print("Enter option: ");
        String option = scan.nextLine();

        switch (option) {
            case "1" -> assignStudentToModuleMenu();
            case "2" -> removeStudentFromModuleMenu();
            case "3" -> switchStudentModuleMenu();
            case "0" -> { return; }
            default -> System.out.println("Invalid option.");
        }
    }

    /**
     * Menu for assigning a student to a module
     */
    private void assignStudentToModuleMenu() {
        System.out.print("Enter student email: ");
        String studentEmail = scan.nextLine().trim();

        Users.User user = UserManager.findByEmail(studentEmail);
        if (user == null || !(user instanceof Users.Student)) {
            System.out.println("Student not found.");
            return;
        }

        Users.Student student = (Users.Student) user;
        
        // Display available modules
        System.out.println("\nAvailable Modules:");
        int count = 0;
        for (Module m : ModuleManager.modules) {
            if (!student.getRegisteredModules().contains(m.getCode())) {
                System.out.println("  " + m.getCode() + " - " + m.getName());
                count++;
            }
        }
        
        if (count == 0) {
            System.out.println("No available modules to assign (student may be registered in all modules).");
            return;
        }

        System.out.print("\nEnter module code to assign: ");
        String moduleCode = scan.nextLine().trim().toUpperCase();

        if (student.getRegisteredModules().contains(moduleCode)) {
            System.out.println("Student is already registered in this module.");
            return;
        }

        Module m = ModuleManager.findModuleByCode(moduleCode);
        if (m == null) {
            System.out.println("Module does not exist.");
            return;
        }

        // Register student to module
        boolean success = ModuleManager.registerStudentToModule(moduleCode, studentEmail);
        if (success) {
            student.getRegisteredModules().add(moduleCode);
            UserManager.saveToFile();
            System.out.println("Student successfully assigned to module " + moduleCode);
        } else {
            System.out.println("Failed to assign student to module.");
        }
    }

    /**
     * Menu for removing a student from a module
     */
    private void removeStudentFromModuleMenu() {
        System.out.print("Enter student email: ");
        String studentEmail = scan.nextLine().trim();

        Users.User user = UserManager.findByEmail(studentEmail);
        if (user == null || !(user instanceof Users.Student)) {
            System.out.println("Student not found.");
            return;
        }

        Users.Student student = (Users.Student) user;
        
        System.out.println("\nModules registered by " + student.getName() + ":");
        if (student.getRegisteredModules().isEmpty()) {
            System.out.println("No modules registered.");
            return;
        }

        for (int i = 0; i < student.getRegisteredModules().size(); i++) {
            System.out.println((i + 1) + ". " + student.getRegisteredModules().get(i));
        }

        System.out.print("\nEnter module code to remove: ");
        String moduleCode = scan.nextLine().trim().toUpperCase();

        if (!student.getRegisteredModules().contains(moduleCode)) {
            System.out.println("Student is not registered in this module.");
            return;
        }

        // Also remove from ClassManager if student is in any class of this module
        ClassManager.loadFromFile();
        ClassManager.removeStudentFromModuleClasses(studentEmail, moduleCode);

        // Remove from ModuleManager
        boolean success = ModuleManager.removeStudentFromModule(moduleCode, studentEmail);
        if (success) {
            student.getRegisteredModules().remove(moduleCode);
            UserManager.saveToFile();
            System.out.println("Student successfully removed from module " + moduleCode);
        } else {
            System.out.println("Failed to remove student from module.");
        }
    }

    /**
     * Menu for switching a student from one module to another
     */
    private void switchStudentModuleMenu() {
        System.out.print("Enter student email: ");
        String studentEmail = scan.nextLine().trim();

        Users.User user = UserManager.findByEmail(studentEmail);
        if (user == null || !(user instanceof Users.Student)) {
            System.out.println("Student not found.");
            return;
        }

        Users.Student student = (Users.Student) user;
        
        System.out.println("\nModules registered by " + student.getName() + ":");
        if (student.getRegisteredModules().isEmpty()) {
            System.out.println("No modules registered.");
            return;
        }

        for (int i = 0; i < student.getRegisteredModules().size(); i++) {
            System.out.println((i + 1) + ". " + student.getRegisteredModules().get(i));
        }

        System.out.print("\nEnter current module code to switch FROM: ");
        String oldModuleCode = scan.nextLine().trim().toUpperCase();

        if (!student.getRegisteredModules().contains(oldModuleCode)) {
            System.out.println("Student is not registered in this module.");
            return;
        }

        System.out.print("Enter new module code to switch TO: ");
        String newModuleCode = scan.nextLine().trim().toUpperCase();

        if (student.getRegisteredModules().contains(newModuleCode)) {
            System.out.println("Student is already registered in this module.");
            return;
        }

        Module newModule = ModuleManager.findModuleByCode(newModuleCode);
        if (newModule == null) {
            System.out.println("Module does not exist.");
            return;
        }

        // Also remove from classes of old module
        ClassManager.loadFromFile();
        ClassManager.removeStudentFromModuleClasses(studentEmail, oldModuleCode);

        // Switch modules
        boolean success = ModuleManager.switchStudentModule(oldModuleCode, newModuleCode, studentEmail);
        if (success) {
            student.getRegisteredModules().remove(oldModuleCode);
            student.getRegisteredModules().add(newModuleCode);
            UserManager.saveToFile();
            System.out.println("Student successfully switched from " + oldModuleCode + " to " + newModuleCode);
        } else {
            System.out.println("Failed to switch student module.");
        }
    }

}
