package IOManage;

import java.io.*;
import java.util.ArrayList;
import Entity.Module;
import Users.AcademicLeader;

public class ModuleManager {

    // data read from module.txt will be stored here
    public static ArrayList<Module> modules = new ArrayList<>();

    // ================= BASIC CRUD =================
    public static void addModule(Module m) {
        loadAllModules(); // always reload first

        // prevent duplicate module (same code + same leader)
        for (Module existing : modules) {
            if (existing.getCode().equalsIgnoreCase(m.getCode())
                    && existing.getAcademicLeaderEmail().equalsIgnoreCase(m.getAcademicLeaderEmail())) {
                System.out.println("Module already exists.");
                return;
            }
        }

        modules.add(m);
        saveToFile();
    }

    public static Module findModuleByCode(String code) {
        loadAllModules(); // ensure latest data
        for (Module m : modules) {
            if (m.getCode().equalsIgnoreCase(code)) {
                return m;
            }
        }
        return null;
    }

    public static void editModule(Module m) {
        saveToFile();
    }

    public static void deleteModule(Module m) {
        loadAllModules();
        modules.removeIf(existing ->
                existing.getCode().equalsIgnoreCase(m.getCode())
                        && existing.getAcademicLeaderEmail().equalsIgnoreCase(m.getAcademicLeaderEmail())
        );
        // Write modules directly to file (don't use saveToFile which re-reads from file)
        File file = new File("data/module.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Module mod : modules) {
                StringBuilder sb = new StringBuilder();
                sb.append(mod.getCode()).append(",")
                  .append(mod.getName()).append(",")
                  .append(mod.getAcademicLeaderEmail());

                for (String lec : mod.getLecturerEmails()) {
                    sb.append(",").append(lec);
                }
                
                if (!mod.getStudentEmails().isEmpty()) {
                    sb.append(",STUDENT:");
                    for (String student : mod.getStudentEmails()) {
                        sb.append(",").append(student);
                    }
                }

                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving modules after delete: " + e.getMessage());
        }
    }

    // ================= STUDENT REGISTRATION =================
    public static boolean registerStudentToModule(String moduleCode, String studentEmail) {
        loadAllModules(); // ensure latest data
        
        Module m = findModuleByCode(moduleCode);
        if (m == null) {
            return false;
        }
        
        if (m.getStudentEmails().contains(studentEmail)) {
            return false; // already registered
        }
        
        m.addStudent(studentEmail);
        saveToFile();
        return true;
    }

    /**
     * Remove a student from a specific module
     * @param moduleCode The module code
     * @param studentEmail The student's email
     * @return true if successfully removed, false otherwise
     */
    public static boolean removeStudentFromModule(String moduleCode, String studentEmail) {
        loadAllModules(); // ensure latest data
        
        Module m = findModuleByCode(moduleCode);
        if (m == null) {
            return false;
        }
        
        if (!m.getStudentEmails().contains(studentEmail)) {
            return false; // student not registered
        }
        
        m.removeStudent(studentEmail);
        saveToFile();
        return true;
    }

    /**
     * Switch a student from one module to another
     * @param oldModuleCode The current module code
     * @param newModuleCode The new module code
     * @param studentEmail The student's email
     * @return true if successfully switched, false otherwise
     */
    public static boolean switchStudentModule(String oldModuleCode, String newModuleCode, String studentEmail) {
        loadAllModules(); // ensure latest data
        
        Module oldModule = findModuleByCode(oldModuleCode);
        Module newModule = findModuleByCode(newModuleCode);
        
        if (oldModule == null || newModule == null) {
            return false;
        }
        
        if (!oldModule.getStudentEmails().contains(studentEmail)) {
            return false; // student not registered in old module
        }
        
        if (newModule.getStudentEmails().contains(studentEmail)) {
            return false; // student already registered in new module
        }
        
        // Remove from old module and add to new module
        oldModule.removeStudent(studentEmail);
        newModule.addStudent(studentEmail);
        saveToFile();
        return true;
    }

    // ================= LECTURER ASSIGN =================
    public static boolean assignLecturer(Module m, String lecturerEmail, AcademicLeader leader) {
        // Ensure we have the latest data and the module is in the modules list
        loadAllModules();
        
        // Find the module in the modules list to ensure we're modifying the correct instance
        Module moduleInList = findModuleByCode(m.getCode());
        if (moduleInList == null) {
            return false;
        }

        if (!moduleInList.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
            return false;
        }

        if (!leader.getLecturerEmails().contains(lecturerEmail)) {
            return false;
        }

        if (moduleInList.getLecturerEmails().contains(lecturerEmail)) {
            return false;
        }

        moduleInList.addLecturer(lecturerEmail);
        saveToFile();
        return true;
    }

    public static boolean removeLecturer(Module m, String lecturerEmail, AcademicLeader leader) {

        if (!m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
            return false;
        }

        if (!m.getLecturerEmails().contains(lecturerEmail)) {
            return false;
        }

        // Remove lecturer from all class groups of this module
        ClassManager.removeLecturerFromModuleClasses(lecturerEmail, m.getCode());
        
        // Re-find the module from the modules list to ensure we're modifying the correct reference
        // (ClassManager.loadFromFile may have triggered loadAllModules which reloads the modules list)
        Module moduleInList = findModuleByCode(m.getCode());
        if (moduleInList != null) {
            moduleInList.removeLecturer(lecturerEmail);
        }
        
        saveToFile();
        return true;
    }

