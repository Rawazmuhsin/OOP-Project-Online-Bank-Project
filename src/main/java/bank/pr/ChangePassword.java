package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ChangePassword extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Colors scheme - same as other screens
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    
    // Components
    private String userName;
    private int userId;
    private JLabel greeting;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private List<JButton> menuButtons = new ArrayList<>();
    private JPanel sidebarPanel;

    public ChangePassword(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Kurdish-O-Banking (KOB) - Change Password");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create components
        createSidebar();
        createHeaderPanel();
        createMainContent();
        
        // Set icon if available
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
    }
    
    private void createSidebar() {
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR, 
                    0, getHeight(), new Color(10, 20, 50)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        
        sidebarPanel.setPreferredSize(new Dimension(240, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        
        // Logo panel
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
        
        JLabel bankName = new JLabel("Kurdish-O-Banking");
        bankName.setForeground(Color.WHITE);
        bankName.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        JLabel tagline = new JLabel("Your Future, Your Bank");
        tagline.setForeground(new Color(200, 200, 200));
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 12));
        
        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.add(bankName);
        namePanel.add(Box.createVerticalStrut(3));
        namePanel.add(tagline);
        
        logoPanel.add(namePanel, BorderLayout.CENTER);
        
        // Try to add logo image if available
        try {
            ImageIcon logoIcon = new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png");
            java.awt.Image image = logoIcon.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(image);
            JLabel logoLabel = new JLabel(logoIcon);
            logoPanel.add(logoLabel, BorderLayout.WEST);
        } catch (Exception e) {
            System.err.println("Error loading small logo: " + e.getMessage());
        }
        
        sidebarPanel.add(logoPanel);
        
        // Add separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(70, 80, 120));
        separator.setBackground(new Color(70, 80, 120));
        separator.setMaximumSize(new Dimension(220, 1));
        sidebarPanel.add(separator);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Menu items with icons
        String[] menuItems = {"Dashboard", "Balance", "Accounts", "Deposit", "Withdraw", "Transfers", "Transactions", "Cards", "QR Codes"};
        String[] iconNames = {"dashboard", "balance", "accounts", "deposit", "withdraw", "transfers", "transactions", "cards", "qrcode"};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton button = createMenuButton(menuItems[i], iconNames[i]);
            
            final String item = menuItems[i];
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleButtonClick(item);
                    updateSelectedButton(item);
                }
            });
            
            sidebarPanel.add(button);
            menuButtons.add(button);
            sidebarPanel.add(Box.createVerticalStrut(5));
        }
        
        // Set Accounts as initially selected
        updateSelectedButton("Accounts");
        
        // Add logout at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        JSeparator bottomSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        bottomSeparator.setForeground(new Color(70, 80, 120));
        bottomSeparator.setBackground(new Color(70, 80, 120));
        bottomSeparator.setMaximumSize(new Dimension(220, 1));
        sidebarPanel.add(bottomSeparator);
        
        JButton logoutButton = createMenuButton("Logout", "logout");
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                // Open login page
                SwingUtilities.invokeLater(() -> {
                    LoginUI loginPage = new LoginUI();
                    loginPage.setVisible(true);
                });
            }
        });
        
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private JButton createMenuButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.setMaximumSize(new Dimension(220, 45));
        button.setPreferredSize(new Dimension(220, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));
        
        // Try to load icon if available
        try {
            ImageIcon icon = new ImageIcon("icons/" + iconName + ".png");
            java.awt.Image image = icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            button.setIcon(icon);
        } catch (Exception e) {
            // If icon not found, use text only
            System.err.println("Icon not found: " + iconName);
        }
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setBackground(new Color(45, 55, 95));
                    button.setContentAreaFilled(true);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setContentAreaFilled(false);
                }
            }
        });
        
        return button;
    }
    
    private void updateSelectedButton(String selectedItem) {
        for (JButton button : menuButtons) {
            if (button.getText().equals(selectedItem)) {
                button.setBackground(SECONDARY_COLOR);
                button.setContentAreaFilled(true);
                button.setFont(new Font("SansSerif", Font.BOLD, 14));
                button.putClientProperty("selected", true);
            } else {
                button.setContentAreaFilled(false);
                button.setFont(new Font("SansSerif", Font.PLAIN, 14));
                button.putClientProperty("selected", false);
            }
        }
    }
    
    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        // Greeting panel
        JPanel greetingPanel = new JPanel(new BorderLayout());
        greetingPanel.setOpaque(false);
        
        greeting = new JLabel("Change Password");
        greeting.setFont(new Font("SansSerif", Font.BOLD, 20));
        greeting.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel("Update your account security");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        greetingPanel.add(greeting, BorderLayout.NORTH);
        greetingPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(greetingPanel, BorderLayout.WEST);
        
        // Current date/time panel
        JPanel dateTimePanel = new JPanel(new BorderLayout());
        dateTimePanel.setOpaque(false);
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
        
        JLabel dateLabel = new JLabel(now.format(dateFormatter));
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel timeLabel = new JLabel(now.format(timeFormatter));
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        timeLabel.setForeground(LIGHT_TEXT_COLOR);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        dateTimePanel.add(dateLabel, BorderLayout.NORTH);
        dateTimePanel.add(timeLabel, BorderLayout.SOUTH);
        
        headerPanel.add(dateTimePanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createMainContent() {
        // Main panel with GridBagLayout for responsive design
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Create change password panel
        JPanel passwordPanel = createSectionPanel("Update Password");
        passwordPanel.setLayout(new BorderLayout());
        
        // Create form content
        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setOpaque(false);
        formContent.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        // User info section
        JLabel userInfoLabel = new JLabel("Changing password for: " + userName);
        userInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userInfoLabel.setForeground(LIGHT_TEXT_COLOR);
        userInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContent.add(userInfoLabel);
        formContent.add(Box.createVerticalStrut(20));
        
        // Current password field
        JPanel currentPassPanel = createPasswordField("Current Password", currentPasswordField = new JPasswordField());
        formContent.add(currentPassPanel);
        formContent.add(Box.createVerticalStrut(20));
        
        // New password field
        JPanel newPassPanel = createPasswordField("New Password", newPasswordField = new JPasswordField());
        formContent.add(newPassPanel);
        formContent.add(Box.createVerticalStrut(20));
        
        // Confirm password field
        JPanel confirmPassPanel = createPasswordField("Confirm New Password", confirmPasswordField = new JPasswordField());
        formContent.add(confirmPassPanel);
        formContent.add(Box.createVerticalStrut(15));
        
        // Status label for feedback
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formContent.add(statusLabel);
        formContent.add(Box.createVerticalStrut(25));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton updateButton = new JButton("Update Password");
        updateButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        updateButton.setBackground(SECONDARY_COLOR);
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.addActionListener(e -> changePassword());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cancelButton.setBackground(new Color(240, 240, 240));
        cancelButton.setForeground(TEXT_COLOR);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> navigateToUserProfile());
        
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        
        formContent.add(buttonPanel);
        
        passwordPanel.add(formContent, BorderLayout.CENTER);
        
        // Password tips panel
        JPanel tipsPanel = createSectionPanel("Password Requirements");
        tipsPanel.setLayout(new BorderLayout());
        
        JPanel tipsContent = new JPanel();
        tipsContent.setLayout(new BoxLayout(tipsContent, BoxLayout.Y_AXIS));
        tipsContent.setOpaque(false);
        tipsContent.setBorder(BorderFactory.createEmptyBorder(15, 25, 25, 25));
        
        // Add tips
        JLabel tipsTitle = new JLabel("For a strong password:");
        tipsTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        tipsTitle.setForeground(TEXT_COLOR);
        tipsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        tipsContent.add(tipsTitle);
        tipsContent.add(Box.createVerticalStrut(15));
        
        String[] tips = {
            "• Use at least 8 characters",
            "• Include uppercase and lowercase letters",
            "• Include numbers and special characters",
            "• Avoid using personal information",
            "• Don't reuse passwords from other websites"
        };
        
        for (String tip : tips) {
            JLabel tipLabel = new JLabel(tip);
            tipLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            tipLabel.setForeground(LIGHT_TEXT_COLOR);
            tipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            tipsContent.add(tipLabel);
            tipsContent.add(Box.createVerticalStrut(10));
        }
        
        tipsPanel.add(tipsContent, BorderLayout.CENTER);
        
        // Add components to main panel with responsive layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        mainPanel.add(passwordPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        mainPanel.add(tipsPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(CARD_COLOR);
        titleBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        titleBar.add(titleLabel, BorderLayout.WEST);
        
        panel.add(titleBar, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createPasswordField(String labelText, JPasswordField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setPreferredSize(new Dimension(Short.MAX_VALUE, 40));
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void changePassword() {
        // Clear previous status
        statusLabel.setText("");
        
        // Get password values
        char[] currentPass = currentPasswordField.getPassword();
        char[] newPass = newPasswordField.getPassword();
        char[] confirmPass = confirmPasswordField.getPassword();
        
        // Validate inputs
        if (currentPass.length == 0 || newPass.length == 0 || confirmPass.length == 0) {
            statusLabel.setText("All fields are required");
            return;
        }
        
        // Check if new password matches confirmation
        if (!new String(newPass).equals(new String(confirmPass))) {
            statusLabel.setText("New passwords don't match");
            return;
        }
        
        // Check minimum password length
        if (newPass.length < 4) { // In a real app, this would be 8+
            statusLabel.setText("New password must be at least 4 characters");
            return;
        }
        
        // Proceed with password change
        boolean success = updatePasswordInDatabase(new String(currentPass), new String(newPass));
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                    "Password has been updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Return to user profile
            navigateToUserProfile();
        }
        
        // Clear password fields for security
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }
    
    private boolean updatePasswordInDatabase(String currentPassword, String newPassword) {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // 1. Verify current password
            String verifyQuery = "SELECT password FROM accounts WHERE account_id = ?";
            PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery);
            verifyStmt.setInt(1, userId);
            ResultSet rs = verifyStmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                // Compare the entered current password with the stored password
                // Note: In a real system, you'd use a secure password hashing library
                if (!currentPassword.equals(storedPassword)) {
                    statusLabel.setText("Current password is incorrect");
                    return false;
                }
            } else {
                statusLabel.setText("User account not found");
                return false;
            }
            
            // 2. Update password in database
            String updateQuery = "UPDATE accounts SET password = ? WHERE account_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, newPassword); // In production, hash the password
            updateStmt.setInt(2, userId);
            
            int rowsAffected = updateStmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void navigateToUserProfile() {
        SwingUtilities.invokeLater(() -> {
            UserProfile profile = new UserProfile();
            profile.setUserInfo(userName, userId);
            profile.setVisible(true);
            this.dispose();
        });
    }
    
    private void handleButtonClick(String buttonName) {
        System.out.println("Button clicked: " + buttonName);
        
        switch (buttonName) {
            case "Dashboard":
                // Go to Dashboard page
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, userId);
                    dashboard.setVisible(true);
                    this.dispose();
                });
                break;
            case "Balance":
                // Go to Balance page
                SwingUtilities.invokeLater(() -> {
                    BalancePage balancePage = new BalancePage(userName, userId);
                    balancePage.setVisible(true);
                    this.dispose();
                });
                break;
            case "Accounts":
                // Go to User Profile page
                navigateToUserProfile();
                break;
            case "Deposit":
                // Go to Deposit page
                SwingUtilities.invokeLater(() -> {
                    Deposite depositScreen = new Deposite();
                    depositScreen.setUserInfo(userName, userId);
                    depositScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Withdraw":
                // Go to Withdraw page
                SwingUtilities.invokeLater(() -> {
                    Withdraw withdrawScreen = new Withdraw();
                    withdrawScreen.setUserInfo(userName, userId);
                    withdrawScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transfers":
                // Go to transfers page
                SwingUtilities.invokeLater(() -> {
                    Transfer transferScreen = new Transfer();
                    transferScreen.setUserInfo(userName, userId);
                    transferScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transactions":
                // Go to transactions page
                SwingUtilities.invokeLater(() -> {
                    Transaction transactionScreen = new Transaction(userId, userName);
                    transactionScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Cards":
                // Cards feature not implemented yet
                JOptionPane.showMessageDialog(this, "Cards feature coming soon!", "Information", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "QR Codes":
                // Show QR Codes tab on dashboard
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, userId);
                    dashboard.setVisible(true);
                    // Switch to QR Codes tab handled in dashboard
                    this.dispose();
                });
                break;
            default:
                break;
        }
    }
    
    // Custom gradient panel for backgrounds with rounded corners
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color gradientStart;
        private Color gradientEnd;
        
        public RoundedPanel(int radius, Color baseColor) {
            super();
            this.cornerRadius = radius;
            this.gradientStart = baseColor;
            this.gradientEnd = darkenColor(baseColor, 0.2f);
            setOpaque(false);
        }
        
        private Color darkenColor(Color color, float factor) {
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - factor));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            GradientPaint gradient = new GradientPaint(
                0, 0, gradientStart,
                0, getHeight(), gradientEnd
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2d.dispose();
        }
    }
}