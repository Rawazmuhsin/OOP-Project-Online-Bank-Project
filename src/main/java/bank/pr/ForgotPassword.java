package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ForgotPassword extends JFrame {

    private JTextField[] fields;
    private String generatedOTP;
    private String userEmail;
    private boolean otpVerified = false;

    public ForgotPassword() {
        setTitle("Forgot Password");
        setSize(700, 850); // Adjusted height to match SignUp page
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(230, 233, 239));
        setLayout(new BorderLayout());

        // Main white panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Back button in top-left
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(new Color(66, 103, 244));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            this.dispose();
            new LoginUI().setVisible(true);
        });

        // Create a panel for the back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(backButton);
        mainPanel.add(topPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Header
        JLabel headerLabel = new JLabel("Forgot Password");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(66, 103, 244));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Instructions
        JLabel instructionLabel = new JLabel("Please enter your email or username to reset your password");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        instructionLabel.setForeground(new Color(120, 120, 120));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(instructionLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Fields
        String[] labels = {
            "Email or Username",
            "Authentication Code",
            "New Password",
            "Confirm New Password"
        };
        fields = new JTextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
            fieldPanel.setBackground(Color.WHITE);
            fieldPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel fieldLabel = new JLabel(labels[i]);
            fieldLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            fieldPanel.add(fieldLabel);fields[i] = labels[i].toLowerCase().contains("password") ? new JPasswordField() : new JTextField();
            fields[i].setFont(new Font("Arial", Font.PLAIN, 16));
            fields[i].setMaximumSize(new Dimension(600, 50));
            fields[i].setPreferredSize(new Dimension(600, 50));
            fields[i].setBackground(new Color(250, 252, 255));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            if (i > 0) {
                fields[i].setEnabled(false);
            }
            fieldPanel.add(fields[i]);
            mainPanel.add(fieldPanel);

            if (i < labels.length - 1) {
                mainPanel.add(Box.createRigidArea(new Dimension(0, 25))); // Consistent spacing
            }
        }

        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Send OTP Button
        JButton sendOTPButton = createStyledButton("SEND OTP", 600, 60);
        sendOTPButton.addActionListener(e -> sendOTP());
        sendOTPButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(sendOTPButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Reset Password Button
        JButton resetButton = createStyledButton("RESET PASSWORD", 600, 60);
        resetButton.addActionListener(e -> resetPassword());
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(resetButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Footer note
        JLabel noteLabel = new JLabel("<html><center>For security purposes, you may be asked to verify additional information.<br>Contact support if you encounter any issues.</center></html>");
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        noteLabel.setForeground(new Color(130, 130, 130));
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(noteLabel);

        // Center the panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(230, 233, 239));
        centerPanel.add(mainPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(66, 103, 244));
        button.setForeground(Color.WHITE);
        button.setMaximumSize(new Dimension(width, height));
        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }private void sendOTP() {
        String emailOrUsername = fields[0].getText().trim();
        if (emailOrUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your email or username", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT email FROM accounts WHERE email = ? OR name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, emailOrUsername);
            stmt.setString(2, emailOrUsername);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userEmail = rs.getString("email");
                generatedOTP = generateOTP();
                JOptionPane.showMessageDialog(this, 
                    "Your OTP is: " + generatedOTP + "\n(For demo purposes only)", 
                    "OTP Verification", 
                    JOptionPane.INFORMATION_MESSAGE);
                fields[1].setEnabled(true);
                fields[1].requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "No account found with that email/username", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void resetPassword() {
        if (!otpVerified) {
            String enteredOTP = fields[1].getText().trim();
            if (enteredOTP.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the OTP", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!enteredOTP.equals(generatedOTP)) {
                JOptionPane.showMessageDialog(this, "Invalid OTP", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            otpVerified = true;
            fields[2].setEnabled(true);
            fields[3].setEnabled(true);
            fields[2].requestFocus();
            JOptionPane.showMessageDialog(this, "OTP verified. Please enter new password.", "Success", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String newPassword = fields[2].getText();
        String confirmPassword = fields[3].getText();
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter and confirm your new password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE accounts SET password = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword);
            stmt.setString(2, userEmail);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Password reset successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new LoginUI().setVisible(true); // Navigate to login page
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    }public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ForgotPassword().setVisible(true));
    }
}