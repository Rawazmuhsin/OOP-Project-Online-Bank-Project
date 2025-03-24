package bank.pr;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ForgotPasswordApp {
    public static void main(String[] args) {
        new EmailVerificationPage();
    }
}

class EmailVerificationPage extends JFrame {
    private JTextField emailField;
    private static String userEmail;

    public EmailVerificationPage() {
        setTitle("Forgot Password - Step 1");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Enter Your Email", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(emailField, gbc);

        JButton nextButton = new JButton("Next");
        nextButton.setBackground(Color.ORANGE);
        nextButton.setForeground(Color.WHITE);
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(nextButton, gbc);

        nextButton.addActionListener(e -> {
            userEmail = emailField.getText().trim();
            if (!userEmail.isEmpty()) {
                if (emailExists(userEmail)) {
                    new VerificationCodePage(userEmail);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Email not found! Please enter a valid email.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter your email!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(backButton, gbc);

        backButton.addActionListener(e -> {
            new LoginApp(); // Go back to login page
            dispose();
        });

        setVisible(true);
    }

    public static String getUserEmail() {
        return userEmail;
    }

    private boolean emailExists(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT 1 FROM accounts WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet result = stmt.executeQuery();
            return result.next();  // If a result is found, email exists
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}

class VerificationCodePage extends JFrame {
    private JTextField codeField;
    private String userEmail;

    public VerificationCodePage(String email) {
        this.userEmail = email;
        setTitle("Forgot Password - Step 2");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(30, 30, 30));

        JLabel titleLabel = new JLabel("Enter Verification Code", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 30, 300, 30);
        add(titleLabel);

        codeField = new JTextField();
        codeField.setBounds(50, 80, 300, 35);
        add(codeField);

        JButton nextButton = new JButton("Next");
        nextButton.setBounds(150, 140, 100, 40);
        nextButton.setBackground(Color.ORANGE);
        nextButton.setForeground(Color.WHITE);
        add(nextButton);

        nextButton.addActionListener(e -> {
            new ResetPasswordPage(userEmail);
            dispose();
        });

        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(50, 200, 100, 40);
        add(backButton);

        backButton.addActionListener(e -> {
            new EmailVerificationPage(); // Go back to email verification page
            dispose();
        });

        setVisible(true);
    }
}

class ResetPasswordPage extends JFrame {
    private JPasswordField newPasswordField, confirmNewPasswordField;
    private String userEmail;

    public ResetPasswordPage(String email) {
        this.userEmail = email;
        setTitle("Forgot Password - Step 3");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(30, 30, 30));

        JLabel titleLabel = new JLabel("Enter New Password", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 30, 300, 30);
        add(titleLabel);

        newPasswordField = new JPasswordField();
        newPasswordField.setBounds(50, 80, 300, 35);
        add(newPasswordField);

        confirmNewPasswordField = new JPasswordField();
        confirmNewPasswordField.setBounds(50, 130, 300, 35);
        add(confirmNewPasswordField);

        JButton resetButton = new JButton("Reset Password");
        resetButton.setBounds(120, 190, 160, 40);
        resetButton.setBackground(Color.ORANGE);
        resetButton.setForeground(Color.WHITE);
        add(resetButton);

        resetButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmNewPasswordField.getPassword());
            if (newPassword.equals(confirmPassword)) {
                if (updatePassword(userEmail, newPassword)) {
                    JOptionPane.showMessageDialog(null, "Password Reset Successful!");
                    new LoginApp();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to update password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);
        backButton.setBounds(50, 250, 100, 40);
        add(backButton);

        backButton.addActionListener(e -> {
            new VerificationCodePage(userEmail); // Go back to verification code page
            dispose();
        });

        setVisible(true);
    }

    private boolean updatePassword(String email, String newPassword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE accounts SET password = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newPassword); // Hash this for security in production
            stmt.setString(2, email);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
