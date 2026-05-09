package IOManage;

import Entity.Comment;
import Entity.Module;
import Users.Student;
import Users.Lecturer;
import Users.AcademicLeader;
import Users.User;
import IOManage.ModuleManager;
import IOManage.UserManager;

import java.io.*;
import java.util.ArrayList;

public class CommentManager {

    private static ArrayList<Comment> comments = new ArrayList<>();

    // ============================
    // FIND LECTURER BY EMAIL
    // ============================
    public static Lecturer findLecturerByEmail(String email) {
        for (User u : UserManager.users) {
            if (u instanceof Lecturer l &&
                l.getEmail().equalsIgnoreCase(email)) {
                return l;
            }
        }
        return null;
    }

    // ============================
    // FIND ACADEMIC LEADER
    // ============================
    public static AcademicLeader findLeaderForLecturer(String lecturerEmail) {
    // 1. Load all modules so we know who manages which lecturer
    UserManager.loadFromFile();
    ModuleManager.loadAllModules();

    // 2. Find any module where this lecturer is assigned
    for (Module m : ModuleManager.modules) {
        if (m.getLecturerEmails() != null &&
                m.getLecturerEmails().contains(lecturerEmail)) {

            String leaderEmail = m.getAcademicLeaderEmail();

            // 3. Convert that email into an AcademicLeader object
            User u = UserManager.findByEmail(leaderEmail);
            if (u instanceof AcademicLeader al) {
                return al;
            }
        }
    }
    
    // ---------- 2. Fallback: use user mapping (users.txt) ----------
    for (User u : UserManager.users) {
        if (u instanceof AcademicLeader al &&
                al.getLecturerEmails().contains(lecturerEmail)) {
            return al;
        }
    }
    
    // 4. No matching leader found
    return null;
    }


    // ============================
    // LOAD FROM FILE
    // ============================
    public static void loadFromFile() {

        comments.clear();
        File file = new File("data/comments.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] p = line.split(",", 7);
                if (p.length < 7) continue;

                comments.add(new Comment(
                        p[0], // studentName
                        p[1], // studentEmail
                        p[2], // role
                        p[3], // lecturerName
                        p[4], // lecturerEmail
                        p[5], // leaderEmail
                        p[6]  // content
                ));
            }

        } catch (IOException e) {
            System.out.println("Error loading comments: " + e.getMessage());
        }
    }

    // ============================
    // ADD COMMENT
    // ============================
    public static void addComment(Comment c) {
        if (c == null) return;
        comments.add(c);
        saveToFile();
    }

    // ============================
    // SAVE TO FILE
    // ============================
    private static void saveToFile() {

        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, "comments.txt");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {

            for (Comment c : comments) {
                pw.println(
                        c.getStudentName() + "," +
                        c.getStudentEmail() + "," +
                        c.getRole() + "," +
                        c.getLecturerName() + "," +
                        c.getLecturerEmail() + "," +
                        c.getAcademicLeaderEmail() + "," +
                        c.getContent().replace(",", ";")
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving comments: " + e.getMessage());
        }
    }

    // ============================
    // GET COMMENTS FOR LECTURER
    // ============================
    public static ArrayList<Comment> getCommentsForLecturer(String lecturerEmail) {
        ArrayList<Comment> list = new ArrayList<>();
        for (Comment c : comments) {
            if (c.getLecturerEmail().equalsIgnoreCase(lecturerEmail)) {
                list.add(c);
            }
        }
        return list;
    }

    public static ArrayList<Comment> getAllComments() {
        return comments;
    }

    public static ArrayList<Comment> filterCommentsByAcademicLeader(String leaderEmail) {
        ArrayList<Comment> result = new ArrayList<>();

        for (Comment c : comments) {
            if (c.getAcademicLeaderEmail().equalsIgnoreCase(leaderEmail)) {
                result.add(c);
            }
        }

        return result;
    }
}
