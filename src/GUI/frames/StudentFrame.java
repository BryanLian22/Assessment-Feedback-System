package GUI.frames;

import Users.Student;
import Users.User;
import IOManage.UserManager;
import IOManage.ModuleManager;
import IOManage.ClassManager;
import IOManage.CommentManager;
import IOManage.ReportManager;
import Entity.Module;
import Entity.ClassGroup;
import Entity.Comment;
import Users.Lecturer;
import Users.AcademicLeader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StudentFrame extends JFrame {
    private Student student;
    private JPanel contentPanel;
    private JButton activeButton;
    
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color SIDEBAR_COLOR = new Color(52, 73, 94);
    private static final Color SIDEBAR_HOVER = new Color(70, 90, 110);
    private static final Color SIDEBAR_ACTIVE = new Color(41, 128, 185);

    public StudentFrame(Student student) {
        this.student = student;
        initializeFrame();
        applyModernLook();
    }

    private void initializeFrame() {
        setTitle("APU Assessment Feedback System - Student Dashboard");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Top Header Bar
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // Main Container with Sidebar and Content
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);

        // Left Sidebar
        JPanel sidebar = createSidebar();
        mainContainer.add(sidebar, BorderLayout.WEST);

        // Right Content Area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        showDashboardContent();
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PANEL_COLOR);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        topBar.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel titleLabel = new JLabel("APU Assessment Feedback System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 73, 94));
        topBar.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Welcome, " + student.getName() + " | Student");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(127, 140, 141));
        topBar.add(userLabel, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, SIDEBAR_COLOR, 0, getHeight(), new Color(44, 62, 80));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Dashboard Button
        JButton dashboardBtn = createSidebarButton("Dashboard", true);
        dashboardBtn.addActionListener(e -> {
            setActiveButton(dashboardBtn);
            showDashboardContent();
        });
        sidebar.add(dashboardBtn);

        // Profile Section (Collapsible)
        CollapsibleSection profileSection = new CollapsibleSection("Profile");
        JButton editProfileBtn = createSidebarButton("Edit Profile", false);
        editProfileBtn.addActionListener(e -> {
            setActiveButton(editProfileBtn);
            showEditProfilePanel();
        });
        profileSection.addButton(editProfileBtn);
        sidebar.add(profileSection);

        // Registration Section (Collapsible)
        CollapsibleSection registrationSection = new CollapsibleSection("Registration");
        JButton registerClassBtn = createSidebarButton("Register Class", false);
        registerClassBtn.addActionListener(e -> {
            setActiveButton(registerClassBtn);
            showRegisterClassPanel();
        });
        registrationSection.addButton(registerClassBtn);
        sidebar.add(registrationSection);

        // View Section (Collapsible)
        CollapsibleSection viewSection = new CollapsibleSection("View");
        JButton viewTimetableBtn = createSidebarButton("View Timetable", false);
        viewTimetableBtn.addActionListener(e -> {
            setActiveButton(viewTimetableBtn);
            showViewTimetablePanel();
        });
        JButton viewResultsBtn = createSidebarButton("View Results", false);
        viewResultsBtn.addActionListener(e -> {
            setActiveButton(viewResultsBtn);
            showViewResultsPanel();
        });
        JButton viewAssessmentsBtn = createSidebarButton("View Assessments", false);
        viewAssessmentsBtn.addActionListener(e -> {
            setActiveButton(viewAssessmentsBtn);
            showViewAssessmentsPanel();
        });
        viewSection.addButton(viewTimetableBtn);
        viewSection.addButton(viewResultsBtn);
        viewSection.addButton(viewAssessmentsBtn);
        sidebar.add(viewSection);

        // Feedback Section (Collapsible)
        CollapsibleSection feedbackSection = new CollapsibleSection("Feedback");
        JButton commentLecturerBtn = createSidebarButton("Comment Lecturer", false);
        commentLecturerBtn.addActionListener(e -> {
            setActiveButton(commentLecturerBtn);
            showCommentLecturerPanel();
        });
        feedbackSection.addButton(commentLecturerBtn);
        sidebar.add(feedbackSection);

        // Logout Button at bottom
        sidebar.add(Box.createVerticalGlue());
        JButton logoutBtn = createSidebarButton("Logout", false);
        logoutBtn.setForeground(new Color(231, 76, 60));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Logout", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new GUI.LoginFrame().setVisible(true);
            }
        });
        sidebar.add(logoutBtn);

        return sidebar;
    }

    // Collapsible Section Class
    private class CollapsibleSection extends JPanel {
        private JButton headerButton;
        private JPanel contentPanel;
        private boolean isExpanded = false;

        public CollapsibleSection(String title) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            setAlignmentX(Component.LEFT_ALIGNMENT);

            headerButton = new JButton(title + " ▼") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    if (getModel().isRollover()) {
                        g2.setColor(SIDEBAR_HOVER);
                    } else {
                        g2.setColor(new Color(0, 0, 0, 0));
                    }
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            };
            headerButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            headerButton.setForeground(new Color(200, 200, 200));
            headerButton.setHorizontalAlignment(SwingConstants.LEFT);
            headerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            headerButton.setContentAreaFilled(false);
            headerButton.setFocusPainted(false);
            headerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            headerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            headerButton.addActionListener(e -> toggle());

            contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setOpaque(false);
            contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.setVisible(false);

            add(headerButton);
            add(contentPanel);
        }

        public void addButton(JButton button) {
            contentPanel.add(button);
        }

        private void toggle() {
            isExpanded = !isExpanded;
            contentPanel.setVisible(isExpanded);
            headerButton.setText(headerButton.getText().replace("▼", "").replace("▲", "") + (isExpanded ? " ▲" : " ▼"));
            revalidate();
            repaint();
        }
    }

    private JButton createSidebarButton(String text, boolean isActive) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (this == activeButton || isActive) {
                    g2.setColor(SIDEBAR_ACTIVE);
                } else if (getModel().isRollover()) {
                    g2.setColor(SIDEBAR_HOVER);
                } else {
                    g2.setColor(new Color(0, 0, 0, 0));
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 20));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (isActive) {
            activeButton = button;
        }
        
        return button;
    }

    private void setActiveButton(JButton button) {
        if (activeButton != null) {
            activeButton.repaint();
        }
        activeButton = button;
        button.repaint();
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        // Dynamic sizing: add padding around text
        button.setMargin(new Insets(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void showDashboardContent() {
        contentPanel.removeAll();
        
        // Welcome Header
        JPanel welcomePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        welcomePanel.setPreferredSize(new Dimension(getWidth(), 120));
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel welcomeTitle = new JLabel("Student Dashboard");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeTitle.setForeground(Color.WHITE);
        welcomePanel.add(welcomeTitle, BorderLayout.CENTER);
        
        JLabel welcomeSubtitle = new JLabel("Welcome, " + student.getName() + "!");
        welcomeSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeSubtitle.setForeground(new Color(255, 255, 255, 220));
        welcomePanel.add(welcomeSubtitle, BorderLayout.SOUTH);
        
        contentPanel.add(welcomePanel, BorderLayout.NORTH);

        // Info Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        cardsPanel.setBackground(BACKGROUND_COLOR);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        cardsPanel.add(createInfoCard("Modules", String.valueOf(student.getRegisteredModules().size()), PRIMARY_COLOR));
        cardsPanel.add(createInfoCard("Classes", String.valueOf(student.getRegisteredClasses().size()), SECONDARY_COLOR));
        cardsPanel.add(createInfoCard("Email", student.getEmail().length() > 20 ? student.getEmail().substring(0, 20) + "..." : student.getEmail(), new Color(155, 89, 182)));
        
        contentPanel.add(cardsPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createInfoCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(127, 140, 141));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    // Panel Methods
    private void showEditProfilePanel() {
        contentPanel.removeAll();
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Edit Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(student.getName(), 25);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(student.getEmail(), 25);
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField currentPwField = new JPasswordField(25);
        panel.add(currentPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField newPwField = new JPasswordField(25);
        panel.add(newPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField confirmPwField = new JPasswordField(25);
        panel.add(confirmPwField, gbc);

        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        saveButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String currentPw = new String(currentPwField.getPassword());
            String newPw = new String(newPwField.getPassword());
            String confirmPw = new String(confirmPwField.getPassword());

            boolean emailChanged = false;
            
            if (!newName.isEmpty()) {
                student.setName(newName);
            }
            
            if (!newEmail.isEmpty() && !newEmail.equals(student.getEmail())) {
                String oldEmail = student.getEmail();
                
                if (IOManage.EmailUpdateManager.emailExistsInSystem(newEmail)) {
                    JOptionPane.showMessageDialog(this, 
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
                    JOptionPane.showMessageDialog(this, 
                        "✗ Failed to update email.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!newPw.isEmpty()) {
                if (IOManage.PasswordUtils.verifyPassword(currentPw, student.getPassword())) {
                    if (newPw.equals(confirmPw)) {
                        student.setPassword(IOManage.PasswordUtils.hashPassword(newPw));
                        JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!emailChanged) {
                UserManager.updateUser(student);
                UserManager.saveToFile();
            } else {
                UserManager.loadFromFile();
            }
            
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        gbc.gridx = 1; gbc.gridy = 6; gbc.anchor = GridBagConstraints.EAST;
        panel.add(saveButton, gbc);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Button Renderer for class table cells
    private class ClassButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ClassButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Check if class is already registered
            boolean isRegistered = false;
            if (row < table.getModel().getRowCount()) {
                String status = (String) table.getModel().getValueAt(row, 6);
                isRegistered = "Registered".equals(status);
            }
            
            if (isRegistered) {
                setBackground(new Color(149, 165, 166));
                setForeground(Color.BLACK);
                setEnabled(false);
                setText("Registered");
            } else {
                setBackground(PRIMARY_COLOR);
                setForeground(Color.BLACK);
                setEnabled(true);
                setText("Register");
            }
            
            return this;
        }
    }

    // Button Editor for class table cells
    private class ClassButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private Student student;
        private StudentFrame frame;
        private JTable table;
        private int currentRow;

        public ClassButtonEditor(JCheckBox checkBox, Student student, StudentFrame frame) {
            super(checkBox);
            this.student = student;
            this.frame = frame;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table;
            this.currentRow = row;
            
            // Check if already registered
            String status = (String) table.getModel().getValueAt(row, 6);
            if ("Registered".equals(status)) {
                button.setEnabled(false);
                button.setText("Registered");
                button.setBackground(new Color(149, 165, 166));
                button.setForeground(Color.BLACK);
            } else {
                button.setEnabled(true);
                label = (value == null) ? "Register" : value.toString();
                button.setText(label);
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(Color.BLACK);
            }
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed && table != null && currentRow >= 0) {
                String classCode = (String) table.getModel().getValueAt(currentRow, 0);
                
                // Check if already registered
                if (student.getRegisteredClasses().contains(classCode)) {
                    JOptionPane.showMessageDialog(frame, "You already registered this class.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    ClassGroup cg = ClassManager.findByClassCode(classCode);
                    if (cg == null) {
                        JOptionPane.showMessageDialog(frame, "Class does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        String moduleCode = cg.getModule().getCode();
                        if (!student.getRegisteredModules().contains(moduleCode)) {
                            JOptionPane.showMessageDialog(frame, "You have not registered the module for this class.", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            Module m = ModuleManager.findModuleByCode(moduleCode);
                            if (m == null || !m.getStudentEmails().contains(student.getEmail())) {
                                JOptionPane.showMessageDialog(frame, "You are not registered under this module.", "Error", JOptionPane.ERROR_MESSAGE);
                            } else {
                                if (cg.getStudentEmails() != null && cg.getStudentEmails().contains(student.getEmail())) {
                                    JOptionPane.showMessageDialog(frame, "You are already registered in this class.", "Error", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    // ✅ ENFORCE: Remove from other classes in same module first
                                    ClassManager.removeStudentFromModuleClasses(student.getEmail(), moduleCode);
                                    
                                    cg.addStudent(student.getEmail());
                                    ClassManager.updateClassGroup(cg);
                                    student.getRegisteredClasses().add(classCode);
                                    UserManager.saveToFile();
                                    JOptionPane.showMessageDialog(frame, "Class registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    frame.showRegisterClassPanel(); // Refresh the panel
                                }
                            }
                        }
                    }
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private void showRegisterClassPanel() {
        contentPanel.removeAll();
        ModuleManager.loadAllModules();
        ClassManager.loadFromFile();
        UserManager.loadFromFile();
        
        // Reload registered modules from file (clear + repopulate to ensure accurate state)
        student.getRegisteredModules().clear();
        for (Module m : ModuleManager.modules) {
            if (m.getStudentEmails().contains(student.getEmail())) {
                student.getRegisteredModules().add(m.getCode());
            }
        }
        // Reload registered classes from file (clear + repopulate)
        student.getRegisteredClasses().clear();
        for (ClassGroup cg : ClassManager.classGroups) {
            if (cg.getStudentEmails() != null && cg.getStudentEmails().contains(student.getEmail())) {
                student.getRegisteredClasses().add(cg.getClassCode());
            }
        }

        if (student.getRegisteredModules().isEmpty()) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(PANEL_COLOR);
            panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            JLabel messageLabel = new JLabel("You must register modules before registering classes.", JLabel.CENTER);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(messageLabel, BorderLayout.CENTER);
            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Register Class");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Filter Panel (left) + Refresh (right)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Lecturer Name Filter
        JLabel lecturerLabel = new JLabel("Filter by Lecturer:");
        lecturerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JComboBox<String> lecturerFilter = new JComboBox<>();
        lecturerFilter.addItem("All Lecturers");
        lecturerFilter.setPreferredSize(new Dimension(200, 30));
        
        // Day Filter
        JLabel dayLabel = new JLabel("Filter by Day:");
        dayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JComboBox<String> dayFilter = new JComboBox<>();
        dayFilter.addItem("All Days");
        dayFilter.addItem("Monday");
        dayFilter.addItem("Tuesday");
        dayFilter.addItem("Wednesday");
        dayFilter.addItem("Thursday");
        dayFilter.addItem("Friday");
        dayFilter.addItem("Saturday");
        dayFilter.addItem("Sunday");
        dayFilter.setPreferredSize(new Dimension(150, 30));
        
        filterPanel.add(lecturerLabel);
        filterPanel.add(lecturerFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(dayLabel);
        filterPanel.add(dayFilter);

        // Add Refresh button to re-load data and rebuild panel
        JButton refreshBtn = createStyledButton("Refresh", new Color(39, 174, 96));
        refreshBtn.setPreferredSize(new Dimension(110, 30));
        refreshBtn.addActionListener(e -> {
            // reload all data
            ModuleManager.loadAllModules();
            ClassManager.loadFromFile();
            UserManager.loadFromFile();

            // repopulate student's registered modules and classes
            student.getRegisteredModules().clear();
            for (Module m : ModuleManager.modules) {
                if (m.getStudentEmails().contains(student.getEmail())) {
                    student.getRegisteredModules().add(m.getCode());
                }
            }
            student.getRegisteredClasses().clear();
            for (ClassGroup cg : ClassManager.classGroups) {
                if (cg.getStudentEmails() != null && cg.getStudentEmails().contains(student.getEmail())) {
                    student.getRegisteredClasses().add(cg.getClassCode());
                }
            }

            // rebuild the panel to reflect fresh state
            showRegisterClassPanel();
        });

        // Place refresh button to the right side of the filter area
        JPanel filterWrapper = new JPanel(new BorderLayout());
        filterWrapper.setBackground(PANEL_COLOR);
        filterWrapper.add(filterPanel, BorderLayout.WEST);
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        refreshPanel.setBackground(PANEL_COLOR);
        refreshPanel.add(refreshBtn);
        filterWrapper.add(refreshPanel, BorderLayout.EAST);

        // Table Panel for all available classes
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(PANEL_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel tableTitleLabel = new JLabel("All Available Classes");
        tableTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitleLabel.setForeground(new Color(52, 73, 94));
        tablePanel.add(tableTitleLabel, BorderLayout.NORTH);

        // Collect all unique lecturer names from classes
        ArrayList<String> allLecturerNames = new ArrayList<>();
        for (ClassGroup cg : ClassManager.classGroups) {
            // Only include classes for modules the student is registered in
            if (cg.getModule() != null && student.getRegisteredModules().contains(cg.getModule().getCode())) {
                if (cg.getLecturerEmails() != null && !cg.getLecturerEmails().isEmpty()) {
                    for (String email : cg.getLecturerEmails()) {
                        User user = UserManager.findByEmail(email);
                        if (user != null) {
                            String name = user.getName();
                            if (!allLecturerNames.contains(name)) {
                                allLecturerNames.add(name);
                            }
                        }
                    }
                }
            }
        }
        java.util.Collections.sort(allLecturerNames);
        for (String name : allLecturerNames) {
            lecturerFilter.addItem(name);
        }

        // Create table with columns
        String[] columns = {"Class Code", "Module Code", "Module Name", "Lecturer Name", "Venue", "Time", "Status", "Action"};
        DefaultTableModel displayModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only the Action column is editable
            }
        };

        // Store filtering data separately
        ArrayList<Object[]> allClassData = new ArrayList<>();
        ArrayList<ArrayList<String>> allLecturerNameLists = new ArrayList<>();
        ArrayList<String> allDays = new ArrayList<>();

        // Populate data for all classes (only for registered modules)
        for (ClassGroup cg : ClassManager.classGroups) {
            // Only show classes for modules the student is registered in
            if (cg.getModule() == null || !student.getRegisteredModules().contains(cg.getModule().getCode())) {
                continue;
            }
            
            boolean isRegistered = student.getRegisteredClasses().contains(cg.getClassCode())
                    || (cg.getStudentEmails() != null && cg.getStudentEmails().contains(student.getEmail()));
            String status = isRegistered ? "Registered" : "Available";
            
            // Get module info
            String moduleCode = cg.getModule().getCode();
            String moduleName = cg.getModule().getName();
            
            // Get lecturer names
            StringBuilder lecturerNames = new StringBuilder();
            ArrayList<String> lecturerNameList = new ArrayList<>();
            if (cg.getLecturerEmails() != null && !cg.getLecturerEmails().isEmpty()) {
                for (String email : cg.getLecturerEmails()) {
                    User user = UserManager.findByEmail(email);
                    if (user != null) {
                        lecturerNameList.add(user.getName());
                    }
                }
                lecturerNames.append(String.join(", ", lecturerNameList));
            } else {
                lecturerNames.append("N/A");
            }
            
            // Get venue and time
            String venue = (cg.getClassroom() != null && !cg.getClassroom().isEmpty()) ? cg.getClassroom() : "N/A";
            String time = (cg.getTime() != null && !cg.getTime().isEmpty()) ? cg.getTime() : "N/A";
            
            // Extract day from time string (e.g., "Monday 2pm-4pm" -> "Monday")
            String day = "N/A";
            if (time != null && !time.equals("N/A")) {
                String timeStr = time.trim();
                String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                for (String d : days) {
                    if (timeStr.startsWith(d)) {
                        day = d;
                        break;
                    }
                }
            }
            
            allClassData.add(new Object[]{
                cg.getClassCode(), 
                moduleCode,
                moduleName, 
                lecturerNames.toString(),
                venue,
                time,
                status, 
                "Register"
            });
            allLecturerNameLists.add(lecturerNameList);
            allDays.add(day);
        }
        
        // Function to filter and update display model
        Runnable updateFilter = () -> {
            displayModel.setRowCount(0);
            String selectedLecturer = (String) lecturerFilter.getSelectedItem();
            String selectedDay = (String) dayFilter.getSelectedItem();
            
            // Collect filtered rows
            ArrayList<Object[]> filteredRows = new ArrayList<>();
            
            for (int i = 0; i < allClassData.size(); i++) {
                ArrayList<String> lecturerNameList = allLecturerNameLists.get(i);
                String day = allDays.get(i);
                
                // Filter by lecturer
                boolean lecturerMatch = true;
                if (selectedLecturer != null && !selectedLecturer.equals("All Lecturers")) {
                    lecturerMatch = lecturerNameList.contains(selectedLecturer);
                }
                
                // Filter by day
                boolean dayMatch = true;
                if (selectedDay != null && !selectedDay.equals("All Days")) {
                    dayMatch = day.equals(selectedDay);
                }
                
                // Add row if matches both filters
                if (lecturerMatch && dayMatch) {
                    filteredRows.add(allClassData.get(i));
                }
            }
            
            // Sort by status: "Registered" first, then "Available"
            filteredRows.sort((row1, row2) -> {
                String status1 = (String) row1[6]; // Status is at index 6
                String status2 = (String) row2[6];
                
                if ("Registered".equals(status1) && "Available".equals(status2)) {
                    return -1; // Registered comes before Available
                } else if ("Available".equals(status1) && "Registered".equals(status2)) {
                    return 1; // Available comes after Registered
                } else {
                    return 0; // Same status, keep original order
                }
            });
            
            // Add sorted rows to display model
            for (Object[] row : filteredRows) {
                displayModel.addRow(row);
            }
        };
        
        // Add filter listeners
        lecturerFilter.addActionListener(e -> updateFilter.run());
        dayFilter.addActionListener(e -> updateFilter.run());
        
        // Initialize display model with all data
        updateFilter.run();

        JTable table = new JTable(displayModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setForeground(Color.BLACK);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(180);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        table.getColumnModel().getColumn(7).setPreferredWidth(100);

        // Custom renderer and editor for the Action column (Register button)
        table.getColumn("Action").setCellRenderer(new ClassButtonRenderer());
        table.getColumn("Action").setCellEditor(new ClassButtonEditor(new JCheckBox(), student, this));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Combine filter wrapper and table panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(PANEL_COLOR);
        centerPanel.add(filterWrapper, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showViewTimetablePanel() {
        contentPanel.removeAll();
        ClassManager.loadFromFile();

        ArrayList<ClassGroup> studentClasses = new ArrayList<>();
        for (ClassGroup cg : ClassManager.classGroups) {
            if (cg.getStudentEmails().contains(student.getEmail())) {
                studentClasses.add(cg);
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("My Timetable");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        panel.add(titleLabel, BorderLayout.NORTH);

        if (studentClasses.isEmpty()) {
            JLabel messageLabel = new JLabel("You are not registered in any classes yet.", JLabel.CENTER);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            messageLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
            panel.add(messageLabel, BorderLayout.CENTER);
        } else {
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
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showViewResultsPanel() {
        contentPanel.removeAll();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title panel with Module Summary button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PANEL_COLOR);
        JLabel titleLabel = new JLabel("My Results");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Add Module Summary button
        JButton viewModuleReportBtn = createStyledButton("Module Summary", new Color(155, 89, 182));
        viewModuleReportBtn.addActionListener(e -> showStudentModuleReportPanel());
        titlePanel.add(viewModuleReportBtn, BorderLayout.EAST);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Load grading system and modules
        IOManage.GradingSystemManager.loadFromFile();
        Entity.GradingSystem gradingSystem = IOManage.GradingSystemManager.getGradingSystem();
        ModuleManager.loadAllModules();

        // Load assessments to map AssessmentID -> ModuleCode
        java.util.Map<String, String> assessmentToModule = new java.util.HashMap<>();
        java.util.Map<String, Double> assessmentMaxMarks = new java.util.HashMap<>();
        File assessmentsFile = new File("data/assessments.txt");
        if (assessmentsFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(assessmentsFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        String assessmentId = parts[0].trim();
                        String moduleCode = parts[1].trim();
                        double maxMarks = Double.parseDouble(parts[5].trim());
                        assessmentToModule.put(assessmentId, moduleCode);
                        assessmentMaxMarks.put(assessmentId, maxMarks);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                // Handle error
            }
        }

        // Collect all result data first
        ArrayList<Object[]> allResultsData = new ArrayList<>();
        java.util.Set<String> moduleSet = new java.util.TreeSet<>();
        java.util.Set<String> assessmentSet = new java.util.TreeSet<>();

        File marksFile = new File("data/marks.txt");
        if (marksFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(marksFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split("\\|");
                    // Format: AssessmentID|AssessmentName|Type|LecturerEmail|StudentEmail|StudentName|Marks|Comment
                    if (parts.length >= 6) {
                        String studentEmail = parts[4].trim();
                        if (studentEmail.equalsIgnoreCase(student.getEmail())) {
                            String assessmentId = parts[0].trim();
                            String assessmentName = parts[1].trim();
                            String type = parts[2].trim();
                            double marks = parts.length > 6 ? Double.parseDouble(parts[6].trim()) : 0.0;
                            String comment = parts.length > 7 ? parts[7].trim() : "";
                            
                            // Get module code from assessment mapping
                            String moduleCode = assessmentToModule.getOrDefault(assessmentId, "N/A");
                            
                            // Get module name from ModuleManager
                            String moduleName = "N/A";
                            Module module = ModuleManager.findModuleByCode(moduleCode);
                            if (module != null) {
                                moduleName = module.getName();
                            }
                            
                            // Add to filter sets
                            if (!moduleCode.equals("N/A")) {
                                moduleSet.add(moduleCode + " - " + moduleName);
                            }
                            assessmentSet.add(assessmentName);
                            
                            // Get max marks from assessment mapping
                            double maxMarks = assessmentMaxMarks.getOrDefault(assessmentId, 100.0);
                            
                            // Calculate percentage: (marks / maxMarks) * 100
                            double percentage = Entity.GradingSystem.calculatePercentage(marks, maxMarks);
                            
                            String grade = gradingSystem.getGrade(percentage);
                            String gradeName = gradingSystem.getGradeName(percentage);
                            
                            String scoreDisplay = String.format("%.1f/%.0f", marks, maxMarks);
                            String percentDisplay = String.format("%.1f%%", percentage);
                            String moduleDisplay = moduleCode.equals("N/A") ? "N/A" : moduleCode + " - " + moduleName;
                            
                            // Store: AssessmentName, Module, Type, Score, Percentage, Grade, Classification, Comment
                            allResultsData.add(new Object[]{assessmentName, moduleDisplay, type, scoreDisplay, percentDisplay, grade, gradeName, comment});
                        }
                    }
                }
            } catch (IOException | NumberFormatException e) {
                // Handle error
            }
        }

        if (allResultsData.isEmpty()) {
            JLabel messageLabel = new JLabel("No results available yet.", JLabel.CENTER);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            messageLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
            panel.add(titlePanel, BorderLayout.NORTH);
            panel.add(messageLabel, BorderLayout.CENTER);
        } else {
            // Create filter panel
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            filterPanel.setBackground(PANEL_COLOR);
            filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 0, 10, 0)
            ));
            
            // Module filter
            JLabel moduleFilterLabel = new JLabel("Filter by Module:");
            moduleFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            filterPanel.add(moduleFilterLabel);
            
            JComboBox<String> moduleFilter = new JComboBox<>();
            moduleFilter.addItem("All Modules");
            for (String mod : moduleSet) {
                moduleFilter.addItem(mod);
            }
            moduleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            moduleFilter.setPreferredSize(new Dimension(180, 28));
            filterPanel.add(moduleFilter);
            
            // Spacer
            filterPanel.add(Box.createHorizontalStrut(20));
            
            // Assessment filter
            JLabel assessmentFilterLabel = new JLabel("Filter by Assessment:");
            assessmentFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            filterPanel.add(assessmentFilterLabel);
            
            JComboBox<String> assessmentFilter = new JComboBox<>();
            assessmentFilter.addItem("All Assessments");
            for (String assess : assessmentSet) {
                assessmentFilter.addItem(assess);
            }
            assessmentFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            assessmentFilter.setPreferredSize(new Dimension(180, 28));
            filterPanel.add(assessmentFilter);

            // Table setup
            String[] columns = {"Assessment", "Module", "Type", "Score", "Percentage", "Grade", "Classification", "Comment"};
            DefaultTableModel displayModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Filter update function
            Runnable updateFilter = () -> {
                displayModel.setRowCount(0);
                String selectedModule = (String) moduleFilter.getSelectedItem();
                String selectedAssessment = (String) assessmentFilter.getSelectedItem();
                
                for (Object[] row : allResultsData) {
                    String rowAssessment = (String) row[0];
                    String rowModule = (String) row[1];
                    
                    boolean moduleMatch = selectedModule == null || "All Modules".equals(selectedModule) || rowModule.equals(selectedModule);
                    boolean assessmentMatch = selectedAssessment == null || "All Assessments".equals(selectedAssessment) || rowAssessment.equals(selectedAssessment);
                    
                    if (moduleMatch && assessmentMatch) {
                        displayModel.addRow(row);
                    }
                }
            };
            
            // Add filter listeners
            moduleFilter.addActionListener(e -> updateFilter.run());
            assessmentFilter.addActionListener(e -> updateFilter.run());
            
            // Initialize display with all data
            updateFilter.run();

            JTable table = new JTable(displayModel);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setRowHeight(30);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            table.getTableHeader().setBackground(new Color(52, 73, 94));
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            // Set column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(150); // Assessment
            table.getColumnModel().getColumn(1).setPreferredWidth(150); // Module
            table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Type
            table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Score
            table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Percentage
            table.getColumnModel().getColumn(5).setPreferredWidth(50);  // Grade
            table.getColumnModel().getColumn(6).setPreferredWidth(120); // Classification
            table.getColumnModel().getColumn(7).setPreferredWidth(180); // Comment
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            
            // Combine header panel (title + filters)
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(PANEL_COLOR);
            headerPanel.add(titlePanel, BorderLayout.NORTH);
            headerPanel.add(filterPanel, BorderLayout.SOUTH);
            
            panel.add(headerPanel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Show student's module report with GPA for all assessments and CGPA per module
     */
    private void showStudentModuleReportPanel() {
        contentPanel.removeAll();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header with back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JButton backButton = createStyledButton("← Back to Results", new Color(127, 140, 141));
        backButton.addActionListener(e -> showViewResultsPanel());
        headerPanel.add(backButton, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("Module Summary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Get modules that have reports for this student
        ArrayList<String> modulesWithReports = IOManage.ReportManager.getModulesWithReportsForStudent(student.getEmail());
        
        if (modulesWithReports.isEmpty()) {
            JLabel noReportsLabel = new JLabel("<html><div style='text-align: center; padding: 40px; color: #7f8c8d;'>" +
                "No module reports available yet.<br><br>" +
                "Module reports are generated by Academic Leaders and will appear here<br>" +
                "once they include your assessment results.</div></html>", JLabel.CENTER);
            noReportsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(noReportsLabel, BorderLayout.CENTER);
        } else {
            // Create tabbed pane for each module
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            
            IOManage.ModuleManager.loadAllModules();
            
            for (String moduleCode : modulesWithReports) {
                Entity.Module module = IOManage.ModuleManager.findModuleByCode(moduleCode);
                String moduleName = module != null ? module.getName() : moduleCode;
                
                JPanel modulePanel = createStudentModuleReportPanel(moduleCode, moduleName);
                tabbedPane.addTab(moduleCode + " - " + moduleName, modulePanel);
            }
            
            panel.add(tabbedPane, BorderLayout.CENTER);
        }
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Create a panel showing the student's report for a specific module
     */
    private JPanel createStudentModuleReportPanel(String moduleCode, String moduleName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Load report data for this student and module
        Entity.Report report = IOManage.ReportManager.loadReportWithDetailsForStudent(student.getEmail(), moduleCode);
        
        if (report == null || report.getClassReports().isEmpty()) {
            JLabel noDataLabel = new JLabel("No data available for this module.", JLabel.CENTER);
            noDataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(noDataLabel, BorderLayout.CENTER);
            return panel;
        }
        
        // Get student data from the report
        Entity.Report.StudentReportData studentData = null;
        for (Entity.Report.ClassReportData classReport : report.getClassReports()) {
            for (Entity.Report.StudentReportData sd : classReport.getStudentReports()) {
                if (sd.getStudentEmail().equalsIgnoreCase(student.getEmail())) {
                    studentData = sd;
                    break;
                }
            }
        }
        
        if (studentData == null || studentData.getAssessmentMarks().isEmpty()) {
            JLabel noDataLabel = new JLabel("No assessment data found for you in this module.", JLabel.CENTER);
            noDataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(noDataLabel, BorderLayout.CENTER);
            return panel;
        }
        
        // Summary cards at top
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        summaryPanel.setBackground(PANEL_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Calculate overall percentage
        double totalPercentage = 0;
        for (Entity.Report.AssessmentMark mark : studentData.getAssessmentMarks()) {
            totalPercentage += mark.getPercentage();
        }
        double avgPercentage = studentData.getAssessmentMarks().size() > 0 ? 
            totalPercentage / studentData.getAssessmentMarks().size() : 0;
        
        // Get grade for average percentage
        IOManage.GradingSystemManager.loadFromFile();
        Entity.GradingSystem gradingSystem = IOManage.GradingSystemManager.getGradingSystem();
        String overallGrade = gradingSystem.getGrade(avgPercentage);
        String overallGradeName = gradingSystem.getGradeName(avgPercentage);
        
        summaryPanel.add(createStudentSummaryCard("Average %", String.format("%.1f%%", avgPercentage), PRIMARY_COLOR));
        summaryPanel.add(createStudentSummaryCard("Overall Grade", overallGrade + " (" + overallGradeName + ")", SECONDARY_COLOR));
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Assessment details table
        String[] columns = {"Assessment", "Type", "Score", "Percentage", "Grade"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Entity.Report.AssessmentMark mark : studentData.getAssessmentMarks()) {
            model.addRow(new Object[]{
                mark.getAssessmentName(),
                mark.getAssessmentType(),
                String.format("%.1f / %.0f", mark.getMarks(), mark.getMaxMarks()),
                String.format("%.1f%%", mark.getPercentage()),
                mark.getGrade()
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        // Add legend panel at bottom
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        legendPanel.setBackground(new Color(248, 249, 250));
        legendPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(PANEL_COLOR);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(legendPanel, BorderLayout.SOUTH);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStudentSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(PANEL_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(127, 140, 141));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private void showCommentLecturerPanel() {
        contentPanel.removeAll();
        CommentManager.loadFromFile();
        ClassManager.loadFromFile();
        UserManager.loadFromFile();
        
        // Get all classes the student is registered in
        ArrayList<ClassGroup> studentClasses = new ArrayList<>();
        for (ClassGroup cg : ClassManager.classGroups) {
            if (cg.getStudentEmails() != null && cg.getStudentEmails().contains(student.getEmail())) {
                studentClasses.add(cg);
            }
        }
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Comment Lecturer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Lecturer Email & Name:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> lecturerCombo = new JComboBox<>();
        lecturerCombo.setPreferredSize(new Dimension(300, 25));
        
        // Collect unique lecturer emails from student's registered classes
        ArrayList<String> lecturerEmails = new ArrayList<>();
        for (ClassGroup cg : studentClasses) {
            if (cg.getLecturerEmails() != null && !cg.getLecturerEmails().isEmpty()) {
                for (String email : cg.getLecturerEmails()) {
                    if (!lecturerEmails.contains(email)) {
                        lecturerEmails.add(email);
                    }
                }
            }
        }
        
        // Populate dropdown with lecturer name and email
        if (lecturerEmails.isEmpty()) {
            lecturerCombo.addItem("-- No Lecturers Available --");
        } else {
            for (String email : lecturerEmails) {
                User user = UserManager.findByEmail(email);
                if (user != null && user instanceof Lecturer) {
                    String displayText = user.getName() + " (" + email + ")";
                    lecturerCombo.addItem(displayText);
                } else {
                    // Fallback to just email if user not found
                    lecturerCombo.addItem(email);
                }
            }
        }
        
        panel.add(lecturerCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Your Comment:"), gbc);
        gbc.gridx = 1;
        JTextArea commentArea = new JTextArea(5, 25);
        commentArea.setLineWrap(true);
        JScrollPane commentScroll = new JScrollPane(commentArea);
        commentScroll.setPreferredSize(new Dimension(300, 100));
        panel.add(commentScroll, gbc);

        JButton submitButton = createStyledButton("Submit", PRIMARY_COLOR);
        submitButton.addActionListener(e -> {
            String selected = (String) lecturerCombo.getSelectedItem();
            if (selected == null || selected.equals("-- No Lecturers Available --")) {
                JOptionPane.showMessageDialog(this, "Please select a lecturer!", "Error", JOptionPane.ERROR_MESSAGE);
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
            
            String content = commentArea.getText().trim();

            if (lecEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a lecturer!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Lecturer lecturer = CommentManager.findLecturerByEmail(lecEmail);
            if (lecturer == null) {
                JOptionPane.showMessageDialog(this, "Lecturer not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AcademicLeader leader = CommentManager.findLeaderForLecturer(lecEmail);
            if (leader == null) {
                JOptionPane.showMessageDialog(this, "No Academic Leader found for lecturer " + lecEmail + ". Comment not saved.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Comment cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Comment comment = new Comment(student.getName(), student.getEmail(), "Student", lecturer.getName(), lecturer.getEmail(), leader.getEmail(), content);
            CommentManager.addComment(comment);
            JOptionPane.showMessageDialog(this, "Comment submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            commentArea.setText("");
        });

        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        panel.add(submitButton, gbc);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showViewAssessmentsPanel() {
        contentPanel.removeAll();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("My Assessments");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(topPanel, BorderLayout.NORTH);

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
            JLabel messageLabel = new JLabel("You are not registered in any modules.", JLabel.CENTER);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(messageLabel, BorderLayout.CENTER);
            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
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
                    boolean completed = false;
                    double studentMarks = 0.0;

                    java.util.List<Users.Lecturer.StudentMark> marks = 
                        lecturer.getMarksForAssessment(assessment.getAssessmentId());

                    for (Users.Lecturer.StudentMark mark : marks) {
                        if (mark.getStudentId().equalsIgnoreCase(student.getEmail())) {
                            completed = true;
                            studentMarks = mark.getMarks();
                            break;
                        }
                    }

                    String status;
                    if (completed) {
                        // Calculate percentage: (marks / maxMarks) * 100
                        double percentage = Entity.GradingSystem.calculatePercentage(studentMarks, assessment.getMaxMarks());
                        
                        IOManage.GradingSystemManager.loadFromFile();
                        Entity.GradingSystem gradingSystem = IOManage.GradingSystemManager.getGradingSystem();
                        
                        String grade = gradingSystem.getGrade(percentage);
                        String gradeName = gradingSystem.getGradeName(percentage);
                        
                        // Format: Score (Percentage) - Grade: Classification
                        status = String.format("%.1f/%.0f (%.1f%%) - %s: %s", 
                            studentMarks, assessment.getMaxMarks(), percentage, grade, gradeName);
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
            JLabel messageLabel = new JLabel("No assessments found for your registered modules.", JLabel.CENTER);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(messageLabel, BorderLayout.CENTER);
            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
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
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
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
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 10, 0),
            BorderFactory.createLineBorder(new Color(230, 230, 230))
        ));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Summary
        long completedCount = allAssessments.stream().filter(a -> (boolean) a[7]).count();
        long pendingCount = allAssessments.size() - completedCount;
        JLabel summaryLabel = new JLabel(String.format(
            "Total: %d assessments | Completed: %d | Pending: %d", 
            allAssessments.size(), completedCount, pendingCount));
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        summaryLabel.setForeground(new Color(100, 100, 100));
        
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(PANEL_COLOR);
        summaryPanel.add(summaryLabel);
        panel.add(summaryPanel, BorderLayout.SOUTH);

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void applyModernLook() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // Use default look and feel
        }
    }
}
