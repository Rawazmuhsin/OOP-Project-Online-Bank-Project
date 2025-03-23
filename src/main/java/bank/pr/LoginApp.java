package bank.pr;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(30, 30, 30));

        JLabel welcomeLabel = new JLabel("Welcome", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(100, 20, 200, 30);
        add(welcomeLabel);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(50, 70, 100, 20);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 70, 200, 25);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(50, 110, 100, 20);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 110, 200, 25);
        add(passwordField);

        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBounds(150, 140, 200, 20);
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

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 180, 100, 30);
        loginButton.setBackground(Color.ORANGE);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticateUser(email, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(loginButton);

        JButton forgotPasswordButton = new JButton("Forgot Password");
        forgotPasswordButton.setBounds(260, 180, 150, 30);
        forgotPasswordButton.addActionListener(e -> {
            new ForgotPasswordApp();
            dispose();
        });
        add(forgotPasswordButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(20, 220, 80, 30);
        backButton.addActionListener(e -> {
            new HomeScreen();
            dispose();
        });
        add(backButton);

        setVisible(true);
    }

    private boolean authenticateUser(String email, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM accounts WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        new LoginApp();
    }
}
