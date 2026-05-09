package IOManage;

import Entity.ClassGroup;
import Entity.Module;
import java.io.*;
import java.util.ArrayList;

public class ClassManager {

    public static ArrayList<ClassGroup> classGroups = new ArrayList<>();
    private static final String FILE_PATH = "data/classgroup.txt";

    // ================= CRUD =================
    public static void addClassGroup(ClassGroup cg) {
        loadFromFile(); // load existing first
        classGroups.add(cg);
        saveToFile();
    }

    public static ClassGroup findByClassCode(String classCode) {
        for (ClassGroup cg : classGroups) {
            if (cg.getClassCode().equalsIgnoreCase(classCode)) {
                return cg;
            }
        }
        return null;
    }

    public static boolean deleteClassGroup(ClassGroup cg) {
        boolean removed = classGroups.remove(cg);
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    public static void updateClassGroup(ClassGroup cg) {
        for (int i = 0; i < classGroups.size(); i++) {
            if (classGroups.get(i).getClassCode().equalsIgnoreCase(cg.getClassCode())) {
                classGroups.set(i, cg);
                break;
            }
        }
        saveToFile();
    }

    /**
     * Check if a module has any associated class groups
     * @param moduleCode The module code to check
     * @return true if there are classes for the module, false otherwise
     */
    public static boolean hasClassesForModule(String moduleCode) {
        loadFromFile(); // reload to ensure fresh state
        
        for (ClassGroup cg : classGroups) {
            if (cg.getModule() != null && cg.getModule().getCode().equalsIgnoreCase(moduleCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a student from all classes of a specific module
     * @param studentEmail The student's email
     * @param moduleCode The module code
     */
    public static void removeStudentFromModuleClasses(String studentEmail, String moduleCode) {
        loadFromFile(); // reload to ensure fresh state
        
        for (ClassGroup cg : classGroups) {
            if (cg.getModule() != null && cg.getModule().getCode().equalsIgnoreCase(moduleCode)) {
                if (cg.getStudentEmails() != null && cg.getStudentEmails().contains(studentEmail)) {
                    cg.getStudentEmails().remove(studentEmail);
                }
            }
        }
        
        // Save changes immediately after removing student from all classes
        saveToFile();
    }

    /**
     * Remove a lecturer from all classes of a specific module
     * @param lecturerEmail The lecturer's email
     * @param moduleCode The module code
     */
    public static void removeLecturerFromModuleClasses(String lecturerEmail, String moduleCode) {
        loadFromFile(); // reload to ensure fresh state
        
        for (ClassGroup cg : classGroups) {
            if (cg.getModule() != null && cg.getModule().getCode().equalsIgnoreCase(moduleCode)) {
                if (cg.getLecturerEmails() != null && cg.getLecturerEmails().contains(lecturerEmail)) {
                    cg.getLecturerEmails().remove(lecturerEmail);
                }
            }
        }
        
        // Save changes immediately after removing lecturer from all classes
        saveToFile();
    }

    // ================= FILE SAVE =================
    public static void saveToFile() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        File file = new File(FILE_PATH);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (ClassGroup cg : classGroups) {
                // Format: classCode,moduleCode,time,classroom,LECTURER:,lecturerEmail1,lecturerEmail2,...,STUDENT:,studentEmail1,studentEmail2,...
                StringBuilder line = new StringBuilder();
                line.append(cg.getClassCode()).append(",")
                    .append(cg.getModule().getCode()).append(",")
                    .append(cg.getTime() != null ? cg.getTime() : "").append(",")
                    .append(cg.getClassroom() != null ? cg.getClassroom() : "");
                
                // Add lecturer emails if any
                if (!cg.getLecturerEmails().isEmpty()) {
                    line.append(",LECTURER:");
                    for (String lecturerEmail : cg.getLecturerEmails()) {
                        line.append(",").append(lecturerEmail);
                    }
                }
                
                // Add student emails if any
                if (!cg.getStudentEmails().isEmpty()) {
                    line.append(",STUDENT:");
                    for (String studentEmail : cg.getStudentEmails()) {
                        line.append(",").append(studentEmail);
                    }
                }
                
                pw.println(line.toString());
            }
            System.out.println("Class groups saved successfully");
        } catch (IOException e) {
            System.out.println("Error saving class groups: " + e.getMessage());
        }
    }

    // ================= FILE LOAD =================
    public static void loadFromFile() {
        classGroups.clear();

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("classgroup.txt not found, no class group loaded");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1); // include empty fields

                if (parts.length < 2) {
                    System.out.println("Corrupted line skipped: " + line);
                    continue;
                }

                String classCode = parts[0].trim();
                String moduleCode = parts[1].trim();
                String time = parts.length > 2 ? parts[2].trim() : "";
                String classroom = parts.length > 3 ? parts[3].trim() : "";

                Module module = ModuleManager.findModuleByCode(moduleCode);
                if (module == null) {
                    System.out.println("Module not found for class group: " + classCode);
                    continue;
                }

                ClassGroup cg = new ClassGroup(classCode, module, time, classroom);
                
                // Load lecturer emails and student emails (format: ...,LECTURER:,email1,email2,...,STUDENT:,email1,email2,...)
                ArrayList<String> lecturerList = new ArrayList<>();
                ArrayList<String> studentList = new ArrayList<>();
                boolean readingLecturers = false;
                boolean readingStudents = false;
                
                for (int i = 4; i < parts.length; i++) {
                    String part = parts[i].trim();
                    if (part.isEmpty()) continue;
                    
                    if (part.equals("LECTURER:") || part.startsWith("LECTURER:")) {
                        readingLecturers = true;
                        readingStudents = false; // Stop reading students if we hit LECTURER marker
                        if (part.length() > "LECTURER:".length()) {
                            lecturerList.add(part.substring("LECTURER:".length()));
                        }
                        continue;
                    }
                    
                    if (part.equals("STUDENT:") || part.startsWith("STUDENT:")) {
                        readingStudents = true;
                        readingLecturers = false; // Stop reading lecturers if we hit STUDENT marker
                        if (part.length() > "STUDENT:".length()) {
                            studentList.add(part.substring("STUDENT:".length()));
                        }
                        continue;
                    }
                    
                    if (readingLecturers) {
                        lecturerList.add(part);
                    } else if (readingStudents) {
                        studentList.add(part);
                    }
                }
                
                // Add lecturer emails to the class group
                for (String lecturerEmail : lecturerList) {
                    cg.addLecturer(lecturerEmail);
                }
                
                // Add student emails to the class group
                for (String studentEmail : studentList) {
                    cg.addStudent(studentEmail);
                }
                
                classGroups.add(cg);
            }
            System.out.println("Class groups loaded successfully");
        } catch (IOException e) {
            System.out.println("Error loading class groups: " + e.getMessage());
        }
    }
}
