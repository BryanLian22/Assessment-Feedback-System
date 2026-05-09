package Users;

import App.Menu;
import IOManage.UserManager;
import IOManage.ModuleManager;
import IOManage.ClassManager;
import IOManage.CommentManager;
import Entity.Module;
import Entity.ClassGroup;
import Entity.Comment;
import Users.Lecturer;
import Users.AcademicLeader;

import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;

public class Student extends User {

    private ArrayList<String> registeredModules = new ArrayList<>();
    private ArrayList<String> registeredClasses = new ArrayList<>();
    private ArrayList<String> results = new ArrayList<>();

    public Student(String name, String email, String password) {
        super(name, email, password);

        // 🔁 reload module registration after restart
        loadRegisteredModulesFromModules();
    }

    // ============================
    // MENU
    // ============================
    @Override
    public void showMenu() {
        Menu.studentMenu(this);
    }

    // ============================
    // LOAD MODULE REGISTRATION FROM FILE
    // ============================
    private void loadRegisteredModulesFromModules() {

        ModuleManager.loadAllModules();

        for (Module m : ModuleManager.modules) {
            if (m.getStudentEmails().contains(this.getEmail())) {
                if (!registeredModules.contains(m.getCode())) {
                    registeredModules.add(m.getCode());
                }
            }
        }
    }

    // ============================
    // REGISTER CLASS (STUDENT-ONLY SAFE VERSION)
    // ============================
    public void registerClass() {

        // 🔁 HARD REFRESH EVERYTHING (VERY IMPORTANT)
        ModuleManager.loadAllModules();
        ClassManager.loadFromFile();
        loadRegisteredModulesFromModules();

        if (registeredModules.isEmpty()) {
            System.out.println("You must register modules before registering classes.");
            pause();
            return;
        }

        System.out.print("Enter class code to register (or 'n' to cancel): ");
        String classCode = scan.nextLine().trim().toUpperCase();

        if (classCode.equalsIgnoreCase("N")) {
            pause();
            return;
        }

        // ❌ prevent duplicate (session + restart)
        if (registeredClasses.contains(classCode)) {
            System.out.println("You already registered this class.");
            pause();
            return;
        }

        // class must exist
        ClassGroup cg = ClassManager.findByClassCode(classCode);
        if (cg == null) {
            System.out.println("Class does not exist.");
            pause();
            return;
        }

        // ⚠️ DO NOT TRUST MODULE OBJECT STATE – USE CODE ONLY
        String moduleCode = cg.getModule().getCode();

        // class must belong to registered module
        if (!registeredModules.contains(moduleCode)) {
            System.out.println("You have not registered the module for this class.");
            pause();
            return;
        }

        // final safety check against file truth
        Module m = ModuleManager.findModuleByCode(moduleCode);
        if (m == null || !m.getStudentEmails().contains(this.getEmail())) {
            System.out.println("You are not registered under this module.");
            pause();
            return;
        }

        // Check if already registered in ClassGroup (file-based check)
        if (cg.getStudentEmails().contains(this.getEmail())) {
            System.out.println("You are already registered in this class.");
            pause();
            return;
        }

        // ✅ ENFORCE: Only 1 class per module - remove from other classes in same module
        ClassManager.removeStudentFromModuleClasses(this.getEmail(), moduleCode);

        // ✅ register class (add student email to ClassGroup and save to file)
        cg.addStudent(this.getEmail());
        ClassManager.updateClassGroup(cg);

        // Also update session list
        registeredClasses.add(classCode);
        UserManager.saveToFile();

        System.out.println("Class registered successfully.");
        pause();
    }

    // ============================
    // VIEW TIMETABLE
    // ============================
    public void viewTimetable() {

        System.out.println("\n===== YOUR TIMETABLE =====");

        // Load class groups to check student enrollment
        ClassManager.loadFromFile();

        // Collect all class groups where this student's email is registered
        ArrayList<ClassGroup> studentClasses = new ArrayList<>();
        for (ClassGroup cg : ClassManager.classGroups) {
            if (cg.getStudentEmails().contains(this.getEmail())) {
                studentClasses.add(cg);
            }
        }

        if (studentClasses.isEmpty()) {
            System.out.println("You are not registered in any classes yet.");
            pause();
            return;
        }

        // Display header
        String[] columns = {"Class Code", "Time", "Classroom"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (ClassGroup cg : studentClasses) {
            String time = cg.getTime() != null && !cg.getTime().isEmpty() ? cg.getTime() : "N/A";
            String classroom = cg.getClassroom() != null && !cg.getClassroom().isEmpty() ? cg.getClassroom() : "N/A";
            model.addRow(new Object[]{cg.getClassCode(), time, classroom});
        }

        // Display the table (you can customize this part to match your UI framework)
        // For console output, we'll just print the model data
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                System.out.print(model.getValueAt(i, j) + "\t");
            }
            System.out.println();
        }

