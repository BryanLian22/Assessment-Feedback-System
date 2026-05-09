package Users;

import App.Menu;
import Entity.ClassGroup;
import Entity.Comment;
import Entity.Module;
import Entity.Report;
import IOManage.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AcademicLeader extends User {//user is extended,academic leader having properties of user and can use their function.

    private static final Scanner scan = new Scanner(System.in);//init scanner so that we can use it to read the line key in by user.
    private ArrayList<String> lecturerEmails = new ArrayList<>();

    public AcademicLeader(String name, String email, String password) {
        super(name, email, password);
    }

    @Override
    public void showMenu() { //function that handle the academic menu
        Menu.academicLeaderMenu(this);
    }

    public void manageModules() {

        // always load latest modules for this leader
        ModuleManager.loadFromFile(email);

        while (true) {
            System.out.println("\n=== Module Management Menu ===");
            System.out.println("1. Add Module");
            System.out.println("2. Edit Module");
            System.out.println("3. Delete Module");
            System.out.println("4. Assign Lecturer");
            System.out.println("5. Remove Lecturer");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scan.nextLine();

            switch (choice) {

                // ================= ADD MODULE =================
                case "1": {
                    String code;

                    while (true) {
                        System.out.print("Enter Module Code: ");
                        code = scan.nextLine().trim();
                        
                        if (code.isEmpty()){
                            System.out.println("Module code cannot be empty.");
                        }

                        if (ModuleManager.findModuleByCode(code) != null) {
                            System.out.println("Module code already exists.");
                        } else {
                            break;
                        }
                    }

                    // ===== Validate Module Name =====
                    String name;
                    while (true) {
                        System.out.print("Enter Module Name: ");
                        name = scan.nextLine().trim();

                        if (name.isEmpty()) {
                            System.out.println("Module name cannot be empty.");
                        } else {
                            break;
                        }
                    }

                    // no lecturer during creation
                    Module newModule = new Module(code, name, email);
                    ModuleManager.addModule(newModule);

                    System.out.println("Module added successfully!");
                    break;
                }

                // ================= EDIT MODULE =================
                case "2": {
                    System.out.print("Enter Module Code to edit: ");
                    String editCode = scan.nextLine();

                    Module moduleToEdit = ModuleManager.findModuleByCode(editCode);

                    if (moduleToEdit == null || !moduleToEdit.getAcademicLeaderEmail().equalsIgnoreCase(this.getEmail())) {
                        System.out.println("Module not found or not under your management.");
                        break;
                    }

                    System.out.print("Enter new Module Name: ");
                    String newName = scan.nextLine();

                    ModuleManager.editModule(moduleToEdit, this, newName); // pass leader to enforce check
                    System.out.println("Module updated successfully!");
                    break;
                }

                // ================= DELETE MODULE =================
                case "3": {
                    System.out.print("Enter Module Code to delete: ");
                    String delCode = scan.nextLine();

                    Module moduleToDelete = ModuleManager.findModuleByCode(delCode);

                    if (moduleToDelete == null || !moduleToDelete.getAcademicLeaderEmail().equalsIgnoreCase(this.getEmail())) {
                        System.out.println("Module not found or not under your management.");
                        break;
                    }

                    // Check if module has classes
                    if (ClassManager.hasClassesForModule(delCode)) {
                        System.out.println("Cannot delete module: There are classes associated with this module. Please delete all classes first.");
                        break;
                    }

                    ModuleManager.deleteModule(moduleToDelete, this); // pass leader to enforce check
                    System.out.println("Module deleted successfully!");
                    break;
                }

                // ================= ASSIGN LECTURER =================
                case "4": {
                    System.out.print("Enter Module Code: ");
                    String code = scan.nextLine();

                    Module m = ModuleManager.findModuleByCode(code);
                    if (m == null || !m.getAcademicLeaderEmail().equalsIgnoreCase(this.getEmail())) {
                        System.out.println("Module not found or not under your management.");
                        break;
                    }

                    System.out.print("Enter Lecturer Email to assign: ");
                    String lecEmail = scan.nextLine();

                    boolean success = ModuleManager.assignLecturer(m, lecEmail, this);
                    if (success) {
                        System.out.println("Lecturer assigned successfully!");
                    } else {
                        System.out.println("Failed - lecturer not under you or already assigned.");
                    }
                    break;
                }

                case "5": {
                    System.out.print("Enter Module Code: ");
                    String code = scan.nextLine();

                    Module m = ModuleManager.findModuleByCode(code);
                    if (m == null || !m.getAcademicLeaderEmail().equalsIgnoreCase(this.getEmail())) {
                        System.out.println("Module not found or not under your management.");
                        break;
                    }

                    System.out.print("Enter Lecturer Email to remove: ");
                    String lecEmail = scan.nextLine();

                    boolean removed = ModuleManager.removeLecturer(m, lecEmail, this);
                    if (removed) {
                        System.out.println("Lecturer removed successfully!");
                    } else {
                        System.out.println("Lecturer not assigned to this module.");
                    }

                    break;
                }

                case "0":
                    return;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public void analyzeReports() {
        while (true) {
            System.out.println("\n=== Analyze Reports ===");
            System.out.println("1. Add Report");
            System.out.println("2. View Reports");
            System.out.println("3. Delete Report");
            System.out.println("4. View All Report from other leader");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String choice = scan.nextLine();

            switch (choice) {
                case "1":
                    addReport();
                    break;
                case "2":
                    viewReport();//only current leader report
                    break;
                case "3":
                    deleteReport();
                    break;
                case "4":
                    viewAllReport();//all leader report
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    public ArrayList<String> getLecturerEmails() {
        return lecturerEmails;
    }

    public void addLecturerEmail(String email) {
        lecturerEmails.add(email);
    }

    private void addReport() {
        ModuleManager.modules.clear();
        ModuleManager.loadFromFile(email);
        ReportManager.loadClassGroups();

        for (int i = 0; i < ModuleManager.modules.size(); i++) {
            Module m = ModuleManager.modules.get(i);
            System.out.println((i + 1) + ". " + m.getCode());
        }

        System.out.print("Select module (number or code): ");
        String selection = scan.nextLine().trim();

        Module selected;
        if (selection.matches("\\d+")) { // user keyed the list number
            int choice = Integer.parseInt(selection);
            if (choice < 1 || choice > ModuleManager.modules.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            selected = ModuleManager.modules.get(choice - 1);
        } else {
            // allow typing the actual module code
            selected = ModuleManager.findModuleByCode(selection);
            if (selected == null) {
                System.out.println("Module not found: " + selection);
                return;
            }
        }

        // show the report by using generateModuleReport
        boolean ok = ReportManager.generateModuleReport(selected);
        if (!ok){
            //no classes -> (do NOT save empty report)
            System.out.println("Report not saved because this module has no classes.");
            return;
        }
        // save the report data to text file.
        Report r = ReportManager.generateReportObject(selected, this.getEmail());
        ReportManager.addReport(r, this.getEmail());
        System.out.println("Report saved.");
    }

    private void viewReport() {//view only current leader reports
        ReportManager.loadReportFromFile(email);//load report from the file that only under this leader

        if (ReportManager.reports.isEmpty()) {//check is report.txt is empty or not found.
            System.out.println("No reports found.");
            return;
        }

        for (Report r : ReportManager.reports) { //go through reports arraylist and loop all data out.
            System.out.println("\nReport ID: " + r.getReportId());
            System.out.println("Module Code: " + r.getModuleCode());
            System.out.println("Total Students: " + r.getTotalStudents());
            System.out.println("Overall Average: " + r.getOverallAverage());
        }
    }

    private void deleteReport() {
        ReportManager.loadReportFromFile(email); // load only current leader's reports

        if (ReportManager.reports.isEmpty()) {
            System.out.println("No reports to delete.");
            return;
        }

        System.out.print("\nEnter Report ID to delete: ");
        String id = scan.nextLine();

        Report r = ReportManager.findReportByID(id);
        if (r == null) {
            System.out.println("Report ID not found.");
            return;
        }

        boolean deleted = ReportManager.deleteReport(r);
        if (deleted) {
            System.out.println("Report deleted successfully.");
        }
    }

    private void viewAllReport() {//view all the report related all leader.

        // load ALL reports (no filter)
        ReportManager.loadAllReportFromFile();

        if (ReportManager.reports.isEmpty()) {
            System.out.println("No reports found.");
            return;
        }

        System.out.println("\n=== All Reports ===");

        for (int i = 0; i < ReportManager.reports.size(); i++) {
            Report r = ReportManager.reports.get(i);

            System.out.println("\n--- Report " + (i + 1) + " ---");
            System.out.println("Report ID           : " + r.getReportId());
            System.out.println("Module Code         : " + r.getModuleCode());
            System.out.println("Academic Leader     : " + r.getAcademicLeaderEmail());
            System.out.println("Total Students      : " + r.getTotalStudents());
            System.out.println("Overall Average     : " + r.getOverallAverage());
        }
    }

    public void viewComment() {
        // load comments.txt to java , comments arraylist
        CommentManager.loadFromFile();

        // filter the comments list by using the email, only using the comments with related academicleader email
        ArrayList<Comment> myComments = CommentManager.filterCommentsByAcademicLeader(this.getEmail());

        // 3. skip if no comments
        if (myComments.isEmpty()) {
            System.out.println("\nNo comments available for you.");
            return;
        }

        // 4. Display the filtered comments
        System.out.println("\n=== Comments Related to Your Team ===");

        for (int i = 0; i < myComments.size(); i++) {
            Comment c = myComments.get(i);

            System.out.println("\n--- Comment " + (i + 1) + " ---");
            System.out.println("Student Name     : " + c.getStudentName());
            System.out.println("Student Email    : " + c.getStudentEmail());
            System.out.println("Lecturer Email   : " + c.getLecturerEmail());
            System.out.println("Comment          : " + c.getContent());
        }
    } //view comment of lecturer made by students//
    
    /**
     * Register a lecturer to a class group
     * Academic Leader can register lecturers to classes for modules they manage
     */
    public boolean registerLecturerToClass(String lecturerEmail, String classCode) {
        // Check if lecturer is under this academic leader
        if (!lecturerEmails.contains(lecturerEmail)) {
            System.out.println("Lecturer " + lecturerEmail + " is not under your management.");
            return false;
        }
        
        // Load all modules to check ownership
        ModuleManager.loadAllModules();
        
        // Load all class groups from classgroup.txt
        ClassManager.loadFromFile();
        
        // Find the class group
        ClassGroup classGroup = ClassManager.findByClassCode(classCode);
        if (classGroup == null) {
            System.out.println("Class group not found: " + classCode);
            return false;
        }
        
        // Check if the module belongs to this academic leader
        Module module = classGroup.getModule();
        if (!module.getAcademicLeaderEmail().equalsIgnoreCase(this.getEmail())) {
            System.out.println("You do not manage module " + module.getCode() + ". Cannot register lecturer to this class.");
            return false;
        }
        
        // Check if lecturer is assigned to the module
        if (!module.getLecturerEmails().contains(lecturerEmail)) {
            System.out.println("Lecturer " + lecturerEmail + " is not assigned to module " + module.getCode() + ".");
            return false;
        }
        
        // Check if already registered
        if (classGroup.getLecturerEmails().contains(lecturerEmail)) {
            System.out.println("Lecturer " + lecturerEmail + " is already registered to class " + classCode);
            return false;
        }
        
        // Register the lecturer
        classGroup.addLecturer(lecturerEmail);
        ClassManager.updateClassGroup(classGroup);
        System.out.println("Successfully registered lecturer " + lecturerEmail + " to class " + classCode);
        return true;
    }
    
    /**
     * Menu for registering lecturer to class
     */
    public void registerLecturerToClassMenu() {
        System.out.println("\n--- Register Lecturer to Class ---");
        
        // Check if there are any lecturers under this leader
        if (lecturerEmails.isEmpty()) {
            System.out.println("No lecturers under your management.");
            return;
        }
        
        // Display lecturers
        System.out.println("\nYour Lecturers:");
        for (int i = 0; i < lecturerEmails.size(); i++) {
            System.out.println((i + 1) + ". " + lecturerEmails.get(i));
        }
        
        System.out.print("\nSelect lecturer number: ");
        try {
            int lecturerChoice = Integer.parseInt(scan.nextLine());
            if (lecturerChoice < 1 || lecturerChoice > lecturerEmails.size()) {
                System.out.println("Invalid choice.");
                return;
            }
            String selectedLecturerEmail = lecturerEmails.get(lecturerChoice - 1);
            
            // Load modules managed by this leader
            ModuleManager.loadFromFile(email);
            
            if (ModuleManager.modules.isEmpty()) {
                System.out.println("No modules under your management.");
                return;
            }
            
            // Load all class groups
            ClassManager.loadFromFile();
            
            // Find available classes for modules managed by this leader
            ArrayList<ClassGroup> availableClasses = new ArrayList<>();
            for (Module module : ModuleManager.modules) {
                // Check if lecturer is assigned to this module
                if (module.getLecturerEmails().contains(selectedLecturerEmail)) {
                    // Find classes for this module
                    for (ClassGroup cg : ClassManager.classGroups) {
                        if (cg.getModule().getCode().equalsIgnoreCase(module.getCode())) {
                            // Only show classes not already registered by this lecturer
                            if (!cg.getLecturerEmails().contains(selectedLecturerEmail)) {
                                availableClasses.add(cg);
                            }
                        }
                    }
                }
            }
            
            if (availableClasses.isEmpty()) {
                System.out.println("No available classes for lecturer " + selectedLecturerEmail + 
                                 ". They may have already been registered to all available classes.");
                return;
            }
            
            // Display available classes
            System.out.println("\nAvailable Classes for " + selectedLecturerEmail + ":");
            System.out.println("─────────────────────────────────────────────────────────────");
            System.out.printf("%-5s %-15s %-15s %-25s %-15s%n", "No.", "Class Code", "Module", "Time", "Classroom");
            System.out.println("─────────────────────────────────────────────────────────────");
            
            for (int i = 0; i < availableClasses.size(); i++) {
                ClassGroup cg = availableClasses.get(i);
                String classCode = cg.getClassCode();
                String moduleCode = cg.getModule().getCode();
                String moduleName = cg.getModule().getName();
                String time = cg.getTime() != null && !cg.getTime().isEmpty() ? cg.getTime() : "Not set";
                String classroom = cg.getClassroom() != null && !cg.getClassroom().isEmpty() ? cg.getClassroom() : "Not set";
                
                System.out.printf("%-5d %-15s %-15s %-25s %-15s%n", 
                    (i + 1), 
                    classCode, 
                    moduleCode + " (" + moduleName + ")", 
                    time, 
                    classroom);
            }
            
            System.out.println("─────────────────────────────────────────────────────────────");
            System.out.print("\nEnter class number to register (or 0 to cancel): ");
            
            int classChoice = Integer.parseInt(scan.nextLine());
            
            if (classChoice == 0) {
                System.out.println("Registration cancelled.");
                return;
            }
            
            if (classChoice < 1 || classChoice > availableClasses.size()) {
                System.out.println("Invalid choice.");
                return;
            }
            
            ClassGroup selectedClass = availableClasses.get(classChoice - 1);
            registerLecturerToClass(selectedLecturerEmail, selectedClass.getClassCode());
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Remove a lecturer from a class group
     * Only allowed if this academic leader manages the module containing the class
     */
    public boolean removeLecturerFromClass(String lecturerEmail, String classCode) {
        if (lecturerEmail == null || classCode == null) return false;

        // load latest data
        ModuleManager.loadAllModules();
        ClassManager.loadFromFile();

        ClassGroup classGroup = ClassManager.findByClassCode(classCode);
        if (classGroup == null) {
            System.out.println("Class group not found: " + classCode);
            return false;
        }

        Module module = classGroup.getModule();
        if (module == null || !module.getAcademicLeaderEmail().equalsIgnoreCase(this.getEmail())) {
            System.out.println("You do not manage module " + (module != null ? module.getCode() : "unknown") + ". Cannot remove lecturer from this class.");
            return false;
        }

        if (!classGroup.getLecturerEmails().contains(lecturerEmail)) {
            System.out.println("Lecturer " + lecturerEmail + " is not assigned to class " + classCode);
            return false;
        }

        // remove lecturer and persist
        classGroup.removeLecturer(lecturerEmail);
        ClassManager.updateClassGroup(classGroup);
        System.out.println("Removed lecturer " + lecturerEmail + " from class " + classCode);
        return true;
    }
    
    /**
     * Edit Academic Leader profile information
     */
    public void editProfile(String newName, String newEmail, String newPassword) {
        String oldEmail = this.getEmail();
        
        if (newName != null && !newName.trim().isEmpty()) {
            setName(newName);
        }
        
        if (newEmail != null && !newEmail.trim().isEmpty() && !newEmail.equals(oldEmail)) {
            // Check if new email already exists in system
            if (IOManage.EmailUpdateManager.emailExistsInSystem(newEmail)) {
                System.out.println("✗ Error: Email " + newEmail + " already exists in the system.");
                return;
            }
            
            // Update email across all files
            if (!IOManage.EmailUpdateManager.updateEmailAcrossSystem(oldEmail, newEmail)) {
                System.out.println("✗ Failed to update email.");
                return;
            }
            System.out.println("✓ Email updated successfully!");
        }
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            setPassword(newPassword);
        }
        
        UserManager.saveToFile();
    }
}
