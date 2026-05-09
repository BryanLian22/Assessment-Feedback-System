package GUI.frames;

import Users.AcademicLeader;
import IOManage.UserManager;
import IOManage.*;
import Entity.Module;
import Entity.ClassGroup;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AcademicLeaderFrame extends JFrame {
    private AcademicLeader leader;
    private JPanel contentPanel;
    private JButton activeButton;
    
    private static final Color PRIMARY_COLOR = new Color(230, 126, 34);
    private static final Color SECONDARY_COLOR = new Color(211, 84, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color SIDEBAR_COLOR = new Color(52, 73, 94);
    private static final Color SIDEBAR_HOVER = new Color(70, 90, 110);
    private static final Color SIDEBAR_ACTIVE = new Color(230, 126, 34);

    public AcademicLeaderFrame(AcademicLeader leader) {
        this.leader = leader;
        initializeFrame();
        applyModernLook();
    }

    private void initializeFrame() {
        setTitle("APU Assessment Feedback System - Academic Leader Dashboard");
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

        JLabel userLabel = new JLabel("Welcome, " + leader.getName() + " | Academic Leader");
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

        // Modules Section (Collapsible)
        CollapsibleSection moduleSection = new CollapsibleSection("Modules");
        JButton manageModulesBtn = createSidebarButton("Manage Modules", false);
        manageModulesBtn.addActionListener(e -> {
            setActiveButton(manageModulesBtn);
            showManageModulesPanel();
        });
        moduleSection.addButton(manageModulesBtn);
        sidebar.add(moduleSection);

        // Reports Section (Collapsible)
        CollapsibleSection reportSection = new CollapsibleSection("Reports");
        JButton moduleSummaryBtn = createSidebarButton("Module Summary", false);
        moduleSummaryBtn.addActionListener(e -> {
            setActiveButton(moduleSummaryBtn);
            showAnalyzeReportsPanel();
        });
        reportSection.addButton(moduleSummaryBtn);
        
        JButton analyzeReportsBtn = createSidebarButton("Analyze Reports", false);
        analyzeReportsBtn.addActionListener(e -> {
            setActiveButton(analyzeReportsBtn);
            showAnalyzeReportsTabbedPanel();
        });
        reportSection.addButton(analyzeReportsBtn);
        
        JButton generateReportBtn = createSidebarButton("Generate Report", false);
        generateReportBtn.addActionListener(e -> {
            setActiveButton(generateReportBtn);
            showGenerateAnalyzedReportPanel();
        });
        reportSection.addButton(generateReportBtn);
        
        JButton viewGeneratedReportsBtn = createSidebarButton("View Generated Reports", false);
        viewGeneratedReportsBtn.addActionListener(e -> {
            setActiveButton(viewGeneratedReportsBtn);
            showViewGeneratedReportsPanel();
        });
        reportSection.addButton(viewGeneratedReportsBtn);
        
        sidebar.add(reportSection);

        // Comments Section (Collapsible)
        CollapsibleSection commentSection = new CollapsibleSection("Comments");
        JButton viewCommentsBtn = createSidebarButton("View Comments", false);
        viewCommentsBtn.addActionListener(e -> {
            setActiveButton(viewCommentsBtn);
            showViewCommentsPanel();
        });
        commentSection.addButton(viewCommentsBtn);
        sidebar.add(commentSection);

        // Classes Section (Collapsible)
        CollapsibleSection classSection = new CollapsibleSection("Classes");
        JButton registerLecturerBtn = createSidebarButton("Register Lecturer to Class", false);
        registerLecturerBtn.addActionListener(e -> {
            setActiveButton(registerLecturerBtn);
            showRegisterLecturerToClassPanel();
        });
        classSection.addButton(registerLecturerBtn);
        sidebar.add(classSection);

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
        
        JLabel welcomeTitle = new JLabel("Academic Leader Dashboard");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeTitle.setForeground(Color.WHITE);
        welcomePanel.add(welcomeTitle, BorderLayout.CENTER);
        
        JLabel welcomeSubtitle = new JLabel("Welcome, " + leader.getName() + "!");
        welcomeSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeSubtitle.setForeground(new Color(255, 255, 255, 220));
        welcomePanel.add(welcomeSubtitle, BorderLayout.SOUTH);
        
        contentPanel.add(welcomePanel, BorderLayout.NORTH);

        // Welcome Message
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center; padding: 40px;'><h2 style='color: #34495e;'>Welcome to your Dashboard</h2><p style='color: #7f8c8d; font-size: 14px;'>Manage modules, view module summaries, and oversee academic operations</p></div></html>", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setBackground(PANEL_COLOR);
        welcomeLabel.setOpaque(true);
        welcomeLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Panel Methods - simplified versions that show content in panel
    private void showEditProfilePanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Edit Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Embedded form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(leader.getName(), 25);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(leader.getEmail(), 25);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(emailField, gbc);

        // Password change controls
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel currentPwLabel = new JLabel("Current Password:");
        currentPwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(currentPwLabel, gbc);
        gbc.gridx = 1;
        JPasswordField currentPwField = new JPasswordField(25);
        formPanel.add(currentPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel newPwLabel = new JLabel("New Password:");
        newPwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(newPwLabel, gbc);
        gbc.gridx = 1;
        JPasswordField newPwField = new JPasswordField(25);
        formPanel.add(newPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel confirmPwLabel = new JLabel("Confirm Password:");
        confirmPwLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(confirmPwLabel, gbc);
        gbc.gridx = 1;
        JPasswordField confirmPwField = new JPasswordField(25);
        formPanel.add(confirmPwField, gbc);
        
        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", new Color(127, 140, 141));
        
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String currentPw = new String(currentPwField.getPassword());
            String newPw = new String(newPwField.getPassword());
            String confirmPw = new String(confirmPwField.getPassword());

            boolean emailChanged = false;
            
            if (!name.isEmpty()) leader.setName(name);
            
            // Update email using EmailUpdateManager
            if (!email.isEmpty() && !email.equals(leader.getEmail())) {
                String oldEmail = leader.getEmail();
                
                if (IOManage.EmailUpdateManager.emailExistsInSystem(email)) {
                    JOptionPane.showMessageDialog(this, 
                        "✗ Error: Email " + email + " already exists in the system.", 
                        "Email Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (IOManage.EmailUpdateManager.updateEmailAcrossSystem(oldEmail, email)) {
                    emailChanged = true;
                    // ✅ UPDATE: Set the new email in the current leader object
                    leader.setEmail(email);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "✗ Failed to update email.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (!newPw.isEmpty()) {
                if (!IOManage.PasswordUtils.verifyPassword(currentPw, leader.getPassword())) {
                    JOptionPane.showMessageDialog(this, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!newPw.equals(confirmPw)) {
                    JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                leader.setPassword(IOManage.PasswordUtils.hashPassword(newPw));
            }

            if (!emailChanged) {
                UserManager.updateUser(leader);
                UserManager.saveToFile();
            } else {
                UserManager.loadFromFile();
            }
            
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        });
        
        cancelButton.addActionListener(e -> {
            nameField.setText(leader.getName());
            emailField.setText(leader.getEmail());
            currentPwField.setText("");
            newPwField.setText("");
            confirmPwField.setText("");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showManageModulesPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header panel with title and search
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JLabel titleLabel = new JLabel("Manage Modules");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Search bar panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(PANEL_COLOR);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(searchLabel);
        
        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(250, 28));
        searchField.setToolTipText("Search by module code or name");
        searchPanel.add(searchField);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchPanel.add(clearButton);
        
        headerPanel.add(searchPanel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        ModuleManager.loadFromFile(leader.getEmail());
        UserManager.loadFromFile();
        
        // Collect all module data
        ArrayList<Object[]> allModulesData = new ArrayList<>();
        for (Module m : ModuleManager.modules) {
            if (m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                allModulesData.add(new Object[]{
                    m.getCode(), 
                    m.getName(), 
                    m.getAcademicLeaderEmail(),
                    getLecturerNames(m)
                });
            }
        }
        
        String[] columns = {"Module Code", "Module Name", "Academic Leader", "Assigned Lecturers"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Search filter function
        Runnable updateSearch = () -> {
            model.setRowCount(0);
            String searchText = searchField.getText().trim().toLowerCase();
            
            for (Object[] row : allModulesData) {
                String code = ((String) row[0]).toLowerCase();
                String name = ((String) row[1]).toLowerCase();
                
                if (searchText.isEmpty() || code.contains(searchText) || name.contains(searchText)) {
                    model.addRow(row);
                }
            }
        };
        
        // Add search listener with real-time filtering
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSearch.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSearch.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSearch.run(); }
        });
        
        clearButton.addActionListener(e -> {
            searchField.setText("");
            updateSearch.run();
        });
        
        // Initialize display with all data
        updateSearch.run();
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        // Set column widths for better display
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(300);
        // Apply word-wrap to name and lecturers columns
        GUI.utils.GUIStyleUtils.applyWordWrapToTable(table, 1, 3);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(PANEL_COLOR);
        
        JButton addButton = createStyledButton("Add Module", PRIMARY_COLOR);
        JButton editButton = createStyledButton("Edit Module", PRIMARY_COLOR);
        JButton deleteButton = createStyledButton("Delete Module", new Color(231, 76, 60));
        JButton assignLecturerButton = createStyledButton("Assign Lecturer", PRIMARY_COLOR);
        JButton removeLecturerButton = createStyledButton("Remove Lecturer", PRIMARY_COLOR);
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        addButton.addActionListener(e -> {
            showAddModuleDialogInline(table, model);
        });
        
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a module to edit!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String moduleCode = (String) model.getValueAt(row, 0);
            showEditModuleDialogInline(moduleCode, table, model);
        });
        
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a module to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String moduleCode = (String) model.getValueAt(row, 0);
            
            // Check if module has classes
            if (ClassManager.hasClassesForModule(moduleCode)) {
                JOptionPane.showMessageDialog(this, "Cannot delete module: There are classes associated with this module. Please delete all classes first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this module?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Module m = ModuleManager.findModuleByCode(moduleCode);
                if (m != null && m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                    ModuleManager.deleteModule(m, leader);
                    JOptionPane.showMessageDialog(this, "Module deleted successfully!");
                    refreshModuleTable(model);
                }
            }
        });
        
        assignLecturerButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a module!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String moduleCode = (String) model.getValueAt(row, 0);
            GUI.dialogs.AcademicLeaderDialogs.showAssignLecturerDialog(this, leader, moduleCode);
            refreshModuleTable(model);
        });
        
        removeLecturerButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a module!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String moduleCode = (String) model.getValueAt(row, 0);
            GUI.dialogs.AcademicLeaderDialogs.showRemoveLecturerDialog(this, leader, moduleCode);
            refreshModuleTable(model);
        });
        
        refreshButton.addActionListener(e -> refreshModuleTable(model));
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(assignLecturerButton);
        buttonPanel.add(removeLecturerButton);
        buttonPanel.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private String getLecturerNames(Module m) {
        if (m.getLecturerEmails() == null || m.getLecturerEmails().isEmpty()) {
            return "None";
        }
        java.util.ArrayList<String> names = new java.util.ArrayList<>();
        for (String email : m.getLecturerEmails()) {
            Users.User user = UserManager.findByEmail(email);
            if (user != null) {
                names.add(user.getName() + " (" + email + ")");
            } else {
                names.add(email);
            }
        }
        return String.join(", ", names);
    }
    
    private void refreshModuleTable(DefaultTableModel model) {
        ModuleManager.loadFromFile(leader.getEmail());
        UserManager.loadFromFile();
        model.setRowCount(0);
        
        for (Module m : ModuleManager.modules) {
            if (m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                model.addRow(new Object[]{
                    m.getCode(), 
                    m.getName(), 
                    m.getAcademicLeaderEmail(),
                    getLecturerNames(m)
                });
            }
        }
    }
    
    private void showAddModuleDialogInline(JTable table, DefaultTableModel model) {
        // Load all modules to calculate next module code
        ModuleManager.loadAllModules();
        
        // Calculate next module code (e.g., mod2, mod3, mod4 -> mod5)
        String nextModuleCode = getNextModuleCode();
        
        JDialog dialog = new JDialog(this, "Add Module", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel codeLabel = new JLabel("Module Code:");
        codeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(codeLabel, gbc);
        gbc.gridx = 1;
        JTextField codeField = new JTextField(nextModuleCode, 20);
        codeField.setEditable(false);
        codeField.setBackground(new Color(240, 240, 240));
        codeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(codeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Module Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(nameField, gbc);
        
        JButton addButton = createStyledButton("Add", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", new Color(127, 140, 141));
        
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
            refreshModuleTable(model);
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditModuleDialogInline(String moduleCode, JTable table, DefaultTableModel model) {
        Module m = ModuleManager.findModuleByCode(moduleCode);
        if (m == null || !m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
            JOptionPane.showMessageDialog(this, "Module not found or not under your management.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Edit Module", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel codeLabel = new JLabel("Module Code:");
        codeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(codeLabel, gbc);
        gbc.gridx = 1;
        JLabel codeValueLabel = new JLabel(m.getCode());
        codeValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(codeValueLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Module Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(m.getName(), 20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(nameField, gbc);
        
        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", new Color(127, 140, 141));
        
        saveButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Module name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ModuleManager.editModule(m, leader, newName);
            JOptionPane.showMessageDialog(dialog, "Module updated successfully!");
            dialog.dispose();
            refreshModuleTable(model);
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAnalyzeReportsPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Module Summary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Load modules for this leader
        ModuleManager.loadFromFile(leader.getEmail());
        ReportManager.loadClassGroups();
        
        if (ModuleManager.modules.isEmpty()) {
            JLabel noModulesLabel = new JLabel("<html><div style='text-align: center; padding: 40px; color: #7f8c8d;'>No modules available. Create a module first.</div></html>", JLabel.CENTER);
            noModulesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(noModulesLabel, BorderLayout.CENTER);
        } else {
            // Create module selection panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(PANEL_COLOR);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel moduleLabel = new JLabel("Select Module:");
            moduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(moduleLabel, gbc);
            
            gbc.gridx = 1;
            JComboBox<String> moduleCombo = new JComboBox<>();
            moduleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            moduleCombo.setPreferredSize(new Dimension(300, 30));
            for (Module m : ModuleManager.modules) {
                // Only show modules that belong to this leader
                if (m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                    String status = ReportManager.reportExistsForModule(m.getCode()) ? " [Report Exists]" : "";
                    moduleCombo.addItem(m.getCode() + " - " + m.getName() + status);
                }
            }
            formPanel.add(moduleCombo, gbc);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            buttonPanel.setBackground(PANEL_COLOR);
            
            JButton generateButton = createStyledButton("Finalize Module", PRIMARY_COLOR);
            JButton viewReportButton = createStyledButton("View Summary", new Color(52, 152, 219));
            JButton deleteReportButton = createStyledButton("Delete Summary", new Color(231, 76, 60));
            
            generateButton.addActionListener(e -> {
                String selected = (String) moduleCombo.getSelectedItem();
                if (selected == null) return;
                String moduleCode = selected.split(" - ")[0];
                Module selectedModule = ModuleManager.findModuleByCode(moduleCode);
                
                if (selectedModule == null) {
                    JOptionPane.showMessageDialog(this, "Module not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if module belongs to this leader
                if (!selectedModule.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                    JOptionPane.showMessageDialog(this, "You can only generate reports for modules you manage.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if report exists
                boolean exists = ReportManager.reportExistsForModule(moduleCode);
                if (exists) {
                    int confirm = JOptionPane.showConfirmDialog(this, 
                        "A report already exists for this module. Generating a new report will replace it.\nContinue?",
                        "Replace Report?", JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) return;
                }
                
                // Generate comprehensive report
                Entity.Report report = ReportManager.generateComprehensiveReport(selectedModule, leader.getEmail());
                if (report == null) {
                    JOptionPane.showMessageDialog(this, "Cannot generate report. No classes found for this module.", 
                        "No Classes", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                ReportManager.saveReport(report);
                JOptionPane.showMessageDialog(this, "Report generated successfully!\n\n" +
                    "Module: " + report.getModuleCode() + "\n" +
                    "Total Students: " + report.getTotalStudents() + "\n" +
                    "Module Average: " + report.getOverallAverage() + "%\n" +
                    "Classes: " + report.getClassReports().size());
                
                // Refresh the panel
                showAnalyzeReportsPanel();
            });
            
            viewReportButton.addActionListener(e -> {
                showAllReportsTable();
            });
            
            deleteReportButton.addActionListener(e -> {
                String selected = (String) moduleCombo.getSelectedItem();
                if (selected == null) return;
                String moduleCode = selected.split(" - ")[0];
                
                ReportManager.loadReportFromFile(leader.getEmail());
                Entity.Report report = null;
                for (Entity.Report r : ReportManager.reports) {
                    if (r.getModuleCode().equalsIgnoreCase(moduleCode)) {
                        report = r;
                        break;
                    }
                }
                
                if (report == null) {
                    JOptionPane.showMessageDialog(this, "No report exists for this module.", 
                        "No Report", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete the report for module " + moduleCode + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ReportManager.deleteReport(report);
                    JOptionPane.showMessageDialog(this, "Report deleted successfully!");
                    showAnalyzeReportsPanel();
                }
            });
            
            buttonPanel.add(generateButton);
            buttonPanel.add(viewReportButton);
            buttonPanel.add(deleteReportButton);
            
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
            formPanel.add(buttonPanel, gbc);
            
            // Existing reports table
            ReportManager.loadReportFromFile(leader.getEmail());
            if (!ReportManager.reports.isEmpty()) {
                gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                JLabel reportsLabel = new JLabel("Your Generated Reports:");
                reportsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                reportsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
                formPanel.add(reportsLabel, gbc);
                
                String[] columns = {"Report ID", "Module", "Students", "Avg %", "Generated Date"};
                javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
                    @Override public boolean isCellEditable(int row, int column) { return false; }
                };
                
                for (Entity.Report r : ReportManager.reports) {
                    model.addRow(new Object[]{r.getReportId(), r.getModuleCode(), 
                        r.getTotalStudents(), r.getOverallAverage() + "%", r.getGeneratedDate()});
                }
                
                JTable table = new JTable(model);
                table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                table.setRowHeight(25);
                table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(600, 200));
                
                gbc.gridy = 3;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weighty = 1.0;
                formPanel.add(scrollPane, gbc);
            }
            
            panel.add(formPanel, BorderLayout.CENTER);
        }
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showAllReportsTable() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header with back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JButton backButton = createStyledButton("← Back", new Color(127, 140, 141));
        backButton.addActionListener(e -> showAnalyzeReportsPanel());
        
        JLabel titleLabel = new JLabel("All Finalized Summary");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Load ALL reports from all leaders
        ReportManager.loadAllReportFromFile();
        
        if (ReportManager.reports.isEmpty()) {
            JLabel noReportsLabel = new JLabel("<html><div style='text-align: center; padding: 40px; color: #7f8c8d;'>No reports have been generated yet.</div></html>", JLabel.CENTER);
            noReportsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(noReportsLabel, BorderLayout.CENTER);
        } else {
            String[] columns = {"Report ID", "Module Code", "Academic Leader", "Total Students", "Average %", "Generated Date"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (Entity.Report r : ReportManager.reports) {
                // Get leader name from email
                Users.User leaderUser = UserManager.findByEmail(r.getAcademicLeaderEmail());
                String leaderName = leaderUser != null ? leaderUser.getName() + " (" + r.getAcademicLeaderEmail() + ")" : r.getAcademicLeaderEmail();
                
                model.addRow(new Object[]{
                    r.getReportId(),
                    r.getModuleCode(),
                    leaderName,
                    r.getTotalStudents(),
                    r.getOverallAverage() + "%",
                    r.getGeneratedDate()
                });
            }
            
            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setRowHeight(28);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            table.getColumnModel().getColumn(0).setPreferredWidth(80);
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(200);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);
            table.getColumnModel().getColumn(4).setPreferredWidth(80);
            table.getColumnModel().getColumn(5).setPreferredWidth(120);
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            
            // Button panel for viewing details
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttonPanel.setBackground(PANEL_COLOR);
            
            JButton viewDetailsButton = createStyledButton("View Details", new Color(52, 152, 219));
            viewDetailsButton.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Please select a report to view details!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String moduleCode = (String) model.getValueAt(row, 1);
                showModuleReportDetails(moduleCode);
            });
            
            buttonPanel.add(viewDetailsButton);
            
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showModuleReportDetails(String moduleCode) {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JButton backButton = createStyledButton("← Back", new Color(127, 140, 141));
        backButton.addActionListener(e -> showAnalyzeReportsPanel());
        
        Module module = ModuleManager.findModuleByCode(moduleCode);
        String moduleName = module != null ? module.getName() : moduleCode;
        
        JLabel titleLabel = new JLabel("Module Report: " + moduleCode + " - " + moduleName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Load the full report with details
        Entity.Report report = ReportManager.generateComprehensiveReport(
            ModuleManager.findModuleByCode(moduleCode), leader.getEmail());
        
        if (report == null || report.getClassReports().isEmpty()) {
            JLabel noDataLabel = new JLabel("<html><div style='text-align: center; padding: 40px; color: #7f8c8d;'>No data available for this report.</div></html>", JLabel.CENTER);
            panel.add(noDataLabel, BorderLayout.CENTER);
        } else {
            // Create tabbed pane for each class
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            
            // Summary tab
            JPanel summaryPanel = createReportSummaryPanel(report);
            tabbedPane.addTab("Summary", summaryPanel);
            
            // Tab for each class
            for (Entity.Report.ClassReportData classReport : report.getClassReports()) {
                JPanel classPanel = createClassReportPanel(classReport);
                String tabTitle = classReport.getClassCode() + " (" + classReport.getLecturerName() + ")";
                tabbedPane.addTab(tabTitle, classPanel);
            }
            
            panel.add(tabbedPane, BorderLayout.CENTER);
        }
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createReportSummaryPanel(Entity.Report report) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Summary info cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        cardsPanel.setBackground(PANEL_COLOR);
        
        cardsPanel.add(createSummaryCard("Total Students", report.getTotalStudents(), PRIMARY_COLOR));
        cardsPanel.add(createSummaryCard("Module Average", report.getOverallAverage() + "%", SECONDARY_COLOR));
        cardsPanel.add(createSummaryCard("Total Classes", String.valueOf(report.getClassReports().size()), new Color(155, 89, 182)));
        cardsPanel.add(createSummaryCard("Generated", report.getGeneratedDate(), new Color(26, 188, 156)));
        
        panel.add(cardsPanel, BorderLayout.NORTH);
        
        // Class averages table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(PANEL_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel tableLabel = new JLabel("Class Performance Summary");
        tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tablePanel.add(tableLabel, BorderLayout.NORTH);
        
        String[] columns = {"Class Code", "Lecturer", "Students", "Class Average %"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        for (Entity.Report.ClassReportData classReport : report.getClassReports()) {
            model.addRow(new Object[]{
                classReport.getClassCode(),
                classReport.getLecturerName(),
                classReport.getStudentReports().size(),
                String.format("%.2f%%", classReport.getClassAverage())
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createClassReportPanel(Entity.Report.ClassReportData classReport) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Class info header
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        infoPanel.setBackground(PANEL_COLOR);
        infoPanel.add(new JLabel("Lecturer: " + classReport.getLecturerName() + " (" + classReport.getLecturerEmail() + ")"));
        infoPanel.add(new JLabel("  |  Class Average: " + String.format("%.2f%%", classReport.getClassAverage())));
        infoPanel.add(new JLabel("  |  Students: " + classReport.getStudentReports().size()));
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Student details table
        String[] columns = {"Student", "Email", "Avg %", "Assessment", "Type", "Score", "%", "Grade"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        for (Entity.Report.StudentReportData student : classReport.getStudentReports()) {
            boolean firstRow = true;
            for (Entity.Report.AssessmentMark mark : student.getAssessmentMarks()) {
                model.addRow(new Object[]{
                    firstRow ? student.getStudentName() : "",
                    firstRow ? student.getStudentEmail() : "",
                    firstRow ? String.format("%.1f%%", student.getAveragePercentage()) : "",
                    mark.getAssessmentName(),
                    mark.getAssessmentType(),
                    String.format("%.1f/%.0f", mark.getMarks(), mark.getMaxMarks()),
                    String.format("%.1f%%", mark.getPercentage()),
                    mark.getGrade()
                });
                firstRow = false;
            }
            // Add empty row between students
            model.addRow(new Object[]{"", "", "", "", "", "", "", ""});
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // Student
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Email
        table.getColumnModel().getColumn(2).setPreferredWidth(60);  // Avg %
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Assessment
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Type
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Score
        table.getColumnModel().getColumn(6).setPreferredWidth(60);  // %
        table.getColumnModel().getColumn(7).setPreferredWidth(50);  // Grade
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryCard(String title, String value, Color color) {
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
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private void showViewCommentsPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header panel with title and filters
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JLabel titleLabel = new JLabel("View Comments");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        IOManage.CommentManager.loadFromFile();
        java.util.ArrayList<Entity.Comment> myComments = IOManage.CommentManager.filterCommentsByAcademicLeader(leader.getEmail());
        
        if (myComments.isEmpty()) {
            panel.add(headerPanel, BorderLayout.NORTH);
            JLabel noCommentsLabel = new JLabel("<html><div style='text-align: center; padding: 40px; color: #7f8c8d;'>No comments available for you.</div></html>", JLabel.CENTER);
            noCommentsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(noCommentsLabel, BorderLayout.CENTER);
        } else {
            // Collect all comment data and filter sets
            ArrayList<Object[]> allCommentsData = new ArrayList<>();
            java.util.Set<String> studentSet = new java.util.TreeSet<>();
            java.util.Set<String> lecturerSet = new java.util.TreeSet<>();
            
            for (Entity.Comment c : myComments) {
                studentSet.add(c.getStudentName());
                lecturerSet.add(c.getLecturerEmail());
                allCommentsData.add(new Object[]{c.getStudentName(), c.getStudentEmail(), c.getLecturerEmail(), c.getContent()});
            }
            
            // Create filter panel
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            filterPanel.setBackground(PANEL_COLOR);
            filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 0, 10, 0)
            ));
            
            // Student filter
            JLabel studentFilterLabel = new JLabel("Filter by Student:");
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
            
            // Spacer
            filterPanel.add(Box.createHorizontalStrut(20));
            
            // Lecturer filter
            JLabel lecturerFilterLabel = new JLabel("Filter by Lecturer:");
            lecturerFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            filterPanel.add(lecturerFilterLabel);
            
            JComboBox<String> lecturerFilter = new JComboBox<>();
            lecturerFilter.addItem("All Lecturers");
            for (String lecturer : lecturerSet) {
                lecturerFilter.addItem(lecturer);
            }
            lecturerFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lecturerFilter.setPreferredSize(new Dimension(180, 28));
            filterPanel.add(lecturerFilter);
            
            headerPanel.add(filterPanel, BorderLayout.SOUTH);
            panel.add(headerPanel, BorderLayout.NORTH);
            
            String[] columns = {"Student Name", "Student Email", "Lecturer Email", "Comment"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Info label
            JLabel infoLabel = new JLabel();
            infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Filter update function
            Runnable updateFilter = () -> {
                model.setRowCount(0);
                String selectedStudent = (String) studentFilter.getSelectedItem();
                String selectedLecturer = (String) lecturerFilter.getSelectedItem();
                
                int count = 0;
                for (Object[] row : allCommentsData) {
                    String rowStudent = (String) row[0];
                    String rowLecturer = (String) row[2];
                    
                    boolean studentMatch = selectedStudent == null || "All Students".equals(selectedStudent) || rowStudent.equals(selectedStudent);
                    boolean lecturerMatch = selectedLecturer == null || "All Lecturers".equals(selectedLecturer) || rowLecturer.equals(selectedLecturer);
                    
                    if (studentMatch && lecturerMatch) {
                        model.addRow(row);
                        count++;
                    }
                }
                infoLabel.setText(String.format("Showing %d of %d comments", count, allCommentsData.size()));
            };
            
            // Add filter listeners
            studentFilter.addActionListener(e -> updateFilter.run());
            lecturerFilter.addActionListener(e -> updateFilter.run());
            
            // Initialize display with all data
            updateFilter.run();
            
            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            table.getColumnModel().getColumn(0).setPreferredWidth(120);
            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(300);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
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

    /**
     * Shows the Analyze Reports panel with 5 different analysis tabs
     */
    private void showAnalyzeReportsTabbedPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JLabel titleLabel = new JLabel("Analyze Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Load necessary data - load ALL modules for comprehensive analysis
        ModuleManager.loadAllModules();
        ReportManager.loadClassGroups();
        UserManager.loadFromFile();
        IOManage.ClassManager.loadFromFile();
        
        // Create tabbed pane with 5 analysis types
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // Tab 1: Student Statistics
        tabbedPane.addTab("Student Statistics", createStudentStatisticsPanel(false));
        
        // Tab 2: Grade Distribution
        tabbedPane.addTab("Grade Distribution", createGradeDistributionPanel(false));
        
        // Tab 3: Module Performance Comparison
        tabbedPane.addTab("Module Performance", createModulePerformancePanel(false));
        
        // Tab 4: Class Comparison
        tabbedPane.addTab("Class Comparison", createClassComparisonPanel(false));
        
        // Tab 5: Assessment Type Analysis
        tabbedPane.addTab("Assessment Analysis", createAssessmentAnalysisPanel(false));
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Tab 1: Student Statistics - Total students in APU, per module, per class
     */
    private JPanel createStudentStatisticsPanel(boolean myModulesOnly) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Filter panel at the top
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        JCheckBox myModulesCheckbox = new JCheckBox("Show only my modules", myModulesOnly);
        myModulesCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        myModulesCheckbox.setBackground(PANEL_COLOR);
        myModulesCheckbox.addActionListener(e -> {
            // Refresh the entire Analyze Reports panel with new filter
            showAnalyzeReportsTabbedPanelWithFilter(0, myModulesCheckbox.isSelected());
        });
        filterPanel.add(myModulesCheckbox);
        
        // Academic Leader filter
        JLabel leaderFilterLabel = new JLabel("Filter by Academic Leader:");
        leaderFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(leaderFilterLabel);
        
        JComboBox<String> leaderFilter = new JComboBox<>();
        leaderFilter.addItem("All Leaders");
        java.util.Set<String> leaders = new java.util.TreeSet<>();
        for (Module m : ModuleManager.modules) {
            leaders.add(m.getAcademicLeaderEmail());
        }
        for (String l : leaders) {
            leaderFilter.addItem(l);
        }
        leaderFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(leaderFilter);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(PANEL_COLOR);
        
        // Calculate statistics
        java.util.Map<String, Integer> studentsPerModule = new java.util.LinkedHashMap<>();
        java.util.Map<String, Integer> studentsPerClass = new java.util.LinkedHashMap<>();
        
        // Create a copy of modules list to avoid ConcurrentModificationException
        java.util.List<Module> modulesCopy = new java.util.ArrayList<>(ModuleManager.modules);
        java.util.List<Entity.ClassGroup> classesCopy = new java.util.ArrayList<>(IOManage.ClassManager.classGroups);
        
        Runnable updateData = () -> {
            studentsPerModule.clear();
            studentsPerClass.clear();
            int[] totalStudents = {0};
            
            String selectedLeader = (String) leaderFilter.getSelectedItem();
            
            for (Module m : modulesCopy) {
                // Apply filters
                if (myModulesCheckbox.isSelected() && !m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) continue;
                if (selectedLeader != null && !"All Leaders".equals(selectedLeader) && !m.getAcademicLeaderEmail().equalsIgnoreCase(selectedLeader)) continue;
                
                int moduleStudentCount = 0;
                for (Entity.ClassGroup cg : classesCopy) {
                    if (cg.getModule() != null && cg.getModule().getCode().equalsIgnoreCase(m.getCode())) {
                        int classCount = cg.getStudentEmails().size();
                        studentsPerClass.put(cg.getClassCode() + " (" + m.getCode() + ")", classCount);
                        moduleStudentCount += classCount;
                    }
                }
                
                studentsPerModule.put(m.getCode() + " - " + m.getName(), moduleStudentCount);
                totalStudents[0] += moduleStudentCount;
            }
            
            // Update content
            contentPanel.removeAll();
            
            // Summary cards
            JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
            cardsPanel.setBackground(PANEL_COLOR);
            
            cardsPanel.add(createSummaryCard("Total Students", String.valueOf(totalStudents[0]), PRIMARY_COLOR));
            cardsPanel.add(createSummaryCard("Total Modules", String.valueOf(studentsPerModule.size()), SECONDARY_COLOR));
            cardsPanel.add(createSummaryCard("Total Classes", String.valueOf(studentsPerClass.size()), new Color(155, 89, 182)));
            
            contentPanel.add(cardsPanel, BorderLayout.NORTH);
            
            // Tables panel
            JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
            tablesPanel.setBackground(PANEL_COLOR);
            tablesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            // Students per Module table
            JPanel moduleTablePanel = new JPanel(new BorderLayout());
            moduleTablePanel.setBackground(PANEL_COLOR);
            JLabel moduleLabel = new JLabel("Students per Module");
            moduleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            moduleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            moduleTablePanel.add(moduleLabel, BorderLayout.NORTH);
            
            String[] moduleColumns = {"Module", "Student Count"};
            javax.swing.table.DefaultTableModel moduleModel = new javax.swing.table.DefaultTableModel(moduleColumns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            for (java.util.Map.Entry<String, Integer> entry : studentsPerModule.entrySet()) {
                moduleModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
            }
            JTable moduleTable = new JTable(moduleModel);
            moduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            moduleTable.setRowHeight(25);
            moduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            moduleTablePanel.add(new JScrollPane(moduleTable), BorderLayout.CENTER);
            
            // Students per Class table
            JPanel classTablePanel = new JPanel(new BorderLayout());
            classTablePanel.setBackground(PANEL_COLOR);
            JLabel classLabel = new JLabel("Students per Class");
            classLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            classLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            classTablePanel.add(classLabel, BorderLayout.NORTH);
            
            String[] classColumns = {"Class (Module)", "Student Count"};
            javax.swing.table.DefaultTableModel classModel = new javax.swing.table.DefaultTableModel(classColumns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            for (java.util.Map.Entry<String, Integer> entry : studentsPerClass.entrySet()) {
                classModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
            }
            JTable classTable = new JTable(classModel);
            classTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            classTable.setRowHeight(25);
            classTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            classTablePanel.add(new JScrollPane(classTable), BorderLayout.CENTER);
            
            tablesPanel.add(moduleTablePanel);
            tablesPanel.add(classTablePanel);
            
            contentPanel.add(tablesPanel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        };
        
        leaderFilter.addActionListener(e -> updateData.run());
        updateData.run();
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showAnalyzeReportsTabbedPanelWithFilter(int selectedTab, boolean myModulesOnly) {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JLabel titleLabel = new JLabel("Analyze Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Load necessary data
        ModuleManager.loadAllModules();
        ReportManager.loadClassGroups();
        UserManager.loadFromFile();
        IOManage.ClassManager.loadFromFile();
        
        // Create tabbed pane with 5 analysis types
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        tabbedPane.addTab("Student Statistics", createStudentStatisticsPanel(myModulesOnly));
        tabbedPane.addTab("Grade Distribution", createGradeDistributionPanel(myModulesOnly));
        tabbedPane.addTab("Module Performance", createModulePerformancePanel(myModulesOnly));
        tabbedPane.addTab("Class Comparison", createClassComparisonPanel(myModulesOnly));
        tabbedPane.addTab("Assessment Analysis", createAssessmentAnalysisPanel(myModulesOnly));
        
        tabbedPane.setSelectedIndex(selectedTab);
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Tab 2: Grade Distribution - Count of A, B, C, D, E, F grades
     */
    private JPanel createGradeDistributionPanel(boolean myModulesOnly) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Filter panel at the top
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        JCheckBox myModulesCheckbox = new JCheckBox("Show only my modules", myModulesOnly);
        myModulesCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        myModulesCheckbox.setBackground(PANEL_COLOR);
        myModulesCheckbox.addActionListener(e -> {
            showAnalyzeReportsTabbedPanelWithFilter(1, myModulesCheckbox.isSelected());
        });
        filterPanel.add(myModulesCheckbox);
        
        // Module filter
        JLabel moduleFilterLabel = new JLabel("Filter by Module:");
        moduleFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(moduleFilterLabel);
        
        JComboBox<String> moduleFilter = new JComboBox<>();
        moduleFilter.addItem("All Modules");
        for (Module m : ModuleManager.modules) {
            if (!myModulesOnly || m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                moduleFilter.addItem(m.getCode() + " - " + m.getName());
            }
        }
        moduleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(moduleFilter);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(PANEL_COLOR);
        
        // Create a copy of modules list to avoid ConcurrentModificationException
        java.util.List<Module> modulesCopy = new java.util.ArrayList<>(ModuleManager.modules);
        
        Runnable updateData = () -> {
            mainContentPanel.removeAll();
            
            java.util.Map<String, int[]> gradesByModule = new java.util.LinkedHashMap<>();
            int[] totalGrades = new int[6]; // A, B, C, D, E, F
            
            String selectedModule = (String) moduleFilter.getSelectedItem();
            
            for (Module m : modulesCopy) {
                // Apply filters
                if (myModulesCheckbox.isSelected() && !m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) continue;
                if (selectedModule != null && !"All Modules".equals(selectedModule) && !selectedModule.startsWith(m.getCode() + " - ")) continue;
                
                int[] moduleGrades = new int[6];
                Entity.Report report = ReportManager.generateComprehensiveReport(m, m.getAcademicLeaderEmail());
                
                if (report != null) {
                    for (Entity.Report.ClassReportData classReport : report.getClassReports()) {
                        for (Entity.Report.StudentReportData student : classReport.getStudentReports()) {
                            for (Entity.Report.AssessmentMark mark : student.getAssessmentMarks()) {
                                String grade = mark.getGrade();
                                int idx = gradeToIndex(grade);
                                if (idx >= 0 && idx < 6) {
                                    moduleGrades[idx]++;
                                    totalGrades[idx]++;
                                }
                            }
                        }
                    }
                }
                gradesByModule.put(m.getCode(), moduleGrades);
            }
            
            // Summary cards for total grades
            JPanel cardsPanel = new JPanel(new GridLayout(1, 6, 10, 10));
            cardsPanel.setBackground(PANEL_COLOR);
            
            String[] gradeLabels = {"A", "B", "C", "D", "E", "F"};
            Color[] gradeColors = {
                new Color(46, 204, 113),  // A - Green
                new Color(52, 152, 219),  // B - Blue
                new Color(155, 89, 182),  // C - Purple
                new Color(241, 196, 15),  // D - Yellow
                new Color(230, 126, 34),  // E - Orange
                new Color(231, 76, 60)    // F - Red
            };
            
            for (int i = 0; i < 6; i++) {
                cardsPanel.add(createSummaryCard("Grade " + gradeLabels[i], String.valueOf(totalGrades[i]), gradeColors[i]));
            }
            
            mainContentPanel.add(cardsPanel, BorderLayout.NORTH);
            
            // Grade distribution table by module
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(PANEL_COLOR);
            tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JLabel tableLabel = new JLabel("Grade Distribution by Module");
            tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            tableLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            tablePanel.add(tableLabel, BorderLayout.NORTH);
            
            String[] columns = {"Module", "A", "B", "C", "D", "E", "F", "Total"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            
            for (java.util.Map.Entry<String, int[]> entry : gradesByModule.entrySet()) {
                int[] grades = entry.getValue();
                int total = 0;
                for (int g : grades) total += g;
                model.addRow(new Object[]{entry.getKey(), grades[0], grades[1], grades[2], 
                    grades[3], grades[4], grades[5], total});
            }
            
            // Add total row
            int grandTotal = 0;
            for (int g : totalGrades) grandTotal += g;
            model.addRow(new Object[]{"TOTAL", totalGrades[0], totalGrades[1], totalGrades[2], 
                totalGrades[3], totalGrades[4], totalGrades[5], grandTotal});
            
            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setRowHeight(28);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
            mainContentPanel.add(tablePanel, BorderLayout.CENTER);
            
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        };
        
        moduleFilter.addActionListener(e -> updateData.run());
        updateData.run();
        
        panel.add(mainContentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private int gradeToIndex(String grade) {
        if (grade == null) return -1;
        switch (grade.toUpperCase()) {
            case "A": return 0;
            case "B": return 1;
            case "C": return 2;
            case "D": return 3;
            case "E": return 4;
            case "F": return 5;
            default: return -1;
        }
    }
    
    /**
     * Tab 3: Module Performance - Average scores, highest/lowest performing modules
     */
    private JPanel createModulePerformancePanel(boolean myModulesOnly) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Filter panel at the top
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        JCheckBox myModulesCheckbox = new JCheckBox("Show only my modules", myModulesOnly);
        myModulesCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        myModulesCheckbox.setBackground(PANEL_COLOR);
        myModulesCheckbox.addActionListener(e -> {
            showAnalyzeReportsTabbedPanelWithFilter(2, myModulesCheckbox.isSelected());
        });
        filterPanel.add(myModulesCheckbox);
        
        // Academic Leader filter
        JLabel leaderFilterLabel = new JLabel("Filter by Academic Leader:");
        leaderFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(leaderFilterLabel);
        
        JComboBox<String> leaderFilter = new JComboBox<>();
        leaderFilter.addItem("All Leaders");
        java.util.Set<String> leaders = new java.util.TreeSet<>();
        for (Module m : ModuleManager.modules) {
            leaders.add(m.getAcademicLeaderEmail());
        }
        for (String l : leaders) {
            leaderFilter.addItem(l);
        }
        leaderFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(leaderFilter);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(PANEL_COLOR);
        
        // Create a copy of modules list to avoid ConcurrentModificationException
        java.util.List<Module> modulesCopy = new java.util.ArrayList<>(ModuleManager.modules);
        
        Runnable updateData = () -> {
            mainContentPanel.removeAll();
            
            java.util.List<Object[]> modulePerformance = new java.util.ArrayList<>();
            String[] highestModule = {"N/A"};
            String[] lowestModule = {"N/A"};
            double[] highestAvg = {0};
            double[] lowestAvg = {100};
            
            String selectedLeader = (String) leaderFilter.getSelectedItem();
            
            for (Module m : modulesCopy) {
                // Apply filters
                if (myModulesCheckbox.isSelected() && !m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) continue;
                if (selectedLeader != null && !"All Leaders".equals(selectedLeader) && !m.getAcademicLeaderEmail().equalsIgnoreCase(selectedLeader)) continue;
                
                Entity.Report report = ReportManager.generateComprehensiveReport(m, m.getAcademicLeaderEmail());
                if (report != null && report.getTotalStudentsCount() > 0) {
                    double avg = report.getModuleAverageValue();
                    modulePerformance.add(new Object[]{m.getCode(), m.getName(), 
                        report.getTotalStudentsCount(), String.format("%.2f%%", avg)});
                    
                    if (avg > highestAvg[0]) {
                        highestAvg[0] = avg;
                        highestModule[0] = m.getCode() + " (" + String.format("%.2f%%", avg) + ")";
                    }
                    if (avg < lowestAvg[0]) {
                        lowestAvg[0] = avg;
                        lowestModule[0] = m.getCode() + " (" + String.format("%.2f%%", avg) + ")";
                    }
                }
            }
            
            // Summary cards
            JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
            cardsPanel.setBackground(PANEL_COLOR);
            
            cardsPanel.add(createSummaryCard("Total Modules", String.valueOf(modulePerformance.size()), PRIMARY_COLOR));
            cardsPanel.add(createSummaryCard("Highest Performing", highestModule[0], new Color(46, 204, 113)));
            cardsPanel.add(createSummaryCard("Lowest Performing", lowestModule[0], new Color(231, 76, 60)));
            
            mainContentPanel.add(cardsPanel, BorderLayout.NORTH);
            
            // Performance table
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(PANEL_COLOR);
            tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JLabel tableLabel = new JLabel("Module Performance Ranking");
            tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            tableLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            tablePanel.add(tableLabel, BorderLayout.NORTH);
            
            String[] columns = {"Module Code", "Module Name", "Students", "Average %"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            
            // Sort by average descending
            modulePerformance.sort((a, b) -> {
                double avgA = Double.parseDouble(((String)a[3]).replace("%", ""));
                double avgB = Double.parseDouble(((String)b[3]).replace("%", ""));
                return Double.compare(avgB, avgA);
            });
            
            for (Object[] row : modulePerformance) {
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setRowHeight(28);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
            mainContentPanel.add(tablePanel, BorderLayout.CENTER);
            
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        };
        
        leaderFilter.addActionListener(e -> updateData.run());
        updateData.run();
        
        panel.add(mainContentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Tab 4: Class Comparison - Compare class averages within modules
     */
    private JPanel createClassComparisonPanel(boolean myModulesOnly) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Filter panel at the top
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        JCheckBox myModulesCheckbox = new JCheckBox("Show only my modules", myModulesOnly);
        myModulesCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        myModulesCheckbox.setBackground(PANEL_COLOR);
        myModulesCheckbox.addActionListener(e -> {
            showAnalyzeReportsTabbedPanelWithFilter(3, myModulesCheckbox.isSelected());
        });
        filterPanel.add(myModulesCheckbox);
        
        // Module filter
        JLabel moduleFilterLabel = new JLabel("Filter by Module:");
        moduleFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(moduleFilterLabel);
        
        JComboBox<String> moduleFilter = new JComboBox<>();
        moduleFilter.addItem("All Modules");
        for (Module m : ModuleManager.modules) {
            if (!myModulesOnly || m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                moduleFilter.addItem(m.getCode() + " - " + m.getName());
            }
        }
        moduleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(moduleFilter);
        
        // Lecturer filter
        JLabel lecturerFilterLabel = new JLabel("Filter by Lecturer:");
        lecturerFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(lecturerFilterLabel);
        
        JComboBox<String> lecturerFilter = new JComboBox<>();
        lecturerFilter.addItem("All Lecturers");
        java.util.Set<String> lecturers = new java.util.TreeSet<>();
        for (Module m : ModuleManager.modules) {
            if (m.getLecturerEmails() != null) {
                lecturers.addAll(m.getLecturerEmails());
            }
        }
        for (String l : lecturers) {
            lecturerFilter.addItem(l);
        }
        lecturerFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(lecturerFilter);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(PANEL_COLOR);
        
        // Create a copy of modules list to avoid ConcurrentModificationException
        java.util.List<Module> modulesCopy = new java.util.ArrayList<>(ModuleManager.modules);
        
        Runnable updateData = () -> {
            mainContentPanel.removeAll();
            
            java.util.List<Object[]> classData = new java.util.ArrayList<>();
            String[] bestClass = {"N/A"};
            String[] worstClass = {"N/A"};
            double[] bestAvg = {0};
            double[] worstAvg = {100};
            
            String selectedModule = (String) moduleFilter.getSelectedItem();
            String selectedLecturer = (String) lecturerFilter.getSelectedItem();
            
            for (Module m : modulesCopy) {
                // Apply filters
                if (myModulesCheckbox.isSelected() && !m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) continue;
                if (selectedModule != null && !"All Modules".equals(selectedModule) && !selectedModule.startsWith(m.getCode() + " - ")) continue;
                
                Entity.Report report = ReportManager.generateComprehensiveReport(m, m.getAcademicLeaderEmail());
                if (report != null) {
                    for (Entity.Report.ClassReportData classReport : report.getClassReports()) {
                        // Apply lecturer filter
                        if (selectedLecturer != null && !"All Lecturers".equals(selectedLecturer) && !classReport.getLecturerEmail().equalsIgnoreCase(selectedLecturer)) continue;
                        
                        double avg = classReport.getClassAverage();
                        int studentCount = classReport.getStudentReports().size();
                        
                        if (studentCount > 0) {
                            classData.add(new Object[]{
                                classReport.getClassCode(),
                                m.getCode(),
                                classReport.getLecturerName(),
                                studentCount,
                                String.format("%.2f%%", avg)
                            });
                            
                            if (avg > bestAvg[0]) {
                                bestAvg[0] = avg;
                                bestClass[0] = classReport.getClassCode() + " (" + String.format("%.2f%%", avg) + ")";
                            }
                            if (avg < worstAvg[0]) {
                                worstAvg[0] = avg;
                                worstClass[0] = classReport.getClassCode() + " (" + String.format("%.2f%%", avg) + ")";
                            }
                        }
                    }
                }
            }
            
            // Summary cards
            JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
            cardsPanel.setBackground(PANEL_COLOR);
            
            cardsPanel.add(createSummaryCard("Total Classes", String.valueOf(classData.size()), PRIMARY_COLOR));
            cardsPanel.add(createSummaryCard("Best Performing Class", bestClass[0], new Color(46, 204, 113)));
            cardsPanel.add(createSummaryCard("Needs Improvement", worstClass[0], new Color(231, 76, 60)));
            
            mainContentPanel.add(cardsPanel, BorderLayout.NORTH);
            
            // Class comparison table
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(PANEL_COLOR);
            tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JLabel tableLabel = new JLabel("Class Performance Comparison");
            tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            tableLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            tablePanel.add(tableLabel, BorderLayout.NORTH);
            
            String[] columns = {"Class Code", "Module", "Lecturer", "Students", "Class Average"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            
            // Sort by average descending
            classData.sort((a, b) -> {
                double avgA = Double.parseDouble(((String)a[4]).replace("%", ""));
                double avgB = Double.parseDouble(((String)b[4]).replace("%", ""));
                return Double.compare(avgB, avgA);
            });
            
            for (Object[] row : classData) {
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setRowHeight(28);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
            mainContentPanel.add(tablePanel, BorderLayout.CENTER);
            
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        };
        
        moduleFilter.addActionListener(e -> updateData.run());
        lecturerFilter.addActionListener(e -> updateData.run());
        updateData.run();
        
        panel.add(mainContentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Tab 5: Assessment Type Analysis - Performance by assessment type
     */
    private JPanel createAssessmentAnalysisPanel(boolean myModulesOnly) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Filter panel at the top
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        JCheckBox myModulesCheckbox = new JCheckBox("Show only my modules", myModulesOnly);
        myModulesCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        myModulesCheckbox.setBackground(PANEL_COLOR);
        myModulesCheckbox.addActionListener(e -> {
            showAnalyzeReportsTabbedPanelWithFilter(4, myModulesCheckbox.isSelected());
        });
        filterPanel.add(myModulesCheckbox);
        
        // Assessment type filter
        JLabel typeFilterLabel = new JLabel("Filter by Assessment Type:");
        typeFilterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(typeFilterLabel);
        
        JComboBox<String> typeFilter = new JComboBox<>();
        typeFilter.addItem("All Types");
        typeFilter.addItem("Assignment");
        typeFilter.addItem("Quiz");
        typeFilter.addItem("Exam");
        typeFilter.addItem("Project");
        typeFilter.addItem("Presentation");
        typeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(typeFilter);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(PANEL_COLOR);
        
        // Create a copy of modules list to avoid ConcurrentModificationException
        java.util.List<Module> modulesCopy = new java.util.ArrayList<>(ModuleManager.modules);
        
        Runnable updateData = () -> {
            mainContentPanel.removeAll();
            
            java.util.Map<String, double[]> typeStats = new java.util.LinkedHashMap<>();
            String[] bestType = {"N/A"};
            String[] worstType = {"N/A"};
            double[] bestAvg = {0};
            double[] worstAvg = {100};
            
            String selectedType = (String) typeFilter.getSelectedItem();
            
            for (Module m : modulesCopy) {
                // Apply filters
                if (myModulesCheckbox.isSelected() && !m.getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) continue;
                
                Entity.Report report = ReportManager.generateComprehensiveReport(m, m.getAcademicLeaderEmail());
                if (report != null) {
                    for (Entity.Report.ClassReportData classReport : report.getClassReports()) {
                        for (Entity.Report.StudentReportData student : classReport.getStudentReports()) {
                            for (Entity.Report.AssessmentMark mark : student.getAssessmentMarks()) {
                                String type = mark.getAssessmentType();
                                if (type == null || type.trim().isEmpty()) type = "Unknown";
                                
                                // Apply type filter
                                if (selectedType != null && !"All Types".equals(selectedType) && !type.equalsIgnoreCase(selectedType)) continue;
                                
                                typeStats.putIfAbsent(type, new double[]{0, 0});
                                double[] stats = typeStats.get(type);
                                stats[0] += mark.getPercentage();
                                stats[1]++;
                            }
                        }
                    }
                }
            }
            
            // Calculate averages and find best/worst types
            java.util.List<Object[]> typeData = new java.util.ArrayList<>();
            
            for (java.util.Map.Entry<String, double[]> entry : typeStats.entrySet()) {
                double[] stats = entry.getValue();
                double avg = stats[1] > 0 ? stats[0] / stats[1] : 0;
                typeData.add(new Object[]{entry.getKey(), (int)stats[1], String.format("%.2f%%", avg)});
                
                if (avg > bestAvg[0] && stats[1] > 0) {
                    bestAvg[0] = avg;
                    bestType[0] = entry.getKey() + " (" + String.format("%.2f%%", avg) + ")";
                }
                if (avg < worstAvg[0] && stats[1] > 0) {
                    worstAvg[0] = avg;
                    worstType[0] = entry.getKey() + " (" + String.format("%.2f%%", avg) + ")";
                }
            }
            
            // Summary cards
            JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
            cardsPanel.setBackground(PANEL_COLOR);
            
            cardsPanel.add(createSummaryCard("Assessment Types", String.valueOf(typeStats.size()), PRIMARY_COLOR));
            cardsPanel.add(createSummaryCard("Best Performance", bestType[0], new Color(46, 204, 113)));
            cardsPanel.add(createSummaryCard("Needs Focus", worstType[0], new Color(231, 76, 60)));
            
            mainContentPanel.add(cardsPanel, BorderLayout.NORTH);
            
            // Assessment type analysis table
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(PANEL_COLOR);
            tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JLabel tableLabel = new JLabel("Performance by Assessment Type");
            tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            tableLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            tablePanel.add(tableLabel, BorderLayout.NORTH);
            
            String[] columns = {"Assessment Type", "Total Submissions", "Average Score"};
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            
            // Sort by average descending
            typeData.sort((a, b) -> {
                double avgA = Double.parseDouble(((String)a[2]).replace("%", ""));
                double avgB = Double.parseDouble(((String)b[2]).replace("%", ""));
                return Double.compare(avgB, avgA);
            });
            
            for (Object[] row : typeData) {
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.setRowHeight(28);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
            mainContentPanel.add(tablePanel, BorderLayout.CENTER);
            
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        };
        
        typeFilter.addActionListener(e -> updateData.run());
        updateData.run();
        
        panel.add(mainContentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void showRegisterLecturerToClassPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Register Lecturer to Class");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        if (leader.getLecturerEmails().isEmpty()) {
            JLabel noLecturersLabel = new JLabel("<html><div style='text-align: center; padding: 40px; color: #7f8c8d;'>No lecturers under your management.</div></html>", JLabel.CENTER);
            noLecturersLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(noLecturersLabel, BorderLayout.CENTER);
        } else {
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(PANEL_COLOR);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 15, 15, 15);
            gbc.anchor = GridBagConstraints.WEST;
            
            JComboBox<String> lecturerCombo = new JComboBox<>();
            lecturerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lecturerCombo.setPreferredSize(new Dimension(300, 30));
            for (String lecEmail : leader.getLecturerEmails()) {
                lecturerCombo.addItem(lecEmail);
            }
            
            ModuleManager.loadFromFile(leader.getEmail());
            IOManage.ClassManager.loadFromFile();
            
            JComboBox<String> classCombo = new JComboBox<>();
            classCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            classCombo.setPreferredSize(new Dimension(300, 30));
            updateClassComboInline(classCombo, leader, (String) lecturerCombo.getSelectedItem());
            
            lecturerCombo.addActionListener(e -> updateClassComboInline(classCombo, leader, (String) lecturerCombo.getSelectedItem()));
            
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel lecturerLabel = new JLabel("Lecturer Email:");
            lecturerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(lecturerLabel, gbc);
            gbc.gridx = 1;
            formPanel.add(lecturerCombo, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            JLabel classLabel = new JLabel("Class Code:");
            classLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            formPanel.add(classLabel, gbc);
            gbc.gridx = 1;
            formPanel.add(classCombo, gbc);
            
            JButton registerButton = createStyledButton("Register", PRIMARY_COLOR);
            registerButton.addActionListener(e -> {
                String lecEmail = (String) lecturerCombo.getSelectedItem();
                String classCode = (String) classCombo.getSelectedItem();
                if (classCode == null || classCode.equals("-- No Available Classes --")) {
                    JOptionPane.showMessageDialog(this, "No available classes for this lecturer.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean success = leader.registerLecturerToClass(lecEmail, classCode);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Lecturer registered to class successfully!");
                    updateClassComboInline(classCombo, leader, lecEmail);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to register lecturer to class.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            // Remove Lecturer button: allow unassigning lecturer from one of their classes
            JButton removeButton = createStyledButton("Remove Lecturer", new Color(231, 76, 60));
            removeButton.addActionListener(e -> {
                String lecEmail = (String) lecturerCombo.getSelectedItem();
                if (lecEmail == null) return;
                // Collect classes where this lecturer is assigned (and module under this leader)
                java.util.List<String> assignedClassCodes = new java.util.ArrayList<>();
                ClassManager.loadFromFile();
                ModuleManager.loadFromFile(leader.getEmail());
                for (ClassGroup cg : ClassManager.classGroups) {
                    if (cg.getLecturerEmails().contains(lecEmail)) {
                        // ensure module is managed by this leader
                        if (cg.getModule() != null && cg.getModule().getAcademicLeaderEmail().equalsIgnoreCase(leader.getEmail())) {
                            assignedClassCodes.add(cg.getClassCode());
                        }
                    }
                }
                if (assignedClassCodes.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "This lecturer is not assigned to any class under your management.", "No Assignments", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String selected = (String) JOptionPane.showInputDialog(this, "Select class to remove lecturer from:", "Remove Lecturer", JOptionPane.PLAIN_MESSAGE, null, assignedClassCodes.toArray(), assignedClassCodes.get(0));
                if (selected != null && !selected.isEmpty()) {
                    boolean removed = leader.removeLecturerFromClass(lecEmail, selected);
                    if (removed) {
                        JOptionPane.showMessageDialog(this, "Lecturer removed from class successfully!");
                        updateClassComboInline(classCombo, leader, lecEmail);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to remove lecturer from class.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(PANEL_COLOR);
            buttonPanel.add(registerButton);
            buttonPanel.add(removeButton);
            
            panel.add(formPanel, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void updateClassComboInline(JComboBox<String> classCombo, AcademicLeader leader, String lecturerEmail) {
        classCombo.removeAllItems();
        ModuleManager.loadFromFile(leader.getEmail());
        IOManage.ClassManager.loadFromFile();
        
        java.util.ArrayList<Entity.ClassGroup> availableClasses = new java.util.ArrayList<>();
        for (Module module : ModuleManager.modules) {
            if (module.getLecturerEmails().contains(lecturerEmail)) {
                for (Entity.ClassGroup cg : IOManage.ClassManager.classGroups) {
                    if (cg.getModule().getCode().equalsIgnoreCase(module.getCode())) {
                        // Only classes without a lecturer are available for assignment
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
            for (Entity.ClassGroup cg : availableClasses) {
                classCombo.addItem(cg.getClassCode());
            }
        }
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
     * Gets the next module code by incrementing from existing codes (e.g., mod2, mod3, mod4 -> mod5)
     */
    private String getNextModuleCode() {
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
    
    /**
     * Panel for generating analyzed reports with parameters (module, semester, lecturer)
     */
    private void showGenerateAnalyzedReportPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JLabel titleLabel = new JLabel("Generate Analyzed Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Load data
        ModuleManager.loadAllModules();
        IOManage.ClassManager.loadFromFile();
        UserManager.loadFromFile();
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Report Type
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Report Type:"), gbc);
        gbc.gridx = 1;
        String[] reportTypes = {"Student Statistics", "Grade Distribution", "Module Performance", "Class Comparison", "Assessment Analysis"};
        JComboBox<String> reportTypeCombo = new JComboBox<>(reportTypes);
        reportTypeCombo.setPreferredSize(new Dimension(250, 30));
        formPanel.add(reportTypeCombo, gbc);
        
        // Module Filter
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Module:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> moduleCombo = new JComboBox<>();
        moduleCombo.addItem("ALL - All Modules");
        for (Module m : ModuleManager.modules) {
            moduleCombo.addItem(m.getCode() + " - " + m.getName());
        }
        moduleCombo.setPreferredSize(new Dimension(250, 30));
        formPanel.add(moduleCombo, gbc);
        
        // Semester Filter
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> semesterCombo = new JComboBox<>();
        semesterCombo.addItem("ALL - All Semesters");
        semesterCombo.addItem("2025-01");
        semesterCombo.addItem("2025-02");
        semesterCombo.addItem("2026-01");
        semesterCombo.addItem("2026-02");
        semesterCombo.setPreferredSize(new Dimension(250, 30));
        formPanel.add(semesterCombo, gbc);
        
        // Lecturer Filter
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Lecturer:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> lecturerCombo = new JComboBox<>();
        lecturerCombo.addItem("ALL - All Lecturers");
        java.util.Set<String> lecturerEmails = new java.util.TreeSet<>();
        for (Users.User u : UserManager.users) {
            if (u instanceof Users.Lecturer) {
                lecturerEmails.add(u.getEmail() + " - " + u.getName());
            }
        }
        for (String lec : lecturerEmails) {
            lecturerCombo.addItem(lec);
        }
        lecturerCombo.setPreferredSize(new Dimension(250, 30));
        formPanel.add(lecturerCombo, gbc);
        
        // Generate Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton generateBtn = createStyledButton("Generate Report", PRIMARY_COLOR);
        generateBtn.setPreferredSize(new Dimension(200, 40));
        formPanel.add(generateBtn, gbc);
        
        // Result Area
        JTextArea resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder("Report Preview"));
        
        // Save Button (initially disabled)
        JButton saveBtn = createStyledButton("Save Report", new Color(39, 174, 96));
        saveBtn.setEnabled(false);
        
        // Store the generated report for saving
        final Entity.AnalyzedReport[] generatedReport = {null};
        
        generateBtn.addActionListener(e -> {
            String reportType = (String) reportTypeCombo.getSelectedItem();
            String moduleSelection = (String) moduleCombo.getSelectedItem();
            String semesterSelection = (String) semesterCombo.getSelectedItem();
            String lecturerSelection = (String) lecturerCombo.getSelectedItem();
            
            // Parse selections
            String moduleCode = moduleSelection.startsWith("ALL") ? "ALL" : moduleSelection.split(" - ")[0].trim();
            String semester = semesterSelection.startsWith("ALL") ? "ALL" : semesterSelection.trim();
            String lecturerEmail = lecturerSelection.startsWith("ALL") ? "ALL" : lecturerSelection.split(" - ")[0].trim();
            
            Entity.AnalyzedReport report = null;
            
            // Generate report based on type
            switch (reportType) {
                case "Student Statistics":
                    report = IOManage.AnalyzedReportManager.generateStudentStatisticsReport(moduleCode, semester, lecturerEmail, leader.getEmail());
                    break;
                case "Grade Distribution":
                    report = IOManage.AnalyzedReportManager.generateGradeDistributionReport(moduleCode, semester, lecturerEmail, leader.getEmail());
                    break;
                case "Module Performance":
                    report = IOManage.AnalyzedReportManager.generateModulePerformanceReport(moduleCode, semester, lecturerEmail, leader.getEmail());
                    break;
                case "Class Comparison":
                    report = IOManage.AnalyzedReportManager.generateClassComparisonReport(moduleCode, semester, lecturerEmail, leader.getEmail());
                    break;
                case "Assessment Analysis":
                    report = IOManage.AnalyzedReportManager.generateAssessmentAnalysisReport(moduleCode, semester, lecturerEmail, leader.getEmail());
                    break;
            }
            
            if (report != null && report.getTotalRecords() > 0) {
                resultArea.setText(report.getReportContent());
                generatedReport[0] = report;
                saveBtn.setEnabled(true);
            } else {
                resultArea.setText("No data matches the selected parameters.\n\nPlease try different filter options:\n- Select a different module\n- Select a different semester\n- Select a different lecturer\n- Or select 'ALL' for any parameter to broaden the search");
                generatedReport[0] = null;
                saveBtn.setEnabled(false);
            }
        });
        
        saveBtn.addActionListener(e -> {
            if (generatedReport[0] != null) {
                IOManage.AnalyzedReportManager.addReport(generatedReport[0]);
                JOptionPane.showMessageDialog(this, 
                    "Report saved successfully!\nReport ID: " + generatedReport[0].getReportId(), 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                saveBtn.setEnabled(false);
            }
        });
        
        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBackground(PANEL_COLOR);
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(resultScroll, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(saveBtn);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Panel for viewing all generated analyzed reports
     */
    private void showViewGeneratedReportsPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JLabel titleLabel = new JLabel("View Generated Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Load reports
        IOManage.AnalyzedReportManager.loadFromFile();
        
        // Table
        String[] columns = {"Report ID", "Type", "Module", "Semester", "Lecturer", "Generated Date", "Records"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        for (Entity.AnalyzedReport r : IOManage.AnalyzedReportManager.analyzedReports) {
            model.addRow(new Object[]{
                r.getReportId(),
                r.getReportTypeString(),
                r.getModuleCode(),
                r.getSemester(),
                r.getLecturerEmail(),
                r.getGeneratedDate(),
                r.getTotalRecords()
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane tableScroll = new JScrollPane(table);
        
        // Report content area
        JTextArea contentArea = new JTextArea(15, 50);
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        contentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createTitledBorder("Report Content"));
        
        // Show content when row selected
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String reportId = (String) model.getValueAt(row, 0);
                    Entity.AnalyzedReport report = IOManage.AnalyzedReportManager.findById(reportId);
                    if (report != null) {
                        contentArea.setText(report.getReportContent());
                        contentArea.setCaretPosition(0);
                    }
                }
            }
        });
        
        // Buttons
        JButton refreshBtn = createStyledButton("Refresh", SECONDARY_COLOR);
        refreshBtn.addActionListener(e -> showViewGeneratedReportsPanel());
        
        JButton deleteBtn = createStyledButton("Delete Selected", new Color(231, 76, 60));
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a report to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String reportId = (String) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete report " + reportId + "?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                IOManage.AnalyzedReportManager.deleteReport(reportId);
                JOptionPane.showMessageDialog(this, "Report deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showViewGeneratedReportsPanel();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(deleteBtn);
        
        // Split pane for table and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, contentScroll);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.4);
        
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show message if no reports
        if (IOManage.AnalyzedReportManager.analyzedReports.isEmpty()) {
            contentArea.setText("No generated reports found.\n\nUse 'Generate Report' to create analyzed reports with parameters.");
        }
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
