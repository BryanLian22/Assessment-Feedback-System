package Users;

import App.Menu;
import Entity.ClassGroup;
import Entity.Module;
import IOManage.AssessmentManager;
import IOManage.ClassManager;
import IOManage.ModuleManager;
import IOManage.UserManager;
import java.util.*;
import javax.swing.table.DefaultTableModel;


public class Lecturer extends User {
    private String leaderEmail;
    
    // Collections to manage assessments and student marks
    private final Map<String, Assessment> assessments; // Key: assessmentId, Value: Assessment object
    private final Map<String, List<StudentMark>> studentMarks; // Key: assessmentId, Value: List of student marks
    
    /**
     * Pause execution for 1 second
     */
    private void pause() {
        try { 
            Thread.sleep(1000); 
        } catch (InterruptedException e) { 
        }
    }
    
    /**
     * Constructor for Lecturer
     */
    public Lecturer(String name, String email, String password, String leaderEmail) {
        super(name, email, password);
        this.leaderEmail = leaderEmail;
        this.assessments = new HashMap<>();
        this.studentMarks = new HashMap<>();
        // Load saved data when lecturer is created
        AssessmentManager.loadLecturerData(this);
    }
    
    @Override
    public void showMenu() {
        Menu.lecturerMenu(this);

    }
    
    // ==================== Profile Management ====================
    
    /**
     * Edit personal profile information
     */
    public void editProfile(String newName, String newEmail, String newPhoneNumber, 
                           String newDepartment, String newOfficeLocation) {
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
            // ✅ UPDATE: Set the new email in the current lecturer object
            this.setEmail(newEmail);
            System.out.println("✓ Email updated successfully!");
        }
        
        // Remove phone, department, and office location updates
        