    public static boolean editModule(Module m, AcademicLeader leader, String newName) {

        if (!m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
            return false;
        }

        m.setName(newName);
        saveToFile();
        return true;
    }

    public static boolean deleteModule(Module m, AcademicLeader leader) {

        if (!m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
            return false;
        }

        // Check if module has any associated classes
        if (ClassManager.hasClassesForModule(m.getCode())) {
            return false; // Cannot delete module with existing classes
        }

        deleteModule(m); // reuse base delete
        return true;
    }

    // ================= FILE SAVE =================
    public static void saveToFile() {

        ArrayList<Module> allModules = new ArrayList<>();
        File file = new File("data/module.txt");

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;
                while ((line = br.readLine()) != null) {

                    String[] parts = line.split(",");
                    if (parts.length < 3) continue;

                    String code = parts[0].trim();
                    String name = parts[1].trim();
                    String leaderEmail = parts[2].trim();

                    ArrayList<String> lecturerList = new ArrayList<>();
                    ArrayList<String> studentList = new ArrayList<>();
                    boolean readingStudents = false;
                    
                    for (int i = 3; i < parts.length; i++) {
                        String part = parts[i].trim();
                        if (part.isEmpty()) continue;
                        
                        if (part.equals("STUDENT:") || part.startsWith("STUDENT:")) {
                            readingStudents = true;
                            if (part.length() > "STUDENT:".length()) {
                                studentList.add(part.substring("STUDENT:".length()));
                            }
                            continue;
                        }
                        
                        if (readingStudents) {
                            studentList.add(part);
                        } else {
                            lecturerList.add(part);
                        }
                    }

                    Module mod = new Module(code, name, leaderEmail, lecturerList);
                    for (String studentEmail : studentList) {
                        mod.addStudent(studentEmail);
                    }
                    allModules.add(mod);
                }

            } catch (IOException e) {
                System.out.println("Error loading modules before save: " + e.getMessage());
            }
        }

        // remove outdated versions
        for (Module m : modules) {
            allModules.removeIf(existing ->
                    existing.getCode().equalsIgnoreCase(m.getCode())
                            && existing.getAcademicLeaderEmail().equalsIgnoreCase(m.getAcademicLeaderEmail())
            );
        }

        allModules.addAll(modules);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Module m : allModules) {
                StringBuilder sb = new StringBuilder();
                sb.append(m.getCode()).append(",")
                  .append(m.getName()).append(",")
                  .append(m.getAcademicLeaderEmail());

                for (String lec : m.getLecturerEmails()) {
                    sb.append(",").append(lec);
                }
                
                if (!m.getStudentEmails().isEmpty()) {
                    sb.append(",STUDENT:");
                    for (String student : m.getStudentEmails()) {
                        sb.append(",").append(student);
                    }
                }

                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving modules: " + e.getMessage());
        }
    }

    // ================= FILE LOAD =================
    public static void loadFromFile(String currentLeaderEmail) {

        modules.clear();
        File file = new File("data/module.txt");

        if (!file.exists()) {
            System.out.println("module.txt not found, no module loaded");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String code = parts[0].trim();
                String name = parts[1].trim();
                String leaderEmail = parts[2].trim();

                if (!leaderEmail.equalsIgnoreCase(currentLeaderEmail)) {
                    continue;
                }

                ArrayList<String> lecturerList = new ArrayList<>();
                ArrayList<String> studentList = new ArrayList<>();
                boolean readingStudents = false;
                
                for (int i = 3; i < parts.length; i++) {
                    String part = parts[i].trim();
                    if (part.isEmpty()) continue;
                    
                    if (part.equals("STUDENT:") || part.startsWith("STUDENT:")) {
                        readingStudents = true;
                        if (part.length() > "STUDENT:".length()) {
                            studentList.add(part.substring("STUDENT:".length()));
                        }
                        continue;
                    }
                    
                    if (readingStudents) {
                        studentList.add(part);
                    } else {
                        lecturerList.add(part);
                    }
                }

                Module mod = new Module(code, name, leaderEmail, lecturerList);
                for (String studentEmail : studentList) {
                    mod.addStudent(studentEmail);
                }
                modules.add(mod);
            }

            System.out.println("Modules loaded successfully!");

        } catch (IOException e) {
            System.out.println("Error loading modules: " + e.getMessage());
        }
    }

    public static void loadAllModules() {

        modules.clear();
        File file = new File("data/module.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String code = parts[0].trim();
                String name = parts[1].trim();
                String leaderEmail = parts[2].trim();

                ArrayList<String> lecturerList = new ArrayList<>();
                ArrayList<String> studentList = new ArrayList<>();
                boolean readingStudents = false;
                
                for (int i = 3; i < parts.length; i++) {
                    String part = parts[i].trim();
                    if (part.isEmpty()) continue;
                    
                    if (part.equals("STUDENT:") || part.startsWith("STUDENT:")) {
                        readingStudents = true;
                        if (part.length() > "STUDENT:".length()) {
                            studentList.add(part.substring("STUDENT:".length()));
                        }
                        continue;
                    }
                    
                    if (readingStudents) {
                        studentList.add(part);
                    } else {
                        lecturerList.add(part);
                    }
                }

                Module mod = new Module(code, name, leaderEmail, lecturerList);
                for (String studentEmail : studentList) {
                    mod.addStudent(studentEmail);
                }
                modules.add(mod);
            }

        } catch (IOException e) {
            System.out.println("Error loading modules: " + e.getMessage());
        }
    }
}
