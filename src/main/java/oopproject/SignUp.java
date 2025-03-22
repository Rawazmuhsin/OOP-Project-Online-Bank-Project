import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class SignUp extends JFrame implements ActionListener {
    private static final Color DARK_BLUE = new Color(25, 33, 43);
    private static final Color ORANGE = new Color(255, 140, 0);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color WHITE = Color.WHITE;
    
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JToggleButton showPassword;
    private JLabel statusLabel;
    private boolean isPasswordVisible = false;

    public SignUp() {
        setTitle("Sign Up");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(DARK_BLUE);

        
        JPanel titlePanel = new JPanel();
        titlePanel.setBounds(0, 20, 400, 40);
        titlePanel.setBackground(DARK_BLUE);
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel);


        JPanel formPanel = new JPanel();
        formPanel.setBounds(50, 70, 300, 300);
        formPanel.setLayout(null);
        formPanel.setBackground(DARK_BLUE);

     
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(0, 0, 100, 25);
        nameLabel.setForeground(WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(nameLabel);
        
        nameField = new JTextField();
        nameField.setBounds(0, 25, 300, 35);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBackground(LIGHT_GRAY);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ORANGE, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(nameField);

        
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(0, 70, 100, 25);
        emailLabel.setForeground(WHITE);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(emailLabel);
        
        emailField = new JTextField();
        emailField.setBounds(0, 95, 300, 35);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBackground(LIGHT_GRAY);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ORANGE, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(emailField);

       
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(0, 140, 100, 25);
        passwordLabel.setForeground(WHITE);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(0, 165, 260, 35);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(LIGHT_GRAY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ORANGE, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        passwordField.setEchoChar('●');
        formPanel.add(passwordField);

        
        showPassword = new JToggleButton("👁");
        showPassword.setBounds(265, 165, 35, 35);
        showPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        showPassword.setBackground(ORANGE);
        showPassword.setForeground(WHITE);
        showPassword.setFocusPainted(false);
        showPassword.setBorderPainted(false);
        showPassword.addActionListener(this);
        formPanel.add(showPassword);

        mainPanel.add(formPanel);

        
        statusLabel = new JLabel("");
        statusLabel.setBounds(50, 280, 300, 30);
        statusLabel.setForeground(ORANGE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBackground(new Color(0, 0, 0, 50));
        statusLabel.setOpaque(true);
        mainPanel.add(statusLabel);

        
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(50, 330, 300, 40);
        signUpButton.setBackground(ORANGE);
        signUpButton.setForeground(WHITE);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorderPainted(false);
        signUpButton.addActionListener(this);
        mainPanel.add(signUpButton);

       
        JButton backButton = new JButton("Already have an account? Login");
        backButton.setBounds(50, 390, 300, 30);
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setForeground(ORANGE);
        backButton.setBackground(DARK_BLUE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            this.dispose();
            new Gui().setVisible(true);
        });
        mainPanel.add(backButton);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == showPassword) {
            if (isPasswordVisible) {
                passwordField.setEchoChar('●');
                showPassword.setText("👁");
            } else {
                passwordField.setEchoChar((char) 0);
                showPassword.setText("👁");
            }
            isPasswordVisible = !isPasswordVisible;
        } else {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in all fields");
                statusLabel.setForeground(ORANGE);
                statusLabel.setBackground(new Color(40, 40, 40));
            }
            else if (name.equals("user") && email.equals("user@epu.edu.iq") && password.equals("1234@epu")) {
                statusLabel.setText("Account created successfully!");
                statusLabel.setForeground(new Color(50, 205, 50));
                statusLabel.setBackground(new Color(40, 40, 40));
                
                Timer timer = new Timer(2000, evt -> {
                    this.dispose();
                    new Gui().setVisible(true);
                });
                timer.setRepeats(false);
                timer.start();
            }
            else {
                statusLabel.setText("The Information Provided is incorrect");
                statusLabel.setForeground(ORANGE);
                statusLabel.setBackground(new Color(40, 40, 40));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SignUp().setVisible(true);
        });
    }
}