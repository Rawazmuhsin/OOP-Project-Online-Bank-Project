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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class LoginUI extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // Colors scheme - same as Dashboard and Transfer
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);

    private JTextField userField;
    private JPasswordField passField;
    private JLabel errorLabel;

    public LoginUI() {
        // Set look and feel to be more modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Login - Kurdish-O-Banking (KOB)");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
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
        
        // Features or welcome message
        JPanel welcomePanel = new RoundedPanel(15, new Color(40, 50, 90, 180));
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        welcomePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomePanel.setMaximumSize(new Dimension(300, 250));
        
        JLabel welcomeTitle = new JLabel("Welcome Back!");
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel welcomeText = new JLabel("<html>Log in to access your accounts, transfer funds, and manage your banking needs securely.</html>");
        welcomeText.setForeground(new Color(220, 220, 220));
        welcomeText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        welcomeText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        welcomePanel.add(welcomeTitle);
        welcomePanel.add(Box.createVerticalStrut(15));
        welcomePanel.add(welcomeText);
        
        sidebar.add(welcomePanel);
        sidebar.add(Box.createVerticalGlue());
        
        // Right side login form
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(BACKGROUND_COLOR);
        
        // Main login card
        JPanel loginCard = new RoundedPanel(20, CARD_COLOR);
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        loginCard.setPreferredSize(new Dimension(450, 550));
        
        JLabel titleLabel = new JLabel("Login to Your Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Enter your credentials to access your account");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Error label for validation messages
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Email field
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        emailLabel.setForeground(TEXT_COLOR);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        userField = new JTextField();
        userField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userField.setMaximumSize(new Dimension(400, 40));
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        userField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Password field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passLabel.setForeground(TEXT_COLOR);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        passField = new JPasswordField();
        passField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passField.setMaximumSize(new Dimension(400, 40));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Remember me & Show password
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
        optionsPanel.setOpaque(false);
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.setMaximumSize(new Dimension(400, 30));
        
        JCheckBox rememberMe = new JCheckBox("Remember me");
        rememberMe.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rememberMe.setForeground(TEXT_COLOR);
        rememberMe.setOpaque(false);
        
        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setFont(new Font("SansSerif", Font.PLAIN, 14));
        showPass.setForeground(TEXT_COLOR);
        showPass.setOpaque(false);
        showPass.addActionListener(e -> {
            if (showPass.isSelected()) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('•');
            }
        });
        
        optionsPanel.add(rememberMe);
        optionsPanel.add(Box.createHorizontalGlue());
        optionsPanel.add(showPass);
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(SECONDARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setMaximumSize(new Dimension(400, 50));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(25, 118, 210));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(SECONDARY_COLOR);
            }
        });
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
        
        // Forgot password link
        JPanel forgotPassPanel = new JPanel();
        forgotPassPanel.setLayout(new BoxLayout(forgotPassPanel, BoxLayout.X_AXIS));
        forgotPassPanel.setOpaque(false);
        forgotPassPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        forgotPassPanel.setMaximumSize(new Dimension(400, 30));
        
        JLabel forgotPassLabel = new JLabel("Forgot your password?");
        forgotPassLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        forgotPassLabel.setForeground(SECONDARY_COLOR);
        forgotPassLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPassLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new ForgotPassword().setVisible(true);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPassLabel.setText("<html><u>Forgot your password?</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                forgotPassLabel.setText("Forgot your password?");
            }
        });
        
        forgotPassPanel.add(Box.createHorizontalGlue());
        forgotPassPanel.add(forgotPassLabel);
        
        // Divider
        JPanel dividerPanel = new JPanel();
        dividerPanel.setLayout(new BoxLayout(dividerPanel, BoxLayout.X_AXIS));
        dividerPanel.setOpaque(false);
        dividerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dividerPanel.setMaximumSize(new Dimension(400, 20));
        
        JPanel leftDivider = new JPanel();
        leftDivider.setBackground(new Color(220, 220, 220));
        leftDivider.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        leftDivider.setPreferredSize(new Dimension(100, 1));
        
        JLabel orLabel = new JLabel("  OR  ");
        orLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        orLabel.setForeground(LIGHT_TEXT_COLOR);
        
        JPanel rightDivider = new JPanel();
        rightDivider.setBackground(new Color(220, 220, 220));
        rightDivider.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        rightDivider.setPreferredSize(new Dimension(100, 1));
        
        dividerPanel.add(leftDivider);
        dividerPanel.add(orLabel);
        dividerPanel.add(rightDivider);
        
        // Sign up button
        JButton signUpButton = new JButton("Create New Account");
        signUpButton.setBackground(new Color(240, 240, 240));
        signUpButton.setForeground(TEXT_COLOR);
        signUpButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        signUpButton.setFocusPainted(false);
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpButton.setMaximumSize(new Dimension(400, 50));
        signUpButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add hover effect
        signUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signUpButton.setBackground(new Color(230, 230, 230));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signUpButton.setBackground(new Color(240, 240, 240));
            }
        });
        
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SignUp().setVisible(true);
            }
        });
        
        // Back to home
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.X_AXIS));
        homePanel.setOpaque(false);
        homePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePanel.setMaximumSize(new Dimension(400, 30));
        
        JLabel backToHomeLabel = new JLabel("← Back to Homepage");
        backToHomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backToHomeLabel.setForeground(SECONDARY_COLOR);
        backToHomeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backToHomeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new WelcomePage().setVisible(true);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                backToHomeLabel.setText("<html><u>← Back to Homepage</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                backToHomeLabel.setText("← Back to Homepage");
            }
        });
        
        homePanel.add(backToHomeLabel);
        homePanel.add(Box.createHorizontalGlue());
        
        // Add components to login card
        loginCard.add(titleLabel);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(subtitleLabel);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(errorLabel);
        loginCard.add(Box.createVerticalStrut(20));
        
        loginCard.add(emailLabel);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(userField);
        loginCard.add(Box.createVerticalStrut(20));
        
        loginCard.add(passLabel);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(passField);
        loginCard.add(Box.createVerticalStrut(15));
        
        loginCard.add(optionsPanel);
        loginCard.add(Box.createVerticalStrut(25));
        
        loginCard.add(loginButton);
        loginCard.add(Box.createVerticalStrut(15));
        loginCard.add(forgotPassPanel);
        loginCard.add(Box.createVerticalStrut(25));
        
        loginCard.add(dividerPanel);
        loginCard.add(Box.createVerticalStrut(25));
        
        loginCard.add(signUpButton);
        loginCard.add(Box.createVerticalStrut(20));
        loginCard.add(homePanel);
        
        // Add login card to login panel
        loginPanel.add(loginCard);
        
        // Add panels to frame
        add(sidebar, BorderLayout.WEST);
        add(loginPanel, BorderLayout.CENTER);
        
        // Try to set icon
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
    }

    private void attemptLogin() {
        String email = userField.getText();
        String password = new String(passField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password");
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
                    errorLabel.setText("Invalid email or password");
                    passField.setText("");
                }
            }
        } catch (SQLException ex) {
            errorLabel.setText("Database error: " + ex.getMessage());
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
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, userId);
                    dashboard.setVisible(true);
                }
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
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new ManagerDashboard().setVisible(true);
                }
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
                new LoginUI().setVisible(true);
            }
        });
    }
}