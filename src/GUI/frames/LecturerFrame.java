package GUI.frames;

import Users.Lecturer;
import Users.Lecturer.Assessment;
import Users.Lecturer.StudentMark;
import Users.Lecturer.AssessmentType;
import IOManage.ClassManager;
import IOManage.ModuleManager;
import IOManage.UserManager;
import Entity.ClassGroup;
import Entity.Module;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class LecturerFrame extends JFrame {
    private Lecturer lecturer;
    private JPanel contentPanel;
    private JButton activeButton;
    
    private static final Color PRIMARY_COLOR = new Color(155, 89, 182);
    private static final Color SECONDARY_COLOR = new Color(142, 68, 173);
    private static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color SIDEBAR_COLOR = new Color(52, 73, 94);
    private static final Color SIDEBAR_HOVER = new Color(70, 90, 110);
    private static final Color SIDEBAR_ACTIVE = new Color(155, 89, 182);

    public LecturerFrame(Lecturer lecturer) {
        this.lecturer = lecturer;
        initializeFrame();
        applyModernLook();
    }

    private void initializeFrame() {
        setTitle("APU Assessment Feedback System - Lecturer Dashboard");
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
        mainContainer.setOpaque(true);

        // Left Sidebar
        JPanel sidebar = createSidebar();
        mainContainer.add(sidebar, BorderLayout.WEST);

        // Right Content Area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setOpaque(true);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        add(mainContainer, BorderLayout.CENTER);
        
        // Show dashboard content after adding to frame
        showDashboardContent();
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

        JLabel userLabel = new JLabel("Welcome, " + lecturer.getName() + " | Lecturer");
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
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        sidebar.setOpaque(true);

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

        // Assessments Section (Collapsible)
        CollapsibleSection assessmentSection = new CollapsibleSection("Assessments");
        JButton designAssessmentBtn = createSidebarButton("Design Assessment", false);
        designAssessmentBtn.addActionListener(e -> {
            setActiveButton(designAssessmentBtn);
            showDesignAssessmentPanel();
        });
        JButton viewAssessmentsBtn = createSidebarButton("View Assessments", false);
        viewAssessmentsBtn.addActionListener(e -> {
            setActiveButton(viewAssessmentsBtn);
            showViewAssessmentsPanel();
        });
        assessmentSection.addButton(designAssessmentBtn);
        assessmentSection.addButton(viewAssessmentsBtn);
        sidebar.add(assessmentSection);

        // Marks Section (Collapsible)
        CollapsibleSection marksSection = new CollapsibleSection("Marks");
        JButton enterMarksBtn = createSidebarButton("Enter Marks", false);
        enterMarksBtn.addActionListener(e -> {
            setActiveButton(enterMarksBtn);
            showEnterMarksPanel();
        });
        JButton viewMarksBtn = createSidebarButton("View Marks", false);
        viewMarksBtn.addActionListener(e -> {
            setActiveButton(viewMarksBtn);
            showViewMarksPanel();
        });
        marksSection.addButton(enterMarksBtn);
        marksSection.addButton(viewMarksBtn);
        sidebar.add(marksSection);

        // Feedback Section (Collapsible)
        CollapsibleSection feedbackSection = new CollapsibleSection("Feedback");
        JButton provideFeedbackBtn = createSidebarButton("Provide Feedback", false);
        provideFeedbackBtn.addActionListener(e -> {
            setActiveButton(provideFeedbackBtn);
            showProvideFeedbackPanel();
        });
        JButton viewFeedbackBtn = createSidebarButton("View Feedback", false);
        viewFeedbackBtn.addActionListener(e -> {
            setActiveButton(viewFeedbackBtn);
            showViewFeedbackPanel();
        });
        feedbackSection.addButton(provideFeedbackBtn);
        feedbackSection.addButton(viewFeedbackBtn);
        sidebar.add(feedbackSection);

        // Schedule Section (Collapsible)
        CollapsibleSection scheduleSection = new CollapsibleSection("Schedule");
        JButton viewScheduleBtn = createSidebarButton("View Schedule", false);
        viewScheduleBtn.addActionListener(e -> {
            setActiveButton(viewScheduleBtn);
            showViewSchedulePanel();
        });
        scheduleSection.addButton(viewScheduleBtn);
        sidebar.add(scheduleSection);

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
        welcomePanel.setPreferredSize(new Dimension(0, 120));
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel welcomeTitle = new JLabel("Lecturer Dashboard");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeTitle.setForeground(Color.WHITE);
        welcomePanel.add(welcomeTitle, BorderLayout.CENTER);
        
        JLabel welcomeSubtitle = new JLabel("Welcome, " + lecturer.getName() + "!");
        welcomeSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeSubtitle.setForeground(new Color(255, 255, 255, 220));
        welcomePanel.add(welcomeSubtitle, BorderLayout.SOUTH);
        
        contentPanel.add(welcomePanel, BorderLayout.NORTH);

        // Leader Info Panel - Show as table
        JPanel leaderInfoPanel = new JPanel(new BorderLayout());
        leaderInfoPanel.setBackground(PANEL_COLOR);
        leaderInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel leaderTitleLabel = new JLabel("Assigned Academic Leaders");
        leaderTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        leaderTitleLabel.setForeground(new Color(52, 73, 94));
        leaderInfoPanel.add(leaderTitleLabel, BorderLayout.NORTH);
        
        // Parse the leader info and display in table
        String[] columns = {"Academic Leader Email", "Module Code", "Module Name"};
        DefaultTableModel leaderModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Parse lecturer.getAssignedLeaderInfo() - format appears to be text, parse it
        String leaderInfo = lecturer.getAssignedLeaderInfo();
        if (leaderInfo != null && !leaderInfo.trim().isEmpty()) {
            String[] lines = leaderInfo.split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                // Expected format: something like "Email: xxx, Module: yyy" or tab-separated
                // Try to parse common formats
                if (line.contains("|")) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3) {
                        leaderModel.addRow(new Object[]{
                            parts[0].trim().replaceAll(".*Email[:\\s]+", "").trim(),
                            parts[1].trim(),
                            parts[2].trim()
                        });
                    }
                } else if (line.contains("\t")) {
                    String[] parts = line.split("\t");
                    if (parts.length >= 3) {
                        leaderModel.addRow(new Object[]{parts[0].trim(), parts[1].trim(), parts[2].trim()});
                    }
                }
            }
        }
        
        // If no data parsed, try to get from modules directly
        if (leaderModel.getRowCount() == 0) {
            ModuleManager.loadAllModules();
            java.util.Set<String> processed = new java.util.HashSet<>();
            for (Module m : ModuleManager.modules) {
                if (m.getLecturerEmails().contains(lecturer.getEmail())) {
                    String key = m.getAcademicLeaderEmail() + "|" + m.getCode();
                    if (!processed.contains(key)) {
                        leaderModel.addRow(new Object[]{
                            m.getAcademicLeaderEmail(),
                            m.getCode(),
                            m.getName()
                        });
                        processed.add(key);
                    }
                }
            }
        }
        
        JTable leaderTable = new JTable(leaderModel);
        leaderTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        leaderTable.setRowHeight(25);
        leaderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane leaderScrollPane = new JScrollPane(leaderTable);
        leaderScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        leaderInfoPanel.add(leaderScrollPane, BorderLayout.CENTER);

        contentPanel.add(leaderInfoPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Panel Methods (replacing dialogs)
    private void showEditProfilePanel() {
        contentPanel.removeAll();
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("Edit Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(lecturer.getName(), 25);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(nameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(lecturer.getEmail(), 25);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(emailField, gbc);

        // Password change fields
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel currentPwLabel = new JLabel("Current Password:");
        currentPwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(currentPwLabel, gbc);
        gbc.gridx = 1;
        JPasswordField currentPwField = new JPasswordField(25);
        panel.add(currentPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel newPwLabel = new JLabel("New Password:");
        newPwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(newPwLabel, gbc);
        gbc.gridx = 1;
        JPasswordField newPwField = new JPasswordField(25);
        panel.add(newPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        JLabel confirmPwLabel = new JLabel("Confirm Password:");
        confirmPwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(confirmPwLabel, gbc);
        gbc.gridx = 1;
        JPasswordField confirmPwField = new JPasswordField(25);
        panel.add(confirmPwField, gbc);

        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String currentPw = new String(currentPwField.getPassword());
            String newPw = new String(newPwField.getPassword());
            String confirmPw = new String(confirmPwField.getPassword());

            boolean emailChanged = false;
            
            // Update basic profile info
            if (!name.isEmpty()) lecturer.setName(name);
            
            // Update email using EmailUpdateManager
            if (!email.isEmpty() && !email.equals(lecturer.getEmail())) {
                String oldEmail = lecturer.getEmail();
                
                if (IOManage.EmailUpdateManager.emailExistsInSystem(email)) {
                    JOptionPane.showMessageDialog(this, 
                        "✗ Error: Email " + email + " already exists in the system.", 
                        "Email Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (IOManage.EmailUpdateManager.updateEmailAcrossSystem(oldEmail, email)) {
                    emailChanged = true;
                    lecturer.setEmail(email);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "✗ Failed to update email.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Handle password change if a new password was entered
            if (!newPw.isEmpty()) {
                if (!IOManage.PasswordUtils.verifyPassword(currentPw, lecturer.getPassword())) {
                    JOptionPane.showMessageDialog(this, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!newPw.equals(confirmPw)) {
                    JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                lecturer.setPassword(IOManage.PasswordUtils.hashPassword(newPw));
            }

            if (!emailChanged) {
                UserManager.updateUser(lecturer);
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

    private void showDesignAssessmentPanel() {
        contentPanel.removeAll();
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Design Assessment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Get next assessment ID
        String nextAssessmentId = getNextAssessmentId();
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Assessment ID:"), gbc);
        gbc.gridx = 1;
        JTextField idField = new JTextField(nextAssessmentId, 25);
        idField.setEditable(false); // Auto-generated, not editable
        panel.add(idField, gbc);

        // Load modules assigned to this lecturer
        ModuleManager.loadAllModules();
        java.util.List<Module> assignedModules = new ArrayList<>();
        for (Module m : ModuleManager.modules) {
            if (m.getLecturerEmails() != null && m.getLecturerEmails().contains(lecturer.getEmail())) {
                assignedModules.add(m);
            }
        }

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Module Code:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> moduleCombo = new JComboBox<>();
        for (Module m : assignedModules) {
            moduleCombo.addItem(m.getCode() + " - " + m.getName());
        }
        moduleCombo.setPreferredSize(new Dimension(250, 25));
        panel.add(moduleCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Assessment Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(25);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Assessment Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<AssessmentType> typeCombo = new JComboBox<>(AssessmentType.values());
        typeCombo.setPreferredSize(new Dimension(200, 25));
        panel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Maximum Marks:"), gbc);
        gbc.gridx = 1;
        JTextField maxMarksField = new JTextField("100", 25);
        panel.add(maxMarksField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(3, 25);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(200, 80));
        panel.add(descScroll, gbc);

        JButton createButton = createStyledButton("Create", PRIMARY_COLOR);
        createButton.addActionListener(e -> {
            String assessmentId = idField.getText().trim();
            String selected = (String) moduleCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a module!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String moduleCode = selected.split(" - ")[0]; // Extract module code from dropdown
            String assessmentName = nameField.getText().trim();
            AssessmentType type = (AssessmentType) typeCombo.getSelectedItem();
            double maxMarks;
            try {
                maxMarks = Double.parseDouble(maxMarksField.getText().trim());
                if (maxMarks <= 1) {
                    JOptionPane.showMessageDialog(this, "Maximum marks must be greater than 1!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid maximum marks value!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String description = descArea.getText().trim();

            if (assessmentId.isEmpty() || moduleCode.isEmpty() || assessmentName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                lecturer.designAssessment(assessmentId, moduleCode, assessmentName, type, maxMarks, description);
                JOptionPane.showMessageDialog(this, "Assessment created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Update assessment ID for next assessment
                idField.setText(getNextAssessmentId());
                nameField.setText("");
                maxMarksField.setText("100");
                descArea.setText("");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 1; gbc.gridy = 7; gbc.anchor = GridBagConstraints.EAST;
        panel.add(createButton, gbc);

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

        // Header panel with title and filters
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JLabel titleLabel = new JLabel("View Assessments");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        List<Assessment> assessments = lecturer.getAllAssessments();
        
        // Collect all data and filter sets
        ArrayList<Object[]> allAssessmentsData = new ArrayList<>();
        java.util.Set<String> moduleSet = new java.util.TreeSet<>();
        java.util.Set<String> typeSet = new java.util.TreeSet<>();
        
        for (Assessment a : assessments) {
            moduleSet.add(a.getModuleCode());
            typeSet.add(a.getType().getDisplayName());
            allAssessmentsData.add(new Object[]{
                a.getAssessmentId(),
                a.getModuleCode(),
                a.getAssessmentName(),
                a.getType().getDisplayName(),
                a.getMaxMarks(),
                a.getDescription()
            });
        }
        
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
        moduleFilter.setPreferredSize(new Dimension(150, 28));
        filterPanel.add(moduleFilter);
        
        // Spacer
        filterPanel.add(Box.createHorizontalStrut(20));
        
        // Type filter
        JLabel typeFilterLabel = new JLabel("Filter by Type:");
        typeFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(typeFilterLabel);
        
        JComboBox<String> typeFilter = new JComboBox<>();
        typeFilter.addItem("All Types");
        for (String type : typeSet) {
            typeFilter.addItem(type);
        }
        typeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeFilter.setPreferredSize(new Dimension(150, 28));
        filterPanel.add(typeFilter);
        
        headerPanel.add(filterPanel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        String[] columns = {"Assessment ID", "Module Code", "Name", "Type", "Max Marks", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Filter update function
        Runnable updateFilter = () -> {
            model.setRowCount(0);
            String selectedModule = (String) moduleFilter.getSelectedItem();
            String selectedType = (String) typeFilter.getSelectedItem();
            
            for (Object[] row : allAssessmentsData) {
                String rowModule = (String) row[1];
                String rowType = (String) row[3];
                
                boolean moduleMatch = selectedModule == null || "All Modules".equals(selectedModule) || rowModule.equals(selectedModule);
                boolean typeMatch = selectedType == null || "All Types".equals(selectedType) || rowType.equals(selectedType);
                
                if (moduleMatch && typeMatch) {
                    model.addRow(row);
                }
            }
        };
        
        // Add filter listeners
        moduleFilter.addActionListener(e -> updateFilter.run());
        typeFilter.addActionListener(e -> updateFilter.run());
        
        // Initialize display with all data
        updateFilter.run();

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        // Apply word-wrap to description column (column 5)
        GUI.utils.GUIStyleUtils.applyWordWrapToTable(table, 5);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(PANEL_COLOR);
        
        JButton updateButton = createStyledButton("Update Selected", PRIMARY_COLOR);
        updateButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an assessment to update!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String assessmentId = (String) model.getValueAt(row, 0);
            showUpdateAssessmentPanel(assessmentId);
        });
        
        JButton deleteButton = createStyledButton("Delete Selected", new Color(231, 76, 60));
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an assessment to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String assessmentId = (String) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this assessment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (lecturer.deleteAssessment(assessmentId)) {
                    JOptionPane.showMessageDialog(this, "Assessment deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    showViewAssessmentsPanel(); // Refresh
                } else {
                    JOptionPane.showMessageDialog(this, "Assessment not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUpdateAssessmentPanel(String assessmentId) {
        Assessment assessment = lecturer.getAssessment(assessmentId);
        if (assessment == null) {
            JOptionPane.showMessageDialog(this, "Assessment not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        contentPanel.removeAll();
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Update Assessment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Assessment Name:"), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(assessment.getAssessmentName(), 25);
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Maximum Marks:"), gbc);
        gbc.gridx = 1;
        JTextField maxMarksField = new JTextField(String.valueOf(assessment.getMaxMarks()), 25);
        panel.add(maxMarksField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(assessment.getDescription(), 3, 25);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(200, 80));
        panel.add(descScroll, gbc);

        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            double maxMarks;
            try {
                maxMarks = Double.parseDouble(maxMarksField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid maximum marks value!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String description = descArea.getText().trim();

            lecturer.updateAssessment(assessmentId, name, maxMarks, description);
            JOptionPane.showMessageDialog(this, "Assessment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            showViewAssessmentsPanel(); // Refresh
        });

        JButton backButton = createStyledButton("Back", new Color(127, 140, 141));
        backButton.addActionListener(e -> showViewAssessmentsPanel());

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        panel.add(backButton, gbc);
        gbc.gridx = 1;
        panel.add(saveButton, gbc);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEnterMarksPanel() {
        contentPanel.removeAll();
        
        List<Assessment> assessments = lecturer.getAllAssessments();
        if (assessments.isEmpty()) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(PANEL_COLOR);
            panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            JLabel messageLabel = new JLabel("No assessments available. Please create assessments first.", JLabel.CENTER);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(messageLabel, BorderLayout.CENTER);
            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Enter Marks");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Assessment ID:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> assessmentCombo = new JComboBox<>();
        for (Assessment a : assessments) {
            assessmentCombo.addItem(a.getAssessmentId() + " - " + a.getAssessmentName());
        }
        assessmentCombo.setPreferredSize(new Dimension(300, 25));
        panel.add(assessmentCombo, gbc);

        // Student Email & Name dropdown
        gbc.gridx = 0; gbc.gridy = 2;
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
            java.util.ArrayList<String> studentEmails = module.getStudentEmails();
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

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Marks:"), gbc);
        gbc.gridx = 1;
        JTextField marksField = new JTextField(25);
        panel.add(marksField, gbc);
        
        // Label to show existing marks warning
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JLabel existingMarksLabel = new JLabel(" ");
        existingMarksLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        existingMarksLabel.setForeground(new Color(192, 57, 43));
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
                    existingMarksLabel.setText(String.format("This student already has marks: %.2f (saving will replace)", sm.getMarks()));
                    marksField.setText(String.format("%.2f", sm.getMarks()));
                    break;
                }
            }
        };
        
        // Add listener to student combo to check existing marks
        studentCombo.addActionListener(e -> checkExistingMarks.run());
        
        // Also check when assessment changes
        assessmentCombo.addActionListener(e -> {
            // Small delay to allow student combo to update first
            javax.swing.SwingUtilities.invokeLater(checkExistingMarks);
        });

        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        saveButton.addActionListener(e -> {
            String selected = (String) assessmentCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an assessment!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String assessmentId = selected.split(" - ")[0];
            
            String studentSelected = (String) studentCombo.getSelectedItem();
            if (studentSelected == null || studentSelected.equals("-- No Students Registered --")) {
                JOptionPane.showMessageDialog(this, "Please select a student!", "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Invalid marks value!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (marksField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter marks!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if this was an update (existing mark label shows warning)
            boolean wasUpdate = existingMarksLabel.getText().contains("already has marks");

            try {
                lecturer.enterMarks(assessmentId, studentEmail, studentName, marks);
                String successMsg = wasUpdate ? 
                    "Mark updated successfully!" : "Marks entered successfully!";
                JOptionPane.showMessageDialog(this, successMsg, "Success", JOptionPane.INFORMATION_MESSAGE);
                existingMarksLabel.setText(" ");
                marksField.setText("");
                // Refresh to check for next student
                checkExistingMarks.run();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(saveButton, gbc);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showViewMarksPanel() {
        contentPanel.removeAll();
        
        // Reload lecturer data to ensure we have the latest marks and feedback from file
        IOManage.AssessmentManager.loadLecturerData(lecturer);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("View Marks");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Get all assessments for the dropdown
        List<Assessment> assessments = lecturer.getAllAssessments();

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(PANEL_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JLabel label = new JLabel("Filter by Assessment:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComboBox<String> assessmentCombo = new JComboBox<>();
        assessmentCombo.addItem("All Assessments");
        for (Assessment a : assessments) {
            assessmentCombo.addItem(a.getAssessmentId() + " - " + a.getAssessmentName());
        }
        assessmentCombo.setPreferredSize(new Dimension(300, 25));
        assessmentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputPanel.add(label);
        inputPanel.add(assessmentCombo);

        String[] columns = {"Student ID", "Student Name", "Assessment ID", "Marks", "Feedback"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

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

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProvideFeedbackPanel() {
        contentPanel.removeAll();
        
        List<Assessment> assessments = lecturer.getAllAssessments();
        if (assessments.isEmpty()) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(PANEL_COLOR);
            panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            JLabel messageLabel = new JLabel("No assessments available.", JLabel.CENTER);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            panel.add(messageLabel, BorderLayout.CENTER);
            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Provide Feedback");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        
        // Info label
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><b>Note:</b> Feedback can only be provided after marks are entered.</html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(200, 100, 0));
        panel.add(infoLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Assessment ID:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> assessmentCombo = new JComboBox<>();
        for (Assessment a : assessments) {
            assessmentCombo.addItem(a.getAssessmentId() + " - " + a.getAssessmentName());
        }
        assessmentCombo.setPreferredSize(new Dimension(300, 25));
        panel.add(assessmentCombo, gbc);

        // Student dropdown - only students with marks
        gbc.gridx = 0; gbc.gridy = 3;
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
            
            String assessmentId = selectedAssessment.split(" - ")[0];
            List<Users.Lecturer.StudentMark> marks = lecturer.getMarksForAssessment(assessmentId);
            
            if (marks == null || marks.isEmpty()) {
                studentCombo.addItem("-- No Students Have Marks Yet --");
                return;
            }
            
            IOManage.UserManager.loadFromFile();
            
            for (Users.Lecturer.StudentMark mark : marks) {
                String currentFeedback = mark.getFeedback().isEmpty() || mark.getFeedback().equals("No feedback available") 
                    ? " [No feedback]" : " [Has feedback]";
                String displayText = mark.getStudentName() + " (" + mark.getStudentId() + ") - " 
                    + mark.getMarks() + " marks" + currentFeedback;
                studentCombo.addItem(displayText);
            }
        };
        
        assessmentCombo.addActionListener(e -> {
            String selected = (String) assessmentCombo.getSelectedItem();
            updateStudentCombo.accept(selected);
        });
        
        if (assessmentCombo.getItemCount() > 0) {
            updateStudentCombo.accept((String) assessmentCombo.getSelectedItem());
        }

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Feedback:"), gbc);
        gbc.gridx = 1;
        JTextArea feedbackArea = new JTextArea(5, 25);
        feedbackArea.setLineWrap(true);
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        feedbackScroll.setPreferredSize(new Dimension(300, 100));
        panel.add(feedbackScroll, gbc);

        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        saveButton.addActionListener(e -> {
            String selected = (String) assessmentCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select an assessment!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String assessmentId = selected.split(" - ")[0];
            
            String studentSelected = (String) studentCombo.getSelectedItem();
            if (studentSelected == null || studentSelected.equals("-- No Students Have Marks Yet --")) {
                JOptionPane.showMessageDialog(this, 
                    "No students have marks for this assessment yet.\nPlease enter marks before providing feedback!", 
                    "Cannot Provide Feedback", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String studentEmail;
            try {
                int start = studentSelected.indexOf("(") + 1;
                int end = studentSelected.indexOf(")");
                studentEmail = studentSelected.substring(start, end);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid student selection!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String feedback = feedbackArea.getText().trim();

            if (feedback.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter feedback!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                lecturer.provideFeedback(assessmentId, studentEmail, feedback);
                JOptionPane.showMessageDialog(this, "Feedback provided successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                feedbackArea.setText("");
                updateStudentCombo.accept(selected);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Cannot provide feedback:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        panel.add(saveButton, gbc);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showViewFeedbackPanel() {
        contentPanel.removeAll();
        
        // Reload lecturer data to ensure we have the latest feedback from file
        IOManage.AssessmentManager.loadLecturerData(lecturer);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header panel with title and filters
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JLabel titleLabel = new JLabel("View Feedback");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Collect all feedback and filter sets
        List<Object[]> feedbackData = new ArrayList<>();
        java.util.Set<String> moduleSet = new java.util.TreeSet<>();
        java.util.Set<String> assessmentSet = new java.util.TreeSet<>();
        java.util.Set<String> studentSet = new java.util.TreeSet<>();
        
        List<Assessment> assessments = lecturer.getAllAssessments();
        
        for (Assessment assessment : assessments) {
            String assessmentId = assessment.getAssessmentId();
            List<Users.Lecturer.StudentMark> marks = lecturer.getMarksForAssessment(assessmentId);
            
            for (Users.Lecturer.StudentMark mark : marks) {
                String feedback = mark.getFeedback();
                if (feedback != null && !feedback.isEmpty() && !feedback.equals("No feedback available")) {
                    moduleSet.add(assessment.getModuleCode());
                    assessmentSet.add(assessment.getAssessmentName());
                    studentSet.add(mark.getStudentName());
                    
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
            panel.add(headerPanel, BorderLayout.NORTH);
            JLabel messageLabel = new JLabel("No feedback has been provided yet.", JLabel.CENTER);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            messageLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
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
            JLabel moduleFilterLabel = new JLabel("Module:");
            moduleFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            filterPanel.add(moduleFilterLabel);
            
            JComboBox<String> moduleFilter = new JComboBox<>();
            moduleFilter.addItem("All Modules");
            for (String mod : moduleSet) {
                moduleFilter.addItem(mod);
            }
            moduleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            moduleFilter.setPreferredSize(new Dimension(120, 28));
            filterPanel.add(moduleFilter);
            
            // Spacer
            filterPanel.add(Box.createHorizontalStrut(10));
            
            // Assessment filter
            JLabel assessmentFilterLabel = new JLabel("Assessment:");
            assessmentFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            filterPanel.add(assessmentFilterLabel);
            
            JComboBox<String> assessmentFilter = new JComboBox<>();
            assessmentFilter.addItem("All Assessments");
            for (String assess : assessmentSet) {
                assessmentFilter.addItem(assess);
            }
            assessmentFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            assessmentFilter.setPreferredSize(new Dimension(150, 28));
            filterPanel.add(assessmentFilter);
            
            // Spacer
            filterPanel.add(Box.createHorizontalStrut(10));
            
            // Student filter
            JLabel studentFilterLabel = new JLabel("Student:");
            studentFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            filterPanel.add(studentFilterLabel);
            
            JComboBox<String> studentFilter = new JComboBox<>();
            studentFilter.addItem("All Students");
            for (String student : studentSet) {
                studentFilter.addItem(student);
            }
            studentFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            studentFilter.setPreferredSize(new Dimension(150, 28));
            filterPanel.add(studentFilter);
            
            headerPanel.add(filterPanel, BorderLayout.SOUTH);
            panel.add(headerPanel, BorderLayout.NORTH);
            
            // Create table
            String[] columns = {"Module", "Assessment", "Student ID", "Student Name", "Marks", "Feedback"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Info label (will be updated by filter)
            JLabel infoLabel = new JLabel();
            infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Filter update function
            Runnable updateFilter = () -> {
                model.setRowCount(0);
                String selectedModule = (String) moduleFilter.getSelectedItem();
                String selectedAssessment = (String) assessmentFilter.getSelectedItem();
                String selectedStudent = (String) studentFilter.getSelectedItem();
                
                int count = 0;
                for (Object[] row : feedbackData) {
                    String rowModule = (String) row[0];
                    String rowAssessment = (String) row[1];
                    String rowStudent = (String) row[3];
                    
                    boolean moduleMatch = selectedModule == null || "All Modules".equals(selectedModule) || rowModule.equals(selectedModule);
                    boolean assessmentMatch = selectedAssessment == null || "All Assessments".equals(selectedAssessment) || rowAssessment.equals(selectedAssessment);
                    boolean studentMatch = selectedStudent == null || "All Students".equals(selectedStudent) || rowStudent.equals(selectedStudent);
                    
                    if (moduleMatch && assessmentMatch && studentMatch) {
                        model.addRow(row);
                        count++;
                    }
                }
                infoLabel.setText(String.format("Showing %d of %d feedback entries", count, feedbackData.size()));
            };
            
            // Add filter listeners
            moduleFilter.addActionListener(e -> updateFilter.run());
            assessmentFilter.addActionListener(e -> updateFilter.run());
            studentFilter.addActionListener(e -> updateFilter.run());
            
            // Initialize display with all data
            updateFilter.run();

            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setRowHeight(30);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            // Set column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Module
            table.getColumnModel().getColumn(1).setPreferredWidth(150); // Assessment
            table.getColumnModel().getColumn(2).setPreferredWidth(100); // Student ID
            table.getColumnModel().getColumn(3).setPreferredWidth(150); // Student Name
            table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Marks
            table.getColumnModel().getColumn(5).setPreferredWidth(350); // Feedback
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                BorderFactory.createLineBorder(new Color(230, 230, 230))
            ));
            
            // Info panel
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBackground(PANEL_COLOR);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            infoPanel.add(infoLabel);
            
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setBackground(PANEL_COLOR);
            centerPanel.add(scrollPane, BorderLayout.CENTER);
            centerPanel.add(infoPanel, BorderLayout.SOUTH);
            
            panel.add(centerPanel, BorderLayout.CENTER);
        }
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showViewSchedulePanel() {
        contentPanel.removeAll();
        
        ClassManager.loadFromFile();

        List<ClassGroup> myClassGroups = new ArrayList<>();
        for (ClassGroup cg : ClassManager.classGroups) {
            if (cg.getLecturerEmails().contains(lecturer.getEmail())) {
                myClassGroups.add(cg);
            }
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("View Schedule");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        panel.add(titleLabel, BorderLayout.NORTH);

        if (myClassGroups.isEmpty()) {
            JLabel messageLabel = new JLabel("No classes registered. Please register to classes first.", JLabel.CENTER);
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            messageLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
            panel.add(messageLabel, BorderLayout.CENTER);
        } else {
            String[] columns = {"Class Code", "Module", "Time", "Classroom"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (ClassGroup cg : myClassGroups) {
                String moduleCode = cg.getModule().getCode();
                String moduleName = cg.getModule().getName();
                String time = cg.getTime() != null && !cg.getTime().isEmpty() ? cg.getTime() : "Not set";
                String classroom = cg.getClassroom() != null && !cg.getClassroom().isEmpty() ? cg.getClassroom() : "Not set";
                model.addRow(new Object[]{cg.getClassCode(), moduleCode + " (" + moduleName + ")", time, classroom});
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

    private void applyModernLook() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // Use default look and feel
        }
    }
    
    /**
     * Get the next assessment ID starting from A01 and incrementing by 1
     * Reads all assessments from assessments.txt to find the highest ID
     */
    private String getNextAssessmentId() {
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
