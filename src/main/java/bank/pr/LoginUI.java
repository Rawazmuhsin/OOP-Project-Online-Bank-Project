package bank.pr;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class LoginUI extends JFrame {

    public LoginUI() {
        setTitle("Online Banking System");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // White Panel with border
        JPanel panel = new JPanel();
        panel.setBounds(75, 50, 350, 400);
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        add(panel);

        // Title
        JLabel titleLabel = new JLabel("ONLINE BANKING SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(20, 20, 40));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(20, 20, 310, 30);
        panel.add(titleLabel);

        // Username label
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userLabel.setBounds(20, 70, 300, 20);
        panel.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(20, 95, 310, 35);
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        userField.setBackground(Color.WHITE);
        userField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(userField);

        // Password label
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passLabel.setBounds(20, 140, 300, 20);
        panel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(20, 165, 310, 35);
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBackground(Color.WHITE);
        passField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(passField);

        // Show Password
        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setBounds(20, 210, 150, 20);
        showPass.setFont(new Font("Arial", Font.PLAIN, 12));
        showPass.setOpaque(false);
        showPass.addActionListener(e -> {
            if (showPass.isSelected()) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar('•');
            }
        });
        panel.add(showPass);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(20, 245, 310, 40);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(66, 133, 244)); // Google blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        panel.add(loginButton);

        // Links: Forgot password and Create account
        JLabel forgotPass = new JLabel("Forgot Password?");
        forgotPass.setForeground(new Color(66, 133, 244));
        forgotPass.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPass.setBounds(20, 295, 150, 30);
        panel.add(forgotPass);

        JLabel createAccount = new JLabel("Create account");
        createAccount.setForeground(new Color(66, 133, 244));
        createAccount.setFont(new Font("Arial", Font.PLAIN, 12));
        createAccount.setHorizontalAlignment(SwingConstants.RIGHT);
        createAccount.setBounds(180, 295, 150, 30);
        panel.add(createAccount);

        // Set background of frame
        getContentPane().setBackground(new Color(245, 247, 250));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
