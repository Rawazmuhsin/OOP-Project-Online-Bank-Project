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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SignUp extends JFrame {
    
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

    private JTextField[] fields;
    private JRadioButton checkingRadio, savingsRadio;
    private JLabel errorLabel;

    public SignUp() {
        // Set look and feel to be more modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Sign Up - Kurdish-O-Banking");
        
        // Adjust size based on screen dimensions
        Dimension screenSize = getToolkit().getScreenSize();
        setSize(Math.min(1000, screenSize.width - 100), 
                Math.min(700, screenSize.height - 100));
                
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        
        // Benefits panel
        JPanel benefitsPanel = new RoundedPanel(15, new Color(40, 50, 90, 180));
        benefitsPanel.setLayout(new BoxLayout(benefitsPanel, BoxLayout.Y_AXIS));
        benefitsPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        benefitsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        benefitsPanel.setMaximumSize(new Dimension(300, 300));
        
        JLabel benefitsTitle = new JLabel("Why Choose Us?");
        benefitsTitle.setForeground(Color.WHITE);
        benefitsTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        benefitsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Feature items
        String[] features = {
            "Free online banking 24/7",
            "No hidden fees or charges",
            "Secure transactions",
            "Mobile banking app",
            "Dedicated customer support"
        };
        
        JPanel featuresList = new JPanel();
        featuresList.setLayout(new BoxLayout(featuresList, BoxLayout.Y_AXIS));
        featuresList.setOpaque(false);
        
        for (String feature : features) {
            JPanel featureItem = new JPanel(new BorderLayout(10, 0));
            featureItem.setOpaque(false);
            featureItem.setAlignmentX(Component.LEFT_ALIGNMENT);
            featureItem.setMaximumSize(new Dimension(300, 35));
            
            JLabel checkmark = new JLabel("✓");
            checkmark.setForeground(ACCENT_COLOR);
            checkmark.setFont(new Font("SansSerif", Font.BOLD, 16));
            
            JLabel featureText = new JLabel(feature);
            featureText.setForeground(new Color(220, 220, 220));
            featureText.setFont(new Font("SansSerif", Font.PLAIN, 14));
            
            featureItem.add(checkmark, BorderLayout.WEST);
            featureItem.add(featureText, BorderLayout.CENTER);
            
            featuresList.add(featureItem);
            featuresList.add(Box.createVerticalStrut(10));
        }
        
        benefitsPanel.add(benefitsTitle);
        benefitsPanel.add(Box.createVerticalStrut(20));
        benefitsPanel.add(featuresList);
        
        sidebar.add(benefitsPanel);
        sidebar.add(Box.createVerticalGlue());
        
        // Main content area with signup form
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create a scroll pane for the main panel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Signup form card
        JPanel formCard = new RoundedPanel(20, CARD_COLOR);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        // Increase height to fit all components
        formCard.setPreferredSize(new Dimension(450, 700));
        
        // Back to login link
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
        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Join Kurdish-O-Banking and start managing your finances");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Error label for validation errors
        errorLabel = new JLabel("Please fill in all required fields");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formCard.add(backButton);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(titleLabel);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(subtitleLabel);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(errorLabel);
        formCard.add(Box.createVerticalStrut(15));
        
        // Form fields
        String[] labels = {"Username", "Email", "Password", "Confirm Password", "Phone Number"};
        fields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            JLabel fieldLabel = new JLabel(labels[i]);
            fieldLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            fieldLabel.setForeground(TEXT_COLOR);
            fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            formCard.add(fieldLabel);
            formCard.add(Box.createVerticalStrut(5)); // Reduced vertical spacing
            
            fields[i] = labels[i].toLowerCase().contains("password") ? 
                       new JPasswordField() : new JTextField();
            fields[i].setFont(new Font("SansSerif", Font.PLAIN, 14));
            fields[i].setMaximumSize(new Dimension(370, 40)); // Fixed width
            fields[i].setPreferredSize(new Dimension(370, 40)); // Added preferred size
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            fields[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            
            formCard.add(fields[i]);
            formCard.add(Box.createVerticalStrut(10)); // Reduced vertical spacing
        }
        
        // Account type selection
        JLabel accountTypeLabel = new JLabel("Account Type");
        accountTypeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        accountTypeLabel.setForeground(TEXT_COLOR);
        accountTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formCard.add(accountTypeLabel);
        formCard.add(Box.createVerticalStrut(5)); // Reduced vertical spacing
        
        // Create account type options panel
        JPanel accountTypePanel = new JPanel();
        accountTypePanel.setLayout(new BoxLayout(accountTypePanel, BoxLayout.X_AXIS));
        accountTypePanel.setOpaque(false);
        accountTypePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        accountTypePanel.setMaximumSize(new Dimension(370, 50)); // Fixed width
        accountTypePanel.setPreferredSize(new Dimension(370, 50)); // Added preferred size
        
        // Create individual radio button panels for better styling
        JPanel checkingPanel = new JPanel(new BorderLayout());
        checkingPanel.setBackground(new Color(240, 240, 245));
        checkingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        JPanel savingsPanel = new JPanel(new BorderLayout());
        savingsPanel.setBackground(new Color(240, 240, 245));
        savingsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Create radio buttons
        checkingRadio = new JRadioButton("Checking Account");
        savingsRadio = new JRadioButton("Savings Account");
        ButtonGroup group = new ButtonGroup();
        group.add(checkingRadio);
        group.add(savingsRadio);
        
        // Style the radio buttons
        checkingRadio.setFont(new Font("SansSerif", Font.BOLD, 14));
        checkingRadio.setForeground(TEXT_COLOR);
        checkingRadio.setBackground(new Color(240, 240, 245));
        checkingRadio.setSelected(true); // Default selection
        
        savingsRadio.setFont(new Font("SansSerif", Font.BOLD, 14));
        savingsRadio.setForeground(TEXT_COLOR);
        savingsRadio.setBackground(new Color(240, 240, 245));
        
        // Add radio buttons to panels
        checkingPanel.add(checkingRadio, BorderLayout.CENTER);
        savingsPanel.add(savingsRadio, BorderLayout.CENTER);
        
        // Add panels to account type panel
        accountTypePanel.add(checkingPanel);
        accountTypePanel.add(Box.createHorizontalStrut(15));
        accountTypePanel.add(savingsPanel);
        
        formCard.add(accountTypePanel);
        formCard.add(Box.createVerticalStrut(30)); // Added more space before the sign-up button
        
        // Create a clear and distinct sign-up button
        JButton signUpButton = new JButton("Create Account");
        signUpButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBackground(ACCENT_COLOR); // Orange accent color
        signUpButton.setFocusPainted(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setOpaque(true);
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        signUpButton.setMaximumSize(new Dimension(370, 50));
        signUpButton.setPreferredSize(new Dimension(370, 50));
        
        // Try to add the hand icon to the button
        try {
            ImageIcon signUpIcon = new ImageIcon("Logo/signup_hand_icon.png");
            // Scale the icon to a smaller size to fit alongside text
            java.awt.Image img = signUpIcon.getImage().getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
            signUpIcon = new ImageIcon(img);
            signUpButton.setIcon(signUpIcon);
            // Position the icon to the left of the text
            signUpButton.setIconTextGap(10);
            signUpButton.setHorizontalAlignment(JButton.LEFT);
        } catch (Exception e) {
            // Button already has text, so no icon needed
            System.err.println("Error loading signup icon: " + e.getMessage());
            // Center the text if no icon
            signUpButton.setHorizontalAlignment(JButton.CENTER);
        }
        
        // Add action listener for button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processSignUp();
            }
        });
        
        // Add button to form with proper padding
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(370, 50));
        buttonPanel.add(signUpButton);
        
        formCard.add(buttonPanel);
        formCard.add(Box.createVerticalStrut(20));
        
        // Login link
        JPanel loginLinkPanel = new JPanel();
        loginLinkPanel.setLayout(new BoxLayout(loginLinkPanel, BoxLayout.X_AXIS));
        loginLinkPanel.setOpaque(false);
        loginLinkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginLinkPanel.setMaximumSize(new Dimension(370, 30)); // Fixed width
        
        JLabel alreadyHaveAccountLabel = new JLabel("Already have an account? ");
        alreadyHaveAccountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        alreadyHaveAccountLabel.setForeground(TEXT_COLOR);
        
        JLabel loginLink = new JLabel("Log In");
        loginLink.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginLink.setForeground(SECONDARY_COLOR);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginUI().setVisible(true);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setText("<html><u>Log In</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setText("Log In");
            }
        });
        
        loginLinkPanel.add(alreadyHaveAccountLabel);
        loginLinkPanel.add(loginLink);
        loginLinkPanel.add(Box.createHorizontalGlue());
        
        formCard.add(loginLinkPanel);
        
        // Add form card to main panel
        mainPanel.add(formCard);
        
        // Add panels to frame
        add(sidebar, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER); // Use scrollPane instead of mainPanel
        
        // Try to set icon
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
    }
    
    // Method to hash password using SHA-256 with salt
    private String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Add salt to digest
            md.update(salt);
            
            // Get the hashed password
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Store both salt and hashed password
            StringBuilder sb = new StringBuilder();
            sb.append(Base64.getEncoder().encodeToString(salt));
            sb.append(":");
            sb.append(Base64.getEncoder().encodeToString(hashedPassword));
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void processSignUp() {
        String username = fields[0].getText().trim();
        String email = fields[1].getText().trim();
        String password = new String(((JPasswordField) fields[2]).getPassword());
        String confirmPassword = new String(((JPasswordField) fields[3]).getPassword());
        String phone = fields[4].getText().trim();
        String accountType = checkingRadio.isSelected() ? "Checking" : 
                           savingsRadio.isSelected() ? "Savings" : "";

        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || accountType.isEmpty()) {
            errorLabel.setText("Please fill in all required fields");
            return;
        }
        
        // Validate email format
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errorLabel.setText("Please enter a valid email address");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return;
        }
        
        // Check password strength (minimum 6 characters)
        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters long");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Hash the password before storing it
            String hashedPassword = hashPassword(password);
            
            String sql = "INSERT INTO accounts (username, email, password, phone, account_type) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);  // Store the hashed password instead of plain text
            stmt.setString(4, phone);
            stmt.setString(5, accountType);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                    "Your account has been created successfully!\nYou can now log in with your email and password.",
                    "Account Created",
                    JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new LoginUI().setVisible(true);
            } else {
                errorLabel.setText("Failed to create account. Please try again.");
            }
        } catch (SQLException ex) {
            // Check for duplicate entry errors
            if (ex.getMessage().contains("Duplicate entry")) {
                if (ex.getMessage().contains("username")) {
                    errorLabel.setText("Username already exists. Please choose another one.");
                } else if (ex.getMessage().contains("email")) {
                    errorLabel.setText("Email already registered. Please use another email.");
                } else {
                    errorLabel.setText("Account already exists");
                }
            } else {
                errorLabel.setText("Database error: " + ex.getMessage());
            }
            ex.printStackTrace();
        }
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
                new SignUp().setVisible(true);
            }
        });
    }
}