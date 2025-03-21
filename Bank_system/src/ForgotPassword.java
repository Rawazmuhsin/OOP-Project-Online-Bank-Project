
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Random;

public class ForgotPassword extends JFrame implements ActionListener {
    
    private static  Color DARK_BLUE = new Color(25, 33, 43);
    private static  Color ORANGE = new Color(255, 140, 0);
    private static  Color LIGHT_GRAY = new Color(240, 240, 240);
    private static  Color WHITE = Color.WHITE;
   
   
    private JTextField emailField;
    private JTextField codeField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private JToggleButton showNewPassword;
    private JToggleButton showConfirmPassword;
    private JPanel emailPanel;
    private JPanel codePanel;
    private JPanel passwordPanel;
    private JButton sendCodeButton;
    private JButton verifyCodeButton;
    private JButton resetButton;
    
   
    private String generatedCode;
    private boolean isCodeVerified = false;
    
    public ForgotPassword() {
       
        setTitle("Forgot Password");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
     
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(DARK_BLUE);
        
       
        JPanel logoPanel = new JPanel();
        logoPanel.setBounds(0, 20, 400, 60);
        logoPanel.setBackground(DARK_BLUE);
        
     
        ImageIcon icon = new ImageIcon("bolt.png");
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setPreferredSize(new Dimension(40, 40));
        logoPanel.add(iconLabel);
        mainPanel.add(logoPanel);
        
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBounds(0, 90, 400, 80);
        titlePanel.setBackground(DARK_BLUE);
        
        JLabel titleLabel = new JLabel("Forgot Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Please verify your email to reset password");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(subtitleLabel);
        mainPanel.add(titlePanel);
        
      
        createEmailPanel();
        createCodePanel();
        createPasswordPanel();
        
       
        emailPanel.setVisible(true);
        codePanel.setVisible(false);
        passwordPanel.setVisible(false);
        
        mainPanel.add(emailPanel);
        mainPanel.add(codePanel);
        mainPanel.add(passwordPanel);
        
      
        statusLabel = new JLabel("");
        statusLabel.setBounds(50, 450, 300, 20);
        statusLabel.setForeground(ORANGE);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(statusLabel);
        
        add(mainPanel);
    }
    
    private void createEmailPanel() {
        emailPanel = new JPanel();
        emailPanel.setBounds(50, 180, 300, 250);
        emailPanel.setLayout(null);
        emailPanel.setBackground(DARK_BLUE);
        
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setBounds(0, 0, 300, 25);
        emailLabel.setForeground(LIGHT_GRAY);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailPanel.add(emailLabel);
        
        emailField = new JTextField();
        emailField.setBounds(0, 25, 300, 35);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBackground(LIGHT_GRAY);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ORANGE, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        emailPanel.add(emailField);
        
        sendCodeButton = new JButton("Send Verification Code");
        sendCodeButton.setBounds(0, 80, 300, 40);
        sendCodeButton.setBackground(ORANGE);
        sendCodeButton.setForeground(WHITE);
        sendCodeButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendCodeButton.setFocusPainted(false);
        sendCodeButton.setBorderPainted(false);
        sendCodeButton.addActionListener(this);
        emailPanel.add(sendCodeButton);
    }
    
    private void createCodePanel() {
        codePanel = new JPanel();
        codePanel.setBounds(50, 180, 300, 250);
        codePanel.setLayout(null);
        codePanel.setBackground(DARK_BLUE);
        
        JLabel codeLabel = new JLabel("Enter Verification Code");
        codeLabel.setBounds(0, 0, 300, 25);
        codeLabel.setForeground(LIGHT_GRAY);
        codeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        codePanel.add(codeLabel);
        
        codeField = new JTextField();
        codeField.setBounds(0, 25, 300, 35);
        codeField.setFont(new Font("Arial", Font.PLAIN, 14));
        codeField.setBackground(LIGHT_GRAY);
        codeField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ORANGE, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        codePanel.add(codeField);
        
        verifyCodeButton = new JButton("Verify Code");
        verifyCodeButton.setBounds(0, 80, 300, 40);
        verifyCodeButton.setBackground(ORANGE);
        verifyCodeButton.setForeground(WHITE);
        verifyCodeButton.setFont(new Font("Arial", Font.BOLD, 14));
        verifyCodeButton.setFocusPainted(false);
        verifyCodeButton.setBorderPainted(false);
        verifyCodeButton.addActionListener(this);
        codePanel.add(verifyCodeButton);
    }
    
