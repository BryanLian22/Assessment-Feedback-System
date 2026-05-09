package GUI.dialogs;

import Users.Student;
import IOManage.UserManager;
import IOManage.ModuleManager;
import IOManage.ClassManager;
import IOManage.CommentManager;
import Entity.Module;
import Entity.ClassGroup;
import Entity.Comment;
import Users.Lecturer;
import Users.AcademicLeader;
import GUI.utils.GUIStyleUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StudentDialogs {
    
    public static void showEditProfileDialog(JFrame parent, Student student) {
        JDialog dialog = new JDialog(parent, "Edit Profile", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(student.getName(), 20);
        panel.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(student.getEmail(), 20);
        panel.add(emailField, gbc);

        // Current Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField currentPwField = new JPasswordField(20);
        panel.add(currentPwField, gbc);

        // New Password
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPwField = new JPasswordField(20);
        panel.add(newPwField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPwField = new JPasswordField(20);
        panel.add(confirmPwField, gbc);

        JButton saveButton = GUIStyleUtils.createPrimaryButton("Save", 100, 35);
        JButton cancelButton = GUIStyleUtils.createSecondaryButton("Cancel", 100, 35);

        saveButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String currentPw = new String(currentPwField.getPassword());
            String newPw = new String(newPwField.getPassword());
            String confirmPw = new String(confirmPwField.getPassword());

            boolean emailChanged = false;
            
            if (!newName.isEmpty() && !newName.equals(student.getName())) {
                student.setName(newName);
            }
            
            if (!newEmail.isEmpty() && !newEmail.equals(student.getEmail())) {
                String oldEmail = student.getEmail();
                
                if (IOManage.EmailUpdateManager.emailExistsInSystem(newEmail)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "✗ Error: Email " + newEmail + " already exists in the system.", 
                        "Email Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (IOManage.EmailUpdateManager.updateEmailAcrossSystem(oldEmail, newEmail)) {
                    emailChanged = true;
                    // ✅ UPDATE: Set the new email in the current student object
                    student.setEmail(newEmail);
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "✗ Failed to update email.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Update password
            if (!newPw.isEmpty()) {
                if (!IOManage.PasswordUtils.verifyPassword(currentPw, student.getPassword())) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Current password is incorrect!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!newPw.equals(confirmPw)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "New passwords do not match!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                student.setPassword(IOManage.PasswordUtils.hashPassword(newPw));
            }
            
            // Save changes
            if (!emailChanged) {
                // Only save to users.txt if email was NOT changed
                UserManager.updateUser(student);
                UserManager.saveToFile();
            } else {
                // Email was changed by EmailUpdateManager, just reload
                UserManager.loadFromFile();
            }
            
            JOptionPane.showMessageDialog(dialog, 
                "Profile updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void showRegisterClassDialog(JFrame parent, Student student) {
        ModuleManager.loadAllModules();
        ClassManager.loadFromFile();
        
        // Reload registered modules from file
        for (Module m : ModuleManager.modules) {
            if (m.getStudentEmails().contains(student.getEmail())) {
                if (!student.getRegisteredModules().contains(m.getCode())) {
                    student.getRegisteredModules().add(m.getCode());
                }
            }
        }

        if (student.getRegisteredModules().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "You must register modules before registering classes.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Register Class", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Enter Class Code:");
        JTextField classCodeField = new JTextField(15);
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(label);
        inputPanel.add(classCodeField);

        registerButton.addActionListener(e -> {
            String classCode = classCodeField.getText().trim().toUpperCase();
            
            if (classCode.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a class code!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (student.getRegisteredClasses().contains(classCode)) {
                JOptionPane.showMessageDialog(dialog, "You already registered this class.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ClassGroup cg = ClassManager.findByClassCode(classCode);
            if (cg == null) {
                JOptionPane.showMessageDialog(dialog, "Class does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String moduleCode = cg.getModule().getCode();
            if (!student.getRegisteredModules().contains(moduleCode)) {
                JOptionPane.showMessageDialog(dialog, "You have not registered the module for this class.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Module m = ModuleManager.findModuleByCode(moduleCode);
            if (m == null || !m.getStudentEmails().contains(student.getEmail())) {
                JOptionPane.showMessageDialog(dialog, "You are not registered under this module.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (cg.getStudentEmails().contains(student.getEmail())) {
                JOptionPane.showMessageDialog(dialog, "You are already registered in this class.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ✅ ENFORCE: Remove from other classes in same module first
            ClassManager.removeStudentFromModuleClasses(student.getEmail(), moduleCode);
            
            cg.addStudent(student.getEmail());
            ClassManager.updateClassGroup(cg);
            student.getRegisteredClasses().add(classCode);
            UserManager.saveToFile();
            JOptionPane.showMessageDialog(dialog, "Class registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void showViewTimetableDialog(JFrame parent, Student student) {
        ClassManager.loadFromFile();

        ArrayList<ClassGroup> studentClasses = new ArrayList<>();
        for (ClassGroup cg : ClassManager.classGroups) {
            if (cg.getStudentEmails().contains(student.getEmail())) {
                studentClasses.add(cg);
            }
        }

        JDialog dialog = new JDialog(parent, "My Timetable", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(parent);

        String[] columns = {"Class Code", "Time", "Classroom"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (ClassGroup cg : studentClasses) {
            String time = cg.getTime() != null && !cg.getTime().isEmpty() ? cg.getTime() : "N/A";
            String classroom = cg.getClassroom() != null && !cg.getClassroom().isEmpty() ? cg.getClassroom() : "N/A";
            model.addRow(new Object[]{cg.getClassCode(), time, classroom});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        if (studentClasses.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "You are not registered in any classes yet.", "No Classes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void showViewResultsDialog(JFrame parent, Student student) {
        JDialog dialog = new JDialog(parent, "My Results", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(parent);

        String[] columns = {"Assessment ID", "Assessment Name", "Type", "Lecturer", "Score", "Grade", "Feedback"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load marks from marks.txt with correct format
        File marksFile = new File("data/marks.txt");
        
        if (marksFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(marksFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    
                    // Format: assessmentID|assessmentName|assessmentType|lecturerEmail|studentEmail|studentName|marks|feedback
                    String[] parts = line.split("\\|");
                    
                    if (parts.length >= 7) {
                        String studentEmail = parts[4].trim(); // Student email is at index 4
                        
                        // Check if this mark belongs to the current student
                        if (studentEmail.equalsIgnoreCase(student.getEmail())) {
                            String assessmentId = parts[0].trim();
                            String assessmentName = parts[1].trim();
                            String type = parts[2].trim();
                            String lecturerEmail = parts[3].trim();
                            double marks = Double.parseDouble(parts[6].trim());
                            String feedback = parts.length > 7 ? parts[7].trim() : "";
                            
                            // Load assessment to get max marks
                            IOManage.UserManager.loadFromFile();
                            Users.User user = IOManage.UserManager.findByEmail(lecturerEmail);
                            double maxMarks = 100.0; // default
                            
                            if (user instanceof Users.Lecturer) {
                                Users.Lecturer lec = (Users.Lecturer) user;
                                IOManage.AssessmentManager.loadLecturerData(lec);
                                Users.Lecturer.Assessment assessment = lec.getAssessment(assessmentId);
                                if (assessment != null) {
                                    maxMarks = assessment.getMaxMarks();
                                }
                            }
                            
                            // Calculate percentage and grade
                            double percentage = (marks / maxMarks) * 100.0;
                            
                            // Load grading system for grade
                            IOManage.GradingSystemManager.loadFromFile();
                            Entity.GradingSystem gradingSystem = IOManage.GradingSystemManager.getGradingSystem();
                            String grade = gradingSystem.getGrade(percentage);
                            
                            String scoreDisplay = String.format("%.1f/%.0f (%.1f%%)", marks, maxMarks, percentage);
                            
                            model.addRow(new Object[]{
                                assessmentId,
                                assessmentName,
                                type,
                                lecturerEmail,
                                scoreDisplay,
                                grade,
                                feedback.isEmpty() ? "No feedback" : feedback
                            });
                        }
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Error loading marks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "No results available yet.", "No Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Set column widths for better display
        table.getColumnModel().getColumn(0).setPreferredWidth(80);   // Assessment ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Assessment Name
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Type
        table.getColumnModel().getColumn(3).setPreferredWidth(150);  // Lecturer
        table.getColumnModel().getColumn(4).setPreferredWidth(120);  // Score
        table.getColumnModel().getColumn(5).setPreferredWidth(60);   // Grade
        table.getColumnModel().getColumn(6).setPreferredWidth(200);  // Feedback
        
        JScrollPane scrollPane = new JScrollPane(table);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void showCommentLecturerDialog(JFrame parent, Student student) {
        CommentManager.loadFromFile();

        JDialog dialog = new JDialog(parent, "Comment Lecturer", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Lecturer Email:");
        JTextField lecEmailField = new JTextField(20);
        JLabel commentLabel = new JLabel("Your Comment:");
        JTextArea commentArea = new JTextArea(5, 30);
        commentArea.setLineWrap(true);
        JScrollPane commentScroll = new JScrollPane(commentArea);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(label, gbc);
        gbc.gridx = 1;
        inputPanel.add(lecEmailField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(commentLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(commentScroll, gbc);

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            String lecEmail = lecEmailField.getText().trim();
            String content = commentArea.getText().trim();

            if (lecEmail.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter lecturer email!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Lecturer lecturer = CommentManager.findLecturerByEmail(lecEmail);
            if (lecturer == null) {
                JOptionPane.showMessageDialog(dialog, "Lecturer not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AcademicLeader leader = CommentManager.findLeaderForLecturer(lecEmail);
            if (leader == null) {
                JOptionPane.showMessageDialog(dialog, "No Academic Leader found for lecturer " + lecEmail + ". Comment not saved.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Comment cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Comment comment = new Comment(student.getName(), student.getEmail(), "Student", lecturer.getName(), lecturer.getEmail(), leader.getEmail(), content);
            CommentManager.addComment(comment);
            JOptionPane.showMessageDialog(dialog, "Comment submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    public static void showViewAssessmentsDialog(JFrame parent, Student student) {
        JDialog dialog = new JDialog(parent, "My Assessments", true);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(parent);

        // Load necessary data
        ModuleManager.loadAllModules();
        IOManage.UserManager.loadFromFile();
        
        // Reload registered modules from file
        for (Module m : ModuleManager.modules) {
            if (m.getStudentEmails().contains(student.getEmail())) {
                if (!student.getRegisteredModules().contains(m.getCode())) {
                    student.getRegisteredModules().add(m.getCode());
                }
            }
        }

        if (student.getRegisteredModules().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "You are not registered in any modules.", "No Modules", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Collect all assessments from registered modules
        ArrayList<Object[]> allAssessments = new ArrayList<>();

        for (String moduleCode : student.getRegisteredModules()) {
            Module module = ModuleManager.findModuleByCode(moduleCode);
            if (module == null) continue;

            ArrayList<String> lecturerEmails = module.getLecturerEmails();
            if (lecturerEmails == null || lecturerEmails.isEmpty()) continue;

            for (String lecturerEmail : lecturerEmails) {
                Users.User user = IOManage.UserManager.findByEmail(lecturerEmail);
                if (!(user instanceof Users.Lecturer)) continue;

                Users.Lecturer lecturer = (Users.Lecturer) user;
                IOManage.AssessmentManager.loadLecturerData(lecturer);

                java.util.List<Users.Lecturer.Assessment> moduleAssessments = 
                    lecturer.getAssessmentsByModule(moduleCode);

                for (Users.Lecturer.Assessment assessment : moduleAssessments) {
                    // Check if student has completed this assessment
                    boolean completed = false;
                    double studentMarks = 0.0;
                    String feedback = "";

                    java.util.List<Users.Lecturer.StudentMark> marks = 
                        lecturer.getMarksForAssessment(assessment.getAssessmentId());

                    for (Users.Lecturer.StudentMark mark : marks) {
                        if (mark.getStudentId().equalsIgnoreCase(student.getEmail())) {
                            completed = true;
                            studentMarks = mark.getMarks();
                            feedback = mark.getFeedback();
                            break;
                        }
                    }

                    String status;
                    if (completed) {
                        double percentage = (studentMarks / assessment.getMaxMarks()) * 100.0;
                        
                        // Load grading system for grade calculation
                        IOManage.GradingSystemManager.loadFromFile();
                        Entity.GradingSystem gradingSystem = IOManage.GradingSystemManager.getGradingSystem();
                        String grade = gradingSystem.getGrade(percentage);
                        
                        status = String.format("%.1f/%.0f (%.1f%%) [%s]", 
                            studentMarks, assessment.getMaxMarks(), percentage, grade);
                    } else {
                        status = "Pending";
                    }

                    // Updated: removed checkmark, added description
                    allAssessments.add(new Object[]{
                        assessment.getAssessmentId(),
                        assessment.getAssessmentName(),
                        moduleCode,
                        assessment.getType().getDisplayName(),
                        assessment.getMaxMarks(),
                        assessment.getDescription() != null ? assessment.getDescription() : "",
                        status,
                        completed  // Keep for sorting and highlighting
                    });
                }
            }
        }

        if (allAssessments.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No assessments found for your registered modules.", "No Assessments", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Sort: incomplete first, then completed
        allAssessments.sort((a1, a2) -> {
            boolean comp1 = (boolean) a1[7];
            boolean comp2 = (boolean) a2[7];
            if (comp1 == comp2) {
                return ((String) a1[0]).compareTo((String) a2[0]);
            }
            return comp1 ? 1 : -1;
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("My Assessments");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table - updated columns without checkmark, added Description
        String[] columns = {"ID", "Assessment Name", "Module", "Type", "Max Marks", "Description", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        for (Object[] row : allAssessments) {
            // Display 7 columns (exclude the completed flag at index 7)
            Object[] displayRow = new Object[7];
            System.arraycopy(row, 0, displayRow, 0, 7);
            model.addRow(displayRow);
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Set column widths - updated for new columns
        table.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(180);  // Assessment Name
        table.getColumnModel().getColumn(2).setPreferredWidth(80);   // Module
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Type
        table.getColumnModel().getColumn(4).setPreferredWidth(80);   // Max Marks
        table.getColumnModel().getColumn(5).setPreferredWidth(200);  // Description
        table.getColumnModel().getColumn(6).setPreferredWidth(180);  // Status
        
        // Highlight completed rows in green - updated to check Status column (index 6)
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) table.getValueAt(row, 6);  // Status is now at index 6
                if (!status.equals("Pending")) {
                    if (!isSelected) {
                        c.setBackground(new Color(200, 255, 200));
                    }
                } else {
                    if (!isSelected) {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Summary
        long completedCount = allAssessments.stream().filter(a -> (boolean) a[7]).count();
        long pendingCount = allAssessments.size() - completedCount;
        JLabel summaryLabel = new JLabel(String.format(
            "Total: %d assessments | Completed: %d | Pending: %d", 
            allAssessments.size(), completedCount, pendingCount));
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.add(summaryLabel);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}

