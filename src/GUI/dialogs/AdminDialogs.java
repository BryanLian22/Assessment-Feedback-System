package GUI.dialogs;

import Users.AdminStaff;
import Users.User;
import Users.Student;
import Users.Lecturer;
import Users.AcademicLeader;
import IOManage.UserManager;
import IOManage.ModuleManager;
import IOManage.ClassManager;
import IOManage.GradingSystemManager;
import Entity.ClassGroup;
import Entity.Module;
import Entity.GradingSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminDialogs {
    
    public static void showEditProfileDialog(JFrame parent, AdminStaff admin) {
        JDialog dialog = new JDialog(parent, "Edit Profile", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(admin.getName(), 20);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(admin.getEmail(), 20);
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
            
            boolean emailChanged = false;
            
            if (!name.isEmpty()) admin.setName(name);
            
            if (!email.isEmpty() && !email.equals(admin.getEmail())) {
                // Protect root superadmin from email change
                if (admin.getEmail().equalsIgnoreCase("root")) {
                    JOptionPane.showMessageDialog(dialog, 
                        "✗ Error: The root superadmin email cannot be changed.", 
                        "Protected Account", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String oldEmail = admin.getEmail();
                
                if (IOManage.EmailUpdateManager.emailExistsInSystem(email)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "✗ Error: Email " + email + " already exists in the system.", 
                        "Email Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (IOManage.EmailUpdateManager.updateEmailAcrossSystem(oldEmail, email)) {
                    emailChanged = true;
                    // ✅ UPDATE: Set the new email in the current admin object
                    admin.setEmail(email);
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "✗ Failed to update email.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!newPw.isEmpty()) {
                if (IOManage.PasswordUtils.verifyPassword(currentPw, admin.getPassword())) {
                    if (newPw.equals(confirmPw)) {
                        admin.setPassword(IOManage.PasswordUtils.hashPassword(newPw));
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            "New passwords do not match!", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "Current password is incorrect!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!emailChanged) {
                UserManager.updateUser(admin);
                UserManager.saveToFile();
            } else {
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

    public static void showManageUsersDialog(JFrame parent, AdminStaff admin) {
        UserManager.loadFromFile();

        JDialog dialog = new JDialog(parent, "Manage Users", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parent);

        String[] columns = {"Name", "Email", "Type"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (User u : UserManager.users) {
            String type = "";
            if (u instanceof AdminStaff) type = "AdminStaff";
            else if (u instanceof AcademicLeader) type = "AcademicLeader";
            else if (u instanceof Lecturer) type = "Lecturer";
            else if (u instanceof Student) type = "Student";
            model.addRow(new Object[]{u.getName(), u.getEmail(), type});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        createButton.addActionListener(e -> {
            showCreateUserDialog(dialog, admin);
            refreshUserTable(model);
        });

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a user to edit!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String email = (String) model.getValueAt(row, 1);
            showEditUserDialog(dialog, admin, email);
            refreshUserTable(model);
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a user to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String email = (String) model.getValueAt(row, 1);
            
            // Protect root superadmin from deletion
            if (email.equalsIgnoreCase("root")) {
                JOptionPane.showMessageDialog(dialog, "✗ Error: The root superadmin account cannot be deleted.", "Protected Account", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User targetUser = UserManager.findByEmail(email);
            if (targetUser != null) {
                // Check for critical dependencies
                String dependencies = UserManager.checkUserDependencies(targetUser);
                
                if (dependencies != null) {
                    // Block deletion for sole lecturers
                    if (targetUser instanceof Lecturer && dependencies.contains("Sole lecturer")) {
                        JOptionPane.showMessageDialog(dialog, 
                            "❌ Cannot delete this user:\n\n" + dependencies + "\nPlease assign another lecturer before deleting this user.", 
                            "Deletion Blocked", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Block deletion for academic leaders with modules
                    if (targetUser instanceof AcademicLeader && dependencies.contains("Managing module")) {
                        JOptionPane.showMessageDialog(dialog, 
                            "❌ Cannot delete this user:\n\n" + dependencies + "\nPlease reassign or delete the modules before deleting this user.", 
                            "Deletion Blocked", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Show warning for other dependencies
                    int warnConfirm = JOptionPane.showConfirmDialog(dialog, 
                        "⚠️ WARNING: This user has dependencies:\n\n" + dependencies + "\nThese references will be removed. Continue?", 
                        "Dependency Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (warnConfirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                
                int confirm = JOptionPane.showConfirmDialog(dialog, 
                    "Are you sure you want to delete " + targetUser.getName() + " (" + targetUser.getEmail() + ")?", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Cleanup references before deletion
                    UserManager.cleanupUserReferences(targetUser);
                    UserManager.users.remove(targetUser);
                    UserManager.saveToFile();
                    JOptionPane.showMessageDialog(dialog, "User deleted successfully!");
                    refreshUserTable(model);
                }
            }
        });

        refreshButton.addActionListener(e -> refreshUserTable(model));
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void refreshUserTable(DefaultTableModel model) {
        UserManager.loadFromFile();
        model.setRowCount(0);
        for (User u : UserManager.users) {
            String type = "";
            if (u instanceof AdminStaff) type = "AdminStaff";
            else if (u instanceof AcademicLeader) type = "AcademicLeader";
            else if (u instanceof Lecturer) type = "Lecturer";
            else if (u instanceof Student) type = "Student";
            model.addRow(new Object[]{u.getName(), u.getEmail(), type});
        }
    }

    private static void showCreateUserDialog(JDialog parent, AdminStaff admin) {
        JDialog dialog = new JDialog(parent, "Create User", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(20);
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Student", "Lecturer", "AcademicLeader", "AdminStaff"});
        panel.add(typeCombo, gbc);

        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String type = (String) typeCombo.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if email already exists in the system
            if (UserManager.findByEmail(email) != null) {
                JOptionPane.showMessageDialog(dialog, "✗ Error: Email " + email + " already exists in the system.", "Email Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hash the password before creating user
            String hashedPassword = IOManage.PasswordUtils.hashPassword(password);
            
            User newUser = null;
            switch (type) {
                case "Student" -> newUser = new Student(name, email, hashedPassword);
                case "Lecturer" -> newUser = new Lecturer(name, email, hashedPassword, "");
                case "AcademicLeader" -> newUser = new AcademicLeader(name, email, hashedPassword);
                case "AdminStaff" -> newUser = new AdminStaff(name, email, hashedPassword);
            }

            if (newUser != null) {
                UserManager.addUser(newUser);
                UserManager.saveToFile();
                JOptionPane.showMessageDialog(dialog, "User created successfully!");
                dialog.dispose();
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

    private static void showEditUserDialog(JDialog parent, AdminStaff admin, String targetEmail) {
        User targetUser = UserManager.findByEmail(targetEmail);
        if (targetUser == null) {
            JOptionPane.showMessageDialog(parent, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Edit User: " + targetUser.getName(), true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(targetUser.getName(), 20);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(targetUser.getEmail(), 20);
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Admin Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField adminPwField = new JPasswordField(20);
        panel.add(adminPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPwField = new JPasswordField(20);
        panel.add(newPwField, gbc);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String adminPw = new String(adminPwField.getPassword());
            String newPw = new String(newPwField.getPassword());

            if (!name.isEmpty()) targetUser.setName(name);
            
            boolean emailChanged = false;
            if (!email.isEmpty() && !email.equals(targetUser.getEmail())) {
                // Protect root superadmin from email change
                if (targetUser.getEmail().equalsIgnoreCase("root")) {
                    JOptionPane.showMessageDialog(dialog, "✗ Error: The root superadmin email cannot be changed.", "Protected Account", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String oldEmail = targetUser.getEmail();
                if (IOManage.EmailUpdateManager.emailExistsInSystem(email)) {
                    JOptionPane.showMessageDialog(dialog, "✗ Error: Email " + email + " already exists in the system.", "Email Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (IOManage.EmailUpdateManager.updateEmailAcrossSystem(oldEmail, email)) {
                    JOptionPane.showMessageDialog(dialog, "✓ Email updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    emailChanged = true;
                } else {
                    JOptionPane.showMessageDialog(dialog, "✗ Failed to update email.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (!newPw.isEmpty()) {
                if (!IOManage.PasswordUtils.verifyPassword(adminPw, admin.getPassword())) {
                    JOptionPane.showMessageDialog(dialog, "Admin password incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                targetUser.setPassword(IOManage.PasswordUtils.hashPassword(newPw));
            }

            // Only update and save if email was NOT changed
            if (!emailChanged) {
                UserManager.updateUser(targetUser);
                UserManager.saveToFile();
            } else {
                UserManager.loadFromFile();
            }
            JOptionPane.showMessageDialog(dialog, "User updated successfully!");
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

    public static void showAssignLecturersDialog(JFrame parent, AdminStaff admin) {
        UserManager.loadFromFile();

        JDialog dialog = new JDialog(parent, "Assign Lecturers to Academic Leaders", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(parent);

        java.util.ArrayList<AcademicLeader> leaders = new java.util.ArrayList<>();
        for (User u : UserManager.users) {
            if (u instanceof AcademicLeader al) {
                leaders.add(al);
            }
        }

        if (leaders.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No Academic Leaders found.", "No Leaders", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new BorderLayout());

        JComboBox<String> leaderCombo = new JComboBox<>();
        for (AcademicLeader al : leaders) {
            leaderCombo.addItem(al.getName() + " (" + al.getEmail() + ")");
        }

        JPanel leaderPanel = new JPanel(new FlowLayout());
        leaderPanel.add(new JLabel("Academic Leader:"));
        leaderPanel.add(leaderCombo);

        String[] columns = {"Lecturer Email"};
        DefaultTableModel assignedModel = new DefaultTableModel(columns, 0);
        JTable assignedTable = new JTable(assignedModel);

        String[] columns2 = {"Lecturer Email"};
        DefaultTableModel availableModel = new DefaultTableModel(columns2, 0);
        JTable availableTable = new JTable(availableModel);

        updateLecturerTables(leaderCombo, assignedModel, availableModel, leaders);

        leaderCombo.addActionListener(e -> updateLecturerTables(leaderCombo, assignedModel, availableModel, leaders));

        JButton addButton = new JButton("Add Lecturer");
        JButton removeButton = new JButton("Remove Lecturer");
        JButton saveButton = new JButton("Save");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> {
            int row = availableTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a lecturer to add!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String selected = (String) leaderCombo.getSelectedItem();
            String leaderEmail = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
            AcademicLeader selectedLeader = leaders.stream()
                .filter(al -> al.getEmail().equals(leaderEmail))
                .findFirst().orElse(null);
            
            if (selectedLeader != null) {
                String lecEmail = (String) availableModel.getValueAt(row, 0);
                selectedLeader.addLecturerEmail(lecEmail);
                updateLecturerTables(leaderCombo, assignedModel, availableModel, leaders);
            }
        });

        removeButton.addActionListener(e -> {
            int row = assignedTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a lecturer to remove!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String selected = (String) leaderCombo.getSelectedItem();
            String leaderEmail = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
            AcademicLeader selectedLeader = leaders.stream()
                .filter(al -> al.getEmail().equals(leaderEmail))
                .findFirst().orElse(null);
            
            if (selectedLeader != null) {
                String lecEmail = (String) assignedModel.getValueAt(row, 0);
                selectedLeader.getLecturerEmails().remove(lecEmail);
                updateLecturerTables(leaderCombo, assignedModel, availableModel, leaders);
            }
        });

        saveButton.addActionListener(e -> {
            String selected = (String) leaderCombo.getSelectedItem();
            String leaderEmail = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
            AcademicLeader selectedLeader = leaders.stream()
                .filter(al -> al.getEmail().equals(leaderEmail))
                .findFirst().orElse(null);
            
            if (selectedLeader != null) {
                UserManager.updateUser(selectedLeader);
                UserManager.saveToFile();
                JOptionPane.showMessageDialog(dialog, "Lecturer assignments saved successfully!");
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        JPanel tablePanel = new JPanel(new GridLayout(1, 2));
        tablePanel.add(new JScrollPane(assignedTable));
        tablePanel.add(new JScrollPane(availableTable));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        panel.add(leaderPanel, BorderLayout.NORTH);
        panel.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
            new JScrollPane(assignedTable), 
            new JScrollPane(availableTable)), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void updateLecturerTables(JComboBox<String> leaderCombo, DefaultTableModel assignedModel, 
                                            DefaultTableModel availableModel, java.util.ArrayList<AcademicLeader> leaders) {
        assignedModel.setRowCount(0);
        availableModel.setRowCount(0);

        if (leaderCombo.getSelectedItem() == null) return;

        String selected = (String) leaderCombo.getSelectedItem();
        String leaderEmail = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
        AcademicLeader selectedLeader = leaders.stream()
            .filter(al -> al.getEmail().equals(leaderEmail))
            .findFirst().orElse(null);

        if (selectedLeader == null) return;

        java.util.ArrayList<String> allLecturerEmails = new java.util.ArrayList<>();
        for (User u : UserManager.users) {
            if (u instanceof Lecturer) {
                allLecturerEmails.add(u.getEmail());
            }
        }

        java.util.ArrayList<String> assignedEmails = selectedLeader.getLecturerEmails();
        for (String email : assignedEmails) {
            assignedModel.addRow(new Object[]{email});
        }

        for (String email : allLecturerEmails) {
            boolean isAssigned = false;
            for (AcademicLeader al : leaders) {
                if (al.getLecturerEmails().contains(email)) {
                    isAssigned = true;
                    break;
                }
            }
            if (!isAssigned || assignedEmails.contains(email)) {
                if (!assignedEmails.contains(email)) {
                    availableModel.addRow(new Object[]{email});
                }
            }
        }
    }

    public static void showUpdateGradingSystemDialog(JFrame parent, AdminStaff admin) {
        GradingSystemManager.loadFromFile();
        GradingSystem gs = GradingSystemManager.getGradingSystem();

        JDialog dialog = new JDialog(parent, "Update Letter Grade System", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Info label
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><b>Percentage Formula:</b> (marks / totalMarks) × 100<br/><b>Enter minimum percentage for each grade (0 - 100)</b></html>");
        panel.add(infoLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("A_MIN (Excellent):"), gbc);
        gbc.gridx = 1;
        JTextField aMinField = new JTextField(String.format("%.1f", gs.getAMin()), 10);
        panel.add(aMinField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("B_MIN (Good):"), gbc);
        gbc.gridx = 1;
        JTextField bMinField = new JTextField(String.format("%.1f", gs.getBMin()), 10);
        panel.add(bMinField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("C_MIN (Satisfactory):"), gbc);
        gbc.gridx = 1;
        JTextField cMinField = new JTextField(String.format("%.1f", gs.getCMin()), 10);
        panel.add(cMinField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("D_MIN (Pass):"), gbc);
        gbc.gridx = 1;
        JTextField dMinField = new JTextField(String.format("%.1f", gs.getDMin()), 10);
        panel.add(dMinField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("E_MIN (Marginal):"), gbc);
        gbc.gridx = 1;
        JTextField eMinField = new JTextField(String.format("%.1f", gs.getEMin()), 10);
        panel.add(eMinField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JLabel failNote = new JLabel("(Anything below E_MIN is F - Fail)");
        failNote.setForeground(new java.awt.Color(150, 50, 50));
        panel.add(failNote, gbc);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                double newA = Double.parseDouble(aMinField.getText().trim());
                double newB = Double.parseDouble(bMinField.getText().trim());
                double newC = Double.parseDouble(cMinField.getText().trim());
                double newD = Double.parseDouble(dMinField.getText().trim());
                double newE = Double.parseDouble(eMinField.getText().trim());

                if (newA <= 100 && newA > newB) {
                    gs.setAMin(newA);
                } else {
                    JOptionPane.showMessageDialog(dialog, String.format("Invalid A_MIN! Must be > B_MIN (%.1f) and <= 100.", newB), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (newB < newA && newB > newC && newB >= 0) {
                    gs.setBMin(newB);
                } else {
                    JOptionPane.showMessageDialog(dialog, String.format("Invalid B_MIN! Must be < A_MIN (%.1f) and > C_MIN (%.1f).", newA, newC), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (newC < newB && newC > newD && newC >= 0) {
                    gs.setCMin(newC);
                } else {
                    JOptionPane.showMessageDialog(dialog, String.format("Invalid C_MIN! Must be < B_MIN (%.1f) and > D_MIN (%.1f).", newB, newD), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (newD < newC && newD > newE && newD >= 0) {
                    gs.setDMin(newD);
                } else {
                    JOptionPane.showMessageDialog(dialog, String.format("Invalid D_MIN! Must be < C_MIN (%.1f) and > E_MIN (%.1f).", newC, newE), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (newE < newD && newE >= 0) {
                    gs.setEMin(newE);
                } else {
                    JOptionPane.showMessageDialog(dialog, String.format("Invalid E_MIN! Must be < D_MIN (%.1f) and >= 0.", newD), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                GradingSystemManager.setGradingSystem(gs);
                JOptionPane.showMessageDialog(dialog, "Grading system updated successfully!");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input! Please enter valid numbers (e.g., 80.0).", "Error", JOptionPane.ERROR_MESSAGE);
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

    public static void showManageClassesDialog(JFrame parent, AdminStaff admin) {
        ModuleManager.loadAllModules();
        ClassManager.loadFromFile();

        JDialog dialog = new JDialog(parent, "Manage Classes", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parent);

        String[] columns = {"Class Code", "Module Code", "Time", "Classroom"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (ClassGroup cg : ClassManager.classGroups) {
            model.addRow(new Object[]{cg.getClassCode(), cg.getModule().getCode(), cg.getTime(), cg.getClassroom()});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Class");
        JButton editButton = new JButton("Edit Class");
        JButton deleteButton = new JButton("Delete Class");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        addButton.addActionListener(e -> {
            showAddClassDialog(dialog, admin);
            refreshClassTable(model);
        });

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a class to edit!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String classCode = (String) model.getValueAt(row, 0);
            showEditClassDialog(dialog, admin, classCode);
            refreshClassTable(model);
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a class to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String classCode = (String) model.getValueAt(row, 0);
            ClassGroup cg = ClassManager.findByClassCode(classCode);
            if (cg != null) {
                int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete this class?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ClassManager.deleteClassGroup(cg);
                    JOptionPane.showMessageDialog(dialog, "Class deleted successfully!");
                    refreshClassTable(model);
                }
            }
        });

        refreshButton.addActionListener(e -> refreshClassTable(model));
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void refreshClassTable(DefaultTableModel model) {
        ClassManager.loadFromFile();
        model.setRowCount(0);
        for (ClassGroup cg : ClassManager.classGroups) {
            model.addRow(new Object[]{cg.getClassCode(), cg.getModule().getCode(), cg.getTime(), cg.getClassroom()});
        }
    }

    public static void showAddClassDialog(Window parent, AdminStaff admin) {
        // Load data to get current codes
        ClassManager.loadFromFile();
        ModuleManager.loadAllModules();
        
        // Calculate next class code (e.g., C1, C2, C3 -> C4)
        String nextClassCode = getNextClassCode();
        
        // Get all available module codes for dropdown
        java.util.ArrayList<String> moduleCodes = new java.util.ArrayList<>();
        for (Module m : ModuleManager.modules) {
            moduleCodes.add(m.getCode());
        }
        java.util.Collections.sort(moduleCodes);
        
        // Check if there are any modules
        if (moduleCodes.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No modules available. Please add a module first.", "No Modules", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(parent, "Add Class", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Class Code:"), gbc);
        gbc.gridx = 1;
        JTextField classCodeField = new JTextField(nextClassCode, 20);
        classCodeField.setEditable(false);
        classCodeField.setBackground(new Color(240, 240, 240));
        panel.add(classCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Module Code:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> moduleCodeComboBox = new JComboBox<>(moduleCodes.toArray(new String[0]));
        moduleCodeComboBox.setPreferredSize(new Dimension(200, 25));
        panel.add(moduleCodeComboBox, gbc);

        // Day selection
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Day:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> dayComboBox = new JComboBox<>(new String[]{
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        });
        dayComboBox.setPreferredSize(new Dimension(200, 25));
        panel.add(dayComboBox, gbc);

        // Time selection panel
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 1;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        // Start hour
        JComboBox<Integer> startHourBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            startHourBox.addItem(i);
        }
        startHourBox.setSelectedItem(2);
        startHourBox.setPreferredSize(new Dimension(60, 25));
        
        // Start am/pm
        JComboBox<String> startAmPmBox = new JComboBox<>(new String[]{"am", "pm"});
        startAmPmBox.setSelectedItem("pm");
        startAmPmBox.setPreferredSize(new Dimension(55, 25));
        
        JLabel toLabel = new JLabel("to");
        
        // End hour
        JComboBox<Integer> endHourBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            endHourBox.addItem(i);
        }
        endHourBox.setSelectedItem(4);
        endHourBox.setPreferredSize(new Dimension(60, 25));
        
        // End am/pm
        JComboBox<String> endAmPmBox = new JComboBox<>(new String[]{"am", "pm"});
        endAmPmBox.setSelectedItem("pm");
        endAmPmBox.setPreferredSize(new Dimension(55, 25));
        
        timePanel.add(startHourBox);
        timePanel.add(startAmPmBox);
        timePanel.add(toLabel);
        timePanel.add(endHourBox);
        timePanel.add(endAmPmBox);
        panel.add(timePanel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Classroom:"), gbc);
        gbc.gridx = 1;
        JTextField classroomField = new JTextField(20);
        panel.add(classroomField, gbc);

        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        addButton.addActionListener(e -> {
            String classCode = classCodeField.getText().trim();
            String moduleCode = (String) moduleCodeComboBox.getSelectedItem();
            if (moduleCode == null) {
                moduleCode = "";
            }
            
            // Build time string from selections: "Day StartHourStartAmPm-EndHourEndAmPm"
            String day = (String) dayComboBox.getSelectedItem();
            int startHour = (Integer) startHourBox.getSelectedItem();
            String startAmPm = (String) startAmPmBox.getSelectedItem();
            int endHour = (Integer) endHourBox.getSelectedItem();
            String endAmPm = (String) endAmPmBox.getSelectedItem();
            String time = String.format("%s %d%s-%d%s", day, startHour, startAmPm, endHour, endAmPm);
            
            String classroom = classroomField.getText().trim();

            if (classCode.isEmpty() || moduleCode.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in class code and module code!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Module module = ModuleManager.findModuleByCode(moduleCode);
            if (module == null) {
                JOptionPane.showMessageDialog(dialog, "Module not found. Please add the module first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ClassGroup existing = ClassManager.findByClassCode(classCode);
            if (existing != null) {
                JOptionPane.showMessageDialog(dialog, "Class code already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ClassGroup cg = new ClassGroup(classCode, module, time, classroom);
            ClassManager.addClassGroup(cg);
            JOptionPane.showMessageDialog(dialog, "Class added successfully!");
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

    public static void showEditClassDialog(Window parent, AdminStaff admin, String classCode) {
        ClassGroup cg = ClassManager.findByClassCode(classCode);
        if (cg == null) {
            JOptionPane.showMessageDialog(parent, "Class not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parent, "Edit Class", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Class Code:"), gbc);
        gbc.gridx = 1;
        JTextField classCodeField = new JTextField(cg.getClassCode(), 20);
        panel.add(classCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Module Code:"), gbc);
        gbc.gridx = 1;
        JTextField moduleCodeField = new JTextField(cg.getModule().getCode(), 20);
        panel.add(moduleCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 1;
        JTextField timeField = new JTextField(cg.getTime() != null ? cg.getTime() : "", 20);
        panel.add(timeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Classroom:"), gbc);
        gbc.gridx = 1;
        JTextField classroomField = new JTextField(cg.getClassroom() != null ? cg.getClassroom() : "", 20);
        panel.add(classroomField, gbc);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String newClassCode = classCodeField.getText().trim();
            String newModuleCode = moduleCodeField.getText().trim();
            String newTime = timeField.getText().trim();
            String newClassroom = classroomField.getText().trim();

            if (!newClassCode.isEmpty()) {
                cg.setClassCode(newClassCode);
            }
            if (!newModuleCode.isEmpty()) {
                Module newModule = ModuleManager.findModuleByCode(newModuleCode);
                if (newModule != null) {
                    cg.setModule(newModule);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Module not found. Module not updated.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (!newTime.isEmpty()) {
                cg.setTime(newTime);
            }
            if (!newClassroom.isEmpty()) {
                cg.setClassroom(newClassroom);
            }

            ClassManager.updateClassGroup(cg);
            JOptionPane.showMessageDialog(dialog, "Class updated successfully!");
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

    /**
     * Gets the next class code by incrementing from existing codes (e.g., C1, C2, C3 -> C4)
     */
    private static String getNextClassCode() {
        int maxNumber = 0;
        
        for (ClassGroup cg : ClassManager.classGroups) {
            String code = cg.getClassCode().trim();
            // Check if code starts with "C" followed by digits
            if (code.length() > 1 && code.toUpperCase().startsWith("C")) {
                try {
                    String numStr = code.substring(1);
                    int num = Integer.parseInt(numStr);
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException e) {
                    // Skip if not a valid number after "C"
                }
            }
        }
        
        return "C" + (maxNumber + 1);
    }

    public static void showManageStudentModulesDialog(JFrame parent, AdminStaff admin) {
        ModuleManager.loadAllModules();
        UserManager.loadFromFile();
        ClassManager.loadFromFile();

        JDialog dialog = new JDialog(parent, "Manage Student Modules", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Manage Student Module Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Student Email
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student Email:"), gbc);
        gbc.gridx = 1;
        JTextField studentEmailField = new JTextField(25);
        formPanel.add(studentEmailField, gbc);

        // Search button
        JButton searchButton = new JButton("Search Student");
        gbc.gridx = 2;
        formPanel.add(searchButton, gbc);

        // Current Module
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Current Modules:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JList<String> moduleList = new JList<>();
        moduleList.setPreferredSize(new Dimension(300, 80));
        JScrollPane moduleScroll = new JScrollPane(moduleList);
        formPanel.add(moduleScroll, gbc);
        gbc.gridwidth = 1;

        // New Module (for switch operation)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("New Module Code:"), gbc);
        gbc.gridx = 1;
        JTextField newModuleField = new JTextField(25);
        formPanel.add(newModuleField, gbc);

        // Operation buttons
        JButton removeButton = new JButton("Remove Selected");
        JButton switchButton = new JButton("Switch Module");

        removeButton.addActionListener(e -> {
            String studentEmail = studentEmailField.getText().trim();
            String selectedModule = moduleList.getSelectedValue();

            if (studentEmail.isEmpty() || selectedModule == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a student and module.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student student = (Student) UserManager.findByEmail(studentEmail);
            if (student == null) {
                JOptionPane.showMessageDialog(dialog, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(dialog, 
                "Remove " + student.getName() + " from module " + selectedModule + "?", 
                "Confirm Remove", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from classes first
                ClassManager.removeStudentFromModuleClasses(studentEmail, selectedModule);
                
                // Remove from module
                boolean success = ModuleManager.removeStudentFromModule(selectedModule, studentEmail);
                if (success) {
                    student.getRegisteredModules().remove(selectedModule);
                    UserManager.saveToFile();
                    JOptionPane.showMessageDialog(dialog, "Student removed from module successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh list
                    ModuleManager.loadAllModules();
                    DefaultListModel<String> model = new DefaultListModel<>();
                    for (String mod : student.getRegisteredModules()) {
                        model.addElement(mod);
                    }
                    moduleList.setModel(model);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to remove student from module.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        switchButton.addActionListener(e -> {
            String studentEmail = studentEmailField.getText().trim();
            String selectedModule = moduleList.getSelectedValue();
            String newModule = newModuleField.getText().trim().toUpperCase();

            if (studentEmail.isEmpty() || selectedModule == null || newModule.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields and select a module.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student student = (Student) UserManager.findByEmail(studentEmail);
            if (student == null) {
                JOptionPane.showMessageDialog(dialog, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (student.getRegisteredModules().contains(newModule)) {
                JOptionPane.showMessageDialog(dialog, "Student is already registered in module " + newModule + ".", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Entity.Module newModuleObj = ModuleManager.findModuleByCode(newModule);
            if (newModuleObj == null) {
                JOptionPane.showMessageDialog(dialog, "Module " + newModule + " does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(dialog, 
                "Switch " + student.getName() + " from " + selectedModule + " to " + newModule + "?", 
                "Confirm Switch", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from classes of old module first
                ClassManager.removeStudentFromModuleClasses(studentEmail, selectedModule);
                
                // Switch modules
                boolean success = ModuleManager.switchStudentModule(selectedModule, newModule, studentEmail);
                if (success) {
                    student.getRegisteredModules().remove(selectedModule);
                    student.getRegisteredModules().add(newModule);
                    UserManager.saveToFile();
                    JOptionPane.showMessageDialog(dialog, "Student switched successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh list
                    ModuleManager.loadAllModules();
                    DefaultListModel<String> model = new DefaultListModel<>();
                    for (String mod : student.getRegisteredModules()) {
                        model.addElement(mod);
                    }
                    moduleList.setModel(model);
                    newModuleField.setText("");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to switch student module.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        searchButton.addActionListener(e -> {
            String studentEmail = studentEmailField.getText().trim();
            
            if (studentEmail.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a student email.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = UserManager.findByEmail(studentEmail);
            if (user == null || !(user instanceof Student)) {
                JOptionPane.showMessageDialog(dialog, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
                moduleList.setModel(new DefaultListModel<>());
                return;
            }

            Student student = (Student) user;
            DefaultListModel<String> model = new DefaultListModel<>();
            for (String mod : student.getRegisteredModules()) {
                model.addElement(mod);
            }
            moduleList.setModel(model);

            if (student.getRegisteredModules().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "This student has no registered modules.", "No Modules", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(removeButton, gbc);
        gbc.gridx = 1;
        formPanel.add(switchButton, gbc);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        gbc.gridx = 2;
        formPanel.add(closeButton, gbc);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.setVisible(true);
    }

}


