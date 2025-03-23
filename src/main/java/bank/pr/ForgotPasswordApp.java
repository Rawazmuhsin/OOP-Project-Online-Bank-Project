package bank.pr;

import java.awt.Color;
import java.awt.Font;

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
    
    public EmailVerificationPage() {
        setTitle("Forgot Password - Step 1");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(20, 20, 20));
        
        JLabel titleLabel = new JLabel("Enter Your Email", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 30, 300, 30);
        add(titleLabel);
        
        emailField = new JTextField();
        emailField.setBounds(50, 80, 300, 35);
        add(emailField);
        
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(150, 140, 100, 40);
        nextButton.setBackground(Color.ORANGE);
        nextButton.setForeground(Color.WHITE);
        add(nextButton);
        
        nextButton.addActionListener(e -> {
            new VerificationCodePage();
            dispose();
        });
        
        setVisible(true);
    }
}

class VerificationCodePage extends JFrame {
    private JTextField codeField;
    
    public VerificationCodePage() {
        setTitle("Forgot Password - Step 2");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(20, 20, 20));
        
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
            new ResetPasswordPage();
            dispose();
        });
        
        setVisible(true);
    }
}

class ResetPasswordPage extends JFrame {
    private JPasswordField newPasswordField, confirmNewPasswordField;
    
    public ResetPasswordPage() {
        setTitle("Forgot Password - Step 3");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(20, 20, 20));
        
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
                JOptionPane.showMessageDialog(null, "Password Reset Successful!");
                new LoginApp();
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        setVisible(true);
    }
}
