package bank.pr;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginApp extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;

    public LoginApp() {
        setTitle("Login");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(30, 30, 30));

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(300, 20, 200, 30);
        add(welcomeLabel);

        // Email Label and Field
        JLabel emailLabel = new JLabel("Email", JLabel.CENTER);
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(300, 70, 200, 20);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(300, 100, 200, 25);
        add(emailField);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password", JLabel.CENTER);
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(300, 140, 200, 20);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(300, 170, 200, 25);
        add(passwordField);

        // Show Password Checkbox
        showPasswordCheckBox = new JCheckBox("Show Password", false);
        showPasswordCheckBox.setBounds(300, 200, 200, 20);
        showPasswordCheckBox.setBackground(new Color(30, 30, 30));
        showPasswordCheckBox.setForeground(Color.WHITE);
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        });
        add(showPasswordCheckBox);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(325, 240, 150, 30);
        loginButton.setBackground(Color.ORANGE);
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticateUser(email, password)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                
                // Open HomePage and pass user email
                HomePage homePage = new HomePage(email);
                homePage.setVisible(true);
                
                dispose(); // Close Login Page
            } else {
                JOptionPane.showMessageDialog(null, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(loginButton);

        // Forgot Password Button
        JButton forgotPasswordButton = new JButton("Forgot Password");
        forgotPasswordButton.setBounds(325, 280, 150, 30);
        forgotPasswordButton.addActionListener(e -> {
            new EmailVerificationPage(); // Opens the first step of Forgot Password process
            dispose();
        });
        add(forgotPasswordButton);

        // Back Button (Returns to HomeScreen)
        JButton backButton = new JButton("Back");
        backButton.setBounds(325, 320, 150, 30);
        backButton.addActionListener(e -> {
            new HomeScreen(); // Go back to HomeScreen
            dispose();
        });
        add(backButton);

        setVisible(true);
    }

    // Authenticate user by checking the email and password
    private boolean authenticateUser(String email, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM accounts WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if a matching record is found
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Main method to run the LoginApp
    public static void main(String[] args) {
        new LoginApp();
    }
}
