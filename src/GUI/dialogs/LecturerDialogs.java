package GUI.dialogs;

import Users.Lecturer;
import Users.Lecturer.Assessment;
import Users.Lecturer.StudentMark;
import Users.Lecturer.AssessmentType;
import IOManage.ModuleManager;
import IOManage.ClassManager;
import IOManage.AssessmentManager;
import Entity.Module;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LecturerDialogs {
    
    public static void showEditProfileDialog(JFrame parent, Lecturer lecturer) {
        JDialog dialog = new JDialog(parent, "Edit Profile", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(lecturer.getName(), 20);
        panel.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(lecturer.getEmail(), 20);
        panel.add(emailField, gbc);

        // Password change controls
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField currentPwField = new JPasswordField(20);
        panel.add(currentPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPwField = new JPasswordField(20);
        panel.add(newPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPwField = new JPasswordField(20);
        panel.add(confirmPwField, gbc);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String currentPw = new String(currentPwField.getPassword());
            String newPw = new String(newPwField.getPassword());
            String confirmPw = new String(confirmPwField.getPassword());

            boolean emailChanged = false;
            
            if (!name.isEmpty() && !name.equals(lecturer.getName())) {
                lecturer.setName(name);
            }
            
            if (!email.isEmpty() && !email.equals(lecturer.getEmail())) {
                String oldEmail = lecturer.getEmail();
                
                if (IOManage.EmailUpdateManager.emailExistsInSystem(email)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "✗ Error: Email " + email + " already exists in the system.", 
                        "Email Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (IOManage.EmailUpdateManager.updateEmailAcrossSystem(oldEmail, email)) {
                    emailChanged = true;
                    // ✅ UPDATE: Set the new email in the current lecturer object
                    lecturer.setEmail(email);
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
                if (!IOManage.PasswordUtils.verifyPassword(currentPw, lecturer.getPassword())) {
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
                lecturer.setPassword(IOManage.PasswordUtils.hashPassword(newPw));
            }

            // Save changes
            if (!emailChanged) {
                IOManage.UserManager.updateUser(lecturer);
                IOManage.UserManager.saveToFile();
            } else {
                IOManage.UserManager.loadFromFile();
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

    public static void showDesignAssessmentDialog(JFrame parent, Lecturer lecturer) {
        JDialog dialog = new JDialog(parent, "Design Assessment", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Get next assessment ID
        String nextAssessmentId = getNextAssessmentId();
        
        // Assessment ID
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Assessment ID:"), gbc);
        gbc.gridx = 1;
        JTextField idField = new JTextField(nextAssessmentId, 20);
        idField.setEditable(false); // Auto-generated, not editable
        panel.add(idField, gbc);

        // Load modules assigned to this lecturer
        ModuleManager.loadAllModules();
        java.util.List<Module> assignedModules = new java.util.ArrayList<>();
        for (Module m : ModuleManager.modules) {
            if (m.getLecturerEmails() != null && m.getLecturerEmails().contains(lecturer.getEmail())) {
                assignedModules.add(m);
            }
        }

        // Module Code
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Module Code:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> moduleCombo = new JComboBox<>();
        for (Module m : assignedModules) {
            moduleCombo.addItem(m.getCode() + " - " + m.getName());
        }
        moduleCombo.setPreferredSize(new Dimension(300, 25));
        panel.add(moduleCombo, gbc);

        // Assessment Name
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Assessment Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // Assessment Type
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Assessment Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<AssessmentType> typeCombo = new JComboBox<>(AssessmentType.values());
        panel.add(typeCombo, gbc);

        // Max Marks
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Maximum Marks:"), gbc);
        gbc.gridx = 1;
        JTextField maxMarksField = new JTextField("100", 20);
        panel.add(maxMarksField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);

        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.addActionListener(e -> {
            String assessmentId = idField.getText().trim();
            String selected = (String) moduleCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a module!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String moduleCode = selected.split(" - ")[0]; // Extract module code from dropdown
            String assessmentName = nameField.getText().trim();
            AssessmentType type = (AssessmentType) typeCombo.getSelectedItem();
            double maxMarks;
            try {
                maxMarks = Double.parseDouble(maxMarksField.getText().trim());
                if (maxMarks <= 1) {
                    JOptionPane.showMessageDialog(dialog, "Maximum marks must be greater than 1!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid maximum marks value!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String description = descArea.getText().trim();

            if (assessmentId.isEmpty() || moduleCode.isEmpty() || assessmentName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                lecturer.designAssessment(assessmentId, moduleCode, assessmentName, type, maxMarks, description);
                JOptionPane.showMessageDialog(dialog, "Assessment created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void showViewAssessmentsDialog(JFrame parent, Lecturer lecturer) {
        List<Assessment> assessments = lecturer.getAllAssessments();
        
        JDialog dialog = new JDialog(parent, "View Assessments", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parent);

        String[] columns = {"Assessment ID", "Module Code", "Name", "Type", "Max Marks", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Assessment a : assessments) {
            model.addRow(new Object[]{
                a.getAssessmentId(),
                a.getModuleCode(),
                a.getAssessmentName(),
                a.getType().getDisplayName(),
                a.getMaxMarks(),
                a.getDescription()
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton closeButton = new JButton("Close");

        updateButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select an assessment to update!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String assessmentId = (String) model.getValueAt(row, 0);
            showUpdateAssessmentDialog(dialog, lecturer, assessmentId);
            dialog.dispose();
            showViewAssessmentsDialog(parent, lecturer);
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select an assessment to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String assessmentId = (String) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete this assessment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (lecturer.deleteAssessment(assessmentId)) {
                    JOptionPane.showMessageDialog(dialog, "Assessment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    showViewAssessmentsDialog(parent, lecturer);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Assessment not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void showUpdateAssessmentDialog(JDialog parent, Lecturer lecturer, String assessmentId) {
        Assessment assessment = lecturer.getAssessment(assessmentId);
        if (assessment == null) {
            JOptionPane.showMessageDialog(parent, "Assessment not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Update Assessment", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Assessment Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(assessment.getAssessmentName(), 20);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Maximum Marks:"), gbc);
        gbc.gridx = 1;
        JTextField maxMarksField = new JTextField(String.valueOf(assessment.getMaxMarks()), 20);
        panel.add(maxMarksField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(assessment.getDescription(), 3, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            double maxMarks;
            try {
                maxMarks = Double.parseDouble(maxMarksField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid maximum marks value!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String description = descArea.getText().trim();

            lecturer.updateAssessment(assessmentId, name, maxMarks, description);
            JOptionPane.showMessageDialog(dialog, "Assessment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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

    public static void showEnterMarksDialog(JFrame parent, Lecturer lecturer) {
        List<Assessment> assessments = lecturer.getAllAssessments();
        if (assessments.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No assessments available. Please create assessments first.", "No Assessments", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Enter Marks", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Assessment Selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Assessment ID:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> assessmentCombo = new JComboBox<>();
        for (Assessment a : assessments) {
            assessmentCombo.addItem(a.getAssessmentId() + " - " + a.getAssessmentName());
        }
        assessmentCombo.setPreferredSize(new Dimension(300, 25));
        panel.add(assessmentCombo, gbc);

        // Student Email & Name dropdown
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Student Email & Name:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> studentCombo = new JComboBox<>();
        studentCombo.setPreferredSize(new Dimension(300, 25));
        panel.add(studentCombo, gbc);

        // Function to update student dropdown based on selected assessment
        java.util.function.Consumer<String> updateStudentCombo = (String selectedAssessment) -> {
            studentCombo.removeAllItems();
            if (selectedAssessment == null || selectedAssessment.isEmpty()) {
                return;
            }
            
            // Extract assessment ID and find the assessment
            String assessmentId = selectedAssessment.split(" - ")[0];
            Assessment selectedAssess = null;
            for (Assessment a : assessments) {
                if (a.getAssessmentId().equals(assessmentId)) {
                    selectedAssess = a;
                    break;
                }
            }
            
            if (selectedAssess == null) {
                return;
            }
            
            // Get module code from assessment
            String moduleCode = selectedAssess.getModuleCode();
            
            // Load modules and find the module
            ModuleManager.loadAllModules();
            Module module = ModuleManager.findModuleByCode(moduleCode);
            if (module == null) {
                return;
            }
            
            // Get all student emails registered to this module
            ArrayList<String> studentEmails = module.getStudentEmails();
            if (studentEmails == null || studentEmails.isEmpty()) {
                studentCombo.addItem("-- No Students Registered --");
                return;
            }
            
            // Load UserManager to get student names
            IOManage.UserManager.loadFromFile();
            
            // Populate dropdown with student name and email
            for (String email : studentEmails) {
                Users.User user = IOManage.UserManager.findByEmail(email);
                if (user != null && user instanceof Users.Student) {
                    String displayText = user.getName() + " (" + email + ")";
                    studentCombo.addItem(displayText);
                } else {
                    // Fallback to just email if user not found
                    studentCombo.addItem(email);
                }
            }
        };
        
        // Update student dropdown when assessment changes
        assessmentCombo.addActionListener(e -> {
            String selected = (String) assessmentCombo.getSelectedItem();
            updateStudentCombo.accept(selected);
        });
        
        // Initialize student dropdown with first assessment
        if (assessmentCombo.getItemCount() > 0) {
            updateStudentCombo.accept((String) assessmentCombo.getSelectedItem());
        }

        // Marks
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Marks:"), gbc);
        gbc.gridx = 1;
        JTextField marksField = new JTextField(20);
        panel.add(marksField, gbc);
        
        // Label to show existing marks warning
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel existingMarksLabel = new JLabel(" ");
        existingMarksLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        existingMarksLabel.setForeground(new java.awt.Color(192, 57, 43));
        panel.add(existingMarksLabel, gbc);
        gbc.gridwidth = 1;
        
        // Function to check and display existing marks when student is selected
        Runnable checkExistingMarks = () -> {
            existingMarksLabel.setText(" ");
            marksField.setText("");
            
            String selectedAssessment = (String) assessmentCombo.getSelectedItem();
            String studentSelected = (String) studentCombo.getSelectedItem();
            
            if (selectedAssessment == null || studentSelected == null || 
                studentSelected.equals("-- No Students Registered --")) {
                return;
            }
            
            String assessmentId = selectedAssessment.split(" - ")[0];
            
            // Extract student email
            String studentEmail;
            if (studentSelected.contains("(") && studentSelected.contains(")")) {
                int start = studentSelected.indexOf("(") + 1;
                int end = studentSelected.indexOf(")");
                studentEmail = studentSelected.substring(start, end);
            } else {
                studentEmail = studentSelected;
            }
            
            // Check for existing marks
            java.util.List<Users.Lecturer.StudentMark> existingMarks = lecturer.getMarksForAssessment(assessmentId);
            for (Users.Lecturer.StudentMark sm : existingMarks) {
                if (sm.getStudentId().equalsIgnoreCase(studentEmail)) {
                    existingMarksLabel.setText(String.format("Already has marks: %.2f (saving will replace)", sm.getMarks()));
                    marksField.setText(String.format("%.2f", sm.getMarks()));
                    break;
                }
            }
        };
        
        // Add listener to student combo to check existing marks
        studentCombo.addActionListener(e -> checkExistingMarks.run());
        
        // Also check when assessment changes
        assessmentCombo.addActionListener(e -> {
            javax.swing.SwingUtilities.invokeLater(checkExistingMarks);
        });

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String selected = (String) assessmentCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(dialog, "Please select an assessment!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String assessmentId = selected.split(" - ")[0];
            
            String studentSelected = (String) studentCombo.getSelectedItem();
            if (studentSelected == null || studentSelected.equals("-- No Students Registered --")) {
                JOptionPane.showMessageDialog(dialog, "Please select a student!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Extract email from selection (format: "Name (email)" or just "email")
            String studentEmail;
            String studentName;
            if (studentSelected.contains("(") && studentSelected.contains(")")) {
                int start = studentSelected.indexOf("(") + 1;
                int end = studentSelected.indexOf(")");
                studentEmail = studentSelected.substring(start, end);
                studentName = studentSelected.substring(0, studentSelected.indexOf("(")).trim();
            } else {
                studentEmail = studentSelected;
                // Get student name from UserManager
                IOManage.UserManager.loadFromFile();
                Users.User user = IOManage.UserManager.findByEmail(studentEmail);
                studentName = (user != null) ? user.getName() : studentEmail;
            }
            
            double marks;
            try {
                marks = Double.parseDouble(marksField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid marks value!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (marksField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter marks!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                lecturer.enterMarks(assessmentId, studentEmail, studentName, marks);
                JOptionPane.showMessageDialog(dialog, "Marks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                marksField.setText("");
                existingMarksLabel.setText(" ");
                // Refresh student dropdown in case of any changes
                updateStudentCombo.accept(selected);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void showViewMarksDialog(JFrame parent, Lecturer lecturer) {
        JDialog dialog = new JDialog(parent, "View Marks", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout());
        
        // Get all assessments for the dropdown
        List<Assessment> assessments = lecturer.getAllAssessments();
        
        JLabel label = new JLabel("Filter by Assessment:");
        JComboBox<String> assessmentCombo = new JComboBox<>();
        assessmentCombo.addItem("All Assessments");
        for (Assessment a : assessments) {
            assessmentCombo.addItem(a.getAssessmentId() + " - " + a.getAssessmentName());
        }
        assessmentCombo.setPreferredSize(new Dimension(300, 25));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(label);
        inputPanel.add(assessmentCombo);

        String[] columns = {"Student ID", "Student Name", "Assessment ID", "Marks", "Feedback"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Function to update the table with marks
        java.util.function.Consumer<String> updateMarksTable = (String selected) -> {
            model.setRowCount(0);
            
            List<StudentMark> marksToShow;
            if (selected == null || selected.equals("All Assessments")) {
                // Get all marks from all assessments
                marksToShow = new ArrayList<>();
                for (Assessment a : assessments) {
                    marksToShow.addAll(lecturer.getMarksForAssessment(a.getAssessmentId()));
            }
            } else {
                // Get marks for specific assessment
                String assessmentId = selected.split(" - ")[0];
                marksToShow = lecturer.getMarksForAssessment(assessmentId);
            }
            
            // Populate table
            for (StudentMark mark : marksToShow) {
                model.addRow(new Object[]{
                    mark.getStudentId(),
                    mark.getStudentName(),
                    mark.getAssessmentId(),
                    mark.getMarks(),
                    mark.getFeedback().isEmpty() ? "No feedback" : mark.getFeedback()
                });
            }
        };

        // Update table when assessment selection changes
        assessmentCombo.addActionListener(e -> {
            String selected = (String) assessmentCombo.getSelectedItem();
            updateMarksTable.accept(selected);
        });

        // Initialize table with all marks by default
        updateMarksTable.accept("All Assessments");

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void showProvideFeedbackDialog(JFrame parent, Lecturer lecturer) {
        List<Assessment> assessments = lecturer.getAllAssessments();
        if (assessments.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No assessments available.", "No Assessments", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Provide Feedback", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Info label
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><b>Note:</b> Feedback can only be provided after marks are entered.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(200, 100, 0));
        panel.add(infoLabel, gbc);
        gbc.gridwidth = 1;

        // Assessment Selection
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Assessment ID:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> assessmentCombo = new JComboBox<>();
        for (Assessment a : assessments) {
            assessmentCombo.addItem(a.getAssessmentId() + " - " + a.getAssessmentName());
        }
        assessmentCombo.setPreferredSize(new Dimension(300, 25));
        panel.add(assessmentCombo, gbc);

        // Student dropdown - will show only students with marks
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Student (with marks):"), gbc);
        gbc.gridx = 1;
        JComboBox<String> studentCombo = new JComboBox<>();
        studentCombo.setPreferredSize(new Dimension(300, 25));
        panel.add(studentCombo, gbc);

        // Function to update student dropdown - only students WITH marks
        java.util.function.Consumer<String> updateStudentCombo = (String selectedAssessment) -> {
            studentCombo.removeAllItems();
            if (selectedAssessment == null || selectedAssessment.isEmpty()) {
                return;
            }
            
            // Extract assessment ID
            String assessmentId = selectedAssessment.split(" - ")[0];
            
            // Get students who have marks for this assessment
            List<Users.Lecturer.StudentMark> marks = lecturer.getMarksForAssessment(assessmentId);
            
            if (marks == null || marks.isEmpty()) {
                studentCombo.addItem("-- No Students Have Marks Yet --");
                return;
            }
            
            // Load UserManager to get student names
            IOManage.UserManager.loadFromFile();
            
            // Populate dropdown with students who have marks
            for (Users.Lecturer.StudentMark mark : marks) {
                String email = mark.getStudentId();
                String name = mark.getStudentName();
                String currentFeedback = mark.getFeedback().isEmpty() || mark.getFeedback().equals("No feedback available") 
                    ? " [No feedback]" : " [Has feedback]";
                String displayText = name + " (" + email + ") - " + mark.getMarks() + " marks" + currentFeedback;
                studentCombo.addItem(displayText);
            }
        };
        
        // Update student dropdown when assessment changes
        assessmentCombo.addActionListener(e -> {
            String selected = (String) assessmentCombo.getSelectedItem();
            updateStudentCombo.accept(selected);
        });
        
        // Initialize student dropdown with first assessment
        if (assessmentCombo.getItemCount() > 0) {
            updateStudentCombo.accept((String) assessmentCombo.getSelectedItem());
        }

        // Feedback
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Feedback:"), gbc);
        gbc.gridx = 1;
        JTextArea feedbackArea = new JTextArea(5, 20);
        feedbackArea.setLineWrap(true);
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        panel.add(feedbackScroll, gbc);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String selected = (String) assessmentCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(dialog, "Please select an assessment!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String assessmentId = selected.split(" - ")[0];
            
            String studentSelected = (String) studentCombo.getSelectedItem();
            if (studentSelected == null || studentSelected.equals("-- No Students Have Marks Yet --")) {
                JOptionPane.showMessageDialog(dialog, 
                    "No students have marks for this assessment yet.\nPlease enter marks before providing feedback!", 
                    "Cannot Provide Feedback", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Extract email from selection (format: "Name (email) - XX marks [feedback status]")
            String studentEmail;
            try {
                int start = studentSelected.indexOf("(") + 1;
                int end = studentSelected.indexOf(")");
                studentEmail = studentSelected.substring(start, end);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid student selection!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String feedback = feedbackArea.getText().trim();

            if (feedback.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter feedback!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                lecturer.provideFeedback(assessmentId, studentEmail, feedback);
                JOptionPane.showMessageDialog(dialog, 
                    "Feedback provided successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                feedbackArea.setText("");
                // Refresh student dropdown to update feedback status
                updateStudentCombo.accept(selected);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Cannot provide feedback:\n" + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void showViewFeedbackDialog(JFrame parent, Lecturer lecturer) {
        // Reload lecturer data to ensure we have the latest feedback from file
        AssessmentManager.loadLecturerData(lecturer);
        
        JDialog dialog = new JDialog(parent, "View Feedback", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Feedback Records");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Collect all feedback
        List<Object[]> feedbackData = new ArrayList<>();
        List<Assessment> assessments = lecturer.getAllAssessments();
        
        for (Assessment assessment : assessments) {
            String assessmentId = assessment.getAssessmentId();
            List<Users.Lecturer.StudentMark> marks = lecturer.getMarksForAssessment(assessmentId);
            
            for (Users.Lecturer.StudentMark mark : marks) {
                String feedback = mark.getFeedback();
                if (feedback != null && !feedback.isEmpty() && !feedback.equals("No feedback available")) {
                    feedbackData.add(new Object[]{
                        assessment.getModuleCode(),
                        assessment.getAssessmentName(),
                        mark.getStudentId(),
                        mark.getStudentName(),
                        String.format("%.1f/%.0f", mark.getMarks(), assessment.getMaxMarks()),
                        feedback
                    });
                }
            }
        }

        if (feedbackData.isEmpty()) {
            JLabel noDataLabel = new JLabel("No feedback has been provided yet.", JLabel.CENTER);
            noDataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            mainPanel.add(noDataLabel, BorderLayout.CENTER);
        } else {
            // Create table
            String[] columns = {"Module", "Assessment", "Student ID", "Student Name", "Marks", "Feedback"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Object[] row : feedbackData) {
                model.addRow(row);
            }

            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            // Set column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Module
            table.getColumnModel().getColumn(1).setPreferredWidth(150); // Assessment
            table.getColumnModel().getColumn(2).setPreferredWidth(100); // Student ID
            table.getColumnModel().getColumn(3).setPreferredWidth(150); // Student Name
            table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Marks
            table.getColumnModel().getColumn(5).setPreferredWidth(300); // Feedback
            
            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Info label
            JLabel infoLabel = new JLabel(String.format("Total feedback entries: %d", feedbackData.size()));
            infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            mainPanel.add(infoLabel, BorderLayout.SOUTH);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        if (!feedbackData.isEmpty()) {
            JLabel infoLabel = new JLabel(String.format("Total feedback entries: %d", feedbackData.size()));
            infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            bottomPanel.add(infoLabel, BorderLayout.WEST);
        }
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void showViewScheduleDialog(JFrame parent, Lecturer lecturer) {
        ClassManager.loadFromFile();

        List<Entity.ClassGroup> myClassGroups = new ArrayList<>();
        for (Entity.ClassGroup cg : ClassManager.classGroups) {
            if (cg.getLecturerEmails().contains(lecturer.getEmail())) {
                myClassGroups.add(cg);
            }
        }

        JDialog dialog = new JDialog(parent, "View Schedule", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parent);

        String[] columns = {"Class Code", "Module", "Time", "Classroom"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Entity.ClassGroup cg : myClassGroups) {
            String moduleCode = cg.getModule().getCode();
            String moduleName = cg.getModule().getName();
            String time = cg.getTime() != null && !cg.getTime().isEmpty() ? cg.getTime() : "Not set";
            String classroom = cg.getClassroom() != null && !cg.getClassroom().isEmpty() ? cg.getClassroom() : "Not set";
            model.addRow(new Object[]{cg.getClassCode(), moduleCode + " (" + moduleName + ")", time, classroom});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        if (myClassGroups.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No classes registered. Please register to classes first.", "No Classes", JOptionPane.INFORMATION_MESSAGE);
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
    
    /**
     * Get the next assessment ID starting from A01 and incrementing by 1
     * Reads all assessments from assessments.txt to find the highest ID
     */
    private static String getNextAssessmentId() {
        java.io.File assessmentsFile = new java.io.File("data/assessments.txt");
        int maxNumber = 0;
        
        if (assessmentsFile.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(assessmentsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split("\\|", -1);
                    if (parts.length < 1) continue;
                    
                    String assessmentId = parts[0].trim();
                    // Check if ID matches pattern A## (A followed by 1-3 digits)
                    if (assessmentId.length() >= 2 && assessmentId.toUpperCase().startsWith("A")) {
                        try {
                            String numberPart = assessmentId.substring(1);
                            int number = Integer.parseInt(numberPart);
                            if (number > maxNumber) {
                                maxNumber = number;
                            }
                        } catch (NumberFormatException e) {
                            // Not a valid numeric ID, skip
                        }
                    }
                }
            } catch (java.io.IOException e) {
                // If error reading file, start from A01
                System.out.println("Warning: Could not read assessments.txt: " + e.getMessage());
            }
        }
        
        // Return next ID (maxNumber + 1), formatted with leading zero if needed
        int nextNumber = maxNumber + 1;
        return String.format("A%02d", nextNumber); // A01, A02, ..., A99
    }
}


