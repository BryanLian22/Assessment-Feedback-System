package App;

import Users.AcademicLeader;
import Users.AdminStaff;
import Users.Lecturer;
import Users.Student;
import java.util.Scanner;

public class Menu {
    
    private static final Scanner scan = new Scanner(System.in);

    public static void adminMenu(AdminStaff admin) {
        while (true) {
            System.out.println("===========\nMENU\n===========");
            System.out.println("1. Edit Profile");
            System.out.println("2. Manage Users");
            System.out.println("3. Assign Lecturers");
            System.out.println("4. Update Grading System");
            System.out.println("5. Manage Classes");
            System.out.println("6. Manage Student Modules");
            System.out.println("7. Logout");
            System.out.println("Enter option's number: ");
            String option = scan.nextLine();
            switch (option) {
                case "1" -> {
                    admin.editProfile();
                }
                case "2" -> {
                    admin.manageUsers();
                }
                case "3" -> {
                    admin.assignLecturers();
                }
                case "4" -> {
                    admin.defineGradingSystem();
                }
                case "5" -> {
                    admin.manageClasses();
                }
                case "6" -> {
                    admin.manageStudentModules();
                }
                case "7" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> {
                    System.out.println("Invalid option");
                }
            }
        }
        
    }

    public static void lecturerMenu(Lecturer L) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("       LECTURER MENU");
            System.out.println("========================================");
            
            // Display assigned leader information directly in the menu
            System.out.println("Assigned Academic Leaders:");
            System.out.println(L.getAssignedLeaderInfo());
            
            System.out.println("\n========================================");
            System.out.println("1. Profile");
            System.out.println("2. Assessments");
            System.out.println("3. Marks");
            System.out.println("4. Feedback");
            System.out.println("5. View Schedule");
            System.out.println("0. Logout");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");
            String option = scan.nextLine();    
            switch (option) {
                case "1" -> {
                    lecturerProfileMenu(L);
                }
                case "2" -> {
                    lecturerAssessmentMenu(L);
                }
                case "3" -> {
                    lecturerMarksMenu(L);
                }
                case "4" -> {
                    lecturerFeedbackMenu(L);
                }
                case "5" -> {
                    L.viewSchedule();
                }
                case "0" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> {
                    System.out.println("Invalid option");
                }
            }
        }
    }
    
    private static void lecturerProfileMenu(Lecturer L) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("           PROFILE MENU");
            System.out.println("========================================");
            System.out.println("1. Edit Personal Profile");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");
            String option = scan.nextLine();
            switch (option) {
                case "1" -> {
                    L.editProfileMenu();
                }
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid option");
                }
            }
        }
    }
    
    private static void lecturerAssessmentMenu(Lecturer L) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("        ASSESSMENTS MENU");
            System.out.println("========================================");
            System.out.println("1. Design Module Assessment Types");
            System.out.println("2. View Assessments");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");
            String option = scan.nextLine();
            switch (option) {
                case "1" -> {
                    L.designAssessmentMenu();
                }
                case "2" -> {
                    L.viewAssessmentsMenu();
                }
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid option");
                }
            }
        }
    }
    
    private static void lecturerMarksMenu(Lecturer L) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("            MARKS MENU");
            System.out.println("========================================");
            System.out.println("1. Key-in Assessment Marks");
            System.out.println("2. View Student Marks");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");
            String option = scan.nextLine();
            switch (option) {
                case "1" -> {
                    L.enterMarksMenu();
                }
                case "2" -> {
                    L.viewMarksMenu();
                }
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid option");
                }
            }
        }
    }
    
    private static void lecturerFeedbackMenu(Lecturer L) {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("          FEEDBACK MENU");
            System.out.println("========================================");
            System.out.println("1. Provide Feedback");
            System.out.println("2. View Feedback");
            System.out.println("0. Back to Main Menu");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");
            String option = scan.nextLine();
            switch (option) {
                case "1" -> {
                    L.provideFeedbackMenu();
                }
                case "2" -> {
                    L.viewFeedbackMenu();
                }
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid option");
                }
            }
        }
    }

    public static void studentMenu(Student student) {
        while (true) {
            System.out.println("\n===========================");
            System.out.println("        STUDENT MENU");
            System.out.println("===========================");
            System.out.println("======================================");
            System.out.println(" APU ASSESSMENT FEEDBACK SYSTEM (AFS)");
            System.out.println("======================================");
            System.out.println("1. Edit Profile");
            System.out.println("2. Register Class");
            System.out.println("3. View Timetable");
            System.out.println("4. View Results");
            System.out.println("5. Comment Lecturer");
            System.out.println("6. View Assessments");
            System.out.println("7. Logout");
            System.out.print("Enter your option: ");
            String option = scan.nextLine();
            switch (option) {
                case "1" -> student.editProfile();
                case "2" -> student.registerClass();
                case "3" -> student.viewTimetable();
                case "4" -> student.viewResults();
                case "5" -> student.commentLecturer();
                case "6" -> student.viewAssessments();
                case "7" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
        
            }
        }              
    }

    public static void academicLeaderMenu(AcademicLeader a) {//Function that handle the Menu of Academic Leader
        while (true) {
            System.out.println("===========\nMENU\n===========");
            System.out.println("1. Edit Profile");
            System.out.println("2. Manage Module");
            System.out.println("3. Analyse Report");
            System.out.println("4. View Comment");
            System.out.println("5. Register Lecturer to Class");
            System.out.println("0. Logout");
            System.out.print("Enter option's number: ");
            String option = scan.nextLine();
            switch (option) {
                case "1" -> {
                    a.editProfile(); //lead to Users/AcademicLeader.editProfile()
                }
                case "2" -> {
                    a.manageModules();//lead to Users/AcademicLeader.manageModules()
                }
                case "3" -> {
                    a.analyzeReports();//lead to Users/AcademicLeader.analyzeReports()
                }
                case "4" -> {
                    a.viewComment();//lead to Users/AcademicLeader.viewComment()
                }
                case "5" -> {
                    a.registerLecturerToClassMenu();//lead to Users/AcademicLeader.registerLecturerToClassMenu()
                }
                case "0" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> {
                    System.out.println("Invalid option");
                }
            }
        }
    }
}