    private void createPasswordPanel() {
        passwordPanel = new JPanel();
        passwordPanel.setBounds(50, 180, 300, 250);
        passwordPanel.setLayout(null);
        passwordPanel.setBackground(DARK_BLUE);
        
        // New Password field
        JLabel newPasswordLabel = new JLabel("New password*");
        newPasswordLabel.setBounds(0, 0, 300, 25);
        newPasswordLabel.setForeground(LIGHT_GRAY);
        newPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordPanel.add(newPasswordLabel);
        
        newPasswordField = new JPasswordField();
        newPasswordField.setBounds(0, 25, 260, 35);
        newPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        newPasswordField.setBackground(LIGHT_GRAY);
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ORANGE, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        passwordPanel.add(newPasswordField);
        
        showNewPassword = new JToggleButton("👁");
        showNewPassword.setBounds(265, 25, 35, 35);
        showNewPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        showNewPassword.setBackground(ORANGE);
        showNewPassword.setForeground(WHITE);
        showNewPassword.setFocusPainted(false);
        showNewPassword.setBorderPainted(false);
        showNewPassword.addActionListener(this);
        passwordPanel.add(showNewPassword);
        

        JLabel confirmPasswordLabel = new JLabel("Confirm password*");
        confirmPasswordLabel.setBounds(0, 80, 300, 25);
        confirmPasswordLabel.setForeground(LIGHT_GRAY);
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordPanel.add(confirmPasswordLabel);
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(0, 105, 260, 35);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setBackground(LIGHT_GRAY);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ORANGE, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        passwordPanel.add(confirmPasswordField);
        
        showConfirmPassword = new JToggleButton("👁");
        showConfirmPassword.setBounds(265, 105, 35, 35);
        showConfirmPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        showConfirmPassword.setBackground(ORANGE);
        showConfirmPassword.setForeground(WHITE);
        showConfirmPassword.setFocusPainted(false);
        showConfirmPassword.setBorderPainted(false);
        showConfirmPassword.addActionListener(this);
        passwordPanel.add(showConfirmPassword);
        
        resetButton = new JButton("Reset Password");
        resetButton.setBounds(0, 160, 300, 40);
        resetButton.setBackground(ORANGE);
        resetButton.setForeground(WHITE);
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.addActionListener(this);
        passwordPanel.add(resetButton);
    }
    
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a 6-digit code
        return String.valueOf(code);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendCodeButton) {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                statusLabel.setText("Please enter your email address");
                statusLabel.setForeground(ORANGE);
            } else if (!email.equals("user@epu.edu.iq")) {
                statusLabel.setText("Email not found");
                statusLabel.setForeground(ORANGE);
            } else {
                generatedCode = generateVerificationCode();
                // In a real application, you would send this code to the user's email
                System.out.println("Verification code: " + generatedCode); // For testing purposes
                
                emailPanel.setVisible(false);
                codePanel.setVisible(true);
                statusLabel.setText("Verification code sent to your email");
                statusLabel.setForeground(new Color(0, 150, 0));
            }
        } else if (e.getSource() == verifyCodeButton) {
            String enteredCode = codeField.getText().trim();
            if (enteredCode.isEmpty()) {
                statusLabel.setText("Please enter the verification code");
                statusLabel.setForeground(ORANGE);
            } else if (!enteredCode.equals(generatedCode)) {
                statusLabel.setText("Invalid verification code");
                statusLabel.setForeground(ORANGE);
            } else {
                isCodeVerified = true;
                codePanel.setVisible(false);
                passwordPanel.setVisible(true);
                statusLabel.setText("Code verified successfully");
                statusLabel.setForeground(new Color(0, 150, 0));
            }
        } else if (e.getSource() == showNewPassword) {
            if (showNewPassword.isSelected()) {
                newPasswordField.setEchoChar((char) 0);
            } else {
                newPasswordField.setEchoChar('●');
            }
        } else if (e.getSource() == showConfirmPassword) {
            if (showConfirmPassword.isSelected()) {
                confirmPasswordField.setEchoChar((char) 0);
            } else {
                confirmPasswordField.setEchoChar('●');
            }
        } else if (e.getSource() == resetButton) {
            if (!isCodeVerified) {
                statusLabel.setText("Please verify your email first");
                statusLabel.setForeground(ORANGE);
                return;
            }
            
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                statusLabel.setText("Please fill in all fields");
                statusLabel.setForeground(ORANGE);
            } else if (!newPassword.equals(confirmPassword)) {
                statusLabel.setText("Passwords doesnt Exist");
                statusLabel.setForeground(ORANGE);
            } else {
                statusLabel.setText("Password changed successfully!");
                statusLabel.setForeground(new Color(0, 150, 0));
                
              
                Timer timer = new Timer(2000, evt -> {
                    this.dispose();
                 //   new SignUp().setVisible(true);
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ForgotPassword().setVisible(true);
        });
    }
}