package Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive Module Report containing:
 * - All lecturers' classes under the module
 * - All students under each class with their assessment marks and grades
 * - Average marks per class and overall module average
 */
public class Report {
    private String reportId;
    private String moduleCode;
    private String moduleName;
    private String academicLeaderEmail;
    private int totalStudents;
    private double moduleAverage; // Overall module average percentage
    private String generatedDate;
    
    // Detailed data structures
    private ArrayList<ClassReportData> classReports; // Data for each class
    
    public Report(String reportId, String moduleCode, String academicLeaderEmail) {
        this.reportId = reportId;
        this.moduleCode = moduleCode;
        this.academicLeaderEmail = academicLeaderEmail;
        this.classReports = new ArrayList<>();
        this.totalStudents = 0;
        this.moduleAverage = 0.0;
        this.generatedDate = java.time.LocalDate.now().toString();
    }
    
    // Legacy constructor for backward compatibility
    public Report(String reportId, String moduleCode, String academicLeaderEmail, 
                  String totalStudents, String overallAverage) {
        this.reportId = reportId;
        this.moduleCode = moduleCode;
        this.academicLeaderEmail = academicLeaderEmail;
        try {
            this.totalStudents = Integer.parseInt(totalStudents);
        } catch (NumberFormatException e) {
            this.totalStudents = 0;
        }
        try {
            this.moduleAverage = Double.parseDouble(overallAverage);
        } catch (NumberFormatException e) {
            this.moduleAverage = 0.0;
        }
        this.classReports = new ArrayList<>();
        this.generatedDate = java.time.LocalDate.now().toString();
    }

    // Getters
    public String getReportId() { return reportId; }
    public String getModuleCode() { return moduleCode; }
    public String getModuleName() { return moduleName; }
    public String getAcademicLeaderEmail() { return academicLeaderEmail; }
    public int getTotalStudentsCount() { return totalStudents; }
    public String getTotalStudents() { return String.valueOf(totalStudents); }
    public double getModuleAverageValue() { return moduleAverage; }
    public String getOverallAverage() { return String.format("%.2f", moduleAverage); }
    public String getGeneratedDate() { return generatedDate; }
    public ArrayList<ClassReportData> getClassReports() { return classReports; }

    // Setters
    public void setReportId(String reportId) { this.reportId = reportId; }
    public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public void setAcademicLeaderEmail(String academicLeaderEmail) { this.academicLeaderEmail = academicLeaderEmail; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    public void setTotalStudents(String totalStudents) {
        try { this.totalStudents = Integer.parseInt(totalStudents); } 
        catch (NumberFormatException e) { this.totalStudents = 0; }
    }
    public void setModuleAverage(double moduleAverage) { this.moduleAverage = moduleAverage; }
    public void setOverallAverage(String overallAverage) {
        try { this.moduleAverage = Double.parseDouble(overallAverage); }
        catch (NumberFormatException e) { this.moduleAverage = 0.0; }
    }
    public void setGeneratedDate(String generatedDate) { this.generatedDate = generatedDate; }
    
    public void addClassReport(ClassReportData classReport) {
        this.classReports.add(classReport);
    }
    
    // Inner class for class-level report data
    public static class ClassReportData {
        private String classCode;
        private String lecturerEmail;
        private String lecturerName;
        private double classAverage;
        private ArrayList<StudentReportData> studentReports;
        
        public ClassReportData(String classCode, String lecturerEmail, String lecturerName) {
            this.classCode = classCode;
            this.lecturerEmail = lecturerEmail;
            this.lecturerName = lecturerName;
            this.studentReports = new ArrayList<>();
            this.classAverage = 0.0;
        }
        
        public String getClassCode() { return classCode; }
        public String getLecturerEmail() { return lecturerEmail; }
        public String getLecturerName() { return lecturerName; }
        public double getClassAverage() { return classAverage; }
        public ArrayList<StudentReportData> getStudentReports() { return studentReports; }
        
        public void setClassAverage(double classAverage) { this.classAverage = classAverage; }
        
        public void addStudentReport(StudentReportData studentReport) {
            this.studentReports.add(studentReport);
        }
    }
    
    // Inner class for student-level report data
    public static class StudentReportData {
        private String studentEmail;
        private String studentName;
        private ArrayList<AssessmentMark> assessmentMarks;
        private double averagePercentage; // Average percentage for this module
        
        public StudentReportData(String studentEmail, String studentName) {
            this.studentEmail = studentEmail;
            this.studentName = studentName;
            this.assessmentMarks = new ArrayList<>();
            this.averagePercentage = 0.0;
        }
        
        public String getStudentEmail() { return studentEmail; }
        public String getStudentName() { return studentName; }
        public ArrayList<AssessmentMark> getAssessmentMarks() { return assessmentMarks; }
        public double getCgpa() { return averagePercentage; } // Kept for compatibility
        public double getAveragePercentage() { return averagePercentage; }
        
        public void setCgpa(double avgPercentage) { this.averagePercentage = avgPercentage; } // Kept for compatibility
        public void setAveragePercentage(double avgPercentage) { this.averagePercentage = avgPercentage; }
        
        public void addAssessmentMark(AssessmentMark mark) {
            this.assessmentMarks.add(mark);
        }
    }
    
    // Inner class for individual assessment marks
    public static class AssessmentMark {
        private String assessmentId;
        private String assessmentName;
        private String assessmentType;
        private double marks;
        private double maxMarks;
        private double percentage; // Percentage for this assessment
        private String grade;
        
        public AssessmentMark(String assessmentId, String assessmentName, String assessmentType,
                              double marks, double maxMarks, double percentage, String grade) {
            this.assessmentId = assessmentId;
            this.assessmentName = assessmentName;
            this.assessmentType = assessmentType;
            this.marks = marks;
            this.maxMarks = maxMarks;
            this.percentage = percentage;
            this.grade = grade;
        }
        
        public String getAssessmentId() { return assessmentId; }
        public String getAssessmentName() { return assessmentName; }
        public String getAssessmentType() { return assessmentType; }
        public double getMarks() { return marks; }
        public double getMaxMarks() { return maxMarks; }
        public double getGpa() { return percentage; } // Kept for compatibility - returns percentage
        public double getPercentage() { return percentage; }
        public String getGrade() { return grade; }
    }
}

