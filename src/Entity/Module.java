package Entity;

import java.util.ArrayList;

public class Module {

    private String code;
    private String name;

    // ✅ MULTIPLE lecturers
    private ArrayList<String> lecturerEmails;

    // ✅ STUDENT enrollment
    private ArrayList<String> studentEmails;

    // existing relationship (KEEP)
    private ArrayList<ClassGroup> classGroups = new ArrayList<>();

    private String academicLeaderEmail;

    // ================= CONSTRUCTORS =================

    // constructor used when loading from file
    public Module(String code, String name, String academicLeaderEmail, ArrayList<String> lecturerEmails) {
        this.code = code;
        this.name = name;
        this.academicLeaderEmail = academicLeaderEmail;

        // safety: prevent null list
        if (lecturerEmails == null) {
            this.lecturerEmails = new ArrayList<>();
        } else {
            this.lecturerEmails = lecturerEmails;
        }
        
        this.studentEmails = new ArrayList<>();
    }

    // constructor used when creating new module (NO lecturers yet)
    public Module(String code, String name, String academicLeaderEmail) {
        this.code = code;
        this.name = name;
        this.academicLeaderEmail = academicLeaderEmail;
        this.lecturerEmails = new ArrayList<>();
        this.studentEmails = new ArrayList<>();
    }

    // ================= GETTERS =================

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getAcademicLeaderEmail() {
        return academicLeaderEmail;
    }

    public ArrayList<String> getLecturerEmails() {
        return lecturerEmails;
    }

    public ArrayList<ClassGroup> getClassGroups() {
        return classGroups;
    }

    public ArrayList<String> getStudentEmails() {
        return studentEmails;
    }

    // ================= SETTERS =================

    public void setCode(String code) {
        if (code != null && !code.isEmpty()) {
            this.code = code;
        }
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }

    public void setAcademicLeaderEmail(String academicLeaderEmail) {
        if (academicLeaderEmail != null && !academicLeaderEmail.isEmpty()) {
            this.academicLeaderEmail = academicLeaderEmail;
        }
    }

    // ================= LECTURER METHODS =================

    public void addLecturer(String email) {
        if (email == null || email.isEmpty()) return;

        if (!lecturerEmails.contains(email)) {
            lecturerEmails.add(email);
        }
    }

    public void removeLecturer(String email) {
        lecturerEmails.remove(email);
    }

    // ================= STUDENT METHODS =================

    public void addStudent(String email) {
        if (email == null || email.isEmpty()) return;

        if (!studentEmails.contains(email)) {
            studentEmails.add(email);
        }
    }

    public void removeStudent(String email) {
        studentEmails.remove(email);
    }

    // ================= CLASS GROUP =================

    public void addClassGroup(ClassGroup cg) {
        if (cg == null) return;

        // prevent duplicate class group (by code)
        for (ClassGroup existing : classGroups) {
            if (existing.getClassCode().equalsIgnoreCase(cg.getClassCode())) {
                return;
            }
        }

        classGroups.add(cg);
    }
}