        pause();
    }

    // ============================
    // VIEW RESULTS
    // ============================
    public void viewResults() {

        System.out.println("\n===== YOUR RESULTS =====");

        // Load marks from marks.txt
        ArrayList<String> studentResults = new ArrayList<>();
        File marksFile = new File("data/marks.txt");
        
        if (!marksFile.exists()) {
            System.out.println("No marks file found.");
            pause();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(marksFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                // Format: assessmentID|assessmentName|assessmentType|lecturerEmail|studentEmail|studentName|marks|feedback
                String[] parts = line.split("\\|");
                
                if (parts.length >= 7) {
                    String studentEmail = parts[4].trim(); // Student email is at index 4
                    
                    // Check if this mark belongs to the current student
                    if (studentEmail.equalsIgnoreCase(this.getEmail())) {
                        String assessmentId = parts[0].trim();
                        String assessmentName = parts[1].trim();
                        String type = parts[2].trim();
                        String lecturerEmail = parts[3].trim();
                        double marks = Double.parseDouble(parts[6].trim());
                        String comment = parts.length > 7 ? parts[7].trim() : "";
                        
                        // Load assessment to get max marks
                        // Find lecturer and load their assessments
                        IOManage.UserManager.loadFromFile();
                        Users.User user = IOManage.UserManager.findByEmail(lecturerEmail);
                        double maxMarks = 100.0; // default
                        
                        if (user instanceof Users.Lecturer) {
                            Users.Lecturer lec = (Users.Lecturer) user;
                            IOManage.AssessmentManager.loadLecturerData(lec);
                            Users.Lecturer.Assessment assessment = lec.getAssessment(assessmentId);
                            if (assessment != null) {
                                maxMarks = assessment.getMaxMarks();
                            }
                        }
                        
                        // Calculate percentage and grade
                        double percentage = (marks / maxMarks) * 100.0;
                        String grade = getGradeFromPercentage(percentage);
                        
                        // Store formatted result with percentage and grade
                        String resultLine = String.format("%-15s %-15s %-10s %-20s %6.2f/%6.2f (%5.1f%%) [%s]", 
                                assessmentName, 
                                assessmentId, 
                                type, 
                                lecturerEmail, 
                                marks, 
                                maxMarks,
                                percentage,
                                grade);
                        if (!comment.isEmpty()) {
                            resultLine += " | Feedback: " + comment;
                        }
                        studentResults.add(resultLine);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading marks: " + e.getMessage());
            pause();
            return;
        }

        if (studentResults.isEmpty()) {
            System.out.println("No results available yet.");
            pause();
            return;
        }

        // Display header
        System.out.println(String.format("%-15s %-15s %-10s %-20s %-18s %-10s", 
                "Assessment", "ID", "Type", "Lecturer Email", "Score", "Grade"));
        System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────────");

        for (String result : studentResults) {
            System.out.println(result);
        }

        pause();
    }
    
    /**
     * Get letter grade based on percentage
     * Percentage is calculated as: (marks / totalMarks) * 100
     * @param marks obtained marks
     * @param totalMarks maximum marks
     * @return Letter grade (A, B, C, D, E, F)
     */
    private String getGrade(double marks, double totalMarks) {
        // Calculate percentage
        double percentage = Entity.GradingSystem.calculatePercentage(marks, totalMarks);
        
        // Load grading system from file
        IOManage.GradingSystemManager.loadFromFile();
        Entity.GradingSystem gradingSystem = IOManage.GradingSystemManager.getGradingSystem();
        
        return gradingSystem.getGrade(percentage);
    }
    
    /**
     * Get letter grade from percentage value directly
     */
    private String getGradeFromPercentage(double percentage) {
        IOManage.GradingSystemManager.loadFromFile();
        Entity.GradingSystem gradingSystem = IOManage.GradingSystemManager.getGradingSystem();
        
        return gradingSystem.getGrade(percentage);
    }

    public void addResult(String resultText) {
        results.add(resultText);
        UserManager.saveToFile();
    }

    // ============================
    // COMMENT LECTURER
    // ============================
    public void commentLecturer() {

        CommentManager.loadFromFile();

        System.out.print("Enter lecturer email (or 'n' to cancel): ");
        String lecEmail = scan.nextLine().trim();

        if (lecEmail.equalsIgnoreCase("N")) {
            pause();
            return;
        }

        Lecturer lecturer = CommentManager.findLecturerByEmail(lecEmail);
        if (lecturer == null) {
            System.out.println("Lecturer not found.");
            pause();
            return;
        }

        AcademicLeader leader = CommentManager.findLeaderForLecturer(lecEmail);
        if (leader == null) {
        System.out.println("No Academic Leader found for lecturer " + lecEmail +
                           ". Comment not saved.");
        pause();
        return;
        }
        
        String leaderEmail = leader.getEmail();

        System.out.print("Enter your comment: ");
        String content = scan.nextLine();

        if (content == null || content.trim().isEmpty()) {
            System.out.println("Comment cannot be empty.");
            pause();
            return;
        }

        Comment comment = new Comment(
                this.getName(),
                this.getEmail(),
                "Student",
                lecturer.getName(),
                lecturer.getEmail(),
                leaderEmail,
                content
        );

        CommentManager.addComment(comment);
        System.out.println("Comment submitted successfully.");
        pause();
    }

    // ============================
    // VIEW ASSESSMENTS
    // ============================
    public void viewAssessments() {
        System.out.println("\n===== YOUR ASSESSMENTS =====");

        // Load necessary data
        ModuleManager.loadAllModules();
        IOManage.UserManager.loadFromFile();
        loadRegisteredModulesFromModules();

        if (registeredModules.isEmpty()) {
            System.out.println("You are not registered in any modules.");
            pause();
            return;
        }

        // Collect all assessments from registered modules
        ArrayList<AssessmentInfo> allAssessments = new ArrayList<>();

        for (String moduleCode : registeredModules) {
            Module module = ModuleManager.findModuleByCode(moduleCode);
            if (module == null) continue;

            // Get all lecturers for this module
            ArrayList<String> lecturerEmails = module.getLecturerEmails();
            if (lecturerEmails == null || lecturerEmails.isEmpty()) continue;

            for (String lecturerEmail : lecturerEmails) {
                Users.User user = IOManage.UserManager.findByEmail(lecturerEmail);
                if (!(user instanceof Users.Lecturer)) continue;

                Users.Lecturer lecturer = (Users.Lecturer) user;
                IOManage.AssessmentManager.loadLecturerData(lecturer);

                // Get assessments for this module
                java.util.List<Users.Lecturer.Assessment> moduleAssessments = 
                    lecturer.getAssessmentsByModule(moduleCode);

                for (Users.Lecturer.Assessment assessment : moduleAssessments) {
                    // Check if student has completed this assessment (has marks)
                    boolean completed = false;
                    double studentMarks = 0.0;
                    String feedback = "";

                    java.util.List<Users.Lecturer.StudentMark> marks = 
                        lecturer.getMarksForAssessment(assessment.getAssessmentId());

                    for (Users.Lecturer.StudentMark mark : marks) {
                        if (mark.getStudentId().equalsIgnoreCase(this.getEmail())) {
                            completed = true;
                            studentMarks = mark.getMarks();
                            feedback = mark.getFeedback();
                            break;
                        }
                    }

                    allAssessments.add(new AssessmentInfo(
                        assessment.getAssessmentId(),
                        assessment.getAssessmentName(),
                        assessment.getType().getDisplayName(),
                        moduleCode,
                        assessment.getMaxMarks(),
                        assessment.getDescription(),
                        lecturerEmail,
                        completed,
                        studentMarks,
                        feedback
                    ));
                }
            }
        }

        if (allAssessments.isEmpty()) {
            System.out.println("No assessments found for your registered modules.");
            pause();
            return;
        }

        // Sort assessments: incomplete first, then completed
        allAssessments.sort((a1, a2) -> {
            if (a1.completed == a2.completed) {
                // If both have same completion status, sort by module code then assessment ID
                int moduleCompare = a1.moduleCode.compareTo(a2.moduleCode);
                return moduleCompare != 0 ? moduleCompare : a1.assessmentId.compareTo(a2.assessmentId);
            }
            return a1.completed ? 1 : -1; // Incomplete first
        });

        // Display assessments
        System.out.println("\nLegend: [✓] Completed (marks entered) | [ ] Pending");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────────");
        System.out.printf("%-3s %-12s %-20s %-12s %-10s %-12s %-8s%n", 
                "✓", "ID", "Name", "Module", "Type", "Max Marks", "Status");
        System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────────");

        for (AssessmentInfo info : allAssessments) {
            String statusIcon = info.completed ? "[✓]" : "[ ]";
            String status = info.completed ? 
                String.format("%.1f/%.0f", info.studentMarks, info.maxMarks) : "Pending";

            // Highlight completed assessments
            if (info.completed) {
                System.out.print("\033[32m"); // Green color for completed
            }

            System.out.printf("%-3s %-12s %-20s %-12s %-10s %-12.0f %-8s%n", 
                    statusIcon,
                    info.assessmentId,
                    truncate(info.assessmentName, 20),
                    info.moduleCode,
                    truncate(info.type, 10),
                    info.maxMarks,
                    status);

            if (info.completed) {
                System.out.print("\033[0m"); // Reset color
            }
        }

        System.out.println("─────────────────────────────────────────────────────────────────────────────────────────────────");
        
        // Summary
        long completedCount = allAssessments.stream().filter(a -> a.completed).count();
        long pendingCount = allAssessments.size() - completedCount;
        System.out.printf("\nTotal: %d assessments | Completed: %d | Pending: %d%n", 
                allAssessments.size(), completedCount, pendingCount);

        // Option to view details
        System.out.print("\nEnter Assessment ID to view details (or press Enter to go back): ");
        String selectedId = scan.nextLine().trim();

        if (!selectedId.isEmpty()) {
            viewAssessmentDetails(allAssessments, selectedId);
        }

        pause();
    }

    /**
     * View detailed information about a specific assessment
     */
    private void viewAssessmentDetails(ArrayList<AssessmentInfo> assessments, String assessmentId) {
        AssessmentInfo selected = null;
        for (AssessmentInfo info : assessments) {
            if (info.assessmentId.equalsIgnoreCase(assessmentId)) {
                selected = info;
                break;
            }
        }

        if (selected == null) {
            System.out.println("\nAssessment not found.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ASSESSMENT DETAILS                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println("Assessment ID    : " + selected.assessmentId);
        System.out.println("Name             : " + selected.assessmentName);
        System.out.println("Type             : " + selected.type);
        System.out.println("Module           : " + selected.moduleCode);
        System.out.println("Maximum Marks    : " + selected.maxMarks);
        System.out.println("Description      : " + selected.description);
        System.out.println("Lecturer         : " + selected.lecturerEmail);
        System.out.println("─────────────────────────────────────────────────────────────────");
        
        if (selected.completed) {
            double percentage = (selected.studentMarks / selected.maxMarks) * 100.0;
            String grade = getGradeFromPercentage(percentage);
            
            System.out.println("Status           : ✓ COMPLETED");
            System.out.printf("Your Score       : %.2f / %.2f (%.1f%%) [Grade: %s]%n", 
                    selected.studentMarks, selected.maxMarks, percentage, grade);
            
            if (!selected.feedback.isEmpty() && !selected.feedback.equals("No feedback available")) {
                System.out.println("\nFeedback from Lecturer:");
                System.out.println("┌────────────────────────────────────────────────────────────┐");
                System.out.println("│ " + wrapText(selected.feedback, 58) + " │");
                System.out.println("└────────────────────────────────────────────────────────────┘");
            }
        } else {
            System.out.println("Status           : [ ] PENDING (No marks entered yet)");
        }
        System.out.println("─────────────────────────────────────────────────────────────────");
    }

    /**
     * Helper method to truncate long strings
     */
    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() <= length ? str : str.substring(0, length - 3) + "...";
    }

    /**
     * Helper method to wrap text for better display
     */
    private String wrapText(String text, int width) {
        if (text == null || text.length() <= width) return text;
        // Simple wrapping - just truncate for now
        return text.length() > width ? text.substring(0, width - 3) + "..." : text;
    }

    /**
     * Inner class to hold assessment information
     */
    private static class AssessmentInfo {
        String assessmentId;
        String assessmentName;
        String type;
        String moduleCode;
        double maxMarks;
        String description;
        String lecturerEmail;
        boolean completed;
        double studentMarks;
        String feedback;

        AssessmentInfo(String assessmentId, String assessmentName, String type, 
                      String moduleCode, double maxMarks, String description,
                      String lecturerEmail, boolean completed, double studentMarks, 
                      String feedback) {
            this.assessmentId = assessmentId;
            this.assessmentName = assessmentName;
            this.type = type;
            this.moduleCode = moduleCode;
            this.maxMarks = maxMarks;
            this.description = description;
            this.lecturerEmail = lecturerEmail;
            this.completed = completed;
            this.studentMarks = studentMarks;
            this.feedback = feedback;
        }
    }

    // ============================
    // UTIL
    // ============================
    private void pause() {
        System.out.println("Press Enter to continue...");
        scan.nextLine();
    }

    // ============================
    // GETTERS
    // ============================
    public ArrayList<String> getRegisteredModules() {
        return registeredModules;
    }

    public ArrayList<String> getRegisteredClasses() {
        return registeredClasses;
    }

    public ArrayList<String> getResultsList() {
        return results;
    }
    
    public void editProfile(String newName, String newEmail) {
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
        } else if (newEmail != null && !newEmail.trim().isEmpty()) {
            setEmail(newEmail);
            IOManage.UserManager.saveToFile();
        }
    }
}
