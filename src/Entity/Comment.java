package Entity;

public class Comment {

    private String studentName;
    private String studentEmail;
    private String role;
    private String lecturerName;
    private String lecturerEmail;
    private String academicLeaderEmail;
    private String content; // 

    public Comment(String studentName, String studentEmail, String role,
                   String lecturerName, String lecturerEmail,
                   String academicLeaderEmail, String content) {

        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.role = role;
        this.lecturerName = lecturerName;
        this.lecturerEmail = lecturerEmail;
        this.academicLeaderEmail = academicLeaderEmail;
        this.content = content;
    }

    // Getters
    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getRole() { return role; }
    public String getLecturerName() { return lecturerName; }
    public String getLecturerEmail() { return lecturerEmail; }
    public String getAcademicLeaderEmail() { return academicLeaderEmail; }
    public String getContent() { return content; }

    // Setters
    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public void setLecturerEmail(String lecturerEmail) {
        this.lecturerEmail = lecturerEmail;
    }

    public void setAcademicLeaderEmail(String academicLeaderEmail) {
        this.academicLeaderEmail = academicLeaderEmail;
    }
}
