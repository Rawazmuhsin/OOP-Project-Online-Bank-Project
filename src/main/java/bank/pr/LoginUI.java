package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginUI extends JFrame {

    private JTextField userField;
    private JPasswordField passField;

    public LoginUI() {
        setTitle(" Kurdish - O - Banking (KOB)");
        setSize(700, 850); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(230, 233, 239));
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel titleLabel = new JLabel("Kurdish - O - Banking ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(66, 103, 244));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Login description
        JLabel loginDescLabel = new JLabel("Please enter your credentials to login");
        loginDescLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        loginDescLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(loginDescLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBackground(Color.WHITE);
        emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 16));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.add(emailLabel);

        userField = new JTextField();
        userField.setFont(new Font("Arial", Font.PLAIN, 16));
        userField.setMaximumSize(new Dimension(600, 50));
        userField.setPreferredSize(new Dimension(600, 50));
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        emailPanel.add(userField);
        mainPanel.add(emailPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel passPanel = new JPanel();
        passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.Y_AXIS));
        passPanel.setBackground(Color.WHITE);
        passPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passPanel.add(passLabel);

        passField = new JPasswordField();
        passField.setFont(new Font("Arial", Font.PLAIN, 16));
        passField.setMaximumSize(new Dimension(600, 50));
        passField.setPreferredSize(new Dimension(600, 50));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passPanel.add(passField);
        mainPanel.add(passPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setFont(new Font("Arial", Font.PLAIN, 14));
        showPass.setOpaque(false);
        showPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPass.addActionListener(e -> {
            if (showPass.isSelected()) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('•');
            }
        });
        mainPanel.add(showPass);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        JButton loginButton = createStyledButton("LOGIN", 600, 60);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel forgotPass = new JLabel("Forgot Password?");
        forgotPass.setForeground(new Color(66, 103, 244));
        forgotPass.setFont(new Font("Arial", Font.PLAIN, 14));
        forgotPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new ForgotPassword().setVisible(true);
            }
        });
        mainPanel.add(forgotPass);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel signUpLink = new JLabel("Don't have an account? Sign Up");
        signUpLink.setForeground(new Color(66, 103, 244));
        signUpLink.setFont(new Font("Arial", Font.PLAIN, 14));
        signUpLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new SignUp().setVisible(true);
            }
        });
        mainPanel.add(signUpLink);

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
    }

    private void attemptLogin() {
        String email = userField.getText();
        String password = new String(passField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both email and password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            // First try to authenticate as user
            boolean isUserAuthenticated = attemptUserLogin(conn, email, password);
            
            // If not a user, try as admin
            if (!isUserAuthenticated) {
                boolean isAdminAuthenticated = attemptAdminLogin(conn, email, password);
                
                // If neither user nor admin authentication succeeded
                if (!isAdminAuthenticated) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid email or password",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean attemptUserLogin(Connection conn, String email, String password) throws SQLException {
        // Query to get user account information by email
        String query = "SELECT account_id, username, email FROM accounts WHERE email = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String userName = rs.getString("username");
            int userId = rs.getInt("account_id");

            // Update last login time (optional)
            try {
                String updateQuery = "UPDATE accounts SET last_login = NOW() WHERE account_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, userId);
                updateStmt.executeUpdate();
            } catch (SQLException ex) {
                // Not critical if this fails, so just log it
                System.err.println("Could not update last login time: " + ex.getMessage());
            }

            dispose();

            // Create and show dashboard with user information
            SwingUtilities.invokeLater(() -> {
                Dashbord dashboard = new Dashbord();
                dashboard.setUserInfo(userName, userId);
                dashboard.setVisible(true);
            });
            
            // Show welcome message for user
            JOptionPane.showMessageDialog(null,
                "Welcome, " + userName + "!",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);
                
            return true;
        }
        
        return false;
    }
    
    private boolean attemptAdminLogin(Connection conn, String email, String password) throws SQLException {
        // Query admin table by email
        String query = "SELECT admin_id, username, first_name, last_name FROM admin WHERE email = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            int adminId = rs.getInt("admin_id");
            String adminName = rs.getString("first_name") + " " + rs.getString("last_name");
            
            // Update last login time
            try {
                String updateQuery = "UPDATE admin SET last_login = NOW() WHERE admin_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, adminId);
                updateStmt.executeUpdate();
            } catch (SQLException ex) {
                // Not critical if this fails, so just log it
                System.err.println("Could not update last login time: " + ex.getMessage());
            }

            dispose();

            // Create and show the manager dashboard for admin users
            SwingUtilities.invokeLater(() -> {
                new ManagerDashboard().setVisible(true);
            });
            
            // Show welcome message for admin
            JOptionPane.showMessageDialog(null,
                "Welcome, " + adminName + "! (Admin)",
                "Admin Login Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            return true;
        }
        
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}