        AssessmentManager.saveLecturerData(this);
        pause();
    }
    
    /**
     * Get profile information
     */
    public String getProfileInfo() {
        StringBuilder profile = new StringBuilder();
        // Use parent class methods for name and email
        profile.append("Name: ").append(getName()).append("\n");
        profile.append("Email: ").append(getEmail()).append("\n");
        profile.append("Leader Email: ").append(leaderEmail).append("\n");
        pause();
        return profile.toString();
    }
    
    // ==================== Assessment Type Design ====================
    
    /**
     * Design and create a new assessment type for a module
     */
    public void designAssessment(String assessmentId, String moduleCode, String assessmentName, 
                                AssessmentType type, double maxMarks, 
                                String description) {
        // Check if max marks is greater than 1
        if (maxMarks <= 1) {
            throw new IllegalArgumentException("Maximum marks must be greater than 1.");
        }
        
        // Check if module exists
        ModuleManager.loadAllModules();
        Module module = ModuleManager.findModuleByCode(moduleCode);
        if (module == null) {
            throw new IllegalArgumentException("Module with code '" + moduleCode + "' does not exist. Cannot design assessment.");
        }
        
        // Check if lecturer is assigned to this module
        if (!module.getLecturerEmails().contains(this.getEmail())) {
            throw new IllegalArgumentException("You are not assigned to module '" + moduleCode + "'. Cannot design assessment for this module.");
        }
        Assessment assessment = new Assessment(assessmentId, moduleCode, assessmentName, 
                                              type, maxMarks, description);
        assessments.put(assessmentId, assessment);
        studentMarks.put(assessmentId, new ArrayList<>());
        AssessmentManager.saveLecturerData(this); // Auto-save after creating assessment
        pause();
    }
    
    /**
     * Design assessment without auto-save (used by AssessmentManager)
     */
    public void designAssessmentWithoutSave(String assessmentId, String moduleCode, String assessmentName, 
                                AssessmentType type, double maxMarks, 
                                String description) {
        // Check if max marks is greater than 1
        if (maxMarks <= 1) {
            throw new IllegalArgumentException("Maximum marks must be greater than 1.");
        }
        
        // Check if module exists
        ModuleManager.loadAllModules();
        Module module = ModuleManager.findModuleByCode(moduleCode);
        if (module == null) {
            throw new IllegalArgumentException("Module with code '" + moduleCode + "' does not exist. Cannot design assessment.");
        }
        
        // Check if lecturer is assigned to this module
        if (!module.getLecturerEmails().contains(this.getEmail())) {
            throw new IllegalArgumentException("You are not assigned to module '" + moduleCode + "'. Cannot design assessment for this module.");
        }
        Assessment assessment = new Assessment(assessmentId, moduleCode, assessmentName, 
                                              type, maxMarks, description);
        assessments.put(assessmentId, assessment);
        studentMarks.put(assessmentId, new ArrayList<>());
    }
    
    /**
     * Update an existing assessment
     */
    public void updateAssessment(String assessmentId, String assessmentName, 
                                double maxMarks, String description) {
        Assessment assessment = assessments.get(assessmentId);
        if (assessment != null) {
            assessment.setAssessmentName(assessmentName);
            assessment.setMaxMarks(maxMarks);
            assessment.setDescription(description);
            AssessmentManager.saveLecturerData(this); // Auto-save after updating assessment
        }
        pause();
    }
    
    /**
     * Get all assessments for a specific module
     */
    public List<Assessment> getAssessmentsByModule(String moduleCode) {
        List<Assessment> moduleAssessments = new ArrayList<>();
        for (Assessment assessment : assessments.values()) {
            if (assessment.getModuleCode().equals(moduleCode)) {
                moduleAssessments.add(assessment);
            }
        }
        return moduleAssessments;
    }
    
    /**
     * Get a specific assessment by ID
     */
    public Assessment getAssessment(String assessmentId) {
        return assessments.get(assessmentId);
    }
    
    /**
     * Get all assessments
     */
    public List<Assessment> getAllAssessments() {
        return new ArrayList<>(assessments.values());
    }
    
    /**
     * Delete an assessment
     */
    public boolean deleteAssessment(String assessmentId) {
        if (assessments.containsKey(assessmentId)) {
            assessments.remove(assessmentId);
            studentMarks.remove(assessmentId);
            AssessmentManager.saveLecturerData(this); // Auto-save after deleting assessment
            pause();
            return true;
        }
        pause();
        return false;
    }
    
    // ==================== Key-in Assessment Marks ====================
    
    /**
     * Enter marks for a student in a specific assessment
     */
    public void enterMarks(String assessmentId, String studentId, String studentName, 
                          double marks) {
        Assessment assessment = assessments.get(assessmentId);
        if (assessment == null) {
            throw new IllegalArgumentException("Assessment not found: " + assessmentId);
        }
        
        // Check if module exists and lecturer is assigned to the module
        String moduleCode = assessment.getModuleCode();
        ModuleManager.loadAllModules();
        Module module = ModuleManager.findModuleByCode(moduleCode);
        if (module == null) {
            throw new IllegalArgumentException("Module with code '" + moduleCode + "' does not exist. Cannot enter marks.");
        }
        
        if (!module.getLecturerEmails().contains(this.getEmail())) {
            throw new IllegalArgumentException("You are not assigned to module '" + moduleCode + "'. Cannot enter marks for assessments in this module.");
        }
        
        if (marks < 0 || marks > assessment.getMaxMarks()) {
            throw new IllegalArgumentException("Marks must be between 0 and " + assessment.getMaxMarks());
        }
        
        List<StudentMark> marksList = studentMarks.get(assessmentId);
        if (marksList == null) {
            marksList = new ArrayList<>();
            studentMarks.put(assessmentId, marksList);
        }
        
        // Check if student already has marks, update if exists
        boolean found = false;
        for (StudentMark studentMark : marksList) {
            if (studentMark.getStudentId().equals(studentId)) {
                studentMark.setMarks(marks);
                found = true;
                break;
            }
        }
        
        // If not found, add new entry
        if (!found) {
            marksList.add(new StudentMark(studentId, studentName, assessmentId, marks));
        }
        AssessmentManager.saveLecturerData(this, false, true); // Auto-save after entering marks (suppress assessment message, show marks)
        pause();
    }
    
    /**
     * Enter marks without auto-save (used by AssessmentManager)
     */
    public void enterMarksWithoutSave(String assessmentId, String studentId, String studentName, 
                          double marks) {
        Assessment assessment = assessments.get(assessmentId);
        if (assessment == null) {
            throw new IllegalArgumentException("Assessment not found: " + assessmentId);
        }
        
        // Check if module exists and lecturer is assigned to the module
        String moduleCode = assessment.getModuleCode();
        ModuleManager.loadAllModules();
        Module module = ModuleManager.findModuleByCode(moduleCode);
        if (module == null) {
            throw new IllegalArgumentException("Module with code '" + moduleCode + "' does not exist. Cannot enter marks.");
        }
        
        if (!module.getLecturerEmails().contains(this.getEmail())) {
            throw new IllegalArgumentException("You are not assigned to module '" + moduleCode + "'. Cannot enter marks for assessments in this module.");
        }
        
        if (marks < 0 || marks > assessment.getMaxMarks()) {
            throw new IllegalArgumentException("Marks must be between 0 and " + assessment.getMaxMarks());
        }
        
        List<StudentMark> marksList = studentMarks.get(assessmentId);
        if (marksList == null) {
            marksList = new ArrayList<>();
            studentMarks.put(assessmentId, marksList);
        }
        
        // Check if student already has marks, update if exists
        boolean found = false;
        for (StudentMark studentMark : marksList) {
            if (studentMark.getStudentId().equals(studentId)) {
                studentMark.setMarks(marks);
                found = true;
                break;
            }
        }
        
        // If not found, add new entry
        if (!found) {
            marksList.add(new StudentMark(studentId, studentName, assessmentId, marks));
        }
    }
    
    /**
     * Enter marks for multiple students at once
     */
    public void enterMarksBatch(String assessmentId, Map<String, Double> studentMarksMap) {
        for (Map.Entry<String, Double> entry : studentMarksMap.entrySet()) {
            String studentId = entry.getKey();
            double marks = entry.getValue();
            enterMarks(assessmentId, studentId, "Student " + studentId, marks);
        }
        pause();
    }
    
    /**
     * Get marks for a specific assessment
     */
    public List<StudentMark> getMarksForAssessment(String assessmentId) {
        return new ArrayList<>(studentMarks.getOrDefault(assessmentId, new ArrayList<>()));
    }
    
    /**
     * Get marks for a specific student across all assessments
     */
    public List<StudentMark> getMarksForStudent(String studentId) {
        List<StudentMark> studentMarksList = new ArrayList<>();
        for (List<StudentMark> marksList : studentMarks.values()) {
            for (StudentMark mark : marksList) {
                if (mark.getStudentId().equals(studentId)) {
                    studentMarksList.add(mark);
                }
            }
        }
        return studentMarksList;
    }
    
    // ==================== Provide Feedback ====================
    
    /**
     * Provide feedback for a student's assessment
     * NOTE: Feedback can only be provided AFTER marks have been entered
     */
    public void provideFeedback(String assessmentId, String studentId, String feedback) {
        // Check if assessment exists first
        Assessment assessment = assessments.get(assessmentId);
        if (assessment == null) {
            throw new IllegalArgumentException("Assessment with ID '" + assessmentId + "' does not exist. Cannot provide feedback.");
        }
        
        // Check if student exists in the system (without reloading users to avoid recursive load during startup)
        Users.User user = UserManager.findByEmail(studentId);
        if (user == null || !(user instanceof Users.Student)) {
            throw new IllegalArgumentException("Student with email '" + studentId + "' does not exist. Cannot provide feedback.");
        }
        
        // Get student name for verification
        Users.Student student = (Users.Student) user;
        String studentName = student.getName();
        
        List<StudentMark> marksList = studentMarks.get(assessmentId);
        if (marksList == null || marksList.isEmpty()) {
            throw new IllegalArgumentException("No marks have been entered for assessment '" + assessmentId + "'. Please enter marks before providing feedback.");
        }
        
        // Find the student's mark entry
        StudentMark studentMark = null;
        for (StudentMark mark : marksList) {
            if (mark.getStudentId().equals(studentId)) {
                // Verify the name matches if it's stored in StudentMark
                if (mark.getStudentName() != null && !mark.getStudentName().isEmpty()) {
                    if (!mark.getStudentName().equals(studentName)) {
                        throw new IllegalArgumentException("Student name mismatch. Expected: '" + studentName + "', Found: '" + mark.getStudentName() + "'. Cannot provide feedback.");
                    }
                }
                studentMark = mark;
                break;
            }
        }
        
        // If student mark not found, they haven't been graded yet
        if (studentMark == null) {
            throw new IllegalArgumentException("Student '" + studentId + "' does not have marks for assessment '" + assessmentId + "'. Please enter marks before providing feedback.");
        }
        
        // Now we can safely provide feedback
        studentMark.setFeedback(feedback);
        AssessmentManager.saveLecturerData(this, false, false); // Auto-save after providing feedback (suppress assessment message, suppress marks message)
        pause();
    }
    
    /**
     * Provide feedback without auto-save (used by AssessmentManager)
     * NOTE: Feedback can only be provided AFTER marks have been entered
     */
    public void provideFeedbackWithoutSave(String assessmentId, String studentId, String feedback) {
        // Check if assessment exists first
        Assessment assessment = assessments.get(assessmentId);
        if (assessment == null) {
            throw new IllegalArgumentException("Assessment with ID '" + assessmentId + "' does not exist. Cannot provide feedback.");
        }
        
        // Check if student exists in the system (use already-loaded users to avoid recursive loads)
        Users.User user = UserManager.findByEmail(studentId);
        if (user == null || !(user instanceof Users.Student)) {
            throw new IllegalArgumentException("Student with email '" + studentId + "' does not exist. Cannot provide feedback.");
        }
        
        // Get student name for verification
        Users.Student student = (Users.Student) user;
        String studentName = student.getName();
        
        List<StudentMark> marksList = studentMarks.get(assessmentId);
        if (marksList == null || marksList.isEmpty()) {
            throw new IllegalArgumentException("No marks have been entered for assessment '" + assessmentId + "'. Please enter marks before providing feedback.");
        }
        
        // Find the student's mark entry
        StudentMark studentMark = null;
        for (StudentMark mark : marksList) {
            if (mark.getStudentId().equals(studentId)) {
                // Verify the name matches if it's stored in StudentMark
                if (mark.getStudentName() != null && !mark.getStudentName().isEmpty()) {
                    if (!mark.getStudentName().equals(studentName)) {
                        throw new IllegalArgumentException("Student name mismatch. Expected: '" + studentName + "', Found: '" + mark.getStudentName() + "'. Cannot provide feedback.");
                    }
                }
                studentMark = mark;
                break;
            }
        }
        
        // If student mark not found, they haven't been graded yet
        if (studentMark == null) {
            throw new IllegalArgumentException("Student '" + studentId + "' does not have marks for assessment '" + assessmentId + "'. Please enter marks before providing feedback.");
        }
        
        // Now we can safely provide feedback
        studentMark.setFeedback(feedback);
    }
    
    /**
     * Get feedback for a student's assessment
     */
    public String getFeedback(String assessmentId, String studentId) {
        List<StudentMark> marksList = studentMarks.get(assessmentId);
        if (marksList != null) {
            for (StudentMark studentMark : marksList) {
                if (studentMark.getStudentId().equals(studentId)) {
                    return studentMark.getFeedback();
                }
            }
        }
        return "No feedback available";
    }
    
    /**
     * Provide feedback for all students in an assessment
     */
    public void provideFeedbackBatch(String assessmentId, Map<String, String> feedbackMap) {
        for (Map.Entry<String, String> entry : feedbackMap.entrySet()) {
            provideFeedback(assessmentId, entry.getKey(), entry.getValue());
        }
        pause();
    }
    
    // ==================== Existing Methods ====================
    
    /**
     * Get assigned leader information as formatted string
     * Returns academic leader email, module name and code for modules where this lecturer is assigned
     */
    public String getAssignedLeaderInfo() {
        StringBuilder info = new StringBuilder();
        
        // Load all modules from file
        ModuleManager.loadAllModules();
        
        // Find modules where this lecturer is assigned
        List<Module> assignedModules = new ArrayList<>();
        for (Module module : ModuleManager.modules) {
            if (module.getLecturerEmails().contains(this.getEmail())) {
                assignedModules.add(module);
            }
        }
        
        if (assignedModules.isEmpty()) {
            info.append("No modules assigned.");
            return info.toString();
        }
        
        // Format the data as a table
        info.append("─────────────────────────────────────────────────────────────\n");
        info.append(String.format("%-30s %-15s %-30s%n", "Academic Leader Email", "Module Code", "Module Name"));
        info.append("─────────────────────────────────────────────────────────────\n");
        
        for (Module module : assignedModules) {
            String leaderEmail = module.getAcademicLeaderEmail();
            String moduleCode = module.getCode();
            String moduleName = module.getName();
            
            info.append(String.format("%-30s %-15s %-30s%n", 
                leaderEmail, 
                moduleCode, 
                moduleName));
        }
        
        info.append("─────────────────────────────────────────────────────────────");
        
        return info.toString();
    }
    
    /**
     * View assigned leader information
     * Displays academic leader email, module name and code for modules where this lecturer is assigned
     */
    public void viewLeader() {
        System.out.println("\n=== Assigned Academic Leaders ===");
        System.out.println(getAssignedLeaderInfo());
        pause();
    }
    
    /**
     * View schedule based on registered classes only
     */
    public void viewSchedule() {
        System.out.println("\n=== My Schedule ===");
        
        // Load all class groups from classgroup.txt
        ClassManager.loadFromFile();
        
        // Find class groups where this lecturer is registered
        List<ClassGroup> myClassGroups = new ArrayList<>();
        for (ClassGroup cg : ClassManager.classGroups) {
            if (cg.getLecturerEmails().contains(this.getEmail())) {
                myClassGroups.add(cg);
            }
        }
        
        if (myClassGroups.isEmpty()) {
            System.out.println("No classes registered. Please register to classes first.");
            pause();
            return;
        }
        
        // Display schedule
        System.out.println("\nSchedule Details:");
        System.out.println("─────────────────────────────────────────────────────────────");
        String[] columns = {"Class Code", "Module", "Time", "Classroom"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (ClassGroup cg : myClassGroups) {
            String classCode = cg.getClassCode();
            String moduleCode = cg.getModule().getCode();
            String moduleName = cg.getModule().getName();
            String time = cg.getTime() != null && !cg.getTime().isEmpty() ? cg.getTime() : "Not set";
            String classroom = cg.getClassroom() != null && !cg.getClassroom().isEmpty() ? cg.getClassroom() : "Not set";
            
            model.addRow(new Object[] {classCode, moduleCode + " (" + moduleName + ")", time, classroom});
        }
        
        // Print the table
        System.out.printf("%-15s %-15s %-25s %-15s%n", columns);
        System.out.println("─────────────────────────────────────────────────────────────");
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                System.out.printf("%-15s ", model.getValueAt(i, j));
            }
            System.out.println();
        }
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("\nTotal classes: " + myClassGroups.size());
        pause();
    }
    
    /**
     * View and make quiz/test assessments
     * This method allows lecturers to view existing assessments and create new ones
     */
    public void designAssessments() {
        // View all existing assessments
        System.out.println("\n=== Existing Assessments ===");
        if (assessments.isEmpty()) {
            System.out.println("No assessments created yet.");
        } else {
            for (Assessment assessment : assessments.values()) {
                System.out.println(assessment);
            }
        }
        
        // Note: To create a new assessment, use the designAssessment() method
        // Example usage:
        // designAssessment("ASS001", "CS101", "Quiz 1", AssessmentType.QUIZ, 100.0, "First quiz");
        pause();
    }
    
    /**
     * Based on assessment, generate marks and provide feedback
     * This method allows lecturers to enter marks for students and provide feedback
     */
    public void enterMarks() {
        // Display all assessments for which marks can be entered
        System.out.println("\n=== Available Assessments ===");
        if (assessments.isEmpty()) {
            System.out.println("No assessments available. Please create assessments first using designAssessments().");
            return;
        }
        
        // Show assessments with their current marks status
        for (Map.Entry<String, Assessment> entry : assessments.entrySet()) {
            String assessmentId = entry.getKey();
            Assessment assessment = entry.getValue();
            List<StudentMark> marks = studentMarks.getOrDefault(assessmentId, new ArrayList<>());
            
            System.out.println("\nAssessment: " + assessment.getAssessmentName() + " (ID: " + assessmentId + ")");
            System.out.println("Type: " + assessment.getType().getDisplayName());
            System.out.println("Max Marks: " + assessment.getMaxMarks());
            System.out.println("Students with marks: " + marks.size());
            
            if (!marks.isEmpty()) {
                System.out.println("Student Marks:");
                for (StudentMark mark : marks) {
                    System.out.println("  - " + mark.getStudentName() + " (" + mark.getStudentId() + "): " 
                                     + mark.getMarks() + "/" + assessment.getMaxMarks());
                    if (!mark.getFeedback().isEmpty()) {
                        System.out.println("    Feedback: " + mark.getFeedback());
                    }
                }
            }
        }
        
        // Note: To enter marks, use the enterMarks() method with parameters:
        // enterMarks("ASS001", "STU001", "John Doe", 85.0);
        // To provide feedback, use:
        // provideFeedback("ASS001", "STU001", "Good work! Keep it up.");
        pause();
    }
    
    // ==================== Menu Helper Methods ====================
    
    /**
     * Menu for editing personal profile
     */
    public void editProfileMenu() {
        System.out.println("\n--- Edit Personal Profile ---");
        System.out.println("(Press Enter to skip a field)");
        
        System.out.print("Enter new name: ");
        String name = scan.nextLine();
        
        System.out.print("Enter new email: ");
        String email = scan.nextLine();
        
        editProfile(name, email, null, null, null);
        System.out.println("\nProfile updated successfully!");
        pause();
    }
    
    /**
     * Menu for viewing profile
     */
    public void viewProfileMenu() {
        System.out.println("\n--- Personal Profile ---");
        System.out.println(getProfileInfo());
        pause();
    }
    
    /**
     * Menu for designing assessments
     */
    public void designAssessmentMenu() {
        System.out.println("\n--- Design Module Assessment Types ---");
        
        System.out.print("Enter Assessment ID: ");
        String assessmentId = scan.nextLine();
        
        System.out.print("Enter Module Code: ");
        String moduleCode = scan.nextLine();
        
        System.out.print("Enter Assessment Name: ");
        String assessmentName = scan.nextLine();
        
        // Display assessment types
        System.out.println("\nAvailable Assessment Types:");
        AssessmentType[] types = AssessmentType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].getDisplayName());
        }
        
        System.out.print("Select Assessment Type (1-" + types.length + "): ");
        int typeChoice = scan.nextInt();
        scan.nextLine();
        
        if (typeChoice < 1 || typeChoice > types.length) {
            System.out.println("Invalid choice! Using QUIZ as default.");
            typeChoice = 1;
        }
        AssessmentType selectedType = types[typeChoice - 1];
        
        // Removed max marks input - always use default 100
        double maxMarks = 100.0;
        
        System.out.print("Enter Description: ");
        String description = scan.nextLine();
        
        try {
            designAssessment(assessmentId, moduleCode, assessmentName, 
                            selectedType, maxMarks, description);
            System.out.println("\nAssessment created successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n" + e.getMessage());
            System.out.println("Returning to Assessment Menu...");
        }
        pause();
    }
    
    /**
     * Menu for viewing assessments
     */
    public void viewAssessmentsMenu() {
        System.out.println("\n--- View Assessments ---");
        designAssessments();
        
        System.out.println("\nOptions:");
        System.out.println("1. View assessments by module");
        System.out.println("2. View specific assessment");
        System.out.println("3. Update assessment");
        System.out.println("4. Delete assessment");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
        
        int choice = scan.nextInt();
        scan.nextLine();
        
        switch (choice) {
            case 1:
                System.out.print("Enter Module Code: ");
                String moduleCode = scan.nextLine();
                List<Assessment> assessments = getAssessmentsByModule(moduleCode);
                if (assessments.isEmpty()) {
                    System.out.println("No assessments found for module: " + moduleCode);
                } else {
                    System.out.println("\nAssessments for " + moduleCode + ":");
                    for (Assessment assessment : assessments) {
                        System.out.println(assessment);
                    }
                }
                break;
            case 2:
                System.out.print("Enter Assessment ID: ");
                String assessmentId = scan.nextLine();
                Assessment assessment = getAssessment(assessmentId);
                if (assessment != null) {
                    System.out.println(assessment);
                } else {
                    System.out.println("Assessment not found!");
                }
                break;
            case 3:
                updateAssessmentMenu();
                break;
            case 4:
                deleteAssessmentMenu();
                break;
        }
        pause();
    }
    
    /**
     * Menu for updating assessment
     */
    private void updateAssessmentMenu() {
        System.out.println("\n--- Update Assessment ---");
        System.out.print("Enter Assessment ID to update: ");
        String assessmentId = scan.nextLine();
        
        Assessment assessment = getAssessment(assessmentId);
        if (assessment == null) {
            System.out.println("Assessment not found!");
            return;
        }
        
        System.out.println("Current Assessment: " + assessment);
        System.out.println("(Press Enter to keep current value)");
        
        System.out.print("Enter new Assessment Name: ");
        String name = scan.nextLine();
        if (name.isEmpty()) {
            name = assessment.getAssessmentName();
        }
        
        System.out.print("Enter new Maximum Marks (default: 100, press Enter to use default): ");
        String maxMarksStr = scan.nextLine();
        double maxMarks = maxMarksStr.isEmpty() ? 100.0 : Double.parseDouble(maxMarksStr);
        
        System.out.print("Enter new Description: ");
        String description = scan.nextLine();
        if (description.isEmpty()) {
            description = assessment.getDescription();
        }
        
        updateAssessment(assessmentId, name, maxMarks, description);
        System.out.println("Assessment updated successfully!");
        pause();
    }
    
    /**
     * Menu for deleting assessment
     */
    private void deleteAssessmentMenu() {
        System.out.println("\n--- Delete Assessment ---");
        System.out.print("Enter Assessment ID to delete: ");
        String assessmentId = scan.nextLine();
        
        if (deleteAssessment(assessmentId)) {
            System.out.println("Assessment deleted successfully!");
        } else {
            System.out.println("Assessment not found!");
        }
        pause();
    }
    
    /**
     * Menu for entering marks
     */
    public void enterMarksMenu() {
        System.out.println("\n--- Key-in Assessment Marks ---");
        enterMarks();
        
        System.out.println("\nOptions:");
        System.out.println("1. Enter marks for a student");
        System.out.println("2. Enter marks for multiple students");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
        
        int choice;
        try {
            choice = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Returning to Marks Menu...");
            return;
        }
        
        switch (choice) {
            case 1:
                System.out.print("Enter Assessment ID: ");
                String assessmentId = scan.nextLine();
                
                System.out.print("Enter Student Email: ");
                String studentEmail = scan.nextLine();
                
                System.out.print("Enter Student Name: ");
                String studentName = scan.nextLine();
                
                System.out.print("Enter Marks: ");
                String marksInput = scan.nextLine();
                double marks;
                try {
                    marks = Double.parseDouble(marksInput.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid marks value. Returning to Marks Menu...");
                    return;
                }
                
                // Check if student already has marks for this assessment
                List<StudentMark> existingMarks = getMarksForAssessment(assessmentId);
                Double existingMark = null;
                for (StudentMark sm : existingMarks) {
                    if (sm.getStudentId().equalsIgnoreCase(studentEmail)) {
                        existingMark = sm.getMarks();
                        break;
                    }
                }
                
                if (existingMark != null) {
                    System.out.printf("\n⚠ WARNING: Student '%s' already has marks for this assessment!%n", studentName);
                    System.out.printf("Current Mark: %.2f%n", existingMark);
                    System.out.printf("New Mark: %.2f%n", marks);
                    System.out.print("Do you want to replace the existing mark? (Y/N): ");
                    String confirm = scan.nextLine().trim();
                    if (!confirm.equalsIgnoreCase("Y")) {
                        System.out.println("Mark update cancelled.");
                        return;
                    }
                }
                
                try {
                    enterMarks(assessmentId, studentEmail, studentName, marks);
                    if (existingMark != null) {
                        System.out.printf("Mark updated successfully! (%.2f → %.2f)%n", existingMark, marks);
                    } else {
                        System.out.println("Marks entered successfully!");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("\n" + e.getMessage());
                    System.out.println("Returning to Marks Menu...");
                }
                break;
            case 2:
                enterMarksBatchMenu();
                break;
        }
        pause();
    }
    
    /**
     * Menu for entering marks in batch
     */
    private void enterMarksBatchMenu() {
        System.out.println("\n--- Enter Marks for Multiple Students ---");
        System.out.print("Enter Assessment ID: ");
        String assessmentId = scan.nextLine();
        
        Map<String, Double> marksMap = new HashMap<>();
        System.out.println("Enter student marks (Enter 'done' when finished):");
        
        while (true) {
            System.out.print("Enter Student Email (or 'done' to finish): ");
            String studentEmail = scan.nextLine();
            
            if (studentEmail.equalsIgnoreCase("done")) {
                break;
            }
            
            System.out.print("Enter Marks for " + studentEmail + ": ");
            String marksInput = scan.nextLine();
            double marks;
            try {
                marks = Double.parseDouble(marksInput.trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid marks value. Returning to Marks Menu...");
                return;
            }
            
            marksMap.put(studentEmail, marks);
        }
        
        if (!marksMap.isEmpty()) {
            try {
                enterMarksBatch(assessmentId, marksMap);
                System.out.println("Marks entered successfully for " + marksMap.size() + " students!");
            } catch (IllegalArgumentException e) {
                System.out.println("\n" + e.getMessage());
                System.out.println("Returning to Marks Menu...");
            }
        }
        pause();
    }
    
    /**
     * Menu for viewing marks
     */
    public void viewMarksMenu() {
        System.out.println("\n--- View Student Marks ---");
        System.out.println("1. View marks for an assessment");
        System.out.println("2. View marks for a student");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
        
        int choice = scan.nextInt();
        scan.nextLine();
        
        switch (choice) {
            case 1:
                System.out.print("Enter Assessment ID: ");
                String assessmentId = scan.nextLine();
                List<StudentMark> marks = getMarksForAssessment(assessmentId);
                
                if (marks.isEmpty()) {
                    System.out.println("No marks found for this assessment.");
                } else {
                    System.out.println("\nStudent Marks:");
                    for (StudentMark mark : marks) {
                        System.out.println(mark);
                    }
                }
                break;
            case 2:
                System.out.print("Enter Student Email: ");
                String studentEmail = scan.nextLine();
                List<StudentMark> studentMarks = getMarksForStudent(studentEmail);
                
                if (studentMarks.isEmpty()) {
                    System.out.println("No marks found for this student.");
                } else {
                    System.out.println("\nMarks for Student " + studentEmail + ":");
                    for (StudentMark mark : studentMarks) {
                        System.out.println(mark);
                    }
                }
                break;
        }
        pause();
    }
    
    /**
     * Menu for providing feedback
     */
    public void provideFeedbackMenu() {
        System.out.println("\n--- Provide Feedback ---");
        System.out.println("NOTE: You can only provide feedback AFTER entering marks for the student.");
        System.out.println("1. Provide feedback for a student");
        System.out.println("2. Provide feedback for multiple students");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
        
        int choice = scan.nextInt();
        scan.nextLine();
        
        switch (choice) {
            case 1:
                System.out.print("Enter Assessment ID: ");
                String assessmentId = scan.nextLine();
                
                // Show students who have marks for this assessment
                List<StudentMark> marks = getMarksForAssessment(assessmentId);
                if (marks == null || marks.isEmpty()) {
                    System.out.println("\nNo marks have been entered for this assessment yet.");
                    System.out.println("Please enter marks first before providing feedback.");
                    pause();
                    break;
                }
                
                System.out.println("\nStudents with marks (eligible for feedback):");
                System.out.println("─────────────────────────────────────────────────────────────");
                for (int i = 0; i < marks.size(); i++) {
                    StudentMark mark = marks.get(i);
                    String currentFeedback = mark.getFeedback().isEmpty() ? "No feedback yet" : "Has feedback";
                    System.out.printf("%d. %s (%s) - Marks: %.2f - %s%n", 
                        (i + 1), 
                        mark.getStudentName(), 
                        mark.getStudentId(), 
                        mark.getMarks(),
                        currentFeedback);
                }
                System.out.println("─────────────────────────────────────────────────────────────");
                
                System.out.print("\nEnter Student Email: ");
                String studentEmail = scan.nextLine();
                
                // Verify student has marks
                boolean hasMarks = false;
                for (StudentMark mark : marks) {
                    if (mark.getStudentId().equalsIgnoreCase(studentEmail)) {
                        hasMarks = true;
                        break;
                    }
                }
                
                if (!hasMarks) {
                    System.out.println("\nThis student does not have marks for the selected assessment.");
                    System.out.println("Please enter marks first before providing feedback.");
                    pause();
                    break;
                }
                
                System.out.print("Enter Feedback: ");
                String feedback = scan.nextLine();
                
                try {
                    provideFeedback(assessmentId, studentEmail, feedback);
                    System.out.println("\n✓ Feedback provided successfully!");
                } catch (IllegalArgumentException e) {
                    System.out.println("\n✗ " + e.getMessage());
                    System.out.println("Returning to Feedback Menu...");
                }
                break;
            case 2:
                provideFeedbackBatchMenu();
                break;
        }
        pause();
    }
    
    /**
     * Menu for providing feedback in batch
     */
    private void provideFeedbackBatchMenu() {
        System.out.println("\n--- Provide Feedback for Multiple Students ---");
        System.out.print("Enter Assessment ID: ");
        String assessmentId = scan.nextLine();
        
        // Show students who have marks for this assessment
        List<StudentMark> marks = getMarksForAssessment(assessmentId);
        if (marks == null || marks.isEmpty()) {
            System.out.println("\nNo marks have been entered for this assessment yet.");
            System.out.println("Please enter marks first before providing feedback.");
            pause();
            return;
        }
        
        System.out.println("\nStudents with marks (eligible for feedback):");
        System.out.println("─────────────────────────────────────────────────────────────");
        for (int i = 0; i < marks.size(); i++) {
            StudentMark mark = marks.get(i);
            String currentFeedback = mark.getFeedback().isEmpty() ? "No feedback yet" : "Has feedback";
            System.out.printf("%d. %s (%s) - Marks: %.2f - %s%n", 
                (i + 1), 
                mark.getStudentName(), 
                mark.getStudentId(), 
                mark.getMarks(),
                currentFeedback);
        }
        System.out.println("─────────────────────────────────────────────────────────────");
        
        java.util.Map<String, String> feedbackMap = new java.util.HashMap<>();
        System.out.println("\nEnter feedback for students (Enter 'done' when finished):");
        System.out.println("Only students listed above can receive feedback.");
        
        while (true) {
            System.out.print("\nEnter Student Email (or 'done' to finish): ");
            String studentEmail = scan.nextLine();
            
            if (studentEmail.equalsIgnoreCase("done")) {
                break;
            }
            
            // Verify student has marks
            boolean hasMarks = false;
            for (StudentMark mark : marks) {
                if (mark.getStudentId().equalsIgnoreCase(studentEmail)) {
                    hasMarks = true;
                    break;
                }
            }
            
            if (!hasMarks) {
                System.out.println("✗ This student does not have marks for the selected assessment.");
                System.out.println("  They are not eligible for feedback. Please try another student.");
                continue;
            }
            
            System.out.print("Enter Feedback for " + studentEmail + ": ");
            String feedback = scan.nextLine();
            
            feedbackMap.put(studentEmail, feedback);
            System.out.println("✓ Feedback recorded for " + studentEmail);
        }
        
        if (!feedbackMap.isEmpty()) {
            int successCount = 0;
            int failCount = 0;
            
            for (java.util.Map.Entry<String, String> entry : feedbackMap.entrySet()) {
                try {
                    provideFeedback(assessmentId, entry.getKey(), entry.getValue());
                    successCount++;
                } catch (IllegalArgumentException e) {
                    System.out.println("✗ Failed for " + entry.getKey() + ": " + e.getMessage());
                    failCount++;
                }
            }
            
            System.out.println("\n─────────────────────────────────────────────────────────────");
            System.out.println("Feedback batch completed!");
            System.out.println("✓ Successful: " + successCount);
            if (failCount > 0) {
                System.out.println("✗ Failed: " + failCount);
            }
            System.out.println("─────────────────────────────────────────────────────────────");
        } else {
            System.out.println("\nNo feedback provided.");
        }
        pause();
    }
    
    /**
     * Menu for viewing feedback - displays all feedback in table format
     */
    public void viewFeedbackMenu() {
        System.out.println("\n--- View Feedback ---");
        
        // Reload assessment data to ensure latest feedback
        AssessmentManager.loadLecturerData(this);
        
        // Collect all feedback from all assessments
        ArrayList<FeedbackRecord> allFeedback = new ArrayList<>();
        
        for (Assessment assessment : assessments.values()) {
            String assessmentId = assessment.getAssessmentId();
            List<StudentMark> marks = getMarksForAssessment(assessmentId);
            
            for (StudentMark mark : marks) {
                if (mark.getFeedback() != null && !mark.getFeedback().isEmpty()) {
                    allFeedback.add(new FeedbackRecord(
                        assessmentId,
                        assessment.getAssessmentName(),
                        assessment.getModuleCode(),
                        mark.getStudentId(),
                        mark.getStudentName(),
                        mark.getMarks(),
                        assessment.getMaxMarks(),
                        mark.getFeedback()
                    ));
                }
            }
        }
        
        if (allFeedback.isEmpty()) {
            System.out.println("\nNo feedback has been provided yet.");
            pause();
            return;
        }
        
        // Sort by module, then assessment, then student
        allFeedback.sort((f1, f2) -> {
            int moduleCompare = f1.moduleCode.compareTo(f2.moduleCode);
            if (moduleCompare != 0) return moduleCompare;
            int assessmentCompare = f1.assessmentId.compareTo(f2.assessmentId);
            if (assessmentCompare != 0) return assessmentCompare;
            return f1.studentName.compareTo(f2.studentName);
        });
        
        // Display feedback in table format
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("%-10s %-15s %-10s %-20s %-15s %-50s%n", 
                "Module", "Assessment", "Student ID", "Student Name", "Marks", "Feedback");
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
        
        for (FeedbackRecord record : allFeedback) {
            String marksDisplay = String.format("%.1f/%.0f", record.marks, record.maxMarks);
            String feedbackPreview = record.feedback.length() > 50 ? 
                record.feedback.substring(0, 47) + "..." : record.feedback;
            
            System.out.printf("%-10s %-15s %-10s %-20s %-15s %-50s%n",
                truncateString(record.moduleCode, 10),
                truncateString(record.assessmentName, 15),
                truncateString(record.studentId, 10),
                truncateString(record.studentName, 20),
                marksDisplay,
                feedbackPreview);
        }
        
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("\nTotal feedback entries: %d%n", allFeedback.size());
        
        // Option to view detailed feedback
        System.out.println("\n1. View detailed feedback for a student");
        System.out.println("2. Filter by assessment");
        System.out.println("3. Filter by module");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");
        
        int choice = scan.nextInt();
        scan.nextLine();
        
        switch (choice) {
            case 1:
                viewDetailedFeedback(allFeedback);
                break;
            case 2:
                filterFeedbackByAssessment();
                break;
            case 3:
                filterFeedbackByModule();
                break;
        }
        
        pause();
    }
    
    /**
     * View detailed feedback for a specific student
     */
    private void viewDetailedFeedback(ArrayList<FeedbackRecord> allFeedback) {
        System.out.print("\nEnter Student Email: ");
        String studentEmail = scan.nextLine().trim();
        
        boolean found = false;
        for (FeedbackRecord record : allFeedback) {
            if (record.studentId.equalsIgnoreCase(studentEmail)) {
                if (!found) {
                    System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
                    System.out.println("║           DETAILED FEEDBACK: " + record.studentName);
                    System.out.println("╚════════════════════════════════════════════════════════════════╝");
                    found = true;
                }
                
                System.out.println("\n─────────────────────────────────────────────────────────────────");
                System.out.println("Module           : " + record.moduleCode);
                System.out.println("Assessment       : " + record.assessmentName + " (" + record.assessmentId + ")");
                System.out.printf("Marks            : %.2f / %.2f%n", record.marks, record.maxMarks);
                System.out.println("Feedback         :");
                System.out.println(wrapFeedbackText(record.feedback, 60));
                System.out.println("─────────────────────────────────────────────────────────────────");
            }
        }
        
        if (!found) {
            System.out.println("\nNo feedback found for student: " + studentEmail);
        }
    }
    
    /**
     * Filter and display feedback by assessment
     */
    private void filterFeedbackByAssessment() {
        System.out.print("\nEnter Assessment ID: ");
        String assessmentId = scan.nextLine().trim();
        
        Assessment assessment = getAssessment(assessmentId);
        if (assessment == null) {
            System.out.println("Assessment not found!");
            return;
        }
        
        List<StudentMark> marks = getMarksForAssessment(assessmentId);
        if (marks == null || marks.isEmpty()) {
            System.out.println("No marks found for this assessment.");
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("Assessment: " + assessment.getAssessmentName() + " (" + assessmentId + ")");
        System.out.println("Module: " + assessment.getModuleCode());
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("%-10s %-25s %-15s %-50s%n", "Student ID", "Student Name", "Marks", "Feedback");
        System.out.println("───────────────────────────────────────────────────────────────────────────────────────────");
        
        int feedbackCount = 0;
        for (StudentMark mark : marks) {
            String feedback = mark.getFeedback();
            if (feedback != null && !feedback.isEmpty()) {
                String marksDisplay = String.format("%.1f/%.0f", mark.getMarks(), assessment.getMaxMarks());
                String feedbackPreview = feedback.length() > 50 ? feedback.substring(0, 47) + "..." : feedback;
                
                System.out.printf("%-10s %-25s %-15s %-50s%n",
                    truncateString(mark.getStudentId(), 10),
                    truncateString(mark.getStudentName(), 25),
                    marksDisplay,
                    feedbackPreview);
                feedbackCount++;
            }
        }
        
        if (feedbackCount == 0) {
            System.out.println("No feedback has been provided for this assessment yet.");
        }
        
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("Feedback entries: %d%n", feedbackCount);
    }
    
    /**
     * Filter and display feedback by module
     */
    private void filterFeedbackByModule() {
        System.out.print("\nEnter Module Code: ");
        String moduleCode = scan.nextLine().trim().toUpperCase();
        
        List<Assessment> moduleAssessments = getAssessmentsByModule(moduleCode);
        if (moduleAssessments.isEmpty()) {
            System.out.println("No assessments found for module: " + moduleCode);
            return;
        }
        
        System.out.println("\n═══════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("Module: " + moduleCode);
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("%-15s %-10s %-20s %-15s %-40s%n", 
                "Assessment", "Student ID", "Student Name", "Marks", "Feedback");
        System.out.println("───────────────────────────────────────────────────────────────────────────────────────────");
        
        int feedbackCount = 0;
        for (Assessment assessment : moduleAssessments) {
            List<StudentMark> marks = getMarksForAssessment(assessment.getAssessmentId());
            
            for (StudentMark mark : marks) {
                String feedback = mark.getFeedback();
                if (feedback != null && !feedback.isEmpty()) {
                    String marksDisplay = String.format("%.1f/%.0f", mark.getMarks(), assessment.getMaxMarks());
                    String feedbackPreview = feedback.length() > 40 ? feedback.substring(0, 37) + "..." : feedback;
                    
                    System.out.printf("%-15s %-10s %-20s %-15s %-40s%n",
                        truncateString(assessment.getAssessmentName(), 15),
                        truncateString(mark.getStudentId(), 10),
                        truncateString(mark.getStudentName(), 20),
                        marksDisplay,
                        feedbackPreview);
                    feedbackCount++;
                }
            }
        }
        
        if (feedbackCount == 0) {
            System.out.println("No feedback has been provided for this module yet.");
        }
        
        System.out.println("═══════════════════════════════════════════════════════════════════════════════════════════");
        System.out.printf("Feedback entries: %d%n", feedbackCount);
    }
    
    /**
     * Helper method to truncate strings for table display
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() <= maxLength ? str : str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Helper method to wrap feedback text with proper indentation
     */
    private String wrapFeedbackText(String text, int width) {
        if (text == null || text.isEmpty()) return "No feedback";
        
        StringBuilder wrapped = new StringBuilder();
        String[] words = text.split(" ");
        int lineLength = 0;
        
        for (String word : words) {
            if (lineLength + word.length() + 1 > width) {
                wrapped.append("\n                 ");
                lineLength = 0;
            }
            if (lineLength > 0) {
                wrapped.append(" ");
                lineLength++;
            }
            wrapped.append(word);
            lineLength += word.length();
        }
        
        return "                 " + wrapped.toString();
    }
    
    /**
     * Inner class to hold feedback record information
     */
    private static class FeedbackRecord {
        String assessmentId;
        String assessmentName;
        String moduleCode;
        String studentId;
        String studentName;
        double marks;
        double maxMarks;
        String feedback;
        
        FeedbackRecord(String assessmentId, String assessmentName, String moduleCode,
                      String studentId, String studentName, double marks, 
                      double maxMarks, String feedback) {
            this.assessmentId = assessmentId;
            this.assessmentName = assessmentName;
            this.moduleCode = moduleCode;
            this.studentId = studentId;
            this.studentName = studentName;
            this.marks = marks;
            this.maxMarks = maxMarks;
            this.feedback = feedback;
        }
    }
    
    /**
     * Enum for Assessment Types
     */
    public enum AssessmentType {
        QUIZ("Quiz"),
        ASSIGNMENT("Assignment"),
        MIDTERM("Midterm Exam"),
        FINAL("Final Exam"),
        PROJECT("Project"),
        PRESENTATION("Presentation"),
        LAB("Lab Work"),
        PARTICIPATION("Participation");
        
        private final String displayName;
        
        AssessmentType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Assessment class representing an assessment type
     */
    public static class Assessment {
        private String assessmentId;
        private String moduleCode;
        private String assessmentName;
        private AssessmentType type;
        private double maxMarks;
        private String description;
        private final Date createdDate;
        
        public Assessment(String assessmentId, String moduleCode, String assessmentName,
                         AssessmentType type, double maxMarks, String description) {
            this.assessmentId = assessmentId;
            this.moduleCode = moduleCode;
            this.assessmentName = assessmentName;
            this.type = type;
            this.maxMarks = maxMarks;
            this.description = description;
            this.createdDate = new Date();
        }
        
        // Getters and Setters
        public String getAssessmentId() { return assessmentId; }
        public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }
        
        public String getModuleCode() { return moduleCode; }
        public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }
        
        public String getAssessmentName() { return assessmentName; }
        public void setAssessmentName(String assessmentName) { this.assessmentName = assessmentName; }
        
        public AssessmentType getType() { return type; }
        public void setType(AssessmentType type) { this.type = type; }
        
        public double getMaxMarks() { return maxMarks; }
        public void setMaxMarks(double maxMarks) { this.maxMarks = maxMarks; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Date getCreatedDate() { return createdDate; }
        
        @Override
        public String toString() {
            return String.format("Assessment[ID: %s, Name: %s, Type: %s, Max Marks: %.2f]",
                    assessmentId, assessmentName, type.getDisplayName(), maxMarks);
        }
    }
    
    /**
     * StudentMark class representing marks and feedback for a student
     */
    public static class StudentMark {
        private String studentId;
        private String studentName;
        private String assessmentId;
        private double marks;
        private String feedback;
        private final Date enteredDate;
        
        public StudentMark(String studentId, String studentName, String assessmentId, double marks) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.assessmentId = assessmentId;
            this.marks = marks;
            this.enteredDate = new Date();
            this.feedback = "";
        }
        
        // Getters and Setters
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public String getAssessmentId() { return assessmentId; }
        public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }
        
        public double getMarks() { return marks; }
        public void setMarks(double marks) { this.marks = marks; }
        
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
        
        public Date getEnteredDate() { return enteredDate; }
        
        @Override
        public String toString() {
            return String.format("StudentMark[Student: %s (%s), Assessment: %s, Marks: %.2f, Feedback: %s]",
                    studentName, studentId, assessmentId, marks, 
                    feedback.isEmpty() ? "No feedback" : feedback);
        }
    }
    
    // ==================== Helper Methods for AssessmentManager ====================
    
    /**
     * Clear all assessments (used by AssessmentManager)
     */
    public void clearAssessments() {
        assessments.clear();
    }
    
    /**
     * Clear all student marks (used by AssessmentManager)
     */
    public void clearStudentMarks() {
        studentMarks.clear();
    }
    
    // ==================== Getters and Setters ====================
    
    public String getLeaderEmail() {
        return leaderEmail;
    }
    
    public void setLeaderEmail(String leaderEmail) {
        this.leaderEmail = leaderEmail;
    }
} 
    // ==================== Inner Classes ====================
