package GUI.dialogs;

import Users.AcademicLeader;
import Users.User;
import IOManage.UserManager;
import IOManage.ModuleManager;
import IOManage.ReportManager;
import IOManage.CommentManager;
import IOManage.ClassManager;
import Entity.Module;
import Entity.Report;
import Entity.Comment;
import Entity.ClassGroup;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AcademicLeaderDialogs {
    
    public static void showEditProfileDialog(JFrame parent, AcademicLeader leader) {
        JDialog dialog = new JDialog(parent, "Edit Profile", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(leader.getName(), 20);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(leader.getEmail(), 20);
        panel.add(emailField, gbc);
        
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

            if (!name.isEmpty()) leader.setName(name);
            if (!email.isEmpty()) leader.setEmail(email);

            if (!newPw.isEmpty()) {
                if (!IOManage.PasswordUtils.verifyPassword(currentPw, leader.getPassword())) {
                    JOptionPane.showMessageDialog(dialog, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!newPw.equals(confirmPw)) {
                    JOptionPane.showMessageDialog(dialog, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                leader.setPassword(IOManage.PasswordUtils.hashPassword(newPw));
            }

            UserManager.updateUser(leader);
            UserManager.saveToFile();
            JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
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

    public static void showManageModulesDialog(JFrame parent, AcademicLeader leader) {
        ModuleManager.loadFromFile(leader.getEmail());
        UserManager.loadFromFile();

        JDialog dialog = new JDialog(parent, "Manage Modules", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(parent);

        String[] columns = {"Module Code", "Module Name", "Academic Leader", "Assigned Lecturers"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Helper method to get lecturer names
        java.util.function.Function<Module, String> getLecturerNames = (Module m) -> {
            if (m.getLecturerEmails() == null || m.getLecturerEmails().isEmpty()) {
                return "None";
            }
            ArrayList<String> names = new ArrayList<>();
            for (String email : m.getLecturerEmails()) {
                User user = UserManager.findByEmail(email);
                if (user != null) {
                    names.add(user.getName() + " (" + email + ")");
                } else {
                    names.add(email);
                }
            }
            return String.join(", ", names);
        };

        for (Module m : ModuleManager.modules) {
            if (m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                model.addRow(new Object[]{
                    m.getCode(), 
                    m.getName(), 
                    m.getAcademicLeaderEmail(),
                    getLecturerNames.apply(m)
                });
            }
        }

        JTable table = new JTable(model);
        // Set column widths for better display
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(300);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Module");
        JButton editButton = new JButton("Edit Module");
        JButton deleteButton = new JButton("Delete Module");
        JButton assignLecturerButton = new JButton("Assign Lecturer");
        JButton removeLecturerButton = new JButton("Remove Lecturer");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> {
            showAddModuleDialog(dialog, leader);
            ModuleManager.loadFromFile(leader.getEmail());
            UserManager.loadFromFile();
            model.setRowCount(0);
            for (Module m : ModuleManager.modules) {
                if (m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                    model.addRow(new Object[]{
                        m.getCode(), 
                        m.getName(), 
                        m.getAcademicLeaderEmail(),
                        getLecturerNames.apply(m)
                    });
                }
            }
        });

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a module to edit!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String moduleCode = (String) model.getValueAt(row, 0);
            showEditModuleDialog(dialog, leader, moduleCode);
            ModuleManager.loadFromFile(leader.getEmail());
            UserManager.loadFromFile();
            model.setRowCount(0);
            for (Module m : ModuleManager.modules) {
                if (m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                    model.addRow(new Object[]{
                        m.getCode(), 
                        m.getName(), 
                        m.getAcademicLeaderEmail(),
                        getLecturerNames.apply(m)
                    });
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a module to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String moduleCode = (String) model.getValueAt(row, 0);
            
            // Check if module has classes
            if (ClassManager.hasClassesForModule(moduleCode)) {
                JOptionPane.showMessageDialog(dialog, "Cannot delete module: There are classes associated with this module. Please delete all classes first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete this module?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Module m = ModuleManager.findModuleByCode(moduleCode);
                if (m != null && m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                    ModuleManager.deleteModule(m, leader);
                    JOptionPane.showMessageDialog(dialog, "Module deleted successfully!");
                    ModuleManager.loadFromFile(leader.getEmail());
                    UserManager.loadFromFile();
                    model.setRowCount(0);
                    for (Module mod : ModuleManager.modules) {
                        if (mod.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                            model.addRow(new Object[]{
                                mod.getCode(), 
                                mod.getName(), 
                                mod.getAcademicLeaderEmail(),
                                getLecturerNames.apply(mod)
                            });
                        }
                    }
                }
            }
        });

        assignLecturerButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a module!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String moduleCode = (String) model.getValueAt(row, 0);
            showAssignLecturerDialog(dialog, leader, moduleCode);
            // Refresh table after assigning lecturer
            ModuleManager.loadFromFile(leader.getEmail());
            UserManager.loadFromFile();
            model.setRowCount(0);
            for (Module m : ModuleManager.modules) {
                if (m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                    model.addRow(new Object[]{
                        m.getCode(), 
                        m.getName(), 
                        m.getAcademicLeaderEmail(),
                        getLecturerNames.apply(m)
                    });
                }
            }
        });

        removeLecturerButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a module!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String moduleCode = (String) model.getValueAt(row, 0);
            showRemoveLecturerDialog(dialog, leader, moduleCode);
            // Refresh table after removing lecturer
            ModuleManager.loadFromFile(leader.getEmail());
            UserManager.loadFromFile();
            model.setRowCount(0);
            for (Module m : ModuleManager.modules) {
                if (m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                    model.addRow(new Object[]{
                        m.getCode(), 
                        m.getName(), 
                        m.getAcademicLeaderEmail(),
                        getLecturerNames.apply(m)
                    });
                }
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(assignLecturerButton);
        buttonPanel.add(removeLecturerButton);
        buttonPanel.add(closeButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void showAddModuleDialog(JDialog parent, AcademicLeader leader) {
        // Load all modules to calculate next module code
        ModuleManager.loadAllModules();
        
        // Calculate next module code (e.g., mod2, mod3, mod4 -> mod5)
        String nextModuleCode = getNextModuleCode();
        
        JDialog dialog = new JDialog(parent, "Add Module", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Module Code:"), gbc);
        gbc.gridx = 1;
        JTextField codeField = new JTextField(nextModuleCode, 20);
        codeField.setEditable(false);
        codeField.setBackground(new Color(240, 240, 240));
        panel.add(codeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Module Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        addButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (ModuleManager.findModuleByCode(code) != null) {
                JOptionPane.showMessageDialog(dialog, "Module code already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Module newModule = new Module(code, name, leader.getEmail());
            ModuleManager.addModule(newModule);
            JOptionPane.showMessageDialog(dialog, "Module added successfully!");
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void showEditModuleDialog(JDialog parent, AcademicLeader leader, String moduleCode) {
        Module m = ModuleManager.findModuleByCode(moduleCode);
        if (m == null || !m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
            JOptionPane.showMessageDialog(parent, "Module not found or not under your management.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Edit Module", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Module Code:"), gbc);
        gbc.gridx = 1;
        JLabel codeLabel = new JLabel(m.getCode());
        panel.add(codeLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Module Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(m.getName(), 20);
        panel.add(nameField, gbc);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Module name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ModuleManager.editModule(m, leader, newName);
            JOptionPane.showMessageDialog(dialog, "Module updated successfully!");
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

    public static void showAssignLecturerDialog(Window parent, AcademicLeader leader, String moduleCode) {
        // Load all modules first to ensure we have the latest data
        ModuleManager.loadAllModules();
        
        Module m = ModuleManager.findModuleByCode(moduleCode);
        if (m == null || !m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
            JOptionPane.showMessageDialog(parent, "Module not found or not under your management.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Load users to get lecturer names
        UserManager.loadFromFile();

        // Get available lecturers (under this leader but not already assigned to this module)
        ArrayList<String> availableLecturers = new ArrayList<>();
        for (String lecEmail : leader.getLecturerEmails()) {
            if (!m.getLecturerEmails().contains(lecEmail)) {
                User user = UserManager.findByEmail(lecEmail);
                if (user != null) {
                    String displayName = user.getName() + " (" + lecEmail + ")";
                    availableLecturers.add(displayName);
                } else {
                    availableLecturers.add(lecEmail);
                }
            }
        }

        if (availableLecturers.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No available lecturers to assign. All lecturers under your management are already assigned to this module.", "No Available Lecturers", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Assign Lecturer", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(450, 180);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Select Lecturer:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> lecturerCombo = new JComboBox<>();
        for (String lecturer : availableLecturers) {
            lecturerCombo.addItem(lecturer);
        }
        lecturerCombo.setPreferredSize(new Dimension(300, 25));
        panel.add(lecturerCombo, gbc);

        JButton assignButton = new JButton("Assign");
        JButton cancelButton = new JButton("Cancel");

        assignButton.addActionListener(e -> {
            String selected = (String) lecturerCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a lecturer!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Extract email from selection (format: "Name (email)" or just "email")
            String lecEmail;
            if (selected.contains("(") && selected.contains(")")) {
                int start = selected.indexOf("(") + 1;
                int end = selected.indexOf(")");
                lecEmail = selected.substring(start, end);
            } else {
                lecEmail = selected;
            }
            
            // Reload modules to ensure we have the latest module instance from the list
            ModuleManager.loadAllModules();
            Module moduleToUpdate = ModuleManager.findModuleByCode(moduleCode);
            if (moduleToUpdate == null || !moduleToUpdate.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                JOptionPane.showMessageDialog(dialog, "Module not found or not under your management.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = ModuleManager.assignLecturer(moduleToUpdate, lecEmail, leader);
            if (success) {
                // Reload modules after successful assignment to ensure consistency
                ModuleManager.loadAllModules();
                JOptionPane.showMessageDialog(dialog, "Lecturer assigned successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to assign lecturer. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(assignButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void showRemoveLecturerDialog(Window parent, AcademicLeader leader, String moduleCode) {
        Module m = ModuleManager.findModuleByCode(moduleCode);
        if (m == null || !m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
            JOptionPane.showMessageDialog(parent, "Module not found or not under your management.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Load users to get lecturer names
        UserManager.loadFromFile();

        // Get lecturers that are both under this leader AND assigned to this module
        ArrayList<String> assignedLecturers = new ArrayList<>();
        for (String lecEmail : m.getLecturerEmails()) {
            // Check if lecturer is under this academic leader
            if (leader.getLecturerEmails().contains(lecEmail)) {
                User user = UserManager.findByEmail(lecEmail);
                if (user != null) {
                    String displayName = user.getName() + " (" + lecEmail + ")";
                    assignedLecturers.add(displayName);
                } else {
                    assignedLecturers.add(lecEmail);
                }
            }
        }

        if (assignedLecturers.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No lecturers assigned to this module.", "No Lecturers", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Remove Lecturer", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(450, 180);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Select Lecturer:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> lecturerCombo = new JComboBox<>();
        for (String lecturer : assignedLecturers) {
            lecturerCombo.addItem(lecturer);
        }
        lecturerCombo.setPreferredSize(new Dimension(300, 25));
        panel.add(lecturerCombo, gbc);

        JButton removeButton = new JButton("Remove");
        JButton cancelButton = new JButton("Cancel");

        removeButton.addActionListener(e -> {
            String selected = (String) lecturerCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a lecturer!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Extract email from selection (format: "Name (email)" or just "email")
            String lecEmail;
            if (selected.contains("(") && selected.contains(")")) {
                int start = selected.indexOf("(") + 1;
                int end = selected.indexOf(")");
                lecEmail = selected.substring(start, end);
            } else {
                lecEmail = selected;
            }
            
            // Reload modules to ensure we have the latest module instance
            ModuleManager.loadAllModules();
            Module moduleToUpdate = ModuleManager.findModuleByCode(moduleCode);
            if (moduleToUpdate == null || !moduleToUpdate.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                JOptionPane.showMessageDialog(dialog, "Module not found or not under your management.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean removed = ModuleManager.removeLecturer(moduleToUpdate, lecEmail, leader);
            if (removed) {
                JOptionPane.showMessageDialog(dialog, "Lecturer removed successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lecturer not assigned to this module.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(removeButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void showAnalyzeReportsDialog(JFrame parent, AcademicLeader leader) {
        JDialog dialog = new JDialog(parent, "Analyze Reports", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parent);

        JPanel buttonPanel = new JPanel();
        JButton addReportButton = new JButton("Add Report");
        JButton viewReportsButton = new JButton("View My Reports");
        JButton deleteReportButton = new JButton("Delete Report");
        JButton viewAllReportsButton = new JButton("View All Reports");
        JButton closeButton = new JButton("Close");

        addReportButton.addActionListener(e -> showAddReportDialog(dialog, leader));
        viewReportsButton.addActionListener(e -> showViewReportsDialog(dialog, leader));
        deleteReportButton.addActionListener(e -> showDeleteReportDialog(dialog, leader));
        viewAllReportsButton.addActionListener(e -> showViewAllReportsDialog(dialog));

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addReportButton);
        buttonPanel.add(viewReportsButton);
        buttonPanel.add(deleteReportButton);
        buttonPanel.add(viewAllReportsButton);
        buttonPanel.add(closeButton);

        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private static void showAddReportDialog(JDialog parent, AcademicLeader leader) {
        ModuleManager.modules.clear();
        ModuleManager.loadFromFile(leader.getEmail());
        ReportManager.loadClassGroups();

        if (ModuleManager.modules.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No modules available.", "No Modules", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Add Report", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);

        JComboBox<String> moduleCombo = new JComboBox<>();
        for (Module m : ModuleManager.modules) {
            moduleCombo.addItem(m.getCode() + " - " + m.getName());
        }

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Select Module:"), gbc);
        gbc.gridx = 1;
        panel.add(moduleCombo, gbc);

        JButton generateButton = new JButton("Generate Report");
        JButton cancelButton = new JButton("Cancel");

        generateButton.addActionListener(e -> {
            String selected = (String) moduleCombo.getSelectedItem();
            String moduleCode = selected.split(" - ")[0];
            Module selectedModule = ModuleManager.findModuleByCode(moduleCode);

            boolean ok = ReportManager.generateModuleReport(selectedModule);
            if (!ok) {
                JOptionPane.showMessageDialog(dialog, "Report not saved because this module has no classes.", "No Classes", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Report r = ReportManager.generateReportObject(selectedModule, leader.getEmail());
            ReportManager.addReport(r, leader.getEmail());
            JOptionPane.showMessageDialog(dialog, "Report saved successfully!");
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void showViewReportsDialog(JDialog parent, AcademicLeader leader) {
        ReportManager.loadReportFromFile(leader.getEmail());

        JDialog dialog = new JDialog(parent, "My Reports", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(parent);

        String[] columns = {"Report ID", "Module Code", "Total Students", "Overall Average"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Report r : ReportManager.reports) {
            model.addRow(new Object[]{r.getReportId(), r.getModuleCode(), r.getTotalStudents(), r.getOverallAverage()});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        if (ReportManager.reports.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No reports found.", "No Reports", JOptionPane.INFORMATION_MESSAGE);
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

    private static void showDeleteReportDialog(JDialog parent, AcademicLeader leader) {
        ReportManager.loadReportFromFile(leader.getEmail());

        if (ReportManager.reports.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No reports to delete.", "No Reports", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Delete Report", true);
        dialog.setSize(450, 180);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Select Report:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> reportCombo = new JComboBox<>();
        
        // Populate dropdown with available reports
        // Format: "Report ID: R001 - Module: ABC123"
        for (Report r : ReportManager.reports) {
            String displayText = "Report ID: " + r.getReportId() + " - Module: " + r.getModuleCode();
            reportCombo.addItem(displayText);
        }
        reportCombo.setPreferredSize(new Dimension(300, 25));
        panel.add(reportCombo, gbc);

        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        deleteButton.addActionListener(e -> {
            String selected = (String) reportCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a report!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extract report ID from the selected item (format: "Report ID: R001 - Module: ABC123")
            String reportId = selected.substring(selected.indexOf("Report ID: ") + 11);
            reportId = reportId.substring(0, reportId.indexOf(" -"));

            Report r = ReportManager.findReportByID(reportId);
            if (r == null) {
                JOptionPane.showMessageDialog(dialog, "Report ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(dialog, 
                "Are you sure you want to delete this report?\n" + selected, 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = ReportManager.deleteReport(r);
            if (deleted) {
                JOptionPane.showMessageDialog(dialog, "Report deleted successfully!");
                dialog.dispose();
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void showViewAllReportsDialog(JDialog parent) {
        ReportManager.loadAllReportFromFile();

        JDialog dialog = new JDialog(parent, "All Reports", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parent);

        String[] columns = {"Report ID", "Module Code", "Academic Leader", "Total Students", "Overall Average"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Report r : ReportManager.reports) {
            model.addRow(new Object[]{r.getReportId(), r.getModuleCode(), r.getAcademicLeaderEmail(), r.getTotalStudents(), r.getOverallAverage()});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        if (ReportManager.reports.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No reports found.", "No Reports", JOptionPane.INFORMATION_MESSAGE);
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

    public static void showViewCommentDialog(JFrame parent, AcademicLeader leader) {
        CommentManager.loadFromFile();

        ArrayList<Comment> myComments = CommentManager.filterCommentsByAcademicLeader(leader.getEmail());

        if (myComments.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No comments available for you.", "No Comments", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "View Comments", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parent);

        String[] columns = {"Student Name", "Student Email", "Lecturer Email", "Comment"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Comment c : myComments) {
            model.addRow(new Object[]{c.getStudentName(), c.getStudentEmail(), c.getLecturerEmail(), c.getContent()});
        }

        JTable table = new JTable(model);
        table.getColumnModel().getColumn(3).setPreferredWidth(300);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void showRegisterLecturerToClassDialog(JFrame parent, AcademicLeader leader) {
        if (leader.getLecturerEmails().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No lecturers under your management.", "No Lecturers", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Register Lecturer to Class", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(parent);

        JComboBox<String> lecturerCombo = new JComboBox<>();
        for (String lecEmail : leader.getLecturerEmails()) {
            lecturerCombo.addItem(lecEmail);
        }

        ModuleManager.loadFromFile(leader.getEmail());
        ClassManager.loadFromFile();

        JComboBox<String> classCombo = new JComboBox<>();
        updateClassCombo(classCombo, leader, (String) lecturerCombo.getSelectedItem());

        lecturerCombo.addActionListener(e -> updateClassCombo(classCombo, leader, (String) lecturerCombo.getSelectedItem()));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Lecturer Email:"), gbc);
        gbc.gridx = 1;
        panel.add(lecturerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Class Code:"), gbc);
        gbc.gridx = 1;
        panel.add(classCombo, gbc);

        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");

        registerButton.addActionListener(e -> {
            String lecEmail = (String) lecturerCombo.getSelectedItem();
            String classCode = (String) classCombo.getSelectedItem();
            if (classCode == null || classCode.equals("-- No Available Classes --")) {
                JOptionPane.showMessageDialog(dialog, "No available classes for this lecturer.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean success = leader.registerLecturerToClass(lecEmail, classCode);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Lecturer registered to class successfully!");
                updateClassCombo(classCombo, leader, lecEmail);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void updateClassCombo(JComboBox<String> classCombo, AcademicLeader leader, String lecturerEmail) {
        classCombo.removeAllItems();
        ModuleManager.loadFromFile(leader.getEmail());
        ClassManager.loadFromFile();

        ArrayList<ClassGroup> availableClasses = new ArrayList<>();
        for (Module module : ModuleManager.modules) {
            if (module.getLecturerEmails().contains(lecturerEmail)) {
                for (ClassGroup cg : ClassManager.classGroups) {
                    if (cg.getModule().getCode().equalsIgnoreCase(module.getCode())) {
                        // Only allow assignment if the class has no lecturer assigned (single-lecturer policy)
                        if (cg.getLecturerEmails().isEmpty()) {
                            availableClasses.add(cg);
                        }
                    }
                }
            }
        }

        if (availableClasses.isEmpty()) {
            classCombo.addItem("-- No Available Classes --");
        } else {
            for (ClassGroup cg : availableClasses) {
                classCombo.addItem(cg.getClassCode());
            }
        }
    }

    /**
     * Gets the next module code by incrementing from existing codes (e.g., mod2, mod3, mod4 -> mod5)
     */
    private static String getNextModuleCode() {
        int maxNumber = 0;
        
        for (Module m : ModuleManager.modules) {
            String code = m.getCode().trim().toLowerCase();
            // Check if code starts with "mod" followed by digits
            if (code.startsWith("mod")) {
                try {
                    String numStr = code.substring(3);
                    int num = Integer.parseInt(numStr);
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException e) {
                    // Skip if not a valid number after "mod"
                }
            }
        }
        
        // If no "mod" codes found, start with mod1
        if (maxNumber == 0) {
            return "mod1";
        }
        
        return "mod" + (maxNumber + 1);
    }
}


