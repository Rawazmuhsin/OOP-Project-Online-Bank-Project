package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ForgotPassword extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Colors scheme - same as Dashboard and Login
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);

    private JTextField[] fields;
    private String generatedOTP;
    private String userEmail;
    private boolean otpVerified = false;
    private JLabel statusLabel;

    public ForgotPassword() {
        // Set look and feel to be more modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Forgot Password - Kurdish-O-Banking");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Left sidebar with gradient background
        JPanel sidebar = new JPanel() {
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
        
        sidebar.setPreferredSize(new Dimension(350, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
        
        // Add logo to sidebar
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png");
            java.awt.Image image = logoIcon.getImage().getScaledInstance(150, 100, java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(image);
            logoLabel.setIcon(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
        
        // Bank name and tagline
        JLabel bankNameLabel = new JLabel("Kurdish-O-Banking");
        bankNameLabel.setForeground(Color.WHITE);
        bankNameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        bankNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel taglineLabel = new JLabel("Your Future, Your Bank");
        taglineLabel.setForeground(new Color(200, 200, 200));
        taglineLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoLabel);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(bankNameLabel);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(taglineLabel);
        sidebar.add(Box.createVerticalStrut(40));
        
        // Recovery information panel
        JPanel infoPanel = new RoundedPanel(15, new Color(40, 50, 90, 180));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(300, 250));
        
        JLabel infoTitle = new JLabel("Account Recovery");
        infoTitle.setForeground(Color.WHITE);
        infoTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoText = new JLabel("<html>Follow these steps to reset your password:<br><br>" +
                "1. Enter your email or username<br>" +
                "2. Enter the OTP code sent to you<br>" +
                "3. Create a new secure password<br><br>" +
                "Your account security is important to us.</html>");
        infoText.setForeground(new Color(220, 220, 220));
        infoText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(infoText);
        
        sidebar.add(infoPanel);
        sidebar.add(Box.createVerticalGlue());
        
        // Main content area with recovery form
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Recovery form card
        JPanel formCard = new RoundedPanel(20, CARD_COLOR);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        formCard.setPreferredSize(new Dimension(450, 580));
        
        // Back button
        JLabel backButton = new JLabel("← Back to Login");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backButton.setForeground(SECONDARY_COLOR);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginUI().setVisible(true);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setText("<html><u>← Back to Login</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setText("← Back to Login");
            }
        });
        
        // Form title
        JLabel titleLabel = new JLabel("Reset Your Password");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("We'll help you recover your account in a few steps");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Status label for showing errors or success messages
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(ERROR_COLOR);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formCard.add(backButton);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(titleLabel);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(subtitleLabel);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(statusLabel);
        formCard.add(Box.createVerticalStrut(20));
        
        // Form fields
        String[] labels = {
            "Email or Username",
            "Authentication Code",
            "New Password",
            "Confirm New Password"
        };
        fields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            JLabel fieldLabel = new JLabel(labels[i]);
            fieldLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            fieldLabel.setForeground(TEXT_COLOR);
            fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            formCard.add(fieldLabel);
            formCard.add(Box.createVerticalStrut(10));
            
            fields[i] = labels[i].toLowerCase().contains("password") ? 
                        new JPasswordField() : new JTextField();
            fields[i].setFont(new Font("SansSerif", Font.PLAIN, 14));
            fields[i].setMaximumSize(new Dimension(400, 40));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            fields[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Disable fields that should be initially disabled
            if (i > 0) {
                fields[i].setEnabled(false);
            }
            
            formCard.add(fields[i]);
            formCard.add(Box.createVerticalStrut(20));
        }
        
        // Buttons
        JButton sendOTPButton = new JButton("Send Verification Code");
        sendOTPButton.setBackground(SECONDARY_COLOR);
        sendOTPButton.setForeground(Color.WHITE);
        sendOTPButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        sendOTPButton.setFocusPainted(false);
        sendOTPButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendOTPButton.setMaximumSize(new Dimension(400, 50));
        sendOTPButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton resetButton = new JButton("Reset Password");
        resetButton.setBackground(ACCENT_COLOR);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        resetButton.setFocusPainted(false);
        resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetButton.setMaximumSize(new Dimension(400, 50));
        resetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add hover effects
        sendOTPButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendOTPButton.setBackground(new Color(25, 118, 210));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                sendOTPButton.setBackground(SECONDARY_COLOR);
            }
        });
        
        resetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                resetButton.setBackground(new Color(235, 145, 0));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                resetButton.setBackground(ACCENT_COLOR);
            }
        });
        
        // Add action listeners
        sendOTPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendOTP();
            }
        });
        
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetPassword();
            }
        });
        
        formCard.add(sendOTPButton);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(resetButton);
        
        // Add note at the bottom
        formCard.add(Box.createVerticalStrut(20));
        JLabel noteLabel = new JLabel("<html><small>For security purposes, you may be asked to verify additional information.<br>Contact support if you encounter any issues.</small></html>");
        noteLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        noteLabel.setForeground(LIGHT_TEXT_COLOR);
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(noteLabel);
        
        // Add the form card to the main panel
        mainPanel.add(formCard);
        
        // Add panels to frame
        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        
        // Try to set icon
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
    }
    
    private void sendOTP() {
        String emailOrUsername = fields[0].getText().trim();
        
        if (emailOrUsername.isEmpty()) {
            statusLabel.setText("Please enter your email or username");
            statusLabel.setForeground(ERROR_COLOR);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Updated SQL query to match database column names
            String sql = "SELECT email FROM accounts WHERE email = ? OR username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, emailOrUsername);
            stmt.setString(2, emailOrUsername);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                userEmail = rs.getString("email");
                generatedOTP = generateOTP();
                
                // For demo purposes, show the OTP (in a real app, this would be sent via email)
                JOptionPane.showMessageDialog(this, 
                    "Your OTP is: " + generatedOTP + "\n(For demo purposes only)", 
                    "OTP Verification", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                fields[1].setEnabled(true);
                fields[1].requestFocus();
                
                statusLabel.setText("Verification code sent to " + maskEmail(userEmail));
                statusLabel.setForeground(SUCCESS_COLOR);
            } else {
                statusLabel.setText("No account found with that email/username");
                statusLabel.setForeground(ERROR_COLOR);
            }
        } catch (SQLException ex) {
            statusLabel.setText("Database error: " + ex.getMessage());
            statusLabel.setForeground(ERROR_COLOR);
            ex.printStackTrace();
        }
    }
    
    private String maskEmail(String email) {
        if (email == null || email.isEmpty() || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        
        String maskedName = name.substring(0, Math.min(3, name.length())) + 
                           "*".repeat(Math.max(0, name.length() - 3));
        
        return maskedName + "@" + domain;
    }

    private void resetPassword() {
        if (!otpVerified) {
            String enteredOTP = fields[1].getText().trim();
            
            if (enteredOTP.isEmpty()) {
                statusLabel.setText("Please enter the verification code");
                statusLabel.setForeground(ERROR_COLOR);
                return;
            }
            
            if (!enteredOTP.equals(generatedOTP)) {
                statusLabel.setText("Invalid verification code");
                statusLabel.setForeground(ERROR_COLOR);
                return;
            }
            
            otpVerified = true;
            fields[2].setEnabled(true);
            fields[3].setEnabled(true);
            fields[2].requestFocus();
            
            statusLabel.setText("Verification successful. Please enter your new password.");
            statusLabel.setForeground(SUCCESS_COLOR);
            return;
        }
        
        String newPassword = new String(((JPasswordField)fields[2]).getPassword());
        String confirmPassword = new String(((JPasswordField)fields[3]).getPassword());
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Please enter and confirm your new password");
            statusLabel.setForeground(ERROR_COLOR);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            statusLabel.setForeground(ERROR_COLOR);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE accounts SET password = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword);
            stmt.setString(2, userEmail);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Password reset successfully! You can now log in with your new password.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                this.dispose();
                new LoginUI().setVisible(true);
            } else {
                statusLabel.setText("Failed to reset password");
                statusLabel.setForeground(ERROR_COLOR);
            }
        } catch (SQLException ex) {
            statusLabel.setText("Database error: " + ex.getMessage());
            statusLabel.setForeground(ERROR_COLOR);
            ex.printStackTrace();
        }
    }

    private String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
    
    // Custom rounded panel class - same as in Dashboard
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;
        
        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2d.dispose();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ForgotPassword().setVisible(true);
            }
        });
    }
}