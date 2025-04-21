package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ChangePassword extends JFrame {
    
    private String userName;
    private int userId;
    
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;

    public ChangePassword(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        
        setTitle("Change Password - Kurdish - O - Banking (KOB)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        // Create main content
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(26, 32, 44)); // #1a202c
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setLayout(null);

        // Sidebar title
        JLabel titleLabel = new JLabel("Kurdish - O - Banking");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 40, 200, 30);
        sidebarPanel.add(titleLabel);// Menu buttons/labels
        String[] menuItems = {"Profile", "Dashboard", "Balance", "Transactions", "Transfer", "Withdraw", "Deposit"};
        int yPos = 120;
        
        for (String item : menuItems) {
            JLabel menuLabel = new JLabel(item);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            menuLabel.setForeground(Color.WHITE);
            menuLabel.setBounds(60, yPos, 200, 30);
            
            // Highlight current page
            if (item.equals("Profile")) {
                menuLabel.setFont(new Font("Arial", Font.BOLD, 14));
                menuLabel.setForeground(new Color(173, 216, 230)); // Light blue
            }
            
            // Make labels clickable
            menuLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            menuLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    handleNavigation(item);
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (!item.equals("Profile")) { // Don't change color for current page
                        menuLabel.setForeground(new Color(200, 200, 200));
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!item.equals("Profile")) { // Don't change color for current page
                        menuLabel.setForeground(Color.WHITE);
                    }
                }
            });
            
            sidebarPanel.add(menuLabel);
            yPos += 40;
        }
        
        return sidebarPanel;
    }
    
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(240, 244, 248)); // #f0f4f8
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        
        // Content container (white panel)
        RoundedPanel contentPanel = new RoundedPanel(10);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(30, 30, 470, 500);
        contentPanel.setLayout(null);
        mainPanel.add(contentPanel);
        
        // Title
        JLabel titleLabel = new JLabel("Change Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64)); // #343a40
        titleLabel.setBounds(40, 30, 250, 30);
        contentPanel.add(titleLabel);
        
        // Subtitle with username
        JLabel subtitleLabel = new JLabel("Update password for " + userName);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125)); // Gray
        subtitleLabel.setBounds(40, 60, 350, 20);
        contentPanel.add(subtitleLabel);
        
        // Form fields
        int yPos = 120;
        int fieldHeight = 35;
        int labelOffset = 25;
        int gap = 65;
        
        // Current password
        JLabel currentPassLabel = new JLabel("Current Password");
        currentPassLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        currentPassLabel.setBounds(40, yPos, 150, 20);
        contentPanel.add(currentPassLabel);
        
        currentPasswordField = new JPasswordField();
        currentPasswordField.setBounds(40, yPos + labelOffset, 390, fieldHeight);
        currentPasswordField.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        contentPanel.add(currentPasswordField);
        
        // New password
        JLabel newPassLabel = new JLabel("New Password");
        newPassLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        newPassLabel.setBounds(40, yPos + gap, 150, 20);
        contentPanel.add(newPassLabel);
        
        newPasswordField = new JPasswordField();newPasswordField.setBounds(40, yPos + gap + labelOffset, 390, fieldHeight);
        newPasswordField.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        contentPanel.add(newPasswordField);
        
        // Confirm new password
        JLabel confirmPassLabel = new JLabel("Confirm New Password");
        confirmPassLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPassLabel.setBounds(40, yPos + (gap * 2), 200, 20);
        contentPanel.add(confirmPassLabel);
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(40, yPos + (gap * 2) + labelOffset, 390, fieldHeight);
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        contentPanel.add(confirmPasswordField);
        
        // Status label for feedback
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(220, 53, 69)); // Red for errors
        statusLabel.setBounds(40, yPos + (gap * 3), 390, 20);
        contentPanel.add(statusLabel);
        
        // Buttons
        RoundedButton updateButton = new RoundedButton("Update Password", 5);
        updateButton.setBackground(new Color(13, 110, 253)); // #0d6efd - Bootstrap primary blue
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        updateButton.setBounds(40, yPos + (gap * 3) + 40, 180, 40);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
        contentPanel.add(updateButton);
        
        RoundedButton cancelButton = new RoundedButton("Cancel", 5);
        cancelButton.setBackground(new Color(248, 249, 250)); // Light gray
        cancelButton.setForeground(new Color(33, 37, 41)); // Dark text
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setBounds(250, yPos + (gap * 3) + 40, 180, 40);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Return to user profile
                navigateToUserProfile();
            }
        });
        contentPanel.add(cancelButton);
        
        // Security tips
        JPanel tipsPanel = new RoundedPanel(5);
        tipsPanel.setBackground(new Color(248, 249, 250)); // Light gray
        tipsPanel.setBounds(40, yPos + (gap * 3) + 100, 390, 130);
        tipsPanel.setLayout(new BoxLayout(tipsPanel, BoxLayout.Y_AXIS));
        tipsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel tipsTitle = new JLabel("Password Tips:");
        tipsTitle.setFont(new Font("Arial", Font.BOLD, 13));
        tipsTitle.setAlignmentX(LEFT_ALIGNMENT);
        tipsPanel.add(tipsTitle);
        tipsPanel.add(Box.createVerticalStrut(10));
        
        String[] tips = {
            "• Use at least 8 characters",
            "• Include uppercase and lowercase letters",
            "• Include numbers and special characters",
            "• Don't reuse passwords from other sites"
        };
        
        for (String tip : tips) {
            JLabel tipLabel = new JLabel(tip);
            tipLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            tipLabel.setForeground(new Color(73, 80, 87)); // Darker gray
            tipLabel.setAlignmentX(LEFT_ALIGNMENT);
            tipsPanel.add(tipLabel);
            tipsPanel.add(Box.createVerticalStrut(5));
        }
        
        contentPanel.add(tipsPanel);
        
        return mainPanel;
    }
    
    private void changePassword() {
        // Clear previous status
        statusLabel.setText("");
        
        // Get password values
        char[] currentPass = currentPasswordField.getPassword();
        char[] newPass = newPasswordField.getPassword();char[] confirmPass = confirmPasswordField.getPassword();
        
        // Validate inputs
      // Validate inputs
        if (currentPass.length == 0 ||  newPass.length == 0 || confirmPass.length == 0) {
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
    
    private void handleNavigation(String destination) {
        this.dispose(); // Close current window
        
        switch (destination) {
            case "Profile":
                navigateToUserProfile();
                break;
            case "Dashboard":
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();dashboard.setUserInfo(userName, userId);
                    dashboard.setVisible(true);
                });
                break;
            case "Balance":
                SwingUtilities.invokeLater(() -> {
                    BalancePage balancePage = new BalancePage(userName, userId);
                    balancePage.setVisible(true);
                });
                break;
            case "Transactions":
                SwingUtilities.invokeLater(() -> {
                    Transaction transactionScreen = new Transaction(userId, userName);
                    transactionScreen.setVisible(true);
                });
                break;
            case "Transfer":
                SwingUtilities.invokeLater(() -> {
                    Transfer transferScreen = new Transfer();
                    transferScreen.setUserInfo(userName, userId);
                    transferScreen.setVisible(true);
                });
                break;
            case "Withdraw":
                SwingUtilities.invokeLater(() -> {
                    Withdraw withdrawScreen = new Withdraw();
                    withdrawScreen.setUserInfo(userName, userId);
                    withdrawScreen.setVisible(true);
                });
                break;
            case "Deposit":
                SwingUtilities.invokeLater(() -> {
                    Deposite depositScreen = new Deposite();
                    depositScreen.setUserInfo(userName, userId);
                    depositScreen.setVisible(true);
                });
                break;
            default:
                break;
        }
    }
    
    // Custom Rounded Panel class
    static class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int cornerRadius) {
            super();
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    // Custom Rounded Button class
    static class RoundedButton extends JButton {
        private int cornerRadius;

        public RoundedButton(String text, int cornerRadius) {
            super(text);
            this.cornerRadius = cornerRadius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}