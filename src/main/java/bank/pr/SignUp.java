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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SignUp extends JFrame {

    private JTextField[] fields;

    public SignUp() {
        setTitle("Sign Up");
        setSize(700, 850); // Slightly taller to accommodate all fields
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(230, 233, 239));
        setLayout(new BorderLayout());

        // Main white panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 40, 60));

        // Back button panel (top-left)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton backButton = new JButton("← Back");
        styleBackButton(backButton);
        topPanel.add(backButton);
        mainPanel.add(topPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Title
        JLabel titleLabel = new JLabel("CREATE YOUR ACCOUNT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(66, 103, 244));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Form fields
        String[] labels = {"Username", "Email", "Password", "Confirm Password", "Phone Number"};
        fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            addFormField(mainPanel, labels[i], i);
            if (i < labels.length - 1) {
                mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
            }
        }

        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Sign Up button
        JButton signUpButton = createActionButton("SIGN UP", 600, 60);
        signUpButton.addActionListener(e -> processSignUp());
        mainPanel.add(signUpButton);

        // Center the panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(230, 233, 239));
        centerPanel.add(mainPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void styleBackButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(new Color(66, 103, 244));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            this.dispose();
            new LoginUI().setVisible(true);
        });
    }

    private void addFormField(JPanel parent, String labelText, int index) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.add(label);
        
        // Input field
        boolean isPassword = labelText.toLowerCase().contains("password");
        fields[index] = isPassword ? new JPasswordField() : new JTextField();
        fields[index].setFont(new Font("Arial", Font.PLAIN, 16));
        fields[index].setMaximumSize(new Dimension(600, 50));
        fields[index].setPreferredSize(new Dimension(600, 50));
        fields[index].setBackground(new Color(250, 252, 255));
        fields[index].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        fieldPanel.add(fields[index]);
        
        parent.add(fieldPanel);
    }

    private JButton createActionButton(String text, int width, int height) {
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
    }

    private void processSignUp() {
        String username = fields[0].getText().trim();
        String email = fields[1].getText().trim();
        String password = new String(((JPasswordField) fields[2]).getPassword());
        String confirmPassword = new String(((JPasswordField) fields[3]).getPassword());
        String phone = fields[4].getText().trim();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all required fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO accounts (name, email, password, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, phone);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nYou can now login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new LoginUI().setVisible(true);
            } else {
                showError("Failed to create account");
            }
        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignUp signUp = new SignUp();
            signUp.setVisible(true);
        });
    }
}