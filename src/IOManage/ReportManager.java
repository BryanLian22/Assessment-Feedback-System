package IOManage;

import Entity.*;
import Entity.Module;
import Entity.Report.ClassReportData;
import Entity.Report.StudentReportData;
import Entity.Report.AssessmentMark;
import Users.Lecturer;
import Users.User;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportManager {

    public static ArrayList<Report> reports = new ArrayList<>();
    private static final String REPORT_FILE = "data/report.txt";
    private static final String REPORT_DETAIL_FILE = "data/report_detail.txt";
    
    /**
     * Generate a comprehensive module report with all classes, students, marks, and grades
     */
    public static Report generateComprehensiveReport(Module module, String leaderEmail) {
        // Ensure data is loaded
        ClassManager.loadFromFile();
        UserManager.loadFromFile();
        GradingSystemManager.loadFromFile();
        GradingSystem gradingSystem = GradingSystemManager.getGradingSystem();
        
        // Load assessments data
        Map<String, String[]> assessmentData = loadAssessmentData();
        
        String reportId = getNextReportId();
        Report report = new Report(reportId, module.getCode(), leaderEmail);
        report.setModuleName(module.getName());
        
        // Get all classes for this module
        ArrayList<ClassGroup> moduleClasses = new ArrayList<>();
        for (ClassGroup cg : ClassManager.classGroups) {
            if (cg.getModule() != null && cg.getModule().getCode().equalsIgnoreCase(module.getCode())) {
                moduleClasses.add(cg);
            }
        }
        
        if (moduleClasses.isEmpty()) {
            return null;
        }
        
        int totalStudents = 0;
        double totalModuleMarks = 0;
        double totalModuleMaxMarks = 0;
        
        // Process each class
        for (ClassGroup cg : moduleClasses) {
            String lecturerEmail = cg.getAssignedLecturer();
            String lecturerName = "Unassigned";
            if (lecturerEmail != null) {
                User lecturer = UserManager.findByEmail(lecturerEmail);
                if (lecturer != null) {
                    lecturerName = lecturer.getName();
                }
            }
            
            ClassReportData classReport = new ClassReportData(cg.getClassCode(), 
                lecturerEmail != null ? lecturerEmail : "", lecturerName);
            
            double classTotalMarks = 0;
            double classTotalMaxMarks = 0;
            
            // Process each student in the class
            for (String studentEmail : cg.getStudentEmails()) {
                User studentUser = UserManager.findByEmail(studentEmail);
                String studentName = studentUser != null ? studentUser.getName() : studentEmail;
                
                StudentReportData studentReport = new StudentReportData(studentEmail, studentName);
                
                double studentTotalMarks = 0;
                double studentTotalMaxMarks = 0;
                
                // Get all assessments for this module and find student's marks
                for (Map.Entry<String, String[]> entry : assessmentData.entrySet()) {
                    String assessmentId = entry.getKey();
                    String[] assessInfo = entry.getValue();
                    String assessModuleCode = assessInfo[0];
                    String assessName = assessInfo[2];
                    String assessType = assessInfo[3];
                    double maxMarks = Double.parseDouble(assessInfo[4]);
                    
                    if (!assessModuleCode.equalsIgnoreCase(module.getCode())) {
                        continue;
                    }
                    
                    Double studentMarks = findStudentMark(studentEmail, assessmentId);
                    if (studentMarks != null) {
                        double percentage = GradingSystem.calculatePercentage(studentMarks, maxMarks);
                        String grade = gradingSystem.getGrade(percentage);
                        
                        AssessmentMark mark = new AssessmentMark(assessmentId, assessName, assessType,
                            studentMarks, maxMarks, percentage, grade);
                        studentReport.addAssessmentMark(mark);
                        
                        // Traditional weighted average: sum actual marks and max marks
                        studentTotalMarks += studentMarks;
                        studentTotalMaxMarks += maxMarks;
                        
                        classTotalMarks += studentMarks;
                        classTotalMaxMarks += maxMarks;
                    }
                }
                
                if (studentTotalMaxMarks > 0) {
                    // Traditional average: (total marks / total max marks) * 100
                    double avgPercentage = (studentTotalMarks / studentTotalMaxMarks) * 100.0;
                    studentReport.setAveragePercentage(avgPercentage);
                }
                
                if (!studentReport.getAssessmentMarks().isEmpty()) {
                    classReport.addStudentReport(studentReport);
                }
            }
            
            if (classTotalMaxMarks > 0) {
                // Traditional class average: (total class marks / total class max marks) * 100
                classReport.setClassAverage((classTotalMarks / classTotalMaxMarks) * 100.0);
                totalModuleMarks += classTotalMarks;
                totalModuleMaxMarks += classTotalMaxMarks;
            }
            
            totalStudents += cg.getStudentEmails().size();
            report.addClassReport(classReport);
        }
        
        report.setTotalStudents(totalStudents);
        if (totalModuleMaxMarks > 0) {
            // Traditional module average: (total marks / total max marks) * 100
            report.setModuleAverage((totalModuleMarks / totalModuleMaxMarks) * 100.0);
        }
        
        return report;
    }
    
    private static Double findStudentMark(String studentEmail, String assessmentId) {
        File marksFile = new File("data/marks.txt");
        if (!marksFile.exists()) return null;
        
        try (BufferedReader br = new BufferedReader(new FileReader(marksFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    String markAssessId = parts[0].trim();
                    String markStudentEmail = parts[4].trim();
                    if (markAssessId.equalsIgnoreCase(assessmentId) && 
                        markStudentEmail.equalsIgnoreCase(studentEmail)) {
                        return Double.parseDouble(parts[6].trim());
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {}
        return null;
    }
    
    private static Map<String, String[]> loadAssessmentData() {
        Map<String, String[]> data = new HashMap<>();
        File file = new File("data/assessments.txt");
        if (!file.exists()) return data;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    String assessmentId = parts[0].trim();
                    String moduleCode = parts[1].trim();
                    String lecturerEmail = parts[2].trim();
                    String name = parts[3].trim();
                    String type = parts[4].trim();
                    String maxMarks = parts[5].trim();
                    data.put(assessmentId, new String[]{moduleCode, lecturerEmail, name, type, maxMarks});
                }
            }
        } catch (IOException e) {}
        return data;
    }
    
    private static String getNextReportId() {
        int max = 0;
        File file = new File(REPORT_FILE);

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    if (parts.length < 1) continue;
                    String id = parts[0].trim();
                    if (id.startsWith("R")) {
                        String numPart = id.substring(1);
                        if (numPart.matches("\\d{1,4}")) {
                            try {
                                int n = Integer.parseInt(numPart);
                                if (n > max) max = n;
                            } catch (NumberFormatException ignore) {}
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error reading report file for next ID: " + e.getMessage());
            }
        }
        return "R" + (max + 1);
    }
    
    /**
     * Save a report (replaces existing report for same module)
     */
    public static void saveReport(Report report) {
        removeReportByModuleCode(report.getModuleCode());
        reports.add(report);
        saveSummaryToFile();
        saveDetailToFile(report);
    }
    
    private static void removeReportByModuleCode(String moduleCode) {
        reports.removeIf(r -> r.getModuleCode().equalsIgnoreCase(moduleCode));
        
        ArrayList<String> linesToKeep = new ArrayList<>();
        File file = new File(REPORT_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String lineModuleCode = parts[1].trim();
                        if (!lineModuleCode.equalsIgnoreCase(moduleCode)) {
                            linesToKeep.add(line);
                        }
                    }
                }
            } catch (IOException e) {}
        }
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String line : linesToKeep) {
                pw.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error saving report file: " + e.getMessage());
        }
        
        removeDetailByModuleCode(moduleCode);
    }
    
    private static void removeDetailByModuleCode(String moduleCode) {
        ArrayList<String> linesToKeep = new ArrayList<>();
        File file = new File(REPORT_DETAIL_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split("\\|");
                    if (parts.length >= 2) {
                        String lineModuleCode = parts[1].trim();
                        if (!lineModuleCode.equalsIgnoreCase(moduleCode)) {
                            linesToKeep.add(line);
                        }
                    }
                }
            } catch (IOException e) {}
        }
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String line : linesToKeep) {
                pw.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error saving report detail file: " + e.getMessage());
        }
    }
    
    private static void saveSummaryToFile() {
        File file = new File(REPORT_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Report r : reports) {
                pw.println(r.getReportId() + "," + r.getModuleCode() + "," + 
                    r.getAcademicLeaderEmail() + "," + r.getTotalStudents() + "," + 
                    r.getOverallAverage() + "," + r.getGeneratedDate());
            }
        } catch (IOException e) {
            System.out.println("Error saving reports: " + e.getMessage());
        }
    }
    
    private static void saveDetailToFile(Report report) {
        File file = new File(REPORT_DETAIL_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            for (ClassReportData classReport : report.getClassReports()) {
                for (StudentReportData studentReport : classReport.getStudentReports()) {
                    for (AssessmentMark mark : studentReport.getAssessmentMarks()) {
                        pw.println(report.getReportId() + "|" + report.getModuleCode() + "|" +
                            classReport.getClassCode() + "|" + classReport.getLecturerEmail() + "|" +
                            studentReport.getStudentEmail() + "|" + studentReport.getStudentName() + "|" +
                            mark.getAssessmentId() + "|" + mark.getAssessmentName() + "|" + mark.getAssessmentType() + "|" +
                            String.format("%.2f", mark.getMarks()) + "|" + String.format("%.2f", mark.getMaxMarks()) + "|" +
                            String.format("%.2f", mark.getGpa()) + "|" + mark.getGrade() + "|" +
                            String.format("%.2f", studentReport.getCgpa()));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving report details: " + e.getMessage());
        }
    }
    
    public static void loadReportFromFile(String currentLeaderEmail) {
        reports.clear();
        File file = new File(REPORT_FILE);
        if (!file.exists()) {
            System.out.println("report.txt not found, no report loaded");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String reportId = parts[0].trim();
                String moduleCode = parts[1].trim();
                String academicLeaderEmail = parts[2].trim();
                String totalStudents = parts[3].trim();
                String overallAverage = parts[4].trim();
                String generatedDate = parts.length > 5 ? parts[5].trim() : "";

                if (!academicLeaderEmail.equalsIgnoreCase(currentLeaderEmail)) continue;

                Report r = new Report(reportId, moduleCode, academicLeaderEmail, totalStudents, overallAverage);
                r.setGeneratedDate(generatedDate);
                reports.add(r);
            }
            System.out.println("Reports loaded successfully!");
        } catch (IOException e) {
            System.out.println("Error loading reports: " + e.getMessage());
        }
    }
    
    public static void loadAllReportFromFile() {
        reports.clear();
        File file = new File(REPORT_FILE);
        if (!file.exists()) {
            System.out.println("report.txt not found, no report loaded");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String reportId = parts[0].trim();
                String moduleCode = parts[1].trim();
                String academicLeaderEmail = parts[2].trim();
                String totalStudents = parts[3].trim();
                String overallAverage = parts[4].trim();
                String generatedDate = parts.length > 5 ? parts[5].trim() : "";

                Report r = new Report(reportId, moduleCode, academicLeaderEmail, totalStudents, overallAverage);
                r.setGeneratedDate(generatedDate);
                reports.add(r);
            }
            System.out.println("Reports loaded successfully!");
        } catch (IOException e) {
            System.out.println("Error loading reports: " + e.getMessage());
        }
    }
    
    /**
     * Load a full report with details for a specific module and student
     */
    public static Report loadReportWithDetailsForStudent(String studentEmail, String moduleCode) {
        File file = new File(REPORT_DETAIL_FILE);
        if (!file.exists()) return null;
        
        GradingSystemManager.loadFromFile();
        GradingSystem gradingSystem = GradingSystemManager.getGradingSystem();
        
        Report report = null;
        Map<String, StudentReportData> studentMap = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 14) {
                    String lineModuleCode = parts[1].trim();
                    String lineStudentEmail = parts[4].trim();
                    
                    if (lineModuleCode.equalsIgnoreCase(moduleCode) && 
                        lineStudentEmail.equalsIgnoreCase(studentEmail)) {
                        
                        if (report == null) {
                            report = new Report(parts[0].trim(), moduleCode, "");
                        }
                        
                        String studentName = parts[5].trim();
                        String assessmentId = parts[6].trim();
                        String assessmentName = parts[7].trim();
                        String assessmentType = parts[8].trim();
                        double marks = Double.parseDouble(parts[9].trim());
                        double maxMarks = Double.parseDouble(parts[10].trim());
                        double gpa = Double.parseDouble(parts[11].trim());
                        String grade = parts[12].trim();
                        double cgpa = Double.parseDouble(parts[13].trim());
                        
                        StudentReportData studentData = studentMap.computeIfAbsent(studentEmail, 
                            k -> new StudentReportData(studentEmail, studentName));
                        
                        AssessmentMark mark = new AssessmentMark(assessmentId, assessmentName, 
                            assessmentType, marks, maxMarks, gpa, grade);
                        studentData.addAssessmentMark(mark);
                        studentData.setCgpa(cgpa);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading report details: " + e.getMessage());
            return null;
        }
        
        if (report != null && !studentMap.isEmpty()) {
            ClassReportData classData = new ClassReportData("", "", "");
            for (StudentReportData student : studentMap.values()) {
                classData.addStudentReport(student);
            }
            report.addClassReport(classData);
        }
        
        return report;
    }
    
    /**
     * Get list of modules that have reports for a student
     */
    public static ArrayList<String> getModulesWithReportsForStudent(String studentEmail) {
        ArrayList<String> modules = new ArrayList<>();
        File file = new File(REPORT_DETAIL_FILE);
        if (!file.exists()) return modules;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String moduleCode = parts[1].trim();
                    String lineStudentEmail = parts[4].trim();
                    if (lineStudentEmail.equalsIgnoreCase(studentEmail) && !modules.contains(moduleCode)) {
                        modules.add(moduleCode);
                    }
                }
            }
        } catch (IOException e) {}
        return modules;
    }
    
    public static boolean reportExistsForModule(String moduleCode) {
        File file = new File(REPORT_FILE);
        if (!file.exists()) return false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    if (parts[1].trim().equalsIgnoreCase(moduleCode)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {}
        return false;
    }
    
    public static Report findReportByID(String reportID) {
        for (Report r : reports) {
            if (r.getReportId().equalsIgnoreCase(reportID)) {
                return r;
            }
        }
        return null;
    }
    
    public static boolean deleteReport(Report report) {
        if (report == null) {
            System.out.println("Report not found.");
            return false;
        }
        removeReportByModuleCode(report.getModuleCode());
        return true;
    }
    
    public static void loadClassGroups() {
        ClassManager.loadFromFile();
    }
    
    // Legacy methods for backward compatibility
    public static double getClassAverage(ClassGroup cg) {
        double totalPercentage = 0;
        int count = 0;
        Module module = cg.getModule();
        if (module == null) return 0;
        
        Map<String, String[]> assessmentData = loadAssessmentData();
        
        for (String studentEmail : cg.getStudentEmails()) {
            for (Map.Entry<String, String[]> entry : assessmentData.entrySet()) {
                String assessmentId = entry.getKey();
                String[] assessInfo = entry.getValue();
                if (!assessInfo[0].equalsIgnoreCase(module.getCode())) continue;
                
                double maxMarks = Double.parseDouble(assessInfo[4]);
                Double marks = findStudentMark(studentEmail, assessmentId);
                if (marks != null) {
                    double percentage = (marks / maxMarks) * 100.0;
                    totalPercentage += percentage;
                    count++;
                }
            }
        }
        return count > 0 ? totalPercentage / count : 0;
    }
    
    public static boolean generateModuleReport(Module module) {
        ArrayList<ClassGroup> relatedClasses = new ArrayList<>();
        for (ClassGroup cg : ClassManager.classGroups) {
            if (cg.getModule() != null && cg.getModule().getCode().equalsIgnoreCase(module.getCode())) {
                relatedClasses.add(cg);
            }
        }
        
        if (relatedClasses.isEmpty()) {
            System.out.println("No classes available for module " + module.getCode());
            return false;
        }
        return true;
    }
    
    public static Report generateReportObject(Module module, String leaderEmail) {
        return generateComprehensiveReport(module, leaderEmail);
    }
    
    public static void addReport(Report r, String currentLeaderEmail) {
        saveReport(r);
    }
}
