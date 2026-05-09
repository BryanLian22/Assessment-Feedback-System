package GUI;

import IOManage.UserManager;
import Users.User;
import Users.Student;
import Users.Lecturer;
import Users.AcademicLeader;
import Users.AdminStaff;
import GUI.frames.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private int attemptCount = 0;
    private static final int MAX_ATTEMPTS = 5;
    
    // Professional color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(245, 246, 250);
    private static final Color PANEL_COLOR = Color.WHITE;

    public LoginFrame() {
        try {
            UserManager.loadFromFile();
        } catch (Exception e) {
            System.err.println("Warning: Could not load users from file: " + e.getMessage());
            // Continue anyway - users might not exist yet
        }
        
        try {
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            applyModernLook();
        } catch (Exception e) {
            System.err.println("Error initializing LoginFrame: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error initializing login window: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeComponents() {
        setTitle("APU Assessment Feedback System (AFS)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 600); // Made larger to fit password field
        setMinimumSize(new Dimension(550, 600)); // Ensure minimum size
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing in case user needs more space
        getContentPane().setBackground(BACKGROUND_COLOR);

        emailField = createStyledTextField("Enter your email");
        
        // Create a simple, working password field with proper visibility
        passwordField = new JPasswordField(20); // 20 columns for width
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setMinimumSize(new Dimension(300, 40));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBackground(PANEL_COLOR);
        passwordField.setEchoChar('●');
        passwordField.setForeground(Color.BLACK);
        passwordField.setVisible(true);
        passwordField.setEnabled(true);
        
        // Add focus listener for border color change
        passwordField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBackground(PANEL_COLOR);
        
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(149, 165, 166));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        field.setForeground(new Color(149, 165, 166));
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        // Use a simpler approach with a label overlay for placeholder
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBackground(PANEL_COLOR);
        field.setEchoChar('●'); // Always show password dots
        field.setForeground(Color.BLACK);
        
        // Create a label for placeholder text
        JLabel placeholderLabel = new JLabel(placeholder);
        placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        placeholderLabel.setForeground(new Color(149, 165, 166));
        placeholderLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                placeholderLabel.setVisible(false);
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    placeholderLabel.setVisible(true);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        // Actually, let's use a simpler approach - just an empty password field with placeholder text as a label
        // But for now, let's fix the issue by making it work like a normal password field
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor, Color hoverColor, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (!isEnabled()) {
                    g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 100));
                } else if (getModel().isPressed() && getModel().isArmed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setEnabled(true);
        button.setDefaultCapable(true);
        
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel with gradient effect
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(getWidth(), 120));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("APU ASSESSMENT FEEDBACK SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Welcome Back", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setPreferredSize(new Dimension(500, 500)); // Ensure enough space

        // Form Panel with shadow effect
        JPanel formPanel = new JPanel();
        formPanel.setBackground(PANEL_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        formPanel.setLayout(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(450, 400)); // Made taller to fit all fields
        formPanel.setMinimumSize(new Dimension(450, 400));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Email Label and Field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);

        // Password Label and Field - make sure it's visible
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(52, 73, 94));
        passwordLabel.setVisible(true);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(20, 10, 5, 10); // Add more space before password field
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 10, 15, 10);
        // Ensure password field is visible and properly sized
        passwordField.setVisible(true);
        passwordField.setEnabled(true);
        formPanel.add(passwordField, gbc);
        
        // Debug: Print to confirm password field is added
        System.out.println("Password field added to form. Size: " + passwordField.getPreferredSize());

        // Status Label
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        formPanel.add(statusLabel, gbc);

        contentPanel.add(formPanel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton loginButton = createStyledButton("Login", PRIMARY_COLOR, SECONDARY_COLOR, 130, 45);
        JButton exitButton = createStyledButton("Exit", new Color(149, 165, 166), new Color(127, 140, 141), 130, 45);

        // Add action listener with debugging
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("=== LOGIN BUTTON CLICKED ===");
                System.out.println("Action command: " + e.getActionCommand());
                handleLogin();
            }
        });
        
        // Ensure button is enabled and visible
        loginButton.setEnabled(true);
        loginButton.setVisible(true);
        
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(LoginFrame.this, 
                    "Are you sure you want to exit?", 
                    "Exit Application", 
                    JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        passwordField.addActionListener(e -> handleLogin());

        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Already set up in setupLayout()
    }

    private void applyModernLook() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // Use default look and feel
        }
    }

    private void handleLogin() {
        System.out.println("Login button clicked!");
        
        String email = emailField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());
        
        System.out.println("Email: " + email);
        System.out.println("Password length: " + password.length());
        
        // Check if fields are empty
        if (email.equals("Enter your email") || email.isEmpty()) {
            System.out.println("Email is empty or placeholder");
            showStatus("Please enter your email", ERROR_COLOR);
            return;
        }
        
        // Password field should be empty if no password entered
        if (password.isEmpty() || password.length() == 0) {
            System.out.println("Password is empty");
            showStatus("Please enter your password", ERROR_COLOR);
            return;
        }

        System.out.println("Attempting login...");
        User currentUser = UserManager.login(email, password);
        System.out.println("Login result: " + (currentUser != null ? "SUCCESS" : "FAILED"));

        if (currentUser != null) {
            attemptCount = 0;
            showStatus("Login successful! Redirecting...", SUCCESS_COLOR);
            
            // Delay to show success message
            Timer timer = new Timer(500, e -> {
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    if (currentUser instanceof Student) {
                        new StudentFrame((Student) currentUser).setVisible(true);
                    } else if (currentUser instanceof Lecturer) {
                        new LecturerFrame((Lecturer) currentUser).setVisible(true);
                    } else if (currentUser instanceof AcademicLeader) {
                        new AcademicLeaderFrame((AcademicLeader) currentUser).setVisible(true);
                    } else if (currentUser instanceof AdminStaff) {
                        new AdminStaffFrame((AdminStaff) currentUser).setVisible(true);
                    }
                });
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            attemptCount++;
            int remaining = MAX_ATTEMPTS - attemptCount;
            if (remaining > 0) {
                showStatus("Invalid credentials. " + remaining + " attempt(s) remaining", ERROR_COLOR);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Maximum login attempts reached. Application will exit.", 
                    "Login Failed", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
}
