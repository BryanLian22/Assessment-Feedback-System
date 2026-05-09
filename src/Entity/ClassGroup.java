package Entity;

import Users.Student;
import java.util.ArrayList;

public class ClassGroup {

    private String classCode;     // e.g., DCS2205-01
    private Module module;        // e.g., DCS2205
    private String time;          // e.g., "Tuesday 12pm-2pm"
    private String classroom;     // e.g., "Audi1"

    private ArrayList<String> studentEmails = new ArrayList<>();
    private ArrayList<String> lecturerEmails = new ArrayList<>();

    public ArrayList<String> getStudentEmails() {
        return studentEmails;
    }

    public void addStudent(String email) {
        if (!studentEmails.contains(email)) {
            studentEmails.add(email);
        }
    }

    public ArrayList<String> getLecturerEmails() {
        return lecturerEmails;
    }

    // Allow only one lecturer per class. If a lecturer is already assigned, this is a no-op.
    public void addLecturer(String email) {
        if (email == null || email.isEmpty()) return;
        // If no lecturer yet, assign. Otherwise ignore to enforce single-lecturer constraint.
        if (lecturerEmails.isEmpty()) {
            lecturerEmails.add(email);
        }
    }
    
    // Helper: return assigned lecturer email or null if none
    public String getAssignedLecturer() {
        return lecturerEmails.isEmpty() ? null : lecturerEmails.get(0);
    }
    
    public void removeLecturer(String email) {
        lecturerEmails.remove(email);
    }

    // Constructor without time/classroom
    public ClassGroup(String classCode, Module module) {
        this.classCode = classCode;
        this.module = module;
        this.time = "";
        this.classroom = "";
    }

    // Constructor with time/classroom
    public ClassGroup(String classCode, Module module, String time, String classroom) {
        this.classCode = classCode;
        this.module = module;
        this.time = time;
        this.classroom = classroom;
    }

    // ================= SETTERS =================
    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    // ================= GETTERS =================
    public String getClassCode() {
        return classCode;
    }

    public Module getModule() {
        return module;
    }

    public String getTime() {
        return time;
    }

    public String getClassroom() {
        return classroom;
    }

    public double getAverageScore(ArrayList<Assessment> allAssessments) {
        int totalScore = 0;
        int count = 0;

        for (Assessment a : allAssessments) {
            // Only consider students in this class
            for (String email : studentEmails) {
                for (Student s : a.getMarks().keySet()) {
                    if (s.getEmail().equalsIgnoreCase(email)) {
                        totalScore += a.getMarks().get(s);
                        count++;
                    }
                }
            }
        }

        return count > 0 ? (double) totalScore / count : 0;
    }

}
