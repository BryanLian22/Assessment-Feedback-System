package IOManage;

import Entity.Module;
import Users.Lecturer;
import Users.Student;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AssessmentManager { //manage asssessments//

    // Toggle console output for student mark saving (used when no context is provided)
    private static final boolean SHOW_MARK_SAVE_OUTPUT = false;

    // Note: These methods are placeholders for future Entity.Assessment functionality
    // Current implementation uses Lecturer.Assessment via saveLecturerData/loadLecturerData
    
    public static void addAssessment(Entity.Assessment a) {
        // Placeholder - not currently used
    }

    public static void saveToFile() {
        // Placeholder - not currently used
    }

    public static void loadFromFile(ArrayList<Student> allStudents, ArrayList<Module> allModules) {
        // Placeholder - not currently used
    }
    
    /**
     * Save lecturer's assessments and student marks to separate text files.
     * Default: show assessment save message, marks message controlled by toggle.
     */
    public static void saveLecturerData(Lecturer lecturer) {
        saveLecturerData(lecturer, true, SHOW_MARK_SAVE_OUTPUT);
    }

    /**
     * Save lecturer data with control over console output.
     * @param lecturer lecturer whose data is saved
     * @param showAssessmentMsg whether to print assessment save confirmation
     * @param showMarksMsg whether to print marks save confirmation
     */
    public static void saveLecturerData(Lecturer lecturer, boolean showAssessmentMsg, boolean showMarksMsg) {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        // Use assessments.txt and marks.txt as filenames
        String assessmentsFilename = "assessments.txt";
        String marksFilename = "marks.txt";
        
        File assessmentsFile = new File(dataDir, assessmentsFilename);
        File marksFile = new File(dataDir, marksFilename);
        
        // Save assessments with lecturer email next to module code
        // Merge with existing assessments from other lecturers
        List<String> allAssessmentLines = new ArrayList<>();
        String currentLecturerEmail = lecturer.getEmail();
        
        // Read existing assessments from file (if it exists)
        if (assessmentsFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(assessmentsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split("\\|", -1);
                    // Keep assessments from other lecturers (skip current lecturer's assessments)
                    if (parts.length >= 7) {
                        // New format: has lecturer email
                        String lineLecturerEmail = unescape(parts[2]);
                        if (!currentLecturerEmail.equals(lineLecturerEmail)) {
                            allAssessmentLines.add(line); // Keep other lecturers' assessments
                        }
                    } else if (parts.length >= 6) {
                        // Old format: no lecturer email, assume it belongs to current lecturer if file was just created
                        // For safety, we'll skip old format entries when current lecturer saves
                        // (they'll be preserved if they don't match current lecturer's email pattern)
                    }
                }
            } catch (IOException e) {
                System.out.println("Warning: Could not read existing assessments: " + e.getMessage());
            }
        }
        
        // Add current lecturer's assessments
        int lecturerAssessmentCount = 0;
        for (Lecturer.Assessment assessment : lecturer.getAllAssessments()) {
            allAssessmentLines.add(String.format("%s|%s|%s|%s|%s|%.2f|%s",
                escape(assessment.getAssessmentId()),
                escape(assessment.getModuleCode()),
                escape(lecturer.getEmail()),
                escape(assessment.getAssessmentName()),
                assessment.getType().name(),
                assessment.getMaxMarks(),
                escape(assessment.getDescription())
            ));
            lecturerAssessmentCount++;
        }
        
        // Write all assessments back to file
        try (PrintWriter pw = new PrintWriter(new FileWriter(assessmentsFile))) {
            for (String line : allAssessmentLines) {
                pw.println(line);
            }
            if (showAssessmentMsg) {
                System.out.println("Assessments saved successfully (" + lecturerAssessmentCount + " for you)");
            }
        } catch (IOException e) {
            System.out.println("Error saving assessments: " + e.getMessage());
        }
        
        // Save student marks with new format: assessmentID|assessmentName|assessmentType|lecturerEmail|studentEmail|studentName|marks|feedback
        // Merge with existing marks from other lecturers
        List<String> allMarkLines = new ArrayList<>();
        int lecturerMarkCount = 0;
        
        // Read existing marks from file (if it exists)
        if (marksFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(marksFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split("\\|", -1);
                    // Keep marks from other lecturers (skip current lecturer's marks)
                    if (parts.length >= 8) {
                        // New format: has lecturer email at index 3
                        String lineLecturerEmail = unescape(parts[3]);
                        if (!currentLecturerEmail.equals(lineLecturerEmail)) {
                            allMarkLines.add(line); // Keep other lecturers' marks
                        }
                    } else if (parts.length >= 5) {
                        // Old format: assessmentId|studentId|studentName|marks|feedback
                        // For safety, we'll skip old format entries when current lecturer saves
                    }
                }
            } catch (IOException e) {
                System.out.println("Warning: Could not read existing marks: " + e.getMessage());
            }
        }
        
        // Add current lecturer's marks
        for (Lecturer.Assessment assessment : lecturer.getAllAssessments()) {
            String assessmentId = assessment.getAssessmentId();
            String assessmentName = assessment.getAssessmentName();
            String assessmentType = assessment.getType().name();
            List<Lecturer.StudentMark> marks = lecturer.getMarksForAssessment(assessmentId);
            for (Lecturer.StudentMark mark : marks) {
                allMarkLines.add(String.format("%s|%s|%s|%s|%s|%s|%.2f|%s",
                    escape(assessmentId),
                    escape(assessmentName),
                    escape(assessmentType),
                    escape(lecturer.getEmail()),
                    escape(mark.getStudentId()), // Assuming studentId is the email
                    escape(mark.getStudentName()),
                    mark.getMarks(),
                    escape(mark.getFeedback())
                ));
                lecturerMarkCount++;
            }
        }
        
        // Write all marks back to file
        try (PrintWriter pw = new PrintWriter(new FileWriter(marksFile))) {
            for (String line : allMarkLines) {
                pw.println(line);
            }
            if (showMarksMsg) {
                System.out.println("Marks saved successfully (" + lecturerMarkCount + " for you)");
            }
        } catch (IOException e) {
            System.out.println("Error saving student marks: " + e.getMessage());
        }
    }
    
    /**
     * Load lecturer's assessments and student marks from separate text files
     */
    public static void loadLecturerData(Lecturer lecturer) {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            return;
        }
        
        // Use assessments.txt and marks.txt as filenames
        String assessmentsFilename = "assessments.txt";
        String marksFilename = "marks.txt";
        
        File assessmentsFile = new File(dataDir, assessmentsFilename);
        File marksFile = new File(dataDir, marksFilename);
        
        // Clear existing data
        lecturer.clearAssessments();
        lecturer.clearStudentMarks();
        
        // Load assessments from separate file
        if (assessmentsFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(assessmentsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split("\\|", -1);
                    // Support old format (6 parts) and new format (7 parts with lecturer email)
                    if (parts.length < 6) continue;
                    
                    // Load assessment
                    String assessmentId = unescape(parts[0]);
                    String moduleCode = unescape(parts[1]);
                    String lecturerEmail;
                    String assessmentName;
                    Lecturer.AssessmentType assessmentType;
                    double maxMarks;
                    String description;
                    
                    // Check if new format (7 parts) or old format (6 parts)
                    if (parts.length >= 7) {
                        // New format: assessmentId|moduleCode|lecturerEmail|assessmentName|type|maxMarks|description
                        lecturerEmail = unescape(parts[2]);
                        assessmentName = unescape(parts[3]);
                        try {
                            assessmentType = Lecturer.AssessmentType.valueOf(parts[4]);
                        } catch (IllegalArgumentException e) {
                            assessmentType = Lecturer.AssessmentType.QUIZ; // Default
                        }
                        maxMarks = Double.parseDouble(parts[5]);
                        description = unescape(parts[6]);
                    } else {
                        // Old format: assessmentId|moduleCode|assessmentName|type|maxMarks|description
                        // For old format, assume it belongs to current lecturer
                        lecturerEmail = lecturer.getEmail();
                        assessmentName = unescape(parts[2]);
                        try {
                            assessmentType = Lecturer.AssessmentType.valueOf(parts[3]);
                        } catch (IllegalArgumentException e) {
                            assessmentType = Lecturer.AssessmentType.QUIZ; // Default
                        }
                        maxMarks = Double.parseDouble(parts[4]);
                        description = unescape(parts[5]);
                    }
                    
                    // Only load assessments that belong to this lecturer
                    if (lecturer.getEmail().equals(lecturerEmail)) {
                        // Use designAssessment without auto-save to avoid recursion
                        try {
                            lecturer.designAssessmentWithoutSave(assessmentId, moduleCode, assessmentName,
                                assessmentType, maxMarks, description);
                        } catch (IllegalArgumentException e) {
                            // Skip assessments for modules that don't exist or lecturer is not assigned to
                            System.out.println("Warning: Skipping assessment " + assessmentId + " for module '" + moduleCode + "': " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading assessments: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error parsing assessment data: " + e.getMessage());
            }
        }
        
        // Load student marks from separate file
        if (marksFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(marksFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split("\\|", -1);
                    // Support old format (5 parts) and new format (8 parts)
                    if (parts.length < 5) continue;
                    
                    String assessmentId;
                    String studentId;
                    String studentName;
                    double marks;
                    String feedback;
                    String lecturerEmail;
                    
                    // Check if new format (8 parts) or old format (5 parts)
                    if (parts.length >= 8) {
                        // New format: assessmentID|assessmentName|assessmentType|lecturerEmail|studentEmail|studentName|marks|feedback
                        assessmentId = unescape(parts[0]);
                        lecturerEmail = unescape(parts[3]);
                        studentId = unescape(parts[4]); // studentEmail
                        studentName = unescape(parts[5]);
                        marks = Double.parseDouble(parts[6]);
                        feedback = unescape(parts[7]);
                    } else {
                        // Old format: assessmentId|studentId|studentName|marks|feedback
                        assessmentId = unescape(parts[0]);
                        studentId = unescape(parts[1]);
                        studentName = unescape(parts[2]);
                        marks = Double.parseDouble(parts[3]);
                        feedback = unescape(parts[4]);
                        lecturerEmail = lecturer.getEmail(); // Assume it belongs to current lecturer
                    }
                    
                    // Only load marks that belong to this lecturer
                    boolean belongsToLecturer = !(parts.length >= 8 && !lecturer.getEmail().equals(lecturerEmail));
                    
                    if (belongsToLecturer) {
                        // Enter marks first without auto-save
                        try {
                            lecturer.enterMarksWithoutSave(assessmentId, studentId, studentName, marks);
                            // Then add feedback if provided
                            if (feedback != null && !feedback.isEmpty()) {
                                lecturer.provideFeedbackWithoutSave(assessmentId, studentId, feedback);
                            }
                        } catch (IllegalArgumentException e) {
                            // Assessment might not exist yet, skip this mark
                            System.out.println("Warning: Could not load mark for assessment " + assessmentId + ": " + e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading student marks: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error parsing marks data: " + e.getMessage());
            }
        }
    }
    
    /**
     * Escape special characters in strings for file storage
     */
    private static String escape(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("|", "\\|")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
    
    /**
     * Unescape special characters from file storage
     */
    private static String unescape(String str) {
        if (str == null) return "";
        return str.replace("\\r", "\r")
                  .replace("\\n", "\n")
                  .replace("\\|", "|")
                  .replace("\\\\", "\\");
    }
}




