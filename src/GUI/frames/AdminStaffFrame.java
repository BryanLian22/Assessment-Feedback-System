package GUI.frames;

import Users.AdminStaff;
import IOManage.*;
import Entity.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AdminStaffFrame extends JFrame {
    private AdminStaff admin;
    private JPanel contentPanel;
    private JButton activeButton;
    
    private static final Color PRIMARY_COLOR = new Color(192, 57, 43);
    private static final Color SECONDARY_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color SIDEBAR_COLOR = new Color(52, 73, 94);
    private static final Color SIDEBAR_HOVER = new Color(70, 90, 110);
    private static final Color SIDEBAR_ACTIVE = new Color(192, 57, 43);

    public AdminStaffFrame(AdminStaff admin) {
        this.admin = admin;
        initializeFrame();
        applyModernLook();
    }

    private void initializeFrame() {
        setTitle("APU Assessment Feedback System - Admin Dashboard");
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

        JLabel userLabel = new JLabel("Welcome, " + admin.getName() + " | Administrator");
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

        // Users Section (Collapsible)
        CollapsibleSection userSection = new CollapsibleSection("Users");
        JButton manageUsersBtn = createSidebarButton("Manage Users", false);
        manageUsersBtn.addActionListener(e -> {
            setActiveButton(manageUsersBtn);
            showManageUsersPanel();
        });
        userSection.addButton(manageUsersBtn);
        
        JButton manageStudentModulesBtn = createSidebarButton("Manage Student Modules", false);
        manageStudentModulesBtn.addActionListener(e -> {
            setActiveButton(manageStudentModulesBtn);
            showManageStudentModulesPanel();
        });
        userSection.addButton(manageStudentModulesBtn);
        sidebar.add(userSection);

        // Lecturers Section (Collapsible)
        CollapsibleSection lecturerSection = new CollapsibleSection("Lecturers");
        JButton assignLecturersBtn = createSidebarButton("Assign Lecturers", false);
        assignLecturersBtn.addActionListener(e -> {
            setActiveButton(assignLecturersBtn);
            showAssignLecturersPanel();
        });
        lecturerSection.addButton(assignLecturersBtn);
        sidebar.add(lecturerSection);

        // Grading Section (Collapsible)
        CollapsibleSection gradingSection = new CollapsibleSection("Grading");
        JButton updateGradingBtn = createSidebarButton("Update Grading System", false);
        updateGradingBtn.addActionListener(e -> {
            setActiveButton(updateGradingBtn);
            showUpdateGradingSystemPanel();
        });
        gradingSection.addButton(updateGradingBtn);
        sidebar.add(gradingSection);

        // Classes Section (Collapsible)
        CollapsibleSection classSection = new CollapsibleSection("Classes");
        JButton manageClassesBtn = createSidebarButton("Manage Classes", false);
        manageClassesBtn.addActionListener(e -> {
            setActiveButton(manageClassesBtn);
            showManageClassesPanel();
        });
        classSection.addButton(manageClassesBtn);
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
        
        JLabel welcomeTitle = new JLabel("Administrator Control Panel");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeTitle.setForeground(Color.WHITE);
        welcomePanel.add(welcomeTitle, BorderLayout.CENTER);
        
        JLabel welcomeSubtitle = new JLabel("Welcome, " + admin.getName() + "!");
        welcomeSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeSubtitle.setForeground(new Color(255, 255, 255, 220));
        welcomePanel.add(welcomeSubtitle, BorderLayout.SOUTH);
        
        contentPanel.add(welcomePanel, BorderLayout.NORTH);

        // Welcome Message
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center; padding: 40px;'><h2 style='color: #34495e;'>Administrator Control Panel</h2><p style='color: #7f8c8d; font-size: 14px;'>Manage users, classes, grading systems, and lecturer assignments</p></div></html>", JLabel.CENTER);
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

    // Panel Methods
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
        JTextField nameField = new JTextField(admin.getName(), 25);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(admin.getEmail(), 25);
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
            
            if (!name.isEmpty()) admin.setName(name);
            
            // Update email using EmailUpdateManager
            if (!email.isEmpty() && !email.equals(admin.getEmail())) {
                // Protect root superadmin from email change
                if (admin.getEmail().equalsIgnoreCase("root")) {
                    JOptionPane.showMessageDialog(this, 
                        "✗ Error: The root superadmin email cannot be changed.", 
                        "Protected Account", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String oldEmail = admin.getEmail();
                
                if (IOManage.EmailUpdateManager.emailExistsInSystem(email)) {
                    JOptionPane.showMessageDialog(this, 
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
                    JOptionPane.showMessageDialog(this, 
                        "✗ Failed to update email.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (!newPw.isEmpty()) {
                if (!IOManage.PasswordUtils.verifyPassword(currentPw, admin.getPassword())) {
                    JOptionPane.showMessageDialog(this, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!newPw.equals(confirmPw)) {
                    JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                admin.setPassword(IOManage.PasswordUtils.hashPassword(newPw));
            }

            if (!emailChanged) {
                UserManager.updateUser(admin);
                UserManager.saveToFile();
            } else {
                UserManager.loadFromFile();
            }
            
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        });
        
        cancelButton.addActionListener(e -> {
            nameField.setText(admin.getName());
            emailField.setText(admin.getEmail());
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

    private void showManageUsersPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header panel with title, search, and filter
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Search and filter panel
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchFilterPanel.setBackground(PANEL_COLOR);
        searchFilterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        // Search bar
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchFilterPanel.add(searchLabel);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 28));
        searchField.setToolTipText("Search by name or email");
        searchFilterPanel.add(searchField);
        
        searchFilterPanel.add(Box.createHorizontalStrut(20));
        
        // Type filter
        JLabel typeLabel = new JLabel("Filter by Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchFilterPanel.add(typeLabel);
        
        JComboBox<String> typeFilter = new JComboBox<>(new String[]{"All Types", "Student", "Lecturer", "AcademicLeader", "AdminStaff"});
        typeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeFilter.setPreferredSize(new Dimension(150, 28));
        searchFilterPanel.add(typeFilter);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchFilterPanel.add(clearButton);
        
        headerPanel.add(searchFilterPanel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        UserManager.loadFromFile();
        
        // Collect all user data
        ArrayList<Object[]> allUsersData = new ArrayList<>();
        for (Users.User u : UserManager.users) {
            String type = "";
            if (u instanceof Users.AdminStaff) type = "AdminStaff";
            else if (u instanceof Users.AcademicLeader) type = "AcademicLeader";
            else if (u instanceof Users.Lecturer) type = "Lecturer";
            else if (u instanceof Users.Student) type = "Student";
            allUsersData.add(new Object[]{u.getName(), u.getEmail(), type});
        }
        
        String[] columns = {"Name", "Email", "Type"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Search and filter function
        Runnable updateFilter = () -> {
            model.setRowCount(0);
            String searchText = searchField.getText().trim().toLowerCase();
            String selectedType = (String) typeFilter.getSelectedItem();
            
            for (Object[] row : allUsersData) {
                String name = ((String) row[0]).toLowerCase();
                String email = ((String) row[1]).toLowerCase();
                String type = (String) row[2];
                
                boolean searchMatch = searchText.isEmpty() || name.contains(searchText) || email.contains(searchText);
                boolean typeMatch = selectedType == null || "All Types".equals(selectedType) || type.equals(selectedType);
                
                if (searchMatch && typeMatch) {
                    model.addRow(row);
                }
            }
        };
        
        // Add search listener with real-time filtering
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFilter.run(); }
        });
        
        typeFilter.addActionListener(e -> updateFilter.run());
        
        clearButton.addActionListener(e -> {
            searchField.setText("");
            typeFilter.setSelectedIndex(0);
            updateFilter.run();
        });
        
        // Initialize display with all data
        updateFilter.run();
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        // Apply word-wrap to all columns
        GUI.utils.GUIStyleUtils.applyWordWrapToAllColumns(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(PANEL_COLOR);
        
        JButton createButton = createStyledButton("Create User", PRIMARY_COLOR);
        JButton editButton = createStyledButton("Edit User", PRIMARY_COLOR);
        JButton deleteButton = createStyledButton("Delete User", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        createButton.addActionListener(e -> {
            showCreateUserFormDialog();
            // Refresh data after dialog closes
            UserManager.loadFromFile();
            allUsersData.clear();
            for (Users.User u : UserManager.users) {
                String type = "";
                if (u instanceof Users.AdminStaff) type = "AdminStaff";
                else if (u instanceof Users.AcademicLeader) type = "AcademicLeader";
                else if (u instanceof Users.Lecturer) type = "Lecturer";
                else if (u instanceof Users.Student) type = "Student";
                allUsersData.add(new Object[]{u.getName(), u.getEmail(), type});
            }
            updateFilter.run();
        });
        
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a user to edit!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String email = (String) model.getValueAt(row, 1);
            showEditUserFormDialog(email);
            // Refresh data after dialog closes
            UserManager.loadFromFile();
            allUsersData.clear();
            for (Users.User u : UserManager.users) {
                String type = "";
                if (u instanceof Users.AdminStaff) type = "AdminStaff";
                else if (u instanceof Users.AcademicLeader) type = "AcademicLeader";
                else if (u instanceof Users.Lecturer) type = "Lecturer";
                else if (u instanceof Users.Student) type = "Student";
                allUsersData.add(new Object[]{u.getName(), u.getEmail(), type});
            }
            updateFilter.run();
        });
        
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a user to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String email = (String) model.getValueAt(row, 1);
            
            // Protect root superadmin from deletion
            if (email.equalsIgnoreCase("root")) {
                JOptionPane.showMessageDialog(this, "✗ Error: The root superadmin account cannot be deleted.", "Protected Account", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Users.User targetUser = UserManager.findByEmail(email);
            if (targetUser != null) {
                // Check for critical dependencies
                String dependencies = UserManager.checkUserDependencies(targetUser);
                
                if (dependencies != null) {
                    // Block deletion for sole lecturers
                    if (targetUser instanceof Users.Lecturer && dependencies.contains("Sole lecturer")) {
                        JOptionPane.showMessageDialog(this, 
                            "❌ Cannot delete this user:\n\n" + dependencies + "\nPlease assign another lecturer before deleting this user.", 
                            "Deletion Blocked", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Block deletion for academic leaders with modules
                    if (targetUser instanceof Users.AcademicLeader && dependencies.contains("Managing module")) {
                        JOptionPane.showMessageDialog(this, 
                            "❌ Cannot delete this user:\n\n" + dependencies + "\nPlease reassign or delete the modules before deleting this user.", 
                            "Deletion Blocked", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Show warning for other dependencies
                    int warnConfirm = JOptionPane.showConfirmDialog(this, 
                        "⚠️ WARNING: This user has dependencies:\n\n" + dependencies + "\nThese references will be removed. Continue?", 
                        "Dependency Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (warnConfirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete " + targetUser.getName() + " (" + targetUser.getEmail() + ")?", 
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Cleanup references before deletion
                    UserManager.cleanupUserReferences(targetUser);
                    UserManager.users.remove(targetUser);
                    UserManager.saveToFile();
                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    // Refresh data
                    allUsersData.clear();
                    for (Users.User u : UserManager.users) {
                        String type = "";
                        if (u instanceof Users.AdminStaff) type = "AdminStaff";
                        else if (u instanceof Users.AcademicLeader) type = "AcademicLeader";
                        else if (u instanceof Users.Lecturer) type = "Lecturer";
                        else if (u instanceof Users.Student) type = "Student";
                        allUsersData.add(new Object[]{u.getName(), u.getEmail(), type});
                    }
                    updateFilter.run();
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            UserManager.loadFromFile();
            allUsersData.clear();
            for (Users.User u : UserManager.users) {
                String type = "";
                if (u instanceof Users.AdminStaff) type = "AdminStaff";
                else if (u instanceof Users.AcademicLeader) type = "AcademicLeader";
                else if (u instanceof Users.Lecturer) type = "Lecturer";
                else if (u instanceof Users.Student) type = "Student";
                allUsersData.add(new Object[]{u.getName(), u.getEmail(), type});
            }
            updateFilter.run();
        });
        
        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void showCreateUserFormDialog() {
        JDialog dialog = new JDialog(this, "Create User", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
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

        JButton createButton = createStyledButton("Create", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", new Color(127, 140, 141));

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
            
            Users.User newUser = null;
            switch (type) {
                case "Student" -> newUser = new Users.Student(name, email, hashedPassword);
                case "Lecturer" -> newUser = new Users.Lecturer(name, email, hashedPassword, "");
                case "AcademicLeader" -> newUser = new Users.AcademicLeader(name, email, hashedPassword);
                case "AdminStaff" -> newUser = new Users.AdminStaff(name, email, hashedPassword);
            }

            if (newUser != null) {
                UserManager.addUser(newUser);
                UserManager.saveToFile();
                JOptionPane.showMessageDialog(dialog, "User created successfully!");
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showEditUserFormDialog(String targetEmail) {
        Users.User targetUser = UserManager.findByEmail(targetEmail);
        if (targetUser == null) {
            JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Edit User: " + targetUser.getName(), true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
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
        panel.add(new JLabel("New Password (optional):"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", new Color(127, 140, 141));

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Email are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean emailChanged = false;
            targetUser.setName(name);
            
            // Check if email is being changed
            if (!email.equals(targetUser.getEmail())) {
                // Protect root superadmin from email change
                if (targetUser.getEmail().equalsIgnoreCase("root")) {
                    JOptionPane.showMessageDialog(dialog, "✗ Error: The root superadmin email cannot be changed.", "Protected Account", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String oldEmail = targetUser.getEmail();
                
                // Check if new email already exists
                if (IOManage.EmailUpdateManager.emailExistsInSystem(email)) {
                    JOptionPane.showMessageDialog(dialog, "✗ Error: Email " + email + " already exists in the system.", "Email Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update email across all files
                if (IOManage.EmailUpdateManager.updateEmailAcrossSystem(oldEmail, email)) {
                    emailChanged = true;
                    targetUser.setEmail(email);
                } else {
                    JOptionPane.showMessageDialog(dialog, "✗ Failed to update email.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (!password.isEmpty()) {
                targetUser.setPassword(IOManage.PasswordUtils.hashPassword(password));
            }
            
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void refreshUserTable(DefaultTableModel model) {
        UserManager.loadFromFile();
        model.setRowCount(0);
        for (Users.User u : UserManager.users) {
            String type = "";
            if (u instanceof Users.AdminStaff) type = "AdminStaff";
            else if (u instanceof Users.AcademicLeader) type = "AcademicLeader";
            else if (u instanceof Users.Lecturer) type = "Lecturer";
            else if (u instanceof Users.Student) type = "Student";
            model.addRow(new Object[]{u.getName(), u.getEmail(), type});
        }
    }

    private void showAssignLecturersPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header panel with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JLabel titleLabel = new JLabel("Assign Lecturers");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        UserManager.loadFromFile();
        java.util.ArrayList<Users.AcademicLeader> leaders = new java.util.ArrayList<>();
        for (Users.User u : UserManager.users) {
            if (u instanceof Users.AcademicLeader al) {
                leaders.add(al);
            }
        }
        
        if (leaders.isEmpty()) {
            panel.add(headerPanel, BorderLayout.NORTH);
            JLabel noLeadersLabel = new JLabel("<html><div style='text-align: center; padding: 40px; color: #7f8c8d;'>No Academic Leaders found.</div></html>", JLabel.CENTER);
            noLeadersLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.add(noLeadersLabel, BorderLayout.CENTER);
        } else {
            // Search panel for leader selection
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            searchPanel.setBackground(PANEL_COLOR);
            searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 0, 10, 0)
            ));
            
            JLabel searchLabel = new JLabel("Search Leader:");
            searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            searchPanel.add(searchLabel);
            
            JTextField searchField = new JTextField(15);
            searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            searchField.setPreferredSize(new Dimension(150, 28));
            searchField.setToolTipText("Type to filter leaders");
            searchPanel.add(searchField);
            
            searchPanel.add(Box.createHorizontalStrut(20));
            
            JLabel leaderLabel = new JLabel("Academic Leader:");
            leaderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            searchPanel.add(leaderLabel);
            
            JComboBox<String> leaderCombo = new JComboBox<>();
            leaderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            leaderCombo.setPreferredSize(new Dimension(300, 30));
            for (Users.AcademicLeader al : leaders) {
                leaderCombo.addItem(al.getName() + " (" + al.getEmail() + ")");
            }
            searchPanel.add(leaderCombo);
            
            // Search filter for leaders
            searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { filterLeaders(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { filterLeaders(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { filterLeaders(); }
                
                private void filterLeaders() {
                    String searchText = searchField.getText().trim().toLowerCase();
                    leaderCombo.removeAllItems();
                    for (Users.AcademicLeader al : leaders) {
                        String display = al.getName() + " (" + al.getEmail() + ")";
                        if (searchText.isEmpty() || display.toLowerCase().contains(searchText)) {
                            leaderCombo.addItem(display);
                        }
                    }
                }
            });
            
            headerPanel.add(searchPanel, BorderLayout.SOUTH);
            panel.add(headerPanel, BorderLayout.NORTH);
            
            JPanel controlPanel = new JPanel(new BorderLayout());
            controlPanel.setBackground(PANEL_COLOR);
            
            String[] columns = {"Assigned Lecturers"};
            DefaultTableModel assignedModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable assignedTable = new JTable(assignedModel);
            assignedTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            assignedTable.setRowHeight(25);
            assignedTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            String[] columns2 = {"Available Lecturers"};
            DefaultTableModel availableModel = new DefaultTableModel(columns2, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable availableTable = new JTable(availableModel);
            availableTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            availableTable.setRowHeight(25);
            availableTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            updateLecturerTablesInline(leaderCombo, assignedModel, availableModel, leaders);
            leaderCombo.addActionListener(e -> updateLecturerTablesInline(leaderCombo, assignedModel, availableModel, leaders));
            
            JPanel tablePanel = new JPanel(new GridLayout(1, 2, 15, 0));
            tablePanel.setBackground(PANEL_COLOR);
            JScrollPane assignedScroll = new JScrollPane(assignedTable);
            assignedScroll.setBorder(BorderFactory.createTitledBorder("Assigned"));
            JScrollPane availableScroll = new JScrollPane(availableTable);
            availableScroll.setBorder(BorderFactory.createTitledBorder("Available"));
            tablePanel.add(assignedScroll);
            tablePanel.add(availableScroll);
            
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
            actionPanel.setBackground(PANEL_COLOR);
            JButton addButton = createStyledButton("Add Lecturer", PRIMARY_COLOR);
            JButton removeButton = createStyledButton("Remove Lecturer", new Color(231, 76, 60));
            JButton saveButton = createStyledButton("Save", new Color(39, 174, 96));
            
            addButton.addActionListener(e -> {
                int row = availableTable.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Please select a lecturer to add!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (leaderCombo.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Please select a leader!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String selected = (String) leaderCombo.getSelectedItem();
                String leaderEmail = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
                Users.AcademicLeader selectedLeader = leaders.stream()
                    .filter(al -> al.getEmail().equals(leaderEmail))
                    .findFirst().orElse(null);
                
                if (selectedLeader != null) {
                    String lecEmail = (String) availableModel.getValueAt(row, 0);
                    selectedLeader.addLecturerEmail(lecEmail);
                    updateLecturerTablesInline(leaderCombo, assignedModel, availableModel, leaders);
                }
            });
            
            removeButton.addActionListener(e -> {
                int row = assignedTable.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Please select a lecturer to remove!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (leaderCombo.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Please select a leader!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String selected = (String) leaderCombo.getSelectedItem();
                String leaderEmail = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
                Users.AcademicLeader selectedLeader = leaders.stream()
                    .filter(al -> al.getEmail().equals(leaderEmail))
                    .findFirst().orElse(null);
                
                if (selectedLeader != null) {
                    String lecEmail = (String) assignedModel.getValueAt(row, 0);
                    selectedLeader.getLecturerEmails().remove(lecEmail);
                    updateLecturerTablesInline(leaderCombo, assignedModel, availableModel, leaders);
                }
            });
            
            saveButton.addActionListener(e -> {
                if (leaderCombo.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Please select a leader!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String selected = (String) leaderCombo.getSelectedItem();
                String leaderEmail = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
                Users.AcademicLeader selectedLeader = leaders.stream()
                    .filter(al -> al.getEmail().equals(leaderEmail))
                    .findFirst().orElse(null);
                
                if (selectedLeader != null) {
                    UserManager.updateUser(selectedLeader);
                    UserManager.saveToFile();
                    JOptionPane.showMessageDialog(this, "Lecturer assignments saved successfully!");
                }
            });
            
            actionPanel.add(addButton);
            actionPanel.add(removeButton);
            actionPanel.add(saveButton);
            
            controlPanel.add(tablePanel, BorderLayout.CENTER);
            controlPanel.add(actionPanel, BorderLayout.SOUTH);
            panel.add(controlPanel, BorderLayout.CENTER);
        }
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void updateLecturerTablesInline(JComboBox<String> leaderCombo, DefaultTableModel assignedModel, 
                                            DefaultTableModel availableModel, java.util.ArrayList<Users.AcademicLeader> leaders) {
        assignedModel.setRowCount(0);
        availableModel.setRowCount(0);
        
        if (leaderCombo.getSelectedItem() == null) return;
        
        String selected = (String) leaderCombo.getSelectedItem();
        String leaderEmail = selected.substring(selected.indexOf("(") + 1, selected.indexOf(")"));
        Users.AcademicLeader selectedLeader = leaders.stream()
            .filter(al -> al.getEmail().equals(leaderEmail))
            .findFirst().orElse(null);
        
        if (selectedLeader == null) return;
        
        java.util.ArrayList<String> allLecturerEmails = new java.util.ArrayList<>();
        for (Users.User u : UserManager.users) {
            if (u instanceof Users.Lecturer) {
                allLecturerEmails.add(u.getEmail());
            }
        }
        
        java.util.ArrayList<String> assignedEmails = selectedLeader.getLecturerEmails();
        for (String email : assignedEmails) {
            assignedModel.addRow(new Object[]{email});
        }
        
        for (String email : allLecturerEmails) {
            boolean isAssigned = false;
            for (Users.AcademicLeader al : leaders) {
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

    private void showUpdateGradingSystemPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Update Letter Grade System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        GradingSystemManager.loadFromFile();
        GradingSystem gs = GradingSystemManager.getGradingSystem();
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Grade scale info
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><b>Percentage Formula:</b> (marks / totalMarks) × 100<br/><b>Enter minimum percentage for each grade (0 - 100)</b></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(infoLabel, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel aMinLabel = new JLabel("A_MIN (Excellent):");
        aMinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(aMinLabel, gbc);
        gbc.gridx = 1;
        JTextField aMinField = new JTextField(String.format("%.1f", gs.getAMin()), 15);
        aMinField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(aMinField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel bMinLabel = new JLabel("B_MIN (Good):");
        bMinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(bMinLabel, gbc);
        gbc.gridx = 1;
        JTextField bMinField = new JTextField(String.format("%.1f", gs.getBMin()), 15);
        bMinField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(bMinField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel cMinLabel = new JLabel("C_MIN (Satisfactory):");
        cMinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(cMinLabel, gbc);
        gbc.gridx = 1;
        JTextField cMinField = new JTextField(String.format("%.1f", gs.getCMin()), 15);
        cMinField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(cMinField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel dMinLabel = new JLabel("D_MIN (Pass):");
        dMinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(dMinLabel, gbc);
        gbc.gridx = 1;
        JTextField dMinField = new JTextField(String.format("%.1f", gs.getDMin()), 15);
        dMinField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(dMinField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel eMinLabel = new JLabel("E_MIN (Marginal):");
        eMinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(eMinLabel, gbc);
        gbc.gridx = 1;
        JTextField eMinField = new JTextField(String.format("%.1f", gs.getEMin()), 15);
        eMinField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(eMinField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JLabel failNote = new JLabel("(Anything below E_MIN is F - Fail)");
        failNote.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        failNote.setForeground(new Color(150, 50, 50));
        formPanel.add(failNote, gbc);
        
        JButton saveButton = createStyledButton("Save", PRIMARY_COLOR);
        JButton cancelButton = createStyledButton("Cancel", new Color(127, 140, 141));
        
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
                    JOptionPane.showMessageDialog(this, String.format("Invalid A_MIN! Must be > B_MIN (%.1f) and <= 100.", newB), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (newB < newA && newB > newC && newB >= 0) {
                    gs.setBMin(newB);
                } else {
                    JOptionPane.showMessageDialog(this, String.format("Invalid B_MIN! Must be < A_MIN (%.1f) and > C_MIN (%.1f).", newA, newC), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (newC < newB && newC > newD && newC >= 0) {
                    gs.setCMin(newC);
                } else {
                    JOptionPane.showMessageDialog(this, String.format("Invalid C_MIN! Must be < B_MIN (%.1f) and > D_MIN (%.1f).", newB, newD), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (newD < newC && newD > newE && newD >= 0) {
                    gs.setDMin(newD);
                } else {
                    JOptionPane.showMessageDialog(this, String.format("Invalid D_MIN! Must be < C_MIN (%.1f) and > E_MIN (%.1f).", newC, newE), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (newE < newD && newE >= 0) {
                    gs.setEMin(newE);
                } else {
                    JOptionPane.showMessageDialog(this, String.format("Invalid E_MIN! Must be < D_MIN (%.1f) and >= 0.", newD), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                GradingSystemManager.setGradingSystem(gs);
                JOptionPane.showMessageDialog(this, "Grading system updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid numbers (e.g., 80.0).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> {
            aMinField.setText(String.format("%.1f", gs.getAMin()));
            bMinField.setText(String.format("%.1f", gs.getBMin()));
            cMinField.setText(String.format("%.1f", gs.getCMin()));
            dMinField.setText(String.format("%.1f", gs.getDMin()));
            eMinField.setText(String.format("%.1f", gs.getEMin()));
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

    private void showManageClassesPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header panel with title and filter
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JLabel titleLabel = new JLabel("Manage Classes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        ModuleManager.loadAllModules();
        ClassManager.loadFromFile();
        
        // Collect all class data and modules for filter
        ArrayList<Object[]> allClassesData = new ArrayList<>();
        java.util.Set<String> moduleSet = new java.util.TreeSet<>();
        
        for (ClassGroup cg : ClassManager.classGroups) {
            moduleSet.add(cg.getModule().getCode());
            allClassesData.add(new Object[]{cg.getClassCode(), cg.getModule().getCode(), cg.getTime(), cg.getClassroom()});
        }
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        // Search bar
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(searchLabel);
        
        JTextField searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(150, 28));
        searchField.setToolTipText("Search by class code");
        filterPanel.add(searchField);
        
        filterPanel.add(Box.createHorizontalStrut(20));
        
        // Module filter
        JLabel moduleLabel = new JLabel("Filter by Module:");
        moduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterPanel.add(moduleLabel);
        
        JComboBox<String> moduleFilter = new JComboBox<>();
        moduleFilter.addItem("All Modules");
        for (String mod : moduleSet) {
            moduleFilter.addItem(mod);
        }
        moduleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        moduleFilter.setPreferredSize(new Dimension(150, 28));
        filterPanel.add(moduleFilter);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(clearButton);
        
        headerPanel.add(filterPanel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        String[] columns = {"Class Code", "Module Code", "Time", "Classroom"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Filter update function
        Runnable updateFilter = () -> {
            model.setRowCount(0);
            String searchText = searchField.getText().trim().toLowerCase();
            String selectedModule = (String) moduleFilter.getSelectedItem();
            
            for (Object[] row : allClassesData) {
                String classCode = ((String) row[0]).toLowerCase();
                String moduleCode = (String) row[1];
                
                boolean searchMatch = searchText.isEmpty() || classCode.contains(searchText);
                boolean moduleMatch = selectedModule == null || "All Modules".equals(selectedModule) || moduleCode.equals(selectedModule);
                
                if (searchMatch && moduleMatch) {
                    model.addRow(row);
                }
            }
        };
        
        // Add filter listeners
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFilter.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFilter.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFilter.run(); }
        });
        
        moduleFilter.addActionListener(e -> updateFilter.run());
        
        clearButton.addActionListener(e -> {
            searchField.setText("");
            moduleFilter.setSelectedIndex(0);
            updateFilter.run();
        });
        
        // Initialize display
        updateFilter.run();
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(PANEL_COLOR);
        
        JButton addButton = createStyledButton("Add Class", PRIMARY_COLOR);
        JButton editButton = createStyledButton("Edit Class", PRIMARY_COLOR);
        JButton deleteButton = createStyledButton("Delete Class", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        addButton.addActionListener(e -> {
            GUI.dialogs.AdminDialogs.showAddClassDialog(this, admin);
            // Refresh data
            ClassManager.loadFromFile();
            allClassesData.clear();
            moduleSet.clear();
            for (ClassGroup cg : ClassManager.classGroups) {
                moduleSet.add(cg.getModule().getCode());
                allClassesData.add(new Object[]{cg.getClassCode(), cg.getModule().getCode(), cg.getTime(), cg.getClassroom()});
            }
            // Update module filter
            moduleFilter.removeAllItems();
            moduleFilter.addItem("All Modules");
            for (String mod : moduleSet) {
                moduleFilter.addItem(mod);
            }
            updateFilter.run();
        });
        
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a class to edit!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String classCode = (String) model.getValueAt(row, 0);
            GUI.dialogs.AdminDialogs.showEditClassDialog(this, admin, classCode);
            // Refresh data
            ClassManager.loadFromFile();
            allClassesData.clear();
            moduleSet.clear();
            for (ClassGroup cg : ClassManager.classGroups) {
                moduleSet.add(cg.getModule().getCode());
                allClassesData.add(new Object[]{cg.getClassCode(), cg.getModule().getCode(), cg.getTime(), cg.getClassroom()});
            }
            updateFilter.run();
        });
        
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a class to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String classCode = (String) model.getValueAt(row, 0);
            ClassGroup cg = ClassManager.findByClassCode(classCode);
            if (cg != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this class?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ClassManager.deleteClassGroup(cg);
                    JOptionPane.showMessageDialog(this, "Class deleted successfully!");
                    // Refresh data
                    allClassesData.clear();
                    moduleSet.clear();
                    for (ClassGroup c : ClassManager.classGroups) {
                        moduleSet.add(c.getModule().getCode());
                        allClassesData.add(new Object[]{c.getClassCode(), c.getModule().getCode(), c.getTime(), c.getClassroom()});
                    }
                    updateFilter.run();
                }
            }
        });
        
        refreshButton.addActionListener(e -> {
            ClassManager.loadFromFile();
            allClassesData.clear();
            moduleSet.clear();
            for (ClassGroup cg : ClassManager.classGroups) {
                moduleSet.add(cg.getModule().getCode());
                allClassesData.add(new Object[]{cg.getClassCode(), cg.getModule().getCode(), cg.getTime(), cg.getClassroom()});
            }
            // Update module filter
            String currentSelection = (String) moduleFilter.getSelectedItem();
            moduleFilter.removeAllItems();
            moduleFilter.addItem("All Modules");
            for (String mod : moduleSet) {
                moduleFilter.addItem(mod);
            }
            if (currentSelection != null && moduleSet.contains(currentSelection)) {
                moduleFilter.setSelectedItem(currentSelection);
            }
            updateFilter.run();
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private void refreshClassTable(DefaultTableModel model) {
        ClassManager.loadFromFile();
        model.setRowCount(0);
        for (ClassGroup cg : ClassManager.classGroups) {
            model.addRow(new Object[]{cg.getClassCode(), cg.getModule().getCode(), cg.getTime(), cg.getClassroom()});
        }
    }

    private void showManageStudentModulesPanel() {
        contentPanel.removeAll();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header panel with title and search
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PANEL_COLOR);
        
        JLabel titleLabel = new JLabel("Manage Student Modules");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(PANEL_COLOR);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(searchLabel);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 28));
        searchField.setToolTipText("Search by student name or email");
        searchPanel.add(searchField);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchPanel.add(clearButton);
        
        headerPanel.add(searchPanel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        ModuleManager.loadAllModules();
        UserManager.loadFromFile();
        ClassManager.loadFromFile();
        
        // Collect all student data
        ArrayList<Object[]> allStudentsData = new ArrayList<>();
        for (Users.User u : UserManager.users) {
            if (u instanceof Users.Student) {
                Users.Student student = (Users.Student) u;
                String modules = String.join(", ", student.getRegisteredModules().isEmpty() ? 
                    java.util.Collections.singletonList("None") : student.getRegisteredModules());
                allStudentsData.add(new Object[]{student.getName(), student.getEmail(), modules, "Manage"});
            }
        }
        
        // Table for students
        String[] columns = {"Student Name", "Student Email", "Registered Modules", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only Action column is editable
            }
        };
        
        // Search filter function
        Runnable updateSearch = () -> {
            model.setRowCount(0);
            String searchText = searchField.getText().trim().toLowerCase();
            
            for (Object[] row : allStudentsData) {
                String name = ((String) row[0]).toLowerCase();
                String email = ((String) row[1]).toLowerCase();
                
                if (searchText.isEmpty() || name.contains(searchText) || email.contains(searchText)) {
                    model.addRow(row);
                }
            }
        };
        
        // Add search listener
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSearch.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSearch.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSearch.run(); }
        });
        
        clearButton.addActionListener(e -> {
            searchField.setText("");
            updateSearch.run();
        });
        
        // Initialize display
        updateSearch.run();
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        
        // Add button renderer and editor for Action column
        table.getColumn("Action").setCellRenderer(new ButtonRenderer("Manage"));
        table.getColumn("Action").setCellEditor(new ManageStudentModulesEditor(
            new JCheckBox(), admin, this, model));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(PANEL_COLOR);
        
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> {
            ModuleManager.loadAllModules();
            UserManager.loadFromFile();
            ClassManager.loadFromFile();
            allStudentsData.clear();
            for (Users.User u : UserManager.users) {
                if (u instanceof Users.Student) {
                    Users.Student student = (Users.Student) u;
                    String modules = String.join(", ", student.getRegisteredModules().isEmpty() ? 
                        java.util.Collections.singletonList("None") : student.getRegisteredModules());
                    allStudentsData.add(new Object[]{student.getName(), student.getEmail(), modules, "Manage"});
                }
            }
            updateSearch.run();
        });
        
        buttonPanel.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    // Button Renderer for Action column
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
        }

        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(new Color(52, 152, 219));
            setForeground(Color.BLACK);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setText("Manage");
            return this;
        }
    }
    
    // Editor for managing student modules
    private class ManageStudentModulesEditor extends javax.swing.DefaultCellEditor {
        private JButton button;
        private String studentEmail;
        private AdminStaff admin;
        private AdminStaffFrame frame;
        private DefaultTableModel tableModel;

        public ManageStudentModulesEditor(JCheckBox checkBox, AdminStaff admin, AdminStaffFrame frame, DefaultTableModel model) {
            super(checkBox);
            this.admin = admin;
            this.frame = frame;
            this.tableModel = model;
            button = new JButton("Manage");
            button.setOpaque(true);
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> fireEditingStopped());
        }

        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.studentEmail = (String) table.getValueAt(row, 1);
            return button;
        }

        public Object getCellEditorValue() {
            showManageStudentDialog(studentEmail);
            return "Manage";
        }

        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
    
    private void showManageStudentDialog(String studentEmail) {
        ModuleManager.loadAllModules();
        UserManager.loadFromFile();
        ClassManager.loadFromFile();
        
        Users.Student student = (Users.Student) UserManager.findByEmail(studentEmail);
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Manage Modules - " + student.getName(), true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel studentLabel = new JLabel("Managing modules for: " + student.getName() + " (" + studentEmail + ")");
        studentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(studentLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(PANEL_COLOR);
        
        // Left panel - Current modules
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Current Modules"));
        JList<String> currentModulesList = new JList<>();
        updateCurrentModulesList(currentModulesList, student);
        JScrollPane leftScroll = new JScrollPane(currentModulesList);
        leftPanel.add(leftScroll, BorderLayout.CENTER);
        
        // Right panel - Available modules (declare first)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Available Modules"));
        JList<String> availableModulesList = new JList<>();
        updateAvailableModulesList(availableModulesList, student);
        JScrollPane rightScroll = new JScrollPane(availableModulesList);
        rightPanel.add(rightScroll, BorderLayout.CENTER);
        
        // Center panel - Buttons
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        centerPanel.setBackground(PANEL_COLOR);
        
        JButton assignButton = createStyledButton("Assign to Selected", new Color(39, 174, 96));
        assignButton.addActionListener(e -> {
            String selectedModule = availableModulesList.getSelectedValue();
            if (selectedModule == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a module to assign.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(dialog, 
                "Assign " + student.getName() + " to module " + selectedModule + "?", 
                "Confirm Assign", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = ModuleManager.registerStudentToModule(selectedModule, studentEmail);
                if (success) {
                    student.getRegisteredModules().add(selectedModule);
                    UserManager.saveToFile();
                    JOptionPane.showMessageDialog(dialog, "Student assigned to module successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    updateCurrentModulesList(currentModulesList, student);
                    updateAvailableModulesList(availableModulesList, student);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to assign student to module.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton removeButton = createStyledButton("Remove Selected", new Color(231, 76, 60));
        removeButton.addActionListener(e -> {
            String selectedModule = currentModulesList.getSelectedValue();
            if (selectedModule == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a module to remove.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(dialog, 
                "Remove " + student.getName() + " from module " + selectedModule + "?", 
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                ClassManager.removeStudentFromModuleClasses(studentEmail, selectedModule);
                ModuleManager.removeStudentFromModule(selectedModule, studentEmail);
                student.getRegisteredModules().remove(selectedModule);
                UserManager.saveToFile();
                JOptionPane.showMessageDialog(dialog, "Module removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateCurrentModulesList(currentModulesList, student);
                updateAvailableModulesList(availableModulesList, student);
            }
        });
        
        JButton switchButton = createStyledButton("Switch to Selected", PRIMARY_COLOR);
        switchButton.addActionListener(e -> {
            String oldModule = currentModulesList.getSelectedValue();
            String newModule = availableModulesList.getSelectedValue();
            
            if (oldModule == null || newModule == null) {
                JOptionPane.showMessageDialog(dialog, "Please select modules to switch.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(dialog, 
                "Switch from " + oldModule + " to " + newModule + "?", 
                "Confirm Switch", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                ClassManager.removeStudentFromModuleClasses(studentEmail, oldModule);
                ModuleManager.switchStudentModule(oldModule, newModule, studentEmail);
                student.getRegisteredModules().remove(oldModule);
                student.getRegisteredModules().add(newModule);
                UserManager.saveToFile();
                JOptionPane.showMessageDialog(dialog, "Module switched successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateCurrentModulesList(currentModulesList, student);
                updateAvailableModulesList(availableModulesList, student);
            }
        });
        
        centerPanel.add(assignButton);
        centerPanel.add(removeButton);
        centerPanel.add(switchButton);
        
        // Layout for left, center, right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(200);
        
        contentPanel.add(splitPane, BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.EAST);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        JButton closeButton = createStyledButton("Close", new Color(127, 140, 141));
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(PANEL_COLOR);
        bottomPanel.add(closeButton);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void updateCurrentModulesList(JList<String> list, Users.Student student) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String mod : student.getRegisteredModules()) {
            model.addElement(mod);
        }
        list.setModel(model);
    }
    
    private void updateAvailableModulesList(JList<String> list, Users.Student student) {
        DefaultListModel<String> model = new DefaultListModel<>();
        ModuleManager.loadAllModules();
        
        // Iterate through all loaded modules
        if (ModuleManager.modules != null) {
            for (Entity.Module m : ModuleManager.modules) {
                if (!student.getRegisteredModules().contains(m.getCode())) {
                    model.addElement(m.getCode());
                }
            }
        }
        list.setModel(model);
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
